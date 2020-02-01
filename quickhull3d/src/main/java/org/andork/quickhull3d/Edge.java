package org.andork.quickhull3d;

import java.util.Arrays;

/**
 * An edge of a {@link Face}. If two faces are neighbors, each will have its own
 * {@code Edge} with the same vertices in opposite order. (Some call this
 * construction a "half edge".) The two faces connected to the edge are
 * {@link #face} and {@link #oppositeEdge}{@code .face} (if
 * {@link #oppositeEdge} is not {@code null}).
 *
 * @author andy
 */
public class Edge {
	public final Face face;
	public final double[] prevVertex;
	public final double[] nextVertex;
	public Edge prevEdge;
	public Edge nextEdge;
	public Edge oppositeEdge;

	Edge(Face face, double[] prevVertex, double[] nextVertex) {
		super();
		if (Switches.PERFORM_SANITY_CHECKS) {
			assert !Arrays.equals(prevVertex, nextVertex);
		}
		this.face = face;
		this.prevVertex = prevVertex;
		this.nextVertex = nextVertex;
	}

	/**
	 * Sets {@link #oppositeEdge} to the given {@code Edge}, and the given
	 * {@code Edge}'s {@link #oppositeEdge} to this.
	 */
	public void setOppositeEdge(Edge newOppositeEdge) {
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
	 * Determines if this {@code Edge} is in the horizon for the given
	 * viewpoint. That is, if this {@code Edge}'s {@link #face}
	 * {@linkplain Face#isWithinHorizonForPoint(double[]) is within the horizon
	 * for the viewpoint}, but its {@link #oppositeEdge}'s {@link #Face} is not.
	 *
	 * @param point
	 * @return
	 */
	public boolean isInHorizonOfViewPoint(double[] point) {
		return face.isWithinHorizonForPoint(point) && !oppositeEdge.face.isWithinHorizonForPoint(point);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Edge[prevVertex=").append(Arrays.toString(prevVertex)).append(", nextVertex=")
				.append(Arrays.toString(nextVertex)).append("]");
		return builder.toString();
	}

}
