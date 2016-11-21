package org.breakout.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.andork.unit.Angle;
import org.andork.unit.Length;
import org.andork.unit.Unit;
import org.breakout.model.SurveyTableModel.Row;
import org.breakout.model.SurveyTableModel.Trip;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

public class MetacaveImporter {
	private static final Map<String, Unit<Length>> metacaveLengthUnits = new HashMap<>();
	private static final Map<String, Unit<Angle>> metacaveAngleUnits = new HashMap<>();

	static {
		metacaveLengthUnits.put("m", Length.meters);
		metacaveLengthUnits.put("in", Length.inches);
		metacaveLengthUnits.put("ft", Length.feet);
		metacaveLengthUnits.put("yd", Length.yards);
		metacaveLengthUnits.put("km", Length.kilometers);

		metacaveAngleUnits.put("deg", Angle.degrees);
		metacaveAngleUnits.put("grad", Angle.gradians);
		metacaveAngleUnits.put("mil", Angle.milsNATO);
		metacaveAngleUnits.put("%", Angle.percentGrade);
	}

	private static Unit<Angle> getAngleUnit(JsonObject obj, String property) {
		if (!obj.has(property)) {
			return null;
		}
		return metacaveAngleUnits.get(obj.get(property).getAsString());
	}

	private static boolean getAsBoolean(JsonObject obj, String property) {
		if (!obj.has(property)) {
			return false;
		}
		JsonElement elem = obj.get(property);
		return elem.isJsonNull() ? null : obj.get(property).getAsBoolean();
	}

	private static Integer getAsInteger(JsonObject obj, String property) {
		if (!obj.has(property)) {
			return null;
		}
		return obj.get(property).getAsInt();
	}

	private static String getAsString(JsonObject obj, String property) {
		if (!obj.has(property)) {
			return null;
		}
		JsonElement elem = obj.get(property);
		return elem.isJsonNull() ? null : elem.getAsString();
	}

	private static String getMeasurement(JsonElement elem) {
		if (elem == null || elem.isJsonNull()) {
			return null;
		}
		if (elem.isJsonArray()) {
			return join(elem.getAsJsonArray(), " ");
		}
		return elem.getAsString();
	}

	private static void getMeasurements(JsonObject obj, Row row) {
		row.setDistance(getAsString(obj, "dist"));
		if ("bs".equals(getAsString(obj, "dir"))) {
			row.setBackAzimuth(getAsString(obj, "azm"));
			row.setBackInclination(getAsString(obj, "inc"));
		} else {
			row.setFrontAzimuth(getAsString(obj, "azm"));
			row.setFrontInclination(getAsString(obj, "inc"));
		}
	}

	private static Integer getRowIndex(JsonObject obj) {
		return getAsInteger(obj, "breakoutRow");
	}

	private static String join(JsonArray array, String separator) {
		if (array.size() == 0) {
			return "";
		}
		StringBuilder builder = new StringBuilder();
		builder.append(array.get(0).getAsString());
		for (int i = 1; i < array.size(); i++) {
			builder.append(separator).append(array.get(i).getAsString());
		}
		return builder.toString();
	}

	private final List<Row> rows = new ArrayList<>();

	private final IdentityHashMap<JsonObject, Trip> trips = new IdentityHashMap<>();

	public List<Row> getRows() {
		List<Row> result = new ArrayList<Row>();
		Row lastRow = null;
		for (Row row : rows) {
			if (row == null) {
				row = new Row();
				if (lastRow != null) {
					row.setTrip(lastRow.getTrip());
				}
			}
			result.add(row);
			lastRow = row;
		}
		return result;
	}

	public void importMetacave(InputStream stream) throws IOException {
		importMetacave(new InputStreamReader(stream, "UTF-8"));
	}

	public void importMetacave(JsonObject obj) {
		JsonObject caves = obj.getAsJsonObject("caves");
		if (caves == null || caves.size() == 0) {
			return;
		}
		caves.entrySet().forEach(e -> {
			JsonObject cave = e.getValue().getAsJsonObject();
			JsonArray trips = cave.getAsJsonArray("trips");
			if (trips == null) {
				return;
			}
			for (int i = 0; i < trips.size(); i++) {
				Trip trip = importTrip(trips.get(i).getAsJsonObject());
				trip.setCave(e.getKey());
			}
		});
	}

	public void importMetacave(Path path) throws IOException {
		importMetacave(path.toFile());
	}

	public void importMetacave(File file) throws IOException {
		try (InputStream in = new FileInputStream(file)) {
			importMetacave(in);
		}
	}

	private void importMetacave(Reader reader) {
		JsonObject obj = new JsonParser().parse(new JsonReader(reader)).getAsJsonObject();
		importMetacave(obj);
	}

	public Trip importTrip(JsonObject obj) {
		Trip trip = importTripHeader(obj);
		importTripSurvey(trip, obj.getAsJsonArray("survey"));
		return trip;
	}

