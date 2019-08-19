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

import java.io.File;
import java.nio.file.Path;

import org.andork.func.Bimapper;
import org.andork.func.FileStringBimapper;
import org.andork.func.PathStringBimapper;
import org.andork.q.QArrayList;
import org.andork.q.QArrayListBimapper;
import org.andork.q.QHashMap;
import org.andork.q.QMapBimapper;
import org.andork.q.QObject;
import org.andork.q.QObjectMapBimapper;
import org.andork.q.QSpec;
import org.breakout.BreakoutMainView;
import org.breakout.SearchMode;

public final class RootModel extends QSpec<RootModel> {
	/**
	 * the path to the current project file, relative to the current
	 * {@linkplain BreakoutMainView#getRootDirectory() root settings directory}.
	 */
	public static final Attribute<Path> currentProjectFile = newAttribute(Path.class, "currentProjectFile");
	/**
	 * the paths to recent project files, relative to the current
	 * {@linkplain BreakoutMainView#getRootDirectory() root settings directory}.
	 */
	public static final Attribute<QArrayList<Path>> recentProjectFiles =
		newAttribute(QArrayList.class, "recentProjectFiles");
	public static final Attribute<QHashMap<Path, Path>> swapFiles = newAttribute(QHashMap.class, "swapFiles");
	public static final Attribute<File> currentProjectFileChooserDirectory =
		newAttribute(File.class, "currentProjectFileChooserDirectory");
	public static final Attribute<Integer> desiredNumSamples = newAttribute(Integer.class, "desiredNumSamples");
	public static final Attribute<Integer> mouseSensitivity = newAttribute(Integer.class, "mouseSensitivity");
	public static final Attribute<Integer> mouseWheelSensitivity = newAttribute(Integer.class, "mouseWheelSensitivity");
	public static final Attribute<Boolean> doNotShowNewProjectInfoDialog =
		newAttribute(Boolean.class, "doNotShowNewProjectInfoDialog");
	public static final Attribute<Boolean> showDataInSurveyTable = newAttribute(Boolean.class, "showDataInSurveyTable");
	public static final Attribute<Boolean> showStationLabels = newAttribute(Boolean.class, "showStationLabels");
	public static final Attribute<Boolean> showSpatialIndex = newAttribute(Boolean.class, "showSpatialIndex");
	public static final Attribute<String> mapboxAccessToken = newAttribute(String.class, "mapboxAccessToken");
	public static final Attribute<SearchMode> searchMode = newAttribute(SearchMode.class, "searchMode");
	public static final Attribute<File> compassImportDirectory = newAttribute(File.class, "compassImportDirectory");
	public static final Attribute<File> wallsImportDirectory = newAttribute(File.class, "wallsImportDirectory");

	public static final RootModel instance = new RootModel();

	public static final Bimapper<QObject<RootModel>, Object> defaultMapper;

	static {
		defaultMapper =
			new QObjectMapBimapper<>(instance)
				.map(swapFiles, QMapBimapper.newInstance(PathStringBimapper.instance, PathStringBimapper.instance))
				.map(currentProjectFile, PathStringBimapper.instance)
				.map(recentProjectFiles, QArrayListBimapper.newInstance(PathStringBimapper.instance))
				.map(currentProjectFileChooserDirectory, FileStringBimapper.instance)
				.map(compassImportDirectory, FileStringBimapper.instance)
				.map(wallsImportDirectory, FileStringBimapper.instance)
				.exclude(currentProjectFile);
	}

	private RootModel() {
		super();
	}

	public static File getCurrentProjectFileChooserDirectory(QObject<RootModel> model) {
		File directory = model.get(currentProjectFileChooserDirectory);
		if (directory == null) {
			Path file = model.get(currentProjectFile);
			if (file != null) {
				directory = file.getParent().toFile();
			}
		}
		if (directory == null) {
			directory = new File(System.getProperty("user.dir"));
		}
		return directory;
	}

	public static void setDefaults(QObject<RootModel> rootModel) {
		if (rootModel.get(RootModel.desiredNumSamples) == null) {
			rootModel.set(RootModel.desiredNumSamples, 2);
		}
		if (rootModel.get(RootModel.mouseSensitivity) == null) {
			rootModel.set(RootModel.mouseSensitivity, 15);
		}
		if (rootModel.get(RootModel.mouseWheelSensitivity) == null) {
			rootModel.set(RootModel.mouseWheelSensitivity, 100);
		}
		if (rootModel.get(RootModel.showStationLabels) == null) {
			rootModel.set(RootModel.showStationLabels, true);
		}
		if (rootModel.get(RootModel.showSpatialIndex) == null) {
			rootModel.set(RootModel.showSpatialIndex, false);
		}
		if (rootModel.get(RootModel.searchMode) == null) {
			rootModel.set(RootModel.searchMode, SearchMode.AUTO);
		}
	}
}
