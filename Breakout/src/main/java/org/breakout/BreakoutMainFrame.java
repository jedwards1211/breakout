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
package org.breakout;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.nio.file.Path;

import javax.swing.JFrame;

import org.andork.bind.BinderWrapper;
import org.andork.bind.QObjectAttributeBinder;
import org.breakout.model.RootModel;

public class BreakoutMainFrame extends JFrame {
	private static final long serialVersionUID = -3629909041138921073L;

	private final BreakoutMainView mainView;

	public BreakoutMainFrame(BreakoutMainView breakoutMainView) {
		mainView = breakoutMainView;
		updateTitle();

		new BinderWrapper<Path>() {
			@Override
			protected void onValueChanged(Path newValue) {
				updateTitle();
			}
		}.bind(new QObjectAttributeBinder<>(RootModel.currentProjectFile)
				.bind(breakoutMainView.getRootModelBinder()));

		getContentPane().add(breakoutMainView.getMainPanel(), BorderLayout.CENTER);
		setJMenuBar(breakoutMainView.getMenuBar());
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		setSize(screenSize.width * 2 / 3, screenSize.height * 2 / 3);
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		setLocationRelativeTo(null);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				try {
					breakoutMainView.shutdown();
					dispose();
				} catch (ShutdownCanceledException ex) {
					return;
				} catch (Exception e1) {
					System.exit(1);
					e1.printStackTrace();
				}
				System.exit(0);
			}
		});
	}

	private void updateTitle() {
		Path rootDirectory = mainView.getRootDirectory();
		Path currentProjectFile = mainView.getRootModel() == null
				? null : mainView.getRootModel().get(RootModel.currentProjectFile);
		setTitle(currentProjectFile == null ? "Breakout" : "Breakout - " +
				rootDirectory.resolve(currentProjectFile).normalize().toString());
	}
}
