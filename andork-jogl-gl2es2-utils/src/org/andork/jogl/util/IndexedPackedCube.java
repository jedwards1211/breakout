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

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.CharBuffer;
import java.nio.FloatBuffer;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2ES2;

public class IndexedPackedCube {
	static float verts[] = {
			// top (red)
			1, 1, 1, 0, 1, 0, 1, 0, 0, 1,
			1, 1, -1, 0, 1, 0, 1, 0, 0, 1,
			-1, 1, 1, 0, 1, 0, 1, 0, 0, 1,
			-1, 1, -1, 0, 1, 0, 1, 0, 0, 1,
			// bottom (green)
			1, -1, 1, 0, -1, 0, 0, 1, 0, 1,
			-1, -1, 1, 0, -1, 0, 0, 1, 0, 1,
			1, -1, -1, 0, -1, 0, 0, 1, 0, 1,
			-1, -1, -1, 0, -1, 0, 0, 1, 0, 1,
			// front (blue)
			1, -1, 1, 0, 0, 1, 0, 0, 1, 1,
			1, 1, 1, 0, 0, 1, 0, 0, 1, 1,
			-1, -1, 1, 0, 0, 1, 0, 0, 1, 1,
			-1, 1, 1, 0, 0, 1, 0, 0, 1, 1,
			// back (yellow)
			1, -1, -1, 0, 0, -1, 1, 1, 0, 1,
			-1, -1, -1, 0, 0, -1, 1, 1, 0, 1,
			1, 1, -1, 0, 0, -1, 1, 1, 0, 1,
			-1, 1, -1, 0, 0, -1, 1, 1, 0, 1,
			// left (cyan)
			-1, -1, -1, -1, 0, 0, 0, 1, 1, 1,
			-1, -1, 1, -1, 0, 0, 0, 1, 1, 1,
			-1, 1, -1, -1, 0, 0, 0, 1, 1, 1,
			-1, 1, 1, -1, 0, 0, 0, 1, 1, 1,
			// right (magenta)
			1, 1, -1, 1, 0, 0, 1, 0, 1, 1,
			1, 1, 1, 1, 0, 0, 1, 0, 1, 1,
			1, -1, -1, 1, 0, 0, 1, 0, 1, 1,
			1, -1, 1, 1, 0, 0, 1, 0, 1, 1,
	};

	static char indices[] = {
			0, 1, 2, 3, 2, 1,
			4, 5, 6, 7, 6, 5,
			8, 9, 10, 11, 10, 9,
			12, 13, 14, 15, 14, 13,
			16, 17, 18, 19, 18, 17,
			20, 21, 22, 23, 22, 21
	};

	// number of coordinates per vertex in this array
	static final int COORDS_PER_VERTEX = 3;
	static final int NORMALS_PER_VERTEX = 3;

	static final int COLORS_PER_VERTEX = 4;
	static final int VALUES_PER_VERTEX = COORDS_PER_VERTEX + NORMALS_PER_VERTEX + COLORS_PER_VERTEX;
	static final int VERTEX_STRIDE = VALUES_PER_VERTEX * 4;
	static final int NORMAL_OFFSET = COORDS_PER_VERTEX;
	static final int COLOR_OFFSET = NORMAL_OFFSET + NORMALS_PER_VERTEX;
	static final int VERTEX_COUNT = verts.length / VALUES_PER_VERTEX;

	private final String vertexShaderCode =
	// This matrix member variable provides a hook to manipulate
	// the coordinates of the objects that use this vertex shader
	"uniform mat4 uMVMatrix;" +
			"uniform mat4 uPMatrix;" +
			"attribute vec4 vPosition;" +
			"attribute vec3 vNormal;" +
			"attribute vec4 vColor;" +
			"varying vec3 v_fxpos;" +
			"varying vec3 v_fxlightpos;" +
			"varying vec3 v_fxnormal;" +
			"varying vec4 v_fcolor;" +
			"void main() {" +
			"  v_fcolor = vColor;" +
			"  v_fxpos = (uMVMatrix * vPosition).xyz;" +
			"  v_fxlightpos = (uMVMatrix * vec4(1.1, 1.8, 3.5, 1.0)).xyz;" +
			"  v_fxnormal = normalize((uMVMatrix * vec4(vNormal, 0)).xyz);" +
			"  gl_Position = uPMatrix * uMVMatrix * vPosition;" +
			"}";
	private final String fragmentShaderCode = "precision lowp float;" +
			"varying vec3 v_fxpos;" +
			"varying vec3 v_fxlightpos;" +
			"varying vec3 v_fxnormal;" +
			"varying vec4 v_fcolor;" +
			"void main() {" +
			"  vec3 incident = v_fxpos - v_fxlightpos;" +
			"  vec3 ref_light = reflect(normalize(incident), v_fxnormal);" +
			"  float intensity = dot(ref_light, vec3(0.0, 0.0, 1.0)) * 25.0 / (dot(incident, incident) + dot(v_fxpos, v_fxpos));"
			+
			"  if (dot(v_fxnormal, ref_light) <= 0.0)" +
			"    intensity = 0.0;" +
			"  vec4 color2 = v_fcolor * clamp(intensity, 0.2, 1.0);" +
			"  gl_FragColor = color2;" +
			"}";

	private FloatBuffer vertexBuffer;

	CharBuffer indexBuffer;

	private int mProgram;
	private int mPositionHandle;
	private int mNormalHandle;
	private int mMVMatrixHandle;

	private int mPMatrixHandle;
	private int mColorHandle;
	private int vbo;

	private int ebo;

	public IndexedPackedCube() {
	}

