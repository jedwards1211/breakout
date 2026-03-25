package org.andork.q;

import org.andork.model.Cell;
import org.andork.q.QSpec.Attribute;

public class QObjectCell<S extends QSpec<S>> implements Cell<QObject<S>> {
	private Cell<QObject<S>> wrapped;

	public QObjectCell(Cell<QObject<S>> wrapped) {
		this.wrapped = wrapped;
	}

	public QObjectCell(QObject<S> value) {
		this.wrapped = new QCell<>(value);
	}

	/**
	 * If the wrapped Cell is a QCell, calls fireChanged on it.
	 */
	public void fireChanged() {
		if (wrapped instanceof QCell<?>) {
			((QCell<?>) wrapped).fireChanged();
		}
	}

	public QObject<S> get() {
		return wrapped.get();
	}

	public void set(QObject<S> newValue) {
		wrapped.set(newValue);
	}

	public <T> T get(Attribute<T> attribute) {
		QObject<S> value = get();
		return value == null ? null : value.get(attribute);
	}

	public <T> void set(Attribute<T> attribute, T value) {
		QObject<S> obj = get();
		if (obj != null)
			obj.set(attribute, value);
	}

	public <T> Cell<T> attribute(Attribute<T> attribute) {
		return Cell.from(() -> {
			QObject<S> root = get();
			return root == null ? null : root.attribute(attribute);
		});
	}

	public <T extends QSpec<T>> QObjectCell<T> objectAttribute(Attribute<QObject<T>> attribute) {
		return new QObjectCell<>(attribute(attribute));
	}
}
