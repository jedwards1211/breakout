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

import static org.andork.math3d.Vecmath.newMat4f;
import static org.andork.util.JavaScript.falsy;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Desktop;
import java.awt.Dialog.ModalityType;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.ItemEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import javax.swing.CellEditor;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableModel;

import org.andork.awt.I18n;
import org.andork.awt.I18n.I18nUpdater;
import org.andork.awt.I18n.Localizer;
import org.andork.awt.anim.Animation;
import org.andork.awt.anim.AnimationQueue;
import org.andork.awt.event.MouseAdapterChain;
import org.andork.awt.event.MouseAdapterWrapper;
import org.andork.awt.layout.DelegatingLayoutManager;
import org.andork.awt.layout.Drawer;
import org.andork.awt.layout.DrawerAutoshowController;
import org.andork.awt.layout.Side;
import org.andork.awt.layout.SideConstraint;
import org.andork.awt.layout.SideConstraintLayoutDelegate;
import org.andork.bind.Binder;
import org.andork.bind.BinderWrapper;
import org.andork.bind.DefaultBinder;
import org.andork.bind.HierarchicalChangeBinder;
import org.andork.bind.QMapKeyedBinder;
import org.andork.bind.QObjectAttributeBinder;
import org.andork.bind.ui.ButtonSelectedBinder;
import org.andork.collect.CollectionUtils;
import org.andork.compass.CompassParseError;
import org.andork.compass.plot.BeginSectionCommand;
import org.andork.compass.plot.CompassPlotCommand;
import org.andork.compass.plot.CompassPlotParser;
import org.andork.compass.plot.DrawSurveyCommand;
import org.andork.compass.project.CompassProject;
import org.andork.compass.project.CompassProjectParser;
import org.andork.compass.survey.CompassSurveyParser;
import org.andork.compass.survey.CompassTrip;
import org.andork.event.BasicPropertyChangeListener;
import org.andork.event.SourcePath;
import org.andork.func.Bimapper;
import org.andork.func.ExceptionRunnable;
import org.andork.func.FloatUnaryOperator;
import org.andork.func.Lodash;
import org.andork.func.Lodash.DebounceOptions;
import org.andork.func.Lodash.DebouncedRunnable;
import org.andork.jogl.AutoClipOrthoProjection;
import org.andork.jogl.DefaultJoglRenderer;
import org.andork.jogl.GL3Framebuffer;
import org.andork.jogl.InterpolationProjection;
import org.andork.jogl.JoglBackgroundColor;
import org.andork.jogl.JoglScene;
import org.andork.jogl.JoglViewSettings;
import org.andork.jogl.JoglViewState;
import org.andork.jogl.PerspectiveProjection;
import org.andork.jogl.Projection;
import org.andork.jogl.awt.JoglOrbiter;
import org.andork.jogl.awt.JoglOrthoNavigator;
import org.andork.jogl.awt.anim.GeneralViewXformOrbitAnimation;
import org.andork.jogl.awt.anim.ProjXformAnimation;
import org.andork.jogl.awt.anim.RandomViewOrbitAnimation;
import org.andork.jogl.awt.anim.SpringViewOrbitAnimation;
import org.andork.jogl.awt.anim.ViewXformAnimation;
import org.andork.math.misc.Fitting;
import org.andork.math3d.Fitting3d;
import org.andork.math3d.FittingFrustum;
import org.andork.math3d.LinePlaneIntersection3f;
import org.andork.math3d.PickXform;
import org.andork.math3d.PlanarHull3f;
import org.andork.math3d.Vecmath;
import org.andork.q.QArrayList;
import org.andork.q.QLinkedHashMap;
import org.andork.q.QMap;
import org.andork.q.QObject;
import org.andork.q.QSpec;
import org.andork.spatial.RNode;
import org.andork.spatial.RTraversal;
import org.andork.spatial.Rectmath;
import org.andork.swing.AnnotatingRowSorter;
import org.andork.swing.FromEDT;
import org.andork.swing.OnEDT;
import org.andork.swing.SmartComboTableRowFilter;
import org.andork.swing.async.DrawerPinningTask;
import org.andork.swing.async.SelfReportingTask;
import org.andork.swing.async.SetTimeout;
import org.andork.swing.async.TaskList;
import org.andork.swing.table.AnnotatingJTable;
import org.andork.swing.table.AnnotatingJTables;
import org.andork.swing.table.RowFilterFactory;
import org.andork.task.ExecutorTaskService;
import org.andork.task.Task;
import org.andork.task.TaskService;
import org.andork.unit.Angle;
import org.andork.unit.Length;
import org.andork.unit.Unit;
import org.andork.unit.UnitizedDouble;
import org.andork.util.FileRecoveryConfig;
import org.andork.util.JavaScript;
import org.andork.util.RecoverableFileOutputStream;
import org.breakout.StatsModel.MinAvgMax;
import org.breakout.compass.CompassConverter;
import org.breakout.compass.ui.CompassParseResultsDialog;
import org.breakout.model.ColorParam;
import org.breakout.model.ProjectModel;
import org.breakout.model.RootModel;
import org.breakout.model.ShotKey;
import org.breakout.model.Survey3dModel;
import org.breakout.model.Survey3dModel.SelectionEditor;
import org.breakout.model.Survey3dModel.Shot3d;
import org.breakout.model.Survey3dModel.Shot3dPickContext;
import org.breakout.model.Survey3dModel.Shot3dPickResult;
import org.breakout.model.SurveyTableModel;
import org.breakout.model.calc.CalcProject;
import org.breakout.model.calc.CalcShot;
import org.breakout.model.calc.CalculateGeometry;
import org.breakout.model.calc.Parsed2Calc;
import org.breakout.model.parsed.ParsedProject;
import org.breakout.model.parsed.ParsedShot;
import org.breakout.model.parsed.ParsedShotMeasurement;
import org.breakout.model.parsed.ProjectParser;
import org.breakout.model.raw.MetacaveExporter;
import org.breakout.model.raw.MetacaveImporter;
import org.breakout.model.raw.MutableSurveyRow;
import org.breakout.model.raw.SurveyRow;
import org.breakout.model.raw.SurveyTrip;
import org.breakout.update.UpdateStatusPanelController;
import org.jdesktop.swingx.JXHyperlink;

import com.andork.plot.LinearAxisConversion;
import com.andork.plot.MouseLooper;
import com.andork.plot.PlotAxis;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2ES2;
import com.jogamp.opengl.GL3;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLCanvas;

public class BreakoutMainView {
	private class AnimationViewSaver implements Animation {
		@Override
		public long animate(long animTime) {
			saveViewXform();
			return 0;
		}
	}

	class FitToFilteredHandler implements ActionListener {
		AnnotatingJTable table;
		long lastAction = 0;

		public FitToFilteredHandler(AnnotatingJTable table) {
			super();
			this.table = table;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			final long time = System.currentTimeMillis();
			lastAction = time;

			table.getAnnotatingRowSorter().invokeWhenDoneSorting(() -> {
				if (time >= lastAction) {
					flyToFiltered(table);
				}
			});
		}
	}

	private class HoverUpdater extends Task<Void> {
		Survey3dModel model3d;
		MouseEvent e;

		public HoverUpdater(Survey3dModel model3d, MouseEvent e) {
			super();
			setStatus("Updating mouseover glow...");
			this.model3d = model3d;
			this.e = e;
		}

		@Override
		protected Void work() throws Exception {
			final Shot3dPickResult picked = pick(model3d, e, hoverUpdaterSpc);

			if (picked != null) {
				LinearAxisConversion conversion = new FromEDT<LinearAxisConversion>() {
					@Override
					public LinearAxisConversion run() throws Throwable {
						SurveyRow orig = sourceRows.get(picked.picked.key());
						SurveyTrip trip = orig != null ? orig.getTrip() : null;
						ShotKey key = picked.picked.key();
						ParsedShot shot = parsedProject.shots.get(picked.picked.key());
						if (shot == null) {
							hintLabel.setText("");
						} else {
							UnitizedDouble<Length> distance = ParsedShotMeasurement.getFirstDistance(shot.measurements);
							UnitizedDouble<Angle> frontAzimuth = ParsedShotMeasurement
									.getFirstFrontAzimuth(shot.measurements);
							UnitizedDouble<Angle> backAzimuth = ParsedShotMeasurement
									.getFirstBackAzimuth(shot.measurements);
							UnitizedDouble<Angle> frontInclination = ParsedShotMeasurement
									.getFirstFrontInclination(shot.measurements);
							UnitizedDouble<Angle> backInclination = ParsedShotMeasurement
									.getFirstBackInclination(shot.measurements);

							QObject<ProjectModel> projectModel = getProjectModel();
							Unit<Length> lengthUnit = projectModel.get(ProjectModel.displayLengthUnit);
							Unit<Angle> angleUnit = projectModel.get(ProjectModel.displayAngleUnit);

							NumberFormat format = DecimalFormat.getInstance();
							format.setMaximumFractionDigits(1);
							format.setMinimumFractionDigits(1);
							format.setGroupingUsed(false);

							String formattedDistance = distance == null
									? "--" : distance.in(lengthUnit).toString(format);
							String formattedFrontAzimuth = frontAzimuth == null
									? "--" : frontAzimuth.in(angleUnit).toString(format);
							String formattedBackAzimuth = backAzimuth == null
									? "--" : backAzimuth.in(angleUnit).toString(format);
							String formattedFrontInclination = frontInclination == null
									? "--" : frontInclination.in(angleUnit).toString(format);
							String formattedBackInclination = backInclination == null
									? "--" : backInclination.in(angleUnit).toString(format);

							hintLabel.setText(String.format(
									"<html>Stations: <b>%s - %s</b>&emsp;Dist: <b>%s</b>&emsp;Azm: <b>%s/%s</b>"
											+ "&emsp;Inc: <b>%s/%s</b>&emsp;<i>%s</i></html>",
									key.fromStation, key.toStation,
									formattedDistance,
									formattedFrontAzimuth,
									formattedBackAzimuth,
									formattedFrontInclination,
									formattedBackInclination,
									trip != null ? trip.getName() : ""));
						}

						LinearAxisConversion conversion = getProjectModel().get(ProjectModel.highlightRange);
						LinearAxisConversion conversion2 = new LinearAxisConversion(conversion.invert(0.0), 1.0,
								conversion.invert(settingsDrawer.getGlowDistAxis().getViewSpan()), 0.0);
						return conversion2;
					}
				}.result();
				runSubtask(1, glowSubtask -> model3d.updateGlow(picked.picked, picked.locationAlongShot, conversion,
						glowSubtask));
			} else {
				runSubtask(1, glowSubtask -> model3d.updateGlow(null, null, null, glowSubtask));
			}

			if (!isCanceled()) {
				autoDrawable.display();
			}
			return null;
		}
	}

	private class ImportCompassTask extends DrawerPinningTask<Void> {
		final List<Path> surveyFiles = new ArrayList<>();
		final List<Path> plotFiles = new ArrayList<>();
		final List<Path> projFiles = new ArrayList<>();
		boolean doImport;

		private ImportCompassTask(Iterable<Path> compassFiles) {
			super(getMainPanel(), taskListDrawer.holder());
			for (Path p : compassFiles) {
				String s = p.toString().toLowerCase();
				if (s.endsWith(".dat")) {
					surveyFiles.add(p);
				} else if (s.endsWith(".plt")) {
					plotFiles.add(p);
				} else if (s.endsWith(".mak")) {
					projFiles.add(p);
				}
			}
			setIndeterminate(false);
			setCompleted(0);

			showDialogLater();
		}

		private String toString(Object o) {
			if (o == null) {
				return null;
			}
			return o.toString();
		}

