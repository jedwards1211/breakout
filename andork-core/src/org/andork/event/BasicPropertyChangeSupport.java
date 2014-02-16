package org.andork.event;

import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

/**
 * This class is an adaptation of {@link PropertyChangeSupport} for
 * {@link BasicPropertyChangeListener}.
 */
public class BasicPropertyChangeSupport implements Serializable {

	private static final long							serialVersionUID	= -486778571303724183L;

	/**
	 * The listener list. A copy-on-write array is used instead of an List
	 * because the latter would run the risk of
	 * {@link ConcurrentModificationException}s (if a listener removed itself or
	 * another listener during an event notification).
	 */
	protected transient BasicPropertyChangeListener[]	listeners;

	/**
	 * Constructs a <code>BasicPropertyChangeSupport</code> object.
	 * 
	 * @param sourceBean
	 *            The bean to be given as the source for any events.
	 */

	public BasicPropertyChangeSupport() {
	}

	/**
	 * Add a BasicPropertyChangeListener to the listener list. The listener is
	 * registered for all properties. The same listener object may be added more
	 * than once, and will be called as many times as it is added. If
	 * <code>listener</code> is null, no exception is thrown and no action is
	 * taken.
	 * 
	 * @param listener
	 *            The BasicPropertyChangeListener to be added
	 */
	public synchronized void addPropertyChangeListener(BasicPropertyChangeListener listener) {
		if (listener == null) {
			return;
		}

		if (listener instanceof BasicPropertyChangeListenerProxy) {
			BasicPropertyChangeListenerProxy proxy = (BasicPropertyChangeListenerProxy) listener;
			// Call two argument add method.
			addPropertyChangeListener(proxy.getPropertyName(), (BasicPropertyChangeListener) proxy.getListener());
		} else {
			BasicPropertyChangeListener[] newListeners;
			if (listeners != null) {
				newListeners = Arrays.copyOf(listeners, listeners.length + 1);
			}
			else {
				newListeners = new BasicPropertyChangeListener[1];
			}
			newListeners[newListeners.length - 1] = listener;
			listeners = newListeners;
		}
	}

	/**
	 * Remove a BasicPropertyChangeListener from the listener list. This removes
	 * a BasicPropertyChangeListener that was registered for all properties. If
	 * <code>listener</code> was added more than once to the same event source,
	 * it will be notified one less time after being removed. If
	 * <code>listener</code> is null, or was never added, no exception is thrown
	 * and no action is taken.
	 * 
	 * @param listener
	 *            The BasicPropertyChangeListener to be removed
	 */
	public synchronized void removePropertyChangeListener(BasicPropertyChangeListener listener) {
		if (listener == null) {
			return;
		}

		if (listener instanceof BasicPropertyChangeListenerProxy) {
			BasicPropertyChangeListenerProxy proxy = (BasicPropertyChangeListenerProxy) listener;
			// Call two argument remove method.
			removePropertyChangeListener(proxy.getPropertyName(), (BasicPropertyChangeListener) proxy.getListener());
		} else {
			if (listeners != null) {
				int i;
				for (i = 0; i < listeners.length; i++) {
					if (listeners[i] == listener) {
						break;
					}
				}
				if (i < listeners.length) {
					if (listeners.length == 1) {
						listeners = null;
					}
					else {
						BasicPropertyChangeListener[] newListeners = new BasicPropertyChangeListener[listeners.length - 1];

						System.arraycopy(listeners, 0, newListeners, 0, i);
						System.arraycopy(listeners, i + 1, newListeners, i, listeners.length - i - 1);
						listeners = newListeners;
					}
				}
			}
		}
	}

