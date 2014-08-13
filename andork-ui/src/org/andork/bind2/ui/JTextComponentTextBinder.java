package org.andork.bind2.ui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;

import org.andork.bind2.Binder;

public class JTextComponentTextBinder extends Binder<String> implements PropertyChangeListener, DocumentListener {
	JTextComponent	component;
	Document		document;

	public JTextComponentTextBinder(JTextComponent component) {
		this.component = component;
		setDocument(component.getDocument());
		component.addPropertyChangeListener("document", this);
	}

	@Override
	public String get() {
		return component.getText();
	}

	public void insertUpdate(DocumentEvent e) {
		updateBindings(false);
	}

	public void removeUpdate(DocumentEvent e) {
		updateBindings(false);
	}

	public void changedUpdate(DocumentEvent e) {
		updateBindings(false);
	}

	public void propertyChange(PropertyChangeEvent evt) {
		setDocument(component.getDocument());
	}

	private void setDocument(Document newDocument) {
		if (document != null) {
			document.removeDocumentListener(this);
		}
		document = newDocument;
		if (newDocument != null) {
			newDocument.addDocumentListener(this);
		}
		updateBindings(false);
	}
}