		@Override
		protected Void workDuringDialog() throws Exception {
			List<SurveyRow> rows = new ArrayList<>();
			final SurveyTableModel newModel;
			final CompassSurveyParser parser = new CompassSurveyParser();
			final CompassPlotParser plotParser = new CompassPlotParser();
			final Map<String, SurveyRow> stationPositionRows = new HashMap<>();

			try {
				int progress = 0;

				for (Path file : projFiles) {
					setStatus("Importing data from " + file + "...");
					CompassProject proj = new CompassProjectParser().parseProject(file);
					surveyFiles.addAll(proj.getDataFiles());
					setCompleted(progress++);
				}

				Set<Path> finalSurveyFiles = new HashSet<>();
				for (Path file : surveyFiles) {
					finalSurveyFiles.add(file.toRealPath().normalize());
				}

				Set<Path> finalPlotFiles = new HashSet<>();
				for (Path file : plotFiles) {
					finalPlotFiles.add(file.toRealPath().normalize());
				}

				setTotal(projFiles.size() + finalSurveyFiles.size() + finalPlotFiles.size());

				for (Path file : finalSurveyFiles) {
					setStatus("Importing data from " + file + "...");
					runSubtask(1, fileSubtask -> {
						fileSubtask.setTotal(2);
						List<CompassTrip> trips = parser.parseCompassSurveyData(file);
						fileSubtask.increment();
						runSubtask(1, subtask -> rows.addAll(CompassConverter.convertFromCompass(trips, subtask)));
					});
				}
				newModel = new SurveyTableModel(rows);
				newModel.setEditable(false);

				for (Path file : finalPlotFiles) {
					setStatus("Importing data from " + file + "...");
					runSubtask(1, fileSubtask -> {
						fileSubtask.setTotal(2);
						List<CompassPlotCommand> commands = plotParser.parsePlot(file);
						fileSubtask.increment();

						fileSubtask.runSubtask(1, convertSubtask -> {
							SurveyTrip trip = null;
							convertSubtask.setTotal(commands.size());
							for (CompassPlotCommand command : commands) {
								if (command instanceof BeginSectionCommand) {
									trip = new SurveyTrip();
									trip.setCave(((BeginSectionCommand) command).getSectionName());
								} else if (command instanceof DrawSurveyCommand) {
									DrawSurveyCommand c = (DrawSurveyCommand) command;
									if (falsy(c.getStationName())) {
										continue;
									}
									SurveyRow row = new MutableSurveyRow()
											.setTrip(trip)
											.setFromStation(c.getStationName())
											.setNorthing(toString(c.getLocation().getNorthing()))
											.setEasting(toString(c.getLocation().getEasting()))
											.setElevation(toString(c.getLocation().getVertical()))
											.toImmutable();
									stationPositionRows.put(c.getStationName(), row);
								}
								convertSubtask.increment();
							}
						});
					});
				}

				if (!stationPositionRows.isEmpty()) {
					newModel.updateRows(row -> {
						SurveyRow posRow = stationPositionRows.get(row.getFromStation());
						if (posRow == null) {
							return row;
						}
						return row.withMutations(r -> {
							r.setNorthing(posRow.getNorthing());
							r.setEasting(posRow.getEasting());
							r.setElevation(posRow.getElevation());
						});
					});
				}
			} catch (Exception ex) {
				ex.printStackTrace();
				new OnEDT() {
					@Override
					public void run() throws Throwable {
						JOptionPane.showMessageDialog(getMainPanel(),
								ex.getClass().getSimpleName() + ": " + ex.getLocalizedMessage(),
								"Failed to import compass data", JOptionPane.ERROR_MESSAGE);
					}
				};

				return null;
			}

			new OnEDT() {

				@Override
				public void run() throws Throwable {
					CompassParseResultsDialog dialog = new CompassParseResultsDialog(i18n);
					List<CompassParseError> errors = new ArrayList<>();
					errors.addAll(parser.getErrors());
					errors.addAll(plotParser.getErrors());
					dialog.setErrors(errors);
					dialog.setSurveyTableModel(newModel);
					dialog.setSize(new Dimension(Toolkit.getDefaultToolkit().getScreenSize()));
					doImport = false;
					dialog.onImport(e -> {
						doImport = true;
						dialog.dispose();
					});
					dialog.setModalityType(ModalityType.APPLICATION_MODAL);
					dialog.setVisible(true);

					if (newModel == null || newModel.getRowCount() == 0) {
						return;
					}

					if (doImport) {
						SurveyTableModel model = surveyDrawer.table().getModel();
						model.clear();
						model.copyRowsFrom(newModel, 0, newModel.getRowCount() - 1, model.getRowCount());
					}
					rebuild3dModel.run();
				}
			};
			return null;
		}
	}

	private class MinAvgMaxCalc {
		int count = 0;
		double total = 0.0;
		double min = Double.NaN;
		double max = Double.NaN;

		public void add(double value) {
			min = Vecmath.nmin(min, value);
			max = Vecmath.nmax(max, value);
			total += value;
			count++;
		}

		public double getAvg() {
			return total / count;
		}

		public QObject<MinAvgMax> toModel(Unit<Length> unit) {
			QObject<MinAvgMax> result = MinAvgMax.spec.newObject();
			result.set(MinAvgMax.min, new UnitizedDouble<>(min, unit));
			result.set(MinAvgMax.avg, new UnitizedDouble<>(getAvg(), unit));
			result.set(MinAvgMax.max, new UnitizedDouble<>(max, unit));
			return result;
		}
	}

	private class MousePickHandler extends MouseAdapter {
		@Override
		public void mouseClicked(MouseEvent e) {
			if (e.getButton() != MouseEvent.BUTTON1 || e.isAltDown()) {
				return;
			}

			Shot3dPickResult picked = pick(model3d, e, spc);

			if (picked == null) {
				surveyDrawer.table().clearSelection();
			} else if (e.getClickCount() == 2) {
				SurveyRow row = sourceRows.get(picked.picked.key());
				if (row != null) {
					String link = row.getTrip() == null ? null : row.getTrip().getSurveyNotes();
					if (link != null) {
						openSurveyNotes(link);
					}
				}
			}
		}

		@Override
		public void mouseMoved(MouseEvent e) {
			if (model3d != null) {
				HoverUpdater updater = new HoverUpdater(model3d, e);
				for (Task<?> task : rebuildTaskService.getTasks()) {
					if (task instanceof HoverUpdater) {
						task.cancel();
					}
				}
				rebuildTaskService.submit(updater);
			}
		}

		@Override
		public void mousePressed(MouseEvent e) {
			if (e.getButton() == MouseEvent.BUTTON1) {
				pickCenterOfOrbit(e);
			} else {
				return;
			}

			if (e.isAltDown()) {
				for (Drawer drawer : Arrays.asList(surveyDrawer, miniSurveyDrawer, taskListDrawer, settingsDrawer)) {
					drawer.holder().release(DrawerAutoshowController.autoshowDrawerHolder);
				}
				canvasMouseAdapterWrapper.setWrapped(windowSelectionMouseHandler);
				windowSelectionMouseHandler.start(e);
				return;
			}

			Shot3dPickResult picked = pick(model3d, e, spc);

			if (picked == null) {
				return;
			}

			ListSelectionModel selModel = surveyDrawer.table().getModelSelectionModel();

			Integer modelRow = shotKeyToModelIndex.get(picked.picked.key());

			if (modelRow != null) {
				if ((e.getModifiersEx() & InputEvent.CTRL_DOWN_MASK) != 0) {
					if (selModel.isSelectedIndex(modelRow)) {
						selModel.removeSelectionInterval(modelRow, modelRow);
					} else {
						selModel.addSelectionInterval(modelRow, modelRow);
					}
				} else {
					selModel.setSelectionInterval(modelRow, modelRow);
				}

				int viewRow = surveyDrawer.table().convertRowIndexToView(modelRow);

				if (viewRow >= 0) {
					Rectangle visibleRect = surveyDrawer.table().getVisibleRect();
					Rectangle cellRect = surveyDrawer.table().getCellRect(viewRow, 0, true);
					visibleRect.y = cellRect.y + cellRect.height / 2 - visibleRect.height / 2;
					surveyDrawer.table().scrollRectToVisible(visibleRect);
				}
			}

			autoDrawable.display();
		}

		void pickCenterOfOrbit(MouseEvent e) {
			if (model3d == null) {
				return;
			}

			float[] origin = new float[3];
			float[] direction = new float[3];
			float[] center = new float[3];
			renderer.getViewState()
					.pickXform()
					.xform(e.getX(), e.getComponent().getHeight() - e.getY(), e.getComponent().getWidth(),
							e.getComponent().getHeight(), origin, direction);

			RNode<float[], Shot3d> bestNode = RTraversal.traverse(model3d.getTree().getRoot(), node -> {
				if (!Rectmath.rayIntersects(origin, direction, node.mbr())) {
					return Float.POSITIVE_INFINITY;
				}
				Rectmath.center(node.mbr(), center);
				return Vecmath.distanceFromLine3sq(center, origin, direction);
			});
			if (bestNode == null) {
				bestNode = model3d.getTree().getRoot();
			}

			Rectmath.center(bestNode.mbr(), center);
			Vecmath.projPointOntoVector3(center, origin, direction, center);
			Vecmath.add3(center, origin, center);

			orbiter.setCenter(center);
		}
	}

	void markProjectRecentlyVisited(Path projectFile) {
		QObject<RootModel> rootModel = getRootModel();
		QArrayList<Path> recentProjectFiles = rootModel.get(RootModel.recentProjectFiles);
		if (recentProjectFiles == null) {
			recentProjectFiles = QArrayList.newInstance();
			rootModel.set(RootModel.recentProjectFiles, recentProjectFiles);
		}

		recentProjectFiles.remove(projectFile);
		while (recentProjectFiles.size() > 20) {
			recentProjectFiles.remove(recentProjectFiles.size() - 1);
		}
		recentProjectFiles.add(0, projectFile);

	}

	private class NewProjectTask extends Task<Void> {
		private NewProjectTask() {
			super();
			setStatus("Creating New Project...");
			setIndeterminate(true);
		}

		@Override
		protected Void work() throws Exception {
			new OnEDT() {
				@Override
				public void run() throws Throwable {
					QObject<RootModel> rootModel = getRootModel();
					rootModel.set(RootModel.currentProjectFile, null);

					if (getProjectModel() != null) {
						getProjectModel().changeSupport().removePropertyChangeListener(projectModelChangeHandler);
					}

					final QObject<ProjectModel> projectModel = ProjectModel.newInstance();

					projectModel.changeSupport().addPropertyChangeListener(projectModelChangeHandler);
					projectModelBinder.set(projectModel);

					surveyDrawer.table().getModel().clear();

					float[] viewXform = projectModel.get(ProjectModel.viewXform);
					if (viewXform != null) {
						renderer.getViewSettings().setViewXform(viewXform);
					}

					Projection projCalculator = projectModel.get(ProjectModel.projCalculator);
					if (projCalculator != null) {
						renderer.getViewSettings().setProjection(projCalculator);
					}

					if (projectModel.get(ProjectModel.cameraView) == CameraView.PERSPECTIVE) {
						installPerspectiveMouseAdapters();
					} else {
						installOrthoMouseAdapters();
					}
				}
			};

			rebuild3dModel.run();
			return null;
		}
	}

	private class OpenProjectTask extends DrawerPinningTask<Void> {
		Path newProjectFile;
		QObject<ProjectModel> projectModel;

		private OpenProjectTask(Path newProjectFile) {
			super(getMainPanel(), taskListDrawer.holder());
			this.newProjectFile = newProjectFile;
			setStatus("Opening project: " + newProjectFile + "...");
			setIndeterminate(true);

			showDialogLater();
		}

		private SurveyTableModel loadSurvey(File file, File backupFile) {
			try (FileReader reader = new FileReader(file)) {
				JsonObject json = new JsonParser().parse(new JsonReader(reader)).getAsJsonObject();
				if (json.has("breakout")) {
					projectModel = ProjectModel.defaultMapper.unmap(
							new Gson().fromJson(json.get("breakout"), Object.class));
				}
				MetacaveImporter importer = new MetacaveImporter();
				importer.importMetacave(json);
				return new SurveyTableModel(importer.getRows());
			} catch (Exception ex) {
				ex.printStackTrace();
				if (!file.equals(backupFile) && backupFile != null && backupFile.exists()) {
					int option = FromEDT.fromEDT(() -> JOptionPane.showConfirmDialog(
							mainPanel,
							"<html>Failed to load survey " + ex.getLocalizedMessage() +
									"<br>A backup exists at " + backupFile +
									"; do you want to try to recover it?</html>",
							"Error", JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE));
					if (option == JOptionPane.NO_OPTION) {
						return null;
					}
					return loadSurvey(backupFile, backupFile);
				}

				OnEDT.onEDT(() -> {
					JOptionPane.showMessageDialog(mainPanel,
							"Failed to load survey: " + ex.getLocalizedMessage(),
							"Error", JOptionPane.ERROR_MESSAGE);
				});
				return null;
			}
		}

