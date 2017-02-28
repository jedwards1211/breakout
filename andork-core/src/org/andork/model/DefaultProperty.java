package org.andork.model;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

public class DefaultProperty<T, V> implements Property<T, V> {
	private final String name;
	private final Class<? super V> valueClass;
	private final Function<? super T, ? extends V> getter;
	private final BiFunction<? super T, V, ? extends T> setter;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public DefaultProperty(String name, Class<? super V> valueClass, Function<? super T, ? extends V> getter) {
		this(name, valueClass, getter, (BiFunction) null);
	}

	public DefaultProperty(String name, Class<? super V> valueClass, Function<? super T, ? extends V> getter,
			BiConsumer<? super T, V> setter) {
		this(name, valueClass, getter, (t, v) -> {
			setter.accept(t, v);
			return t;
		});
	}

	public DefaultProperty(String name, Class<? super V> valueClass, Function<? super T, ? extends V> getter,
			BiFunction<? super T, V, ? extends T> setter) {
		this.name = name;
		this.valueClass = valueClass;
		this.getter = getter;
		this.setter = setter;
	}

	@Override
	public String name() {
		return name;
	}

	@Override
	public Class<? super V> valueClass() {
		return valueClass;
	}

	public Function<? super T, ? extends V> getter() {
		return getter;
	}

	@Override
	public V get(T obj) {
		return getter.apply(obj);
	}

	public BiFunction<? super T, V, ? extends T> setter() {
		return setter;
	}

	@Override
	public T set(T obj, V value) {
		if (setter == null) {
			throw new UnsupportedOperationException("you may not set this property");
		}
		return setter.apply(obj, value);
	}

	@Override
	public String toString() {
		return name;
	}
}
