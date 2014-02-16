package org.andork.collect;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * A {@link SetMultiMap} that stores all values for the same key in a
 * {@link HashSet}.
 * 
 * @author james.a.edwards
 * 
 * @param <K>
 *            the key type.
 * @param <V>
 *            the value type.
 */
public class HashSetMultiMap<K, V> extends AbstractSetMultiMap<K, V> implements SetMultiMap<K, V> {
	public static <K, V> HashSetMultiMap<K, V> newInstance() {
		return new HashSetMultiMap<K, V>();
	}

	@Override
	protected Set<V> createValueSet() {
		return new HashSet<V>();
	}

	@Override
	protected Map<K, Set<V>> createKeyMap() {
		return new HashMap<K, Set<V>>();
	}
}