	/**
	 * Returns an array of all the listeners that were added to the
	 * BasicPropertyChangeSupport object with addPropertyChangeListener().
	 * <p>
	 * If some listeners have been added with a named property, then the
	 * returned array will be a mixture of PropertyChangeListeners and
	 * <code>BasicPropertyChangeListenerProxy</code>s. If the calling method is
	 * interested in distinguishing the listeners then it must test each element
	 * to see if it's a <code>BasicPropertyChangeListenerProxy</code>, perform
	 * the cast, and examine the parameter.
	 * 
	 * <pre>
	 * BasicPropertyChangeListener[] listeners = bean.getPropertyChangeListeners();
	 * for (int i = 0; i &lt; listeners.length; i++) {
	 * 	if (listeners[i] instanceof BasicPropertyChangeListenerProxy) {
	 * 		BasicPropertyChangeListenerProxy proxy = (BasicPropertyChangeListenerProxy) listeners[i];
	 * 		if (proxy.getPropertyName().equals(&quot;foo&quot;)) {
	 * 			// proxy is a BasicPropertyChangeListener which was associated
	 * 			// with the property named &quot;foo&quot;
	 * 		}
	 * 	}
	 * }
	 * </pre>
	 * 
	 * @see BasicPropertyChangeListenerProxy
	 * @return all of the <code>PropertyChangeListeners</code> added or an empty
	 *         array if no listeners have been added
	 * @since 1.4
	 */
	public synchronized BasicPropertyChangeListener[] getPropertyChangeListeners() {
		List returnList = new ArrayList();

		// Add all the PropertyChangeListeners
		if (listeners != null) {
			returnList.addAll(Arrays.asList(listeners));
		}

		// Add all the PropertyChangeListenerProxys
		if (children != null) {
			Iterator<Enum<?>> iterator = children.keySet().iterator();
			while (iterator.hasNext()) {
				Enum<?> key = iterator.next();
				BasicPropertyChangeSupport child = (BasicPropertyChangeSupport) children.get(key);
				BasicPropertyChangeListener[] childListeners = child.getPropertyChangeListeners();
				for (int index = childListeners.length - 1; index >= 0; index--) {
					returnList.add(new BasicPropertyChangeListenerProxy(key, childListeners[index]));
				}
			}
		}
		return (BasicPropertyChangeListener[]) (returnList.toArray(new BasicPropertyChangeListener[0]));
	}

	/**
	 * Add a BasicPropertyChangeListener for a specific property. The listener
	 * will be invoked only when a call on firePropertyChange names that
	 * specific property. The same listener object may be added more than once.
	 * For each property, the listener will be invoked the number of times it
	 * was added for that property. If <code>propertyName</code> or
	 * <code>listener</code> is null, no exception is thrown and no action is
	 * taken.
	 * 
	 * @param propertyName
	 *            The name of the property to listen on.
	 * @param listener
	 *            The BasicPropertyChangeListener to be added
	 */

	public synchronized void addPropertyChangeListener(Enum<?> propertyName, BasicPropertyChangeListener listener) {
		if (listener == null || propertyName == null) {
			return;
		}
		if (children == null) {
			children = new java.util.Hashtable<Enum<?>, BasicPropertyChangeSupport>();
		}
		BasicPropertyChangeSupport child = (BasicPropertyChangeSupport) children.get(propertyName);
		if (child == null) {
			child = new BasicPropertyChangeSupport();
			children.put(propertyName, child);
		}
		child.addPropertyChangeListener(listener);
	}

	/**
	 * Remove a BasicPropertyChangeListener for a specific property. If
	 * <code>listener</code> was added more than once to the same event source
	 * for the specified property, it will be notified one less time after being
	 * removed. If <code>propertyName</code> is null, no exception is thrown and
	 * no action is taken. If <code>listener</code> is null, or was never added
	 * for the specified property, no exception is thrown and no action is
	 * taken.
	 * 
	 * @param propertyName
	 *            The name of the property that was listened on.
	 * @param listener
	 *            The BasicPropertyChangeListener to be removed
	 */

	public synchronized void removePropertyChangeListener(Enum<?> propertyName, BasicPropertyChangeListener listener) {
		if (listener == null || propertyName == null) {
			return;
		}
		if (children == null) {
			return;
		}
		BasicPropertyChangeSupport child = (BasicPropertyChangeSupport) children.get(propertyName);
		if (child == null) {
			return;
		}
		child.removePropertyChangeListener(listener);
	}

