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

import org.andork.util.Java7;

public class Subtask {
	public static Subtask defaultCreate(Task parent) {
		return parent != null ? new Subtask(parent) : dummySubtask();
	}

	public static Subtask dummySubtask() {
		return new Subtask(null);
	}

	private Object parent;
	private Subtask child;
	private String status;
	private boolean indeterminate;
	private int completed;

	private int total;

	private int proportion;

	public Subtask() {
		parent = null;
		proportion = 1;
	}

	private Subtask(Subtask parent, int proportion) {
		this.parent = parent;
		this.proportion = proportion;
	}

	public Subtask(Task parent) {
		this.parent = parent;
		proportion = parent.getTotal();
	}

	public Subtask beginSubtask(int proportion) {
		if (proportion < 1) {
			throw new IllegalArgumentException("proportion must be >= 1");
		}

		if (child != null) {
			throw new IllegalStateException("there is an incomplete subtask.");
		}
		return child = new Subtask(this, proportion);
	}

	public void end() {
		if (parent instanceof Subtask) {
			Subtask parentSubtask = (Subtask) parent;
			parentSubtask.child = null;
			parentSubtask.setCompleted(parentSubtask.getCompleted() + proportion);
		}
	}

	public Subtask getChild() {
		return child;
	}

	public int getCompleted() {
		return completed;
	}

	public double getCompletedRecursive() {
		double result = completed;
		if (child != null) {
			result += child.getCompletedRecursive();
		}
		return result * proportion / total;
	}

	public boolean getIndeterminateRecursive() {
		return indeterminate || child != null && child.getIndeterminateRecursive();
	}

	public Object getParent() {
		return parent;
	}

	public int getProportion() {
		return proportion;
	}

	public String getStatus() {
		return status;
	}

	public String getStatusRecursive() {
		String childStatus = child == null ? null : child.getStatusRecursive();
		return status == null ? childStatus : childStatus == null ? status + "..." : status + ": " + childStatus;
	}

	public int getTotal() {
		return total;
	}

	public boolean isCanceling() {
		return parent instanceof Task ? ((Task) parent).isCanceling()
				: parent instanceof Subtask ? ((Subtask) parent).isCanceling() : null;
	}

	public boolean isIndeterminate() {
		return indeterminate;
	}

	public void setCompleted(int completed) {
		if (this.completed != completed) {
			this.completed = completed;
			updateParent();
		}
	}

	public void setIndeterminate(boolean indeterminate) {
		if (this.indeterminate != indeterminate) {
			this.indeterminate = indeterminate;
			updateParent();
		}
	}

	public void setStatus(String status) {
		if (!Java7.Objects.equals(this.status, status)) {
			this.status = status;
			updateParent();
		}
	}

	public void setTotal(int total) {
		if (this.total != total) {
			this.total = total;
			updateParent();
		}
	}

	private void updateParent() {
		if (parent instanceof Task) {
			Task task = (Task) parent;
			String status = getStatusRecursive();
			if (status != null) {
				task.setStatus(status);
			}
			task.setIndeterminate(getIndeterminateRecursive());
			task.setCompleted((int) Math.round(getCompletedRecursive()));
		} else if (parent instanceof Subtask) {
			((Subtask) parent).updateParent();
		}
	}
	
	public void increment() {
		increment(1);
	}

	public void increment(int amount) {
		setCompleted(completed + amount);
	}
}
