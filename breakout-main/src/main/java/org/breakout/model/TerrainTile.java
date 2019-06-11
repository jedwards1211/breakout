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
import java.util.Iterator;
import java.util.Objects;
import java.util.function.Consumer;

import org.andork.jogl.JoglDrawContext;
import org.andork.jogl.JoglDrawable;
import org.andork.jogl.JoglResource;
import org.andork.jogl.shader.AttribLocation;
import org.andork.math3d.Vecmath;
import org.andork.nativewindow.util.PixelRectangles;
import org.andork.spatial.BoundingSpheres;
import org.andork.spatial.Rectmath;

import com.jogamp.nativewindow.util.Dimension;
import com.jogamp.nativewindow.util.PixelRectangle;
import com.jogamp.opengl.GL2ES2;

public class TerrainTile implements JoglDrawable, JoglResource {
	private static class VertexIterator implements Iterator<float[]> {
		float[][][] vertices;
		int row = 0;
		int col = 0;
		
		public VertexIterator(float[][][] vertices) {
			super();
			this.vertices = vertices;
		}

		@Override
		public boolean hasNext() {
			return row < vertices.length - 1
				|| (row == vertices.length - 1 && col < vertices[row].length);
		}

		@Override
		public float[] next() {
			float[] result = vertices[row][col];
			col++;
			if (col == vertices[row].length) {
				row++;
				col = 0;
			}
			return result;
		}
	}
	
	private static class VertexIterable implements Iterable<float[]> {
		float[][][] vertices;
		
		public VertexIterable(float[][][] vertices) {
			this.vertices = vertices;
		}

		@Override
		public Iterator<float[]> iterator() {
			return new VertexIterator(vertices);
		}
	}

