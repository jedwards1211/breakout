package org.breakout.model;

import static org.andork.util.JavaScript.falsy;
import static org.andork.util.JavaScript.or;
import static org.andork.util.JavaScript.truthy;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
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

	private final JsonObject root = new JsonObject();

	private final JsonObject caves = new JsonObject();

	private final IdentityHashMap<Trip, JsonObject> trips = new IdentityHashMap<>();

	private final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");

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
		// TODO: splays && LRUD-only rows
		if (falsy(row.getFromStation()) || falsy(row.getToStation())) {
			return;
		}

		JsonObject trip = export(row.getTrip());
		JsonArray survey = trip.getAsJsonArray("survey");

		JsonObject lastFromStation = survey.size() < 3 ? emptyObject : survey.get(survey.size() - 3).getAsJsonObject();
		JsonObject lastToStation = survey.size() < 1 ? emptyObject : survey.get(survey.size() - 1).getAsJsonObject();

		JsonArray measurements;

		if (Objects.equals(row.getOverrideFromCave(), lastFromStation.get("cave")) &&
				Objects.equals(row.getFromStation(), lastFromStation.get("station")) &&
				Objects.equals(row.getOverrideToCave(), lastToStation.get("cave")) &&
				Objects.equals(row.getToStation(), lastToStation.get("station"))) {
			// same from and to station;
			// prepare to add measurements to the last shot
			measurements = survey.get(survey.size() - 2).getAsJsonObject().getAsJsonArray("measurements");
		} else {
			// if the last station doesn't match the from station of this shot,
			// insert an empty (non) shot and a new from station
			JsonObject fromStation = null;
			if (Objects.equals(row.getOverrideFromCave(), lastToStation.get("cave")) &&
					Objects.equals(row.getFromStation(), lastToStation.get("station"))) {
				fromStation = lastToStation;
			} else if (survey.size() != 0) {
				survey.add(new JsonObject());
			}
			if (fromStation == null) {
				fromStation = new JsonObject();
				if (truthy(row.getOverrideFromCave())) {
					fromStation.addProperty("cave", row.getOverrideFromCave());
				}
				fromStation.addProperty("station", row.getFromStation());
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

			// insert shot
			measurements = new JsonArray();
			JsonObject shot = new JsonObject();
			shot.add("measurements", measurements);
			shot.addProperty("breakoutRow", rowIndex);
			survey.add(shot);

			// insert to station
			JsonObject toStation = new JsonObject();
			if (truthy(row.getOverrideToCave())) {
				toStation.addProperty("cave", row.getOverrideToCave());
			}
			toStation.addProperty("station", row.getToStation());
			survey.add(toStation);
		}

		// add frontsight measurements
		JsonObject fs = new JsonObject();
		fs.addProperty("dir", "fs");
		if (truthy(row.getDistance())) {
			fs.addProperty("dist", row.getDistance());
		}
		if (truthy(row.getFrontAzimuth())) {
			fs.addProperty("azm", row.getFrontAzimuth());
		}
		if (truthy(row.getFrontInclination())) {
			fs.addProperty("inc", row.getFrontInclination());
		}
		measurements.add(fs);

		// add backsight measurements
		if (truthy(row.getBackAzimuth()) || truthy(row.getBackInclination())) {
			JsonObject bs = new JsonObject();
			bs.addProperty("dir", "bs");
			if (truthy(row.getBackAzimuth())) {
				bs.addProperty("azm", row.getBackAzimuth());
			}
			if (truthy(row.getBackInclination())) {
				bs.addProperty("inc", row.getBackInclination());
			}
			measurements.add(bs);
		}
	}

	public JsonObject export(Trip _trip) {
		Trip trip = _trip == null ? defaultTrip : _trip;
		JsonObject exported = trips.get(trip);
		if (exported == null) {
			exported = new JsonObject();

			String caveName = or(trip.getCave(), "");
			JsonObject cave = (JsonObject) caves.get(caveName);
			if (cave == null) {
				cave = new JsonObject();
				cave.add("trips", new JsonArray());
				caves.add(caveName, cave);
			}
			((JsonArray) cave.get("trips")).add(exported);

			if (trip.getName() != null) {
				exported.addProperty("name", trip.getName());
			}
			if (trip.getDate() != null) {
				exported.addProperty("date", dateFormat.format(trip.getDate()));
			}
			if (trip.getSurveyors() != null) {
				JsonObject surveyors = new JsonObject();
				for (String surveyor : trip.getSurveyors()) {
					surveyors.add(surveyor, null);
				}
				exported.add("surveyors", surveyors);
			}
			exported.addProperty("distUnit", metacaveLengthUnits.get(trip.getDistanceUnit()));
			exported.addProperty("angleUnit", metacaveAngleUnits.get(trip.getAngleUnit()));

			if (trip.getOverrideFrontAzimuthUnit() != null) {
				exported.addProperty("fsAzmUnit", metacaveAngleUnits.get(trip.getOverrideFrontAzimuthUnit()));
			}
			if (trip.getOverrideBackAzimuthUnit() != null) {
				exported.addProperty("bsAzmUnit", metacaveAngleUnits.get(trip.getOverrideBackAzimuthUnit()));
			}
			if (trip.getOverrideFrontInclinationUnit() != null) {
				exported.addProperty("fsIncUnit", metacaveAngleUnits.get(trip.getOverrideFrontInclinationUnit()));
			}
			if (trip.getOverrideBackInclinationUnit() != null) {
				exported.addProperty("bsIncUnit", metacaveAngleUnits.get(trip.getOverrideBackInclinationUnit()));
			}

			exported.addProperty("azmBacksightsCorrected", trip.areBackAzimuthsCorrected());
			exported.addProperty("incBacksightsCorrected", trip.areBackInclinationsCorrected());
			if (trip.getDeclination() != null) {
				exported.addProperty("declination", trip.getDeclination());
			}
			if (trip.getDistanceCorrection() != null) {
				exported.addProperty("distCorrection", trip.getDistanceCorrection());
			}
			if (trip.getFrontAzimuthCorrection() != null) {
				exported.addProperty("azmFsCorrection", trip.getFrontAzimuthCorrection());
			}
			if (trip.getBackAzimuthCorrection() != null) {
				exported.addProperty("azmBsCorrection", trip.getBackAzimuthCorrection());
			}
			if (trip.getFrontInclinationCorrection() != null) {
				exported.addProperty("incFsCorrection", trip.getFrontInclinationCorrection());
			}
			if (trip.getBackInclinationCorrection() != null) {
				exported.addProperty("incBsCorrection", trip.getBackInclinationCorrection());
			}
			exported.add("survey", new JsonArray());
		}
		return exported;
	}

	public JsonObject getRoot() {
		return root;
	}
}
