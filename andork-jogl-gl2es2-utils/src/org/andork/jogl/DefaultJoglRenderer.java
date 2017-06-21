package org.andork.jogl;

import static com.jogamp.opengl.GL.GL_BACK;
import static com.jogamp.opengl.GL.GL_DRAW_FRAMEBUFFER;
import static com.jogamp.opengl.GL.GL_NEAREST;
import static com.jogamp.opengl.GL.GL_READ_FRAMEBUFFER;
import static org.andork.math3d.Vecmath.newMat3f;
import static org.andork.math3d.Vecmath.newMat4f;

import java.awt.Component;
import java.awt.Rectangle;
import java.awt.Window;

import javax.swing.SwingUtilities;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2ES2;
import com.jogamp.opengl.GL3;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;

public class DefaultJoglRenderer implements GLEventListener {
	protected JoglViewState viewState = new JoglViewState();
	protected JoglViewSettings viewSettings = new JoglViewSettings();
	protected JoglScene scene;
	protected GL3Framebuffer framebuffer;

	protected int desiredNumSamples = 1;
	protected boolean desiredUseStencilBuffer = false;

	protected float[] m = newMat4f();
	protected float[] n = newMat3f();

	protected int width;
	protected int height;

	public DefaultJoglRenderer(GL3Framebuffer framebuffer, int desiredNumSamples) {
		super();
		this.framebuffer = framebuffer;
		this.desiredNumSamples = desiredNumSamples;
	}

	public DefaultJoglRenderer(JoglScene scene) {
		super();
		this.scene = scene;
	}

	public DefaultJoglRenderer(JoglScene scene, GL3Framebuffer framebuffer, int desiredNumSamples) {
		super();
		this.scene = scene;
		this.framebuffer = framebuffer;
		this.desiredNumSamples = desiredNumSamples;
	}

	@Override
	public void display(GLAutoDrawable drawable) {
		GL2ES2 gl = (GL2ES2) drawable.getGL();

		int renderingFbo = -1;

		if (framebuffer != null) {
			GL3 gl3 = (GL3) gl;
			renderingFbo = framebuffer.renderingFbo(gl3, drawable.getSurfaceWidth(), drawable.getSurfaceHeight(),
					desiredNumSamples, desiredUseStencilBuffer);
			gl3.glBindFramebuffer(GL_DRAW_FRAMEBUFFER, renderingFbo);
		}

		viewState.update(viewSettings, width, height);

		drawScene(drawable);

		if (framebuffer != null) {
			GL3 gl3 = (GL3) gl;

			gl3.glBindFramebuffer(GL_DRAW_FRAMEBUFFER, 0);
			gl3.glBindFramebuffer(GL_READ_FRAMEBUFFER, renderingFbo);
			gl3.glDrawBuffer(GL_BACK);
			gl3.glBlitFramebuffer(0, 0, drawable.getSurfaceWidth(), drawable.getSurfaceHeight(), 0, 0,
					drawable.getSurfaceWidth(), drawable.getSurfaceHeight(), GL.GL_COLOR_BUFFER_BIT, GL_NEAREST);
		}
	}

	@Override
	public void dispose(GLAutoDrawable drawable) {
		GL2ES2 gl = (GL2ES2) drawable.getGL();

		if (framebuffer != null) {
			framebuffer.dispose((GL3) gl);
		}
	}

	protected void drawScene(GLAutoDrawable drawable) {
		if (scene != null) {
			scene.draw(viewState, (GL2ES2) drawable.getGL(), m, n);
		}
	}

	public int getDesiredNumSamples() {
		return desiredNumSamples;
	}

	public JoglScene getScene() {
		return scene;
	}

	public JoglViewSettings getViewSettings() {
		return viewSettings;
	}

	public JoglViewState getViewState() {
		return viewState;
	}

	@Override
	public void init(GLAutoDrawable drawable) {
		GL2ES2 gl = (GL2ES2) drawable.getGL();

		if (framebuffer != null) {
			framebuffer.init((GL3) gl);
		}
	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
		GL2ES2 gl = (GL2ES2) drawable.getGL();

		this.width = width;
		this.height = height;

		gl.glViewport(x, y, width, height);
	}

	public void setDesiredNumSamples(int desiredNumSamples) {
		this.desiredNumSamples = desiredNumSamples;
	}

	public void setScene(JoglScene scene) {
		this.scene = scene;
	}

	public void setViewSettings(JoglViewSettings viewSettings) {
		this.viewSettings = viewSettings;
	}

	public boolean isDesiredUseStencilBuffer() {
		return desiredUseStencilBuffer;
	}

	public void setDesiredUseStencilBuffer(boolean useStencilBuffer) {
		desiredUseStencilBuffer = useStencilBuffer;
	}
}
