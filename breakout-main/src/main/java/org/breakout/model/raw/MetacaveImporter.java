package org.breakout.model.raw;

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
import org.andork.unit.UnitType;
import org.andork.unit.UnitizedDouble;
import org.andork.unit.UnitizedNumber;

import com.github.krukow.clj_lang.PersistentVector;
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

	private static boolean getAsBoolean(JsonObject obj, String property, boolean defaultValue) {
		if (!obj.has(property)) {
			return defaultValue;
		}
		JsonElement elem = obj.get(property);
		return elem.isJsonNull() ? defaultValue : obj.get(property).getAsBoolean();
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

	private static String intern(String s) {
		return s != null ? s.intern() : s;
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

	private static void getMeasurements(JsonObject obj, MutableSurveyRow row) {
		row.setDistance(getAsString(obj, "dist"));
		if ("bs".equals(getAsString(obj, "dir"))) {
			row.setBackAzimuth(getAsString(obj, "azm"));
			row.setBackInclination(getAsString(obj, "inc"));
		}
		else {
			row.setFrontAzimuth(getAsString(obj, "azm"));
			row.setFrontInclination(getAsString(obj, "inc"));
		}
	}

	private <T extends UnitType<T>> UnitizedNumber<T> parseMeasurement(JsonArray a, Map<String, Unit<T>> units) {
		if (a == null || (a.size() % 2) != 0) {
			return null;
		}
		UnitizedDouble<T> result = null;
		for (int i = 0; i < a.size(); i += 2) {
			double value = Double.parseDouble(a.get(i).getAsString());
			Unit<T> unit = units.get(a.get(i + 1).getAsString());
			if (unit == null) {
				return null;
			}
			UnitizedDouble<T> addend = new UnitizedDouble<>(value, unit);
			result = result == null ? addend : result.add(addend);
		}
		return result;
	}

	private UnitizedNumber<Length> parseLength(JsonArray a) {
		return parseMeasurement(a, metacaveLengthUnits);
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

	private final List<MutableSurveyRow> rows = new ArrayList<>();
	private final List<SurveyLead> leads = new ArrayList<>();
	private final IdentityHashMap<JsonObject, SurveyTrip> trips = new IdentityHashMap<>();

	public List<SurveyRow> getRows() {
		List<SurveyRow> result = new ArrayList<SurveyRow>();
		MutableSurveyRow lastRow = null;
		for (MutableSurveyRow row : rows) {
			if (row == null) {
				final MutableSurveyRow finalLastRow = lastRow;
				row = new MutableSurveyRow().setTrip(finalLastRow == null ? null : finalLastRow.getTrip());
			}
			result.add(row.toImmutable());
			lastRow = row;
		}
		return result;
	}

	public List<SurveyLead> getLeads() {
		return new ArrayList<>(leads);
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
			String caveName = e.getKey();
			JsonObject cave = e.getValue().getAsJsonObject();
			importCave(cave, caveName);
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

	public void importMetacave(Reader reader) {
		JsonObject obj = new JsonParser().parse(new JsonReader(reader)).getAsJsonObject();
		importMetacave(obj);
	}

	public void importCave(JsonObject cave, String caveName) {
		JsonArray trips = cave.getAsJsonArray("trips");
		if (trips != null) {
			for (int i = 0; i < trips.size(); i++) {
				importTrip(trips.get(i).getAsJsonObject(), caveName);
			}
		}
		JsonArray leads = cave.getAsJsonArray("leads");
		if (leads != null) {
			for (int i = 0; i < leads.size(); i++) {
				importLead(leads.get(i).getAsJsonObject(), caveName);
			}
		}
		JsonArray fixedStations = cave.getAsJsonArray("fixedStations");
		if (fixedStations != null) {
			for (int i = 0; i < fixedStations.size(); i++) {
				importFixedStationGroup(fixedStations.get(i).getAsJsonObject(), caveName);
			}
		}
	}

	public SurveyTrip importTrip(JsonObject obj, String caveName) {
		SurveyTrip trip = importTripHeader(obj, caveName);
		importTripSurvey(trip, obj.getAsJsonArray("survey"));
		return trip;
	}

	public SurveyTrip importTripHeader(JsonObject obj, String caveName) {
		SurveyTrip trip = trips.get(obj);
		if (trip == null) {
			MutableSurveyTrip t = new MutableSurveyTrip();
			t.setCave(caveName);
			if (obj.has("name")) {
				t.setName(obj.get("name").getAsString());
			}
			if (obj.has("date")) {
				t.setDate(obj.get("date").getAsString());
			}
			if (obj.has("surveyors")) {
				List<String> surveyors = new ArrayList<>();
				obj.get("surveyors").getAsJsonObject().entrySet().forEach(e -> surveyors.add(e.getKey()));
				t.setSurveyors(surveyors);
			}
			if (obj.has("surveyNotesFile")) {
				t.setAttachedFiles(PersistentVector.create(obj.get("surveyNotesFile").getAsString()));
			}
			if (obj.has("distUnit")) {
				t.setDistanceUnit(metacaveLengthUnits.get(obj.get("distUnit").getAsString()));
			}
			if (obj.has("angleUnit")) {
				t.setAngleUnit(metacaveAngleUnits.get(obj.get("angleUnit").getAsString()));
			}
			t.setBackAzimuthsCorrected(getAsBoolean(obj, "azmBacksightsCorrected", false));
			t.setBackInclinationsCorrected(getAsBoolean(obj, "incBacksightsCorrected", false));
			t.setDeclination(getAsString(obj, "declination"));
			t.setOverrideFrontAzimuthUnit(getAngleUnit(obj, "azmFsUnit"));
			t.setOverrideBackAzimuthUnit(getAngleUnit(obj, "azmBsUnit"));
			t.setOverrideFrontInclinationUnit(getAngleUnit(obj, "incFsUnit"));
			t.setOverrideBackInclinationUnit(getAngleUnit(obj, "incBsUnit"));
			t.setDistanceCorrection(getAsString(obj, "distCorrection"));
			t.setFrontAzimuthCorrection(getAsString(obj, "azmFsCorrection"));
			t.setBackAzimuthCorrection(getAsString(obj, "azmBsCorrection"));
			t.setFrontInclinationCorrection(getAsString(obj, "incFsCorrection"));
			t.setBackInclinationCorrection(getAsString(obj, "incBsCorrection"));
			trip = t.toImmutable();
			trips.put(obj, trip);
		}
		return trip;
	}

	public void importTripSurvey(SurveyTrip trip, JsonArray survey) {
		if (survey == null || survey.size() == 0) {
			return;
		}

		for (int i = 0; i < survey.size(); i += 2) {
			final JsonObject fromStation = survey.get(i).getAsJsonObject();
			final JsonObject shot = i + 1 < survey.size() ? survey.get(i + 1).getAsJsonObject() : null;
			final JsonObject toStation = i + 2 < survey.size() ? survey.get(i + 2).getAsJsonObject() : null;

			final int defaultRow = rows.size();

			Function<JsonObject, MutableSurveyRow> getRow = obj -> {
				Integer rowIndex = getRowIndex(obj);
				if (rowIndex == null) {
					rowIndex = defaultRow;
				}
				while (rows.size() <= rowIndex) {
					rows.add(null);
				}
				MutableSurveyRow row = rows.get(rowIndex);
				if (row == null) {
					row = new MutableSurveyRow();
					row.setTrip(trip);
					row.setOverrideFromCave(intern(getAsString(fromStation, "cave")));
					row.setFromStation(intern(getAsString(fromStation, "station")));
					if (shot != null && shot.size() > 0 && toStation != null) {
						row.setOverrideToCave(intern(getAsString(toStation, "cave")));
						row.setToStation(intern(getAsString(toStation, "station")));
						row.setComment(getAsString(shot, "comment"));
						if (shot.has("surveyNotesFile")) {
							row
								.setOverrideAttachedFiles(
									PersistentVector.create(intern(getAsString(shot, "surveyNotesFile"))));
						}
					}
					else {
						row.setComment(getAsString(fromStation, "comment"));
					}
					rows.set(rowIndex, row);
				}
				return row;
			};

			if (fromStation.has("splays")) {
				JsonArray splays = fromStation.getAsJsonArray("splays");
				for (int k = 0; k < splays.size(); k++) {
					JsonObject splay = splays.get(k).getAsJsonObject();
					MutableSurveyRow row = getRow.apply(splay);
					getMeasurements(splay, row);
					// TODO: splayDepth
				}
			}

			if (fromStation.has("station")
				&& shot != null
				&& shot.size() > 0
				&& toStation != null
				&& toStation.has("station")) {

				JsonArray measurements = shot.getAsJsonArray("measurements");
				if (measurements != null && measurements.size() > 0) {
					// group into frontsights and backsights
					List<JsonObject> frontsights = new ArrayList<>();
					List<JsonObject> backsights = new ArrayList<>();
					for (int k = 0; k < measurements.size(); k++) {
						JsonObject measurement = measurements.get(k).getAsJsonObject();
						if ("bs".equals(getAsString(measurement, "dir"))) {
							frontsights.add(measurement);
						}
						else {
							backsights.add(measurement);
						}
					}
					// merge frontsights and backsights into rows
					for (int k = 0; k < Math.max(frontsights.size(), backsights.size()); k++) {
						JsonObject frontsight = k < frontsights.size() ? frontsights.get(k) : null;
						JsonObject backsight = k < backsights.size() ? backsights.get(k) : null;

						if (frontsight != null) {
							MutableSurveyRow row = getRow.apply(frontsight);
							getMeasurements(frontsight, row);
						}
						if (backsight != null) {
							MutableSurveyRow row = getRow.apply(backsight);
							getMeasurements(backsight, row);
						}
					}
				}
				else if (getAsString(shot, "dist") == "auto") {
					getRow.apply(shot); // adds row as side effect
				}

				boolean excludeDistance = getAsBoolean(shot, "excludeDist", false);
				if (excludeDistance)
					getRow.apply(shot).setExcludeDistance(true);

				boolean excludeFromPlot = getAsBoolean(shot, "excludeFromPlot", false);
				if (excludeFromPlot)
					getRow.apply(shot).setExcludeFromPlotting(true);
			}

			if (fromStation.has("lrud")) {
				MutableSurveyRow lrudRow = getRow.apply(fromStation);
				JsonArray lrud = fromStation.getAsJsonArray("lrud");
				lrudRow.setLeft(getMeasurement(lrud.get(0)));
				lrudRow.setRight(getMeasurement(lrud.get(1)));
				lrudRow.setUp(getMeasurement(lrud.get(2)));
				lrudRow.setDown(getMeasurement(lrud.get(3)));
			}
			if (fromStation.has("nev")) {
				MutableSurveyRow row = getRow.apply(fromStation);
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

	public SurveyLead importLead(JsonObject obj, String caveName) {
		MutableSurveyLead lead = new MutableSurveyLead();
		lead.setCave(caveName);
		lead.setStation(getAsString(obj, "station"));
		lead.setDescription(getAsString(obj, "description"));
		if (obj.has("width") && !obj.get("width").isJsonNull()) {
			lead.setRawWidth(obj.get("width").getAsJsonArray());
			lead.setWidth(parseLength(obj.get("width").getAsJsonArray()));
		}
		if (obj.has("height") && !obj.get("height").isJsonNull()) {
			lead.setRawHeight(obj.get("height").getAsJsonArray());
			lead.setHeight(parseLength(obj.get("height").getAsJsonArray()));
		}
		lead.setDone(obj.has("done") && obj.get("done").getAsBoolean());
		SurveyLead result = lead.toImmutable();
		leads.add(result);
		return result;
	}

	public SurveyTrip importFixedStationGroup(JsonObject obj, String caveName) {
		SurveyTrip trip = importFixedStationGroupHeader(obj, caveName);
		importFixedStations(trip, obj.getAsJsonObject("stations"));
		return trip;
	}

	public SurveyTrip importFixedStationGroupHeader(JsonObject obj, String caveName) {
		SurveyTrip trip = trips.get(obj);
		if (trip == null) {
			MutableSurveyTrip t = new MutableSurveyTrip();
			t.setCave(caveName);
			if (obj.has("distUnit")) {
				t.setDistanceUnit(metacaveLengthUnits.get(obj.get("distUnit").getAsString()));
			}
			t.setBackAzimuthsCorrected(true);
			t.setBackInclinationsCorrected(true);
			t.setDatum(getAsString(obj, "datum"));
			t.setEllipsoid(getAsString(obj, "ellipsoid"));
			t.setUtmZone(getAsString(obj, "utmZone"));
			trip = t.toImmutable();
			trips.put(obj, trip);
		}
		return trip;
	}

	public void importFixedStations(SurveyTrip trip, JsonObject stations) {
		for (Map.Entry<String, JsonElement> station : stations.entrySet()) {
			MutableSurveyRow row = new MutableSurveyRow();
			row.setTrip(trip);
			row.setFromStation(station.getKey());
			JsonObject props = station.getValue().getAsJsonObject();
			if (props.has("lat")) {
				row.setLatitude(getAsString(props, "lat"));
			}
			if (props.has("long")) {
				row.setLongitude(getAsString(props, "long"));
			}
			if (props.has("north")) {
				row.setNorthing(getMeasurement(props.get("north")));
			}
			if (props.has("east")) {
				row.setEasting(getMeasurement(props.get("east")));
			}
			if (props.has("elev")) {
				row.setElevation(getMeasurement(props.get("elev")));
			}
			rows.add(row);
		}
	}
}
