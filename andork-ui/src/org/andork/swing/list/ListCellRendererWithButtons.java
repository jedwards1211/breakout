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
package org.andork.swing.list;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Rectangle;

import javax.swing.AbstractButton;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.SwingUtilities;

import org.andork.swing.CellRendererWithButtons;

@SuppressWarnings("serial")
public abstract class ListCellRendererWithButtons extends CellRendererWithButtons
		implements ListCellRendererTracker, ListCellRenderer {
	/**
	 *
	 */
	private static final long serialVersionUID = -7231795690983332168L;

	protected ListCellRenderer wrapped;

	protected int rendererIndex;

	protected ListCellRendererWithButtons(ListCellRenderer wrapped) {
		super();
		setLayout(new FlowLayout(FlowLayout.LEFT, 1, 1));
		this.wrapped = wrapped;
	}

	@Override
	public void doLayout() {
		JList list = (JList) SwingUtilities.getAncestorOfClass(JList.class, this);
		if (list != null) {
			Rectangle bounds = getBounds();
			Rectangle listBounds = list.getVisibleRect();
			listBounds.y = 0;
			listBounds.height = list.getHeight();
			bounds = bounds.intersection(listBounds);

			int x = bounds.x + bounds.width - spacing;
			for (int i = buttons.length - 1; i >= 0; i--) {
				if (!buttons[i].isVisible()) {
					continue;
				}
				Dimension size = getSizeForLayout(buttons[i]);
				buttons[i].setBounds(x - size.width, spacing / 2, size.width, bounds.height - spacing);
				x -= size.width - spacing;
			}

			contentArea.setBounds(spacing, spacing, x - spacing * 2, getHeight() - spacing * 2);
			if (content != null) {
				content.setBounds(contentArea);
			}
		}
	}

	@Override
	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
			boolean cellHasFocus) {
		rendererIndex = index;
		setContent(wrapped.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus));
		return this;
	}

	@Override
	public int getRendererIndex() {
		return rendererIndex;
	}

	@Override
	protected Dimension getSizeForLayout(AbstractButton button) {
		return button.getPreferredSize();
	}
}
