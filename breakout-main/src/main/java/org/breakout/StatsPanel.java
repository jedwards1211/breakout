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

import java.awt.Font;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Arrays;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import org.andork.awt.GridBagWizard;
import org.andork.awt.GridBagWizard.DefaultAutoInsets;
import org.andork.unit.Length;
import org.andork.unit.Unit;
import org.andork.unit.UnitizedNumber;
import org.osgeo.proj4j.CoordinateReferenceSystem;

public class StatsPanel extends JPanel {
	public static class StatsModel implements Cloneable {
		public static class MinAvgMax {
			public UnitizedNumber<Length> min;
			public UnitizedNumber<Length> avg;
			public UnitizedNumber<Length> max;
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

		public StatsModel clone() {
			try {
				return (StatsModel) super.clone();
			}
			catch (CloneNotSupportedException e) {
				throw new RuntimeException(e);
			}

		}

		public StatsModel withLengthUnit(Unit<Length> lengthUnit) {
			StatsModel clone = this.clone();
			clone.lengthUnit = lengthUnit;
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
			if (stat == null || model == null || model.lengthUnit == null) {
				this.min.setText("");
				this.avg.setText("");
				this.max.setText("");
				return;
			}
			Unit<Length> lengthUnit = model.lengthUnit;
			this.min.setText(stat.min == null ? "" : stat.min.in(lengthUnit).toString(decimalFormat));
			this.avg.setText(stat.avg == null ? "" : stat.avg.in(lengthUnit).toString(decimalFormat));
			this.max.setText(stat.max == null ? "" : stat.max.in(lengthUnit).toString(decimalFormat));
		}
	}

	/**
	 *
	 */
	private static final long serialVersionUID = -3169874702144088188L;

	StatsModel model;
	NumberFormat decimalFormat;
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

	public StatsPanel() {
		NumberFormat format = DecimalFormat.getInstance();
		format.setMinimumFractionDigits(1);
		format.setMaximumFractionDigits(1);
		format.setGroupingUsed(true);
		init();
	}

	public void setLengthUnit(Unit<Length> lengthUnit) {
		if (this.model == null)
			return;
		setModel(model.withLengthUnit(lengthUnit));
	}

	public void setModel(StatsModel model) {
		if (this.model == model)
			return;
		this.model = model;
		modelToView();
	}

	public void modelToView() {
		if (model == null) {
			this.numSelectedLabel.setText("");
			this.numTripsLabel.setText("");
			this.numSurveyorsLabel.setText("");
			this.totalDistanceLabel.setText("");
			this.distLabels.modelToView(null, null);
			this.northLabels.modelToView(null, null);
			this.eastLabels.modelToView(null, null);
			this.depthLabels.modelToView(null, null);
			return;
		}

		this.numSelectedLabel.setText("" + model.numSelected);
		this.numTripsLabel.setText("" + model.numTrips);
		this.numSurveyorsLabel.setText("" + model.numSurveyors);
		this.totalDistanceLabel.setText(model.totalDistance == null ? "" : model.totalDistance.toString(decimalFormat));
		this.distLabels.modelToView(model, model.distStats);
		this.northLabels.modelToView(model, model.northStats);
		this.eastLabels.modelToView(model, model.eastStats);
		this.depthLabels.modelToView(model, model.depthStats);
	}

	private void init() {
		decimalFormat = NumberFormat.getInstance();
		decimalFormat.setMinimumFractionDigits(2);
		decimalFormat.setMaximumFractionDigits(2);

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
	}
}
