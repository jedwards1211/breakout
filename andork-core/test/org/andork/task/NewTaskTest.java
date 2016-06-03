package org.andork.task;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class NewTaskTest {
	public static void main(String[] args) throws InterruptedException, ExecutionException {
		ExecutorService executor = Executors.newSingleThreadExecutor();

		final NewTask<Integer> task = new NewTask<Integer>() {
			@Override
			public Integer doCall() throws Exception {
				setStatus("outer task");

				int k = 0;

				for (int i = 0; i < 100; i++) {
					final int startK = k;
					k = callSubtask(0.01, new NewTask<Integer>() {
						@Override
						public Integer doCall() throws Exception {
							setStatus("inner task " + startK);

							int k = startK;

							for (int j = 0; j < 100; j++) {
								k++;
								setProgress((j + 1) / 100.0);
							}

							return k;
						}
					});

					setProgress((i + 1) / 100.0);
				}

				return k;
			}
		};

		task.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				System.out.println("status: " + task.getCombinedStatus() + ", progress: "
						+ task.getCombinedProgress());
			}
		});

		Integer result = executor.submit(task).get();

		System.out.println("Final result: " + result);
	}
}
