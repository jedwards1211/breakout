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

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;

import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.JPanel;

/**
 *
 * @author andy.edwards
 *
 */
@SuppressWarnings("serial")
public abstract class CellRendererWithButtons extends JPanel implements CellRendererWithContentArea {
	/**
	 *
	 */
	private static final long serialVersionUID = 6180976924327587352L;

	protected int spacing = 1;

	protected AbstractButton[] buttons;
	protected Rectangle contentArea = new Rectangle();

	protected Component content;

	public CellRendererWithButtons() {
		buttons = initButtons();
		for (AbstractButton button : buttons) {
			add(button);
		}
	}

	@Override
	public void doLayout() {
		int x = getWidth();
		for (int i = buttons.length - 1; i >= 0; i--) {
			if (!buttons[i].isVisible()) {
				continue;
			}
			Dimension size = getSizeForLayout(buttons[i]);
			buttons[i].setBounds(x - size.width, 0, size.width, size.height);
			x -= size.width - spacing;
		}
		contentArea.setBounds(spacing, spacing, x - spacing * 2, getHeight() - spacing * 2);
		if (content != null) {
			content.setBounds(contentArea);
		}
	}

	public Component getContent() {
		return content;
	}

	@Override
	public Rectangle getContentArea() {
		return contentArea;
	}

	protected Dimension getSizeForLayout(AbstractButton button) {
		return new Dimension(getHeight(), getHeight());
	}

	protected abstract AbstractButton[] initButtons();

	@Override
	protected void paintChildren(Graphics g) {
		doLayout();
		super.paintChildren(g);
	}

	public void setContent(Component content) {
		this.content = content;
		add(content);

		if (content instanceof JComponent) {
			JComponent jc = (JComponent) content;
			setBorder(jc.getBorder());
			jc.setBorder(null);
		}
		setBackground(content.getBackground());
	}
}
