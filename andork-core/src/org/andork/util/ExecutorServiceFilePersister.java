package org.andork.util;

import java.io.File;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import org.andork.func.Bimapper;

public class ExecutorServiceFilePersister<M> extends AbstractFilePersister<M> {
	ExecutorService	executor;

	public ExecutorServiceFilePersister(final File file, final Bimapper<M, String> format) {
		this(Executors.newSingleThreadExecutor(new ThreadFactory() {
			@Override
			public Thread newThread(Runnable r) {
				Thread thread = new Thread(r);
				thread.setName("DefaultModelPersister: " + file);
				return thread;
			}
		}), format, file);
	}

	public ExecutorServiceFilePersister(ExecutorService executor, Bimapper<M, String> format, File file) {
		super(file, format);
		this.executor = executor;
	}

	protected void saveInBackground(final LinkedList<M> batch) {
		ExecutorServiceFilePersister.this.executor.submit(new Runnable() {
			@Override
			public void run() {
				try {
					save(batch.getLast());
				} finally {
					ExecutorServiceFilePersister.this.batcher.doneHandling(batch);
				}
			}
		});
	}
}
