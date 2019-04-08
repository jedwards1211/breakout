package org.breakout.model;

import static com.jogamp.opengl.GL.GL_ARRAY_BUFFER;
import static com.jogamp.opengl.GL.GL_ELEMENT_ARRAY_BUFFER;
import static com.jogamp.opengl.GL.GL_FLOAT;
import static com.jogamp.opengl.GL.GL_STATIC_DRAW;
import static com.jogamp.opengl.GL.GL_TRIANGLES;
import static com.jogamp.opengl.GL.GL_UNSIGNED_INT;
import static org.andork.math3d.Vecmath.setf;
import static org.andork.math3d.Vecmath.subDot3;

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.function.Consumer;

import org.andork.jogl.JoglDrawContext;
import org.andork.jogl.JoglDrawable;
import org.andork.jogl.JoglResource;
import org.andork.jogl.shader.AttribLocation;
import org.andork.math3d.Vecmath;
import org.andork.nativewindow.util.PixelRectangles;

import com.jogamp.nativewindow.util.Dimension;
import com.jogamp.nativewindow.util.PixelRectangle;
import com.jogamp.opengl.GL2ES2;

public class TerrainTile implements JoglDrawable, JoglResource {
	public TerrainTile(float[][][] vertices) {
		int numVertexRows = vertices.length;
		int numVertexCols = vertices[0].length;
		size = new Dimension(numVertexCols, numVertexRows);

		int numCellRows = numVertexRows - 1;
		int numCellCols = numVertexCols - 1;
		
		setf(corners[0], vertices[0][0]);
		setf(corners[1], vertices[0][numCellCols]);
		setf(corners[2], vertices[numCellRows][0]);
		setf(corners[3], vertices[numCellRows][numCellCols]);
		
		int y, x;
		float[] xz = new float[3];
		float[] yz = new float[3];
		float[][][] normals = new float[numVertexRows][numVertexCols][3];
		for (y = 0; y < numVertexRows; y++) {
			for (x = 0; x < numVertexCols; x++) {
				float[] x1 = x < numVertexRows - 1 ? vertices[y][x + 1] : vertices[y][x];
				float[] x0 = x > 0 ? vertices[y][x - 1] : vertices[y][x];
				float[] y1 = y < numVertexCols - 1 ? vertices[y + 1][x] : vertices[y][x];
				float[] y0 = y > 0 ? vertices[y - 1][x] : vertices[y][x];
				Vecmath.sub3(x1, x0, xz);
				Vecmath.sub3(y1, y0, yz);
				Vecmath.cross(yz, xz, normals[y][x]);
				Vecmath.normalize3(normals[y][x]);
			}
		}
		

		ByteBuffer b;
		b = ByteBuffer.allocateDirect(numVertexRows * numVertexCols * BYTES_PER_VERTEX);
		b.order(ByteOrder.nativeOrder());
		
		double texcoordXinc = 1.0 / numVertexRows;
		double texcoordYinc = 1.0 / numVertexCols;
		float texcoordY, texcoordX;
		for (y = 0, texcoordY = 1 - 0.5f / numVertexCols; y < numVertexRows; y++, texcoordY -= texcoordYinc) {
			for (x = 0, texcoordX = 0.5f / numVertexRows; x < numVertexCols; x++, texcoordX += texcoordXinc) {
				float[] vertex = vertices[y][x];
				float[] normal = normals[y][x];
				b.putFloat(vertex[0]);
				b.putFloat(vertex[1]);
				b.putFloat(vertex[2]);
				b.putFloat(normal[0]);
				b.putFloat(normal[1]);
				b.putFloat(normal[2]);
				b.putFloat(texcoordX);
				b.putFloat(texcoordY);
			}
		}

		b.position(0);
		vertexData = b;
		
		fold = new boolean[numCellRows][numCellCols];

		for (y = 0; y < numCellRows; y++) {
			for (x = 0; x < numCellCols; x++) {
				fold[y][x] = Vecmath.dot3(normals[y][x], normals[y + 1][x + 1]) >=
					Vecmath.dot3(normals[y + 1][x], normals[y][x + 1]);
			}
		}		
	}
	
	public static float[][][] createVertices(BufferedImage heightmap, Consumer<float[]> transform) {
		int numVertexCols = heightmap.getWidth();
		int numVertexRows = heightmap.getHeight();
		
		float[][][] vertices = new float[numVertexRows][numVertexCols][3];
		
		int x, y;
		for (y = 0; y < numVertexRows; y++) {
			float[][] row = vertices[y];
			for (x = 0; x < numVertexCols; x++) {
				float[] vertex = row[x];
				vertex[0] = x;
				vertex[1] = y;
				vertex[2] = (heightmap.getRGB(x, y) & 0xffffff) * 0.1f - 10000f;
			}
		}
		
		if (transform != null) {
			for (y = 0; y < numVertexCols; y++) {
				float[][] row = vertices[y];
				for (x = 0; x < numVertexRows; x++) {
					float[] vertex = row[x];
					transform.accept(vertex);
				}
			}
		}	

		return vertices;
	}

