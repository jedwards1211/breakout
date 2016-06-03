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
package org.andork.ui.test;

import java.util.Iterator;

import javax.swing.JTable;

public abstract class TableFinder<T extends JTable> extends ComponentFinder<T> {
	static class CastingTableFinder<T extends JTable> extends TableFinder<T> {
		ComponentFinder<T> wrapped;

		public CastingTableFinder(ComponentFinder<T> wrapped) {
			super();
			this.wrapped = wrapped;
		}

		@Override
		public Iterator<T> iterator() {
			return wrapped.iterator();
		}
	}

	public static <T extends JTable> TableFinder<T> cast(ComponentFinder<T> wrapped) {
		return new CastingTableFinder<T>(wrapped);
	}

	protected TableFinder() {
	}

	public TableFinder<T> empty() {
		return cast(new FilteringComponentFinder<T>(this) {
			@Override
			public boolean matches(T table) {
				return table.getRowCount() == 0 || table.getColumnCount() == 0;
			}
		});
	}

	public TableFinder<T> nonEmpty() {
		return cast(new FilteringComponentFinder<T>(this) {
			@Override
			public boolean matches(T table) {
				return table.getRowCount() > 0 && table.getColumnCount() > 0;
			}
		});
	}
}
