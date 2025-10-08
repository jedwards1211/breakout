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

import java.awt.Dimension;
import java.awt.Font;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import org.andork.awt.GridBagWizard;
import org.andork.awt.GridBagWizard.DefaultAutoInsets;
import org.andork.unit.Length;
import org.andork.unit.Unit;
import org.andork.unit.UnitizedDouble;
import org.andork.unit.UnitizedNumber;
import org.breakout.StatsPanel.StatsModel.StationPosition;
import org.breakout.model.HasStationKey;
import org.breakout.model.StationKey;
import org.locationtech.proj4j.BasicCoordinateTransform;
import org.locationtech.proj4j.CoordinateReferenceSystem;
import org.locationtech.proj4j.CoordinateTransform;
import org.locationtech.proj4j.ProjCoordinate;

public class StatsPanel extends JPanel {
	public static class StatsModel implements Cloneable {
		public static class MinAvgMax {
			public UnitizedNumber<Length> min;
			public UnitizedNumber<Length> avg;
			public UnitizedNumber<Length> max;
		}

		public static class StationPosition implements HasStationKey {
			public String cave;
			public String name;
			public UnitizedDouble<Length> easting;
			public UnitizedDouble<Length> northing;
			public UnitizedDouble<Length> elevation;

			@Override
			public StationKey stationKey() {
				return new StationKey(cave, name);
			}
		}

		public int numSelected;
		public int numCaves;
		public int numTrips;
		public int numSurveyors;
		public Unit<Length> lengthUnit;
		public UnitizedNumber<Length> totalDistance;
		public MinAvgMax distStats;
		public MinAvgMax northStats;
		public MinAvgMax eastStats;
		public MinAvgMax depthStats;
		public List<StationPosition> stationPositions;
		public CoordinateReferenceSystem coordinateReferenceSystem;
		public CoordinateReferenceSystem displayCoordinateReferenceSystem;

		public StatsModel clone() {
			try {
				return (StatsModel) super.clone();
			}
			catch (CloneNotSupportedException e) {
				throw new RuntimeException(e);
			}

		}

		public StatsModel clone(Consumer<StatsModel> mutator) {
			StatsModel clone = this.clone();
			mutator.accept(clone);
			return clone;
		}
	}

	private class MinAvgMaxLabels {
		JLabel desc = new JLabel();
		JLabel min = new JLabel();
		JLabel avg = new JLabel();
		JLabel max = new JLabel();

		public MinAvgMaxLabels(String desc) {
			this.desc.setText(desc);
			this.desc.setFont(this.desc.getFont().deriveFont(Font.BOLD));
			min.setHorizontalAlignment(SwingConstants.RIGHT);
			avg.setHorizontalAlignment(SwingConstants.RIGHT);
			max.setHorizontalAlignment(SwingConstants.RIGHT);
		}

		public void modelToView(StatsModel model, StatsModel.MinAvgMax stat) {
			if (stat == null || model == null) {
				this.min.setText("");
				this.avg.setText("");
				this.max.setText("");
				return;
			}
			Unit<Length> lengthUnit = model.lengthUnit;
			this.min
				.setText(
					stat.min == null
						? ""
						: (lengthUnit == null ? stat.min : stat.min.in(lengthUnit)).toString(decimalFormat));
			this.avg
				.setText(
					stat.avg == null
						? ""
						: (lengthUnit == null ? stat.avg : stat.avg.in(lengthUnit)).toString(decimalFormat));
			this.max
				.setText(
					stat.max == null
						? ""
						: (lengthUnit == null ? stat.max : stat.max.in(lengthUnit)).toString(decimalFormat));
		}
	}

	/**
	 *
	 */
	private static final long serialVersionUID = -3169874702144088188L;

	StatsModel model;
	NumberFormat decimalFormat;
	NumberFormat latLonFormat;
	JLabel numSelectedCaptionLabel;
	JLabel numSelectedLabel;
	JLabel numTripsCaptionLabel;
	JLabel numTripsLabel;
	JLabel numSurveyorsCaptionLabel;
	JLabel numSurveyorsLabel;
	JLabel totalDistanceCaptionLabel;
	JLabel totalDistanceLabel;
	MinAvgMaxLabels distLabels;
	MinAvgMaxLabels northLabels;
	MinAvgMaxLabels eastLabels;

