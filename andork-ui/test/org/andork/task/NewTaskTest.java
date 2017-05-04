package org.andork.task;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.andork.func.Lodash;
import org.andork.func.Lodash.DebounceOptions;
import org.andork.swing.async.SetTimeout;

public class NewTaskTest {
	public static void main(String[] args) throws InterruptedException, ExecutionException {
		ExecutorService executor = Executors.newSingleThreadExecutor();

		final Task<Integer> task = new Task<Integer>() {
			@Override
			public Integer work() throws Exception {
				setTotal(100);
				setStatus("outer task");

				int k = 0;

				for (int i = 0; i < 100; i++) {
					final int startK = k;
					k = callSubtask(1, new Task<Integer>() {
						@Override
						public Integer work() throws Exception {
							setTotal(100);
							setStatus("inner task " + startK);

							int k = startK;

							for (int j = 0; j < 100; j++) {
								k++;
								Thread.sleep(1);
								increment();
							}

							return k;
						}
					});
				}

				return k;
			}
		};

		executor.submit(() -> {
			Task.debounceOptions.set(new DebounceOptions<Void>().setTimeout(SetTimeout::setTimeout));
		}).get();
		
		task.addChangeListener(e -> {
			System.out.println(task.getCombinedStatus() + task.getCombinedProgress());
		});

		Integer result = executor.submit(task).get();

		System.out.println("Final result: " + result);
	}
}
