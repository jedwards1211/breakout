/*******************************************************************************
 * Breakout Cave Survey Visualizer
 *
 * Copyright (C) 2014 James Edwards
 *
 * jedwards8 at fastmail dot fm
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *******************************************************************************/
package org.andork.bind.ui;

import java.awt.Component;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.AbstractButton;
import javax.swing.JLabel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;

import org.andork.bind.Binder;
import org.andork.util.Java7;

public class ComponentTextBinder extends Binder<String> implements PropertyChangeListener, DocumentListener {
	Binder<String>	upstream;
	Component		comp;

	Document		document;

	boolean			updating;

	public ComponentTextBinder(Component comp) {
		super();
		this.comp = comp;
		if (comp instanceof JTextComponent) {
			comp.addPropertyChangeListener("document", this);
			setDocument(((JTextComponent) comp).getDocument());
		} else {
			comp.addPropertyChangeListener("text", this);
		}
	}

	public static ComponentTextBinder bind(JTextComponent textComp, Binder<String> upstream) {
		return new ComponentTextBinder(textComp).bind(upstream);
	}

	public static ComponentTextBinder bind(JLabel textComp, Binder<String> upstream) {
		return new ComponentTextBinder(textComp).bind(upstream);
	}

	public static ComponentTextBinder bind(AbstractButton textComp, Binder<String> upstream) {
		return new ComponentTextBinder(textComp).bind(upstream);
	}

	public ComponentTextBinder bind(Binder<String> upstream) {
		if (this.upstream != upstream) {
			if (this.upstream != null) {
				unbind0(this.upstream, this);
			}
			this.upstream = upstream;
			if (upstream != null) {
				bind0(this.upstream, this);
			}
			update(false);
		}
		return this;
	}

	public void unbind() {
		bind(null);
	}

	@Override
	public String get() {
		if (comp instanceof JTextComponent) {
			return ((JTextComponent) comp).getText();
		}
		if (comp instanceof JLabel) {
			return ((JLabel) comp).getText();
		}
		if (comp instanceof AbstractButton) {
			return ((AbstractButton) comp).getText();
		}
		return null;
	}

	@Override
	public void set(String newValue) {
		if (comp instanceof JTextComponent) {
			((JTextComponent) comp).setText(newValue);
		} else if (comp instanceof JLabel) {
			((JLabel) comp).setText(newValue);
		} else if (comp instanceof AbstractButton) {
			((AbstractButton) comp).setText(newValue);
		}
	}

	public void update(boolean force) {
		updating = true;
		try {
			String newValue = upstream == null ? null : upstream.get();
			if (force || !Java7.Objects.equals(get(), newValue)) {
				set(newValue);
			}
		} finally {
			updating = false;
		}
	}

	protected void setDocument(Document document) {
		if (this.document != document) {
			if (this.document != null) {
				this.document.removeDocumentListener(this);
			}
			this.document = document;
			if (document != null) {
				document.addDocumentListener(this);
			}

			update(false);
		}
	}

	public void handle(DocumentEvent e) {
		if (!updating && upstream != null && e.getDocument() == document) {
			setUpstream();
		}
	}

	protected void setUpstream() {
		try {
			upstream.set(get());
		} catch (Exception ex) {

		}
	}

	@Override
	public void insertUpdate(DocumentEvent e) {
		handle(e);
	}

	@Override
	public void removeUpdate(DocumentEvent e) {
		handle(e);
	}

	@Override
	public void changedUpdate(DocumentEvent e) {
		handle(e);
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getSource() == comp) {
			if ("text".equals(evt.getPropertyName())) {
				if (!updating && upstream != null) {
					setUpstream();
				}
			}
			else if ("document".equals(evt.getPropertyName())) {
				setDocument(((JTextComponent) comp).getDocument());
			}
		}
	}
}
