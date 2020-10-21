package org.breakout;

import java.awt.Dimension;
import java.awt.Font;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Date;

import javax.swing.JLabel;
import javax.swing.JPanel;

import org.andork.awt.GridBagWizard;
import org.andork.unit.Angle;
import org.andork.unit.Length;
import org.andork.unit.Unit;
import org.andork.unit.UnitizedDouble;
import org.andork.util.StringUtils;
import org.breakout.model.parsed.ParsedField;
import org.breakout.model.parsed.ParsedShot;
import org.breakout.model.parsed.ParsedShotMeasurement;
import org.breakout.model.parsed.ParsedTrip;

public class HintLabels extends JPanel {
	private static final long serialVersionUID = 8575752635019574014L;

	private JLabel stationsLabel = new JLabel("Stations: ");
	private JLabel stationsValue = new JLabel();
	private JLabel distanceLabel = new JLabel("Dist: ");
	private JLabel distanceValue = new JLabel();
	private JLabel azimuthLabel = new JLabel("Azm: ");
	private JLabel azimuthValue = new JLabel();
	private JLabel inclinationLabel = new JLabel("Inc: ");
	private JLabel inclinationValue = new JLabel();
	private JLabel tripNameLabel = new JLabel("Trip: ");
	private JLabel tripNameValue = new JLabel();
	private JLabel dateLabel = new JLabel("Date: ");
	private JLabel dateValue = new JLabel();
	private JLabel surveyorsLabel = new JLabel("Surveyors: ");
	private JLabel surveyorsValue = new JLabel();;
	//
	// static class LabelGroup {
	// JLabel label;
	// JLabel value;
	// float weight;
	// int minWidth;
	// String minWidthText;
	//
	// public LabelGroup(JLabel label, JLabel value, float weight) {
	// super();
	// this.label = label;
	// this.value = value;
	// this.weight = weight;
	// }
	//
	// LabelGroup minWidth(int minWidth) {
	// this.minWidth = minWidth;
	// return this;
	// }
	//
	// LabelGroup minWidthText(String minWidthText) {
	// this.minWidthText = minWidthText;
	// return this;
	// }
	//
	// void setBounds(int x, int y, float width, int height) {
	// int minWidth = this.minWidth;
	// if (minWidthText != null) {
	// minWidth = value.getFontMetrics(value.getFont()).stringWidth(minWidthText);
	// }
	// label.setBounds(x, y, label.getPreferredSize().width, height);
	// value
	// .setBounds(
	// label.getX() + label.getWidth(),
	// y,
	// (int) Math.max(minWidth, width - label.getWidth()),
	// height);
	// }
	//
	// public void setVisible(boolean visible) {
	// label.setVisible(visible);
	// value.setVisible(visible);
	// }
	//
	// }
	//
	// static LabelGroup labelGroup(JLabel label, JLabel value, double weight) {
	// return new LabelGroup(label, value, (float) weight);
	// }

	public HintLabels() {
		setBackground(null);
		setForeground(null);

		for (JLabel label : Arrays
			.asList(
				stationsLabel,
				distanceLabel,
				azimuthLabel,
				inclinationLabel,
				tripNameLabel,
				dateLabel,
				surveyorsLabel)) {
			label.setFont(label.getFont().deriveFont(Font.PLAIN));
			label.setBackground(null);
			label.setForeground(null);
			label.setVisible(false);
			add(label);
		}

		for (JLabel value : Arrays
			.asList(
				stationsValue,
				distanceValue,
				azimuthValue,
				inclinationValue,
				tripNameValue,
				dateValue,
				surveyorsValue)) {
			value.setFont(value.getFont().deriveFont(Font.BOLD));
			value.setBackground(null);
			value.setForeground(null);
			value.setVisible(false);
			add(value);
		}

		setPrefSize(stationsValue, "AAAAAAA - AAAAAAA");
		setPrefSize(distanceValue, "10000.0 ft");
		setPrefSize(azimuthValue, "360.0 deg/360.0 deg");
		setPrefSize(inclinationValue, "-90.0 deg/-90.0 deg");

		GridBagWizard w = GridBagWizard.create(this);
		w.put(stationsLabel).xy(0, 0);
		w.put(stationsLabel, distanceLabel).intoColumn().west();
		w.put(stationsValue).rightOf(stationsLabel);
		w.put(stationsValue, distanceValue).intoColumn().east();

		w.put(azimuthLabel).rightOf(stationsValue);
		w.put(azimuthLabel, inclinationLabel).intoColumn().west().addToInsets(0, 10, 0, 0);
		w.put(azimuthValue).rightOf(azimuthLabel);
		w.put(azimuthValue, inclinationValue).intoColumn().east();

		w.put(tripNameLabel).rightOf(azimuthValue);
		w.put(tripNameLabel, surveyorsLabel).intoColumn().west().addToInsets(0, 10, 0, 0);
		w.put(tripNameValue).rightOf(tripNameLabel);
		w.put(tripNameValue, surveyorsValue).intoColumn().east().fillx(1);
		w.put(dateLabel).rightOf(tripNameValue).addToInsets(0, 10, 0, 0);
		w.put(dateValue).rightOf(dateLabel);
		w.put(surveyorsValue).width(3);

		setPreferredSize(new Dimension(800, 30));
	}