	public TerrainTile(float[][][] vertices) {
		int numVertexRows = vertices.length;
		int numVertexCols = vertices[0].length;
		size = new Dimension(numVertexCols, numVertexRows);
		
		BoundingSpheres.ritterBoundingSphere(new VertexIterable(vertices), boundingSphere);
		for (float[] vertex : new VertexIterable(vertices)) {
			Rectmath.punion3(mbr, vertex, mbr);
		}

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
	
	private float[] boundingSphere = new float[4];
	private float[] mbr = Rectmath.voidRectf(3);
	
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
	private PaintOrder paintOrder = null;
	private PaintOrder nextPaintOrder = new PaintOrder();
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
	
	public float[] boundingSphere() {
		return boundingSphere;
	}
	
	public float[] mbr() {
		return mbr;
	}

	/**
	 * @return {@code true} iff any of the terrain is in front of the camera.
	 */
	private boolean calcOrder(JoglDrawContext context) {
		if (nextPaintOrder.compute(context, corners).equals(paintOrder)) return false;
		if (paintOrder == null) paintOrder = new PaintOrder();

		PaintOrder swap = paintOrder;
		paintOrder = nextPaintOrder;
		nextPaintOrder = swap;
			
		int numVertexRows = size.getHeight();
		int numVertexCols = size.getWidth();
		int numCellRows = numVertexRows - 1;
		int numCellCols = numVertexCols - 1;
	
		
		paintOrder.iterate(numCellRows, numCellCols, new PaintOrder.Iteratee() {
			int rowStart;
			int nextRowStart;
			boolean[] colFold;

			@Override
			public void row(int row) {
				rowStart = row * numVertexCols;
				nextRowStart = rowStart + numVertexCols;
				colFold = fold[row];
			}

			@Override
			public void cell(int row, int col) {
				if (colFold[col]) {
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
		});

		indices.position(0);
		
		return true;
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

		gl.glGenBuffers(1, ebo, 0);

		return true;
	}
	
	@Override
	public void draw(JoglDrawContext context, GL2ES2 gl, float[] m, float[] n) {
		if (!initialized) {
			return;
		}
		if (context.frustum().isSphereOutside(boundingSphere, boundingSphere[3])) {
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
	
	/**
	 * Computes back-to-front paint order for cells of a grid.
	 */
	public static class PaintOrder {
		/**
		 * If {@code true}, the outer loop should iterate over rows;
		 * if {@code false} the outer loop should iterate over columns.
		 */
		public boolean rowsFirst;
		/**
		 * If {@code true}, should iterate from the last row to the first row;
		 * otherwise, should iterate from first to last.
		 */
		public boolean rowsDescending;
		/**
		 * If {@code true}, should iterate from the last col to the first col;
		 * otherwise, should iterate from first to last.
		 */
		public boolean colsDescending;

		@Override
		public int hashCode() {
			return Objects.hash(colsDescending, rowsDescending, rowsFirst);
		}
	
		/**
		 * Computes the paint order given the corners of the grid, where:
		 * 
		 * <ul>
		 * <li>{@code corners[0]} is row 0, col 0
		 * <li>{@code corners[1]} is row 0, col n
		 * <li>{@code corners[2]} is row m, col 0
		 * <li>{@code corners[3]} is row m, col n
		 * </ul>
		 * 
		 * <pre>
		 * C      C
		 * o      o
		 * l      l
		 * 
		 * 0      n
		 * 
		 * 0------1  Row 0
		 * |      |
		 * |      |
		 * |      |
		 * 2------3  Row m
		 * </pre>
		 */
		public PaintOrder compute(JoglDrawContext context, float[][] corners) {
			float farthestDist = -Float.MAX_VALUE;
			float secondFarthestDist = -Float.MAX_VALUE;
			int farthestCorner = -1;
			int secondFarthestCorner = -1;
		
			float[] vi = context.inverseViewMatrix();
		
			for (int i = 0; i < corners.length; i++) {
				float dist = subDot3(vi, 12, corners[i], 0, vi, 8);
				if (dist > farthestDist) {
					secondFarthestDist = farthestDist;
					secondFarthestCorner = farthestCorner;
					farthestDist = dist;
					farthestCorner = i;
				} else if (dist > secondFarthestDist) {
					secondFarthestDist = dist;
					secondFarthestCorner = i;
				}

			}
			
			rowsFirst = (farthestCorner < 2) == (secondFarthestCorner < 2);
			rowsDescending = farthestCorner >= 2;
			colsDescending = (farthestCorner & 0x1) == 1;
			
			return this;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			PaintOrder other = (PaintOrder) obj;
			return colsDescending == other.colsDescending && rowsDescending == other.rowsDescending
					&& rowsFirst == other.rowsFirst;
		}
		
		public String toString() {
			return "PaintOrder[first=" + (rowsFirst ? "rows" : "cols") +
				" rows=" + (rowsDescending ? "descending" : "ascending") +
				" cols=" + (colsDescending ? "descending" : "ascending") + "]";
		}
	
		public static abstract class Iteratee {
			public void row(int row) {}
			public void col(int col) {}
			public void cell(int row, int col) {}
		}
		
		/**
		 * Iterates over the cells of the grid in the current computed order.
		 * @param numRows the number of rows
		 * @param numCols the number of columns
		 * @param iteratee the visitor to call on each row/column/cell
		 */
		public void iterate(int numRows, int numCols, Iteratee iteratee) {
			int firstRow;
			int lastRow;
			int rowStep;
			int firstCol;
			int lastCol;
			int colStep;

			if (rowsDescending) {
				firstRow = numRows - 1;
				lastRow = -1;
				rowStep = -1;
			} else {
				firstRow = 0;
				lastRow = numRows;
				rowStep = 1;
			}
		
			if (colsDescending) {
				firstCol = numCols - 1;
				lastCol = -1;
				colStep = -1;
			} else {
				firstCol = 0;
				lastCol = numCols;
				colStep = 1;
			}
			
			if (rowsFirst) {
				for (int row = firstRow; row != lastRow; row += rowStep) {
					iteratee.row(row);
					for (int col = firstCol; col != lastCol; col += colStep) {
						iteratee.col(col);
						iteratee.cell(row, col);
					}
				}
			} else {
				for (int col = firstCol; col != lastCol; col += colStep) {
					iteratee.col(col);
					for (int row = firstRow; row != lastRow; row += rowStep) {
						iteratee.row(row);
						iteratee.cell(row, col);
					}
				}
			}
		}
	}
}

