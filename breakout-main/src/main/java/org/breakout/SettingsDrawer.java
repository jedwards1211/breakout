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

import static org.andork.bind.ui.BindUI.bindBackground;
import static org.andork.bind.ui.BindUI.bindEnabled;
import static org.andork.bind.ui.BindUI.bindLayout;
import static org.andork.bind.ui.BindUI.bindSelected;
import static org.andork.bind.ui.BindUI.bindSelectedMap;
import static org.andork.bind.ui.BindUI.bindValue;
import static org.andork.q.QAutorun.autorun;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.LinearGradientPaint;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.text.DateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import java.util.Properties;

import javax.swing.AbstractButton;
import javax.swing.DefaultBoundedRangeModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JToggleButton;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.basic.BasicComboBoxUI;

import org.andork.awt.ColorUtils;
import org.andork.awt.GridBagWizard;
import org.andork.awt.GridBagWizard.DefaultAutoInsets;
import org.andork.awt.I18n;
import org.andork.awt.I18n.Localizer;
import org.andork.awt.layout.BetterCardLayout;
import org.andork.awt.layout.Drawer;
import org.andork.awt.layout.Side;
import org.andork.bind.Binder;
import org.andork.bind.BinderWrapper;
import org.andork.date.DateUtils;
import org.andork.func.Bimapper;
import org.andork.func.ExponentialFloatBimapper;
import org.andork.math.misc.Fitting;
import org.andork.model.Cell;
import org.andork.q.QHashMap;
import org.andork.q.QObject;
import org.andork.q.QSpec;
import org.andork.swing.CellRenderers;
import org.andork.swing.FloatSlider;
import org.andork.swing.OnEDT;
import org.andork.swing.PaintablePanel;
import org.andork.swing.border.FillBorder;
import org.andork.swing.border.GradientFillBorder;
import org.andork.swing.border.InnerGradientBorder;
import org.andork.swing.border.MultipleGradientFillBorder;
import org.andork.swing.border.OverrideInsetsBorder;
import org.andork.swing.selector.DefaultSelector;
import org.andork.unit.Angle;
import org.andork.unit.Area;
import org.andork.unit.Length;
import org.andork.unit.Unit;
import org.andork.unit.UnitizedDouble;
import org.breakout.model.ColorParam;
import org.breakout.model.GradientModel;
import org.breakout.model.Gradients;
import org.breakout.model.HighlightMode;
import org.breakout.model.ProjectModel;
import org.breakout.model.RootModel;
import org.breakout.proj4.CoordinateReferenceSystemPreset;
import org.jdesktop.swingx.JXColorSelectionButton;

import com.andork.plot.LinearAxisConversion;
import com.andork.plot.PlotAxis;
import com.andork.plot.PlotAxis.LabelPosition;
import com.andork.plot.PlotAxis.Orientation;
import com.andork.plot.PlotAxisController;

public class SettingsDrawer extends Drawer {
	/**
	 *
	 */
	private static final long serialVersionUID = -1786035787435736379L;

	Localizer localizer;

	JLabel titleLabel;
	ViewButtonsPanel viewButtonsPanel;
	JLabel mouseSensitivityLabel;
	FloatSlider mouseSensitivitySlider;
	JLabel mouseWheelSensitivityLabel;
	FloatSlider mouseWheelSensitivitySlider;
	JLabel colorsLabel;
	JLabel backgroundColorLabel;
	JXColorSelectionButton backgroundColorButton;
	JLabel stationLabelColorLabel;
	JXColorSelectionButton stationLabelColorButton;
	JLabel centerlineColorLabel;
	JXColorSelectionButton centerlineColorButton;

	DefaultSelector<Unit<Length>> displayLengthUnitSelector;
	DefaultSelector<Unit<Angle>> displayAngleUnitSelector;
	JLabel displayCoordinateReferenceSystemLabel;
	DefaultSelector<CoordinateReferenceSystemPreset> displayCoordinateReferenceSystemSelector;

	JLabel maxDateLabel;
	JButton prevYearButton;
	JButton prevMonthButton;
	JButton prevDayButton;
	JToggleButton playButton;
	JButton nextDayButton;
	JButton nextMonthButton;
	JButton nextYearButton;
	FloatSlider maxDateSlider;

	JLabel ambientLightLabel;
	FloatSlider ambientLightSlider;
	JLabel boldnessLabel;
	FloatSlider boldnessSlider;
	JLabel distColorationLabel;
	PlotAxis distColorationAxis;
	PaintablePanel distColorationAxisPanel;

	JLabel stationLabelsOffLabel;
	JLabel lessStationLabelDensityLabel;
	JLabel moreStationLabelDensityLabel;
	JLabel stationLabelFontSizeLabel;
	FloatSlider stationLabelFontSizeSlider;
	JLabel stationLabelDensityLabel;
	FloatSlider stationLabelDensitySlider;
	JCheckBox showLeadLabelsCheckBox;
	JCheckBox showCheckedLeadsCheckBox;
	JLabel centerlinesOffLabel;
	JLabel lessCenterlineDistanceLabel;
	JLabel moreCenterlineDistanceLabel;
	JLabel centerlineDistanceLabel;
	FloatSlider centerlineDistanceSlider;
	JCheckBox showTerrainCheckBox;

	JLabel colorParamLabel;
	DefaultSelector<ColorParam> colorParamSelector;
	JPanel colorParamButtonsPanel;
	BetterCardLayout colorParamButtonsLayout;
	JButton fitParamColorationAxisButton;
	JButton flipParamColorationAxisButton;
	JPanel colorParamDetailsPanel;
	BetterCardLayout colorParamDetailsLayout;
	PlotAxis paramColorationAxis;
	PaintablePanel paramColorationAxisPanel;

	JPanel colorByDepthButtonsPanel;
	JButton inferDepthAxisTiltButton;
	JButton resetDepthAxisTiltButton;
	JButton cameraToDepthAxisTiltButton;

	JPanel colorByDistanceButtonsPanel;
	JButton recalcColorByDistanceButton;

	JLabel highlightModeLabel;
	DefaultSelector<HighlightMode> highlightModeSelector;

