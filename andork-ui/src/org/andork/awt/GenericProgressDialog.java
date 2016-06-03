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
package org.andork.awt;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Window;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.border.EmptyBorder;

import org.andork.awt.GridBagWizard.DefaultAutoInsets;

@SuppressWarnings("serial")
public class GenericProgressDialog extends JDialog {
	/**
	 *
	 */
	private static final long serialVersionUID = 2926249540043287549L;
	JLabel statusLabel;
	JProgressBar progressBar;

	public GenericProgressDialog(Window owner) {
		super(owner);
		init();
	}

	public JProgressBar getProgressBar() {
		return progressBar;
	}

	public JLabel getStatusLabel() {
		return statusLabel;
	}

	private void init() {
		setResizable(false);

		JPanel content = new JPanel();
		content.setBorder(new EmptyBorder(15, 15, 15, 15));

		statusLabel = new JLabel();
		progressBar = new JProgressBar();
		progressBar.setPreferredSize(new Dimension(500, 30));

		GridBagWizard g = GridBagWizard.create(content);
		g.defaults().autoinsets(new DefaultAutoInsets(10, 10));
		g.put(statusLabel, progressBar).fillx(1.0).intoColumn();

		getContentPane().add(content, BorderLayout.CENTER);

		pack();
		setLocationRelativeTo(getOwner());
	}

	public void setProgressBar(JProgressBar progressBar) {
		this.progressBar = progressBar;
	}

	public void setStatusLabel(JLabel statusLabel) {
		this.statusLabel = statusLabel;
	}
}
