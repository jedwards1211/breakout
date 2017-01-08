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

import java.awt.event.ActionEvent;
import java.io.File;
import java.nio.file.Path;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.andork.awt.I18n;
import org.andork.awt.I18n.Localizer;
import org.andork.awt.layout.MultilineLabelHolder;
import org.andork.swing.JFileChooserUtils;
import org.andork.swing.OnEDT;
import org.breakout.model.RootModel;

@SuppressWarnings("serial")
public class NewProjectAction extends AbstractAction {
	/**
	 *
	 */
	private static final long serialVersionUID = -8899030390228292424L;

	BreakoutMainView mainView;

	JFileChooser projectFileChooser;

	public NewProjectAction(final BreakoutMainView mainView) {
		super();
		this.mainView = mainView;

		new OnEDT() {
			@Override
			public void run() throws Throwable {
				Localizer localizer = mainView.getI18n().forClass(NewProjectAction.this.getClass());
				localizer.setName(NewProjectAction.this, "name");

				projectFileChooser = new JFileChooser();
				projectFileChooser.setAcceptAllFileFilterUsed(false);
				projectFileChooser.addChoosableFileFilter(new FileNameExtensionFilter(
						"Metacave File (*.mcj, *.json)", "mcj", "json"));
			}
		};
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		I18n i18n = mainView.getI18n();
		Localizer localizer = i18n.forClass(getClass());

		File directory = mainView.getRootModel().get(RootModel.currentProjectFileChooserDirectory);
		if (directory == null) {
			Path currentProjectFile = mainView.getRootModel().get(RootModel.currentProjectFile);
			if (currentProjectFile != null) {
				directory = currentProjectFile.getParent().toFile();
			}
		}
		if (directory != null) {
			projectFileChooser.setCurrentDirectory(directory);
		}

		projectFileChooser.setDialogTitle("Save New Project As");

		File projectFile;

		do {
			int choice = projectFileChooser.showSaveDialog(mainView.getMainPanel());
			projectFile = JFileChooserUtils.correctSelectedFileExtension(projectFileChooser);

			if (choice != JFileChooser.APPROVE_OPTION || projectFile == null) {
				return;
			}

			if (projectFile.exists()) {
				choice = JOptionPane.showConfirmDialog(mainView.getMainPanel(),
						new MultilineLabelHolder(localizer.getFormattedString("projectFileAlreadyExistsDialog.message",
								projectFile.getName())).setWidth(600),
						localizer.getString("projectFileAlreadyExistsDialog.title"),
						JOptionPane.YES_NO_CANCEL_OPTION);
				if (choice == JOptionPane.YES_OPTION) {
					break;
				} else if (choice != JOptionPane.NO_OPTION) {
					return;
				}
			} else {
				break;
			}
		} while (true);

		mainView.getRootModel().set(RootModel.currentProjectFileChooserDirectory,
				projectFileChooser.getCurrentDirectory());

		try {
			projectFile.createNewFile();
		} catch (Exception ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(
					mainView.getMainPanel(),
					new MultilineLabelHolder(localizer.getFormattedString(
							"failedToCreateProjectFileDialog.message",
							projectFile.toString() + "<br><b>" + ex.getClass().getName() + "</b>: "
									+ ex.getLocalizedMessage()))
											.setWidth(600),
					localizer.getString("failedToCreateProjectFileDialog.title"),
					JOptionPane.ERROR_MESSAGE);
			return;
		}

		mainView.openProject(projectFile.toPath());
	}
}
