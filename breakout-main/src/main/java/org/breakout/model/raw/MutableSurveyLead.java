/**
 * Generated from {@code SurveyLead.record.js} by java-record-generator on 8/25/2019, 1:57:31 AM.
 * {@link https://github.com/jedwards1211/java-record-generator#readme}
 */

package org.breakout.model.raw;

import com.google.gson.JsonArray;
import org.andork.unit.UnitizedNumber;
import org.andork.unit.Length;
import com.github.krukow.clj_ds.TransientMap;
import com.github.krukow.clj_lang.PersistentHashMap;
import java.util.Objects;
import java.util.function.Function;

/**
 * The mutable version of {@link SurveyLead}.
 */
public final class MutableSurveyLead {
	private volatile PersistentHashMap<String, Object> persisted;
	private final TransientMap<String, Object> data;

	@SuppressWarnings("unchecked")
	MutableSurveyLead(PersistentHashMap<String, Object> data) {
		persisted = data;
		this.data = persisted.asTransient();
	}

	public MutableSurveyLead() {
		this(SurveyLead.initialData);
	}

	boolean dataEquals(PersistentHashMap<String, Object> prevData) {
		return persisted == prevData;
	}

	PersistentHashMap<String, Object> persist() {
		if (persisted == null) {
			persisted = (PersistentHashMap<String, Object>) data.persist();
		}
		return persisted;
	}

	public SurveyLead toImmutable() {
		return new SurveyLead(persist());
	}

	@SuppressWarnings("unchecked")
	public <T> T get(String key) {
		return (T) persist().get(key);
	}

	@SuppressWarnings("unchecked")
	public <T> T get(String key, T defaultValue) {
		T result = (T) persist().get(key);
		return result != null ? result : defaultValue;
	}

	private static boolean equals(Object a, Object b) {
		if (a instanceof Number || b instanceof Number ||
			a instanceof String || b instanceof String) {
			return Objects.equals(a, b);
		}
		return a == b;
	}

	public MutableSurveyLead set(String key, Object value) {
		if (persisted != null && equals(value, persisted.get(key))) {
			return this;
		}
		persisted = null;
		data.plus(key, value);
		return this;
	}

	public <T> MutableSurveyLead update(String key, Function<? super T, ? extends T> updater) {
		@SuppressWarnings("unchecked")
		T oldValue = (T) persist().get(key);
		T newValue = updater.apply(oldValue);
		if (equals(oldValue, newValue)) {
			return this;
		}
		data.plus(key, newValue);
		return this;
	}

	public MutableSurveyLead delete(String key) {
		if (persisted != null && !persisted.containsKey(key)) {
			return this;
		}
		persisted = null;
		data.minus(key);
		return this;
	}

	
	
	/**
	 * @return name of cave the lead is in.
	 */
	public String getCave() {
		return get(SurveyLead.cave);
	}
	
	/**
	 * @return the name of the nearest station.
	 */
	public String getStation() {
		return get(SurveyLead.station);
	}
	
	/**
	 * @return the description of the lead.
	 */
	public String getDescription() {
		return get(SurveyLead.description);
	}
	
	/**
	 * @return the width of the lead from metacave.
	 */
	public JsonArray getRawWidth() {
		return get(SurveyLead.rawWidth);
	}
	
	/**
	 * @return the height of the lead from metacave.
	 */
	public JsonArray getRawHeight() {
		return get(SurveyLead.rawHeight);
	}
	
	/**
	 * @return the width of the lead.
	 */
	public UnitizedNumber<Length> getWidth() {
		return get(SurveyLead.width);
	}
	
	/**
	 * @return the height of the lead.
	 */
	public UnitizedNumber<Length> getHeight() {
		return get(SurveyLead.height);
	}
	
	/**
	 * @return whether the lead is done or not.
	 */
	public Boolean isDone() {
		return get(SurveyLead.done, false);
	}
	
	
	/**
	 * Sets name of cave the lead is in.
	 *
	 * @param cave - the new value for name of cave the lead is in
	 *
	 * @return this {@code SurveyLead} if {@code cave} is unchanged, or a copy with the new {@code cave}.
	 */
	public MutableSurveyLead setCave(String cave) {
		return set(SurveyLead.cave, cave);
	}
	
	/**
	 * Sets the name of the nearest station.
	 *
	 * @param station - the new value for the name of the nearest station
	 *
	 * @return this {@code SurveyLead} if {@code station} is unchanged, or a copy with the new {@code station}.
	 */
	public MutableSurveyLead setStation(String station) {
		return set(SurveyLead.station, station);
	}
	
	/**
	 * Sets the description of the lead.
	 *
	 * @param description - the new value for the description of the lead
	 *
	 * @return this {@code SurveyLead} if {@code description} is unchanged, or a copy with the new {@code description}.
	 */
	public MutableSurveyLead setDescription(String description) {
		return set(SurveyLead.description, description);
	}
	
	/**
	 * Sets the width of the lead from metacave.
	 *
	 * @param rawWidth - the new value for the width of the lead from metacave
	 *
	 * @return this {@code SurveyLead} if {@code rawWidth} is unchanged, or a copy with the new {@code rawWidth}.
	 */
	public MutableSurveyLead setRawWidth(JsonArray rawWidth) {
		return set(SurveyLead.rawWidth, rawWidth);
	}
	