	JLabel glowDistLabel;
	PlotAxis glowDistAxis;
	PaintablePanel glowDistAxisPanel;
	JButton resetViewButton;
	JButton fitViewToEverythingButton;
	JButton fitViewToSelectedButton;
	JButton orbitToPlanButton;
	JButton debugButton;

	JLabel numSamplesLabel;
	JSlider numSamplesSlider;

	JLabel versionLabel;

	JPanel mainPanel;
	JScrollPane mainPanelScrollPane;

	JLabel openSurveyScanCommandLabel;

	BinderWrapper<QObject<RootModel>> rootBinder = new BinderWrapper<>();

	BinderWrapper<QObject<ProjectModel>> projectBinder = new BinderWrapper<>();

	javax.swing.Timer maxDateTimer;

	private JButton pickParamGradientButton;

	private JComboBox<GradientModel> paramGradientComboBox;

	public SettingsDrawer(
		final I18n i18n,
		Binder<QObject<RootModel>> rootBinder,
		Binder<QObject<ProjectModel>> projectBinder) {
		this.rootBinder.bind(rootBinder);
		this.projectBinder.bind(projectBinder);

		new OnEDT() {
			@Override
			public void run() throws Throwable {
				localizer = i18n.forClass(SettingsDrawer.this.getClass());

				delegate().dockingSide(Side.RIGHT);

				Color background = getBackground();
				if (background == null) {
					background = new Color(228, 228, 228);
				}
				setUnderpaintBorder(
					GradientFillBorder
						.from(Side.TOP)
						.to(Side.BOTTOM)
						.colors(
							ColorUtils.darkerColor(background, 0.05),
							ColorUtils.darkerColor(Color.LIGHT_GRAY, 0.05)));
				setBorder(
					new OverrideInsetsBorder(
						new InnerGradientBorder(new Insets(0, 5, 0, 0), Color.GRAY),
						new Insets(0, 8, 0, 0)));

				createComponents(i18n);
				createLayout();
				createListeners();
				createBindings();

				org.andork.awt.AWTUtil.traverse(SettingsDrawer.this, comp -> {
					if (comp instanceof AbstractButton) {
						AbstractButton button = (AbstractButton) comp;
						button.setOpaque(false);
					}
				});
			}
		};
	}

	class IncMaxDate implements ActionListener {
		private final int field;
		private final boolean prev;
		private int amount = 1;

		public IncMaxDate(int field, boolean prev) {
			super();
			this.field = field;
			this.prev = prev;
		}

		public void setAmount(int amount) {
			this.amount = amount;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			Date date = ColorParam.calcDateFromDaysSince1800(getProjectAttribute(ProjectModel.maxDate));
			if (date == null) {
				date = ColorParam.calcDateFromDaysSince1800(maxDateSlider.getFloatMaximum());
				if (date == null)
					return;
			}
			date = prev ? DateUtils.lowerOf(date, field) : DateUtils.higherOf(date, field);
			if (Math.abs(amount) != 1) {
				date = DateUtils.add(date, field, amount - (int) Math.signum(amount));
			}
			Date now = new Date();
			if (date.compareTo(now) >= 0) {
				date = now;
			}
			setProjectAttribute(ProjectModel.maxDate, ColorParam.calcDaysSince1800(date));
		}
	}

	IncMaxDate maxDateAnimation = new IncMaxDate(Calendar.MONTH, false);

	private <T> T getProjectAttribute(QSpec.Attribute<T> attribute) {
		QObject<ProjectModel> project = projectBinder.get();
		return project == null ? null : project.get(attribute);
	}

	private <T> T setProjectAttribute(QSpec.Attribute<T> attribute, T value) {
		QObject<ProjectModel> project = projectBinder.get();
		return project == null ? null : project.set(attribute, value);
	}

	private <T> Cell<T> projectAttribute(QSpec.Attribute<T> attribute) {
		return Cell.from(() -> projectBinder.ifDefined(p -> p.attribute(attribute)));
	}

	private <T> Cell<T> rootAttribute(QSpec.Attribute<T> attribute) {
		return Cell.from(() -> rootBinder.ifDefined(p -> p.attribute(attribute)));
	}

