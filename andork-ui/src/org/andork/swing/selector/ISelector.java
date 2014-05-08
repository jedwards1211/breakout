package org.andork.swing.selector;


/**
 * An interface for some UI element that allows the user to select a value. The
 * selection can be changed by {@link #setSelection(T)} or by user action. When
 * the selection is changed all registered {@link ISelectorListener}s will be
 * notified. <br>
 * <br>
 * For example, in the rollout strategy panel, one {@code ISelector} is a
 * {@link DefaultSelector}, which contains a single combo box with a list of the
 * user's positions. The other {@code ISelector} is an
 * {@link OptionPositionBuilder}, which allows the user to pick the action,
 * expiration date, strike price, and contract type and sets its selection to an
 * {@link IPosition} constructed from those values. These two selectors are
 * combined by a {@link TandemSelector}, which keeps them in sync with each
 * other and provides a single point to listen for selection changes.
 * 
 * @author james.a.edwards
 */
public interface ISelector<T> {
	/**
	 * Sets the selected position. If the new selection is different from the
	 * current selection, the {@link ISelectorListener}s will be notified.
	 * 
	 * @param newSelection
	 *            the new selection.
	 */
	public void setSelection(T newSelection);

	/**
	 * Sets whether the selector component is enabled for user interaction. Even
	 * if it is not enabled, the program will still be able to
	 * {@link #setSelection(Object)}.
	 */
	public void setEnabled(boolean enabled);

	/**
	 * Gets the selected position.
	 * 
	 * @return the selected position.
	 */
	public T getSelection();

	public void addSelectorListener(ISelectorListener<T> listener);

	public void removeSelectorListener(ISelectorListener<T> listener);
}