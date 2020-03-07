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

import static com.jogamp.opengl.GL.GL_CLAMP_TO_EDGE;
import static com.jogamp.opengl.GL.GL_COLOR_ATTACHMENT0;
import static com.jogamp.opengl.GL.GL_FRAMEBUFFER;
import static com.jogamp.opengl.GL.GL_LINEAR;
import static com.jogamp.opengl.GL.GL_RGBA;
import static com.jogamp.opengl.GL.GL_TEXTURE_2D;
import static com.jogamp.opengl.GL.GL_TEXTURE_MAG_FILTER;
import static com.jogamp.opengl.GL.GL_TEXTURE_MIN_FILTER;
import static com.jogamp.opengl.GL.GL_TEXTURE_WRAP_S;
import static com.jogamp.opengl.GL.GL_TEXTURE_WRAP_T;
import static com.jogamp.opengl.GL.GL_UNSIGNED_BYTE;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2ES2;
import com.jogamp.opengl.GL2GL3;
import com.jogamp.opengl.GL3ES3;

public class GLFramebufferTexture implements JoglResource {
	private long lastDisplay;

	private int width;
	private int height;

	private int framebuffer = -1;
	private int texture = -1;

	private void destroyOffscreenBuffers(GL2ES2 gl) {
		int[] temps = new int[1];
		if (framebuffer >= 0) {
			temps[0] = framebuffer;
			gl.glDeleteFramebuffers(1, temps, 0);
			framebuffer = -1;
		}
		if (texture >= 0) {
			temps[0] = texture;
			gl.glDeleteTextures(1, temps, 0);
			texture = -1;
		}
	}

	@Override
	public void dispose(GL2ES2 gl) {
		destroyOffscreenBuffers(gl);
	}

	@Override
	public boolean init(GL2ES2 gl) {
		return true;
	}

	public int renderingFbo(GL2ES2 gl, int width, int height) {
		long lastDisplay = this.lastDisplay;
		this.lastDisplay = System.currentTimeMillis();
		long elapsed = this.lastDisplay - lastDisplay;

		if (framebuffer < 0
			|| this.width < width
			|| this.height < height
			|| elapsed >= 1000 && (this.width != width || this.height != height)) {
			destroyOffscreenBuffers(gl);

			int[] temps = new int[1];
			this.width = width;
			this.height = height;

			gl.glGenFramebuffers(1, temps, 0);
			framebuffer = temps[0];
			gl.glBindFramebuffer(GL_FRAMEBUFFER, framebuffer);

			gl.glGenTextures(1, temps, 0);
			texture = temps[0];
			gl.glEnable(GL_TEXTURE_2D);
			gl.glBindTexture(GL_TEXTURE_2D, texture);
			gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
			gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
			gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
			gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
			gl.glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, null);
			gl.glBindTexture(GL_TEXTURE_2D, 0);

			gl.glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, texture, 0);

			int status = gl.glCheckFramebufferStatus(GL_FRAMEBUFFER);
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

		return framebuffer;
	}

	public int texture() {
		return texture;
	}

	public int width() {
		return width;
	}

	public int height() {
		return height;
	}
}
