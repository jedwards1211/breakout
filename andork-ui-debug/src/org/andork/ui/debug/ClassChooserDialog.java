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
package org.andork.ui.debug;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

@SuppressWarnings("serial")
public class ClassChooserDialog extends JDialog {
	private static class GlobalInstanceHolder {
		private static ClassChooserDialog INSTANCE = new ClassChooserDialog();
	}

	/**
	 *
	 */
	private static final long serialVersionUID = 6093657186382286602L;

	public static void main(String[] args) {
		String s = ClassChooserDialog.showDialog(null, null);
		System.out.println(s);
	}

	public static String showDialog(String title, Component parent) {
		return GlobalInstanceHolder.INSTANCE.show(title, parent);
	}

	private ClassChooserPane chooserPane;

	private JOptionPane optionPane;

	public ClassChooserDialog() {
		init();
	}

	public void init() {
		chooserPane = new ClassChooserPane();
		optionPane = new JOptionPane(chooserPane,
				JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_CANCEL_OPTION, null);

		PropertyChangeListener optionPaneListener = new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				setVisible(false);
			}
		};
		optionPane.addPropertyChangeListener(JOptionPane.VALUE_PROPERTY, optionPaneListener);

		chooserPane.getMatchList().addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2 && chooserPane.getMatchList().getSelectedIndex() >= 0) {
					optionPane.setValue(JOptionPane.OK_OPTION);
				}
			}
		});

		getContentPane().add(optionPane, BorderLayout.CENTER);
		pack();
	}

	public String show(String title, Component parent) {
		setTitle(title);
		chooserPane.setTypePrefix("");
		optionPane.setValue(null);
		setLocationRelativeTo(parent);

		setModalityType(ModalityType.APPLICATION_MODAL);
		setVisible(true);

		return optionPane.getValue() != null &&
				(Integer) optionPane.getValue() == JOptionPane.OK_OPTION ? chooserPane.getSelectedClassName() : null;
	}
}
