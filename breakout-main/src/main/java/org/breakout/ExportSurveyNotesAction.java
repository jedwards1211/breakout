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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import org.andork.awt.I18n;
import org.andork.awt.I18n.Localizer;
import org.andork.swing.OnEDT;
import org.andork.swing.async.SelfReportingTask;
import org.breakout.model.ProjectModel;
import org.breakout.model.calc.CalcTrip;

public class ExportSurveyNotesAction extends AbstractAction {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7127888643288579381L;

	private static final Logger logger = Logger.getLogger(ExportSurveyNotesAction.class.getName());

	BreakoutMainView mainView;

	public ExportSurveyNotesAction(final BreakoutMainView mainView) {
		super();
		this.mainView = mainView;

		new OnEDT() {
			@Override
			public void run() throws Throwable {
				Localizer localizer = mainView.getI18n().forClass(ExportSurveyNotesAction.this.getClass());
				localizer.setName(ExportSurveyNotesAction.this, "name");
			}
		};
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		I18n i18n = mainView.getI18n();
		Localizer localizer = i18n.forClass(getClass());

		JFileChooser outputDirChooser = mainView.fileChooser(ProjectModel.exportSurveyNotesDirectory, null);
		outputDirChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		outputDirChooser.setDialogTitle(localizer.getFormattedString("outputDirChooser.dialogTitle"));

		int choice = outputDirChooser.showSaveDialog(mainView.getMainPanel());
		if (choice != JFileChooser.APPROVE_OPTION) {
			return;
		}
		File outputDir = outputDirChooser.getSelectedFile();

		final Path finalOutputDir = outputDir.toPath();
		mainView.ioTaskService.submit(new SelfReportingTask<Void>(mainView.mainPanel) {
			@Override

			protected Void workDuringDialog() throws Exception {
				try {
					Set<CalcTrip> trips = mainView.getSelectedCalcTrips();
					for (CalcTrip trip : trips) {
						if (trip.attachedFiles == null || trip.attachedFiles.isEmpty())
							continue;

						String tripName = (trip.name == null ? "" : trip.name).replaceAll("\\.$", "").trim();
						String summary = SummarizeTripStations.summarizeTripStations(trip);

						Set<String> attachedFiles = new HashSet<>(trip.attachedFiles);
						List<Object> found = mainView.findAttachedFiles(attachedFiles, this);

						forEach(found, item -> {
							if (item instanceof Path) {
								Path path = (Path) item;
								setStatus("Copying " + path.getFileName() + "...");
								String fileName = summary + " " + tripName;
								if (found.size() > 1) {
									fileName += " " + path.getFileName().toString();
								}
								else {
									Matcher m =
										Pattern.compile("\\.[^.\\\\/]+$").matcher(path.getFileName().toString());
									if (m.find())
										fileName += m.group();
								}
								Files
									.copy(
										path,
										finalOutputDir.resolve(Paths.get(fileName)),
										StandardCopyOption.REPLACE_EXISTING,
										StandardCopyOption.COPY_ATTRIBUTES);
							}
						});
					}
				}
				catch (Exception ex) {
					String message = localizer.getFormattedString("exportFailedMessage");
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
