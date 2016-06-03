package org.andork.q2;

import org.andork.q2.QSpec.Property;

/**
 * {@code QObject} (along with {@link QSpec}) provides the closest functionality
 * to reflection on a POJO that is possible without actually using reflection.
 * It also provides property {@linkplain #changeSupport() change support} that
 * will notify listeners of any property changes, so that you don't have to
 * write any boilerplate property change notification code.<br>
 * <br>
 * The Q doesn't stand for anything.
 *
 * @author andy.edwards
 * @param <S>
 *            the type of {@link QSpec} for this object.
 */
public abstract class QObject<S extends QSpec> extends QElement {
	S spec;

	public QObject(S spec) {
		this.spec = spec;
	}

	public void addListener(QObjectListener listener) {
		super.addListener(listener);
	}

	protected abstract <T> T doGet(Property<T> property);

	protected abstract <T> T doSet(Property<T> property, T newValue);

	@Override
	public boolean equals(Object other) {
		return spec.equals(this, other);
	}

	protected void fireObjectChanged(Property<?> property, Object oldValue, Object newValue) {
		fireObjectChanged(property, oldValue, newValue, -1);
	}

	protected void fireObjectChanged(Property<?> property, Object oldValue, Object newValue, int index) {
		forEachListener(QObjectListener.class,
				l -> l.objectChanged(this, property, oldValue, newValue));
	}

	public <T> T get(Property<T> property) {
		return property.get(this);
	}

	@Override
	public int hashCode() {
		return spec.hashCode(this);
	}

	public void removeListener(QObjectListener listener) {
		super.removeListener(listener);
	}

	public <T> T set(Property<T> property, T newValue) {
		return property.set(this, newValue);
	}

	public S spec() {
		return spec;
	}
}
