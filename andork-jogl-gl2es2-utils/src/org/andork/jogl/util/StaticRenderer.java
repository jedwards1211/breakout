package org.andork.jogl.util;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import org.andork.jogl.JoglManagedResource;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2ES2;
import com.jogamp.opengl.GL2GL3;
import com.jogamp.opengl.GLContext;

public class StaticRenderer extends JoglManagedResource {
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

		public Options(boolean usingVBOs, int mode) {
			this.usingVBOs = usingVBOs;
			this.mode = mode;
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

	private final Options options;
	private final ByteBuffer vertices;
	private final int numVertices;
	private int vertexVbo;
	private int bytesPerVertex;
	private int[] vertexAttribOffsets;
	private int[] vertexAttribLocations;

	public StaticRenderer(ByteBuffer vertices, Options options) {
		this.vertices = vertices;
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
		
		numVertices = vertices.capacity() / bytesPerVertex;
	}

	public void setVertexAttribLocations(int... locations) {
		System.arraycopy(locations, 0, vertexAttribLocations, 0,
				Math.min(locations.length, vertexAttribLocations.length));
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
		try {
			final int[] vbos = new int[2];
			int numBuffers = 1;
			gl.glGenBuffers(numBuffers, IntBuffer.wrap(vbos));

			vertexVbo = vbos[0];
			gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vertexVbo);
			gl.glBufferData(GL.GL_ARRAY_BUFFER, vertices.capacity(), vertices, GL2ES2.GL_STATIC_DRAW);
		} catch (final Exception e) {
			options.usingVBOs = false;
		}

		return true;
	}

	public void draw() {
		final GL2GL3 gl = GLContext.getCurrentGL().getGL2GL3();

		if (options.usingVBOs) {
			gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vertexVbo);
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

		gl.glDrawArrays(options.mode, 0, numVertices);

		for (int i = 0; i < vertexAttribLocations.length; i++) {
			gl.glDisableVertexAttribArray(vertexAttribLocations[i]);
		}
	}
}