	public static float[][][] createVertices(PixelRectangle heightmap, Consumer<float[]> transform) {
		int numVertexCols = heightmap.getSize().getWidth();
		int numVertexRows = heightmap.getSize().getHeight();
		
		float[][][] vertices = new float[numVertexRows][numVertexCols][3];
		
		int x, y;
		for (y = 0; y < numVertexRows; y++) {
			float[][] row = vertices[y];
			for (x = 0; x < numVertexCols; x++) {
				float[] vertex = row[x];
				vertex[0] = x;
				vertex[1] = y;
				vertex[2] = (PixelRectangles.getARGB(heightmap, x, y) & 0xffffff) * 0.1f - 10000f;
			}
		}
		
		if (transform != null) {
			for (y = 0; y < numVertexCols; y++) {
				float[][] row = vertices[y];
				for (x = 0; x < numVertexRows; x++) {
					float[] vertex = row[x];
					transform.accept(vertex);
				}
			}
		}	

		return vertices;
	}

	public TerrainTile(BufferedImage heightmap, Consumer<float[]> transform) {
		this(createVertices(heightmap, transform));
	}

	public TerrainTile(PixelRectangle heightmap, Consumer<float[]> transform) {
		this(createVertices(heightmap, transform));
	}
	
	private Dimension size;
	
	/**
	 * C      C
	 * o	  o
	 * l      l
	 * 
	 * 0      n
	 * 
	 * 0------1  Row 0
	 * |      |
	 * |      |
	 * |      |
	 * 2------3  Row m
	 */
	private float[][] corners = new float[4][3];
	private int lastBestCorner = -1;
	private int lastSecondBestCorner = -1;
	/**
	 * Which diagonal to use when breaking up each cell into triangles.
	 * fold[x][y] is true -- use diagonal (x, y) - (x+1, y+1)
	 * fold[x][y] is false -- use diagonal (x, y+1) - (x+1, y)
	 */
	private boolean[][] fold;

	private static int BYTES_PER_VERTEX = 8 * 4;

	private boolean initialized;
	private ByteBuffer vertexData;
	private IntBuffer indices;
	private int[] vbo = new int[1];
	private int[] ebo = new int[1];

	public AttribLocation positionLocation;
	public AttribLocation normalLocation;
	public AttribLocation texcoordLocation;

	/**
	 * @return {@code true} iff any of the terrain is in front of the camera.
	 */
	private boolean calcOrder(JoglDrawContext context) {
		float bestDist = -Float.MAX_VALUE;
		float secondBestDist = -Float.MAX_VALUE;
		int bestCorner = -1;
		int secondBestCorner = -1;
	
		float[] vi = context.inverseViewMatrix();
	
		for (int i = 0; i < corners.length; i++) {
			float dist = subDot3(vi, 12, corners[i], 0, vi, 8);
			if (dist > bestDist) {
				secondBestDist = bestDist;
				secondBestCorner = bestCorner;
				bestDist = dist;
				bestCorner = i;
			} else if (dist > secondBestDist) {
				secondBestDist = dist;
				secondBestCorner = i;
			}
		}
		
		if (bestDist < 0f) {
			return false;
		}
		
		if (bestCorner == lastBestCorner && secondBestCorner == lastSecondBestCorner) {
			return false;
		}
	
		calcOrder(size, bestCorner, secondBestCorner, indices);
	
		lastBestCorner = bestCorner;
		lastSecondBestCorner = secondBestCorner;
		return true;
	}

