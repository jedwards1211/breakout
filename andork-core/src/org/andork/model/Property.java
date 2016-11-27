package org.andork.model;

/**
 * Java reflection without actual reflection. With a list of the names, types,
 * getters, and setters of each property of a model class defined by
 * {@code Property}s, you can cut out a lot of boilerplate in things like
 * serialization and deserialization, creating a table model, etc.
 *
 * See {@link DefaultProperty} for an implementation that should be sufficient
 * for basic use cases.
 *
 * @author Andy
 *
 * @param <T>
 *            the model type
 * @param <V>
 *            the type of values for this property
 */
public interface Property<T, V> {
	/**
	 * The name of this property.
	 */
	public String name();

	/**
	 * @return the type of values for this property.
	 */
	public Class<? super V> valueClass();

	/**
	 * @return the value of this property in {@code obj}.
	 */
	public V get(T obj);

	/**
	 * Sets the value of this property in {@code obj}.
	 *
	 * @return the previous value of this property in {@code obj}.
	 */
	public V set(T obj, V value);
}