		@Override
		protected Void workDuringDialog() throws Exception {
			boolean changed = FromEDT.fromEDT(() -> {
				QObject<RootModel> rootModel = getRootModel();
				rootModel.set(RootModel.currentProjectFile, newProjectFile);
				markProjectRecentlyVisited(newProjectFile);

				if (getProjectModel() != null) {
					getProjectModel().changeSupport().removePropertyChangeListener(projectModelChangeHandler);
				}

				surveyDrawer.table().getModel().clear();
				destroyCalculatedModel();
				return true;
			});

			if (!changed) {
				return null;
			}

			File fileToLoad = newProjectFile.toFile();
			File backupFile = fileRecoveryConfig.getBackupFile(fileToLoad);
			if (!fileToLoad.exists() && backupFile.exists()) {
				fileToLoad = backupFile;
			}
			SurveyTableModel surveyModel = loadSurvey(fileToLoad, backupFile);
			if (surveyModel == null) {
				return null;
			}

			Path swapFile = getSwapFile(newProjectFile);
			if (Files.exists(swapFile)) {
				projectModel = loadProjectModel(swapFile.toFile());
			}

			if (projectModel == null) {
				projectModel = ProjectModel.instance.newObject();
			}
			ProjectModel.setDefaults(projectModel);

			OnEDT.onEDT(() -> {
				if (surveyModel != null && surveyModel.getRowCount() > 0) {
					surveyDrawer.table().getModel()
							.copyRowsFrom(surveyModel, 0, surveyModel.getRowCount() - 1, 0);
					rebuild3dModel.run();
				}

				projectModel.changeSupport().addPropertyChangeListener(projectModelChangeHandler);
				projectModelBinder.set(projectModel);

				float[] viewXform = projectModel.get(ProjectModel.viewXform);
				if (viewXform != null) {
					renderer.getViewSettings().setViewXform(viewXform);
				}

				Projection projCalculator = projectModel.get(ProjectModel.projCalculator);
				if (projCalculator != null) {
					renderer.getViewSettings().setProjection(projCalculator);
				}

				if (projectModel.get(ProjectModel.cameraView) == CameraView.PERSPECTIVE) {
					installPerspectiveMouseAdapters();
				} else {
					installOrthoMouseAdapters();
				}

				if (!Files.exists(newProjectFile)) {
					saveProject();
				}
			});

			return null;
		}
	}

	class OtherMouseHandler extends MouseAdapter {
		@Override
		public void mousePressed(MouseEvent e) {
			cameraAnimationQueue.clear();
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			saveViewXform();
		}

		@Override
		public void mouseWheelMoved(MouseWheelEvent e) {
			saveViewXform();
		}
	}

	private class TableSelectionHandler implements ListSelectionListener {
		@Override
		public void valueChanged(ListSelectionEvent e) {
			if (e.getValueIsAdjusting() || model3d == null) {
				return;
			}

			final Survey3dModel model3d = BreakoutMainView.this.model3d;

			final SelectionEditor editor = model3d.editSelection();

			ListSelectionModel selModel = (ListSelectionModel) e.getSource();

			if (e.getFirstIndex() < 0) {
				for (ShotKey key : calcProject.shots.keySet()) {
					editor.deselect(key);
				}

				miniSurveyDrawer.statsPanel().getModelBinder().set(StatsModel.spec.newObject());
			} else {
				MinAvgMaxCalc distCalc = new MinAvgMaxCalc();
				MinAvgMaxCalc northCalc = new MinAvgMaxCalc();
				MinAvgMaxCalc eastCalc = new MinAvgMaxCalc();
				MinAvgMaxCalc depthCalc = new MinAvgMaxCalc();

				Consumer<float[]> addPoints = points -> {
					if (points == null) {
						return;
					}
					for (int i = 0; i < points.length; i += 3) {
						if (Double.isFinite(points[i]) &&
								Double.isFinite(points[i + 1]) &&
								Double.isFinite(points[i + 2])) {
							northCalc.add(-points[i + 2]);
							eastCalc.add(points[i]);
							depthCalc.add(-points[i + 1]);
						}
					}
				};

				for (int i = e.getFirstIndex(); i <= e.getLastIndex()
						&& i < surveyDrawer.table().getModel().getRowCount(); i++) {
					ShotKey shotKey = modelIndexToShotKey.get(i);
					if (shotKey == null) {
						continue;
					}

					if (selModel.isSelectedIndex(i)) {
						editor.select(shotKey);
						CalcShot shot = calcProject.shots.get(shotKey);
						if (shot != null) {
							if (!Double.isNaN(shot.distance)) {
								distCalc.add(shot.distance);
							}
							addPoints.accept(shot.vertices);
						}
					} else {
						editor.deselect(shotKey);
					}
				}

				QObject<StatsModel> statsModel = StatsModel.spec.newObject();
				if (distCalc.count > 0) {
					statsModel.set(StatsModel.numSelected, distCalc.count);
					statsModel.set(StatsModel.totalDistance, Length.meters(distCalc.total));
					statsModel.set(StatsModel.distStats, distCalc.toModel(Length.meters));
					statsModel.set(StatsModel.northStats, northCalc.toModel(Length.meters));
					statsModel.set(StatsModel.eastStats, eastCalc.toModel(Length.meters));
					statsModel.set(StatsModel.depthStats, depthCalc.toModel(Length.meters));
				}

				miniSurveyDrawer.statsPanel().getModelBinder().set(statsModel);
			}

			rebuildTaskService.submit(task -> {
				editor.commit();

				float[] bounds = Rectmath.voidRectf(3);
				float[] p = Rectmath.voidRectf(3);

				for (ShotKey key : model3d.getSelectedShots()) {
					CalcShot shot = calcProject.shots.get(key);
					p[0] = (float) Math.min(shot.fromStation.position[0], shot.toStation.position[0]);
					p[1] = (float) Math.min(shot.fromStation.position[1], shot.toStation.position[1]);
					p[2] = (float) Math.min(shot.fromStation.position[2], shot.toStation.position[2]);
					p[3] = (float) Math.max(shot.fromStation.position[0], shot.toStation.position[0]);
					p[4] = (float) Math.max(shot.fromStation.position[1], shot.toStation.position[1]);
					p[5] = (float) Math.max(shot.fromStation.position[2], shot.toStation.position[2]);
					Rectmath.union3(bounds, p, bounds);
				}

				if (!model3d.getSelectedShots().isEmpty()) {
					// scale3( center , 0.5 / newSelectedShots.size( ) );
					p[0] = (bounds[0] + bounds[3]) * 0.5f;
					p[1] = (bounds[1] + bounds[4]) * 0.5f;
					p[2] = (bounds[2] + bounds[5]) * 0.5f;

					SwingUtilities.invokeLater(() -> {
						orbiter.setCenter(p);
						navigator.setCenter(p);
					});
				}

				autoDrawable.display();
			});
		}
	}

	private class RebuildTask extends Task<Void> {
		final ProjectParser parser = new ProjectParser();
		final Parsed2Calc p2c = new Parsed2Calc();
		final Map<ShotKey, Integer> shotKeyToModelIndex = new HashMap<>();
		final Map<Integer, ShotKey> modelIndexToShotKey = new HashMap<>();
		final Map<ShotKey, SurveyRow> sourceRows = new HashMap<>();

		@Override
		protected Void work() throws Exception {
			setTotal(5);
			setStatus("Updating view");
			try {
				OnEDT.onEDT(() -> taskListDrawer.holder().hold(this));
				parse();
				CalculateGeometry.calculateGeometry(p2c.project);
				updateView();
			} finally {
				OnEDT.onEDT(() -> taskListDrawer.holder().release(this));
			}
			return null;
		}

		void parse() throws Exception {
			runSubtask(1, parsingSubtask -> {
				parsingSubtask.setStatus("parsing shot data");
				parsingSubtask.setIndeterminate(true);

				SurveyTableModel copy = FromEDT.fromEDT(() -> surveyDrawer.table().getModel().clone());
				List<SurveyRow> rows = copy.getRows();

				if (parsingSubtask.isCanceled()) {
					return;
				}

				parsingSubtask.setIndeterminate(false);
				parsingSubtask.setTotal(rows.size());
				parsingSubtask.setCompleted(0);

				int modelIndex = 0;
				for (SurveyRow row : rows) {
					if (row == null || row.getTrip() == null || row.getTrip().getDistanceUnit() == null) {
						modelIndex++;
						continue;
					}
					ShotKey key = parser.parse(row);
					if (key != null) {
						shotKeyToModelIndex.put(key, modelIndex);
						modelIndexToShotKey.put(modelIndex, key);
						sourceRows.put(key, row);
					}
					modelIndex++;
					parsingSubtask.setCompleted(modelIndex);
				}
			});

			runSubtask(1, subtask -> p2c.convert(parser.project, subtask));
		}

		public void updateView() throws Exception {
			destroyCalculatedModel();

			final Survey3dModel model = callSubtask(2, subtask -> Survey3dModel.create(p2c.project, 10, 3, 3, subtask));
			if (isCanceled()) {
				return;
			}

			runSubtask(1, subtask -> {
				subtask.setIndeterminate(true);
				subtask.setStatus("installing model");
				float[] bounds = Arrays.copyOf(model.getTree().getRoot().mbr(), 6);

				bounds[1] = bounds[4] + 100;
				bounds[4] = bounds[1] + 100;

				SwingUtilities.invokeLater(() -> {
					BreakoutMainView.this.shotKeyToModelIndex = shotKeyToModelIndex;
					BreakoutMainView.this.modelIndexToShotKey = modelIndexToShotKey;
					BreakoutMainView.this.sourceRows = sourceRows;
					parsedProject = parser.project;
					calcProject = p2c.project;
					model3d = model;

					model.setParamPaint(settingsDrawer.getParamColorationAxisPaint());

					projectModelBinder.update(true);

					float[] center = new float[3];
					Rectmath.center(model.getTree().getRoot().mbr(), center);
					orbiter.setCenter(center);
					navigator.setCenter(center);

					autoDrawable.invoke(false, drawable -> {
						scene.add(model);
						scene.initLater(model);
						return false;
					});
				});
			});
		}
	}

	private static Shot3dPickContext hoverUpdaterSpc = new Shot3dPickContext();

	private static final int SCANNED_NOTES_SEARCH_DEPTH = 10;

	JMenuBar menuBar;

	GLAutoDrawable autoDrawable;
	GLCanvas canvas;
	JoglScene scene;
	JoglBackgroundColor bgColor;
	DefaultJoglRenderer renderer;
	DefaultNavigator navigator;

	JoglOrbiter orbiter;
	JoglOrthoNavigator orthoNavigator;

	I18n i18n = new I18n();

	PerspectiveProjection perspCalculator = new PerspectiveProjection(
			(float) Math.PI / 2, 1f,
			1e7f);

	final ScheduledExecutorService debouncer = Executors.newSingleThreadScheduledExecutor();
	final TaskService rebuildTaskService = ExecutorTaskService.newSingleThreadedTaskService();
	final TaskService sortTaskService = ExecutorTaskService.newSingleThreadedTaskService();
	final TaskService ioTaskService = ExecutorTaskService.newSingleThreadedTaskService();

	public void shutdown() {
		if (hasUnsavedChanges()) {
			int choice = JOptionPane.showConfirmDialog(
					SwingUtilities.getWindowAncestor(getMainPanel()),
					"Do you want to save changes?",
					"Unsaved Changes",
					JOptionPane.YES_NO_CANCEL_OPTION,
					JOptionPane.WARNING_MESSAGE);
			switch (choice) {
			case JOptionPane.YES_OPTION:
				saveProject();
				break;
			case JOptionPane.CANCEL_OPTION:
				return;
			}
		}
		debouncer.shutdown();
		rebuildTaskService.shutdownNow();
		sortTaskService.shutdownNow();
		ioTaskService.shutdown();

		JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(mainPanel);
		JDialog finalTaskDialog = new JDialog((JFrame) null, frame.getTitle());
		TaskList finalTaskList = new TaskList();
		finalTaskList.addService(ioTaskService);
		JScrollPane finalTaskListScroller = new JScrollPane(finalTaskList);
		finalTaskDialog.getContentPane().add(finalTaskListScroller, BorderLayout.CENTER);
		finalTaskDialog.setSize(800, 200);
		finalTaskDialog.setLocationRelativeTo(frame);

		Thread shutdownThread = new Thread("Shutdown") {
			@Override
			public void run() {
				try {
					debouncer.awaitTermination(30, TimeUnit.SECONDS);
					ioTaskService.awaitTermination(30, TimeUnit.SECONDS);
				} catch (InterruptedException e) {
					e.printStackTrace();
					System.exit(1);
				}
				System.exit(0);
			}
		};
		shutdownThread.start();
		frame.dispose();
		finalTaskDialog.setVisible(true);
	}

