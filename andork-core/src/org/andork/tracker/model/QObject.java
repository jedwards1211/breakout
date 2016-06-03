package org.andork.tracker.model;

import org.andork.tracker.model.QSpec.Property;

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
public abstract class QObject<S extends QSpec> {
	final S spec;
	final KeyedDependency<Property<?>> deps = new KeyedDependency<>();

	public QObject(S spec) {
		this.spec = spec;
	}

	public S spec() {
		return spec;
	}

	protected abstract <T> T doGet(Property<T> property);

	protected abstract <T> T doSet(Property<T> property, T newValue);

	public <T> T get(Property<T> property) {
		deps.depend(property);
		return property.get(this);
	}

	public <T> T set(Property<T> property, T newValue) {
		T oldValue = property.set(this, newValue);
		if (oldValue != newValue) {
			deps.changed(property);
		}
		return oldValue;
	}

	public boolean equals(Object other) {
		deps.depend();
		return spec.equals(this, other);
	}

	public int hashCode() {
		deps.depend();
		return spec.hashCode(this);
	}
}
