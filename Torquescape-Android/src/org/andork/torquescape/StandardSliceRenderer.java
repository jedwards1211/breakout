package org.andork.torquescape;

import static org.andork.torquescape.GLUtils.checkGlError;
import static org.andork.torquescape.GLUtils.checkProgramLinkStatus;

import org.andork.torquescape.model.slice.StandardSlice;

import android.opengl.GLES20;

public class StandardSliceRenderer implements ISliceRenderer<StandardSlice>
{
	public static final Factory FACTORY = new Factory();

	private final String vertexShaderCode =
			// This matrix member variable provides a hook to manipulate
			// the coordinates of the objects that use this vertex shader
			"uniform mat4 uMVMatrix;" +
					"uniform mat4 uPMatrix;" +
					"attribute vec4 vPosition;" +
					"attribute vec3 vNormal;" +
					"varying vec3 v_fxnormal;" +
					"void main() {" +
					"  v_fxnormal = normalize((uMVMatrix * vec4(vNormal, 0)).xyz);" +
					"  gl_Position = uPMatrix * uMVMatrix * vPosition;" +
					"}";

	private final String fragmentShaderCode =
			"precision mediump float;" +
					"varying vec3 v_fxnormal;" +
					"uniform vec4 vAmbientColor;" +
					"uniform vec4 vDiffuseColor;" +
					"void main() {" +
					"  float intensity = dot(v_fxnormal, vec3(0.0, 0.0, 1.0));" +
					"  gl_FragColor = mix(vAmbientColor, vDiffuseColor, intensity);" +
					"}";

	private int mProgram;

	private int indexEbo;

	private ZoneRenderer zoneRenderer;
	private StandardSlice slice;

	public StandardSliceRenderer(ZoneRenderer zoneRenderer, StandardSlice slice)
	{
		this.zoneRenderer = zoneRenderer;
		this.slice = slice;
	}

	public void init()
	{
		int vertexShader = GLUtils.loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
		int fragmentShader = GLUtils.loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);

		mProgram = GLES20.glCreateProgram(); // create empty OpenGL ES Program
		checkGlError("glCreateProgram");
		GLES20.glAttachShader(mProgram, vertexShader); // add the vertex shader
		checkGlError("glAttachShader");
		// to program
		GLES20.glAttachShader(mProgram, fragmentShader); // add the fragment
		checkGlError("glAttachShader");
		// shader to program
		GLES20.glLinkProgram(mProgram); // creates OpenGL ES program executables
		checkGlError("glLinkProgram");
		checkProgramLinkStatus(mProgram);

		int[] vbos = new int[1];

		GLES20.glGenBuffers(1, vbos, 0);
		checkGlError("glGenBuffers");

		indexEbo = vbos[0];

		slice.indexBuffer.position(0);

		GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, indexEbo);
		checkGlError("glBindBuffer");
		GLES20.glBufferData(GLES20.GL_ELEMENT_ARRAY_BUFFER, slice.indexBuffer.capacity() * 2, slice.indexBuffer, GLES20.GL_STATIC_DRAW);
		checkGlError("glBufferData");

		GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);
		checkGlError("glBindBuffer");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.andork.torquescape.SliceRenderer#draw(float[], float[],
	 * org.andork.torquescape.model.Zone,
	 * org.andork.torquescape.model.StandardSlice)
	 */
	@Override
	public void draw(float[] mvMatrix, float[] pMatrix)
	{
		GLES20.glUseProgram(mProgram);
		checkGlError("glUseProgram");

		int mvMatrixLoc = GLES20.glGetUniformLocation(mProgram, "uMVMatrix");
		checkGlError("glGetUniformLocation");
		GLES20.glUniformMatrix4fv(mvMatrixLoc, 1, false, mvMatrix, 0);
		checkGlError("glUniformMatrix4fv");

		int pMatrixLoc = GLES20.glGetUniformLocation(mProgram, "uPMatrix");
		checkGlError("glGetUniformLocation");
		GLES20.glUniformMatrix4fv(pMatrixLoc, 1, false, pMatrix, 0);
		checkGlError("glUniformMatrix4fv");

		int ambientLoc = GLES20.glGetUniformLocation(mProgram, "vAmbientColor");
		checkGlError("glGetUniformLocation");
		GLES20.glUniform4fv(ambientLoc, 1, slice.ambientColor, 0);
		checkGlError("glUniform4fv");

		int diffuseLoc = GLES20.glGetUniformLocation(mProgram, "vDiffuseColor");
		checkGlError("glGetUniformLocation");
		GLES20.glUniform4fv(diffuseLoc, 1, slice.diffuseColor, 0);
		checkGlError("glUniform4fv");

		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, zoneRenderer.vertVbo);
		checkGlError("glBindBuffer");

		int vPositionLoc = GLES20.glGetAttribLocation(mProgram, "vPosition");
		checkGlError("glGetAttribLocation");
		GLES20.glEnableVertexAttribArray(vPositionLoc);
		checkGlError("glEnableVertexAttribArray");
		GLES20.glVertexAttribPointer(vPositionLoc, 3, GLES20.GL_FLOAT, false, 24, 0);
		checkGlError("glVertexAttribPointer");

		int vNormalLoc = GLES20.glGetAttribLocation(mProgram, "vNormal");
		checkGlError("glGetAttribLocation");
		GLES20.glEnableVertexAttribArray(vNormalLoc);
		checkGlError("glEnableVertexAttribArray");
		GLES20.glVertexAttribPointer(vNormalLoc, 3, GLES20.GL_FLOAT, false, 24, 12);
		checkGlError("glVertexAttribPointer");

		GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, indexEbo);
		checkGlError("glBindBuffer");

		GLES20.glDrawElements(GLES20.GL_TRIANGLES, slice.indexBuffer.capacity(), GLES20.GL_UNSIGNED_SHORT, 0);
		checkGlError("glDrawElements");

		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
		checkGlError("glBindBuffer");
		GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);
		checkGlError("glBindBuffer");

	}

	public static class Factory implements ISliceRendererFactory<StandardSlice>
	{
		private Factory()
		{

		}

		@Override
		public ISliceRenderer<StandardSlice> create(ZoneRenderer zoneRenderer, StandardSlice slice)
		{
			return new StandardSliceRenderer(zoneRenderer, slice);
		}
	}
}
