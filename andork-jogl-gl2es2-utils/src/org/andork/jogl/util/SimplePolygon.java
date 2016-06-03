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
package org.andork.jogl.util;

import static org.andork.jogl.util.JoglUtils.checkGLError;

import java.nio.Buffer;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2ES2;

public class SimplePolygon {
	static int program;
	static int vertexShader;
	static int fragmentShader;
	private static final String vertexShaderCode = "uniform mat4 mvpMatrix;" +
			"attribute vec3 coord;" +
			"void main() {" +
			"  gl_Position = mvpMatrix * vec4(coord, 1.0);" +
			"}";
	private static final String fragmentShaderCode = "uniform vec4 color;" +
			"void main() {" +
			"  gl_FragColor = color;" +
			"}";

	public static void globalInit(GL2ES2 gl) {
		vertexShader = JoglUtils.loadShader(gl, GL2ES2.GL_VERTEX_SHADER, vertexShaderCode);
		fragmentShader = JoglUtils.loadShader(gl, GL2ES2.GL_FRAGMENT_SHADER, fragmentShaderCode);

		program = gl.glCreateProgram();
		gl.glAttachShader(program, vertexShader);
		checkGLError(gl, "glAttachShader");
		gl.glAttachShader(program, fragmentShader);
		checkGLError(gl, "glAttachShader");
		gl.glLinkProgram(program);
		checkGLError(gl, "glLinkProgram");
	}

	public int coordsType = GL2ES2.GL_FLOAT;
	public int coordsStride = 12;
	public int vertexCount;

	public Buffer coords;

	public final float[] color = { 1, 0, 0, 1 };

	int vbo;

	public void draw(GL2ES2 gl, float[] mvpMatrix) {
		gl.glUseProgram(program);
		checkGLError(gl, "glUseProgram");

		int mvpMatrixIndex = gl.glGetUniformLocation(program, "mvpMatrix");
		checkGLError(gl, "glGetUniformLocation");
		gl.glUniformMatrix4fv(mvpMatrixIndex, 1, false, mvpMatrix, 0);
		checkGLError(gl, "glUniformMatrix4fv");

		int colorLocation = gl.glGetUniformLocation(program, "color");
		checkGLError(gl, "glGetUniformLocation");
		gl.glUniform4fv(colorLocation, 1, color, 0);
		checkGLError(gl, "glUniform4fv");

		gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vbo);
		checkGLError(gl, "glBindBuffer");

		int coordIndex = gl.glGetAttribLocation(program, "coord");
		checkGLError(gl, "glGetAttribLocation");
		gl.glEnableVertexAttribArray(coordIndex);
		checkGLError(gl, "glEnableVertexAttribArray");
		gl.glVertexAttribPointer(coordIndex, 3, coordsType, false, coordsStride, 0);
		checkGLError(gl, "glVertexAttribPointer");

		gl.glDrawArrays(GL.GL_LINE_STRIP, 0, vertexCount);
		checkGLError(gl, "glDrawArrays");

		gl.glBindBuffer(GL.GL_ARRAY_BUFFER, 0);
	}

	public void init(GL2ES2 gl) {
		int[] temp = new int[1];

		gl.glGenBuffers(1, temp, 0);
		checkGLError(gl, "glGenBuffers");
		vbo = temp[0];

		gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vbo);
		checkGLError(gl, "glBindBuffer");
		coords.position(0);
		gl.glBufferData(GL.GL_ARRAY_BUFFER, coords.capacity(), coords, GL.GL_STATIC_DRAW);
		checkGLError(gl, "glBufferData");
		gl.glBindBuffer(GL.GL_ARRAY_BUFFER, 0);
	}
}
