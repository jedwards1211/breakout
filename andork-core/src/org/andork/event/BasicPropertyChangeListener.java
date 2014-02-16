package org.andork.event;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.EventListener;

/**
 * An adaptation of {@link PropertyChangeListener} to work around the need for
 * allocating {@link PropertyChangeEvent}s on the heap. All fields of the
 * {@link PropertyChangeEvent} are passed as method parameters, so they are
 * allocated on the stack. This may be significant when property changes are
 * occuring rapidly.<br>
 * <br>
 * Down with the heap!
 * 
 * @author james.a.edwards
 */
public interface BasicPropertyChangeListener extends EventListener {
	/**
	 * This method gets called when a property is changed.
	 * 
	 * @param source
	 *            the object whose property was changed.
	 * @param property
	 *            the property that was changed
	 * @param oldValue
	 *            the old value of the property (some callers may pass
	 *            {@code null}).
	 * @param newValue
	 *            the new value of the property (some callers may pass
	 *            {@code null}).
	 * @param index
	 *            the index of the property if applicable (if not, should be
	 *            {@code <0}).
	 */
	public void propertyChange(Object source, Enum<?> property, Object oldValue, Object newValue, int index);
}
