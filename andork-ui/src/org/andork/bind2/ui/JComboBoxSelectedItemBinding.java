package org.andork.bind2.ui;

import javax.swing.JComboBox;

import org.andork.bind2.Binding;
import org.andork.bind2.Link;

public class JComboBoxSelectedItemBinding implements Binding {
	public final Link<Object>	selectedItemLink	= new Link<Object>(this);
	public final JComboBox		comboBox;

	public JComboBoxSelectedItemBinding(JComboBox comboBox) {
		super();
		this.comboBox = comboBox;
	}

	public void update(boolean force) {
		comboBox.setSelectedItem(selectedItemLink.get());
	}
}
