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
package org.andork.ui.test.fixture;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.text.JTextComponent;

import org.andork.swing.DoSwingR2;

public interface ComponentFixture {
	public void click(Component comp);

	public static class Common {
		public static String readText(final Component comp) {
			return new DoSwingR2<String>() {
				@Override
				protected String doRun() {
					if (comp instanceof JLabel) {
						return ((JLabel) comp).getText();
					} else if (comp instanceof JTextComponent) {
						return ((JTextComponent) comp).getText();
					}
					throw new IllegalArgumentException("Can't read text of: " + comp);
				}
			}.result();
		}
	}
}
