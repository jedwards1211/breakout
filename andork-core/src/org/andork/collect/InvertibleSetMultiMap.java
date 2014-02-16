package org.andork.collect;

public interface InvertibleSetMultiMap<K, V> extends InvertibleMultiMap<K, V>, SetMultiMap<K, V> {
	/**
	 * @return an {@link InvertibleSetMultiMap} where the key-value pairs have been
	 *         reversed.
	 */
	public abstract InvertibleSetMultiMap<V, K> inverse();
}