	boolean loadingSurvey = false;
	boolean editingSurvey = false;

	final double[] fromLoc = new double[3];

	final double[] toLoc = new double[3];

	final double[] toToLoc = new double[3];

	final double[] leftAtTo = new double[3];
	final double[] leftAtTo2 = new double[3];
	final double[] leftAtFrom = new double[3];

	JPanel mainPanel;
	MouseAdapterWrapper canvasMouseAdapterWrapper;

	// normal mouse mode
	MouseLooper mouseLooper;
	MouseAdapterChain mouseAdapterChain;

	MousePickHandler pickHandler;

	DrawerAutoshowController autoshowController;
	OtherMouseHandler otherMouseHandler;
	WindowSelectionMouseHandler windowSelectionMouseHandler;
	TableSelectionHandler selectionHandler;

	RowFilterFactory<String, TableModel, Integer> rowFilterFactory;
	SurveyDrawer surveyDrawer;
	MiniSurveyDrawer miniSurveyDrawer;
	TaskListDrawer taskListDrawer;

	SettingsDrawer settingsDrawer;
	Survey3dModel model3d;
	float[] v = newMat4f();
	int debugMbrCount = 0;

	Shot3dPickContext spc = new Shot3dPickContext();

	final LinePlaneIntersection3f lpx = new LinePlaneIntersection3f();

	final float[] p0 = new float[3];
	final float[] p1 = new float[3];
	final float[] p2 = new float[3];
	File rootFile;
	Path rootDirectory;

	final Binder<QObject<RootModel>> rootModelBinder = new DefaultBinder<>();

	final Binder<QObject<ProjectModel>> projectModelBinder = new DefaultBinder<>();

	Binder<ColorParam> colorParamBinder = QObjectAttributeBinder.bind(
			ProjectModel.colorParam,
			projectModelBinder);

	Binder<QMap<ColorParam, LinearAxisConversion, ?>> paramRangesBinder = QObjectAttributeBinder.bind(
			ProjectModel.paramRanges,
			projectModelBinder);

	Binder<LinearAxisConversion> paramRangeBinder = QMapKeyedBinder.bindKeyed(
			colorParamBinder,
			paramRangesBinder);

	final AnimationQueue cameraAnimationQueue = new AnimationQueue();

	NewProjectAction newProjectAction = new NewProjectAction(this);
	SaveProjectAction saveProjectAction = new SaveProjectAction(this);
	SaveProjectAsAction saveProjectAsAction = new SaveProjectAsAction(this);

	EditSurveyScanPathsAction editSurveyScanPathsAction = new EditSurveyScanPathsAction(this);

	OpenProjectAction openProjectAction = new OpenProjectAction(this);

	ImportCompassAction importCompassAction = new ImportCompassAction(this);

	ExportImageAction exportImageAction = new ExportImageAction(this);

	final WeakHashMap<Animation, Object> protectedAnimations = new WeakHashMap<>();

	JLabel hintLabel;

	CameraView currentView;

	Map<ShotKey, Integer> shotKeyToModelIndex = Collections.emptyMap();
	Map<Integer, ShotKey> modelIndexToShotKey = Collections.emptyMap();
	Map<ShotKey, SurveyRow> sourceRows = Collections.emptyMap();
	ParsedProject parsedProject = new ParsedProject();
	CalcProject calcProject = new CalcProject();

	private static final FileRecoveryConfig fileRecoveryConfig = new FileRecoveryConfig() {};

	private <S extends QSpec<S>> void saveModel(QObject<S> m, Path path, Bimapper<QObject<S>, Object> mapper) {
		saveModel(m, path == null ? null : path.toFile(), mapper);
	}

	private <S extends QSpec<S>> void saveModel(QObject<S> m, File file, Bimapper<QObject<S>, Object> mapper) {
		if (m == null || file == null) {
			return;
		}

		ioTaskService.submit(new Task<Void>() {
			@Override
			protected Void work() throws Exception {
				QObject<S> model = FromEDT.fromEDT(() -> m.deepClone());
				setStatus("Saving settings...");
				setIndeterminate(true);

				try (Writer w = new OutputStreamWriter(new RecoverableFileOutputStream(file, fileRecoveryConfig),
						"UTF-8")) {
					if (!file.getParentFile().exists()) {
						file.getParentFile().mkdirs();
					}
					Gson gson = new GsonBuilder()
							.setPrettyPrinting()
							.create();
					w.write(gson.toJson(mapper.map(model), Object.class));
					w.write(System.lineSeparator());
					w.flush();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				return null;
			}
		});
	}

	final DebouncedRunnable saveRootModel = Lodash.debounce(
			() -> saveModel(getRootModel(), rootFile, RootModel.defaultMapper),
			1000, new DebounceOptions<Void>().executor(debouncer));

	final DebouncedRunnable saveSwap = Lodash.debounce(
			() -> saveModel(getProjectModel(), getCurrentSwapFile(), ProjectModel.defaultMapper),
			1000, new DebounceOptions<Void>().executor(debouncer));

	final DebouncedRunnable rebuild3dModel = Lodash.debounce(() -> {
		rebuildTaskService.submit(new RebuildTask());
	}, 1000, new DebounceOptions<Void>().executor(debouncer));

	public BreakoutMainView() {
		final GLProfile glp = GLProfile.get(GLProfile.GL3);
		final GLCapabilities caps = new GLCapabilities(glp);
		autoDrawable = canvas = new GLCanvas(caps);
		autoDrawable.display();

		scene = new JoglScene();
		bgColor = new JoglBackgroundColor();
		scene.add(bgColor);

		renderer = new DefaultJoglRenderer(scene, new GL3Framebuffer(), 1);
		renderer.setDesiredUseStencilBuffer(true);

		autoDrawable.addGLEventListener(renderer);

		navigator = new DefaultNavigator(autoDrawable, renderer);
		navigator.setMoveFactor(5f);
		navigator.setWheelFactor(5f);

		orbiter = new JoglOrbiter(autoDrawable, renderer.getViewSettings());
		orthoNavigator = new JoglOrthoNavigator(autoDrawable, renderer.getViewState(), renderer.getViewSettings());

		hintLabel = new JLabel("A");
		hintLabel.setForeground(Color.WHITE);
		hintLabel.setBackground(Color.BLACK);
		hintLabel.setOpaque(true);
		Font hintFont = hintLabel.getFont();
		hintLabel.setFont(hintFont.deriveFont(Font.PLAIN).deriveFont(hintFont.getSize2D() + 3f));
		hintLabel.setPreferredSize(new Dimension(200, hintLabel.getPreferredSize().height));
		hintLabel.setText(" ");
		hintLabel.setVerticalAlignment(SwingConstants.TOP);

		final Consumer<Runnable> sortRunner = r -> sortTaskService.submit(task -> {
			task.setStatus("Sorting survey table...");
			r.run();
		});

		OnEDT.onEDT(() -> {
			surveyDrawer = new SurveyDrawer(sortRunner);

			rowFilterFactory = text -> new SmartComboTableRowFilter(Arrays.asList(
					new SurveyDesignationFilter(text),
					new SurveyorFilter(text),
					new DescriptionFilter(text)));

			AnnotatingJTables.connectSearchFieldAndRadioButtons(
					surveyDrawer.table(), surveyDrawer.searchField().textComponent,
					rowFilterFactory, surveyDrawer.highlightButton(),
					surveyDrawer.filterButton(), Color.YELLOW);
		});

		pickHandler = new MousePickHandler();

		canvasMouseAdapterWrapper = new MouseAdapterWrapper();
		canvas.addMouseListener(canvasMouseAdapterWrapper);
		canvas.addMouseMotionListener(canvasMouseAdapterWrapper);
		canvas.addMouseWheelListener(canvasMouseAdapterWrapper);

		mouseLooper = new MouseLooper();
		windowSelectionMouseHandler = new WindowSelectionMouseHandler(new WindowSelectionMouseHandler.Context() {
			@Override
			public void endSelection() {
				canvasMouseAdapterWrapper.setWrapped(mouseLooper);
			}

			@Override
			public GLAutoDrawable getDrawable() {
				return autoDrawable;
			}

			@Override
			public TaskService getRebuildTaskService() {
				return rebuildTaskService;
			}

			@Override
			public JoglScene getScene() {
				return scene;
			}

			@Override
			public Survey3dModel getSurvey3dModel() {
				return model3d;
			}

			@Override
			public JoglViewState getViewState() {
				return renderer.getViewState();
			}

			@Override
			public void selectShots(Set<Shot3d> newSelected, boolean add, boolean toggle) {
				OnEDT.onEDT(() -> {
					ListSelectionModel selModel = surveyDrawer.table().getModelSelectionModel();
					selModel.setValueIsAdjusting(true);
					if (!add && !toggle) {
						selModel.clearSelection();
					}
					for (Shot3d shot3d : newSelected) {
						Integer row = shotKeyToModelIndex.get(shot3d.key());
						if (toggle && selModel.isSelectedIndex(row)) {
							selModel.removeSelectionInterval(row, row);
						} else {
							selModel.addSelectionInterval(row, row);
						}
					}
					selModel.setValueIsAdjusting(false);
				});
			}
		});
		canvasMouseAdapterWrapper.setWrapped(mouseLooper);

		autoshowController = new DrawerAutoshowController();

		otherMouseHandler = new OtherMouseHandler();

		mouseAdapterChain = new MouseAdapterChain();
		mouseAdapterChain.addMouseAdapter(pickHandler);
		mouseAdapterChain.addMouseAdapter(autoshowController);
		mouseAdapterChain.addMouseAdapter(otherMouseHandler);

		mainPanel = new JPanel();
		mainPanel.setLayout(new DelegatingLayoutManager() {
			@Override
			public void onLayoutChanged(Container target) {
				Window w = SwingUtilities.getWindowAncestor(target);
				if (w != null) {
					w.invalidate();
					w.validate();
				}
				target.invalidate();
				target.validate();
			}
		});

		rebuildTaskService.setDebounceOptions(new DebounceOptions<Void>().setTimeout(SetTimeout::setTimeout));
		sortTaskService.setDebounceOptions(new DebounceOptions<Void>().setTimeout(SetTimeout::setTimeout));
		ioTaskService.setDebounceOptions(new DebounceOptions<Void>().setTimeout(SetTimeout::setTimeout));

		taskListDrawer = new TaskListDrawer();
		taskListDrawer.addTaskService(rebuildTaskService);
		taskListDrawer.addTaskService(sortTaskService);
		taskListDrawer.addTaskService(ioTaskService);
		taskListDrawer.addTo(mainPanel);

		settingsDrawer = new SettingsDrawer(i18n, rootModelBinder, projectModelBinder);
		settingsDrawer.addTo(mainPanel);

		surveyDrawer.table().getModel().setEditable(false);
		surveyDrawer.editButton().setSelected(false);
		surveyDrawer.editButton().addItemListener(e -> {
			editingSurvey = e.getStateChange() == ItemEvent.SELECTED;
			if (e.getStateChange() == ItemEvent.DESELECTED) {
				CellEditor editor = surveyDrawer.table().getCellEditor();
				if (editor != null) {
					editor.stopCellEditing();
				}
				getProjectModel().set(ProjectModel.hasUnsavedChanges, true);
				rebuild3dModel.run();
			}
		});

		surveyDrawer.addTo(mainPanel);

		selectionHandler = new TableSelectionHandler();
		surveyDrawer.table().getModelSelectionModel().addListSelectionListener(selectionHandler);

		OnEDT.onEDT(() -> {
			miniSurveyDrawer = new MiniSurveyDrawer(i18n, sortRunner);

			miniSurveyDrawer.table().setModel(surveyDrawer.table().getModel());
			miniSurveyDrawer.table().setModelSelectionModel(surveyDrawer.table().getModelSelectionModel());

			AnnotatingJTables.connectSearchFieldAndRadioButtons(
					miniSurveyDrawer.table(),
					miniSurveyDrawer.searchField().textComponent,
					rowFilterFactory, miniSurveyDrawer.highlightButton(),
					miniSurveyDrawer.filterButton(),
					Color.YELLOW);

			miniSurveyDrawer.delegate().dockingSide(Side.LEFT);
			miniSurveyDrawer.mainResizeHandle();
			miniSurveyDrawer.addTo(mainPanel);

			miniSurveyDrawer.delegate()
					.putExtraConstraint(Side.BOTTOM, new SideConstraint(surveyDrawer, Side.TOP, 0));
		});

		settingsDrawer.delegate().putExtraConstraint(Side.BOTTOM, new SideConstraint(surveyDrawer, Side.TOP, 0));

		taskListDrawer.delegate().putExtraConstraint(Side.LEFT,
				new SideConstraint(miniSurveyDrawer, Side.RIGHT, 0));
		taskListDrawer.delegate().putExtraConstraint(Side.RIGHT,
				new SideConstraint(settingsDrawer, Side.LEFT, 0));

		SideConstraintLayoutDelegate spinnerDelegate = new SideConstraintLayoutDelegate();
		spinnerDelegate.putExtraConstraint(
				Side.LEFT, new SideConstraint(miniSurveyDrawer, Side.RIGHT, 0));
		spinnerDelegate.putExtraConstraint(
				Side.BOTTOM, new SideConstraint(surveyDrawer, Side.TOP, 0));

		SideConstraintLayoutDelegate hintLabelDelegate = new SideConstraintLayoutDelegate();
		hintLabelDelegate.putExtraConstraint(
				Side.LEFT, new SideConstraint(taskListDrawer.pinButton(), Side.RIGHT, 0));
		hintLabelDelegate.putExtraConstraint(
				Side.RIGHT, new SideConstraint(settingsDrawer, Side.LEFT, 0));
		hintLabelDelegate.putExtraConstraint(
				Side.BOTTOM, new SideConstraint(surveyDrawer, Side.TOP, 0));

		mainPanel.add(taskListDrawer.pinButton(), spinnerDelegate);
		mainPanel.add(hintLabel, hintLabelDelegate);

		SideConstraintLayoutDelegate canvasDelegate = new SideConstraintLayoutDelegate();
		canvasDelegate.putExtraConstraint(Side.TOP, new SideConstraint(taskListDrawer, Side.BOTTOM, 0));
		canvasDelegate.putExtraConstraint(Side.LEFT, new SideConstraint(miniSurveyDrawer, Side.RIGHT, 0));
		canvasDelegate.putExtraConstraint(Side.RIGHT, new SideConstraint(settingsDrawer, Side.LEFT, 0));
		canvasDelegate.putExtraConstraint(Side.BOTTOM, new SideConstraint(hintLabel, Side.TOP, 0));
		mainPanel.add(canvas, canvasDelegate);

		surveyDrawer.table().setTransferHandler(new SurveyTableTransferHandler());

		surveyDrawer.table().addPropertyChangeListener("model", new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				@SuppressWarnings("unchecked")
				AnnotatingRowSorter<TableModel, Integer> sorter = (AnnotatingRowSorter<TableModel, Integer>) miniSurveyDrawer
						.table().getRowSorter();

				SurveyTableModel newModel = (SurveyTableModel) evt.getNewValue();

				miniSurveyDrawer.table().setRowSorter(null);
				miniSurveyDrawer.table().setModel(newModel);
				sorter.setModel(newModel);
				miniSurveyDrawer.table().setRowSorter(sorter);
			}
		});

