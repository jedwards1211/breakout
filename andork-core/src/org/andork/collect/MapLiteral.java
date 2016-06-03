package org.andork.collect;

import java.util.LinkedHashMap;

@SuppressWarnings("serial")
public class MapLiteral<K, V> extends LinkedHashMap<K, V> {
	/**
	 *
	 */
	private static final long serialVersionUID = 587327688999384531L;

	public static <K, V> MapLiteral<K, V> create() {
		return new MapLiteral<>();
	}

	public MapLiteral<K, V> map(K key, V value) {
		put(key, value);
		return this;
	}
}
