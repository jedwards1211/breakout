package org.andork.bind2.ui;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JComboBox;

import org.andork.bind2.Binder;

public class JComboBoxSelectedItemBinder extends Binder<Object> implements ItemListener {
	private JComboBox	comboBox;

	public JComboBoxSelectedItemBinder() {
	}

	public void bind(JComboBox comboBox) {
		if (this.comboBox != comboBox) {
			if (this.comboBox != null) {
				this.comboBox.removeItemListener(this);
			}
			this.comboBox = comboBox;
			if (comboBox != null) {
				comboBox.addItemListener(this);
			}
		}
	}

	@Override
	public Object get() {
		return comboBox.getSelectedItem();
	}

	public void itemStateChanged(ItemEvent e) {
		if (e.getStateChange() == ItemEvent.SELECTED) {
			updateBindings(false);
		}
	}
}
