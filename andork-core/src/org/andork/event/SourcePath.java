package org.andork.event;

public class SourcePath {
	public final Object	parent;
	public final Object	child;

	public SourcePath(Object parent, Object child) {
		super();
		this.parent = parent;
		this.child = child;
	}

	public String toString() {
		return getClass().getName() + "[" + toStringHelper(this) + "]";
	}

	private String toStringHelper(Object source) {
		if (source instanceof SourcePath) {
			SourcePath path = (SourcePath) source;
			return path.parent + ", " + toStringHelper(path.child);
		}
		return source.toString();
	}
}
