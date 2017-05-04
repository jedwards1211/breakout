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

import java.awt.Dimension;

import javax.swing.JScrollPane;

import org.andork.func.Lodash.DebounceOptions;
import org.andork.swing.QuickTestFrame;
import org.andork.task.ExecutorTaskService;
import org.andork.task.Task;
import org.andork.task.TaskService;

public class TaskPaneTest {
	static class TestTask extends Task<Void> {
		public TestTask(String status) {
			setIndeterminate(false);
			setStatus(status);
			setCompleted(0);
			setTotal(100);
		}

		@Override
		protected Void work() throws Exception {
			for (int i = 0; i < 100; i++) {
				setCompleted(i);
				Thread.sleep(100);
			}
			return null;
		}
	}

	public static void main(String[] args) throws Exception {
		TaskService service = ExecutorTaskService.newSingleThreadedTaskService();
		service.setDebounceOptions(new DebounceOptions<Void>().setTimeout(SetTimeout::setTimeout));
		TaskList taskList = new TaskList();
		taskList.addService(service);
		JScrollPane taskListScrollPane = new JScrollPane(taskList);
		taskListScrollPane.setPreferredSize(new Dimension(300, 500));

		QuickTestFrame.frame(taskListScrollPane).setVisible(true);

		for (int i = 0; i < 10; i++) {
			TestTask task = new TestTask("Task " + i);
			service.submit(task);

			Thread.sleep(1000);
		}
	}
}
