package org.andork.bind2.ui;

import java.util.Collections;
import java.util.List;

import javax.swing.JComboBox;

import org.andork.bind2.Binding;
import org.andork.bind2.Link;
import org.andork.swing.selector.JComboBoxUtils;

public class JComboBoxItemsBinding implements Binding {
	public final Link<List<?>>	itemsLink	= new Link<List<?>>(this);
	public final JComboBox		comboBox;

	public JComboBoxItemsBinding(JComboBox comboBox) {
		super();
		this.comboBox = comboBox;
	}

	public void update(boolean force) {
		List<?> values = itemsLink.get();
		if (values == null) {
			values = Collections.emptyList();
		}
		JComboBoxUtils.setItemsWithoutChangingSelectionIfPossible(comboBox, values);
	}
}
