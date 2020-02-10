package org.andork.math3d;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class NormalGenerator3f {
	public static class Vertex implements Cloneable {
		public int index;
		public float x;
		public float y;
		public float z;
		public float normalX;
		public float normalY;
		public float normalZ;

		@Override
		public Vertex clone() {
			Vertex result = new Vertex();
			result.index = index;
			result.x = x;
			result.y = y;
			result.z = z;
			result.normalX = normalX;
			result.normalY = normalY;
			result.normalZ = normalZ;
			return result;
		}

		/**
		 * Normalizes normalX/Y/Z to a unit vector (unless all are zero).
		 */
		public void normalize() {
			double length = Math.sqrt(normalX * normalX + normalY * normalY + normalZ * normalZ);
			if (length != 0) {
				normalX /= length;
				normalY /= length;
				normalZ /= length;
			}
		}

		public String positionString() {
			return "(" + x + ", " + y + ", " + z + ")";
		}

		@Override
		public String toString() {
			return "Vertex [index="
				+ index
				+ ", x="
				+ x
				+ ", y="
				+ y
				+ ", z="
				+ z
				+ ", normalX="
				+ normalX
				+ ", normalY="
				+ normalY
				+ ", normalZ="
				+ normalZ
				+ "]";
		}
	}

	public static class Edge {
		public Triangle triangle;
		public Vertex vertex;
		public Edge prevEdge;
		public Edge nextEdge;
		public Edge oppositeEdge;
		public boolean folded;

		public void fold() {
			folded = true;
			if (oppositeEdge != null)
				oppositeEdge.folded = true;
		}

		public Edge nextAtVertex() {
			return oppositeEdge != null ? oppositeEdge.nextEdge : null;
		}

		public Edge prevAtVertex() {
			return prevEdge.oppositeEdge;
		}
	}

	public static class Triangle {
		public final Edge[] edges = new Edge[3];
		public float normalX;
		public float normalY;
		public float normalZ;

		public double dot(Triangle other) {
			return normalX * other.normalX + normalY * other.normalY + normalZ * other.normalZ;
		}

		public double[] getCentroid(double[] centroid) {
			centroid[0] = 0;
			centroid[1] = 0;
			centroid[2] = 0;
			for (NormalGenerator3f.Edge edge : edges) {
				centroid[0] += edge.vertex.x;
				centroid[1] += edge.vertex.y;
				centroid[2] += edge.vertex.z;
			}
			centroid[0] /= 3;
			centroid[1] /= 3;
			centroid[2] /= 3;
			return centroid;

		}

		@Override
		public String toString() {
			return "Triangle ["
				+ "\n  "
				+ edges[0].vertex.toString()
				+ "\n  "
				+ edges[1].vertex.toString()
				+ "\n  "
				+ edges[2].vertex.toString()
				+ "\n]";
		}

		public void computeNormal() {
			Vertex a = edges[0].vertex;
			Vertex b = edges[1].vertex;
			Vertex c = edges[2].vertex;

			float b0 = b.x - a.x;
			float b1 = b.y - a.y;
			float b2 = b.z - a.z;
			float c0 = c.x - a.x;
			float c1 = c.y - a.y;
			float c2 = c.z - a.z;

			normalX = b1 * c2 - b2 * c1;
			normalY = b2 * c0 - b0 * c2;
			normalZ = b0 * c1 - b1 * c0;

			double length = Math.sqrt(normalX * normalX + normalY * normalY + normalZ * normalZ);
			if (length != 0) {
				normalX /= length;
				normalY /= length;
				normalZ /= length;
			}
		}
	}

	public static class MeshBuilder {
		private static class VertexKey {
			public float x;
			public float y;
			public float z;

			public VertexKey(float x, float y, float z) {
				super();
				this.x = x;
				this.y = y;
				this.z = z;
			}

			@Override
			public int hashCode() {
				return Objects.hash(x, y, z);
			}

			@Override
			public boolean equals(Object obj) {
				if (this == obj)
					return true;
				if (obj == null)
					return false;
				if (getClass() != obj.getClass())
					return false;
				VertexKey other = (VertexKey) obj;
				return Float.floatToIntBits(x) == Float.floatToIntBits(other.x)
					&& Float.floatToIntBits(y) == Float.floatToIntBits(other.y)
					&& Float.floatToIntBits(z) == Float.floatToIntBits(other.z);
			}

			@Override
			public String toString() {
				return "VertexKey [x=" + x + ", y=" + y + ", z=" + z + "]";
			}
		}

		private static class EdgeKey {
			public Vertex prev;
			public Vertex next;

			public EdgeKey(Vertex prev, Vertex next) {
				super();
				this.prev = prev;
				this.next = next;
			}

			@Override
			public int hashCode() {
				return Objects.hash(System.identityHashCode(next), System.identityHashCode(prev));
			}

			@Override
			public boolean equals(Object obj) {
				if (this == obj)
					return true;
				if (obj == null)
					return false;
				if (getClass() != obj.getClass())
					return false;
				EdgeKey other = (EdgeKey) obj;
				return next == other.next && prev == other.prev;
			}
		}

		private final Map<VertexKey, Vertex> vertices = new HashMap<>();
		private final Map<EdgeKey, Edge> edges = new HashMap<>();
		private final List<Triangle> triangles = new ArrayList<>();

		private Vertex getVertex(float x, float y, float z) {
			VertexKey key = new VertexKey(x, y, z);
			Vertex v = vertices.get(key);
			if (v == null) {
				v = new Vertex();
				v.x = x;
				v.y = y;
				v.z = z;
				vertices.put(key, v);
			}
			return v;
		}

		public Triangle[] getTriangles() {
			return triangles.toArray(new Triangle[triangles.size()]);
		}

		/**
		 * Adds a triangle to the mesh.
		 * 
		 * @param x0
		 * @param y0
		 * @param z0
		 * @param x1
		 * @param y1
		 * @param z1
		 * @param x2
		 * @param y2
		 * @param z2
		 * @throws Exception if any edges are already present in the mesh
		 * @return the {@code Triangle} that was added
		 */
		public Triangle add(float x0, float y0, float z0, float x1, float y1, float z1, float x2, float y2, float z2) {
			Vertex v0 = getVertex(x0, y0, z0);
			Vertex v1 = getVertex(x1, y1, z1);
			Vertex v2 = getVertex(x2, y2, z2);

			EdgeKey h01 = new EdgeKey(v0, v1);
			EdgeKey h12 = new EdgeKey(v1, v2);
			EdgeKey h20 = new EdgeKey(v2, v0);
			if (edges.containsKey(h01)) {
				throw new RuntimeException(
					"there is already an edge from " + v0.positionString() + " to " + v1.positionString());
			}
			if (edges.containsKey(h12)) {
				throw new RuntimeException(
					"there is already an edge from " + v1.positionString() + " to " + v2.positionString());
			}
			if (edges.containsKey(h20)) {
				throw new RuntimeException(
					"there is already an edge from " + v2.positionString() + " to " + v0.positionString());
			}

			Edge e0 = new Edge();
			Edge e1 = new Edge();
			Edge e2 = new Edge();

			EdgeKey h02 = new EdgeKey(v0, v2);
			EdgeKey h21 = new EdgeKey(v2, v1);
			EdgeKey h10 = new EdgeKey(v1, v0);
			Edge eo0 = edges.get(h10);
			Edge eo1 = edges.get(h21);
			Edge eo2 = edges.get(h02);
			e0.oppositeEdge = eo0;
			e1.oppositeEdge = eo1;
			e2.oppositeEdge = eo2;
			if (eo0 != null)
				eo0.oppositeEdge = e0;
			if (eo1 != null)
				eo1.oppositeEdge = e1;
			if (eo2 != null)
				eo2.oppositeEdge = e2;

			edges.put(h01, e0);
			edges.put(h12, e1);
			edges.put(h20, e2);

			e0.vertex = v0;
			e1.vertex = v1;
			e2.vertex = v2;

			e0.nextEdge = e1;
			e1.nextEdge = e2;
			e2.nextEdge = e0;
			e0.prevEdge = e2;
			e1.prevEdge = e0;
			e2.prevEdge = e1;

			Triangle triangle = new Triangle();
			triangle.edges[0] = e0;
			triangle.edges[1] = e1;
			triangle.edges[2] = e2;
			e0.triangle = triangle;
			e1.triangle = triangle;
			e2.triangle = triangle;

			float b0 = x1 - x0;
			float b1 = y1 - y0;
			float b2 = z1 - z0;
			float c0 = x2 - x0;
			float c1 = y2 - y0;
			float c2 = z2 - z0;

			float x = b1 * c2 - b2 * c1;
			float y = b2 * c0 - b0 * c2;
			float z = b0 * c1 - b1 * c0;

			triangle.normalX = x;
			triangle.normalY = y;
			triangle.normalZ = z;

			double length = Math.sqrt(x * x + y * y + z * z);
			if (length != 0) {
				triangle.normalX /= length;
				triangle.normalY /= length;
				triangle.normalZ /= length;
			}

			triangles.add(triangle);

			return triangle;
		}
	}

	private final Triangle[] triangles;
	private float foldAngle = (float) Math.PI / 2;
	private float cosFoldAngle = 0;
	private float foldSharpness = 1;

	public NormalGenerator3f(Triangle[] triangles) {
		this.triangles = triangles;
	}

	/**
	 * Sets the minimum fold angle, in radians.
	 */
	public NormalGenerator3f foldAngle(double foldAngle) {
		this.foldAngle = (float) foldAngle;
		this.cosFoldAngle = (float) Math.cos(foldAngle);
		return this;
	}

	/**
	 * Sets the minimum fold angle, in radians.
	 */
	public NormalGenerator3f foldAngle(float foldAngle) {
		this.foldAngle = foldAngle;
		this.cosFoldAngle = (float) Math.cos(foldAngle);
		return this;
	}

	public float foldAngle() {
		return this.foldAngle;
	}

	/**
	 * Sets the fold sharpness. This value is used to interpolate the average normal
	 * of all triangles around a vertex and the normal of the fan of triangles up to
	 * fold edges.
	 * 
	 * @param foldSharpness a value from 0 (equivalent to no folding) to 1 (fold is
	 *                      completely crisp).
	 */
	public NormalGenerator3f foldSharpness(float foldSharpness) {
		if (foldSharpness < 0 || foldSharpness > 1) {
			throw new IllegalArgumentException("foldSharpness must be between 0 and 1");
		}
		this.foldSharpness = foldSharpness;
		return this;
	}

	public float foldSharpness() {
		return this.foldSharpness;
	}

	public Triangle[] triangles() {
		return this.triangles;
	}

	/**
	 * Computes normals for the mesh.
	 * 
	 * @return the total number of vertices in the resulting mesh.
	 */
	public int generateNormals() {
		initialize();
		computeSmoothNormals();
		fold();
		return computeFoldedNormals();
	}

	private static final int UNPROCESSED = -1;
	private static final int SMOOTHED = -2;
	private static final int FOLDED = -3;

	private void initialize() {
		for (Triangle triangle : this.triangles) {
			for (Edge edge : triangle.edges) {
				edge.vertex.index = UNPROCESSED;
			}
		}
	}

	private Edge getStartEdge(Edge edge, boolean stopAtFolds) {
		Edge current = edge;
		do {
			Edge prev = current.prevAtVertex();
			if (prev == null || (prev.folded && stopAtFolds))
				return current;
			current = prev;
		} while (current != edge);
		return current;
	}

	private void forEachEdgeAtVertex(Edge edge, boolean stopAtFolds, Consumer<Edge> iteratee) {
		Edge start = getStartEdge(edge, stopAtFolds);
		edge = start;

		do {
			iteratee.accept(edge);
			if (edge.folded && stopAtFolds)
				break;
			edge = edge.nextAtVertex();
		} while (edge != null && edge != start);
	}

	private void forEachTriangleAtVertex(Edge edge, boolean stopAtFolds, Consumer<Triangle> iteratee) {
		forEachEdgeAtVertex(edge, stopAtFolds, e -> iteratee.accept(e.triangle));
	}

	private void forEachEdgePairAtVertex(Edge edge, boolean stopAtFolds, BiConsumer<Edge, Edge> iteratee) {
		Edge start = getStartEdge(edge, stopAtFolds);
		Edge prev = start;
		Edge next = prev.nextAtVertex();
		while (next != null && next != start) {
			iteratee.accept(prev, next);
			if (next.folded && stopAtFolds)
				break;
			prev = next;
			next = next.nextAtVertex();
		}
	}

	private void computeSmoothNormals() {
		for (Triangle triangle : this.triangles) {
			for (Edge edge : triangle.edges) {
				if (edge.vertex.index == SMOOTHED)
					continue;

				Vertex v = edge.vertex;
				v.normalX = 0;
				v.normalY = 0;
				v.normalZ = 0;

				forEachTriangleAtVertex(edge, false, t -> {
					v.normalX += t.normalX;
					v.normalY += t.normalY;
					v.normalZ += t.normalZ;
				});

				v.normalize();
				v.index = SMOOTHED;
			}
		}
	}

	private void fold() {
		for (Triangle triangle : this.triangles) {
			for (Edge edge : triangle.edges) {
				if (edge.vertex.index == FOLDED)
					continue;
				edge.vertex.index = FOLDED;

				forEachEdgePairAtVertex(edge, false, (prev, next) -> {
					if (prev.triangle.dot(next.triangle) <= cosFoldAngle) {
						prev.fold();
					}
				});
			}
		}
	}

	private int computeFoldedNormals() {
		int k = 0;

		float f = foldSharpness;
		float rf = 1 - foldSharpness;

		for (Triangle triangle : this.triangles) {
			for (Edge edge : triangle.edges) {
				if (edge.vertex.index >= 0)
					continue;

				Vertex orig = edge.vertex;
				Vertex v = orig.clone();
				v.index = k++;

				v.normalX = 0;
				v.normalY = 0;
				v.normalZ = 0;

				forEachEdgeAtVertex(edge, true, e -> {
					e.vertex = v;
					v.normalX += e.triangle.normalX;
					v.normalY += e.triangle.normalY;
					v.normalZ += e.triangle.normalZ;
				});

				double length = Math.sqrt(v.normalX * v.normalX + v.normalY * v.normalY + v.normalZ * v.normalZ);
				if (length != 0) {
					v.normalX /= length;
					v.normalY /= length;
					v.normalZ /= length;
				}

				v.normalX = f * v.normalX + rf * orig.normalX;
				v.normalY = f * v.normalY + rf * orig.normalY;
				v.normalZ = f * v.normalZ + rf * orig.normalZ;
				if (f != 0 && f != 1) {
					v.normalize();
				}
			}
		}

		return k;

	}
}
