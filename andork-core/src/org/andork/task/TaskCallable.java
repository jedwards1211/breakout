package org.andork.task;

@FunctionalInterface
public interface TaskCallable<R> {
	public R work(Task<R> task) throws Exception;
}
