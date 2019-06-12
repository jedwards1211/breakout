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
package org.andork.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.text.JTextComponent;

import org.andork.swing.event.EasyDocumentListener;

@SuppressWarnings("serial")
public class TextComponentWithHintAndClear extends JPanel {
	/**
	 *
	 */
	private static final long serialVersionUID = -5280095822456009459L;
	public final JTextComponent textComponent;
	private JButton clearButton;
	private JLabel hintLabel;
	private Box adornments;

	public TextComponentWithHintAndClear(JTextComponent textComponent, String hint) {
		this.textComponent = textComponent;

		clearButton = new JButton();
		ModernStyleClearButton.createClearButton(clearButton);

		hintLabel = new JLabel(hint);
		hintLabel.setForeground(Color.LIGHT_GRAY);
		hintLabel.setFont(hintLabel.getFont().deriveFont(Font.ITALIC));
		hintLabel.setOpaque(false);
		
		textComponent.setLayout(new BorderLayout());
		textComponent.add(hintLabel, BorderLayout.WEST);

		setLayout(new BorderLayout());
		add(textComponent, BorderLayout.CENTER);
		adornments = Box.createHorizontalBox();
		adornments.add(clearButton);
		add(adornments, BorderLayout.EAST);

		setBorder(textComponent.getBorder());
		setBackground(textComponent.getBackground());
		textComponent.setBorder(null);

		clearButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				TextComponentWithHintAndClear.this.textComponent.setText("");
				TextComponentWithHintAndClear.this.textComponent.requestFocus();
			}
		});

		textComponent.getDocument().addDocumentListener(new EasyDocumentListener() {
			@Override
			public void documentChanged(DocumentEvent e) {
				updateHintLabelVisible();
			}
		});

		updateHintLabelVisible();
	}

	public TextComponentWithHintAndClear(String hint) {
		this(new JTextField(), hint);
	}
	
	public Box getAdornments() {
		return adornments;
	}

	private void updateHintLabelVisible() {
		String text = textComponent.getText();
		hintLabel.setVisible(text == null || "".equals(text));
	}
}
