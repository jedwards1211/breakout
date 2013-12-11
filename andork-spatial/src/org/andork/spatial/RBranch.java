package org.andork.spatial;

public interface RBranch<R, T> extends RNode<R, T> {
	public int numChildren();

	public RNode<R, T> childAt(int index);
}
