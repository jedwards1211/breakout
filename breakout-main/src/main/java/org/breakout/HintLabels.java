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
import org.breakout.model.calc.CalcShot;
import org.breakout.model.parsed.ParsedField;
import org.breakout.model.parsed.ParsedShot;
import org.breakout.model.parsed.ParsedShotMeasurement;
import org.breakout.model.parsed.ParsedTrip;

public class HintLabels extends JPanel {
	private static final long serialVersionUID = 8575752635019574014L;

	private JLabel fromLabel = new JLabel("From: ");
	private JLabel fromValue = new JLabel();
	private JLabel toLabel = new JLabel("To: ");
	private JLabel toValue = new JLabel();
	private JLabel distanceLabel = new JLabel("Dist: ");
	private JLabel distanceValue = new JLabel();
	private JLabel elevationLabel = new JLabel("Elev: ");
	private JLabel elevationValue = new JLabel();
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

	public HintLabels() {
		setBackground(null);
		setForeground(null);

		for (JLabel label : Arrays
			.asList(
				fromLabel,
				toLabel,
				distanceLabel,
				elevationLabel,
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
				fromValue,
				toValue,
				distanceValue,
				elevationValue,
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

		setPrefSize(fromValue, "AAAAAA");
		setPrefSize(toValue, "AAAAAA");
		setPrefSize(distanceValue, "10000.0 ft");
		setPrefSize(elevationValue, "1000.0/1000.0 ft");
		setPrefSize(azimuthValue, "360.0/360.0 deg");
		setPrefSize(inclinationValue, "-90.0/-90.0 deg");

		GridBagWizard w = GridBagWizard.create(this);
		w.defaults().west();
		int col = 0;
		w.put(fromLabel).xy(col++, 0);
		w.put(fromLabel, toLabel).intoColumn();
		w.put(fromValue).xy(col++, 0);
		w.put(fromValue, toValue).intoColumn();

		w.put(distanceLabel).xy(col++, 0);
		w.put(distanceLabel, elevationLabel).intoColumn();
		w.put(distanceValue).xy(col++, 0);
		w.put(distanceValue, elevationValue).intoColumn();

		w.put(azimuthLabel).xy(col++, 0);
		w.put(azimuthLabel, inclinationLabel).intoColumn().addToInsets(0, 10, 0, 0);
		w.put(azimuthValue).xy(col++, 0);
		w.put(azimuthValue, inclinationValue).intoColumn();

		w.put(tripNameLabel).xy(col++, 0);
		w.put(tripNameLabel, surveyorsLabel).intoColumn().addToInsets(0, 10, 0, 0);
		w.put(tripNameValue).xy(col++, 0);
		w.put(tripNameValue, surveyorsValue).intoColumn().fillx(1);
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

		CalcShot calcShot();

		Unit<Length> lengthUnit();

		Unit<Angle> angleUnit();
	}

	public void update(UpdateOptions options) {
		ParsedShot shot = options.shot();
		CalcShot calcShot = options.calcShot();
		ParsedTrip trip = shot != null ? shot.trip : null;

		fromLabel.setVisible(shot != null);
		fromValue.setVisible(shot != null);
		toLabel.setVisible(shot != null);
		toValue.setVisible(shot != null);
		distanceLabel.setVisible(shot != null);
		distanceValue.setVisible(shot != null);
		elevationLabel.setVisible(shot != null);
		elevationValue.setVisible(shot != null);
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

		String formattedFromElevation =
			calcShot == null ? "--" : format.format(Length.meters(calcShot.fromStation.position[1]).get(lengthUnit));
		String formattedToElevation =
			calcShot == null ? "--" : format.format(Length.meters(calcShot.toStation.position[1]).get(lengthUnit));

		fromValue.setText(ParsedField.getValue(shot.fromStation.name));
		toValue.setText(ParsedField.getValue(shot.toStation.name));
		distanceValue.setText(formattedDistance);
		elevationValue.setText(String.format("%s/%s %s", formattedFromElevation, formattedToElevation, lengthUnit));
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