	private void createBindings() {
		bindBackground(backgroundColorButton, projectAttribute(ProjectModel.backgroundColor));
		bindBackground(stationLabelColorButton, projectAttribute(ProjectModel.stationLabelColor));
		bindBackground(centerlineColorButton, projectAttribute(ProjectModel.centerlineColor));

		bindSelectedMap(projectAttribute(ProjectModel.cameraView))
			.map(CameraView.PLAN, viewButtonsPanel.getPlanButton())
			.map(CameraView.SIDEWAYS_PLAN, viewButtonsPanel.getSidewaysPlanButton())
			.map(CameraView.PERSPECTIVE, viewButtonsPanel.getPerspectiveButton())
			.map(CameraView.NORTH_FACING_PROFILE, viewButtonsPanel.getNorthButton())
			.map(CameraView.SOUTH_FACING_PROFILE, viewButtonsPanel.getSouthButton())
			.map(CameraView.EAST_FACING_PROFILE, viewButtonsPanel.getEastButton())
			.map(CameraView.WEST_FACING_PROFILE, viewButtonsPanel.getWestButton())
			.map(CameraView.AUTO_PROFILE, viewButtonsPanel.getAutoProfileButton());

		// This is a bit confusing... used to map the linear sliders to an exponential
		// curve.
		// The numbers are: rawMin, sliderMin, rawMax, sliderMax, rawAtSliderMid
		// In other words, when the slider is at sliderMin, the value in the model will
		// be rawMin.
		double[] mouseSensitivityCurve = Fitting.threePointExponential(1, 0, 1000, 100, 70);
		double[] wheelSensitivityCurve = Fitting.threePointExponential(1, 0, 5000, 100, 70);

		Bimapper<Integer, Float> int2float =
			Bimapper.from(i -> i == null ? null : i.floatValue(), f -> f == null ? null : f.intValue());

		bindValue(
			mouseSensitivitySlider,
			Cell.compose(rootAttribute(RootModel.mouseSensitivity), int2float),
			new ExponentialFloatBimapper(mouseSensitivityCurve));
		bindValue(
			mouseWheelSensitivitySlider,
			Cell.compose(rootAttribute(RootModel.mouseWheelSensitivity), int2float),
			new ExponentialFloatBimapper(wheelSensitivityCurve));

		autorun(() -> {
			GradientModel gradient = getProjectAttribute(ProjectModel.paramGradient);
			if (gradient == null)
				return;
			paramColorationAxisPanel
				.setUnderpaintBorder(
					MultipleGradientFillBorder
						.from(Side.LEFT)
						.to(Side.RIGHT)
						.linear(gradient.fractions, gradient.colors));

		});
		bindValue(colorParamSelector, projectAttribute(ProjectModel.colorParam));
		bindLayout(colorParamDetailsPanel, colorParamDetailsLayout, projectAttribute(ProjectModel.colorParam));
		bindLayout(colorParamButtonsPanel, colorParamButtonsLayout, projectAttribute(ProjectModel.colorParam));

		paramColorationAxis.bind(Cell.from(() -> {
			QObject<ProjectModel> project = projectBinder.get();
			if (project == null)
				return null;

			ColorParam colorParam = project.get(ProjectModel.colorParam);
			if (colorParam == null)
				return null;
			LinearAxisConversion paramConversion = project.get(ProjectModel.paramRanges).get(colorParam);
			if (paramConversion == null)
				return null;

			Unit<Length> lengthUnit = project.get(ProjectModel.displayLengthUnit);
			if (lengthUnit == null || colorParam.getUnitType() == null)
				return paramConversion;

			@SuppressWarnings("rawtypes")
			Unit systemUnit = Length.meters;
			@SuppressWarnings("rawtypes")
			Unit displayUnit = Length.meters;
			if (colorParam.getUnitType() == Length.type) {
				displayUnit = lengthUnit;
			}
			else if (colorParam.getUnitType() == Area.type) {
				systemUnit = Area.square(systemUnit);
				displayUnit = Area.square(lengthUnit);
			}

			return new LinearAxisConversion(
				new UnitizedDouble<>(paramConversion.invert(0), systemUnit).get(displayUnit),
				0,
				new UnitizedDouble<>(paramConversion.invert(1), systemUnit).get(displayUnit),
				1);
		}, (LinearAxisConversion paramConversion) -> {
			QObject<ProjectModel> project = projectBinder.get();
			if (project == null || paramConversion == null)
				return;

			ColorParam colorParam = project.get(ProjectModel.colorParam);
			if (colorParam == null)
				return;

			if (project.get(ProjectModel.paramRanges) == null) {
				project.set(ProjectModel.paramRanges, new QHashMap<>());
			}
			Unit<Length> lengthUnit = project.get(ProjectModel.displayLengthUnit);
			if (lengthUnit == null || colorParam == null || colorParam.getUnitType() == null) {
				project.get(ProjectModel.paramRanges).set(colorParam, paramConversion);
				return;
			}
			@SuppressWarnings("rawtypes")
			Unit systemUnit = Length.meters;
			@SuppressWarnings("rawtypes")
			Unit displayUnit = Length.meters;
			if (colorParam.getUnitType() == Length.type) {
				displayUnit = lengthUnit;
			}
			else if (colorParam.getUnitType() == Area.type) {
				systemUnit = Area.square(systemUnit);
				displayUnit = Area.square(lengthUnit);
			}
			project
				.get(ProjectModel.paramRanges)
				.set(
					colorParam,
					new LinearAxisConversion(
						new UnitizedDouble<>(paramConversion.invert(0), displayUnit).get(systemUnit),
						0,
						new UnitizedDouble<>(paramConversion.invert(1), displayUnit).get(systemUnit),
						1));
		}));

		Bimapper<LinearAxisConversion, LinearAxisConversion> distConversionBimapper = Bimapper.from((systemConv) -> {
			Unit<Length> displayLengthUnit = getProjectAttribute(ProjectModel.displayLengthUnit);
			if (systemConv == null)
				return null;
			if (displayLengthUnit == null)
				displayLengthUnit = Length.meters;
			return new LinearAxisConversion(
				Length.meters(systemConv.invert(0)).get(displayLengthUnit),
				0,
				Length.meters(systemConv.invert(1)).get(displayLengthUnit),
				1);
		}, (displayConv) -> {
			Unit<Length> displayLengthUnit = getProjectAttribute(ProjectModel.displayLengthUnit);
			if (displayConv == null)
				return null;
			if (displayLengthUnit == null)
				displayLengthUnit = Length.meters;
			return new LinearAxisConversion(
				new UnitizedDouble<>(displayConv.invert(0), displayLengthUnit).get(Length.meters),
				0,
				new UnitizedDouble<>(displayConv.invert(1), displayLengthUnit).get(Length.meters),
				1);
		});

		distColorationAxis.bind(projectAttribute(ProjectModel.distRange), distConversionBimapper);

		bindValue(highlightModeSelector, projectAttribute(ProjectModel.highlightMode));
		glowDistAxis.bind(projectAttribute(ProjectModel.highlightRange), distConversionBimapper);

		autorun(() -> {
			updateMaxDateLabelText(ColorParam.calcDateFromDaysSince1800(getProjectAttribute(ProjectModel.maxDate)));
		});

		bindValue(
			maxDateSlider,
			projectAttribute(ProjectModel.maxDate),
			Bimapper
				.from(
					value -> value == null ? maxDateSlider.getFloatMaximum() : value,
					value -> value != null && value >= maxDateSlider.getFloatMaximum() ? null : value));

		prevYearButton.addActionListener(new IncMaxDate(Calendar.YEAR, true));
		prevMonthButton.addActionListener(new IncMaxDate(Calendar.MONTH, true));
		prevDayButton.addActionListener(new IncMaxDate(Calendar.DATE, true));
		nextDayButton.addActionListener(new IncMaxDate(Calendar.DATE, false));
		nextMonthButton.addActionListener(new IncMaxDate(Calendar.MONTH, false));
		nextYearButton.addActionListener(new IncMaxDate(Calendar.YEAR, false));

		maxDateTimer = new Timer(1000 / 12, maxDateAnimation);

		autorun(() -> {
			Integer framerate = getProjectAttribute(ProjectModel.maxDateAnimationFramerate);
			if (framerate != null)
				maxDateTimer.setDelay(Math.round(1000 / framerate));
		});
		autorun(() -> {
			Integer monthsPerFrame = getProjectAttribute(ProjectModel.maxDateAnimationMonthsPerFrame);
			if (monthsPerFrame != null)
				maxDateAnimation.setAmount(monthsPerFrame);
		});

		playButton.addItemListener(e -> {
			switch (e.getStateChange()) {
			case ItemEvent.SELECTED:
				maxDateTimer.start();
				break;
			case ItemEvent.DESELECTED:
				maxDateTimer.stop();
				break;
			}
		});

		bindValue(ambientLightSlider, projectAttribute(ProjectModel.ambientLight));
		bindValue(boldnessSlider, projectAttribute(ProjectModel.boldness));

		bindValue(centerlineDistanceSlider, projectAttribute(ProjectModel.centerlineDistance));
		bindValue(stationLabelDensitySlider, projectAttribute(ProjectModel.stationLabelDensity));

		bindValue(stationLabelFontSizeSlider, projectAttribute(ProjectModel.stationLabelFontSize));

		bindSelected(showLeadLabelsCheckBox, projectAttribute(ProjectModel.showLeadLabels));
		bindSelected(showCheckedLeadsCheckBox, projectAttribute(ProjectModel.showCheckedLeads));
		bindEnabled(showCheckedLeadsCheckBox, projectAttribute(ProjectModel.showLeadLabels));
		bindSelected(showTerrainCheckBox, projectAttribute(ProjectModel.showTerrain));

		bindValue(numSamplesSlider, rootAttribute(RootModel.desiredNumSamples));

		bindValue(displayLengthUnitSelector, projectAttribute(ProjectModel.displayLengthUnit));
		bindValue(displayAngleUnitSelector, projectAttribute(ProjectModel.displayAngleUnit));
		bindValue(
			displayCoordinateReferenceSystemSelector,
			projectAttribute(ProjectModel.displayCoordinateReferenceSystem));

		bindValue(paramGradientComboBox, projectAttribute(ProjectModel.paramGradient));
	}