	/**
	 * Returns an array of all the listeners which have been associated with the
	 * named property.
	 * 
	 * @param propertyName
	 *            The name of the property being listened to
	 * @return all of the <code>PropertyChangeListeners</code> associated with
	 *         the named property. If no such listeners have been added, or if
	 *         <code>propertyName</code> is null, an empty array is returned.
	 */
	public synchronized BasicPropertyChangeListener[] getPropertyChangeListeners(Enum<?> propertyName) {
		ArrayList returnList = new ArrayList();

		if (children != null && propertyName != null) {
			BasicPropertyChangeSupport support = (BasicPropertyChangeSupport) children.get(propertyName);
			if (support != null) {
				returnList.addAll(Arrays.asList(support.getPropertyChangeListeners()));
			}
		}
		return (BasicPropertyChangeListener[]) (returnList.toArray(new BasicPropertyChangeListener[0]));
	}

	/**
	 * Report an int bound property update to any registered listeners. No event
	 * is fired if old and new are equal and non-null.
	 * <p>
	 * This is merely a convenience wrapper around the more general
	 * firePropertyChange method that takes Object values.
	 * 
	 * @param propertyName
	 *            The programmatic name of the property that was changed.
	 * @param oldValue
	 *            The old value of the property.
	 * @param newValue
	 *            The new value of the property.
	 */
	public void firePropertyChange(Object source, Enum<?> propertyName, int oldValue, int newValue) {
		if (oldValue == newValue) {
			return;
		}
		firePropertyChange(source, propertyName, new Integer(oldValue), new Integer(newValue));
	}

	/**
	 * Report a boolean bound property update to any registered listeners. No
	 * event is fired if old and new are equal and non-null.
	 * <p>
	 * This is merely a convenience wrapper around the more general
	 * firePropertyChange method that takes Object values.
	 * 
	 * @param propertyName
	 *            The programmatic name of the property that was changed.
	 * @param oldValue
	 *            The old value of the property.
	 * @param newValue
	 *            The new value of the property.
	 */
	public void firePropertyChange(Object source, Enum<?> propertyName, boolean oldValue, boolean newValue) {
		firePropertyChange(source, propertyName, Boolean.valueOf(oldValue), Boolean.valueOf(newValue));
	}

	/**
	 * Fire an existing PropertyChangeEvent to any registered listeners. No
	 * event is fired if the given event's old and new values are equal and
	 * non-null.
	 * 
	 * @param evt
	 *            The PropertyChangeEvent object.
	 */
	public void firePropertyChange(Object source, Enum<?> propertyName, Object oldValue, Object newValue) {
		if (oldValue != null && newValue != null && oldValue.equals(newValue)) {
			return;
		}
		firePropertyChange(source, propertyName, oldValue, newValue, -1);
	}

	/**
	 * Fire an existing PropertyChangeEvent to any registered listeners. No
	 * event is fired if the given event's old and new values are equal and
	 * non-null.
	 * 
	 * @param evt
	 *            The PropertyChangeEvent object.
	 */
	public void firePropertyChange(Object source, Enum<?> propertyName, Object oldValue, Object newValue, int index) {
		if (oldValue != null && newValue != null && oldValue.equals(newValue)) {
			return;
		}

		if (listeners != null) {
			for (BasicPropertyChangeListener target : listeners) {
				target.propertyChange(source, propertyName, oldValue, newValue, index);
			}
		}

		if (children != null && propertyName != null) {
			BasicPropertyChangeSupport child = null;
			child = (BasicPropertyChangeSupport) children.get(propertyName);
			if (child != null) {
				child.firePropertyChange(source, propertyName, oldValue, newValue, index);
			}
		}
	}

	/**
	 * Report a bound indexed property update to any registered listeners.
	 * <p>
	 * No event is fired if old and new values are equal and non-null.
	 * 
	 * @param propertyName
	 *            The programmatic name of the property that was changed.
	 * @param index
	 *            index of the property element that was changed.
	 * @param oldValue
	 *            The old value of the property.
	 * @param newValue
	 *            The new value of the property.
	 * @since 1.5
	 */
	public void fireIndexedPropertyChange(Object source, Enum<?> propertyName, int index, Object oldValue, Object newValue) {
		firePropertyChange(source, propertyName, oldValue, newValue, index);
	}

