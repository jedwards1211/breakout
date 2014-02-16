package org.andork.ui.test;

import java.util.Iterator;

import javax.swing.JTable;

public abstract class TableFinder<T extends JTable> extends ComponentFinder<T> {
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

	public static <T extends JTable> TableFinder<T> cast(ComponentFinder<T> wrapped) {
		return new CastingTableFinder<T>(wrapped);
	}

	static class CastingTableFinder<T extends JTable> extends TableFinder<T> {
		ComponentFinder<T>	wrapped;

		public CastingTableFinder(ComponentFinder<T> wrapped) {
			super();
			this.wrapped = wrapped;
		}

		@Override
		public Iterator<T> iterator() {
			return wrapped.iterator();
		}
	}
}