		surveyDrawer.table().addSurveyTableListener(new SurveyTableListener() {

			@Override
			public void surveyNotesClicked(String link, int viewRow) {
				openSurveyNotes(link);
			}
		});

		surveyDrawer.setBinder(QObjectAttributeBinder.bind(ProjectModel.surveyDrawer, projectModelBinder));
		settingsDrawer.setBinder(QObjectAttributeBinder.bind(ProjectModel.settingsDrawer, projectModelBinder));
		taskListDrawer.setBinder(QObjectAttributeBinder.bind(ProjectModel.taskListDrawer, projectModelBinder));

		new BinderWrapper<Color>() {
			@Override
			protected void onValueChanged(Color bgColor) {
				if (bgColor != null) {
					BreakoutMainView.this.bgColor.set(bgColor.getRed() / 255f, bgColor.getGreen() / 255f,
							bgColor.getBlue() / 255f, 1f);
					autoDrawable.display();
				}
			}
		}.bind(QObjectAttributeBinder.bind(ProjectModel.backgroundColor, projectModelBinder));

		new BinderWrapper<Float>() {
			@Override
			protected void onValueChanged(Float stationLabelDensity) {
				if (model3d != null && stationLabelDensity != null) {
					model3d.setStationLabelDensity(stationLabelDensity);
					autoDrawable.display();
				}
			}
		}.bind(QObjectAttributeBinder.bind(ProjectModel.stationLabelDensity, projectModelBinder));

		new BinderWrapper<Float>() {
			@Override
			protected void onValueChanged(Float stationLabelFontSize) {
				if (model3d != null && stationLabelFontSize != null) {
					model3d.setStationLabelFontSize(stationLabelFontSize);
					autoDrawable.display();
				}
			}
		}.bind(QObjectAttributeBinder.bind(ProjectModel.stationLabelFontSize, projectModelBinder));

		new BinderWrapper<Color>() {
			@Override
			protected void onValueChanged(Color stationLabelColor) {
				if (model3d != null && stationLabelColor != null) {
					model3d.setStationLabelColor(stationLabelColor);
					autoDrawable.display();
				}
			}
		}.bind(QObjectAttributeBinder.bind(ProjectModel.stationLabelColor, projectModelBinder));

		new BinderWrapper<Float>() {
			@Override
			protected void onValueChanged(Float maxCenterlineDistance) {
				if (model3d != null && maxCenterlineDistance != null) {
					model3d.setMaxCenterlineDistance(maxCenterlineDistance);
					autoDrawable.display();
				}
			}
		}.bind(QObjectAttributeBinder.bind(ProjectModel.centerlineDistance, projectModelBinder));

		new BinderWrapper<Color>() {
			@Override
			protected void onValueChanged(Color centerlineColor) {
				if (model3d != null && centerlineColor != null) {
					model3d.setCenterlineColor(centerlineColor);
					autoDrawable.display();
				}
			}
		}.bind(QObjectAttributeBinder.bind(ProjectModel.centerlineColor, projectModelBinder));

		new BinderWrapper<Boolean>() {
			@Override
			protected void onValueChanged(Boolean showSpatialIndex) {
				if (model3d != null && showSpatialIndex != null) {
					model3d.setShowSpatialIndex(showSpatialIndex);
					autoDrawable.display();
				}
			}
		}.bind(QObjectAttributeBinder.bind(RootModel.showSpatialIndex, rootModelBinder));

		new BinderWrapper<Integer>() {
			@Override
			protected void onValueChanged(Integer newValue) {
				if (newValue != null) {
					float sensitivity = newValue / 20f;
					orbiter.setSensitivity(sensitivity);
					navigator.setSensitivity(sensitivity);
				}
			}
		}.bind(QObjectAttributeBinder.bind(RootModel.mouseSensitivity, rootModelBinder));

		new BinderWrapper<Integer>() {
			@Override
			protected void onValueChanged(Integer newValue) {
				if (newValue != null) {
					float sensitivity = newValue / 5f;
					navigator.setWheelFactor(sensitivity);
				}
			}
		}.bind(QObjectAttributeBinder.bind(RootModel.mouseWheelSensitivity, rootModelBinder));

		new BinderWrapper<Float>() {
			@Override
			protected void onValueChanged(final Float newValue) {
				if (model3d != null && newValue != null) {
					model3d.setAmbientLight(newValue);
					autoDrawable.display();
				}
			}
		}.bind(QObjectAttributeBinder.bind(ProjectModel.ambientLight, projectModelBinder));

		new BinderWrapper<LinearAxisConversion>() {
			@Override
			protected void onValueChanged(LinearAxisConversion range) {
				if (model3d != null && range != null) {
					final float nearDist = (float) range.invert(0.0);
					final float farDist = (float) range
							.invert(settingsDrawer.getDistColorationAxis().getViewSpan());
					final Survey3dModel model3d = BreakoutMainView.this.model3d;
					model3d.setNearDist(nearDist);
					model3d.setFarDist(farDist);
					autoDrawable.display();
				}
			}
		}.bind(QObjectAttributeBinder.bind(ProjectModel.distRange, projectModelBinder));

		new BinderWrapper<LinearAxisConversion>() {
			@Override
			protected void onValueChanged(LinearAxisConversion range) {
				if (model3d != null && range != null) {
					final float loParam = (float) range.invert(0.0);
					final float hiParam = (float) range.invert(settingsDrawer.getParamColorationAxis()
							.getViewSpan());
					final Survey3dModel model3d = BreakoutMainView.this.model3d;
					model3d.setLoParam(loParam);
					model3d.setHiParam(hiParam);
					autoDrawable.display();
				}
			}
		}.bind(paramRangeBinder);

		new BinderWrapper<float[]>() {
			@Override
			protected void onValueChanged(float[] depthAxis) {
				if (depthAxis == null) {
					return;
				}
				final float[] finalDepthAxis = Arrays.copyOf(depthAxis, depthAxis.length);
				if (model3d != null && depthAxis != null && depthAxis.length == 3) {
					final Survey3dModel model3d = BreakoutMainView.this.model3d;
					model3d.setDepthAxis(finalDepthAxis);
					autoDrawable.display();
				}
			}
		}.bind(QObjectAttributeBinder.bind(ProjectModel.depthAxis, projectModelBinder));

		new BinderWrapper<ColorParam>() {
			@Override
			protected void onValueChanged(final ColorParam colorParam) {
				if (colorParam != null && model3d != null) {
					final Survey3dModel model3d = BreakoutMainView.this.model3d;

					rebuildTaskService.submit(task -> {
						task.setTotal(1);
						task.setStatus("Recoloring");
						model3d.setColorParam(colorParam, task);
						autoDrawable.display();
					});
				}
			}
		}.bind(QObjectAttributeBinder.bind(ProjectModel.colorParam, projectModelBinder));

		miniSurveyDrawer.statsPanel().lengthUnitBinder().bind(
				QObjectAttributeBinder.bind(ProjectModel.displayLengthUnit, projectModelBinder));

		menuBar = new JMenuBar();
		JMenu fileMenu = new JMenu();
		menuBar.add(fileMenu);

		fileMenu.add(new JMenuItem(newProjectAction));
		fileMenu.add(new JMenuItem(openProjectAction));
		fileMenu.add(new JMenuItem(saveProjectAction));
		fileMenu.add(new JMenuItem(saveProjectAsAction));
		JMenu openRecentMenu = new JMenu();
		fileMenu.add(openRecentMenu);
		fileMenu.add(new JSeparator());
		fileMenu.add(new JMenuItem(editSurveyScanPathsAction));
		fileMenu.add(new JSeparator());
		JMenu importMenu = new JMenu();
		importMenu.add(new JMenuItem(importCompassAction));
		fileMenu.add(importMenu);
		JMenu exportMenu = new JMenu();
		exportMenu.add(new JMenuItem(exportImageAction));
		fileMenu.add(exportMenu);

		JMenu debugMenu = new JMenu();
		menuBar.add(debugMenu);
		JCheckBoxMenuItem showSpatialIndexItem = new JCheckBoxMenuItem();
		new ButtonSelectedBinder(showSpatialIndexItem).bind(
				new QObjectAttributeBinder<>(RootModel.showSpatialIndex)
						.bind(rootModelBinder));
		debugMenu.add(showSpatialIndexItem);

		JMenuItem noRecentFilesItem = new JMenuItem();
		noRecentFilesItem.setEnabled(false);

		new BinderWrapper<QArrayList<Path>>() {
			@Override
			protected void onValueChanged(QArrayList<Path> newValue) {
				openRecentMenu.removeAll();
				if (newValue == null || newValue.isEmpty()) {
					openRecentMenu.add(noRecentFilesItem);
				} else {
					for (Path file : newValue) {
						openRecentMenu.add(new JMenuItem(
								new OpenRecentProjectAction(BreakoutMainView.this, file)));
					}
				}
			}

		}.bind(new HierarchicalChangeBinder<QArrayList<Path>>()
				.bind(new QObjectAttributeBinder<>(RootModel.recentProjectFiles).bind(rootModelBinder)));

