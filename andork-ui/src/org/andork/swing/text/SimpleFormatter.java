package org.andork.swing.text;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.event.DocumentEvent;
import javax.swing.text.JTextComponent;

import org.andork.swing.event.EasyDocumentListener;
import org.andork.util.Format;

public class SimpleFormatter {
	JTextComponent	textComp;
	Format			format;

	ChangeHandler	changeHandler	= new ChangeHandler();

	public SimpleFormatter(Format format) {
		super();
		this.format = format;
	}

	public void install(JTextComponent textComp) {
		if (this.textComp != null) {
			throw new IllegalStateException("must be uninstalled first");
		}
		this.textComp = textComp;
		textComp.getDocument().addDocumentListener(changeHandler);
		textComp.addFocusListener(changeHandler);
		textComp.addPropertyChangeListener("value", changeHandler);
	}

	public void uninstall() {
		if (textComp == null) {
			throw new IllegalStateException("must be installed first");
		}
		textComp.getDocument().removeDocumentListener(changeHandler);
		textComp.removePropertyChangeListener("value", changeHandler);
		textComp.removeFocusListener(changeHandler);
		textComp = null;
	}

	public void valueToText() {
		changeHandler.updating = true;
		try {
			textComp.setText(format.format(textComp.getClientProperty("value")));
		} finally {
			changeHandler.updating = false;
		}

	}

	public void commitEdit() throws Exception {
		changeHandler.updating = true;
		try {
			textComp.putClientProperty("value", format.parse(textComp.getText()));
		} finally {
			changeHandler.updating = false;
		}
	}

	private class ChangeHandler extends EasyDocumentListener implements PropertyChangeListener, FocusListener {
		boolean	updating	= false;

		public void propertyChange(PropertyChangeEvent evt) {
			if (!updating && evt.getSource() == textComp && "value".equals(evt.getPropertyName())) {
				valueToText();
			}
		}

		@Override
		public void documentChanged(DocumentEvent e) {
			if (!updating && e.getDocument() == textComp.getDocument()) {
				try {
					commitEdit();
				} catch (Exception e1) {}
			}
		}

		public void focusGained(FocusEvent e) {

		}

		public void focusLost(FocusEvent e) {
			if (e.getSource() == textComp) {
				valueToText();
			}
		}
	}
}