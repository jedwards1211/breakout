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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog.ModalityType;
import java.awt.Window;

import javax.swing.JDialog;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import org.andork.task.Task;
import org.andork.task.TaskCallable;
import org.andork.task.TaskRunnable;
import org.andork.util.JavaScript;

public abstract class SelfReportingTask<R> extends Task<R> {
	protected Component dialogParent;
	protected JDialog dialog;

	public SelfReportingTask(Component dialogParent) {
		super();
		this.dialogParent = dialogParent;
	}

	public SelfReportingTask(String status, Component dialogParent) {
		super();
		setStatus(status);
		this.dialogParent = dialogParent;
	}
	
	public static void callSelfReportingSubtask(Task<?> parent, int proportion, Component dialogParent, TaskRunnable runnable) throws Exception {
		parent.callSubtask(proportion, new SelfReportingTask<Void>(dialogParent) {
			@Override
			protected Void workDuringDialog() throws Exception {
				runnable.work(this);
				return null;
			}
		});
	}
	
	public static <R> R callSelfReportingSubtask(Task<?> parent, int proportion, Component dialogParent, TaskCallable<R> callable) throws Exception {
		return parent.callSubtask(proportion, new SelfReportingTask<R>(dialogParent) {
			@Override
			protected R workDuringDialog() throws Exception {
				return callable.work(this);
			}
		});
	}

	protected JDialog createDialog(final Window owner) {
		TaskPane taskPane = new TaskPane(SelfReportingTask.this);
		JDialog dialog = new JDialog(owner);
		dialog.setModalityType(ModalityType.DOCUMENT_MODAL);
		dialog.getContentPane().add(taskPane, BorderLayout.CENTER);
		dialog.pack();
		dialog.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		return dialog;
	}

	protected abstract R workDuringDialog() throws Exception;

	@Override
	protected R work() throws Exception {
		showDialogLater();

		try {
			return workDuringDialog();
		} finally {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					dialog.dispose();
				}
			});
		}
	}

	public void showDialogLater() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				Window owner = null;
				if (dialogParent instanceof Window) {
					owner = (Window) dialogParent;
				} else if (dialogParent != null) {
					owner = SwingUtilities.getWindowAncestor(dialogParent);
				}
				if (dialog == null) {
					dialog = createDialog(owner);
				}
				if (!dialog.isVisible()) {
					dialog.setLocationRelativeTo(owner);
					dialog.setVisible(true);
				}
			}
		});
	}
	
	public static Component getDialogParent(Task<?> task) {
		if (task instanceof SelfReportingTask) {
			return JavaScript.or(((SelfReportingTask<?>) task).dialog, ((SelfReportingTask<?>) task).dialogParent);
		}
		return null;
	}
}