	MinAvgMaxLabels depthLabels;

	JTable stationPositionsTable;
	JScrollPane stationPositionsTableScrollPane;

	public StatsPanel() {
		NumberFormat format = DecimalFormat.getInstance();
		format.setMinimumFractionDigits(1);
		format.setMaximumFractionDigits(1);
		format.setGroupingUsed(true);
		init();
		modelToView();
	}

	public void setLengthUnit(Unit<Length> lengthUnit) {
		if (this.model == null)
			return;
		setModel(model.clone(c -> c.lengthUnit = lengthUnit));
	}

	public void setDisplayCoordinateReferenceSystem(CoordinateReferenceSystem crs) {
		if (this.model == null)
			return;
		setModel(model.clone(c -> c.displayCoordinateReferenceSystem = crs));
	}

	public void setModel(StatsModel model) {
		if (this.model == model)
			return;
		this.model = model;
		modelToView();
	}

	public void modelToView() {
		if (model == null) {
			numSelectedLabel.setText("");
			numTripsLabel.setText("");
			numSurveyorsLabel.setText("");
			totalDistanceLabel.setText("");
			distLabels.modelToView(null, null);
			northLabels.modelToView(null, null);
			eastLabels.modelToView(null, null);
			depthLabels.modelToView(null, null);
			stationPositionsTable.setModel(new DefaultTableModel());
			stationPositionsTableScrollPane.setVisible(false);
			return;
		}

		Unit<Length> lengthUnit = model.lengthUnit;

		numSelectedLabel.setText("" + model.numSelected);
		numTripsLabel.setText("" + model.numTrips);
		numSurveyorsLabel.setText("" + model.numSurveyors);
		totalDistanceLabel
			.setText(
				model.totalDistance == null
					? ""
					: (lengthUnit == null ? model.totalDistance : model.totalDistance.in(lengthUnit))
						.toString(decimalFormat));
		distLabels.modelToView(model, model.distStats);
		northLabels.modelToView(model, model.northStats);
		eastLabels.modelToView(model, model.eastStats);
		depthLabels.modelToView(model, model.depthStats);

		boolean showPositionsTable = false;
		DefaultTableModel tableModel = new DefaultTableModel();
		TableColumnModel columnModel = new DefaultTableColumnModel();
		if (model.coordinateReferenceSystem != null
			&& model.displayCoordinateReferenceSystem != null
			&& model.stationPositions != null
			&& !model.stationPositions.isEmpty()) {
			showPositionsTable = true;
			CoordinateReferenceSystem toCrs = model.displayCoordinateReferenceSystem;
			CoordinateTransform xform = new BasicCoordinateTransform(model.coordinateReferenceSystem, toCrs);
			ProjCoordinate coord = new ProjCoordinate();

			tableModel.setRowCount(model.stationPositions.size());
			tableModel.setColumnIdentifiers(new String[] { "Cave", "Station", "Coordinates" });
			int i = 0;
			for (StationPosition station : model.stationPositions) {
				tableModel.setValueAt(station.cave, i, 0);
				tableModel.setValueAt(station.name, i, 1);
				coord.x = station.easting.get(Length.meters);
				coord.y = station.northing.get(Length.meters);
				coord.z = station.elevation.get(Length.meters);
				xform.transform(coord, coord);
				tableModel.setValueAt(latLonFormat.format(coord.y) + "," + latLonFormat.format(coord.x), i, 2);
				i++;
			}

			// Show the cave column if there are multiple caves, otherwise hide it
			if (model.numCaves > 1) {
				TableColumn caveColumn = new TableColumn(0, 75);
				caveColumn.setHeaderValue("Cave");
				columnModel.addColumn(caveColumn);
			}
			TableColumn stationColumn = new TableColumn(1, 75);
			stationColumn.setHeaderValue("Station");
			columnModel.addColumn(stationColumn);
			TableColumn coordinatesColumn = new TableColumn(2, 250);
			coordinatesColumn.setHeaderValue("Coordinates");
			columnModel.addColumn(coordinatesColumn);
		}
		stationPositionsTable.setModel(tableModel);
		stationPositionsTable.setColumnModel(columnModel);
		// TODO: show this once transforms between various coordinate systems are
		// working
		showPositionsTable = false;
		stationPositionsTableScrollPane.setVisible(showPositionsTable);
	}