	/**
	 * Sets the height of the lead from metacave.
	 *
	 * @param rawHeight - the new value for the height of the lead from metacave
	 *
	 * @return this {@code SurveyLead} if {@code rawHeight} is unchanged, or a copy with the new {@code rawHeight}.
	 */
	public MutableSurveyLead setRawHeight(JsonArray rawHeight) {
		return set(SurveyLead.rawHeight, rawHeight);
	}
	
	/**
	 * Sets the width of the lead.
	 *
	 * @param width - the new value for the width of the lead
	 *
	 * @return this {@code SurveyLead} if {@code width} is unchanged, or a copy with the new {@code width}.
	 */
	public MutableSurveyLead setWidth(UnitizedNumber<Length> width) {
		return set(SurveyLead.width, width);
	}
	
	/**
	 * Sets the height of the lead.
	 *
	 * @param height - the new value for the height of the lead
	 *
	 * @return this {@code SurveyLead} if {@code height} is unchanged, or a copy with the new {@code height}.
	 */
	public MutableSurveyLead setHeight(UnitizedNumber<Length> height) {
		return set(SurveyLead.height, height);
	}
	
	/**
	 * Sets whether the lead is done or not.
	 *
	 * @param done - the new value for whether the lead is done or not
	 *
	 * @return this {@code SurveyLead} if {@code done} is unchanged, or a copy with the new {@code done}.
	 */
	public MutableSurveyLead setDone(Boolean done) {
		return set(SurveyLead.done, done);
	}
	
	
	/**
	 * Updates name of cave the lead is in.
	 *
	 * @param updater - {@code Function} that takes the current value of {@code cave} and returns the new value for {@code cave}.
	 *
	 * @return this {@code MutableSurveyLead} if {@code cave} is unchanged, or a copy with the updated {@code cave}.
	 */
	public MutableSurveyLead updateCave(Function<String, String> updater) {
		return update(SurveyLead.cave, updater);
	}
	
	/**
	 * Updates the name of the nearest station.
	 *
	 * @param updater - {@code Function} that takes the current value of {@code station} and returns the new value for {@code station}.
	 *
	 * @return this {@code MutableSurveyLead} if {@code station} is unchanged, or a copy with the updated {@code station}.
	 */
	public MutableSurveyLead updateStation(Function<String, String> updater) {
		return update(SurveyLead.station, updater);
	}
	
	/**
	 * Updates the description of the lead.
	 *
	 * @param updater - {@code Function} that takes the current value of {@code description} and returns the new value for {@code description}.
	 *
	 * @return this {@code MutableSurveyLead} if {@code description} is unchanged, or a copy with the updated {@code description}.
	 */
	public MutableSurveyLead updateDescription(Function<String, String> updater) {
		return update(SurveyLead.description, updater);
	}
	
	/**
	 * Updates the width of the lead from metacave.
	 *
	 * @param updater - {@code Function} that takes the current value of {@code rawWidth} and returns the new value for {@code rawWidth}.
	 *
	 * @return this {@code MutableSurveyLead} if {@code rawWidth} is unchanged, or a copy with the updated {@code rawWidth}.
	 */
	public MutableSurveyLead updateRawWidth(Function<JsonArray, JsonArray> updater) {
		return update(SurveyLead.rawWidth, updater);
	}
	
	/**
	 * Updates the height of the lead from metacave.
	 *
	 * @param updater - {@code Function} that takes the current value of {@code rawHeight} and returns the new value for {@code rawHeight}.
	 *
	 * @return this {@code MutableSurveyLead} if {@code rawHeight} is unchanged, or a copy with the updated {@code rawHeight}.
	 */
	public MutableSurveyLead updateRawHeight(Function<JsonArray, JsonArray> updater) {
		return update(SurveyLead.rawHeight, updater);
	}
	
	/**
	 * Updates the width of the lead.
	 *
	 * @param updater - {@code Function} that takes the current value of {@code width} and returns the new value for {@code width}.
	 *
	 * @return this {@code MutableSurveyLead} if {@code width} is unchanged, or a copy with the updated {@code width}.
	 */
	public MutableSurveyLead updateWidth(Function<UnitizedNumber<Length>, UnitizedNumber<Length>> updater) {
		return update(SurveyLead.width, updater);
	}
	
	/**
	 * Updates the height of the lead.
	 *
	 * @param updater - {@code Function} that takes the current value of {@code height} and returns the new value for {@code height}.
	 *
	 * @return this {@code MutableSurveyLead} if {@code height} is unchanged, or a copy with the updated {@code height}.
	 */
	public MutableSurveyLead updateHeight(Function<UnitizedNumber<Length>, UnitizedNumber<Length>> updater) {
		return update(SurveyLead.height, updater);
	}
	
	/**
	 * Updates whether the lead is done or not.
	 *
	 * @param updater - {@code Function} that takes the current value of {@code done} and returns the new value for {@code done}.
	 *
	 * @return this {@code MutableSurveyLead} if {@code done} is unchanged, or a copy with the updated {@code done}.
	 */
	public MutableSurveyLead updateDone(Function<Boolean, Boolean> updater) {
		return update(SurveyLead.done, updater);
	}
	
	
}
