package org.andork.collect;

import java.util.Set;

/**
 * A {@link MultiMap} that stores values for the same key in a
 * {@link Set}, and does not store duplicate values.
 * 
 * @author james.a.edwards
 * 
 * @param <K>
 *            the key type.
 * @param <V>
 *            the value type.
 */
public interface SetMultiMap<K, V> extends MultiMap<K, V> {
	/**
	 * @param key
	 *            the key to get values mapped to.
	 * @return all values mapped to {@code key}. This set is not necessarily
	 *         modifiable or backed by the {@code SetMultiMap}.
	 */
	public abstract Set<V> get(K key);
}