	private void createComponents(I18n i18n) {
		titleLabel = new JLabel();
		Font smallFont = titleLabel.getFont().deriveFont(titleLabel.getFont().getSize() * 0.8f);
		titleLabel.setFont(titleLabel.getFont().deriveFont(titleLabel.getFont().getSize() * 1.5f));
		localizer.setText(titleLabel, "titleLabel.text");

		viewButtonsPanel = new ViewButtonsPanel();

		Color darkColor = new Color(255 * 3 / 10, 255 * 3 / 10, 255 * 3 / 10);

		colorsLabel = new JLabel();
		localizer.setText(colorsLabel, "colorsLabel.text");

		stationLabelsOffLabel = new JLabel();
		localizer.setText(stationLabelsOffLabel, "offLabel.text");
		lessStationLabelDensityLabel = new JLabel();
		localizer.setText(lessStationLabelDensityLabel, "lessLabel.text");
		moreStationLabelDensityLabel = new JLabel();
		localizer.setText(moreStationLabelDensityLabel, "moreLabel.text");
		stationLabelDensityLabel = new JLabel();
		localizer.setText(stationLabelDensityLabel, "stationLabelDensityLabel.text");
		stationLabelDensitySlider = new FloatSlider(0, 600, 40);

		stationLabelFontSizeLabel = new JLabel();
		localizer.setText(stationLabelFontSizeLabel, "stationLabelFontSizeLabel.text");
		stationLabelFontSizeSlider = new FloatSlider(8, 72, 12);

		stationLabelColorLabel = new JLabel();
		localizer.setText(stationLabelColorLabel, "stationLabelColorLabel.text");
		stationLabelColorButton = new JXColorSelectionButton();

		showLeadLabelsCheckBox = new JCheckBox();
		localizer.setText(showLeadLabelsCheckBox, "showLeadLabelsCheckBox.text");
		showCheckedLeadsCheckBox = new JCheckBox();
		localizer.setText(showCheckedLeadsCheckBox, "showCheckedLeadsCheckBox.text");

		centerlinesOffLabel = new JLabel();
		localizer.setText(centerlinesOffLabel, "offLabel.text");
		lessCenterlineDistanceLabel = new JLabel();
		localizer.setText(lessCenterlineDistanceLabel, "closerLabel.text");
		moreCenterlineDistanceLabel = new JLabel();
		localizer.setText(moreCenterlineDistanceLabel, "fartherLabel.text");
		centerlineDistanceLabel = new JLabel();
		localizer.setText(centerlineDistanceLabel, "centerlineDistanceLabel.text");
		centerlineDistanceSlider = new FloatSlider(0, 10000, 1000);

		showTerrainCheckBox = new JCheckBox();
		localizer.setText(showTerrainCheckBox, "showTerrainCheckBox.text");

		for (JLabel label : Arrays
			.asList(
				stationLabelsOffLabel,
				lessStationLabelDensityLabel,
				moreStationLabelDensityLabel,
				centerlinesOffLabel,
				lessCenterlineDistanceLabel,
				moreCenterlineDistanceLabel)) {
			label.setFont(smallFont);
		}

		centerlineColorLabel = new JLabel();
		localizer.setText(centerlineColorLabel, "centerlineColorLabel.text");
		centerlineColorButton = new JXColorSelectionButton();

		backgroundColorLabel = new JLabel();
		localizer.setText(backgroundColorLabel, "backgroundColorLabel.text");
		backgroundColorButton = new JXColorSelectionButton();

		displayLengthUnitSelector = new DefaultSelector<>();
		displayLengthUnitSelector.setAvailableValues(Length.meters, Length.feet);

		displayAngleUnitSelector = new DefaultSelector<>();
		displayAngleUnitSelector.setAvailableValues(Angle.degrees, Angle.gradians, Angle.milsNATO);

		displayCoordinateReferenceSystemLabel = new JLabel();
		localizer.setText(displayCoordinateReferenceSystemLabel, "displayCoordinateReferenceSystemLabel.text");

		displayCoordinateReferenceSystemSelector = new DefaultSelector<>();
		displayCoordinateReferenceSystemSelector.setAvailableValues(CoordinateReferenceSystemPreset.values());

		maxDateLabel = new JLabel();
		maxDateLabel.setPreferredSize(new Dimension(200, 20));
		updateMaxDateLabelText(null);
		prevYearButton = new JButton(new ImageIcon(getClass().getResource("prevYear.png")));
		prevMonthButton = new JButton(new ImageIcon(getClass().getResource("prevMonth.png")));
		prevDayButton = new JButton(new ImageIcon(getClass().getResource("prevDay.png")));
		playButton = new JToggleButton(new ImageIcon(getClass().getResource("play.png")));
		nextDayButton = new JButton(new ImageIcon(getClass().getResource("nextDay.png")));
		nextMonthButton = new JButton(new ImageIcon(getClass().getResource("nextMonth.png")));
		nextYearButton = new JButton(new ImageIcon(getClass().getResource("nextYear.png")));

		for (AbstractButton button : new AbstractButton[] {
			prevYearButton,
			prevMonthButton,
			prevDayButton,
			playButton,
			nextDayButton,
			nextMonthButton,
			nextYearButton }) {
			button.setMargin(new Insets(2, 2, 2, 2));
		}

		Date now = new Date();
		maxDateSlider =
			new FloatSlider(
				ColorParam.calcDaysSince1800(new Date(0)),
				ColorParam.calcDaysSince1800(now),
				ColorParam.calcDaysSince1800(now));
		maxDateSlider.setOpaque(false);

		ambientLightLabel = new JLabel();
		localizer.setText(ambientLightLabel, "ambientLightLabel.text");
		ambientLightSlider = new FloatSlider(0f, 1f, 0.5f);
		ambientLightSlider.setOpaque(false);

		boldnessLabel = new JLabel();
		localizer.setText(boldnessLabel, "boldnessLabel.text");
		boldnessSlider = new FloatSlider(0f, 5f, 0f);
		boldnessSlider.setOpaque(false);

		distColorationLabel = new JLabel();
		localizer.setText(distColorationLabel, "distColorationLabel.text");

		distColorationAxis = new PlotAxis(Orientation.HORIZONTAL, LabelPosition.TOP);
		distColorationAxisPanel = PaintablePanel.wrap(distColorationAxis);
		distColorationAxisPanel
			.setUnderpaintBorder(MultipleGradientFillBorder.from(Side.LEFT).to(Side.RIGHT).linear(new float[]
			{ 0f, 1f }, new Color[] { ColorUtils.alphaColor(darkColor, 0), darkColor }));

		distColorationAxis.getAxisConversion().set(0, 0, 10000, 200);
		distColorationAxis.setForeground(Color.WHITE);
		distColorationAxis.setMajorTickColor(Color.WHITE);
		distColorationAxis.setMinorTickColor(Color.WHITE);

		colorParamLabel = new JLabel();
		localizer.setText(colorParamLabel, "colorParamLabel.text");
		colorParamSelector = new DefaultSelector<>();
		colorParamSelector.setAvailableValues(ColorParam.values());
		colorParamButtonsPanel = new JPanel();
		colorParamButtonsPanel.setOpaque(false);
		fitParamColorationAxisButton = new JButton(new ImageIcon(getClass().getResource("fit.png")));
		fitParamColorationAxisButton.setMargin(new Insets(2, 2, 2, 2));
		localizer.setToolTipText(fitParamColorationAxisButton, "fitParamColorationAxisButton.tooltip");
		flipParamColorationAxisButton = new JButton(new ImageIcon(getClass().getResource("flip.png")));
		flipParamColorationAxisButton.setMargin(new Insets(2, 2, 2, 2));
		localizer.setToolTipText(flipParamColorationAxisButton, "flipParamColorationAxisButton.tooltip");
		colorParamDetailsPanel = new JPanel();
		colorParamDetailsPanel.setOpaque(false);

		paramColorationAxis = new PlotAxis(Orientation.HORIZONTAL, LabelPosition.TOP);
		paramColorationAxisPanel = PaintablePanel.wrap(paramColorationAxis);
		paramColorationAxisPanel
			.setUnderpaintBorder(
				MultipleGradientFillBorder
					.from(Side.LEFT)
					.to(Side.RIGHT)
					.linear(Gradients.DEFAULT.fractions, Gradients.DEFAULT.colors));

		paramColorationAxis.setForeground(Color.WHITE);
		paramColorationAxis.setMajorTickColor(Color.WHITE);
		paramColorationAxis.setMinorTickColor(Color.WHITE);

		pickParamGradientButton = new JButton();
		pickParamGradientButton
			.setIcon(new TriangleIcon(SwingConstants.SOUTH, 5, Color.WHITE, Color.WHITE, Color.WHITE));
		pickParamGradientButton.setBackground(null);
		pickParamGradientButton.setBorder(new EmptyBorder(12, 12, 12, 12));
		pickParamGradientButton.setContentAreaFilled(false);
		paramColorationAxisPanel.add(pickParamGradientButton, BorderLayout.EAST);

		paramColorationAxisPanel.setLayout(new LayoutManager() {
			@Override
			public void removeLayoutComponent(Component comp) {
			}

			@Override
			public Dimension preferredLayoutSize(Container parent) {
				return paramColorationAxis.getPreferredSize();
			}

			@Override
			public Dimension minimumLayoutSize(Container parent) {
				return paramColorationAxis.getMinimumSize();
			}

			@Override
			public void layoutContainer(Container parent) {
				int height = parent.getHeight();
				paramColorationAxis.setBounds(0, 0, parent.getWidth(), height);
				pickParamGradientButton.setBounds(parent.getWidth() - height, 0, height, height);
				parent.setComponentZOrder(pickParamGradientButton, 0);
				parent.setComponentZOrder(paramColorationAxis, 1);
			}

			@Override
			public void addLayoutComponent(String name, Component comp) {
			}
		});

		paramGradientComboBox = new JComboBox<GradientModel>();
		paramGradientComboBox.setPreferredSize(new Dimension(200, 1));
		paramGradientComboBox.setUI(new BasicComboBoxUI());
		paramGradientComboBox.setRenderer(new DefaultListCellRenderer() {
			@Override
			public Component getListCellRendererComponent(
				JList<?> list,
				Object value,
				int index,
				boolean isSelected,
				boolean cellHasFocus) {
				GradientModel gradient = (GradientModel) value;
				JComponent comp =
					(JComponent) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
				comp.setPreferredSize(new Dimension(paramColorationAxisPanel.getWidth(), 25));
				comp
					.setBorder(
						MultipleGradientFillBorder
							.from(Side.LEFT)
							.to(Side.RIGHT)
							.linear(gradient.fractions, gradient.colors));
				return comp;
			}
		});
		for (GradientModel gradient : Gradients.CHOICES) {
			paramGradientComboBox.addItem(gradient);
		}

		colorByDepthButtonsPanel = new JPanel();
		colorByDepthButtonsPanel.setOpaque(false);
		inferDepthAxisTiltButton = new JButton(new ImageIcon(getClass().getResource("tilted-depth-axis.png")));
		inferDepthAxisTiltButton.setMargin(new Insets(2, 2, 2, 2));
		localizer.setToolTipText(inferDepthAxisTiltButton, "inferDepthAxisTiltButton.tooltip");
		resetDepthAxisTiltButton = new JButton(new ImageIcon(getClass().getResource("vertical-depth-axis.png")));
		resetDepthAxisTiltButton.setMargin(new Insets(2, 2, 2, 2));
		localizer.setToolTipText(resetDepthAxisTiltButton, "resetDepthAxisTiltButton.tooltip");
		cameraToDepthAxisTiltButton = new JButton(new ImageIcon(getClass().getResource("forward-depth-axis.png")));
		cameraToDepthAxisTiltButton.setMargin(new Insets(2, 2, 2, 2));
		localizer.setToolTipText(cameraToDepthAxisTiltButton, "cameraToDepthAxisTiltButton.tooltip");

		colorByDistanceButtonsPanel = new JPanel();
		colorByDistanceButtonsPanel.setOpaque(false);
		recalcColorByDistanceButton = new JButton(new ImageIcon(getClass().getResource("refresh.png")));
		recalcColorByDistanceButton.setMargin(new Insets(2, 2, 2, 2));
		localizer.setToolTipText(recalcColorByDistanceButton, "recalcColorByDistanceButton.tooltip");

		highlightModeLabel = new JLabel();
		localizer.setText(highlightModeLabel, "highlightModeLabel.text");

		highlightModeSelector = new DefaultSelector<>();
		highlightModeSelector.setAvailableValues(HighlightMode.values());
		highlightModeSelector
			.comboBox()
			.setRenderer(
				CellRenderers
					.map(
						value -> localizer.getString("highlightModeSelector.text." + Objects.toString(value)),
						highlightModeSelector.comboBox().getRenderer()));

		glowDistLabel = new JLabel();
		localizer.setText(glowDistLabel, "glowDistLabel.text");

		glowDistAxis = new PlotAxis(Orientation.HORIZONTAL, LabelPosition.TOP);
		glowDistAxisPanel = PaintablePanel.wrap(glowDistAxis);
		glowDistAxisPanel
			.setUnderpaintBorder(MultipleGradientFillBorder.from(Side.LEFT).to(Side.RIGHT).linear(new float[]
			{ 0f, 1f }, new Color[] { Color.CYAN, ColorUtils.alphaColor(Color.CYAN, 0) }));

		glowDistAxis.setForeground(Color.BLACK);
		glowDistAxis.setMajorTickColor(Color.BLACK);
		glowDistAxis.setMinorTickColor(Color.BLACK);

		mouseSensitivityLabel = new JLabel();
		localizer.setText(mouseSensitivityLabel, "mouseSensitivityLabel.text");

		mouseSensitivitySlider = new FloatSlider(0, 100, 20);
		mouseSensitivitySlider.setValue(20);
		mouseSensitivitySlider.setOpaque(false);

		mouseWheelSensitivityLabel = new JLabel();
		localizer.setText(mouseWheelSensitivityLabel, "mouseWheelSensitivityLabel.text");

		mouseWheelSensitivitySlider = new FloatSlider(0, 100, 20);
		mouseWheelSensitivitySlider.setOpaque(false);

		resetViewButton = new JButton("Reset View");
		fitViewToSelectedButton = new JButton("Fit to Selected");
		fitViewToEverythingButton = new JButton("Fit to Everything");
		orbitToPlanButton = new JButton("Orbit to Plan");

		numSamplesLabel = new JLabel();
		localizer.setText(numSamplesLabel, "numSamplesLabel.text.off");
		numSamplesSlider = new JSlider(1, 1, 1);
		numSamplesSlider.setOpaque(false);
		numSamplesSlider.setEnabled(false);

		debugButton = new JButton("Debug");

		mainPanel = new JPanel();
		mainPanel.setBorder(new EmptyBorder(5, 0, 5, 5));
		mainPanel.setOpaque(false);
		mainPanelScrollPane = new JScrollPane(mainPanel);
		mainPanelScrollPane.setBorder(null);
		mainPanelScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		mainPanelScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		mainPanelScrollPane.setOpaque(false);
		mainPanelScrollPane.getViewport().setOpaque(false);
		JScrollBar verticalScrollBar = mainPanelScrollPane.getVerticalScrollBar();
		verticalScrollBar.setUnitIncrement(5);

		Dimension iconButtonSize = flipParamColorationAxisButton.getPreferredSize();
		iconButtonSize.height = colorParamSelector.comboBox().getPreferredSize().height;
		fitParamColorationAxisButton.setPreferredSize(iconButtonSize);
		recalcColorByDistanceButton.setPreferredSize(iconButtonSize);
		inferDepthAxisTiltButton.setPreferredSize(iconButtonSize);
		resetDepthAxisTiltButton.setPreferredSize(iconButtonSize);

		versionLabel = new JLabel();
		versionLabel.setHorizontalAlignment(SwingConstants.CENTER);
		localizer
			.setFormattedText(
				versionLabel,
				"versionLabel.text",
				BreakoutMain.getVersion(),
				BreakoutMain.getBuildDate());
	}

