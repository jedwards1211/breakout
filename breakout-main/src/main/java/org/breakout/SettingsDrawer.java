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

import static org.andork.bind.EqualsBinder.bindEquals;
import static org.andork.func.CompoundBimapper.compose;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Insets;
import java.awt.LinearGradientPaint;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Objects;
import java.util.Properties;
import java.util.function.BiFunction;

import javax.swing.AbstractButton;
import javax.swing.DefaultBoundedRangeModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.andork.awt.AWTUtil;
import org.andork.awt.ColorUtils;
import org.andork.awt.GridBagWizard;
import org.andork.awt.GridBagWizard.DefaultAutoInsets;
import org.andork.awt.I18n;
import org.andork.awt.I18n.Localizer;
import org.andork.awt.layout.BetterCardLayout;
import org.andork.awt.layout.Drawer;
import org.andork.awt.layout.Side;
import org.andork.bind.BiFunctionBinder;
import org.andork.bind.BimapperBinder;
import org.andork.bind.Binder;
import org.andork.bind.BinderWrapper;
import org.andork.bind.QMapKeyedBinder;
import org.andork.bind.QObjectAttributeBinder;
import org.andork.bind.TriFunctionBinder;
import org.andork.bind.ui.BetterCardLayoutBinder;
import org.andork.bind.ui.ButtonSelectedBinder;
import org.andork.bind.ui.ComponentBackgroundBinder;
import org.andork.bind.ui.ComponentEnabledBinder;
import org.andork.bind.ui.ISelectorSelectionBinder;
import org.andork.bind.ui.JSliderValueBinder;
import org.andork.func.LinearFloatBimapper;
import org.andork.func.RoundingFloat2IntegerBimapper;
import org.andork.plot.PlotAxisConversionBinder;
import org.andork.q.QMap;
import org.andork.q.QObject;
import org.andork.swing.CellRenderers;
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
import org.breakout.model.HighlightMode;
import org.breakout.model.ProjectModel;
import org.breakout.model.RootModel;
import org.breakout.update.UpdateStatusPanel;
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
	JSlider mouseSensitivitySlider;
	JLabel mouseWheelSensitivityLabel;
	JSlider mouseWheelSensitivitySlider;
	JLabel colorsLabel;
	JLabel backgroundColorLabel;
	JXColorSelectionButton backgroundColorButton;
	JLabel stationLabelColorLabel;
	JXColorSelectionButton stationLabelColorButton;
	JLabel centerlineColorLabel;
	JXColorSelectionButton centerlineColorButton;

	JLabel displayUnitsLabel;
	DefaultSelector<Unit<Length>> displayLengthUnitSelector;
	DefaultSelector<Unit<Angle>> displayAngleUnitSelector;

	JLabel ambientLightLabel;
	JSlider ambientLightSlider;
	JLabel distColorationLabel;
	PlotAxis distColorationAxis;
	PaintablePanel distColorationAxisPanel;

	JLabel stationLabelsOffLabel;
	JLabel lessStationLabelDensityLabel;
	JLabel moreStationLabelDensityLabel;
	JLabel stationLabelFontSizeLabel;
	JSlider stationLabelFontSizeSlider;
	JLabel stationLabelDensityLabel;
	JSlider stationLabelDensitySlider;
	JCheckBox showLeadLabelsCheckBox;
	JCheckBox showCheckedLeadsCheckBox;
	JLabel centerlinesOffLabel;
	JLabel lessCenterlineDistanceLabel;
	JLabel moreCenterlineDistanceLabel;
	JLabel centerlineDistanceLabel;
	JSlider centerlineDistanceSlider;
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
	Binder<Path> currentProjectFileBinder = QObjectAttributeBinder.bind(RootModel.currentProjectFile, rootBinder);
	Binder<Integer> desiredNumSamplesBinder = QObjectAttributeBinder.bind(RootModel.desiredNumSamples, rootBinder);
	Binder<Integer> mouseSensitivityBinder = QObjectAttributeBinder.bind(RootModel.mouseSensitivity, rootBinder);
	Binder<Integer> mouseWheelSensitivityBinder =
		QObjectAttributeBinder.bind(RootModel.mouseWheelSensitivity, rootBinder);

	BinderWrapper<QObject<ProjectModel>> projectBinder = new BinderWrapper<>();
	Binder<CameraView> cameraViewBinder = QObjectAttributeBinder.bind(ProjectModel.cameraView, projectBinder);
	Binder<Float> stationLabelFontSizeBinder =
		QObjectAttributeBinder.bind(ProjectModel.stationLabelFontSize, projectBinder);
	Binder<Float> stationLabelDensityBinder =
		QObjectAttributeBinder.bind(ProjectModel.stationLabelDensity, projectBinder);
	Binder<Boolean> showLeadLabelsBinder = QObjectAttributeBinder.bind(ProjectModel.showLeadLabels, projectBinder);
	Binder<Boolean> showCheckedLeadsBinder = QObjectAttributeBinder.bind(ProjectModel.showCheckedLeads, projectBinder);
	Binder<Color> stationLabelColorBinder = QObjectAttributeBinder.bind(ProjectModel.stationLabelColor, projectBinder);
	Binder<Float> centerlineDistanceBinder =
		QObjectAttributeBinder.bind(ProjectModel.centerlineDistance, projectBinder);
	Binder<Color> centerlineColorBinder = QObjectAttributeBinder.bind(ProjectModel.centerlineColor, projectBinder);
	Binder<Boolean> showTerrainBinder = QObjectAttributeBinder.bind(ProjectModel.showTerrain, projectBinder);
	Binder<Color> backgroundColorBinder = QObjectAttributeBinder.bind(ProjectModel.backgroundColor, projectBinder);
	Binder<LinearAxisConversion> distRangeBinder = QObjectAttributeBinder.bind(ProjectModel.distRange, projectBinder);
	Binder<GradientModel> paramGradientBinder = QObjectAttributeBinder.bind(ProjectModel.paramGradient, projectBinder);
	Binder<ColorParam> colorParamBinder = QObjectAttributeBinder.bind(ProjectModel.colorParam, projectBinder);
	Binder<QMap<ColorParam, LinearAxisConversion, ?>> paramRangesBinder =
		QObjectAttributeBinder.bind(ProjectModel.paramRanges, projectBinder);
	Binder<LinearAxisConversion> paramRangeBinder = QMapKeyedBinder.bindKeyed(colorParamBinder, paramRangesBinder);
	Binder<LinearAxisConversion> highlightRangeBinder =
		QObjectAttributeBinder.bind(ProjectModel.highlightRange, projectBinder);
	Binder<HighlightMode> highlightModeBinder = QObjectAttributeBinder.bind(ProjectModel.highlightMode, projectBinder);
	Binder<Float> ambientLightBinder = QObjectAttributeBinder.bind(ProjectModel.ambientLight, projectBinder);
	Binder<Unit<Length>> displayLengthUnitBinder =
		QObjectAttributeBinder.bind(ProjectModel.displayLengthUnit, projectBinder);
	Binder<Unit<Angle>> displayAngleUnitBinder =
		QObjectAttributeBinder.bind(ProjectModel.displayAngleUnit, projectBinder);
	Binder<LinearAxisConversion> displayParamRangeBinder =
		new TriFunctionBinder<LinearAxisConversion, Unit<Length>, ColorParam, LinearAxisConversion>(
			(LinearAxisConversion paramConversion, Unit<Length> lengthUnit, ColorParam colorParam) -> {
				if (paramConversion == null)
					return null;
				if (lengthUnit == null || colorParam == null || colorParam.getUnitType() == null)
					return paramConversion;
				Unit systemUnit = Length.meters;
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
			},
			(LinearAxisConversion paramConversion, Unit<Length> lengthUnit, ColorParam colorParam) -> {
				if (paramConversion == null)
					return null;
				if (lengthUnit == null || colorParam == null || colorParam.getUnitType() == null)
					return paramConversion;
				Unit systemUnit = Length.meters;
				Unit displayUnit = Length.meters;
				if (colorParam.getUnitType() == Length.type) {
					displayUnit = lengthUnit;
				}
				else if (colorParam.getUnitType() == Area.type) {
					systemUnit = Area.square(systemUnit);
					displayUnit = Area.square(lengthUnit);
				}
				return new LinearAxisConversion(
					new UnitizedDouble<>(paramConversion.invert(0), displayUnit).get(systemUnit),
					0,
					new UnitizedDouble<>(paramConversion.invert(1), displayUnit).get(systemUnit),
					1);
			}).bind(paramRangeBinder, displayLengthUnitBinder, colorParamBinder);
	Binder<LinearAxisConversion> displayHighlightRangeBinder =
		new BiFunctionBinder<LinearAxisConversion, Unit<Length>, LinearAxisConversion>(
			axisConversionToDisplay,
			axisConversionToSystem).bind(highlightRangeBinder, displayLengthUnitBinder);
	Binder<LinearAxisConversion> displayDistRangeBinder =
		new BiFunctionBinder<LinearAxisConversion, Unit<Length>, LinearAxisConversion>(
			axisConversionToDisplay,
			axisConversionToSystem).bind(distRangeBinder, displayLengthUnitBinder);

	private static final BiFunction<LinearAxisConversion, Unit<Length>, LinearAxisConversion> axisConversionToDisplay =
		(distRange, displayLengthUnit) -> {
			if (distRange == null)
				return null;
			if (displayLengthUnit == null)
				return distRange;
			return new LinearAxisConversion(
				Length.meters(distRange.invert(0)).get(displayLengthUnit),
				0,
				Length.meters(distRange.invert(1)).get(displayLengthUnit),
				1);
		};
	private static final BiFunction<LinearAxisConversion, Unit<Length>, LinearAxisConversion> axisConversionToSystem =
		(displayDistRange, displayLengthUnit) -> {
			if (displayDistRange == null)
				return null;
			if (displayLengthUnit == null)
				return displayDistRange;
			return new LinearAxisConversion(
				new UnitizedDouble<>(displayDistRange.invert(0), displayLengthUnit).get(Length.meters),
				0,
				new UnitizedDouble<>(displayDistRange.invert(1), displayLengthUnit).get(Length.meters),
				1);
		};

	UpdateStatusPanel updateStatusPanel;

	private String loadedVersion;

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

	private void createBindings() {
		ComponentBackgroundBinder.bind(backgroundColorButton, backgroundColorBinder);
		ComponentBackgroundBinder.bind(stationLabelColorButton, stationLabelColorBinder);
		ComponentBackgroundBinder.bind(centerlineColorButton, centerlineColorBinder);

		ButtonSelectedBinder.bind(viewButtonsPanel.getPlanButton(), bindEquals(CameraView.PLAN, cameraViewBinder));
		ButtonSelectedBinder
			.bind(viewButtonsPanel.getPerspectiveButton(), bindEquals(CameraView.PERSPECTIVE, cameraViewBinder));
		ButtonSelectedBinder
			.bind(viewButtonsPanel.getNorthButton(), bindEquals(CameraView.NORTH_FACING_PROFILE, cameraViewBinder));
		ButtonSelectedBinder
			.bind(viewButtonsPanel.getSouthButton(), bindEquals(CameraView.SOUTH_FACING_PROFILE, cameraViewBinder));
		ButtonSelectedBinder
			.bind(viewButtonsPanel.getEastButton(), bindEquals(CameraView.EAST_FACING_PROFILE, cameraViewBinder));
		ButtonSelectedBinder
			.bind(viewButtonsPanel.getWestButton(), bindEquals(CameraView.WEST_FACING_PROFILE, cameraViewBinder));
		ButtonSelectedBinder
			.bind(viewButtonsPanel.getAutoProfileButton(), bindEquals(CameraView.AUTO_PROFILE, cameraViewBinder));
		JSliderValueBinder.bind(mouseSensitivitySlider, mouseSensitivityBinder);
		JSliderValueBinder.bind(mouseWheelSensitivitySlider, mouseWheelSensitivityBinder);
		PlotAxisConversionBinder.bind(distColorationAxis, displayDistRangeBinder);
		BinderWrapper.create((GradientModel gradient) -> {
			paramColorationAxisPanel
				.setUnderpaintBorder(
					MultipleGradientFillBorder
						.from(Side.LEFT)
						.to(Side.RIGHT)
						.linear(gradient.fractions, gradient.colors));
		}).bind(paramGradientBinder);
		ISelectorSelectionBinder.bind(colorParamSelector, colorParamBinder);
		BetterCardLayoutBinder.bind(colorParamDetailsPanel, colorParamDetailsLayout, colorParamBinder);
		BetterCardLayoutBinder.bind(colorParamButtonsPanel, colorParamButtonsLayout, colorParamBinder);
		PlotAxisConversionBinder.bind(paramColorationAxis, displayParamRangeBinder);
		ISelectorSelectionBinder.bind(highlightModeSelector, highlightModeBinder);
		PlotAxisConversionBinder.bind(glowDistAxis, displayHighlightRangeBinder);
		JSliderValueBinder
			.bind(
				ambientLightSlider,
				BimapperBinder
					.bind(
						compose(
							new LinearFloatBimapper(0f, 0f, 1f, ambientLightSlider.getMaximum()),
							RoundingFloat2IntegerBimapper.instance),
						ambientLightBinder));
		JSliderValueBinder
			.bind(
				centerlineDistanceSlider,
				BimapperBinder.bind(RoundingFloat2IntegerBimapper.instance, centerlineDistanceBinder));
		JSliderValueBinder
			.bind(
				stationLabelDensitySlider,
				BimapperBinder.bind(RoundingFloat2IntegerBimapper.instance, stationLabelDensityBinder));
		JSliderValueBinder
			.bind(
				stationLabelFontSizeSlider,
				BimapperBinder
					.bind(
						compose(new LinearFloatBimapper(10f, 0f), RoundingFloat2IntegerBimapper.instance),
						stationLabelFontSizeBinder));
		ButtonSelectedBinder.bind(showLeadLabelsCheckBox, showLeadLabelsBinder);
		ButtonSelectedBinder.bind(showCheckedLeadsCheckBox, showCheckedLeadsBinder);
		ComponentEnabledBinder.bind(showCheckedLeadsCheckBox, showLeadLabelsBinder);
		ButtonSelectedBinder.bind(showTerrainCheckBox, showTerrainBinder);

		JSliderValueBinder.bind(numSamplesSlider, desiredNumSamplesBinder);

		ISelectorSelectionBinder.bind(displayLengthUnitSelector, displayLengthUnitBinder);
		ISelectorSelectionBinder.bind(displayAngleUnitSelector, displayAngleUnitBinder);
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
		stationLabelDensitySlider = new JSlider(0, 600, 40);

		stationLabelFontSizeLabel = new JLabel();
		localizer.setText(stationLabelFontSizeLabel, "stationLabelFontSizeLabel.text");
		stationLabelFontSizeSlider = new JSlider(80, 720, 120);

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
		centerlineDistanceSlider = new JSlider(0, 10000, 1000);

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

		displayUnitsLabel = new JLabel();
		localizer.setText(displayUnitsLabel, "displayUnitsLabel.text");

		displayLengthUnitSelector = new DefaultSelector<>();
		displayLengthUnitSelector.setAvailableValues(Length.meters, Length.feet);

		displayAngleUnitSelector = new DefaultSelector<>();
		displayAngleUnitSelector.setAvailableValues(Angle.degrees, Angle.gradians, Angle.milsNATO);

		ambientLightLabel = new JLabel();
		localizer.setText(ambientLightLabel, "ambientLightLabel.text");

		ambientLightSlider = new JSlider(0, 100, 50);
		ambientLightSlider.setOpaque(false);

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
			.setUnderpaintBorder(MultipleGradientFillBorder.from(Side.LEFT).to(Side.RIGHT).linear(new float[]
			{ 0f, 0.24f, 0.64f, 1f },
				new Color[]
				{ new Color(255, 249, 204), new Color(255, 195, 0), new Color(214, 6, 127), new Color(34, 19, 150) }));

		paramColorationAxis.setForeground(Color.WHITE);
		paramColorationAxis.setMajorTickColor(Color.WHITE);
		paramColorationAxis.setMinorTickColor(Color.WHITE);

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

		mouseSensitivitySlider = new JSlider();
		mouseSensitivitySlider.setValue(20);
		mouseSensitivitySlider.setOpaque(false);

		mouseWheelSensitivityLabel = new JLabel();
		localizer.setText(mouseWheelSensitivityLabel, "mouseWheelSensitivityLabel.text");

		mouseWheelSensitivitySlider = new JSlider(1, 2000, 200);
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
		Properties versionProperties = loadVersionProperties();
		loadedVersion = versionProperties.getProperty("version", "unknown");
		String buildDate = versionProperties.getProperty("build.date", "unknown");

		localizer.setFormattedText(versionLabel, "versionLabel.text", loadedVersion, buildDate);

		updateStatusPanel = new UpdateStatusPanel(i18n);

		AWTUtil.traverse(updateStatusPanel, comp -> {
			comp.setBackground(null);
			if (comp instanceof JComponent) {
				((JComponent) comp).setOpaque(false);
			}
		});
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
		w.put(paramColorationAxisPanel).belowLast().fillx().addToInsets(0, 0, 5, 0);

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

		w.put(ambientLightLabel).belowLast().west();
		w.put(ambientLightSlider).belowLast().fillx();

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

		w.put(updateStatusPanel).belowLast().south().fillx();

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

	public String getLoadedVersion() {
		return loadedVersion;
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

	public UpdateStatusPanel getUpdateStatusPanel() {
		return updateStatusPanel;
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
}