	void setPrefSize(JLabel label, String text) {
		label
			.setPreferredSize(
				new Dimension(label.getFontMetrics(label.getFont()).stringWidth(text), label.getFont().getSize()));
		label.setMinimumSize(label.getPreferredSize());
	}

	interface UpdateOptions {
		ParsedShot shot();

		Unit<Length> lengthUnit();

		Unit<Angle> angleUnit();
	}

	public void update(UpdateOptions options) {
		ParsedShot shot = options.shot();
		ParsedTrip trip = shot != null ? shot.trip : null;

		stationsLabel.setVisible(shot != null);
		stationsValue.setVisible(shot != null);
		distanceLabel.setVisible(shot != null);
		distanceValue.setVisible(shot != null);
		azimuthLabel.setVisible(shot != null);
		azimuthValue.setVisible(shot != null);
		inclinationLabel.setVisible(shot != null);
		inclinationValue.setVisible(shot != null);

		tripNameLabel.setVisible(trip != null);
		tripNameValue.setVisible(trip != null);
		dateLabel.setVisible(trip != null);
		dateValue.setVisible(trip != null);
		surveyorsLabel.setVisible(trip != null);
		surveyorsValue.setVisible(trip != null);

		if (shot == null) {
			return;
		}

		UnitizedDouble<Length> distance = ParsedShotMeasurement.getFirstDistance(shot.measurements);
		UnitizedDouble<Angle> frontAzimuth = ParsedShotMeasurement.getFirstFrontAzimuth(shot.measurements);
		UnitizedDouble<Angle> backAzimuth = ParsedShotMeasurement.getFirstBackAzimuth(shot.measurements);
		UnitizedDouble<Angle> frontInclination = ParsedShotMeasurement.getFirstFrontInclination(shot.measurements);
		UnitizedDouble<Angle> backInclination = ParsedShotMeasurement.getFirstBackInclination(shot.measurements);

		Unit<Length> lengthUnit = options.lengthUnit();
		Unit<Angle> angleUnit = options.angleUnit();

		NumberFormat format = DecimalFormat.getInstance();
		format.setMaximumFractionDigits(1);
		format.setMinimumFractionDigits(1);
		format.setGroupingUsed(false);

		String formattedDistance = distance == null ? "--" : distance.in(lengthUnit).toString(format);
		String formattedFrontAzimuth = frontAzimuth == null ? "--" : format.format(frontAzimuth.get(angleUnit));
		String formattedBackAzimuth = backAzimuth == null ? "--" : format.format(backAzimuth.get(angleUnit));
		String formattedFrontInclination =
			frontInclination == null ? "--" : format.format(frontInclination.get(angleUnit));
		String formattedBackInclination =
			backInclination == null ? "--" : format.format(backInclination.get(angleUnit));

		stationsValue
			.setText(
				String
					.format(
						"%s - %s",
						ParsedField.getValue(shot.fromStation.name),
						ParsedField.getValue(shot.toStation.name)));
		distanceValue.setText(formattedDistance);
		azimuthValue.setText(String.format("%s/%s %s", formattedFrontAzimuth, formattedBackAzimuth, angleUnit));
		inclinationValue
			.setText(String.format("%s/%s %s", formattedFrontInclination, formattedBackInclination, angleUnit));

		if (trip == null) {
			return;
		}

		tripNameValue.setText(trip.name);
		Date date = ParsedField.getValue(trip.date);
		dateValue.setText(date != null ? SettingsDrawer.maxDateFormat.format(date) : "");
		surveyorsValue.setText(trip.surveyors != null ? StringUtils.join(", ", trip.surveyors) : "");
	}
}
