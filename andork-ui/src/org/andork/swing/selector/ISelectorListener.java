package org.andork.swing.selector;

/**
 * Interface for listening to an {@link ISelector}.
 * 
 * @author james.a.edwards
 */
public interface ISelectorListener<T> {
	/**
	 * Called by an {@link ISelector} this listener is registered on when the
	 * selection changes.
	 * 
	 * @param selector
	 *            the {@code ISelector} whose selection changed.
	 * @param oldSelection
	 *            the previous selected value.
	 * @param newSelection
	 *            the new selected value.
	 */
	public void selectionChanged(ISelector<T> selector, T oldSelection, T newSelection);
}
