package org.andork.jogl.util;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import org.andork.jogl.JoglManagedResource;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2ES2;
import com.jogamp.opengl.GL2GL3;
import com.jogamp.opengl.GLContext;

public class PipelinedRenderer extends JoglManagedResource {
	public static class VertexAttribute implements Cloneable {
		public final int size;
		public final int type;
		public final boolean normalized;

		public VertexAttribute(int size, int type, boolean normalized) {
			super();
			this.size = size;
			this.type = type;
			this.normalized = normalized;
		}

		public VertexAttribute clone() {
			try {
				return (VertexAttribute) super.clone();
			} catch (CloneNotSupportedException e) {
				throw new RuntimeException(e);
			}
		}
	}

	public static class Options implements Cloneable {
		public int mode;
		public boolean usingVBOs;
		public List<VertexAttribute> attributes;
		public int numVerticesInBuffer;

		public Options(boolean usingVBOs, int mode, int numVerticesInBuffer) {
			this.usingVBOs = usingVBOs;
			this.mode = mode;
			this.numVerticesInBuffer = numVerticesInBuffer;
			attributes = new ArrayList<>();
		}

		public Options addAttribute(int size, int type, boolean normalized) {
			attributes.add(new VertexAttribute(size, type, normalized));
			return this;
		}

		public Options clone() {
			Options options;
			try {
				options = (Options) super.clone();
			} catch (CloneNotSupportedException e) {
				throw new RuntimeException(e);
			}
			options.attributes = new ArrayList<>();
			for (VertexAttribute attribute : attributes) {
				options.attributes.add(attribute.clone());
			}
			return options;
		}
	}

	private Options options;
	private ByteBuffer vertices;
	private int vertexVbo;
	private int bytesPerVertex;
	private int[] vertexAttribOffsets;
	private int[] vertexAttribLocations;

	public PipelinedRenderer(Options options) {
		this.options = options.clone();
		vertexAttribLocations = new int[options.attributes.size()];
		vertexAttribOffsets = new int[options.attributes.size()];

		bytesPerVertex = 0;
		for (int i = 0; i < options.attributes.size(); i++) {
			vertexAttribOffsets[i] = bytesPerVertex;
			VertexAttribute attribute = options.attributes.get(i);
			switch (attribute.type) {
			case GL.GL_BYTE:
			case GL.GL_UNSIGNED_BYTE:
				bytesPerVertex += attribute.size;
				break;
			case GL.GL_SHORT:
			case GL.GL_UNSIGNED_SHORT:
				bytesPerVertex += 2 * attribute.size;
				break;
			case GL.GL_UNSIGNED_INT:
			case GL.GL_FLOAT:
				bytesPerVertex += 4 * attribute.size;
				break;
			default:
				throw new IllegalArgumentException("Unknown numeric type: attribute.type");
			}
		}
	}

	public void setVertexAttribLocations(int... locations) {
		System.arraycopy(locations, 0, vertexAttribLocations, 0, vertexAttribLocations.length);
	}

	public void put(byte... values) {
		for (byte value : values) {
			vertices.put(value);
		}
		if (!vertices.hasRemaining()) {
			draw();
		}
	}

	public void put(short... values) {
		for (short value : values) {
			vertices.putShort(value);
		}
		if (!vertices.hasRemaining()) {
			draw();
		}
	}

	public void put(int... values) {
		for (int value : values) {
			vertices.putInt(value);
		}
		if (!vertices.hasRemaining()) {
			draw();
		}
	}

	public void put(float... values) {
		for (float value : values) {
			vertices.putFloat(value);
		}
		if (!vertices.hasRemaining()) {
			draw();
		}
	}


	@Override
	public void doDispose(GL2ES2 gl) {
		if (options.usingVBOs) {
			int[] vbos = { vertexVbo };
			int numBuffers = 1;
			gl.glDeleteBuffers(numBuffers, IntBuffer.wrap(vbos));
		}
	}

	@Override
	public boolean doInit(GL2ES2 gl) {
		vertices = Buffers.newDirectByteBuffer(options.numVerticesInBuffer * bytesPerVertex);

		try {
			final int[] vbos = new int[2];
			int numBuffers = 1;
			gl.glGenBuffers(numBuffers, IntBuffer.wrap(vbos));

			vertexVbo = vbos[0];
			gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vertexVbo);
			gl.glBufferData(GL.GL_ARRAY_BUFFER, vertices.capacity(), null, GL2ES2.GL_STREAM_DRAW);
		} catch (final Exception e) {
			options.usingVBOs = false;
		}

		return true;
	}

	public void draw() {
		int numBytes = vertices.position();
		if (numBytes == 0) {
			return;
		}
		vertices.rewind();

		final GL2GL3 gl = GLContext.getCurrentGL().getGL2GL3();

		if (options.usingVBOs) {
			gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vertexVbo);
			gl.glBufferSubData(GL.GL_ARRAY_BUFFER, 0, numBytes, vertices); // upload only the new stuff
		}
		for (int i = 0; i < options.attributes.size(); i++) {
			gl.glEnableVertexAttribArray(vertexAttribLocations[i]);
			VertexAttribute attribute = options.attributes.get(i);
			if (options.usingVBOs) {
				gl.glVertexAttribPointer(
						vertexAttribLocations[i],
						attribute.size,
						attribute.type,
						attribute.normalized,
						bytesPerVertex,
						vertexAttribOffsets[i]);
			} else {
				vertices.position(vertexAttribOffsets[i]);
				gl.getGL2().glVertexAttribPointer(
						vertexAttribLocations[i],
						attribute.size,
						attribute.type,
						attribute.normalized,
						bytesPerVertex,
						vertices);
			}
		}

		gl.glDrawArrays(options.mode, 0, numBytes / bytesPerVertex);

		for (int i = 0; i < vertexAttribLocations.length; i++) {
			gl.glEnableVertexAttribArray(vertexAttribLocations[i]);
		}

		vertices.rewind();
	}
}
