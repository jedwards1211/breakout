package org.andork.swing.selector;

import java.util.Vector;

import javax.swing.DefaultComboBoxModel;

/**
 * Gets rid of DefaultComboBoxModel's silly behavior of setting the selection in
 * the constructors and addElement() (but not in insertElementAt(), LOL!)
 * 
 * @author james.a.edwards
 */
@SuppressWarnings("serial")
public class BetterComboBoxModel extends DefaultComboBoxModel {
	/**
	 * Constructs an empty BetterComboBoxModel object.
	 */
	public BetterComboBoxModel() {
		super();
	}

	/**
	 * Constructs a BetterComboBoxModel object initialized with an array of
	 * objects.
	 * 
	 * @param items
	 *            an array of Object objects
	 */
	public BetterComboBoxModel(final Object items[]) {
		super(items);
		setSelectedItem(null);
	}

	/**
	 * Constructs a BetterComboBoxModel object initialized with a vector.
	 * 
	 * @param v
	 *            a Vector object ...
	 */
	public BetterComboBoxModel(Vector<?> v) {
		super(v);
		setSelectedItem(null);
	}

	public void addElement(Object anObject) {
		insertElementAt(anObject, getSize());
	}
}