	private void createLayout() {
		GridBagWizard w = GridBagWizard.create(mainPanel);
		w.defaults().autoinsets(new DefaultAutoInsets(3, 3));
		GridBagWizard titlePanel = GridBagWizard.quickPanel();
		titlePanel.put(pinButton()).xy(0, 0).northwest();
		titlePanel.put(titleLabel).rightOfLast().fillx(1.0).insets(10, 3, 3, 3);
		w.put(titlePanel.getTarget()).xy(0, 0).fillx(1.0);

		w.put(viewButtonsPanel).belowLast().addToInsets(10, 0, 0, 0);
		GridBagWizard autoButtonPanel = GridBagWizard.quickPanel();
		autoButtonPanel.put(resetViewButton).xy(0, 0).fillx(1.0);
		autoButtonPanel.put(fitViewToSelectedButton).rightOfLast().width(1).fillx(1.0);
		autoButtonPanel.put(orbitToPlanButton).below(resetViewButton).width(1).fillx(1.0);
		autoButtonPanel.put(fitViewToEverythingButton).rightOfLast().width(1).fillx(1.0);
		w.put(autoButtonPanel.getTarget()).belowLast().fillx(1.0);

		w.put(mouseSensitivityLabel).belowLast().west().addToInsets(10, 0, 0, 0);
		w.put(mouseSensitivitySlider).belowLast().fillx().north();

		w.put(mouseWheelSensitivityLabel).belowLast().west().addToInsets(0, 0, 0, 0);
		w.put(mouseWheelSensitivitySlider).belowLast().fillx().north();

		// w.put(colorsLabel).belowLast().fillx().addToInsets(0, 0, 0, 0);

		GridBagWizard colorsPanel = GridBagWizard.quickPanel();
		colorsPanel.put(backgroundColorLabel, stationLabelColorLabel, centerlineColorLabel).intoRow().y(0);
		colorsPanel.put(backgroundColorButton).below(backgroundColorLabel).fillx(1.0).insets(0, 0, 0, 0);
		colorsPanel.put(stationLabelColorButton).below(stationLabelColorLabel).fillx(1.0).insets(0, 0, 0, 0);
		colorsPanel.put(centerlineColorButton).below(centerlineColorLabel).fillx(1.0).insets(0, 0, 0, 0);
		w.put(colorsPanel.getTarget()).belowLast().fillx().addToInsets(5, 0, 5, 0);

		GridBagWizard unitsPanel = GridBagWizard.quickPanel();
		unitsPanel
			.put(displayLengthUnitSelector.comboBox(), displayAngleUnitSelector.comboBox())
			.y(0)
			.intoRow()
			.fillx(1.0);
		w.put(unitsPanel.getTarget()).belowLast().fillx();

		w.put(displayCoordinateReferenceSystemLabel).belowLast().fillx();
		w.put(displayCoordinateReferenceSystemSelector.comboBox()).belowLast().fillx();
		// TODO: make these visible once transforming between coordinate systems works
		displayCoordinateReferenceSystemLabel.setVisible(false);
		displayCoordinateReferenceSystemSelector.comboBox().setVisible(false);

		w.put(colorParamLabel).belowLast().west();
		GridBagWizard colorParamPanel = GridBagWizard.quickPanel();
		colorParamPanel.put(colorParamSelector.comboBox()).xy(0, 0).fillboth(1.0, 0.0).addToInsets(0, 5, 0, 0);
		colorParamButtonsPanel.setLayout(colorParamButtonsLayout = new BetterCardLayout());
		colorParamButtonsLayout.setSizeHidden(false);
		colorParamPanel.put(colorParamButtonsPanel).rightOfLast().filly(1.0);
		colorParamPanel.put(fitParamColorationAxisButton).rightOfLast().filly();
		colorParamPanel.put(flipParamColorationAxisButton).rightOfLast().filly();
		w.put(colorParamPanel.getTarget()).belowLast().fillx();
		colorParamDetailsPanel.setLayout(colorParamDetailsLayout = new BetterCardLayout());
		colorParamDetailsLayout.setSizeHidden(false);
		w.put(colorParamDetailsPanel).belowLast().fillx();
		w.put(paramColorationAxisPanel).belowLast().fillx().addToInsets(0, 0, 0, 0);
		w.put(paramGradientComboBox).belowLast().fillx().addToInsets(-5, 0, 5, 0);

		colorByDepthButtonsPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 0, 0));
		colorByDepthButtonsPanel.add(inferDepthAxisTiltButton);
		colorByDepthButtonsPanel.add(resetDepthAxisTiltButton);
		colorByDepthButtonsPanel.add(cameraToDepthAxisTiltButton);
		colorParamButtonsPanel.add(colorByDepthButtonsPanel, ColorParam.DEPTH);

		colorByDistanceButtonsPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 0, 0));
		colorByDistanceButtonsPanel.add(recalcColorByDistanceButton);
		colorParamButtonsPanel.add(colorByDistanceButtonsPanel, ColorParam.DISTANCE_ALONG_SHOTS);

		w.put(highlightModeLabel).belowLast().west();
		w.put(highlightModeSelector.comboBox()).belowLast().fillx().addToInsets(0, 0, 5, 0);

		w.put(glowDistLabel).belowLast().west();
		w.put(glowDistAxisPanel).belowLast().fillx().addToInsets(0, 0, 5, 0);

		w.put(distColorationLabel).belowLast().west();
		w.put(distColorationAxisPanel).belowLast().fillx().addToInsets(0, 0, 10, 0);

		GridBagWizard maxDatePanel = GridBagWizard.quickPanel();
		maxDatePanel.put(maxDateLabel).xy(0, 0).fillx(1);
		maxDatePanel.put(prevYearButton).rightOfLast();
		maxDatePanel.put(prevMonthButton).rightOfLast();
		maxDatePanel.put(prevDayButton).rightOfLast();
		maxDatePanel.put(playButton).rightOfLast();
		maxDatePanel.put(nextDayButton).rightOfLast();
		maxDatePanel.put(nextMonthButton).rightOfLast();
		maxDatePanel.put(nextYearButton).rightOfLast();
		w.put(maxDatePanel.getTarget()).belowLast().fillx().west();
		w.put(maxDateSlider).belowLast().fillx();

		w.put(ambientLightLabel).belowLast().west();
		w.put(ambientLightSlider).belowLast().fillx();

		w.put(boldnessLabel).belowLast().west();
		w.put(boldnessSlider).belowLast().fillx();

		w.put(stationLabelFontSizeLabel).belowLast().fillx();
		w.put(stationLabelFontSizeSlider).belowLast().fillx();

		w.put(stationLabelDensityLabel).belowLast().fillx();
		GridBagWizard stationLabelDensityPanel = GridBagWizard.quickPanel();
		stationLabelDensityPanel
			.put(stationLabelsOffLabel, lessStationLabelDensityLabel, moreStationLabelDensityLabel)
			.intoRow()
			.y(0);
		stationLabelDensityPanel.put(lessStationLabelDensityLabel).fillx(1.0).west().addToInsets(0, 20, 0, 0);
		stationLabelDensityPanel
			.put(stationLabelDensitySlider)
			.below(stationLabelsOffLabel, moreStationLabelDensityLabel)
			.fillx();
		w.put(stationLabelDensityPanel.getTarget()).belowLast().fillx();

		w.put(showLeadLabelsCheckBox).belowLast().fillx();
		w.put(showCheckedLeadsCheckBox).belowLast().fillx();

		w.put(centerlineDistanceLabel).belowLast().fillx();
		GridBagWizard centerlineDistancePanel = GridBagWizard.quickPanel();
		centerlineDistancePanel
			.put(centerlinesOffLabel, lessCenterlineDistanceLabel, moreCenterlineDistanceLabel)
			.intoRow()
			.y(0);
		centerlineDistancePanel.put(lessCenterlineDistanceLabel).fillx(1.0).west().addToInsets(0, 20, 0, 0);
		centerlineDistancePanel
			.put(centerlineDistanceSlider)
			.below(centerlinesOffLabel, moreCenterlineDistanceLabel)
			.fillx();
		w.put(centerlineDistancePanel.getTarget()).belowLast().fillx();

		w.put(showTerrainCheckBox).belowLast().fillx();

		w.put(numSamplesLabel).belowLast().west();
		w.put(numSamplesSlider).belowLast().fillx();

		w.put(versionLabel).belowLast().south().weighty(1.0).fillx();

		w.put(debugButton).belowLast().southwest();

		debugButton.setVisible(false);

		setLayout(new BorderLayout());
		add(mainPanelScrollPane, BorderLayout.CENTER);
	}

	private void createListeners() {
		new PlotAxisController(distColorationAxis).removeMouseWheelListener();
		new PlotAxisController(paramColorationAxis).removeMouseWheelListener();
		new PlotAxisController(glowDistAxis).removeMouseWheelListener();

		numSamplesSlider.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				updateNumSamplesLabel();
			}
		});

		pickParamGradientButton.addActionListener(e -> {
			paramGradientComboBox.showPopup();
		});
	}

	public JButton getCameraToDepthAxisTiltButton() {
		return cameraToDepthAxisTiltButton;
	}

	public PlotAxis getDistColorationAxis() {
		return distColorationAxis;
	}

	public AbstractButton getFitParamColorationAxisButton() {
		return fitParamColorationAxisButton;
	}

	public JButton getFitViewToEverythingButton() {
		return fitViewToEverythingButton;
	}

	public JButton getFitViewToSelectedButton() {
		return fitViewToSelectedButton;
	}

	public AbstractButton getFlipParamColorationAxisButton() {
		return flipParamColorationAxisButton;
	}

	public PlotAxis getGlowDistAxis() {
		return glowDistAxis;
	}

	public JButton getInferDepthAxisTiltButton() {
		return inferDepthAxisTiltButton;
	}

	public JButton getOrbitToPlanButton() {
		return orbitToPlanButton;
	}

	public PlotAxis getParamColorationAxis() {
		return paramColorationAxis;
	}

	public LinearGradientPaint getParamColorationAxisPaint() {
		return (LinearGradientPaint) ((FillBorder) paramColorationAxisPanel.getUnderpaintBorder())
			.getPaint(
				paramColorationAxisPanel,
				null,
				0,
				0,
				paramColorationAxisPanel.getWidth(),
				paramColorationAxisPanel.getHeight());
	}

	public PaintablePanel getParamColorationAxisPanel() {
		return paramColorationAxisPanel;
	}

	public AbstractButton getRecalcColorByDistanceButton() {
		return recalcColorByDistanceButton;
	}

	public JButton getResetDepthAxisTiltButton() {
		return resetDepthAxisTiltButton;
	}

	public JButton getResetViewButton() {
		return resetViewButton;
	}

	public ViewButtonsPanel getViewButtonsPanel() {
		return viewButtonsPanel;
	}

	private Properties loadVersionProperties() {
		Properties props = new Properties();
		try {
			props.load(getClass().getClassLoader().getResourceAsStream("version.properties"));
		}
		catch (Exception ex) {

		}
		return props;
	}

	public void setMaxNumSamples(int maxNumSamples) {
		if (maxNumSamples != numSamplesSlider.getMaximum()) {
			Integer value = rootBinder.get().get(RootModel.desiredNumSamples);
			if (value == null) {
				value = numSamplesSlider.getValue();
			}
			value = Math.min(value, maxNumSamples);
			DefaultBoundedRangeModel newModel = new DefaultBoundedRangeModel(value, 0, 1, maxNumSamples);
			numSamplesSlider.setModel(newModel);
			numSamplesSlider.setEnabled(maxNumSamples > 1);
			updateNumSamplesLabel();
		}
	}

	private void updateNumSamplesLabel() {
		localizer.unregister(numSamplesLabel);
		if (numSamplesSlider.getValue() < 2) {
			localizer.setText(numSamplesLabel, "numSamplesLabel.text.off");
		}
		else {
			localizer.setFormattedText(numSamplesLabel, "numSamplesLabel.text.on", numSamplesSlider.getValue());
		}
	}

	public static final DateFormat maxDateFormat = DateFormat.getDateInstance();

	private void updateMaxDateLabelText(Date date) {
		localizer
			.setFormattedText(maxDateLabel, "maxDateLabel.text", date == null ? "Present" : maxDateFormat.format(date));
	}

	public void setDateRange(float start, float end) {
		maxDateSlider.setFloatMinimum(start);
		maxDateSlider.setFloatMaximum(end);
	}

	public void setDateRange(Date start, Date end) {
		setDateRange(ColorParam.calcDaysSince1800(start), ColorParam.calcDaysSince1800(end));
	}

	public JToggleButton getPlayButton() {
		return playButton;
	}
}
