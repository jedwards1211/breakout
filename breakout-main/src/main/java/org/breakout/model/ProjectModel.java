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
package org.breakout.model;

import java.awt.Color;
import java.io.File;

import org.andork.awt.layout.DrawerModel;
import org.andork.collect.MultiMap;
import org.andork.collect.MultiMaps;
import org.andork.func.Color2HexStringBimapper;
import org.andork.func.EnumBimapper;
import org.andork.func.FileStringBimapper;
import org.andork.jogl.Projection;
import org.andork.jogl.awt.JoglExportImageDialogModel;
import org.andork.math3d.Clip3f;
import org.andork.math3d.Vecmath;
import org.andork.q.QArrayList;
import org.andork.q.QArrayListBimapper;
import org.andork.q.QLinkedHashMap;
import org.andork.q.QMap;
import org.andork.q.QMapBimapper;
import org.andork.q.QObject;
import org.andork.q.QObjectMapBimapper;
import org.andork.q.QSpec;
import org.andork.unit.Angle;
import org.andork.unit.Length;
import org.andork.unit.Unit;
import org.breakout.CameraView;
import org.breakout.model.raw.SurveyLead;

import com.andork.plot.LinearAxisConversion;
import com.github.krukow.clj_lang.PersistentVector;

public final class ProjectModel extends QSpec<ProjectModel> {
	public static final Attribute<CameraView> cameraView = newAttribute(CameraView.class, "cameraView");
	public static final Attribute<Projection> projCalculator = newAttribute(Projection.class, "projCalculator");
	public static final Attribute<float[]> viewXform = newAttribute(float[].class, "viewXform");
	public static final Attribute<LinearAxisConversion> distRange =
		newAttribute(LinearAxisConversion.class, "distRange");
	public static final Attribute<GradientModel> paramGradient = newAttribute(GradientModel.class, "gradientModel");
	public static final Attribute<ColorParam> colorParam = newAttribute(ColorParam.class, "colorParam");
	public static final Attribute<QMap<ColorParam, LinearAxisConversion, ?>> paramRanges =
		newAttribute(QMap.class, "savedParamRanges");
	public static final Attribute<LinearAxisConversion> highlightRange =
		newAttribute(LinearAxisConversion.class, "highlightRange");
	public static final Attribute<HighlightMode> highlightMode = newAttribute(HighlightMode.class, "highlightMode");
	public static final Attribute<QObject<DrawerModel>> settingsDrawer =
		newAttribute(DrawerModel.instance, "settingsDrawer");
	public static final Attribute<QObject<DrawerModel>> surveyDrawer =
		newAttribute(DrawerModel.instance, "surveyDrawer");
	public static final Attribute<QObject<DrawerModel>> miniSurveyDrawer =
		newAttribute(DrawerModel.instance, "miniSurveyDrawer");
	public static final Attribute<QObject<DrawerModel>> taskListDrawer =
		newAttribute(DrawerModel.instance, "taskListDrawer");
	public static final Attribute<QObject<JoglExportImageDialogModel>> exportImageDialogModel =
		newAttribute(JoglExportImageDialogModel.instance, "exportImageDialogModel");
	public static final Attribute<Float> stationLabelDensity = newAttribute(Float.class, "stationLabelDensity");
	public static final Attribute<Float> stationLabelFontSize = newAttribute(Float.class, "stationLabelFontSize");
	public static final Attribute<Color> stationLabelColor = newAttribute(Color.class, "stationLabelColor");
	public static final Attribute<Color> backgroundColor = newAttribute(Color.class, "backgroundColor");
	public static final Attribute<Float> centerlineDistance = newAttribute(Float.class, "centerlineDistance");
	public static final Attribute<Color> centerlineColor = newAttribute(Color.class, "centerlineColor");
	public static final Attribute<Float> maxDate = newAttribute(Float.class, "maxDate");
	public static final Attribute<Float> ambientLight = newAttribute(Float.class, "ambientLight");
	public static final Attribute<Float> boldness = newAttribute(Float.class, "boldness");
	public static final Attribute<float[]> depthAxis = newAttribute(float[].class, "depthAxis");
	public static final Attribute<QArrayList<File>> surveyScanPaths = newAttribute(QArrayList.class, "surveyScanPaths");
	public static final Attribute<Boolean> hasUnsavedChanges = newAttribute(Boolean.class, "hasUnsavedChanges");
	public static final Attribute<Unit<Length>> displayLengthUnit = newAttribute(Unit.class, "displayLengthUnit");
	public static final Attribute<Unit<Angle>> displayAngleUnit = newAttribute(Unit.class, "displayAngleUnit");
	public static final Attribute<Boolean> showLeadLabels = newAttribute(Boolean.class, "showLeadLabels");
	public static final Attribute<Boolean> showCheckedLeads = newAttribute(Boolean.class, "showCheckedLeads");
	public static final Attribute<Boolean> showTerrain = newAttribute(Boolean.class, "showTerrain");
	public static final Attribute<Clip3f> clip = newAttribute(Clip3f.class, "clip");
	public static final Attribute<String> customMode = newAttribute(String.class, "customMode");
	public static final Attribute<File> compassImportDirectory = newAttribute(File.class, "compassImportDirectory");
	public static final Attribute<File> wallsImportDirectory = newAttribute(File.class, "wallsImportDirectory");
	public static final Attribute<File> linkSurveyNotesDirectory = newAttribute(File.class, "linkSurveyNotesDirectory");
	public static final Attribute<File> importLeadsDirectory = newAttribute(File.class, "importLeadsDirectory");
	public static final Attribute<File> exportSTLDirectory = newAttribute(File.class, "exportSTLDirectory");
	public static final Attribute<File> exportSurveyNotesDirectory =
		newAttribute(File.class, "exportSurveyNotesDirectory");
	public static final Attribute<com.github.krukow.clj_ds.PersistentVector<SurveyLead>> leads =
		newAttribute(com.github.krukow.clj_ds.PersistentVector.class, "leads", (a, b) -> a == b);
	public static final Attribute<MultiMap<StationKey, SurveyLead>> leadIndex =
		newAttribute(MultiMap.class, "leadIndex", (a, b) -> a == b);