	public Trip importTripHeader(JsonObject obj) {
		Trip trip = trips.get(obj);
		if (trip == null) {
			trip = new Trip();
			trips.put(obj, trip);

			if (obj.has("name")) {
				trip.setName(obj.get("name").getAsString());
			}
			if (obj.has("date")) {
				trip.setDate(obj.get("date").getAsString());
			}
			if (obj.has("surveyors")) {
				List<String> surveyors = new ArrayList<>();
				obj.get("surveyors").getAsJsonObject().entrySet().forEach(e -> surveyors.add(e.getKey()));
				trip.setSurveyors(surveyors);
			}
			if (obj.has("surveyNotesFile")) {
				trip.setSurveyNotes(obj.get("surveyNotesFile").getAsString());
			}
			if (obj.has("distUnit")) {
				trip.setDistanceUnit(metacaveLengthUnits.get(obj.get("distUnit").getAsString()));
			}
			if (obj.has("angleUnit")) {
				trip.setAngleUnit(metacaveAngleUnits.get(obj.get("angleUnit").getAsString()));
			}
			trip.setBackAzimuthsCorrected(getAsBoolean(obj, "azmBacksightsCorrected"));
			trip.setBackInclinationsCorrected(getAsBoolean(obj, "incBacksightsCorrected"));
			trip.setDeclination(getAsString(obj, "declination"));
			trip.setOverrideFrontAzimuthUnit(getAngleUnit(obj, "azmFsUnit"));
			trip.setOverrideBackAzimuthUnit(getAngleUnit(obj, "azmBsUnit"));
			trip.setOverrideFrontInclinationUnit(getAngleUnit(obj, "incFsUnit"));
			trip.setOverrideBackInclinationUnit(getAngleUnit(obj, "incBsUnit"));
			trip.setDistanceCorrection(getAsString(obj, "distCorrection"));
			trip.setFrontAzimuthCorrection(getAsString(obj, "azmFsCorrection"));
			trip.setBackAzimuthCorrection(getAsString(obj, "azmBsCorrection"));
			trip.setFrontInclinationCorrection(getAsString(obj, "incFsCorrection"));
			trip.setBackInclinationCorrection(getAsString(obj, "incBsCorrection"));
		}
		return trip;
	}

	public void importTripSurvey(Trip trip, JsonArray survey) {
		if (survey == null || survey.size() == 0) {
			return;
		}

		for (int i = 0; i < survey.size(); i += 2) {
			final JsonObject fromStation = survey.get(i).getAsJsonObject();
			final JsonObject shot = i + 1 < survey.size() ? survey.get(i + 1).getAsJsonObject() : null;
			final JsonObject toStation = i + 2 < survey.size() ? survey.get(i + 2).getAsJsonObject() : null;

			final int defaultRow = rows.size();

			Function<JsonObject, Row> getRow = obj -> {
				Integer rowIndex = getRowIndex(obj);
				if (rowIndex == null) {
					rowIndex = defaultRow;
				}
				while (rows.size() <= rowIndex) {
					rows.add(null);
				}
				Row row = rows.get(rowIndex);
				if (row == null) {
					row = new Row();
					row.setTrip(trip);
					row.setOverrideFromCave(getAsString(fromStation, "cave"));
					row.setFromStation(getAsString(fromStation, "station"));
					if (shot != null && shot.size() > 0 && toStation != null) {
						row.setOverrideToCave(getAsString(toStation, "cave"));
						row.setToStation(getAsString(toStation, "station"));
					}
					rows.set(rowIndex, row);
				}
				return row;
			};

			if (fromStation.has("splays")) {
				JsonArray splays = fromStation.getAsJsonArray("splays");
				for (int k = 0; k < splays.size(); k++) {
					JsonObject splay = splays.get(k).getAsJsonObject();
					Row row = getRow.apply(splay);
					getMeasurements(splay, row);
					// TODO: splayDepth
				}
			}

			if (fromStation != null && fromStation.has("station") &&
					shot != null && shot.size() > 0 &&
					toStation != null && toStation.has("station")) {

				JsonArray measurements = shot.getAsJsonArray("measurements");
				if (measurements != null && measurements.size() > 0) {
					// group into frontsights and backsights
					List<JsonObject> frontsights = new ArrayList<>();
					List<JsonObject> backsights = new ArrayList<>();
					for (int k = 0; k < measurements.size(); k++) {
						JsonObject measurement = measurements.get(k).getAsJsonObject();
						if ("bs".equals(getAsString(measurement, "dir"))) {
							frontsights.add(measurement);
						} else {
							backsights.add(measurement);
						}
					}
					// merge frontsights and backsights into rows
					for (int k = 0; k < Math.max(frontsights.size(), backsights.size()); k++) {
						JsonObject frontsight = k < frontsights.size() ? frontsights.get(k) : null;
						JsonObject backsight = k < backsights.size() ? backsights.get(k) : null;

						if (frontsight != null) {
							Row row = getRow.apply(frontsight);
							getMeasurements(frontsight, row);
						}
						if (backsight != null) {
							Row row = getRow.apply(backsight);
							getMeasurements(backsight, row);
						}
					}
				} else if (getAsString(shot, "dist") == "auto") {
					getRow.apply(shot); // adds row as side effect
				}

				// TODO: excludeDist
			}

			if (fromStation.has("lrud")) {
				Row lrudRow = getRow.apply(fromStation);
				JsonArray lrud = fromStation.getAsJsonArray("lrud");
				lrudRow.setLeft(getMeasurement(lrud.get(0)));
				lrudRow.setRight(getMeasurement(lrud.get(1)));
				lrudRow.setUp(getMeasurement(lrud.get(2)));
				lrudRow.setDown(getMeasurement(lrud.get(3)));
			}
			if (fromStation.has("nev")) {
				Row row = getRow.apply(fromStation);
				JsonArray nev = fromStation.getAsJsonArray("nev");
				row.setNorthing(getMeasurement(nev.get(0)));
				row.setEasting(getMeasurement(nev.get(1)));
				row.setElevation(getMeasurement(nev.get(2)));
			}
			// TODO: lrudAzm

			// TODO: nsew

			// TODO: isEntrance
			// TODO: isAboveGround
			// TODO: depth
		}
	}
}
