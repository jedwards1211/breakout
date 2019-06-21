package org.breakout.model;

public enum HighlightMode {
	/**
	 * Any stations within a certain distance from the hovered station will be highlighted
	 */
	NEARBY,
	/**
	 * Only stations in the same trip as the hovered stations will be highlighted
	 */
	SAME_TRIP,
	/**
	 * Only stations with the same survey designation as the hovered stations will be highlighted
	 */
	SAME_DESIGNATION,
	/**
	 * Shots along the best route (based upon LRUDs) to selected shots will be highlighted
	 */
	DIRECTIONS,
	/**
	 * Shots along the shortest route to selected shots will be highlighted
	 */
	DIRECTIONS_SHORTEST_ROUTE,
}
