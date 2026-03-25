package org.andork.q;

import org.andork.func.Mapper;
import org.andork.model.Cell;

public class QCell<T> extends QElement implements Cell<T> {
	private T value;

	public QCell(T value) {
		this.value = value;
	}

	public T get() {
		dependency.depend();
		return value;
	}

	public void set(T newValue) {
		if (newValue == value) {
			return;
		}
		value = newValue;
		changeSupport.fireChildrenChanged(this);
		dependency.fireChanged();
	}

	/**
	 * Force a change event to be fired, even though the value remains the same.
	 */
	public void fireChanged() {
		dependency.fireChanged();
	}

	@Override
	public QElement deepClone(Mapper<Object, Object> childMapper) {
		dependency.depend();
		return new QCell<>((T) childMapper.map(value));
	}
}
