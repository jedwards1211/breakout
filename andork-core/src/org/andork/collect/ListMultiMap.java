package org.andork.collect;

import java.util.List;

/**
 * A {@link MultiMap} that stores all values for the same key
 * in a {@link List} in the order they were added, and allows duplicate values.
 * 
 * @author james.a.edwards
 * 
 * @param <K>
 *            the key type.
 * @param <V>
 *            the value type.
 */
public interface ListMultiMap<K, V> extends MultiMap<K, V> {
	/**
	 * @param key
	 *            the key to get values mapped to.
	 * @return all values mapped to {@code key}. This list is not necessarily
	 *         modifiable or backed by the {@code ListMultiMap}.
	 */
	public abstract List<V> get(K key);
}
