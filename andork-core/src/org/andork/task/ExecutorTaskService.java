package org.andork.task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.andork.event.BasicPropertyChangeSupport.External;
import org.andork.event.HierarchicalBasicPropertyChangeSupport;
import org.andork.func.Lodash.DebounceOptions;

public class ExecutorTaskService implements TaskService {
	final ExecutorService executor;
	final List<Task<?>> tasks = new CopyOnWriteArrayList<>();
	final List<Task<?>> unmodifiableTasks = Collections.unmodifiableList(tasks);
	final HierarchicalBasicPropertyChangeSupport changeSupport = new HierarchicalBasicPropertyChangeSupport();

	DebounceOptions<Void> debounceOptions;

	public static ExecutorTaskService newSingleThreadedTaskService() {
		return new ExecutorTaskService(Executors.newSingleThreadExecutor());
	}

	public void setDebounceOptions(DebounceOptions<Void> options) {
		debounceOptions = options;
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
	public void submit(Task<?> task) {
		addTask(task);
		task.onceCanceled(() -> {
			if (!task.isRunning()) {
				removeTask(task);
			}
		});
		executor.submit(() -> {
			try {
				return task.call(debounceOptions);
			} catch (TaskCanceledException ex) {
				// ignore
				return null;
			} finally {
				removeTask(task);
			}
		});
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
