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
import org.andork.func.Bimapper;
import org.andork.func.Color2HexStringBimapper;
import org.andork.func.EnumBimapper;
import org.andork.func.FileStringBimapper;
import org.andork.jogl.Projection;
import org.andork.jogl.awt.JoglExportImageDialogModel;
import org.andork.math3d.Vecmath;
import org.andork.q.QArrayList;
import org.andork.q.QArrayListBimapper;
import org.andork.q.QLinkedHashMap;
import org.andork.q.QMap;
import org.andork.q.QMapBimapper;
import org.andork.q.QObject;
import org.andork.q.QObjectMapBimapper;
import org.andork.q.QSpec;
import org.breakout.CameraView;

import com.andork.plot.LinearAxisConversion;

public final class ProjectModel extends QSpec<ProjectModel> {
	public static final Attribute<CameraView> cameraView = newAttribute(
			CameraView.class,
			"cameraView");
	public static final Attribute<Projection> projCalculator = newAttribute(
			Projection.class,
			"projCalculator");
	public static final Attribute<float[]> viewXform = newAttribute(
			float[].class,
			"viewXform");
	public static final Attribute<LinearAxisConversion> distRange = newAttribute(
			LinearAxisConversion.class,
			"distRange");
	public static final Attribute<ColorParam> colorParam = newAttribute(
			ColorParam.class,
			"colorParam");
	public static final Attribute<QMap<ColorParam, LinearAxisConversion, ?>> paramRanges = newAttribute(
			QMap.class,
			"savedParamRanges");
	public static final Attribute<LinearAxisConversion> highlightRange = newAttribute(
			LinearAxisConversion.class,
			"highlightRange");
	public static final Attribute<QObject<DrawerModel>> settingsDrawer = newAttribute(
			DrawerModel.instance,
			"settingsDrawer");
	public static final Attribute<QObject<DrawerModel>> surveyDrawer = newAttribute(
			DrawerModel.instance,
			"surveyDrawer");
	public static final Attribute<QObject<DrawerModel>> miniSurveyDrawer = newAttribute(
			DrawerModel.instance,
			"miniSurveyDrawer");
	public static final Attribute<QObject<DrawerModel>> taskListDrawer = newAttribute(
			DrawerModel.instance,
			"taskListDrawer");
	public static final Attribute<QObject<JoglExportImageDialogModel>> exportImageDialogModel = newAttribute(
			JoglExportImageDialogModel.instance,
			"exportImageDialogModel");
	public static final Attribute<Color> stationLabelColor = newAttribute(
			Color.class,
			"stationLabelColor");
	public static final Attribute<Color> backgroundColor = newAttribute(
			Color.class,
			"backgroundColor");
	public static final Attribute<Color> centerlineColor = newAttribute(
			Color.class,
			"centerlineColor");
	public static final Attribute<Float> ambientLight = newAttribute(
			Float.class,
			"ambientLight");
	public static final Attribute<float[]> depthAxis = newAttribute(
			float[].class,
			"depthAxis");
	public static final Attribute<QArrayList<File>> surveyScanPaths = newAttribute(
			QArrayList.class,
			"surveyScanPaths");
	public static final Attribute<Boolean> hasUnsavedChanges = newAttribute(
			Boolean.class,
			"hasUnsavedChanges");

	public static final ProjectModel instance = new ProjectModel();

	public static final Bimapper<QObject<ProjectModel>, Object> defaultMapper;

	static {
		defaultMapper = new QObjectMapBimapper<>(instance)
				.map(projCalculator, ProjectionCalculatorBimapper.instance)
				.map(distRange, LinearAxisConversionMapBimapper.instance)
				.map(
						paramRanges,
						QMapBimapper.newInstance(EnumBimapper.newInstance(ColorParam.class),
								LinearAxisConversionMapBimapper.instance))
				.map(highlightRange, LinearAxisConversionMapBimapper.instance)
				.map(settingsDrawer, DrawerModel.defaultMapper)
				.map(surveyDrawer, DrawerModel.defaultMapper)
				.map(miniSurveyDrawer, DrawerModel.defaultMapper)
				.map(taskListDrawer, DrawerModel.defaultMapper)
				.map(exportImageDialogModel, JoglExportImageDialogModel.defaultMapper)
				.map(backgroundColor, Color2HexStringBimapper.instance)
				.map(stationLabelColor, Color2HexStringBimapper.instance)
				.map(centerlineColor, Color2HexStringBimapper.instance)
				.map(surveyScanPaths, QArrayListBimapper.newInstance(FileStringBimapper.instance))
				.exclude(hasUnsavedChanges);
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
		if (projectModel.get(ProjectModel.cameraView) == null) {
			projectModel.set(ProjectModel.cameraView, CameraView.PERSPECTIVE);
		}
		if (projectModel.get(ProjectModel.backgroundColor) == null) {
			projectModel.set(ProjectModel.backgroundColor, Color.black);
		}
		if (projectModel.get(ProjectModel.stationLabelColor) == null) {
			projectModel.set(ProjectModel.stationLabelColor, Color.white);
		}
		if (projectModel.get(ProjectModel.centerlineColor) == null) {
			projectModel.set(ProjectModel.centerlineColor, Color.white);
		}
		if (projectModel.get(ProjectModel.distRange) == null) {
			projectModel.set(ProjectModel.distRange, new LinearAxisConversion(0, 0, 20000, 200));
		}
		if (projectModel.get(ProjectModel.viewXform) == null) {
			projectModel.set(ProjectModel.viewXform, Vecmath.newMat4f());
		}
		if (projectModel.get(ProjectModel.colorParam) == null) {
			projectModel.set(ProjectModel.colorParam, ColorParam.DEPTH);
		}
		if (projectModel.get(ProjectModel.paramRanges) == null) {
			projectModel
					.set(ProjectModel.paramRanges, QLinkedHashMap.<ColorParam, LinearAxisConversion> newInstance());
		}
		QMap<ColorParam, LinearAxisConversion, ?> paramRanges = projectModel.get(ProjectModel.paramRanges);
		for (ColorParam colorParam : ColorParam.values()) {
			if (!paramRanges.containsKey(colorParam)) {
				paramRanges.put(colorParam, new LinearAxisConversion());
			}
		}
		if (projectModel.get(ProjectModel.highlightRange) == null) {
			projectModel.set(ProjectModel.highlightRange, new LinearAxisConversion(0, 0, 1000, 200));
		}
		if (projectModel.get(ProjectModel.surveyDrawer) == null) {
			projectModel.set(ProjectModel.surveyDrawer, DrawerModel.instance.newObject());
		}
		if (projectModel.get(ProjectModel.settingsDrawer) == null) {
			projectModel.set(ProjectModel.settingsDrawer, DrawerModel.instance.newObject());
		}
		if (projectModel.get(ProjectModel.miniSurveyDrawer) == null) {
			projectModel.set(ProjectModel.miniSurveyDrawer, DrawerModel.instance.newObject());
		}
		if (projectModel.get(ProjectModel.taskListDrawer) == null) {
			projectModel.set(ProjectModel.taskListDrawer, DrawerModel.instance.newObject());
		}
	}
}
