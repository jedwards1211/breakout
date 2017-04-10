/*******************************************************************************
 * Breakout Cave Survey Visualizer
 *
 * Copyright (C) 2014 James Edwards
 *
 * jedwards8 at fastmail dot fm
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *******************************************************************************/
package org.andork.jogl;

import static com.jogamp.opengl.GL.GL_COLOR_ATTACHMENT0;
import static com.jogamp.opengl.GL.GL_DEPTH_ATTACHMENT;
import static com.jogamp.opengl.GL.GL_FRAMEBUFFER;
import static com.jogamp.opengl.GL.GL_RENDERBUFFER;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2ES3;
import com.jogamp.opengl.GL2GL3;
import com.jogamp.opengl.GL3;
import com.jogamp.opengl.GL3ES3;

public class GL3Framebuffer implements GL3Resource {
	private long lastDisplay;

	private int maxNumSamples = 0;
	private int currentNumSamples = 1;
	private boolean currentUseStencilBuffer = false;
	private int renderingFboWidth;
	private int renderingFboHeight;

	private int renderingFbo = -1;
	private int renderingColorBuffer = -1;
	private int renderingDepthBuffer = -1;

	private void destroyOffscreenBuffers(GL3 gl) {
		int[] temps = new int[2];
		if (renderingFbo >= 0) {
			temps[0] = renderingFbo;
			gl.glDeleteFramebuffers(1, temps, 0);
			renderingFbo = -1;
		}
		int k = 0;
		if (renderingColorBuffer >= 0) {
			temps[k++] = renderingColorBuffer;
		}
		if (renderingDepthBuffer >= 0) {
			temps[k++] = renderingDepthBuffer;
		}
		gl.glDeleteRenderbuffers(k, temps, 0);
		renderingColorBuffer = -1;
		renderingDepthBuffer = -1;
	}

	@Override
	public void dispose(GL3 gl) {
		maxNumSamples = 0;
		destroyOffscreenBuffers(gl);
	}

	public int getMaxNumSamples() {
		return maxNumSamples;
	}

	@Override
	public void init(GL3 gl) {
		int[] temp = new int[1];
		gl.glGetIntegerv(GL.GL_MAX_SAMPLES, temp, 0);
		maxNumSamples = temp[0];
	}

	public int renderingFbo(GL3 gl, int width, int height, int desiredNumSamples, boolean desiredUseStencilBuffer) {
		if (maxNumSamples < 0) {
			init(gl);
		}

		int targetNumSamples = Math.max(1, Math.min(maxNumSamples, desiredNumSamples));

		long lastDisplay = this.lastDisplay;
		this.lastDisplay = System.currentTimeMillis();
		long elapsed = this.lastDisplay - lastDisplay;

		GL3 gl3 = gl;

		if (renderingFbo < 0 || renderingFboWidth < width || renderingFboHeight < height
				|| targetNumSamples != currentNumSamples
				|| desiredUseStencilBuffer != currentUseStencilBuffer
				|| elapsed >= 1000 && (renderingFboWidth != width || renderingFboHeight != height)) {
			destroyOffscreenBuffers(gl3);

			int[] temps = new int[3];
			renderingFboWidth = width;
			renderingFboHeight = height;

			gl3.glGenFramebuffers(1, temps, 0);
			renderingFbo = temps[0];
			gl3.glBindFramebuffer(GL.GL_FRAMEBUFFER, renderingFbo);

			int numBuffers = 2;
			gl3.glGenRenderbuffers(numBuffers, temps, 0);
			renderingColorBuffer = temps[0];
			renderingDepthBuffer = temps[1];

			currentNumSamples = targetNumSamples;
			currentUseStencilBuffer = desiredUseStencilBuffer;

			if (currentNumSamples > 1) {
				gl3.glBindRenderbuffer(GL_RENDERBUFFER, renderingColorBuffer);
				gl3.glRenderbufferStorageMultisample(GL_RENDERBUFFER, currentNumSamples, GL.GL_RGBA8,
						renderingFboWidth, renderingFboHeight);

				gl3.glBindRenderbuffer(GL_RENDERBUFFER, renderingDepthBuffer);
				if (currentUseStencilBuffer) {
					gl3.glRenderbufferStorageMultisample(GL_RENDERBUFFER, currentNumSamples,
							GL.GL_DEPTH24_STENCIL8, renderingFboWidth, renderingFboHeight);
				} else {
					gl3.glRenderbufferStorageMultisample(GL_RENDERBUFFER, currentNumSamples,
							GL.GL_DEPTH_COMPONENT24, renderingFboWidth, renderingFboHeight);
				}
			} else {
				gl3.glBindRenderbuffer(GL_RENDERBUFFER, renderingColorBuffer);
				gl3.glRenderbufferStorage(GL_RENDERBUFFER, GL.GL_RGBA8, renderingFboWidth, renderingFboHeight);

				gl3.glBindRenderbuffer(GL_RENDERBUFFER, renderingDepthBuffer);
				if (currentUseStencilBuffer) {
					gl3.glRenderbufferStorage(GL_RENDERBUFFER, GL.GL_DEPTH24_STENCIL8,
							renderingFboWidth, renderingFboHeight);
				} else {
					gl3.glRenderbufferStorage(GL_RENDERBUFFER, GL.GL_DEPTH_COMPONENT24,
							renderingFboWidth, renderingFboHeight);
				}
			}

			gl3.glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_RENDERBUFFER, renderingColorBuffer);
			if (currentUseStencilBuffer) {
				gl3.glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL2ES3.GL_DEPTH_STENCIL_ATTACHMENT,
						GL_RENDERBUFFER, renderingDepthBuffer);
			} else {
				gl3.glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT,
						GL_RENDERBUFFER, renderingDepthBuffer);
			}

			int status = gl3.glCheckFramebufferStatus(GL_FRAMEBUFFER);
			switch (status) {
			case GL.GL_FRAMEBUFFER_COMPLETE:
				break;

			case GL.GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT:
				throw new RuntimeException("An attachment could not be bound to frame buffer object!");

			case GL.GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT:
				throw new RuntimeException(
						"Attachments are missing! At least one image (texture) must be bound to the frame buffer object!");

			case GL.GL_FRAMEBUFFER_INCOMPLETE_DIMENSIONS:
				throw new RuntimeException(
						"The dimensions of the buffers attached to the currently used frame buffer object do not match!");

			case GL.GL_FRAMEBUFFER_INCOMPLETE_FORMATS:
				throw new RuntimeException(
						"The formats of the currently used frame buffer object are not supported or do not fit together!");

			case GL2GL3.GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER:
				throw new RuntimeException(
						"A Draw buffer is incomplete or undefinied. All draw buffers must specify attachment points that have images attached.");

			case GL2GL3.GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER:
				throw new RuntimeException(
						"A Read buffer is incomplete or undefinied. All read buffers must specify attachment points that have images attached.");

			case GL.GL_FRAMEBUFFER_INCOMPLETE_MULTISAMPLE:
				throw new RuntimeException("All images must have the same number of multisample samples.");

			case GL3ES3.GL_FRAMEBUFFER_INCOMPLETE_LAYER_TARGETS:
				throw new RuntimeException(
						"If a layered image is attached to one attachment, then all attachments must be layered attachments. The attached layers do not have to have the same number of layers, nor do the layers have to come from the same kind of texture.");

			case GL.GL_FRAMEBUFFER_UNSUPPORTED:
				throw new RuntimeException("Attempt to use an unsupported format combinaton!");

			default:
				throw new RuntimeException("Unknown error while attempting to create frame buffer object!");
			}
		}

		return renderingFbo;
	}
}
