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
package org.andork.swing;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JProgressBar;

@SuppressWarnings("serial")
public class SplashFrame extends JFrame {
	private final class Layout implements LayoutManager {

		@Override
		public void addLayoutComponent(String name, Component comp) {
		}

		@Override
		public void layoutContainer(Container parent) {
			Insets insets = imagePanel.getInsets();
			closeButton.setSize(closeButton.getPreferredSize());
			closeButton.setLocation(imagePanel.getWidth() - closeButton.getWidth() - insets.right - 2, insets.top + 2);
			statusLabel.setSize(statusLabel.getPreferredSize());
			statusLabel.setLocation(insets.left + 5,
					imagePanel.getHeight() - statusLabel.getHeight() - insets.bottom - 2);
		}

		@Override
		public Dimension minimumLayoutSize(Container parent) {
			return new Dimension(600, 400);
		}

		@Override
		public Dimension preferredLayoutSize(Container parent) {
			return new Dimension(600, 400);
		}

		@Override
		public void removeLayoutComponent(Component comp) {
		}
	}

	/**
	 *
	 */
	private static final long serialVersionUID = 2904457485554129111L;
	JButton closeButton;
	JLabel statusLabel;
	JProgressBar progressBar;

	SimpleImagePanel imagePanel;

	public SplashFrame() {
		setUndecorated(true);
		closeButton = new JButton();
		ModernStyleClearButton.createClearButton(closeButton);

		statusLabel = new JLabel();
		progressBar = new JProgressBar();

		imagePanel = new SimpleImagePanel();
		imagePanel.add(closeButton);
		imagePanel.add(statusLabel);

		imagePanel.setLayout(new Layout());

		getContentPane().add(imagePanel, BorderLayout.CENTER);
		getContentPane().add(progressBar, BorderLayout.SOUTH);

		setResizable(false);

		closeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
	}

	public JButton getCloseButton() {
		return closeButton;
	}

	public SimpleImagePanel getImagePanel() {
		return imagePanel;
	}

	public JProgressBar getProgressBar() {
		return progressBar;
	}

	public JLabel getStatusLabel() {
		return statusLabel;
	}
}
