package org.andork.q;

import org.andork.model.Cell;

public class QCellRef<T> implements Cell<T> {
	Cell<T> cell;
	QDependency dependency = new QDependency();

	public QCellRef() {
	}

	public QCellRef(Cell<T> cell) {
		this.cell = cell;
	}

	public T get() {
		dependency.depend();
		return cell == null ? null : cell.get();
	}

	public void set(T newValue) {
		if (cell != null && newValue != get()) {
			cell.set(newValue);
		}
	}

	public void setCell(Cell<T> newCell) {
		if (cell != newCell) {
			cell = newCell;
			dependency.fireChanged();
		}
	}

	/**
	 * Force a change event to be fired, even though the value remains the same.
	 */
	public void fireChanged() {
		dependency.fireChanged();
	}
}
