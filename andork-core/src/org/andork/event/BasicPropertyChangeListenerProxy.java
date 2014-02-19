package org.andork.event;

public class BasicPropertyChangeListenerProxy extends java.util.EventListenerProxy implements BasicPropertyChangeListener {

	private Object	propertyName;

	/**
	 * Constructor which binds the PropertyChangeListener to a specific
	 * property.
	 * 
	 * @param listener
	 *            The listener object
	 * @param propertyName
	 *            The name of the property to listen on.
	 */
	public BasicPropertyChangeListenerProxy(Object propertyName, BasicPropertyChangeListener listener) {
		// XXX - msd NOTE: I changed the order of the arguments so that it's
		// similar to PropertyChangeSupport.addPropertyChangeListener(String,
		// PropertyChangeListener);
		super(listener);
		this.propertyName = propertyName;
	}

	/**
	 * Forwards the property change event to the listener delegate.
	 * 
	 * @param evt
	 *            the property change event
	 */
	public void propertyChange(Object source, Object propertyName, Object oldValue, Object newValue, int index) {
		((BasicPropertyChangeListener) getListener()).propertyChange(source, propertyName, oldValue, newValue, index);
	}

	/**
	 * Returns the name of the named property associated with the listener.
	 */
	public Object getPropertyName() {
		return propertyName;
	}

}