	public static final ProjectModel instance = new ProjectModel();

	public static final QObjectMapBimapper<ProjectModel> defaultMapper;

	static {
		defaultMapper =
			new QObjectMapBimapper<>(instance)
				.map(projCalculator, ProjectionCalculatorBimapper.instance)
				.map(distRange, LinearAxisConversionMapBimapper.instance)
				.map(paramGradient, GradientModel.bimapper)
				.map(
					paramRanges,
					QMapBimapper
						.newInstance(
							EnumBimapper.newInstance(ColorParam.class),
							LinearAxisConversionMapBimapper.instance))
				.map(highlightRange, LinearAxisConversionMapBimapper.instance)
				.map(highlightMode, EnumBimapper.newInstance(HighlightMode.class))
				.map(settingsDrawer, DrawerModel.defaultMapper)
				.map(surveyDrawer, DrawerModel.defaultMapper)
				.map(miniSurveyDrawer, DrawerModel.defaultMapper)
				.map(taskListDrawer, DrawerModel.defaultMapper)
				.map(exportImageDialogModel, JoglExportImageDialogModel.defaultMapper)
				.map(backgroundColor, Color2HexStringBimapper.instance)
				.map(stationLabelColor, Color2HexStringBimapper.instance)
				.map(centerlineColor, Color2HexStringBimapper.instance)
				.map(displayLengthUnit, Unit2StringBimapper.length)
				.map(displayAngleUnit, Unit2StringBimapper.angle)
				.map(clip, Clip3fBimapper.instance)
				.map(surveyScanPaths, QArrayListBimapper.newInstance(FileStringBimapper.instance))
				.map(compassImportDirectory, FileStringBimapper.instance)
				.map(wallsImportDirectory, FileStringBimapper.instance)
				.map(linkSurveyNotesDirectory, FileStringBimapper.instance)
				.map(importLeadsDirectory, FileStringBimapper.instance)
				.map(exportSTLDirectory, FileStringBimapper.instance)
				.map(exportSurveyNotesDirectory, FileStringBimapper.instance)
				.exclude(hasUnsavedChanges)
				.exclude(leads)
				.exclude(leadIndex);
	}

