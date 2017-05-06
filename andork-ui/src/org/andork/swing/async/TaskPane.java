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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeListener;

import org.andork.awt.GridBagWizard;
import org.andork.awt.GridBagWizard.DefaultAutoInsets;
import org.andork.task.Task;

public class TaskPane extends JPanel {
	/**
	 *
	 */
	private static final long serialVersionUID = 6326522185648746138L;

	private final ChangeListener changeHandler = e -> modelToView();

	Task<?> task;
	JLabel statusLabel;
	JProgressBar progressBar;

	JButton cancelButton;

	public TaskPane() {
		init();
	}

	public TaskPane(Task<?> child) {
		this();
		setTask(child);
	}

	public Task<?> getTask() {
		return task;
	}

	protected void init() {
		statusLabel = new JLabel();
		progressBar = new JProgressBar();
		cancelButton = new JButton("Cancel");

		progressBar.setPreferredSize(new Dimension(400, cancelButton.getPreferredSize().height));

		cancelButton.addActionListener(e -> {
			if (task != null) {
				task.cancel();
			}
		});

		setBorder(new EmptyBorder(10, 10, 10, 10));

		GridBagWizard gbw = GridBagWizard.create(this);

		gbw.defaults().autoinsets(new DefaultAutoInsets(5, 5));
		gbw.put(progressBar).xy(0, 1).north().fillboth(1.0, 0.0);
		gbw.put(cancelButton).rightOf(progressBar).northwest().filly(0.0);
		gbw.put(statusLabel).above(progressBar, cancelButton).southwest();

		modelToView();
	}

	protected void modelToView() {
		statusLabel.setText(task == null ? null : task.getCombinedStatus());
		double progress = task == null ? 0 : task.getCombinedProgress();
		progressBar.setIndeterminate(task == null ? true : !Double.isFinite(progress));
		progressBar.setMaximum(task == null ? 0 : 1000000);
		progressBar.setValue(task == null ? 0 : (int) Math.floor(progress * 1000000));
		cancelButton.setEnabled(task == null ? false : !task.isCanceled());
		cancelButton.setText(task != null && task.isCanceled() ? "Canceling..." : "Cancel");
	}

	public void setTask(Task<?> task) {
		if (this.task != task) {
			if (this.task != null) {
				this.task.removeChangeListener(changeHandler);
			}
			this.task = task;
			if (task != null) {
				task.addChangeListener(changeHandler);
			}

			modelToView();
		}
	}
}
