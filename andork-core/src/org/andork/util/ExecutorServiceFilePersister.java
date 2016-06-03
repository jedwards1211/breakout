/*******************************************************************************
 * Breakout Cave Survey Visualizer
 *
 * Copyright (C) 2014 James Edwards
 *
 * jedwards8 at fastmail dot fm
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *******************************************************************************/
package org.andork.util;

import java.io.File;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import org.andork.func.Bimapper;

public class ExecutorServiceFilePersister<M> extends AbstractFilePersister<M> {
	ExecutorService executor;

	public ExecutorServiceFilePersister(ExecutorService executor, Bimapper<M, String> format, File file) {
		super(file, format);
		this.executor = executor;
	}

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

	@Override
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