	private ProjectModel() {
		super();
	}

	public static QObject<ProjectModel> newInstance() {
		QObject<ProjectModel> result = QObject.newInstance(instance);
		setDefaults(result);
		return result;
	}

	public static void setDefaults(QObject<ProjectModel> projectModel) {
		projectModel.setIfNull(ProjectModel.cameraView, CameraView.PERSPECTIVE);
		projectModel.setIfNull(ProjectModel.backgroundColor, Color.black);
		projectModel.setIfNull(ProjectModel.stationLabelFontSize, 12f);
		projectModel.setIfNull(ProjectModel.stationLabelDensity, 40f);
		projectModel.setIfNull(ProjectModel.stationLabelColor, Color.white);
		projectModel.setIfNull(ProjectModel.centerlineDistance, 1000.0f);
		projectModel.setIfNull(ProjectModel.centerlineColor, Color.white);
		projectModel.setIfNull(ProjectModel.ambientLight, 0.5f);
		projectModel.setIfNull(ProjectModel.boldness, 0f);
		projectModel.setIfNull(ProjectModel.distRange, new LinearAxisConversion(0, 0, 20000, 200));
		projectModel.setIfNull(ProjectModel.viewXform, Vecmath.newMat4f());
		projectModel.setIfNull(ProjectModel.colorParam, ColorParam.DEPTH);
		projectModel.setIfNull(ProjectModel.paramGradient, Gradients.DEFAULT);
		projectModel
			.setIfNull(ProjectModel.paramRanges, QLinkedHashMap.<ColorParam, LinearAxisConversion>newInstance());
		QMap<ColorParam, LinearAxisConversion, ?> paramRanges = projectModel.get(ProjectModel.paramRanges);
		for (ColorParam colorParam : ColorParam.values()) {
			if (!paramRanges.containsKey(colorParam)) {
				paramRanges.put(colorParam, new LinearAxisConversion());
			}
		}
		projectModel.setIfNull(ProjectModel.highlightRange, new LinearAxisConversion(0, 0, 1000, 200));
		projectModel.setIfNull(ProjectModel.highlightMode, HighlightMode.NEARBY);
		projectModel.setIfNull(ProjectModel.surveyDrawer, DrawerModel.instance.newObject());
		projectModel.setIfNull(ProjectModel.settingsDrawer, DrawerModel.instance.newObject());
		projectModel.setIfNull(ProjectModel.miniSurveyDrawer, DrawerModel.instance.newObject());
		projectModel.setIfNull(ProjectModel.taskListDrawer, DrawerModel.instance.newObject());
		projectModel.setIfNull(ProjectModel.displayLengthUnit, Length.meters);
		projectModel.setIfNull(ProjectModel.displayAngleUnit, Angle.degrees);
		projectModel.setIfNull(ProjectModel.showLeadLabels, true);
		projectModel.setIfNull(ProjectModel.showCheckedLeads, false);
		projectModel.setIfNull(ProjectModel.showTerrain, false);
		projectModel
			.setIfNull(ProjectModel.clip, new Clip3f(new float[]
			{ 0, -1, 0 }, -Float.MAX_VALUE, Float.MAX_VALUE));
		projectModel.setIfNull(ProjectModel.leads, PersistentVector.emptyVector());
		projectModel.setIfNull(ProjectModel.leadIndex, MultiMaps.emptyMultiMap());
	}
}
