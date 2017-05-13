package org.andork.task;

import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Throttler<V> {
	private final ScheduledExecutorService executor;
	private final long wait;

	private volatile long lastCallTime = 0;
	private volatile Callable<V> nextCallable;
	private volatile Future<V> nextFuture;

	public Throttler(long wait) {
		this(wait, Executors.newSingleThreadScheduledExecutor());
	}

	public Throttler(long wait, ScheduledExecutorService executor) {
		this.executor = executor;
		this.wait = wait;
	}
	
	public Future<V> submit(Callable<V> callable) {
		nextCallable = callable;
		if (nextFuture != null) {
			return nextFuture;
		} else {
			long delay = Math.max(0, lastCallTime + wait - System.currentTimeMillis());
			return nextFuture = executor.schedule(() -> {
				nextFuture = null;
				lastCallTime = System.currentTimeMillis();
				return nextCallable.call();
			}, delay, TimeUnit.MILLISECONDS);
		}
	}
}
