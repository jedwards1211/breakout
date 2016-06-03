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
package org.andork.swing.async;

import java.io.File;
import java.util.LinkedList;

import org.andork.func.Bimapper;
import org.andork.func.StreamBimapper;
import org.andork.util.AbstractFilePersister;

public class TaskServiceFilePersister<M> extends AbstractFilePersister<M> {
	TaskService service;
	String description;

	public TaskServiceFilePersister(TaskService service, String description, Bimapper<M, String> format, File file) {
		super(file, format);
		this.service = service;
		this.description = description;
	}

	public TaskServiceFilePersister(TaskService service, String description, StreamBimapper<M> bimapper, File file) {
		super(file, bimapper);
		this.service = service;
		this.description = description;
	}

	@Override
	protected void saveInBackground(final LinkedList<M> batch) {
		Task task = new Task(description) {
			@Override
			protected void execute() throws Exception {
				try {
					save(batch.getLast());
				} finally {
					TaskServiceFilePersister.this.batcher.doneHandling(batch);
				}
			}
		};
		service.submit(task);
	}
}
