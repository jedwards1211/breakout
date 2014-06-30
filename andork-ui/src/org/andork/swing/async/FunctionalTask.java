package org.andork.swing.async;

@FunctionalInterface
public interface FunctionalTask
{
	public void execute( Task task ) throws Exception;
}
