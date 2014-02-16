package org.andork.collect;

/**
 * A {@link MultiMap} that is quickly invertible.
 * 
 * @author james.a.edwards
 * 
 * @param <K>
 *            the key type.
 * @param <V>
 *            the value type.
 */
public interface InvertibleMultiMap<K, V> extends MultiMap<K, V> {

	/**
	 * @return an {@link InvertibleMultiMap} where the key-value pairs have been
	 *         reversed.
	 */
	public abstract InvertibleMultiMap<V, K> inverse();
}