		OnEDT.onEDT(() -> {
			Localizer localizer = i18n.forClass(BreakoutMainView.class);
			localizer.register(menuBar, new I18nUpdater<JMenuBar>() {
				@Override
				public void updateI18n(Localizer localizer, JMenuBar localizedObject) {
					localizer.setText(fileMenu, "fileMenu.text");
					localizer.setText(importMenu, "importMenu.text");
					localizer.setText(exportMenu, "exportMenu.text");
					localizer.setText(openRecentMenu, "openRecentMenu.text");
					localizer.setText(noRecentFilesItem, "noRecentFilesItem.text");

					localizer.setText(debugMenu, "debugMenu.text");
					localizer.setText(showSpatialIndexItem, "showSpatialIndexItem.text");
				}
			});
		});

		settingsDrawer.getFitViewToSelectedButton().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				fitViewToSelected();
			}
		});

		settingsDrawer.getFitViewToEverythingButton().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				fitViewToEverything();
			}
		});

		settingsDrawer.getFitParamColorationAxisButton().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (model3d == null) {
					return;
				}

				final Survey3dModel model3d = BreakoutMainView.this.model3d;

				rebuildTaskService.submit(task -> {
					task.setTotal(1);
					float[] range = task.callSubtask(1,
							calcSubtask -> model3d.calcAutofitParamRange(getDefaultShotsForOperations(2), calcSubtask));

					if (range == null ||
							!Float.isFinite(range[0]) || !Float.isFinite(range[1]) ||
							range[0] == -Float.MAX_VALUE || range[1] == -Float.MIN_VALUE) {
						return;
					}

					ColorParam colorParam = getProjectModel().get(ProjectModel.colorParam);
					if (!colorParam.isLoBright()) {
						float swap = range[0];
						range[0] = range[1];
						range[1] = swap;
					}
					LinearAxisConversion conversion = new LinearAxisConversion(range[0], 0.0, range[1],
							settingsDrawer.getParamColorationAxis().getViewSpan());

					paramRangeBinder.set(conversion);
				});
			}
		});

		settingsDrawer.getFlipParamColorationAxisButton().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				PlotAxis axis = settingsDrawer.getParamColorationAxis();
				LinearAxisConversion conversion = axis.getAxisConversion();
				double start = conversion.invert(0.0);
				double end = conversion.invert(axis.getViewSpan());
				LinearAxisConversion newConversion = new LinearAxisConversion(end, 0.0, start, axis.getViewSpan());
				paramRangeBinder.set(newConversion);
			}
		});

		settingsDrawer.getRecalcColorByDistanceButton().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (model3d == null) {
					return;
				}
				final Survey3dModel model3d = BreakoutMainView.this.model3d;
				rebuildTaskService.submit(task -> {
					task.setTotal(4);
					task.setStatus("Recalculating color by distance");
					Set<ShotKey> selectedShots = new HashSet<>();
					getSelectedShotsFromTable().forEach(selectedShots::add);
					Set<ShotKey> shotsFromView = getShotsInView();
					Set<ShotKey> startShots = selectedShots.isEmpty() ? shotsFromView : selectedShots;
					task.runSubtask(3,
							recalculateTask -> model3d.calcDistFromShots(startShots, recalculateTask));

					Set<ShotKey> rangeShots = startShots == shotsFromView
							? calcProject.shots.keySet() : shotsFromView;
					task.runSubtask(1,
							rangeTask -> model3d.calcAutofitParamRange(rangeShots, rangeTask));
					autoDrawable.display();
				});
			}
		});

		settingsDrawer.getResetViewButton().addActionListener(e -> {
			renderer.getViewSettings().setViewXform(newMat4f());
			autoDrawable.display();
		});

		settingsDrawer.getOrbitToPlanButton().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (model3d == null) {
					return;
				}

				float[] center = new float[3];
				orbiter.getCenter(center);

				if (Vecmath.hasNaNsOrInfinites(center)) {
					model3d.getCenter(center);
				}

				float[] v = newMat4f();
				renderer.getViewSettings().getViewXform(v);

				removeUnprotectedCameraAnimations();
				cameraAnimationQueue.add(new SpringViewOrbitAnimation(autoDrawable, renderer.getViewSettings(),
						center, 0f, (float) -Math.PI * .5f, .1f, .05f, 30));
				cameraAnimationQueue.add(new AnimationViewSaver());
			}
		});

		ViewButtonsPanel viewButtonsPanel = settingsDrawer.getViewButtonsPanel();
		for (CameraView view : CameraView.values()) {
			JToggleButton button = viewButtonsPanel.getButton(view);
			if (button != null) {
				button.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						setCameraView(view);
					}
				});
			}
		}

		settingsDrawer.getInferDepthAxisTiltButton().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (model3d == null) {
					return;
				}
				List<float[]> vectors = new ArrayList<>();
				for (ShotKey key : getDefaultShotsForOperations(3)) {
					CalcShot shot = calcProject.shots.get(key);
					if (shot == null) {
						continue;
					}
					float[] vector = new float[3];
					Vecmath.sub3(shot.toStation.position, shot.fromStation.position, vector);

					if (!Vecmath.hasNaNsOrInfinites(vector)) {
						vectors.add(vector);
					}
				}
				float[] normal = Fitting3d.planeNormalLeastSquares2f(vectors.stream());
				Vecmath.normalize3(normal);

				if (normal[1] > 0) {
					Vecmath.negate3(normal);
				}

				getProjectModel().set(ProjectModel.depthAxis, normal);
			}
		});

		settingsDrawer.getResetDepthAxisTiltButton().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				getProjectModel().set(ProjectModel.depthAxis, new float[] { 0f, -1f, 0f });
			}
		});

		settingsDrawer.getCameraToDepthAxisTiltButton().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				float[] axis = new float[3];
				Vecmath.negate3(renderer.getViewState().inverseViewMatrix(), 8, axis, 0);
				getProjectModel().set(ProjectModel.depthAxis, axis);
			}
		});

		((JTextField) surveyDrawer.searchField().textComponent).addActionListener(new FitToFilteredHandler(
				surveyDrawer.table()));
		((JTextField) miniSurveyDrawer.searchField().textComponent)
				.addActionListener(new FitToFilteredHandler(miniSurveyDrawer.table()));

		new BinderWrapper<Integer>() {
			@Override
			protected void onValueChanged(Integer desiredNumSamples) {
				if (desiredNumSamples != null) {
					renderer.setDesiredNumSamples(desiredNumSamples);
					autoDrawable.display();
				}
			}
		}.bind(QObjectAttributeBinder.bind(RootModel.desiredNumSamples, rootModelBinder));

		autoDrawable.invoke(false, drawable -> {
			GL2ES2 gl = (GL2ES2) drawable.getGL();
			int[] temp = new int[1];
			((GL3) gl).glGetIntegerv(GL.GL_MAX_SAMPLES, temp, 0);
			SwingUtilities.invokeLater(() -> settingsDrawer.setMaxNumSamples(temp[0]));
			return true;
		});

		String rootFilePath = System.getProperty("rootFile");

		if (rootFilePath == null) {
			File rootDir = new File(".breakout");
			rootFile = new File(rootDir, "settings.json");
			if (!rootFile.exists()) {
				rootDir.mkdir();
			}
		} else {
			rootFile = new File(rootFilePath);
		}

		rootDirectory = rootFile.toPath().getParent();

		QObject<RootModel> rootModel = loadRootModel(rootFile);

		if (rootModel == null) {
			rootModel = RootModel.instance.newObject();
			rootModel.set(RootModel.desiredNumSamples, 2);
		}
		if (rootModel.get(RootModel.showStationLabels) == null) {
			rootModel.set(RootModel.showStationLabels, true);
		}
		if (rootModel.get(RootModel.showSpatialIndex) == null) {
			rootModel.set(RootModel.showSpatialIndex, false);
		}

		setRootModel(rootModel);

		if (!recoverBackupIfNecessary(rootModel)) {
			newProject();
		}

		try (FileInputStream updateIn = new FileInputStream("update.properties")) {
			Properties updateProps = new Properties();
			updateProps.load(updateIn);
			updateIn.close();

			UpdateStatusPanelController updateStatusPanelController = new UpdateStatusPanelController(
					settingsDrawer.getUpdateStatusPanel(),
					settingsDrawer.getLoadedVersion(),
					new URL(updateProps.get("latestVersionInfoUrl").toString()),
					new File(updateProps.get("updateDir").toString()));

			updateStatusPanelController.checkForUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean recoverBackupIfNecessary(QObject<RootModel> rootModel) {
		try {
			QArrayList<Path> recentProjectFiles = rootModel.get(RootModel.recentProjectFiles);
			if (recentProjectFiles != null && !recentProjectFiles.isEmpty()) {
				File mostRecentFile = recentProjectFiles.get(0).toFile();
				File mostRecentBackup = fileRecoveryConfig.getBackupFile(mostRecentFile);
				String message = "<html>It appears that Breakout shutdown unexpectedly while you were working on "
						+ mostRecentFile + ",<br>but it is backed up in " +
						mostRecentBackup + ".  What do you want to do?</html>";

				if (!mostRecentFile.exists() && mostRecentBackup.exists()) {
					Object[] options = { "Recover It", "Delete It", "Leave It" };
					int option = JOptionPane.showOptionDialog(
							SwingUtilities.getWindowAncestor(mainPanel),
							message, "File Recovery",
							JOptionPane.WARNING_MESSAGE, 0, null,
							options, options[0]);
					switch (option) {
					case 0:
						openProject(mostRecentFile.toPath());
						return true;
					case 1:
						mostRecentBackup.delete();
						recentProjectFiles.remove(mostRecentFile);
						break;
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(
					SwingUtilities.getWindowAncestor(mainPanel),
					ex.getLocalizedMessage(),
					"Error",
					JOptionPane.ERROR_MESSAGE);
		}
		return false;
	}

	private void changeView(float[] forward, float[] right, boolean ortho, Set<ShotKey> shotsToFit) {
		if (Vecmath.hasNaNsOrInfinites(forward) || Vecmath.hasNaNsOrInfinites(right)) {
			throw new IllegalArgumentException("forward and right must not contain NaN or infinite values");
		}

		mouseLooper.removeMouseAdapter(mouseAdapterChain);

		Survey3dModel model3d = this.model3d;

		float[] up = new float[3];
		Vecmath.cross(right, forward, up);

		Projection newProjCalculator;
		float[] vi = renderer.getViewState().inverseViewMatrix();
		float[] endLocation = { vi[12], vi[13], vi[14] };

		Animation finisher;

		if (ortho) {
			AutoClipOrthoProjection orthoCalculator = new AutoClipOrthoProjection();
			newProjCalculator = orthoCalculator;
			orbiter.getCenter(orthoCalculator.center);

			if (model3d != null) {
				orthoCalculator.radius = Rectmath.radius3(model3d.getTree().getRoot().mbr());
				float[] orthoBounds = model3d.getOrthoBounds(shotsToFit, right, up, forward);
				Rectmath.scaleFromCenter3(orthoBounds, 1 / 0.9f, 1 / 0.9f, 1f, orthoBounds);

				float[] endOrthoLocation = new float[3];
				Rectmath.center(orthoBounds, endOrthoLocation);

				Vecmath.combine(endLocation, endOrthoLocation, right, up, forward);

				float dist = Vecmath.distance3(vi, 12, endLocation, 0);
				endOrthoLocation[2] -= dist;
				Vecmath.combine(endLocation, endOrthoLocation, right, up, forward);

				Rectmath.center(orthoBounds, endOrthoLocation);
				endOrthoLocation[2] = orthoBounds[2];
				Vecmath.combine(orthoCalculator.nearClipPoint, endOrthoLocation, right, up, forward);
				endOrthoLocation[2] = orthoBounds[5];
				Vecmath.combine(orthoCalculator.farClipPoint, endOrthoLocation, right, up, forward);

				orthoCalculator.hSpan = orthoBounds[3] - orthoBounds[0];
				orthoCalculator.vSpan = orthoBounds[4] - orthoBounds[1];
			}

			finisher = l -> {
				orthoCalculator.useNearClipPoint = orthoCalculator.useFarClipPoint = true;
				renderer.getViewSettings().setProjection(orthoCalculator);
				saveProjection();

				installOrthoMouseAdapters();

				autoDrawable.display();
				return 0;
			};
		} else {
			newProjCalculator = perspCalculator;

			if (model3d != null) {
				FittingFrustum frustum = new FittingFrustum();
				float[] projXform = newMat4f();
				perspCalculator.calculate(renderer.getViewState(), projXform);
				PickXform pickXform = new PickXform();
				pickXform.calculate(projXform, renderer.getViewState().viewMatrix());
				frustum.init(pickXform, 0.9f);

				for (ShotKey key : shotsToFit) {
					Shot3d shot = model3d.getShot(key);
					if (shot == null) {
						continue;
					}
					for (float[] coord : shot.coordIterable(endLocation)) {
						frustum.addPoint(coord);
					}
				}

				frustum.calculateOrigin(endLocation);
			}

			finisher = l -> {
				renderer.getViewSettings().setProjection(perspCalculator);
				saveProjection();

				installPerspectiveMouseAdapters();

				autoDrawable.display();
				return 0;
			};
		}

		GeneralViewXformOrbitAnimation viewAnimation = new GeneralViewXformOrbitAnimation(autoDrawable,
				renderer.getViewSettings(), 1750, 30);
		float[] viewXform = newMat4f();
		viewAnimation.setUpWithEndLocation(renderer.getViewState().viewMatrix(), endLocation, forward, right);

		Projection currentProjCalculator = renderer.getViewSettings().getProjection();

		InterpolationProjection calc = new InterpolationProjection(renderer.getViewSettings().getProjection(),
				newProjCalculator, 0f);

		FloatUnaryOperator viewReparam = f -> 1 - (1 - f) * (1 - f);
		FloatUnaryOperator projReparam;
		if (currentProjCalculator instanceof AutoClipOrthoProjection) {
			AutoClipOrthoProjection currentOrthoCalc = (AutoClipOrthoProjection) currentProjCalculator;
			currentOrthoCalc.useNearClipPoint = currentOrthoCalc.useFarClipPoint = false;
			if (ortho) {
				projReparam = viewReparam;
			} else {
				float b = 10f;
				float a = 1 / b;
				float ra = 1 / a / a;
				float rb = 1 / b / b;
				projReparam = f -> {
					float ff = b + f * (a - b);
					float rf = 1 / ff / ff;
					return viewReparam.applyAsFloat((rf - rb) / (ra - rb));
				};
			}
		} else {
			if (ortho) {
				float b = 10f;
				float a = 1 / b;
				float ra = 1 / a / a;
				float rb = 1 / b / b;
				projReparam = f -> {
					float ff = a + viewReparam.applyAsFloat(f) * (b - a);
					float rf = 1 / ff / ff;
					return (rf - ra) / (rb - ra);
				};
			} else {
				projReparam = viewReparam;
			}
		}

		removeUnprotectedCameraAnimations();
		cameraAnimationQueue.add(new ProjXformAnimation(autoDrawable, renderer.getViewSettings(), 1750, false,
				f -> {
					calc.f = projReparam.applyAsFloat(f);
					return calc;
				}).also(new ViewXformAnimation(autoDrawable, renderer.getViewSettings(), 1750, true, f -> {
					viewAnimation.calcViewXform(viewReparam.applyAsFloat(f), viewXform);
					return viewXform;
				})));
		finisher = finisher.also(new AnimationViewSaver());
		protectedAnimations.put(finisher, null);
		cameraAnimationQueue.add(finisher);
	}

	protected void changeView(Set<ShotKey> shotsToFit) {
		float[] forward = new float[3];
		float[] right = new float[3];

		float[] vi = renderer.getViewState().inverseViewMatrix();

		Vecmath.negate3(vi, 8, forward, 0);
		Vecmath.getColumn3(vi, 0, right);

		changeView(forward, right, getProjectModel().get(ProjectModel.cameraView) != CameraView.PERSPECTIVE,
				shotsToFit);
	}

	protected void fitViewToEverything() {
		if (model3d == null) {
			return;
		}

		changeView(CollectionUtils.toHashSet(getShotsFromTable()));
	}

	protected void fitViewToSelected() {
		if (model3d == null) {
			return;
		}

		changeView(CollectionUtils.toHashSet(getSelectedShotsFromTable()));
	}

	protected void flyToFiltered(final AnnotatingJTable table) {
		if (model3d == null) {
			return;
		}

		removeUnprotectedCameraAnimations();

		if (getProjectModel().get(ProjectModel.cameraView) == CameraView.PERSPECTIVE) {
			float[] center = new float[3];
			orbiter.getCenter(center);

			if (Vecmath.hasNaNsOrInfinites(center)) {
				model3d.getCenter(center);
			}
			cameraAnimationQueue.add(new SpringViewOrbitAnimation(autoDrawable, renderer.getViewSettings(),
					center,
					0f, (float) -Math.PI / 4, .1f, .05f, 30));
			cameraAnimationQueue.add(new AnimationViewSaver());
		}
		cameraAnimationQueue.add(new Animation() {
			@Override
			public long animate(long animTime) {
				table.getModelSelectionModel().clearSelection();
				AnnotatingRowSorter<TableModel, Integer> rowSorter = (AnnotatingRowSorter<TableModel, Integer>) table
						.getAnnotatingRowSorter();
				if (rowSorter.getRowFilter() != null) {
					table.selectAll();
				} else {
					ListSelectionModel selectionModel = table.getSelectionModel();
					int intervalStart = -1;
					for (int row = 0; row < rowSorter.getViewRowCount(); row++) {
						if (rowSorter.getAnnotation(row) != null) {
							if (intervalStart < 0) {
								intervalStart = row;
							}
						} else if (intervalStart >= 0) {
							selectionModel.addSelectionInterval(intervalStart, row - 1);
							intervalStart = -1;
						}
					}
				}

				fitViewToSelected();

				if (getProjectModel().get(ProjectModel.cameraView) != CameraView.PERSPECTIVE) {
					return 0;
				}
				rebuildTaskService.submit(task -> SwingUtilities.invokeLater(() -> {

					float[] center = new float[3];
					orbiter.getCenter(center);

					if (Vecmath.hasNaNsOrInfinites(center)) {
						model3d.getCenter(center);
					}

					cameraAnimationQueue.add(new RandomViewOrbitAnimation(autoDrawable, renderer.getViewSettings(),
							center, 0.0005f, (float) -Math.PI / 4, (float) -Math.PI / 9, 30, 60000));
				}));
				return 0;
			}
		});
	}

	public GLAutoDrawable getAutoDrawable() {
		return autoDrawable;
	}

	public Component getCanvas() {
		return canvas;
	}

	public I18n getI18n() {
		return i18n;
	}

	public JPanel getMainPanel() {
		return mainPanel;
	}

	public JMenuBar getMenuBar() {
		return menuBar;
	}

	public NewProjectAction getNewProjectAction() {
		return newProjectAction;
	}

	public OpenProjectAction getOpenProjectAction() {
		return openProjectAction;
	}

	public QObject<ProjectModel> getProjectModel() {
		return projectModelBinder.get();
	}

	public Binder<QObject<ProjectModel>> getProjectModelBinder() {
		return projectModelBinder;
	}

	public Path getRootDirectory() {
		return rootDirectory;
	}

	public File getRootFile() {
		return rootFile;
	}

	public QObject<RootModel> getRootModel() {
		return rootModelBinder.get();
	}

	public Binder<QObject<RootModel>> getRootModelBinder() {
		return rootModelBinder;
	}

	public JoglScene getScene() {
		return scene;
	}

	protected Stream<ShotKey> getSelectedShotsFromTable() {
		SurveyTableModel model = surveyDrawer.table().getModel();
		ListSelectionModel selModel = surveyDrawer.table().getModelSelectionModel();
		return IntStream.range(0, model.getRowCount())
				.filter(selModel::isSelectedIndex)
				.mapToObj(modelIndexToShotKey::get)
				.filter(o -> o != null);
	}

	protected Stream<ShotKey> getShotsFromTable() {
		SurveyTableModel model = surveyDrawer.table().getModel();
		return IntStream.range(0, model.getRowCount())
				.mapToObj(modelIndexToShotKey::get)
				.filter(o -> o != null);
	}

	protected Set<ShotKey> getShotsInView() {
		Set<ShotKey> result = new HashSet<>();
		PlanarHull3f hull = new PlanarHull3f();
		renderer.getViewState().pickXform().exportViewVolume(hull, canvas.getWidth(), canvas.getHeight());
		model3d.getShotsIn(hull, result);
		if (result.isEmpty()) {
			result.addAll(calcProject.shots.keySet());
		}
		return result;
	}

	protected Set<ShotKey> getDefaultShotsForOperations(int minimumNumShots) {
		Set<ShotKey> result = new HashSet<>();
		getSelectedShotsFromTable().forEach(result::add);
		if (result.size() >= minimumNumShots) {
			return result;
		}
		result = getShotsInView();
		if (result.size() >= minimumNumShots) {
			return result;
		}
		return calcProject.shots.keySet();
	}

	public TaskListDrawer getTaskListDrawer() {
		return taskListDrawer;
	}

	public JoglViewSettings getViewSettings() {
		return renderer.getViewSettings();
	}

	public void importCompassFiles(List<File> files) {
		ioTaskService.submit(new ImportCompassTask(Lodash.map(files, file -> file.toPath())));
	}

	private void installOrthoMouseAdapters() {
		if (mouseAdapterChain != null) {
			mouseLooper.removeMouseAdapter(mouseAdapterChain);
		}
		mouseAdapterChain = new MouseAdapterChain();
		mouseAdapterChain.addMouseAdapter(orthoNavigator);
		mouseAdapterChain.addMouseAdapter(pickHandler);
		mouseAdapterChain.addMouseAdapter(autoshowController);
		mouseAdapterChain.addMouseAdapter(otherMouseHandler);
		mouseLooper.addMouseAdapter(mouseAdapterChain);
	}

	private void installPerspectiveMouseAdapters() {
		if (mouseAdapterChain != null) {
			mouseLooper.removeMouseAdapter(mouseAdapterChain);
		}
		mouseAdapterChain = new MouseAdapterChain();
		mouseAdapterChain.addMouseAdapter(pickHandler);
		mouseAdapterChain.addMouseAdapter(navigator);
		mouseAdapterChain.addMouseAdapter(orbiter);
		mouseAdapterChain.addMouseAdapter(autoshowController);
		mouseAdapterChain.addMouseAdapter(otherMouseHandler);
		mouseLooper.addMouseAdapter(mouseAdapterChain);
	}

	/**
	 * Opens the given project file.
	 *
	 * @param newProjectFile
	 *            the path to the project file to open; must be absolute or
	 *            relative to the working directory.
	 */
	public void openProject(Path newProjectFile) {
		ioTaskService.submit(new OpenProjectTask(newProjectFile));
	}

	public void openSurveyFile(Path newSurveyFile) {
		ioTaskService.submit(new OpenProjectTask(newSurveyFile));
	}

	private void openSurveyNotes(File file) {
		if (file == null || !file.exists()) {
			JXHyperlink searchDirsLink = new JXHyperlink(editSurveyScanPathsAction);
			searchDirsLink.setText("search directories");

			JPanel message = new JPanel(new FlowLayout());
			message.add(new JLabel("Couldn't find the file '" + file + "' in any of your "));
			message.add(searchDirsLink);
			message.add(new JLabel("(note: Breakout only searches " + SCANNED_NOTES_SEARCH_DEPTH +
					" levels deep)."));

			JOptionPane.showMessageDialog(mainPanel, message, "Can't find file", JOptionPane.ERROR_MESSAGE);

			return;
		}
		try {
			Desktop.getDesktop().open(file);
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(mainPanel, "Failed to open file '" + file + "': " + e,
					"Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	private void openSurveyNotes(String link) {
		URI uri = null;
		try {
			uri = new URL(link).toURI();
			Desktop.getDesktop().browse(uri);
			return;
		} catch (MalformedURLException e) {
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(mainPanel, "Failed to open URL " + uri + ": " + e,
					"Error", JOptionPane.ERROR_MESSAGE);
		}

		try {
			File file = new File(link);
			if (!file.isAbsolute()) {
				QArrayList<File> dirs = getProjectModel().get(ProjectModel.surveyScanPaths);
				if (dirs == null || dirs.isEmpty()) {
					int option = JOptionPane.showConfirmDialog(mainPanel,
							"There are no directories configured to search for the file: " + link
									+ ".  Would you like to configure them now?",
							"Can't find file",
							JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
					if (option == JOptionPane.YES_OPTION) {
						editSurveyScanPathsAction.actionPerformed(
								new ActionEvent(this, ActionEvent.ACTION_PERFORMED, ""));
						dirs = getProjectModel().get(ProjectModel.surveyScanPaths);
					}
				}
				if (dirs != null) {
					final QArrayList<File> finalDirs = dirs;
					ioTaskService.submit(new SelfReportingTask<Void>(mainPanel) {
						@Override
						protected Void workDuringDialog() throws Exception {
							setStatus("Searching for file: " + link + "...");
							setIndeterminate(true);

							showDialogLater();

							for (File dir : finalDirs) {
								Optional<Path> foundFile = Files
										.find(dir.toPath(), SCANNED_NOTES_SEARCH_DEPTH,
												(Path path, BasicFileAttributes attrs) -> {
													return path.toString().endsWith(link);
												}, FileVisitOption.FOLLOW_LINKS)
										.findFirst();
								if (foundFile.isPresent() && !isCanceled() && !isCanceled()) {
									SwingUtilities.invokeLater(() -> openSurveyNotes(foundFile.get().toFile()));
									return null;
								}
							}
							if (!isCanceled() && !isCanceled()) {
								SwingUtilities.invokeLater(() -> openSurveyNotes(file));
							}
							return null;
						}
					});
					return;
				}
				openSurveyNotes(file);
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

	public void perspectiveMode() {
		float[] forward = new float[3];
		float[] right = new float[3];

		Vecmath.negate3(renderer.getViewState().inverseViewMatrix(), 8, forward, 0);
		Vecmath.getColumn3(renderer.getViewState().inverseViewMatrix(), 0, right);

		changeView(forward, right, false, getDefaultShotsForOperations(1));
	}

	private Shot3dPickResult pick(Survey3dModel model3d, MouseEvent e, Shot3dPickContext spc) {
		PlanarHull3f hull = new PlanarHull3f();
		float[] origin = new float[3];
		float[] direction = new float[3];
		renderer
				.getViewState()
				.pickXform()
				.xform(e.getX(), e.getComponent().getHeight() - e.getY(), e.getComponent().getWidth(),
						e.getComponent().getHeight(), origin, direction);
		renderer.getViewState().pickXform().exportViewVolume(hull, e, 10);

		if (model3d != null) {
			List<PickResult<Shot3d>> pickResults = new ArrayList<>();
			model3d.pickShots(hull, spc, pickResults);

			PickResult<Shot3d> best = null;

			for (PickResult<Shot3d> result : pickResults) {
				if (best == null || result.lateralDistance * best.distance < best.lateralDistance * result.distance
						|| result.lateralDistance == 0 && best.lateralDistance == 0
								&& result.distance < best.distance) {
					best = result;
				}
			}

			return (Shot3dPickResult) best;
		}

		return null;
	}

	public void planMode() {
		changeView(new float[] { 0, -1, 0 }, new float[] { 1, 0, 0 }, true, getDefaultShotsForOperations(1));
	}

	protected void removeUnprotectedCameraAnimations() {
		cameraAnimationQueue.removeAll(anim -> !protectedAnimations.containsKey(anim));
	}

	private void saveProjection() {
		getProjectModel().set(ProjectModel.projCalculator, renderer.getViewSettings().getProjection());
	}

	private void saveViewXform() {
		float[] viewXform = Vecmath.newMat4f();
		renderer.getViewSettings().getViewXform(viewXform);
		getProjectModel().set(ProjectModel.viewXform, viewXform);
	}

	public void setCameraView(CameraView view) {
		if (view != currentView) {
			currentView = view;
			switch (view) {
			case PERSPECTIVE:
				perspectiveMode();
				break;
			case PLAN:
				planMode();
				break;
			case NORTH_FACING_PROFILE:
				northFacingProfileMode();
				break;
			case SOUTH_FACING_PROFILE:
				southFacingProfileMode();
				break;
			case EAST_FACING_PROFILE:
				eastFacingProfileMode();
				break;
			case WEST_FACING_PROFILE:
				westFacingProfileMode();
				break;
			case AUTO_PROFILE:
				autoProfileMode();
				break;
			}
		}
	}

	public Path getSwapFile(Path surveyFile) {
		if (surveyFile == null) {
			return null;
		}
		QObject<RootModel> rootModel = getRootModel();
		QMap<Path, Path, ?> swapFiles = rootModel.get(RootModel.swapFiles);
		if (swapFiles == null) {
			rootModel.set(RootModel.swapFiles, swapFiles = QLinkedHashMap.newInstance());
		}
		Path swapFile = swapFiles.get(surveyFile);
		if (swapFile == null) {
			int index = -1;
			do {
				swapFile = Paths.get(surveyFile.getFileName() + "-" + (++index) + ".json");
			} while (Files.exists(rootDirectory.resolve(swapFile)));
			swapFiles.put(surveyFile, swapFile);
		}
		return rootDirectory.resolve(swapFile);
	}

	private final BasicPropertyChangeListener projectModelChangeHandler = new BasicPropertyChangeListener() {
		@SuppressWarnings("unchecked")
		private QObject<ProjectModel> getProjectModel(Object source) {
			if (source instanceof QObject && ((QObject<?>) source).getSpec() == ProjectModel.instance) {
				return (QObject<ProjectModel>) source;
			}
			if (source instanceof SourcePath) {
				return getProjectModel(((SourcePath) source).parent);
			}
			return null;
		}

		@Override
		public void propertyChange(Object source, Object property, Object oldValue, Object newValue, int index) {
			QObject<ProjectModel> projectModel = getProjectModel(source);
			if (projectModel != null && property != ProjectModel.hasUnsavedChanges) {
				projectModel.set(ProjectModel.hasUnsavedChanges, true);
			}
			saveSwap.run();
		}
	};

	private final BasicPropertyChangeListener rootModelChangeHandler = new BasicPropertyChangeListener() {
		@Override
		public void propertyChange(Object source, Object property, Object oldValue, Object newValue, int index) {
			saveRootModel.run();
		}
	};

	private QObject<RootModel> loadRootModel(File file) {
		return loadModel(file, RootModel.defaultMapper, false);
	}

	private QObject<ProjectModel> loadProjectModel(File file) {
		return loadModel(file, ProjectModel.defaultMapper, false);
	}

	private <S extends QSpec<S>> QObject<S> loadModel(File file, Bimapper<QObject<S>, Object> mapper,
			boolean showError) {
		try (Reader reader = new FileReader(file)) {
			return mapper.unmap(new Gson().fromJson(reader, Object.class));
		} catch (Exception ex) {
			ex.printStackTrace();
			if (showError) {
				OnEDT.onEDT(new ExceptionRunnable() {
					@Override
					public void run() throws Exception {
						JOptionPane.showMessageDialog(mainPanel,
								"Failed to load settings: " + ex.getLocalizedMessage(),
								"Error", JOptionPane.ERROR_MESSAGE);
					}
				});
			}
			return null;
		}
	}

	public void setRootModel(QObject<RootModel> rootModel) {
		QObject<RootModel> currentModel = getRootModel();
		if (currentModel != rootModel) {
			if (currentModel != null) {
				currentModel.changeSupport().removePropertyChangeListener(rootModelChangeHandler);
			}
			rootModelBinder.set(rootModel);
			if (rootModel != null) {
				rootModel.changeSupport().addPropertyChangeListener(rootModelChangeHandler);
			}
		}
	}

	public void northFacingProfileMode() {
		changeView(new float[] { 0, 0, -1 }, new float[] { 1, 0, 0 }, true, getDefaultShotsForOperations(1));
	}

	public void southFacingProfileMode() {
		changeView(new float[] { 0, 0, 1 }, new float[] { -1, 0, 0 }, true, getDefaultShotsForOperations(1));
	}

	public void westFacingProfileMode() {
		changeView(new float[] { -1, 0, 0 }, new float[] { 0, 0, -1 }, true, getDefaultShotsForOperations(1));
	}

	public void eastFacingProfileMode() {
		changeView(new float[] { 1, 0, 0 }, new float[] { 0, 0, 1 }, true, getDefaultShotsForOperations(1));
	}

	public void autoProfileMode() {
		Set<ShotKey> shots = getDefaultShotsForOperations(1);
		List<float[]> forFitting = new ArrayList<>();
		for (ShotKey key : shots) {
			CalcShot shot = calcProject.shots.get(key);
			if (shot != null) {
				forFitting.add(new float[] {
						(float) shot.fromStation.position[0],
						(float) shot.fromStation.position[2]
				});
				forFitting.add(new float[] {
						(float) shot.toStation.position[0],
						(float) shot.toStation.position[2]
				});
			}
		}

		float[] fit = Fitting.linearLeastSquares2f(forFitting);

		if (Vecmath.hasNaNsOrInfinites(fit)) {
			return;
		}

		double azimuth = Math.atan2(1, -fit[0]);

		float[] right = new float[] { (float) Math.sin(azimuth), 0, (float) -Math.cos(azimuth) };
		float[] forward = new float[] { (float) Math.sin(azimuth - Math.PI * 0.5), 0,
				(float) -Math.cos(azimuth - Math.PI * 0.5) };

		if (Vecmath.dot3(renderer.getViewState().inverseViewMatrix(), 8, forward, 0) > 0) {
			Vecmath.negate3(right);
			Vecmath.negate3(forward);
		}

		changeView(forward, right, true, shots);
	}

	public Path getCurrentProjectFile() {
		QObject<RootModel> rootModel = getRootModel();
		if (rootModel == null || rootFile == null) {
			return null;
		}
		Path file = rootModel.get(RootModel.currentProjectFile);
		return file == null
				? null
				: rootFile.toPath().getParent().toAbsolutePath().resolve(file).normalize();
	}

	public Path getCurrentSwapFile() {
		return getSwapFile(getCurrentProjectFile());
	}

	public void newProject() {
		ioTaskService.submit(new NewProjectTask());
	}

	public void saveProject() {
		Path file = getCurrentProjectFile();
		if (file == null) {
			saveProjectAsAction.actionPerformed(
					new ActionEvent(saveProjectAsAction, ActionEvent.ACTION_PERFORMED, "saveProjectAs"));
			return;
		}
		saveProjectToFile(getCurrentProjectFile());
	}

	public void saveProjectToFile(Path projectFile) {
		ioTaskService.submit(task -> {
			task.setStatus("Saving project to " + projectFile);
			task.setIndeterminate(true);

			SurveyTableModel surveyModel = FromEDT.fromEDT(() -> surveyDrawer.table().getModel().clone());
			QObject<ProjectModel> projectModel = getProjectModel();

			if (projectFile == null || surveyModel == null) {
				return;
			}

			try (Writer out = new OutputStreamWriter(
					new RecoverableFileOutputStream(projectFile.toFile(), fileRecoveryConfig))) {
				if (!Files.exists(projectFile.getParent())) {
					projectFile.getParent().toFile().mkdirs();
				}
				MetacaveExporter exporter = new MetacaveExporter();
				exporter.export(surveyModel.getRows());
				JsonObject json = exporter.getRoot();
				Gson gson = new Gson();
				json.add("breakout", gson.toJsonTree(
						ProjectModel.defaultMapper.map(projectModel), Object.class));
				gson.toJson(json, out);
				getProjectModel().set(ProjectModel.hasUnsavedChanges, false);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		});
	}

	public void saveProjectAs(Path newProjectFile) {
		QObject<RootModel> rootModel = getRootModel();
		rootModel.set(RootModel.currentProjectFile, newProjectFile);
		markProjectRecentlyVisited(newProjectFile);
		saveProjectToFile(newProjectFile);
	}

	public boolean hasUnsavedChanges() {
		return hasUnsavedChanges(getProjectModel());
	}

	private void destroyCalculatedModel() {
		SwingUtilities.invokeLater(() -> {
			BreakoutMainView.this.shotKeyToModelIndex = Collections.emptyMap();
			BreakoutMainView.this.modelIndexToShotKey = Collections.emptyMap();
			BreakoutMainView.this.sourceRows = Collections.emptyMap();
			BreakoutMainView.this.parsedProject = new ParsedProject();
			BreakoutMainView.this.calcProject = new CalcProject();
			if (BreakoutMainView.this.model3d != null) {
				final Survey3dModel model3d = BreakoutMainView.this.model3d;
				BreakoutMainView.this.model3d = null;

				autoDrawable.invoke(false, drawable -> {
					scene.remove(model3d);
					scene.disposeLater(model3d);
					return false;
				});
			}
		});
	}

	public static boolean hasUnsavedChanges(QObject<ProjectModel> projectModel) {
		return Boolean.TRUE.equals(projectModel.get(ProjectModel.hasUnsavedChanges));
	}
}
