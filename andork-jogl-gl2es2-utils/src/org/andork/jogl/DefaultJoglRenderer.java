package org.andork.jogl;

import static com.jogamp.opengl.GL.GL_DRAW_FRAMEBUFFER;
import static com.jogamp.opengl.GL.GL_NEAREST;
import static com.jogamp.opengl.GL.GL_READ_FRAMEBUFFER;
import static org.andork.math3d.Vecmath.newMat3f;
import static org.andork.math3d.Vecmath.newMat4f;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2ES2;
import com.jogamp.opengl.GL3;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;

public class DefaultJoglRenderer implements GLEventListener, JoglViewState.Context {
	protected JoglViewState viewState = new JoglViewState(this);
	protected JoglViewSettings viewSettings = new JoglViewSettings();
	protected JoglScene scene;
	protected boolean useFrameBuffer;
	protected GL3Framebuffer framebuffer;
	protected int renderingFbo = 0;
	protected GLFramebufferTexture readFramebuffer;
	protected GLFramebufferTexture drawFramebuffer;

	protected int desiredNumSamples = 1;
	protected boolean desiredUseStencilBuffer = false;

	protected float[] m = newMat4f();
	protected float[] n = newMat3f();

	protected int x;
	protected int y;
	protected int width;
	protected int height;
	protected float devicePixelRatio = 1f;

	@Override
	public void display(GLAutoDrawable drawable) {
		GL2ES2 gl = (GL2ES2) drawable.getGL();

		if (useFrameBuffer && framebuffer == null) {
			framebuffer = new GL3Framebuffer();
			framebuffer.init(gl.getGL3());
		}
		else if (!useFrameBuffer && framebuffer != null) {
			framebuffer.dispose(gl.getGL3());
			framebuffer = null;
		}

		if (drawFramebuffer == null) {
			readFramebuffer = new GLFramebufferTexture();
			readFramebuffer.init(gl);
			drawFramebuffer = new GLFramebufferTexture();
			drawFramebuffer.init(gl);
		}

		if (framebuffer != null) {
			GL3 gl3 = (GL3) gl;
			renderingFbo =
				framebuffer
					.renderingFbo(
						gl3,
						drawable.getSurfaceWidth(),
						drawable.getSurfaceHeight(),
						desiredNumSamples,
						desiredUseStencilBuffer);
			gl3.glBindFramebuffer(GL_DRAW_FRAMEBUFFER, renderingFbo);
		}
		else {
			renderingFbo = 0;
			GL3 gl3 = (GL3) gl;
			gl3.glBindFramebuffer(GL_DRAW_FRAMEBUFFER, 0);
		}
		viewState.update(viewSettings, x, y, width, height, devicePixelRatio);

		drawScene(drawable);

		if (framebuffer != null) {
			GL3 gl3 = (GL3) gl;

			gl3.glBindFramebuffer(GL_READ_FRAMEBUFFER, renderingFbo);
			gl3.glBindFramebuffer(GL_DRAW_FRAMEBUFFER, 0);
			gl3
				.glBlitFramebuffer(
					0,
					0,
					drawable.getSurfaceWidth(),
					drawable.getSurfaceHeight(),
					0,
					0,
					drawable.getSurfaceWidth(),
					drawable.getSurfaceHeight(),
					GL.GL_COLOR_BUFFER_BIT,
					GL_NEAREST);

		}
	}

	@Override
	public void dispose(GLAutoDrawable drawable) {
		GL2ES2 gl = (GL2ES2) drawable.getGL();

		if (framebuffer != null) {
			framebuffer.dispose((GL3) gl);
		}
		if (drawFramebuffer != null) {
			drawFramebuffer.dispose(gl);
		}
		if (readFramebuffer != null) {
			readFramebuffer.dispose(gl);
		}
	}

	protected void drawScene(GLAutoDrawable drawable) {
		if (scene != null) {
			scene.draw(viewState, drawable.getGL().getGL2ES2(), m, n);
		}
	}

	@Override
	public void init(GLAutoDrawable drawable) {
		GL2ES2 gl = (GL2ES2) drawable.getGL();

		if (framebuffer != null) {
			framebuffer.init((GL3) gl);
		}
		if (drawFramebuffer != null) {
			drawFramebuffer.init(gl);
		}
		if (readFramebuffer != null) {
			readFramebuffer.init(gl);
		}
	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
		GL2ES2 gl = (GL2ES2) drawable.getGL();

		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.devicePixelRatio = DevicePixelRatio.getDevicePixelRatio(drawable);

		gl.glViewport(x, y, width, height);
	}

	public int desiredNumSamples() {
		return desiredNumSamples;
	}

	public DefaultJoglRenderer desiredNumSamples(int desiredNumSamples) {
		this.desiredNumSamples = desiredNumSamples;
		return this;
	}

	public JoglScene scene() {
		return scene;
	}

	public DefaultJoglRenderer scene(JoglScene scene) {
		this.scene = scene;
		return this;
	}

	public JoglViewSettings viewSettings() {
		return viewSettings;
	}

	public DefaultJoglRenderer viewSettings(JoglViewSettings viewSettings) {
		this.viewSettings = viewSettings;
		return this;
	}

	public JoglViewState viewState() {
		return viewState;
	}

	public boolean desiredUseStencilBuffer() {
		return desiredUseStencilBuffer;
	}

	public DefaultJoglRenderer desiredUseStencilBuffer(boolean useStencilBuffer) {
		desiredUseStencilBuffer = useStencilBuffer;
		return this;
	}

	public boolean useFrameBuffer() {
		return useFrameBuffer;
	}

	public DefaultJoglRenderer useFrameBuffer(boolean useFrameBuffer) {
		this.useFrameBuffer = useFrameBuffer;
		return this;
	}

	@Override
	public void applyFilters(GL3 gl3, JoglFilter... filters) {
		if (drawFramebuffer == null) {
			readFramebuffer = new GLFramebufferTexture();
			readFramebuffer.init(gl3);
			drawFramebuffer = new GLFramebufferTexture();
			drawFramebuffer.init(gl3);
		}

		int readFbo = readFramebuffer.renderingFbo(gl3, width, height);
		int drawFbo = drawFramebuffer.renderingFbo(gl3, width, height);

		gl3.glBindFramebuffer(GL_READ_FRAMEBUFFER, renderingFbo);
		gl3.glBindFramebuffer(GL_DRAW_FRAMEBUFFER, drawFbo);
		gl3.glBlitFramebuffer(0, 0, width, height, 0, 0, width, height, GL.GL_COLOR_BUFFER_BIT, GL_NEAREST);

		for (int i = 0; i < filters.length; i++) {
			int swapFbo = readFbo;
			readFbo = drawFbo;
			drawFbo = swapFbo;

			GLFramebufferTexture swapFramebuffer = readFramebuffer;
			readFramebuffer = drawFramebuffer;
			drawFramebuffer = swapFramebuffer;

			gl3.glBindFramebuffer(GL_READ_FRAMEBUFFER, readFbo);
			gl3.glBindFramebuffer(GL_DRAW_FRAMEBUFFER, i < filters.length - 1 ? drawFbo : renderingFbo);
			filters[i]
				.apply(
					gl3,
					width,
					height,
					readFbo,
					readFramebuffer.texture(),
					(float) width / readFramebuffer.width(),
					(float) height / readFramebuffer.height());
		}
		gl3.glBindFramebuffer(GL_READ_FRAMEBUFFER, 0);
	}

}
