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
import static com.jogamp.opengl.GL.GL_DEPTH_COMPONENT32;
import static com.jogamp.opengl.GL.GL_FRAMEBUFFER;
import static com.jogamp.opengl.GL.GL_RENDERBUFFER;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL3;

public class GL3Framebuffer implements GL3Resource {
	private long lastDisplay;

	private int maxNumSamples = 0;
	private int currentNumSamples = 1;
	private int renderingFboWidth;
	private int renderingFboHeight;

	private int renderingFbo = -1;
	private int renderingColorBuffer = -1;
	private int renderingDepthBuffer = -1;

	private void destroyOffscreenBuffers(GL3 gl) {
		int[] temps = new int[1];
		if (renderingFbo >= 0) {
			temps[0] = renderingFbo;
			gl.glDeleteFramebuffers(1, temps, 0);
			renderingFbo = -1;
		}
		if (renderingColorBuffer >= 0) {
			temps[0] = renderingColorBuffer;
			gl.glDeleteRenderbuffers(1, temps, 0);
			renderingColorBuffer = -1;
		}
		if (renderingDepthBuffer >= 0) {
			temps[0] = renderingDepthBuffer;
			gl.glDeleteRenderbuffers(1, temps, 0);
			renderingDepthBuffer = -1;
		}
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

	public int renderingFbo(GL3 gl, int width, int height, int desiredNumSamples) {
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
				|| elapsed >= 1000 && (renderingFboWidth != width || renderingFboHeight != height)) {
			destroyOffscreenBuffers(gl3);

			int[] temps = new int[2];
			renderingFboWidth = width;
			renderingFboHeight = height;

			gl3.glGenFramebuffers(1, temps, 0);
			renderingFbo = temps[0];
			gl3.glBindFramebuffer(GL.GL_FRAMEBUFFER, renderingFbo);

			gl3.glGenRenderbuffers(2, temps, 0);
			renderingColorBuffer = temps[0];
			renderingDepthBuffer = temps[1];

			currentNumSamples = targetNumSamples;

			if (currentNumSamples > 1) {
				gl3.glBindRenderbuffer(GL_RENDERBUFFER, renderingColorBuffer);
				gl3.glRenderbufferStorageMultisample(GL_RENDERBUFFER, currentNumSamples, GL.GL_RGBA8,
						renderingFboWidth, renderingFboHeight);

				gl3.glBindRenderbuffer(GL_RENDERBUFFER, renderingDepthBuffer);
				gl3.glRenderbufferStorageMultisample(GL_RENDERBUFFER, currentNumSamples, GL_DEPTH_COMPONENT32,
						renderingFboWidth, renderingFboHeight);
			} else {
				gl3.glBindRenderbuffer(GL_RENDERBUFFER, renderingColorBuffer);
				gl3.glRenderbufferStorage(GL_RENDERBUFFER, GL.GL_RGBA8, renderingFboWidth, renderingFboHeight);

				gl3.glBindRenderbuffer(GL_RENDERBUFFER, renderingDepthBuffer);
				gl3.glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH_COMPONENT32, renderingFboWidth, renderingFboHeight);
			}

			gl3.glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_RENDERBUFFER, renderingColorBuffer);
			gl3.glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_RENDERBUFFER, renderingDepthBuffer);
		}

		return renderingFbo;
	}
}
