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
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.Scrollable;

import org.andork.collect.CollectionUtils;
import org.andork.event.HierarchicalBasicPropertyChangeAdapter;

@SuppressWarnings("serial")
public class TaskList extends JPanel implements Scrollable {
	private class ModelChangeHandler extends HierarchicalBasicPropertyChangeAdapter {
		@Override
		public void childrenChanged(Object source, ChangeType changeType, Object... children) {
			switch (changeType) {
			case ALL_CHILDREN_CHANGED:
				rebuild();
				break;
			case CHILDREN_ADDED:
				for (Object o : children) {
					Task task = (Task) o;
					TaskPane taskPane = new TaskPane(task);
					taskMap.put(task, taskPane);
					add(taskPane);
				}
				revalidate();
				break;
			case CHILDREN_REMOVED:
				for (Object o : children) {
					Task task = (Task) o;
					TaskPane taskPane = taskMap.remove(task);
					if (taskPane != null) {
						taskPane.setTask(null);
						remove(taskPane);
					}
				}
				break;
			}
		}
	}

	/**
	 *
	 */
	private static final long serialVersionUID = -5692418634705030121L;
	final Set<TaskService> services = new HashSet<TaskService>();

	LinkedHashMap<Task, TaskPane> taskMap = CollectionUtils.newLinkedHashMap();

	private ModelChangeHandler modelChangeHandler = new ModelChangeHandler();

	public TaskList() {
		init();
	}

	public void addService(TaskService service) {
		if (services.add(service)) {
			service.changeSupport().addPropertyChangeListener(modelChangeHandler);
			rebuild();
		}
	}

	@Override
	public Dimension getPreferredScrollableViewportSize() {
		return getPreferredSize();
	}

	@Override
	public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean getScrollableTracksViewportHeight() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean getScrollableTracksViewportWidth() {
		return true;
	}

	@Override
	public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
		return 0;
	}

	protected void init() {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
	}

	protected void rebuild() {
		for (TaskPane pane : taskMap.values()) {
			pane.setTask(null);
		}
		removeAll();

		List<Task> tasks = new ArrayList<Task>();

		for (TaskService service : services) {
			tasks.addAll(service.getTasks());
		}

		Collections.sort(tasks, new Comparator<Task>() {
			@Override
			public int compare(Task o1, Task o2) {
				long l = o1.getCreationTimestamp() - o2.getCreationTimestamp();
				return l > 0 ? 1 : l == 0 ? 0 : -1;
			}
		});

		for (Task task : tasks) {
			TaskPane pane = new TaskPane(task);
			taskMap.put(task, pane);
			add(pane);
		}

		revalidate();
	}

	public void removeService(TaskService service) {
		if (services.remove(service)) {
			service.changeSupport().removePropertyChangeListener(modelChangeHandler);
			rebuild();
		}
	}
}
