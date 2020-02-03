package org.andork.quickhull3d;

/**
 * An edge of a {@link Face}. If two faces are neighbors, each will have its own
 * {@code Edge} with the same vertices in opposite order. (Some call this
 * construction a "half edge".) The two faces connected to the edge are
 * {@link #face} and {@link #oppositeEdge}{@code .face} (if
 * {@link #oppositeEdge} is not {@code null}).
 *
 * @author andy
 */
public class Edge<V extends Vertex> {
	public final Face<V> face;
	public final V prevVertex;
	public final V nextVertex;
	public Edge<V> prevEdge;
	public Edge<V> nextEdge;
	public Edge<V> oppositeEdge;

	Edge(Face<V> face, V prevVertex, V nextVertex) {
		super();
		if (Switches.PERFORM_SANITY_CHECKS) {
			assert !Vertex.equals(prevVertex, nextVertex);
		}
		this.face = face;
		this.prevVertex = prevVertex;
		this.nextVertex = nextVertex;
	}

	/**
	 * Sets {@link #oppositeEdge} to the given {@code Edge}, and the given
	 * {@code Edge}'s {@link #oppositeEdge} to this.
	 */
	public void setOppositeEdge(Edge<V> newOppositeEdge) {
		if (Switches.PERFORM_SANITY_CHECKS) {
			assert prevVertex == newOppositeEdge.nextVertex && nextVertex == newOppositeEdge.prevVertex;
		}
		if (oppositeEdge != null) {
			oppositeEdge.oppositeEdge = null;
		}
		oppositeEdge = newOppositeEdge;
		newOppositeEdge.oppositeEdge = this;
	}

	/**
	 * Determines if this {@code Edge} is in the horizon for the given viewpoint.
	 * That is, if this {@code Edge}'s {@link #face}
	 * {@linkplain Face#isWithinHorizonForPoint(double[]) is within the horizon for
	 * the viewpoint}, but its {@link #oppositeEdge}'s {@link #Face} is not.
	 *
	 * @param point
	 * @return
	 */
	public boolean isInHorizonOfViewPoint(V point) {
		return face.isWithinHorizonForPoint(point) && !oppositeEdge.face.isWithinHorizonForPoint(point);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Edge[prevVertex=").append(prevVertex).append(", nextVertex=").append(nextVertex).append("]");
		return builder.toString();
	}

}
