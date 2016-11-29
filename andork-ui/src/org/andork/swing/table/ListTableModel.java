package org.andork.swing.table;

import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

import org.andork.model.DefaultProperty;
import org.andork.model.Property;
import org.andork.swing.list.RealListModel;

public class ListTableModel<E> extends AbstractTableModel {
	private class Listener implements ListDataListener {
		@Override
		public void contentsChanged(ListDataEvent e) {
			fireTableRowsUpdated(e.getIndex0(), e.getIndex1());
		}

		@Override
		public void intervalAdded(ListDataEvent e) {
			fireTableRowsInserted(e.getIndex0(), e.getIndex1());
		}

		@Override
		public void intervalRemoved(ListDataEvent e) {
			fireTableRowsDeleted(e.getIndex0(), e.getIndex1());
		}
	}

	public static interface Column<E, V> {
		public Class<? super V> getColumnClass();

		public String getColumnName();

		public V getValueAt(E row);

		public E setValueAt(E row, V value);

		public boolean isCellEditable(E row);
	}

	public static <E, V> Column<E, V> column(Property<E, V> prop) {
		return new ColumnBuilder<E, V>().columnClass(prop.valueClass())
				.columnName(prop.name())
				.getter(e -> prop.get(e))
				.setter((e, v) -> {
					return prop.set(e, v);
				})
				.create();
	}

	public static <E, V> Column<E, V> column(DefaultProperty<E, V> prop) {
		return new ColumnBuilder<E, V>().columnClass(prop.valueClass())
				.columnName(prop.name())
				.getter(prop.getter())
				.setter(prop.setter())
				.create();
	}

	public static class ColumnBuilder<E, V> {
		private Class<? super V> columnClass = Object.class;
		private String columnName = "";
		private Function<? super E, ? extends V> getter;
		private BiFunction<? super E, V, ? extends E> setter;
		private Function<? super E, Boolean> isCellEditable;

		public static <E, V> ColumnBuilder<E, V> create(Property<E, V> prop) {
			return new ColumnBuilder<E, V>().columnClass(prop.valueClass())
					.columnName(prop.name())
					.getter(e -> prop.get(e))
					.setter((e, v) -> {
						return prop.set(e, v);
					});
		}

		public static <E, V> ColumnBuilder<E, V> create(DefaultProperty<E, V> prop) {
			return new ColumnBuilder<E, V>().columnClass(prop.valueClass())
					.columnName(prop.name())
					.getter(prop.getter())
					.setter(prop.setter());
		}

		public ColumnBuilder<E, V> columnClass(Class<? super V> columnClass) {
			this.columnClass = Objects.requireNonNull(columnClass);
			return this;
		}

		public ColumnBuilder<E, V> columnName(String columnName) {
			this.columnName = Objects.requireNonNull(columnName);
			return this;
		}

		public ColumnBuilder<E, V> getter(Function<? super E, ? extends V> getter) {
			this.getter = getter;
			return this;
		}

		public ColumnBuilder<E, V> setter(BiConsumer<? super E, V> setter) {
			this.setter = (e, v) -> {
				setter.accept(e, v);
				return e;
			};
			return this;
		}

		public ColumnBuilder<E, V> setter(BiFunction<? super E, V, ? extends E> setter) {
			this.setter = setter;
			return this;
		}

		public ColumnBuilder<E, V> editable(Function<? super E, Boolean> isCellEditable) {
			this.isCellEditable = isCellEditable;
			return this;
		}

		public Column<E, V> create() {
			return new Column<E, V>() {
				@Override
				public Class<? super V> getColumnClass() {
					return columnClass;
				}

				@Override
				public String getColumnName() {
					return columnName;
				}

				@Override
				public V getValueAt(E row) {
					if (getter == null) {
						return null;
					}
					return getter.apply(row);
				}

				@Override
				public E setValueAt(E row, V value) {
					if (setter == null) {
						return row;
					}
					return setter.apply(row, value);
				}

				@Override
				public boolean isCellEditable(E row) {
					if (setter == null) {
						return false;
					}
					if (isCellEditable == null) {
						return true;
					}
					return isCellEditable.apply(row);
				}
			};
		}
	}

	private static final long serialVersionUID = -3448915340186127126L;

	@SuppressWarnings("unchecked")
	public static <E> List<E> getList(TableModel model) {
		if (model instanceof ListTableModel) {
			return RealListModel.getList(((ListTableModel<E>) model).getListModel());
		}
		return null;
	}

	private final List<? extends Column<? super E, ?>> columns;

	private final ListModel<E> listModel;

	private final Listener listener = new Listener();

	private boolean editable = true;

	public ListTableModel(List<? extends Column<? super E, ?>> columns, List<E> list) {
		this(columns, (ListModel<E>) RealListModel.wrap(list));
	}

	public ListTableModel(List<? extends Column<? super E, ?>> columns, ListModel<E> list) {
		this.columns = Collections.unmodifiableList(columns);
		this.listModel = list;
		list.addListDataListener(listener);
	}

	public List<? extends Column<? super E, ?>> getColumns() {
		return columns; // already unmodifiable
	}

	@Override
	public int getColumnCount() {
		return columns.size();
	}

	public ListModel<E> getListModel() {
		return listModel;
	}

	@Override
	public int getRowCount() {
		return listModel != null ? listModel.getSize() : 0;
	}

	public E getRowAt(int index) {
		return listModel.getElementAt(index);
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		return columns.get(columnIndex).getValueAt(listModel.getElementAt(rowIndex));
	}

	/**
	 * Always returns {@code true}. If you want a cell not to be editable, make
	 * sure its
	 * {@linkplain ListTableColumn#editor(javax.swing.table.TableCellEditor)
	 * column editor} is {@code null}.
	 */
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		if (!editable) {
			return false;
		}
		return columns.get(columnIndex).isCellEditable(listModel.getElementAt(rowIndex));
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		if (!isCellEditable(rowIndex, columnIndex)) {
			throw new IllegalArgumentException(
					"cell at row " + rowIndex + ", column " + columnIndex + " is not editable");
		}
		E oldRow = listModel.getElementAt(rowIndex);
		E newRow = (E) ((Column) columns.get(columnIndex)).setValueAt(oldRow, aValue);
		if (newRow != oldRow) {
			getList(this).set(rowIndex, newRow);
		}
	}

	public void updateRows(Function<E, E> updater) {
		listModel.removeListDataListener(listener);
		try {
			List<E> rows = getList(this);
			ListIterator<E> i = rows.listIterator();
			while (i.hasNext()) {
				i.set(updater.apply(i.next()));
			}
			fireTableDataChanged();
		} finally {
			listModel.addListDataListener(listener);
		}
	}

}
