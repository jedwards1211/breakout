package org.andork.swing.async;

@FunctionalInterface
public interface TaskSupplier<T>
{
	public T get( Task task ) throws Exception;
}
