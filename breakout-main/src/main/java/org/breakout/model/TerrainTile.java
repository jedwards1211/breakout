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
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.function.Consumer;

import org.andork.jogl.JoglDrawContext;
import org.andork.jogl.JoglDrawable;
import org.andork.jogl.JoglResource;
import org.andork.jogl.shader.AttribLocation;
import org.andork.math3d.Vecmath;

import com.jogamp.common.nio.PointerBuffer;
import com.jogamp.nativewindow.util.Dimension;
import com.jogamp.opengl.GL2ES2;
import com.jogamp.opengl.GL3;

public class TerrainTile implements JoglDrawable, JoglResource {
	public TerrainTile(BufferedImage heightmap, Consumer<float[]> transform) {
		int numVertexCols = heightmap.getWidth();
		int numVertexRows = heightmap.getHeight();
		size = new Dimension(numVertexCols, numVertexRows);

		int numCellRows = numVertexRows - 1;
		int numCellCols = numVertexCols - 1;
		
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
		
		setf(corners[0], vertices[0][0]);
		setf(corners[1], vertices[0][numCellCols]);
		setf(corners[2], vertices[numCellRows][0]);
		setf(corners[3], vertices[numCellRows][numCellCols]);
		
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
		float texcoordX = 0;
		float texcoordY = 0;
		for (y = 0, texcoordY = 1f; y < numVertexRows; y++, texcoordY -= texcoordYinc) {
			for (x = 0, texcoordX = 0; x < numVertexCols; x++, texcoordX += texcoordXinc) {
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
	private IntBuffer counts;
	private PointerBuffer indexPointers;
	private int[] vbo = new int[1];
	private int[] ebo = new int[1];

	public AttribLocation positionLocation;
	public AttribLocation normalLocation;
	public AttribLocation texcoordLocation;

	private Map<Integer, PointerBuffer> precomputedIndexPointers;

	private static Map<Dimension, Map<Integer, PointerBuffer>> precomputedIndexPointersCache = new HashMap<>();

	private static  Map<Integer, PointerBuffer> precomputeIndexPointers(Dimension size) {
		Map<Integer, PointerBuffer> result = precomputedIndexPointersCache.get(size);
		if (result == null) {
			result = new HashMap<Integer, PointerBuffer>();
			result.put(0 + (1 << 2), calcOrder(size, 0, 1));
			result.put(1 + (0 << 2), calcOrder(size, 1, 0));
			result.put(0 + (2 << 2), calcOrder(size, 0, 2));
			result.put(2 + (0 << 2), calcOrder(size, 2, 0));
			result.put(3 + (1 << 2), calcOrder(size, 3, 1));
			result.put(1 + (3 << 2), calcOrder(size, 1, 3));
			result.put(3 + (2 << 2), calcOrder(size, 3, 2));
			result.put(2 + (3 << 2), calcOrder(size, 2, 3));
			precomputedIndexPointersCache.put(size, result);
		}
		return result;
	}
	
	public void usePrecomputedIndexPointers() {
		precomputedIndexPointers = precomputeIndexPointers(size);
	}
	
	private static PointerBuffer calcOrder(Dimension size, int corner0, int corner1) {
		int numCellRows = size.getHeight() - 1;
		int numCellCols = size.getWidth() - 1;
		PointerBuffer indexPointers = PointerBuffer.allocateDirect(numCellRows * numCellCols);
		calcOrder(size, corner0, corner1, indexPointers);
		return indexPointers;
	}

	private static void calcOrder(Dimension size, int corner0, int corner1, PointerBuffer indexPointers) {
		int numCellRows = size.getHeight() - 1;
		int numCellCols = size.getWidth() - 1;
	
		int firstRow;
		int lastRow;
		int rowStep;
		int firstCol;
		int lastCol;
		int colStep;
	
		if (corner0 < 2) {
			firstRow = 0;
			rowStep = numCellCols * 6;
			lastRow = rowStep * (numCellRows - 1);
		} else {
			rowStep = -numCellCols * 6;
			firstRow = -rowStep * (numCellRows - 1);
			lastRow = 0;
		}
	
		if ((corner0 & 0x1) == 0) {
			firstCol = 0;
			colStep = 6;
			lastCol = (numCellCols - 1) * 6;
		} else {
			firstCol = (numCellCols - 1) * 6;
			colStep = -6;
			lastCol = 0;
		}
		
		indexPointers.position(0);
	
		if ((corner0 < 2) == (corner1 < 2)) {
			for (int row = firstRow; lastRow > firstRow ? row <= lastRow : row >= lastRow; row += rowStep) {
				for (int col = firstCol; lastCol > firstCol ? col <= lastCol : col >= lastCol; col += colStep) {
					indexPointers.put((row + col) * 4);
				}
			}
		} else {
			for (int col = firstCol; lastCol > firstCol ? col <= lastCol : col >= lastCol; col += colStep) {
				for (int row = firstRow; lastRow > firstRow ? row <= lastRow : row >= lastRow; row += rowStep) {
					indexPointers.put((row + col) * 4);
				}
			}
	
		}
	
		indexPointers.position(0);
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
		IntBuffer indices = b.asIntBuffer();
	
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

		b = ByteBuffer.allocateDirect(numCellRows * numCellCols * 4);
		b.order(ByteOrder.nativeOrder());
		counts = b.asIntBuffer();
		while (counts.hasRemaining()) {
			counts.put(6);
		}
		counts.position(0);

		if (precomputedIndexPointers == null) {
			indexPointers = PointerBuffer.allocateDirect(numCellRows * numCellCols);
		}

		return true;
	}
	
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
		
		if (precomputedIndexPointers != null) {
			indexPointers = precomputedIndexPointers.get(bestCorner + (secondBestCorner << 2));
			return true;
		}
		
		if (bestCorner == lastBestCorner && secondBestCorner == lastSecondBestCorner) {
			return true;
		}

		calcOrder(size, bestCorner, secondBestCorner, indexPointers);
	
		lastBestCorner = bestCorner;
		lastSecondBestCorner = secondBestCorner;
		return true;
	}

	@Override
	public void draw(JoglDrawContext context, GL2ES2 gl, float[] m, float[] n) {
		if (!initialized || !calcOrder(context) || indexPointers == null) {
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
	
		((GL3) gl).glMultiDrawElements(
			GL_TRIANGLES, counts, GL_UNSIGNED_INT, indexPointers,
			(size.getWidth() - 1) * (size.getHeight() - 1));
	
		gl.glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
		gl.glBindBuffer(GL_ARRAY_BUFFER, 0);
	}
}

