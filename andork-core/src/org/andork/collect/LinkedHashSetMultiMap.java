package org.andork.collect;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * A {@link SetMultiMap} that stores all values for the same key in a
 * {@link LinkedHashSet}. In the future, it may preserve the order in which
 * <i>all</i> key-value pairs were added; currently, it just preserves addition
 * order among pairs with the same key.
 * 
 * @author james.a.edwards
 * 
 * @param <K>
 *            the key type.
 * @param <V>
 *            the value type.
 */
public class LinkedHashSetMultiMap<K, V> extends AbstractSetMultiMap<K, V> implements SetMultiMap<K, V> {
	public static <K, V> LinkedHashSetMultiMap<K, V> newInstance() {
		return new LinkedHashSetMultiMap<K, V>();
	}

	@Override
	protected Set<V> createValueSet() {
		return new LinkedHashSet<V>();
	}

	@Override
	protected Map<K, Set<V>> createKeyMap() {
		return new LinkedHashMap<K, Set<V>>();
	}
}
