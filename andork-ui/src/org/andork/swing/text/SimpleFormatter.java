/*******************************************************************************
 * Breakout Cave Survey Visualizer
 *
 * Copyright (C) 2014 James Edwards
 *
 * jedwards8 at fastmail dot fm
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *******************************************************************************/
package org.andork.swing.text;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.event.DocumentEvent;
import javax.swing.text.JTextComponent;

import org.andork.format.Format;
import org.andork.swing.event.EasyDocumentListener;

public class SimpleFormatter {
	private class ChangeHandler extends EasyDocumentListener implements PropertyChangeListener, FocusListener {
		boolean updating = false;

		@Override
		public void documentChanged(DocumentEvent e) {
			if (!updating && e.getDocument() == textComp.getDocument()) {
				try {
					commitEdit();
				} catch (Exception e1) {
				}
			}
		}

		@Override
		public void focusGained(FocusEvent e) {

		}

		@Override
		public void focusLost(FocusEvent e) {
			if (e.getSource() == textComp) {
				valueToText();
			}
		}

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			if (!updating && evt.getSource() == textComp && "value".equals(evt.getPropertyName())) {
				valueToText();
			}
		}
	}

	JTextComponent textComp;

	Format format;

	ChangeHandler changeHandler = new ChangeHandler();

	public SimpleFormatter(Format format) {
		super();
		this.format = format;
	}

	public void commitEdit() throws Exception {
		changeHandler.updating = true;
		try {
			textComp.putClientProperty("value", format.parse(textComp.getText()));
		} finally {
			changeHandler.updating = false;
		}
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
}
