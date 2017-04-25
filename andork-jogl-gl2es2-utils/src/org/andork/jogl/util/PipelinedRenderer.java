package org.andork.jogl.util;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.andork.jogl.JoglResource;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GL2ES2;
import com.jogamp.opengl.GLContext;
import com.jogamp.opengl.fixedfunc.GLPointerFunc;

public class PipelinedRenderer implements JoglResource {
	public static class Options {
		public int mode = GL.GL_POINTS;
		public int coordsPerVertex = 3;
		public int coordsPerTexcoord = 0;
		public int numVerticesInBuffer = 100;
	}

	Options options;
	int numPendingVertices = 0;
	FloatBuffer texcoords;
	FloatBuffer vertices;
	boolean usingVBOs;
	int vertexVbo;
	int texcoordVbo;

	boolean initialized = false;

	public PipelinedRenderer(Options options) {
		this.options = options;

	}

	public void glTexCoord2f(final float v, final float v1) {
		texcoords.put(v);
		texcoords.put(v1);
	}

	public void glVertex3f(final float inX, final float inY, final float inZ) {
		vertices.put(inX);
		vertices.put(inY);
		vertices.put(inZ);

		if (++numPendingVertices >= options.numVerticesInBuffer && initialized) {
			draw();
		}
	}

	@Override
	public void dispose(GL2ES2 gl) {
		if (!initialized) {
			return;
		}
		initialized = false;

		if (usingVBOs) {
			int[] vbos = { vertexVbo, texcoordVbo };
			int numBuffers = 1;
			if (options.coordsPerTexcoord > 0) {
				numBuffers++;
			}
			gl.glDeleteBuffers(numBuffers, IntBuffer.wrap(vbos));
		}
	}

	@Override
	public boolean init(GL2ES2 gl) {
		if (initialized) {
			return true;
		}
		vertices = Buffers.newDirectFloatBuffer(options.numVerticesInBuffer * options.coordsPerVertex * 4);
		if (options.coordsPerTexcoord > 0) {
			texcoords = Buffers.newDirectFloatBuffer(options.numVerticesInBuffer * options.coordsPerTexcoord * 4);
		}

		try {
			final int[] vbos = new int[2];
			int numBuffers = 1;
			if (options.coordsPerTexcoord > 0) {
				numBuffers++;
			}
			gl.glGenBuffers(numBuffers, IntBuffer.wrap(vbos));

			vertexVbo = vbos[0];
			gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vertexVbo);
			gl.glBufferData(GL.GL_ARRAY_BUFFER, vertices.capacity(), null, GL2ES2.GL_STREAM_DRAW);

			if (options.coordsPerTexcoord > 0) {
				texcoordVbo = vbos[1];
				gl.glBindBuffer(GL.GL_ARRAY_BUFFER, texcoordVbo);
				gl.glBufferData(GL.GL_ARRAY_BUFFER, texcoords.capacity(), null, GL2ES2.GL_STREAM_DRAW);
			}
		} catch (final Exception e) {
			usingVBOs = false;
		}

		initialized = true;
		return true;
	}

	public void draw() {
		if (numPendingVertices == 0) {
			return;
		}

		final GL2 gl = GLContext.getCurrentGL().getGL2();

		vertices.rewind();
		texcoords.rewind();

		gl.glEnableClientState(GLPointerFunc.GL_VERTEX_ARRAY);

		if (usingVBOs) {
			gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vertexVbo);
			gl.glBufferSubData(GL.GL_ARRAY_BUFFER, 0,
					numPendingVertices * options.coordsPerVertex * 4,
					vertices); // upload only the new stuff
			gl.glVertexPointer(3, GL.GL_FLOAT, 0, 0);
		} else {
			gl.glVertexPointer(3, GL.GL_FLOAT, 0, vertices);
		}

		gl.glEnableClientState(GLPointerFunc.GL_TEXTURE_COORD_ARRAY);

		if (options.coordsPerTexcoord > 0) {
			if (usingVBOs) {
				gl.glBindBuffer(GL.GL_ARRAY_BUFFER, texcoordVbo);
				gl.glBufferSubData(GL.GL_ARRAY_BUFFER, 0,
						numPendingVertices * options.coordsPerTexcoord * 4,
						texcoords); // upload only the new stuff
				gl.glTexCoordPointer(2, GL.GL_FLOAT, 0, 0);
			} else {
				gl.glTexCoordPointer(2, GL.GL_FLOAT, 0, texcoords);
			}
		}

		gl.glDrawArrays(options.mode, 0, numPendingVertices);

		vertices.rewind();
		texcoords.rewind();
		numPendingVertices = 0;
	}
}
