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

public class DefaultJoglRenderer implements GLEventListener {
	protected JoglViewState viewState = new JoglViewState();
	protected JoglViewSettings viewSettings = new JoglViewSettings();
	protected JoglScene scene;
	protected boolean useFrameBuffer;
	protected GL3Framebuffer drawFramebuffer;

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

		if (useFrameBuffer && drawFramebuffer == null) {
			drawFramebuffer = new GL3Framebuffer();
			drawFramebuffer.init(gl.getGL3());
		}
		else if (!useFrameBuffer && drawFramebuffer != null) {
			drawFramebuffer.dispose(gl.getGL3());
			drawFramebuffer = null;
		}

		int renderingFbo = -1;

		if (drawFramebuffer != null) {
			GL3 gl3 = (GL3) gl;
			renderingFbo =
				drawFramebuffer
					.renderingFbo(
						gl3,
						drawable.getSurfaceWidth(),
						drawable.getSurfaceHeight(),
						desiredNumSamples,
						desiredUseStencilBuffer);
			gl3.glBindFramebuffer(GL_DRAW_FRAMEBUFFER, renderingFbo);
		}
		else {
			GL3 gl3 = (GL3) gl;
			gl3.glBindFramebuffer(GL_DRAW_FRAMEBUFFER, 0);
		}
		viewState.update(viewSettings, x, y, width, height, devicePixelRatio);

		drawScene(drawable);

		if (drawFramebuffer != null) {
			GL3 gl3 = (GL3) gl;

			gl3.glBindFramebuffer(GL_DRAW_FRAMEBUFFER, 0);
			gl3.glBindFramebuffer(GL_READ_FRAMEBUFFER, renderingFbo);
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

		if (drawFramebuffer != null) {
			drawFramebuffer.dispose((GL3) gl);
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

		if (drawFramebuffer != null) {
			drawFramebuffer.init((GL3) gl);
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
}
