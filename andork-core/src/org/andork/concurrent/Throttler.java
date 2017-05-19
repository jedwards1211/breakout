package org.andork.concurrent;

import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Throttler<V> {
	@FunctionalInterface
	public static interface Scheduler<V> {
		public Future<V> schedule(Callable<V> callable, long delay);
	}

	private final Scheduler<V> scheduler;
	private final long wait;

	private final Object lock = new Object();
	private volatile long lastCallTime = 0;
	private volatile Callable<V> nextCallable;
	private volatile Future<V> nextFuture;

	public Throttler(long wait) {
		this(wait, Executors.newSingleThreadScheduledExecutor());
	}

	public Throttler(long wait, Scheduler<V> scheduler) {
		this.wait = wait;
		this.scheduler = scheduler;
	}

	public Throttler(long wait, ScheduledExecutorService executor) {
		this.wait = wait;
		this.scheduler = (callable, delay) -> executor.schedule(callable, delay, TimeUnit.MILLISECONDS);
	}

	public Future<V> submit(Callable<V> callable) {
		synchronized (lock) {
			nextCallable = callable;
			if (nextFuture != null) {
				return nextFuture;
			} else {
				long delay = Math.max(0, lastCallTime + wait - System.currentTimeMillis());
				return nextFuture = scheduler.schedule(() -> {
					Callable<V> nextCallable;
					synchronized (lock) {
						nextCallable = Throttler.this.nextCallable;
						nextFuture = null;
						lastCallTime = System.currentTimeMillis();
					}
					return nextCallable.call();
				}, delay);
			}
		}
	}
}
