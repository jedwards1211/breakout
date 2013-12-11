package org.andork.spatial;

public interface SpatialIndex<R, T> {
	public RLeaf<R, T> createLeaf(R mbr, T object);
}
