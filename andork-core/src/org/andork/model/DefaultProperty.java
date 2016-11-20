package org.andork.model;

import java.util.Collections;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class DefaultProperty<T, V> implements Property<T, V> {
	private final String name;
	private final Class<V> valueClass;
	private final Function<? super T, ? extends V> getter;
	private final BiConsumer<? super T, V> setter;
	private final Map<String, Object> metadata;

	public DefaultProperty(String name, Class<V> valueClass, Function<? super T, ? extends V> getter) {
		this(name, valueClass, getter, null, Collections.emptyMap());
	}

	public DefaultProperty(String name, Class<V> valueClass, Function<? super T, ? extends V> getter,
			BiConsumer<? super T, V> setter) {
		this(name, valueClass, getter, setter, Collections.emptyMap());
	}

	public DefaultProperty(String name, Class<V> valueClass, Function<? super T, ? extends V> getter,
			BiConsumer<? super T, V> setter, Map<String, Object> metadata) {
		this.name = name;
		this.valueClass = valueClass;
		this.getter = getter;
		this.setter = setter;
		this.metadata = Collections.unmodifiableMap(metadata);
	}

	@Override
	public String name() {
		return name;
	}

	@Override
	public Class<V> valueClass() {
		return valueClass;
	}

	public Function<? super T, ? extends V> getter() {
		return getter;
	}

	@Override
	public V get(T obj) {
		return getter.apply(obj);
	}

	public BiConsumer<? super T, V> setter() {
		return setter;
	}

	@Override
	public V set(T obj, V value) {
		if (setter == null) {
			throw new UnsupportedOperationException("you may not set this property");
		}
		V prevValue = get(obj);
		setter.accept(obj, value);
		return prevValue;
	}

	@Override
	public Map<String, ?> metadata() {
		return metadata;
	}
}
