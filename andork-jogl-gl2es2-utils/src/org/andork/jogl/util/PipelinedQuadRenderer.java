package org.andork.jogl.util;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GL2ES2;
import com.jogamp.opengl.GL2ES3;
import com.jogamp.opengl.GLContext;
import com.jogamp.opengl.fixedfunc.GLPointerFunc;

public class PipelinedQuadRenderer {
	static final int kQuadsPerBuffer = 100;
	static final int kCoordsPerVertVerts = 3;
	static final int kCoordsPerVertTex = 2;
	static final int kVertsPerQuad = 4;
	static final int kTotalBufferSizeVerts = kQuadsPerBuffer * kVertsPerQuad;
	static final int kTotalBufferSizeCoordsVerts = kQuadsPerBuffer * kVertsPerQuad * kCoordsPerVertVerts;
	static final int kTotalBufferSizeCoordsTex = kQuadsPerBuffer * kVertsPerQuad * kCoordsPerVertTex;
	static final int kTotalBufferSizeBytesVerts = kTotalBufferSizeCoordsVerts * 4;
	static final int kTotalBufferSizeBytesTex = kTotalBufferSizeCoordsTex * 4;
	static final int kSizeInBytes_OneVertices_VertexData = kCoordsPerVertVerts * 4;
	static final int kSizeInBytes_OneVertices_TexData = kCoordsPerVertTex * 4;

	int mOutstandingGlyphsVerticesPipeline = 0;
	FloatBuffer mTexCoords;
	FloatBuffer mVertCoords;
	boolean usingVBOs;
	int mVBO_For_ResuableTileVertices;
	int mVBO_For_ResuableTileTexCoords;

	public boolean useVertexArrays = true;

	public PipelinedQuadRenderer() {
		final GL2 gl = GLContext.getCurrentGL().getGL2();
		mVertCoords = Buffers.newDirectFloatBuffer(kTotalBufferSizeCoordsVerts);
		mTexCoords = Buffers.newDirectFloatBuffer(kTotalBufferSizeCoordsTex);

		try {
			final int[] vbos = new int[2];
			gl.glGenBuffers(2, IntBuffer.wrap(vbos));

			mVBO_For_ResuableTileVertices = vbos[0];
			mVBO_For_ResuableTileTexCoords = vbos[1];

			gl.glBindBuffer(GL.GL_ARRAY_BUFFER,
					mVBO_For_ResuableTileVertices);
			gl.glBufferData(GL.GL_ARRAY_BUFFER, kTotalBufferSizeBytesVerts,
					null, GL2ES2.GL_STREAM_DRAW); // stream draw because this is a single quad use pipeline

			gl.glBindBuffer(GL.GL_ARRAY_BUFFER,
					mVBO_For_ResuableTileTexCoords);
			gl.glBufferData(GL.GL_ARRAY_BUFFER, kTotalBufferSizeBytesTex,
					null, GL2ES2.GL_STREAM_DRAW); // stream draw because this is a single quad use pipeline
		} catch (final Exception e) {
			usingVBOs = false;
		}
	}

	public void glTexCoord2f(final float v, final float v1) {
		mTexCoords.put(v);
		mTexCoords.put(v1);
	}

	public void glVertex3f(final float inX, final float inY, final float inZ) {
		mVertCoords.put(inX);
		mVertCoords.put(inY);
		mVertCoords.put(inZ);

		mOutstandingGlyphsVerticesPipeline++;

		if (mOutstandingGlyphsVerticesPipeline >= kTotalBufferSizeVerts) {
			draw();
		}
	}

	public void draw() {
		if (useVertexArrays) {
			drawVertexArrays();
		} else {
			drawIMMEDIATE();
		}
	}

	private void drawVertexArrays() {
		if (mOutstandingGlyphsVerticesPipeline > 0) {
			final GL2 gl = GLContext.getCurrentGL().getGL2();

			mVertCoords.rewind();
			mTexCoords.rewind();

			gl.glEnableClientState(GLPointerFunc.GL_VERTEX_ARRAY);

			if (usingVBOs) {
				gl.glBindBuffer(GL.GL_ARRAY_BUFFER,
						mVBO_For_ResuableTileVertices);
				gl.glBufferSubData(GL.GL_ARRAY_BUFFER, 0,
						mOutstandingGlyphsVerticesPipeline * kSizeInBytes_OneVertices_VertexData,
						mVertCoords); // upload only the new stuff
				gl.glVertexPointer(3, GL.GL_FLOAT, 0, 0);
			} else {
				gl.glVertexPointer(3, GL.GL_FLOAT, 0, mVertCoords);
			}

			gl.glEnableClientState(GLPointerFunc.GL_TEXTURE_COORD_ARRAY);

			if (usingVBOs) {
				gl.glBindBuffer(GL.GL_ARRAY_BUFFER,
						mVBO_For_ResuableTileTexCoords);
				gl.glBufferSubData(GL.GL_ARRAY_BUFFER, 0,
						mOutstandingGlyphsVerticesPipeline * kSizeInBytes_OneVertices_TexData,
						mTexCoords); // upload only the new stuff
				gl.glTexCoordPointer(2, GL.GL_FLOAT, 0, 0);
			} else {
				gl.glTexCoordPointer(2, GL.GL_FLOAT, 0, mTexCoords);
			}

			gl.glDrawArrays(GL2ES3.GL_QUADS, 0,
					mOutstandingGlyphsVerticesPipeline);

			mVertCoords.rewind();
			mTexCoords.rewind();
			mOutstandingGlyphsVerticesPipeline = 0;
		}
	}

	private void drawIMMEDIATE() {
		if (mOutstandingGlyphsVerticesPipeline > 0) {

			final GL2 gl = GLContext.getCurrentGL().getGL2();
			gl.glBegin(GL2ES3.GL_QUADS);

			try {
				final int numberOfQuads = mOutstandingGlyphsVerticesPipeline / 4;
				mVertCoords.rewind();
				mTexCoords.rewind();

				for (int i = 0; i < numberOfQuads; i++) {
					gl.glTexCoord2f(mTexCoords.get(), mTexCoords.get());
					gl.glVertex3f(mVertCoords.get(), mVertCoords.get(),
							mVertCoords.get());

					gl.glTexCoord2f(mTexCoords.get(), mTexCoords.get());
					gl.glVertex3f(mVertCoords.get(), mVertCoords.get(),
							mVertCoords.get());

					gl.glTexCoord2f(mTexCoords.get(), mTexCoords.get());
					gl.glVertex3f(mVertCoords.get(), mVertCoords.get(),
							mVertCoords.get());

					gl.glTexCoord2f(mTexCoords.get(), mTexCoords.get());
					gl.glVertex3f(mVertCoords.get(), mVertCoords.get(),
							mVertCoords.get());
				}
			} catch (final Exception e) {
				e.printStackTrace();
			} finally {
				gl.glEnd();
				mVertCoords.rewind();
				mTexCoords.rewind();
				mOutstandingGlyphsVerticesPipeline = 0;
			}
		}
	}
}
