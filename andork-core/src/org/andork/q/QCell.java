package org.andork.q;

import org.andork.func.Mapper;

public class QCell<T> extends QElement {
	private T value;

	QCell(T value) {
		this.value = value;
	}

	T get() {
		dependency.depend();
		return value;
	}

	void set(T newValue) {
		if (newValue == value) {
			return;
		}
		value = newValue;
		changeSupport.fireChildrenChanged(this);
		dependency.fireChanged();
	}

	@Override
	public QElement deepClone(Mapper<Object, Object> childMapper) {
		dependency.depend();
		return new QCell<>((T) childMapper.map(value));
	}
}