	public void draw(GL2ES2 gl, float[] mvMatrix, float[] pMatrix) {
		// Add program to OpenGL ES environment
		gl.glUseProgram(mProgram);
		checkGLError(gl, "glUseProgram");

		mMVMatrixHandle = gl.glGetUniformLocation(mProgram, "uMVMatrix");
		checkGLError(gl, "glGetUniformLocation");

		gl.glUniformMatrix4fv(mMVMatrixHandle, 1, false, mvMatrix, 0);
		checkGLError(gl, "glUniformMatrix4fv");

		mPMatrixHandle = gl.glGetUniformLocation(mProgram, "uPMatrix");
		checkGLError(gl, "glGetUniformLocation");

		gl.glUniformMatrix4fv(mPMatrixHandle, 1, false, pMatrix, 0);
		checkGLError(gl, "glUniformMatrix4fv");

		gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, ebo);
		checkGLError(gl, "glBindBuffer");

		gl.glDrawElements(GL.GL_TRIANGLES, indices.length, GL.GL_UNSIGNED_SHORT, 0);
		checkGLError(gl, "glDrawArrays");

		gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, 0);
		checkGLError(gl, "glBindBuffer");
	}

	public void init(GL2ES2 gl) {
		int vertexShader = JoglUtils.loadShader(gl, GL2ES2.GL_VERTEX_SHADER, vertexShaderCode);
		int fragmentShader = JoglUtils.loadShader(gl, GL2ES2.GL_FRAGMENT_SHADER, fragmentShaderCode);

		mProgram = gl.glCreateProgram(); // create empty OpenGL ES Program
		checkGLError(gl, "glCreateProgram");
		gl.glAttachShader(mProgram, vertexShader); // add the vertex shader
		checkGLError(gl, "glAttachShader");
		// to program
		gl.glAttachShader(mProgram, fragmentShader); // add the fragment
		checkGLError(gl, "glAttachShader");
		// shader to program
		gl.glLinkProgram(mProgram); // creates OpenGL ES program executables
		checkGLError(gl, "glLinkProgram");

		// initialize vertex byte buffer for shape coordinates
		ByteBuffer bb = ByteBuffer.allocateDirect(
				// (number of coordinate values * 4 bytes per float)
				verts.length * 4);
		// use the device hardware's native byte order
		bb.order(ByteOrder.nativeOrder());

		// create a floating point buffer from the ByteBuffer
		vertexBuffer = bb.asFloatBuffer();
		// add the coordinates to the FloatBuffer
		vertexBuffer.put(verts);
		// set the buffer to read the first coordinate
		vertexBuffer.position(0);

		bb = ByteBuffer.allocateDirect(indices.length * 2);
		bb.order(ByteOrder.nativeOrder());
		indexBuffer = bb.asCharBuffer();
		indexBuffer.put(indices);
		indexBuffer.position(0);

		int[] vbos = new int[1];
		gl.glGenBuffers(1, vbos, 0);
		vbo = vbos[0];

		int[] ebos = new int[1];
		gl.glGenBuffers(1, ebos, 0);
		ebo = ebos[0];

		gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vbo);
		checkGLError(gl, "glBindBuffer");

		gl.glBufferData(GL.GL_ARRAY_BUFFER, vertexBuffer.capacity() * 4, vertexBuffer, GL.GL_STATIC_DRAW);
		checkGLError(gl, "glBufferData");

		// get handle to vertex shader's vPosition member
		mPositionHandle = gl.glGetAttribLocation(mProgram, "vPosition");
		checkGLError(gl, "glGetAttribLocation");

		// Enable a handle to the triangle vertices
		gl.glEnableVertexAttribArray(mPositionHandle);
		checkGLError(gl, "glEnableVertexAttribArray");

		vertexBuffer.position(0);

		// Prepare the triangle coordinate data
		gl.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX, GL.GL_FLOAT, false, VERTEX_STRIDE, 0);
		checkGLError(gl, "glVertexAttribPointer");

		// get handle to fragment shader's vColor member
		mColorHandle = gl.glGetAttribLocation(mProgram, "vColor");
		checkGLError(gl, "glGetAttribLocation");

		// Enable a handle to the triangle vertices
		gl.glEnableVertexAttribArray(mColorHandle);
		checkGLError(gl, "glEnableVertexAttribArray");

		vertexBuffer.position(COLOR_OFFSET);

		// Prepare the triangle coordinate data
		gl.glVertexAttribPointer(mColorHandle, COLORS_PER_VERTEX, GL.GL_FLOAT, false, VERTEX_STRIDE,
				COLOR_OFFSET * 4);
		checkGLError(gl, "glVertexAttribPointer");

		// get handle to fragment shader's vNormal member
		mNormalHandle = gl.glGetAttribLocation(mProgram, "vNormal");
		checkGLError(gl, "glGetAttribLocation");

		// Enable a handle to the triangle vertices
		gl.glEnableVertexAttribArray(mNormalHandle);
		checkGLError(gl, "glEnableVertexAttribArray");

		vertexBuffer.position(NORMAL_OFFSET);

		// Prepare the triangle coordinate data
		gl.glVertexAttribPointer(mNormalHandle, NORMALS_PER_VERTEX, GL.GL_FLOAT, false, VERTEX_STRIDE,
				NORMAL_OFFSET * 4);
		checkGLError(gl, "glVertexAttribPointer");

		gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, ebo);
		checkGLError(gl, "glBindBuffer");

		gl.glBufferData(GL.GL_ELEMENT_ARRAY_BUFFER, indexBuffer.capacity() * 2, indexBuffer, GL.GL_STATIC_DRAW);
		checkGLError(gl, "glBufferData");

		gl.glBindBuffer(GL.GL_ARRAY_BUFFER, 0);
		checkGLError(gl, "glBindBuffer");

		gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, 0);
		checkGLError(gl, "glBindBuffer");
	}
}