	private void init() {
		decimalFormat = NumberFormat.getInstance();
		decimalFormat.setMinimumFractionDigits(2);
		decimalFormat.setMaximumFractionDigits(2);

		latLonFormat = NumberFormat.getInstance();
		latLonFormat.setMinimumFractionDigits(8);
		latLonFormat.setMaximumFractionDigits(8);

		distLabels = new MinAvgMaxLabels("Distance: ");
		northLabels = new MinAvgMaxLabels("North: ");
		eastLabels = new MinAvgMaxLabels("East: ");
		depthLabels = new MinAvgMaxLabels("Depth: ");

		numSelectedCaptionLabel = new JLabel("# Shots Selected: ");
		numSelectedCaptionLabel.setFont(numSelectedCaptionLabel.getFont().deriveFont(Font.BOLD));
		numSelectedLabel = new JLabel();
		numSelectedLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		numTripsCaptionLabel = new JLabel("# Trips Selected: ");
		numTripsCaptionLabel.setFont(numTripsCaptionLabel.getFont().deriveFont(Font.BOLD));
		numTripsLabel = new JLabel();
		numTripsLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		numSurveyorsCaptionLabel = new JLabel("# Surveyors Selected: ");
		numSurveyorsCaptionLabel.setFont(numSurveyorsCaptionLabel.getFont().deriveFont(Font.BOLD));
		numSurveyorsLabel = new JLabel();
		numSurveyorsLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		totalDistanceCaptionLabel = new JLabel("Total Distance: ");
		totalDistanceCaptionLabel.setFont(totalDistanceCaptionLabel.getFont().deriveFont(Font.BOLD));
		totalDistanceLabel = new JLabel();
		totalDistanceLabel.setHorizontalAlignment(SwingConstants.RIGHT);

		stationPositionsTable = new JTable();

		stationPositionsTableScrollPane = new JScrollPane(stationPositionsTable);
		stationPositionsTableScrollPane.setMinimumSize(new Dimension(200, 200));
		stationPositionsTableScrollPane.setPreferredSize(new Dimension(300, 200));

		GridBagWizard gbw = GridBagWizard.create(this);

		gbw.defaults().autoinsets(new DefaultAutoInsets(5, 5));
		gbw.defaults().east();

		int y = 0;
		gbw.put(numSelectedCaptionLabel, numSelectedLabel).x(0).intoRow().y(y++);
		gbw.put(numTripsCaptionLabel, numTripsLabel).x(0).intoRow().y(y++);
		gbw.put(numSurveyorsCaptionLabel, numSurveyorsLabel).x(0).intoRow().y(y++);
		gbw.put(totalDistanceCaptionLabel, totalDistanceLabel).x(0).intoRow().y(y++);
		gbw
			.put(numSelectedCaptionLabel, numTripsCaptionLabel, numSurveyorsCaptionLabel, totalDistanceCaptionLabel)
			.west()
			.fillx(1.0);

		JLabel minLabel = new JLabel("Min");
		minLabel.setFont(minLabel.getFont().deriveFont(Font.BOLD));
		JLabel avgLabel = new JLabel("Avg");
		avgLabel.setFont(avgLabel.getFont().deriveFont(Font.BOLD));
		JLabel maxLabel = new JLabel("Max");
		maxLabel.setFont(maxLabel.getFont().deriveFont(Font.BOLD));

		gbw.put(minLabel, avgLabel, maxLabel).x(1).intoRow().y(y++);

		for (MinAvgMaxLabels labels : Arrays.asList(distLabels, northLabels, eastLabels, depthLabels)) {
			gbw.put(labels.desc, labels.min, labels.avg, labels.max).intoRow().y(y++);
			gbw.put(labels.desc).west();
		}

		gbw.put(stationPositionsTableScrollPane).width(4).x(0).y(y++).fillboth();
	}
}
