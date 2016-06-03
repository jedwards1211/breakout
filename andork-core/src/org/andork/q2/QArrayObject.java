package org.andork.q2;

import java.util.function.Supplier;

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
public final class QArrayObject<S extends QSpec> extends QObject<S> {
	public static <S extends QSpec> QArrayObject<S> create(S spec) {
		return new QArrayObject<S>(spec);
	}

	Object[] values;

	public QArrayObject(S spec) {
		super(spec);
		values = new Object[spec.properties.length];
		for (int i = 0; i < values.length; i++) {
			Supplier<?> supplier = spec.properties[i].initValue();
			values[i] = supplier == null ? null : supplier.get();
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T doGet(Property<T> property) {
		return (T) values[property.index];
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T doSet(Property<T> property, T newValue) {
		T oldValue = (T) values[property.index];
		if (!property.equals(oldValue, newValue)) {
			values[property.index] = newValue;
			fireObjectChanged(property, oldValue, newValue);
		}
		return oldValue;
	}
}
