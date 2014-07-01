package org.andork.swing.async;

@FunctionalInterface
public interface TaskRunnable
{
	public void execute( Task task ) throws Exception;
}
