package org.andork.react;

public abstract class Reaction<T> extends Reactable<T> {
	private boolean valid;

	protected abstract T calculate();

	@Override
	public final T get() {
		validate();
		return super.get();
	}

	public final void invalidate() {
		if (valid) {
			valid = false;
			invalidateReactions();
		}
	}

	public boolean isValid() {
		return valid;
	}

	public final void validate() {
		if (!valid) {
			set(calculate());
			valid = true;
		}
	}
}
