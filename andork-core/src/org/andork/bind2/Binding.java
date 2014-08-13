package org.andork.bind2;

/**
 * A listener that will be {@linkplain #update(boolean) called} whenever the
 * value of any {@link Binder} to which it is bound changes.<br>
 * <br>
 * To reduce boilerplate code, implementations typically provide a {@link Link}
 * to a {@code Binder} for each input.
 * 
 * @author andy.edwards
 */
public interface Binding {
	/**
	 * Called by a {@link Binder} whenever its value changes, if this
	 * {@code Binding} is bound to it. Of course, there is nothing to stop you
	 * from calling this method yourself, and in some cases you may want to.
	 * 
	 * @param force
	 *            whether to "force" an update: this Binder should assume the
	 *            bound values have changed, even if the values of the
	 *            {@link Binder}s it is bound to are still the same object. If
	 *            this {@link Binding} is a {@link Binder} itself, and
	 *            {@code force} is {@code true}, it should update its
	 *            {@link Binding}s even if its source values appear to be
	 *            unchanged. This would be necessary, for example, if the
	 *            internal properties of a value object have changed but the
	 *            reference remains the same and its {@code equals()} method
	 *            doesn't indicate its properties have changed.
	 */
	public void update(boolean force);
}
