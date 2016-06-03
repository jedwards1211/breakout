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
package org.andork.ui.test.fixture;

import java.awt.Component;
import java.lang.reflect.Field;

import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.ComboPopup;

import org.andork.func.Predicate;
import org.andork.reflect.ReflectionUtils;
import org.andork.ui.test.DoSwingR;

public interface JComboBoxFixture extends ComponentFixture {
	public static class Common {
		public static String readText(final JComboBox cb, final int index) {
			return new DoSwingR<String>() {
				@Override
				protected String doRun() {
					Object value = cb.getItemAt(index);
					try {
						BasicComboBoxUI ui = (BasicComboBoxUI) cb.getUI();
						Field popupField = ReflectionUtils.getField(ui.getClass(), "popup");
						popupField.setAccessible(true);
						ComboPopup popup = (ComboPopup) popupField.get(ui);
						JList list = popup.getList();
						ListCellRenderer renderer = list.getCellRenderer();
						Component rendComp = renderer.getListCellRendererComponent(popup.getList(), value, index, false,
								false);
						return ComponentFixture.Common.readText(rendComp);
					} catch (Exception ex) {
					}
					return value.toString();
				}
			}.result();
		}
	}

	public void selectItem(JComboBox cb, Object item);

	public void selectItemAtIndex(JComboBox cb, int index);

	public abstract void selectItemByText(JComboBox cb, Predicate<String> p);

	public void selectItemMatching(JComboBox cb, Predicate<Object> p);
}
