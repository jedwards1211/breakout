package org.breakout.model;

import static org.andork.util.JavaScript.falsy;
import static org.andork.util.JavaScript.or;
import static org.andork.util.JavaScript.truthy;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import org.andork.unit.Angle;
import org.andork.unit.Length;
import org.andork.unit.Unit;
import org.andork.util.Java7.Objects;
import org.breakout.model.SurveyTableModel.Row;
import org.breakout.model.SurveyTableModel.Trip;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class MetacaveExporter {
	private static final Trip defaultTrip = new Trip();
	private static final JsonObject emptyObject = new JsonObject();
	private static final Map<Unit<Length>, String> metacaveLengthUnits = new HashMap<>();
	private static final Map<Unit<Angle>, String> metacaveAngleUnits = new HashMap<>();

	static {
		metacaveLengthUnits.put(Length.meters, "m");
		metacaveLengthUnits.put(Length.inches, "in");
		metacaveLengthUnits.put(Length.feet, "ft");
		metacaveLengthUnits.put(Length.yards, "yd");
		metacaveLengthUnits.put(Length.kilometers, "km");

		metacaveAngleUnits.put(Angle.degrees, "deg");
		metacaveAngleUnits.put(Angle.gradians, "grad");
		metacaveAngleUnits.put(Angle.milsNATO, "mil");
		metacaveAngleUnits.put(Angle.percentGrade, "%");
	}

	private static void addAngleUnitProperty(JsonObject obj, String property, Unit<Angle> unit) {
		if (unit != null) {
			obj.addProperty(property, metacaveAngleUnits.get(unit));
		}
	}

	private static void addLengthUnitProperty(JsonObject obj, String property, Unit<Length> unit) {
		if (unit != null) {
			obj.addProperty(property, metacaveLengthUnits.get(unit));
		}
	}

	private static void addProperty(JsonObject obj, String property, boolean value) {
		obj.addProperty(property, value);
	}

	private static void addProperty(JsonObject obj, String property, String value) {
		if (truthy(value)) {
			obj.addProperty(property, value);
		}
	}

	private static String getAsString(JsonObject obj, String property) {
		if (!obj.has(property) || obj.get(property).isJsonNull()) {
			return null;
		}
		return obj.get(property).getAsString();
	}

	private final JsonObject root = new JsonObject();

	private final JsonObject caves = new JsonObject();

	private final IdentityHashMap<Trip, JsonObject> trips = new IdentityHashMap<>();

	public MetacaveExporter() {
		root.add("caves", caves);
	}

	public void export(List<Row> rows) {
		int rowIndex = 0;
		for (Row row : rows) {
			export(row, rowIndex++);
		}
	}

	public void export(Row row, int rowIndex) {
		if (row == null) {
			return;
		}
		// TODO: splays && LRUD-only rows
		if (falsy(row.getFromStation()) && falsy(row.getToStation())) {
			return;
		}

		JsonObject trip = export(row.getTrip());
		JsonArray survey = trip.getAsJsonArray("survey");

		JsonObject lastFromStation = survey.size() < 3 ? emptyObject : survey.get(survey.size() - 3).getAsJsonObject();
		JsonObject lastToStation = survey.size() < 1 ? emptyObject : survey.get(survey.size() - 1).getAsJsonObject();

		JsonArray measurements = null;

		if (Objects.equals(row.getOverrideFromCave(), getAsString(lastFromStation, "cave")) &&
				Objects.equals(row.getFromStation(), getAsString(lastFromStation, "station")) &&
				Objects.equals(row.getOverrideToCave(), getAsString(lastToStation, "cave")) &&
				Objects.equals(row.getToStation(), getAsString(lastToStation, "station"))) {
			// same from and to station;
			// prepare to add measurements to the last shot
			measurements = survey.get(survey.size() - 2).getAsJsonObject().getAsJsonArray("measurements");
		} else {
			// if the last station doesn't match the from station of this shot,
			// insert an empty (non) shot and a new from station
			JsonObject fromStation = null;
			if (Objects.equals(row.getOverrideFromCave(), getAsString(lastToStation, "cave")) &&
					Objects.equals(row.getFromStation(), getAsString(lastToStation, "station"))) {
				fromStation = lastToStation;
			} else if (survey.size() != 0) {
				// insert empty shot
				survey.add(new JsonObject());
			}
			if (fromStation == null) {
				fromStation = new JsonObject();
				addProperty(fromStation, "cave", row.getOverrideFromCave());
				addProperty(fromStation, "station", row.getFromStation());
				survey.add(fromStation);
			}
			// add lruds to from station
			if (!fromStation.has("lrud")) {
				JsonArray lrud = new JsonArray();
				lrud.add(row.getLeft());
				lrud.add(row.getRight());
				lrud.add(row.getUp());
				lrud.add(row.getDown());
				fromStation.add("lrud", lrud);
				fromStation.addProperty("breakoutRow", rowIndex);
			}

			if (truthy(row.getToStation())) {
				// insert shot
				measurements = new JsonArray();
				JsonObject shot = new JsonObject();
				shot.add("measurements", measurements);
				survey.add(shot);

				// insert to station
				JsonObject toStation = new JsonObject();
				addProperty(toStation, "cave", row.getOverrideToCave());
				addProperty(toStation, "station", row.getToStation());
				survey.add(toStation);
			} else if (truthy(or(
					row.getDistance(), row.getFrontAzimuth(), row.getFrontInclination(), row.getBackAzimuth(),
					row.getBackInclination()))) {
				measurements = new JsonArray();
				fromStation.add("splays", measurements);
			}
		}

		// add frontsight measurements
		if (measurements != null) {
			JsonObject fs = new JsonObject();
			addProperty(fs, "dir", "fs");
			fs.addProperty("breakoutRow", rowIndex);
			addProperty(fs, "dist", row.getDistance());
			addProperty(fs, "azm", row.getFrontAzimuth());
			addProperty(fs, "inc", row.getFrontInclination());
			measurements.add(fs);

			// add backsight measurements
			if (truthy(row.getBackAzimuth()) || truthy(row.getBackInclination())) {
				JsonObject bs = new JsonObject();
				addProperty(bs, "dir", "bs");
				bs.addProperty("breakoutRow", rowIndex);
				addProperty(bs, "azm", row.getBackAzimuth());
				addProperty(bs, "inc", row.getBackInclination());
				measurements.add(bs);
			}
		}
	}

	public JsonObject export(Trip _trip) {
		Trip trip = _trip == null ? defaultTrip : _trip;
		JsonObject exported = trips.get(trip);
		if (exported == null) {
			exported = new JsonObject();
			trips.put(trip, exported);

			String caveName = or(trip.getCave(), "");
			JsonObject cave = (JsonObject) caves.get(caveName);
			if (cave == null) {
				cave = new JsonObject();
				cave.add("trips", new JsonArray());
				caves.add(caveName, cave);
			}
			((JsonArray) cave.get("trips")).add(exported);

			addProperty(exported, "name", trip.getName());
			addProperty(exported, "date", trip.getDate());
			addProperty(exported, "surveyNotesFile", trip.getSurveyNotes());
			if (trip.getSurveyors() != null) {
				JsonObject surveyors = new JsonObject();
				for (String surveyor : trip.getSurveyors()) {
					surveyors.add(surveyor, new JsonObject());
				}
				exported.add("surveyors", surveyors);
			}
			addLengthUnitProperty(exported, "distUnit", trip.getDistanceUnit());
			addAngleUnitProperty(exported, "angleUnit", trip.getAngleUnit());

			addAngleUnitProperty(exported, "azmFsUnit", trip.getOverrideFrontAzimuthUnit());
			addAngleUnitProperty(exported, "azmBsUnit", trip.getOverrideBackAzimuthUnit());
			addAngleUnitProperty(exported, "incFsUnit", trip.getOverrideFrontInclinationUnit());
			addAngleUnitProperty(exported, "incBsUnit", trip.getOverrideBackInclinationUnit());

			addProperty(exported, "azmBacksightsCorrected", trip.areBackAzimuthsCorrected());
			addProperty(exported, "incBacksightsCorrected", trip.areBackInclinationsCorrected());
			addProperty(exported, "declination", trip.getDeclination());
			addProperty(exported, "distCorrection", trip.getDistanceCorrection());
			addProperty(exported, "azmFsCorrection", trip.getFrontAzimuthCorrection());
			addProperty(exported, "azmBsCorrection", trip.getBackAzimuthCorrection());
			addProperty(exported, "incFsCorrection", trip.getFrontInclinationCorrection());
			addProperty(exported, "incBsCorrection", trip.getBackInclinationCorrection());
			exported.add("survey", new JsonArray());
		}
		return exported;
	}

	public JsonObject getRoot() {
		return root;
	}
}