	/**
	 * Report an <code>int</code> bound indexed property update to any
	 * registered listeners.
	 * <p>
	 * No event is fired if old and new values are equal and non-null.
	 * <p>
	 * This is merely a convenience wrapper around the more general
	 * fireIndexedPropertyChange method which takes Object values.
	 * 
	 * @param propertyName
	 *            The programmatic name of the property that was changed.
	 * @param index
	 *            index of the property element that was changed.
	 * @param oldValue
	 *            The old value of the property.
	 * @param newValue
	 *            The new value of the property.
	 * @since 1.5
	 */
	public void fireIndexedPropertyChange(Object source, Enum<?> propertyName, int index, int oldValue, int newValue) {
		if (oldValue == newValue) {
			return;
		}
		fireIndexedPropertyChange(source, propertyName, index, new Integer(oldValue), new Integer(newValue));
	}

	/**
	 * Report a <code>boolean</code> bound indexed property update to any
	 * registered listeners.
	 * <p>
	 * No event is fired if old and new values are equal and non-null.
	 * <p>
	 * This is merely a convenience wrapper around the more general
	 * fireIndexedPropertyChange method which takes Object values.
	 * 
	 * @param propertyName
	 *            The programmatic name of the property that was changed.
	 * @param index
	 *            index of the property element that was changed.
	 * @param oldValue
	 *            The old value of the property.
	 * @param newValue
	 *            The new value of the property.
	 * @since 1.5
	 */
	public void fireIndexedPropertyChange(Object source, Enum<?> propertyName, int index, boolean oldValue, boolean newValue) {
		if (oldValue == newValue) {
			return;
		}
		fireIndexedPropertyChange(source, propertyName, index, Boolean.valueOf(oldValue), Boolean.valueOf(newValue));
	}

	/**
	 * Check if there are any listeners for a specific property, including those
	 * registered on all properties. If <code>propertyName</code> is null, only
	 * check for listeners registered on all properties.
	 * 
	 * @param propertyName
	 *            the property name.
	 * @return true if there are one or more listeners for the given property
	 */
	public synchronized boolean hasListeners(Enum<?> propertyName) {
		if (listeners != null) {
			// there is a generic listener
			return true;
		}
		if (children != null && propertyName != null) {
			BasicPropertyChangeSupport child = (BasicPropertyChangeSupport) children.get(propertyName);
			if (child != null && child.listeners != null) {
				return child.listeners != null;
			}
		}
		return false;
	}

	/**
	 * @serialData Null terminated list of <code>PropertyChangeListeners</code>.
	 *             <p>
	 *             At serialization time we skip non-serializable listeners and
	 *             only serialize the serializable listeners.
	 * 
	 */
	private void writeObject(ObjectOutputStream s) throws IOException {
		s.defaultWriteObject();

		if (listeners != null) {
			for (BasicPropertyChangeListener l : listeners) {
				if (l instanceof Serializable) {
					s.writeObject(l);
				}
			}
		}
		s.writeObject(null);
	}

	private void readObject(ObjectInputStream s) throws ClassNotFoundException, IOException {
		s.defaultReadObject();

		Object listenerOrNull;
		while (null != (listenerOrNull = s.readObject())) {
			addPropertyChangeListener((BasicPropertyChangeListener) listenerOrNull);
		}
	}

	/**
	 * Hashtable for managing listeners for specific properties. Maps property
	 * names to BasicPropertyChangeSupport objects.
	 * 
	 * @serial
	 * @since 1.2
	 */
	private Hashtable<Enum<?>, BasicPropertyChangeSupport>	children;

	/**
	 * Internal version number
	 * 
	 * @serial
	 * @since
	 */
	private int												basicPropertyChangeSupportSerializedDataVersion	= 2;
}
