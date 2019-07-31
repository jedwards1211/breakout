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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.andork.awt.I18n;
import org.andork.awt.I18n.Localizer;
import org.andork.awt.layout.MultilineLabelHolder;
import org.andork.swing.JFileChooserUtils;
import org.andork.swing.OnEDT;
import org.andork.swing.async.SelfReportingTask;
import org.andork.task.Task;
import org.breakout.io.stl.AsciiStlExporter;
import org.breakout.io.stl.BinaryStlExporter;
import org.breakout.model.ProjectModel;
import org.breakout.model.calc.CalcProject;

public class ExportStlAction extends AbstractAction {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7127888643288579381L;

	private static final Logger logger = Logger.getLogger(ExportStlAction.class.getName());

	BreakoutMainView mainView;
	Mode mode;

	public static enum Mode {
		Binary {
			@Override
			public void write(CalcProject project, OutputStream out, Task<?> task) throws IOException {
				BinaryStlExporter.write(project, out, task);
			}
		},
		ASCII {
			@Override
			public void write(CalcProject project, OutputStream out, Task<?> task) throws IOException {
				AsciiStlExporter.write(project, out, task);
			}
		};

		public abstract void write(CalcProject project, OutputStream out, Task<?> task) throws IOException;
	}

	public ExportStlAction(final BreakoutMainView mainView, Mode mode) {
		super();
		this.mainView = mainView;
		this.mode = mode;

		new OnEDT() {
			@Override
			public void run() throws Throwable {
				Localizer localizer = mainView.getI18n().forClass(ExportStlAction.this.getClass());
				localizer
					.register(
						ExportStlAction.this,
						(Localizer l, Action action) -> action
							.putValue(Action.NAME, l.getFormattedString("name", mode.name())));
			}
		};
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		I18n i18n = mainView.getI18n();
		Localizer localizer = i18n.forClass(getClass());

		JFileChooser outputFileChooser = mainView.fileChooser(ProjectModel.exportSTLDirectory);
		outputFileChooser.setAcceptAllFileFilterUsed(false);
		outputFileChooser.addChoosableFileFilter(new FileNameExtensionFilter("Stereolithograhy (*.stl)", "stl"));
		outputFileChooser.setDialogTitle(localizer.getFormattedString("outputFileChooser.dialogTitle", mode.name()));

		File outputFile = null;

		while (outputFile == null) {
			int choice = outputFileChooser.showSaveDialog(mainView.getMainPanel());
			outputFile = JFileChooserUtils.correctSelectedFileExtension(outputFileChooser);

			if (choice != JFileChooser.APPROVE_OPTION || outputFile == null) {
				return;
			}

			if (outputFile.exists()) {
				choice =
					JOptionPane
						.showConfirmDialog(
							mainView.getMainPanel(),
							new MultilineLabelHolder(
								localizer
									.getFormattedString("outputFileAlreadyExistsDialog.message", outputFile.getName()))
										.preferredWidth(400),
							localizer.getFormattedString("outputFileAlreadyExistsDialog.title", mode.name()),
							JOptionPane.YES_NO_CANCEL_OPTION);
				switch (choice) {
				case JOptionPane.NO_OPTION:
					outputFile = null;
					break;
				case JOptionPane.CANCEL_OPTION:
					return;
				}
			}
		}

		final File finalOutputFile = outputFile;
		mainView.ioTaskService.submit(new SelfReportingTask<Void>(mainView.mainPanel) {
			@Override

			protected Void workDuringDialog() throws Exception {
				setStatus(localizer.getFormattedString("exportingToStatus", finalOutputFile));
				try (OutputStream out = new FileOutputStream(finalOutputFile)) {
					mode.write(mainView.calcProject, out, this);
				}
				catch (IOException ex) {
					String message = localizer.getFormattedString("exportFailedMessage", finalOutputFile.toString());
					logger.log(Level.SEVERE, message, ex);

					OnEDT.onEDT(() -> {
						JOptionPane
							.showMessageDialog(
								mainView.getMainPanel(),
								message + ": " + ex.getLocalizedMessage(),
								"Error",
								JOptionPane.ERROR_MESSAGE);
					});
				}
				return null;
			}
		});
	}
}