	private void calcOrder(Dimension size, int corner0, int corner1, IntBuffer indices) {
		int numVertexRows = size.getHeight();
		int numVertexCols = size.getWidth();
		int numCellRows = numVertexRows - 1;
		int numCellCols = numVertexCols - 1;
	
		int firstRow;
		int lastRow;
		int rowStep;
		int firstCol;
		int lastCol;
		int colStep;
	
		if (corner0 < 2) {
			firstRow = 0;
			lastRow = numCellRows;
			rowStep = 1;
		} else {
			firstRow = numCellRows - 1;
			lastRow = -1;
			rowStep = -1;
		}
	
		if ((corner0 & 0x1) == 0) {
			firstCol = 0;
			lastCol = numCellCols;
			colStep = 1;
		} else {
			firstCol = numCellCols - 1;
			lastCol = -1;
			colStep = -1;
		}
		
		indices.position(0);
		
		if ((corner0 < 2) == (corner1 < 2)) {
			for (int row = firstRow; row != lastRow; row += rowStep) {
				int rowStart = row * numVertexCols;
				int nextRowStart = rowStart + numVertexCols;
				for (int col = firstCol; col != lastCol; col += colStep) {
					if (fold[row][col]) {
						indices.put(rowStart + col);
						indices.put(nextRowStart + col + 1);
						indices.put(nextRowStart + col);
						indices.put(nextRowStart + col + 1);
						indices.put(rowStart + col);
						indices.put(rowStart + col + 1);				
					} else {
						indices.put(rowStart + col + 1);
						indices.put(nextRowStart + col);
						indices.put(rowStart + col);
						indices.put(nextRowStart + col);
						indices.put(rowStart + col + 1);
						indices.put(nextRowStart + col + 1);
					}
				}
			}
		} else {
			for (int col = firstCol; col != lastCol; col += colStep) {
				for (int row = firstRow; row != lastRow; row += rowStep) {
					int rowStart = row * numVertexCols;
					int nextRowStart = rowStart + numVertexCols;
					if (fold[row][col]) {
						indices.put(rowStart + col);
						indices.put(nextRowStart + col + 1);
						indices.put(nextRowStart + col);
						indices.put(nextRowStart + col + 1);
						indices.put(rowStart + col);
						indices.put(rowStart + col + 1);				
					} else {
						indices.put(rowStart + col + 1);
						indices.put(nextRowStart + col);
						indices.put(rowStart + col);
						indices.put(nextRowStart + col);
						indices.put(rowStart + col + 1);
						indices.put(nextRowStart + col + 1);
					}
				}
			}	
		}

		indices.position(0);
	}

	@Override
	public void dispose(GL2ES2 gl) {
		if (!initialized) {
			return;
		}
		initialized = false;

		gl.glDeleteBuffers(1, vbo, 0);
	}

	@Override
	public boolean init(GL2ES2 gl) {
		if (initialized) {
			return true;
		}
		initialized = true;

		gl.glGenBuffers(1, vbo, 0);
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[0]);
		gl.glBufferData(GL_ARRAY_BUFFER, vertexData.capacity(), vertexData, GL_STATIC_DRAW);
		gl.glBindBuffer(GL_ARRAY_BUFFER, 0);
	
		int numVertexCols = size.getWidth();
		int numVertexRows = size.getHeight();
		int numCellCols = numVertexCols - 1;
		int numCellRows = numVertexRows - 1;

		ByteBuffer b = ByteBuffer.allocateDirect(numCellRows * numCellCols * 24);
		b.order(ByteOrder.nativeOrder());
		indices = b.asIntBuffer();
	
		for (int row = 0; row < numCellRows; row++) {
			int rowStart = row * numVertexCols;
			int nextRowStart = rowStart + numVertexCols;
			for (int col = 0; col < numCellCols; col++) {
				if (fold[row][col]) {
					indices.put(rowStart + col);
					indices.put(nextRowStart + col + 1);
					indices.put(nextRowStart + col);
					indices.put(nextRowStart + col + 1);
					indices.put(rowStart + col);
					indices.put(rowStart + col + 1);				
				} else {
					indices.put(rowStart + col + 1);
					indices.put(nextRowStart + col);
					indices.put(rowStart + col);
					indices.put(nextRowStart + col);
					indices.put(rowStart + col + 1);
					indices.put(nextRowStart + col + 1);
				}
			}
		}

		indices.position(0);

		gl.glGenBuffers(1, ebo, 0);
		gl.glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo[0]);
		gl.glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices.capacity() * 4, indices, GL_STATIC_DRAW);
		gl.glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);

		return true;
	}
	
	@Override
	public void draw(JoglDrawContext context, GL2ES2 gl, float[] m, float[] n) {
		if (!initialized) {
			return;
		}
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[0]);
		if (positionLocation == null) {
			throw new RuntimeException("positionLocation must not be null");
		}
		gl.glVertexAttribPointer(positionLocation.location(), 3, GL_FLOAT, false, BYTES_PER_VERTEX, 0);
		if (normalLocation != null) {
			gl.glVertexAttribPointer(normalLocation.location(), 3, GL_FLOAT, false, BYTES_PER_VERTEX, 12);
		}
		if (texcoordLocation != null) {
			gl.glVertexAttribPointer(texcoordLocation.location(), 2, GL_FLOAT, false, BYTES_PER_VERTEX, 24);
		}

		gl.glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo[0]);
		if (calcOrder(context)) {
			gl.glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices.capacity() * 4, indices, GL_STATIC_DRAW);
		}

		gl.glDrawElements(
			GL_TRIANGLES, (size.getWidth() - 1) * (size.getHeight() - 1) * 6, GL_UNSIGNED_INT, 0);
	
		gl.glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
		gl.glBindBuffer(GL_ARRAY_BUFFER, 0);
	}
}

