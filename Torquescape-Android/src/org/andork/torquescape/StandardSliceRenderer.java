package org.andork.torquescape;

import static org.andork.gles20.util.GLUtils.checkGLError;
import static org.andork.gles20.util.GLUtils.loadProgram;

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
		mProgram = loadProgram( vertexShaderCode , fragmentShaderCode , true );

		int[] vbos = new int[1];

		GLES20.glGenBuffers(1, vbos, 0);
		checkGLError("glGenBuffers");

		indexEbo = vbos[0];

		slice.indexBuffer.position(0);

		GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, indexEbo);
		checkGLError("glBindBuffer");
		GLES20.glBufferData(GLES20.GL_ELEMENT_ARRAY_BUFFER, slice.indexBuffer.capacity() * 2, slice.indexBuffer, GLES20.GL_STATIC_DRAW);
		checkGLError("glBufferData");

		GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);
		checkGLError("glBindBuffer");
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
		checkGLError("glUseProgram");

		int mvMatrixLoc = GLES20.glGetUniformLocation(mProgram, "uMVMatrix");
		checkGLError("glGetUniformLocation");
		GLES20.glUniformMatrix4fv(mvMatrixLoc, 1, false, mvMatrix, 0);
		checkGLError("glUniformMatrix4fv");

		int pMatrixLoc = GLES20.glGetUniformLocation(mProgram, "uPMatrix");
		checkGLError("glGetUniformLocation");
		GLES20.glUniformMatrix4fv(pMatrixLoc, 1, false, pMatrix, 0);
		checkGLError("glUniformMatrix4fv");

		int ambientLoc = GLES20.glGetUniformLocation(mProgram, "vAmbientColor");
		checkGLError("glGetUniformLocation");
		GLES20.glUniform4fv(ambientLoc, 1, slice.ambientColor, 0);
		checkGLError("glUniform4fv");

		int diffuseLoc = GLES20.glGetUniformLocation(mProgram, "vDiffuseColor");
		checkGLError("glGetUniformLocation");
		GLES20.glUniform4fv(diffuseLoc, 1, slice.diffuseColor, 0);
		checkGLError("glUniform4fv");

		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, zoneRenderer.vertVbo);
		checkGLError("glBindBuffer");

		int vPositionLoc = GLES20.glGetAttribLocation(mProgram, "vPosition");
		checkGLError("glGetAttribLocation");
		GLES20.glEnableVertexAttribArray(vPositionLoc);
		checkGLError("glEnableVertexAttribArray");
		GLES20.glVertexAttribPointer(vPositionLoc, 3, GLES20.GL_FLOAT, false, 24, 0);
		checkGLError("glVertexAttribPointer");

		int vNormalLoc = GLES20.glGetAttribLocation(mProgram, "vNormal");
		checkGLError("glGetAttribLocation");
		GLES20.glEnableVertexAttribArray(vNormalLoc);
		checkGLError("glEnableVertexAttribArray");
		GLES20.glVertexAttribPointer(vNormalLoc, 3, GLES20.GL_FLOAT, false, 24, 12);
		checkGLError("glVertexAttribPointer");

		GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, indexEbo);
		checkGLError("glBindBuffer");

		GLES20.glDrawElements(GLES20.GL_TRIANGLES, slice.indexBuffer.capacity(), GLES20.GL_UNSIGNED_SHORT, 0);
		checkGLError("glDrawElements");

		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
		checkGLError("glBindBuffer");
		GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);
		checkGLError("glBindBuffer");

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
