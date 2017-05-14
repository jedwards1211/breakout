package org.andork.task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.andork.event.BasicPropertyChangeSupport.External;
import org.andork.event.HierarchicalBasicPropertyChangeSupport;
import org.andork.func.Lodash.DebounceOptions;

public class ExecutorTaskService implements TaskService {
	final ExecutorService executor;
	final List<Task<?>> tasks = new CopyOnWriteArrayList<>();
	final List<Task<?>> unmodifiableTasks = Collections.unmodifiableList(tasks);
	final HierarchicalBasicPropertyChangeSupport changeSupport = new HierarchicalBasicPropertyChangeSupport();

	public static ExecutorTaskService newSingleThreadedTaskService() {
		return new ExecutorTaskService(Executors.newSingleThreadExecutor());
	}

	@Override
	public External changeSupport() {
		return changeSupport.external();
	}

	public ExecutorTaskService(ExecutorService executor) {
		super();
		this.executor = executor;
	}

	@Override
	public List<Task<?>> getTasks() {
		return unmodifiableTasks;
	}

	@Override
	public boolean hasTasks() {
		return !tasks.isEmpty();
	}

	void addTask(Task<?> task) {
		tasks.add(task);
		changeSupport.fireChildAdded(this, task);
	}

	void removeTask(Task<?> task) {
		tasks.remove(task);
		changeSupport.fireChildRemoved(this, task);
	}

	@Override
	public <V> Future<V> submit(Task<V> task) {
		addTask(task);
		final Future<V> future = executor.submit(() -> {
			try {
				return task.call();
			} finally {
				removeTask(task);
			}
		});
		task.onceCanceled(() -> {
			if (future != null) future.cancel(false);
			if (!task.isRunning()) {
				removeTask(task);
			}
		});
		return future;
	}

	@Override
	public void shutdown() {
		executor.shutdown();
	}

	@Override
	public boolean awaitTermination(long timeout, TimeUnit timeUnit) throws InterruptedException {
		return executor.awaitTermination(timeout, timeUnit);
	}

	@Override
	public List<Task<?>> shutdownNow() {
		executor.shutdownNow();
		List<Task<?>> result = new ArrayList<>(tasks);
		tasks.clear();
		changeSupport.fireChildrenChanged(this);
		return result;
	}

}
