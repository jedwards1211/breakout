/**
 * Generated from {@code SurveyLead.record.js} by java-record-generator on 8/25/2019, 1:57:31 AM.
 * {@link https://github.com/jedwards1211/java-record-generator#readme}
 */

package org.breakout.model.raw;

import com.google.gson.JsonArray;
import java.text.DecimalFormat;
import org.andork.unit.Unit;
import org.andork.unit.UnitizedNumber;
import org.andork.unit.Length;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.Objects;
import com.github.krukow.clj_lang.PersistentHashMap;
import org.andork.model.DefaultProperty;
import java.util.function.BiConsumer;

/**
 *
 */
public final class SurveyLead {
	
	/**
	 * Key for name of cave the lead is in.
	 */
	public static final String cave = "cave";
	
	/**
	 * Key for the name of the nearest station.
	 */
	public static final String station = "station";
	
	/**
	 * Key for the description of the lead.
	 */
	public static final String description = "description";
	
	/**
	 * Key for the width of the lead from metacave.
	 */
	public static final String rawWidth = "rawWidth";
	
	/**
	 * Key for the height of the lead from metacave.
	 */
	public static final String rawHeight = "rawHeight";
	
	/**
	 * Key for the width of the lead.
	 */
	public static final String width = "width";
	
	/**
	 * Key for the height of the lead.
	 */
	public static final String height = "height";
	
	/**
	 * Key for whether the lead is done or not.
	 */
	public static final String done = "done";
	
	
	static final PersistentHashMap<String, Object> initialData = PersistentHashMap.emptyMap();
	
	
	public static final class Properties {
		public static <V> DefaultProperty<SurveyLead, V> create(
				String name, Class<? super V> valueClass,
				Function<? super SurveyLead, ? extends V> getter,
				BiConsumer<MutableSurveyLead, ? super V> setter) {
			return new DefaultProperty<SurveyLead, V>(
				name, valueClass, getter, (m, v) -> {
					return m.withMutations(m2 -> setter.accept(m2, v));
				}
			);
		}

		
		/**
		 * name of cave the lead is in
		 */
		public static final DefaultProperty<SurveyLead, String> cave = create(
			"cave", String.class,
			r -> r.get(SurveyLead.cave),
			(m, v) -> m.set(SurveyLead.cave, v)
		);
		

		/**
		 * the name of the nearest station
		 */
		public static final DefaultProperty<SurveyLead, String> station = create(
			"station", String.class,
			r -> r.get(SurveyLead.station),
			(m, v) -> m.set(SurveyLead.station, v)
		);
		

		/**
		 * the description of the lead
		 */
		public static final DefaultProperty<SurveyLead, String> description = create(
			"description", String.class,
			r -> r.get(SurveyLead.description),
			(m, v) -> m.set(SurveyLead.description, v)
		);
		

		/**
		 * the width of the lead from metacave
		 */
		public static final DefaultProperty<SurveyLead, JsonArray> rawWidth = create(
			"rawWidth", JsonArray.class,
			r -> r.get(SurveyLead.rawWidth),
			(m, v) -> m.set(SurveyLead.rawWidth, v)
		);
		

		/**
		 * the height of the lead from metacave
		 */
		public static final DefaultProperty<SurveyLead, JsonArray> rawHeight = create(
			"rawHeight", JsonArray.class,
			r -> r.get(SurveyLead.rawHeight),
			(m, v) -> m.set(SurveyLead.rawHeight, v)
		);
		

		/**
		 * the width of the lead
		 */
		public static final DefaultProperty<SurveyLead, UnitizedNumber<Length>> width = create(
			"width", UnitizedNumber.class,
			r -> r.get(SurveyLead.width),
			(m, v) -> m.set(SurveyLead.width, v)
		);
		

		/**
		 * the height of the lead
		 */
		public static final DefaultProperty<SurveyLead, UnitizedNumber<Length>> height = create(
			"height", UnitizedNumber.class,
			r -> r.get(SurveyLead.height),
			(m, v) -> m.set(SurveyLead.height, v)
		);
		

		/**
		 * whether the lead is done or not
		 */
		public static final DefaultProperty<SurveyLead, Boolean> done = create(
			"done", Boolean.class,
			r -> r.get(SurveyLead.done),
			(m, v) -> m.set(SurveyLead.done, v)
		);
		
	}
	
	

	private final PersistentHashMap<String, Object> data;

	SurveyLead(PersistentHashMap<String, Object> data) {
		this.data = data;
	}

	public SurveyLead() {
		this(initialData);
	}

	public MutableSurveyLead toMutable() {
		return new MutableSurveyLead(data);
	}

	public SurveyLead withMutations(Consumer<MutableSurveyLead> mutator) {
		MutableSurveyLead mutable = toMutable();
		mutator.accept(mutable);
		return mutable.dataEquals(data) ? this : mutable.toImmutable();
	}

	@SuppressWarnings("unchecked")
	public <T> T get(String key) {
		return (T) data.get(key);
	}

	@SuppressWarnings("unchecked")
	public <T> T get(String key, T defaultValue) {
		T result = (T) data.get(key);
		return result != null ? result : defaultValue;
	}

	public SurveyLead set(String key, Object newValue) {
		return withMutations(m -> m.set(key, newValue));
	}

	public <T> SurveyLead update(String key, Function<? super T, ? extends T> updater) {
		return set(key, updater.apply(get(key)));
	}

	@Override
	public int hashCode() {
		return data.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (obj instanceof SurveyLead) {
			return ((SurveyLead) obj).data.equals(data);
		}
		if (obj instanceof MutableSurveyLead) {
			return ((MutableSurveyLead) obj).persist().equals(data);
		}
		return false;
	}

	
	/**
	 * @return name of cave the lead is in.
	 */
	public String getCave() {
		return get(cave);
	}
	
	/**
	 * @return the name of the nearest station.
	 */
	public String getStation() {
		return get(station);
	}
	
	/**
	 * @return the description of the lead.
	 */
	public String getDescription() {
		return get(description);
	}
	
	/**
	 * @return the width of the lead from metacave.
	 */
	public JsonArray getRawWidth() {
		return get(rawWidth);
	}
	
	/**
	 * @return the height of the lead from metacave.
	 */
	public JsonArray getRawHeight() {
		return get(rawHeight);
	}
	
	/**
	 * @return the width of the lead.
	 */
	public UnitizedNumber<Length> getWidth() {
		return get(width);
	}
	
	/**
	 * @return the height of the lead.
	 */
	public UnitizedNumber<Length> getHeight() {
		return get(height);
	}
	
	/**
	 * @return whether the lead is done or not.
	 */
	public Boolean isDone() {
		return get(done, false);
	}
	
	
	/**
	 * Sets name of cave the lead is in.
	 *
	 * @param cave - the new value for name of cave the lead is in
	 *
	 * @return this {@code SurveyLead} if {@code cave} is unchanged, or a copy with the new {@code cave}.
	 */
	public SurveyLead setCave(String cave) {
		return set(SurveyLead.cave, cave);
	}
	
	/**
	 * Sets the name of the nearest station.
	 *
	 * @param station - the new value for the name of the nearest station
	 *
	 * @return this {@code SurveyLead} if {@code station} is unchanged, or a copy with the new {@code station}.
	 */
	public SurveyLead setStation(String station) {
		return set(SurveyLead.station, station);
	}
	
	/**
	 * Sets the description of the lead.
	 *
	 * @param description - the new value for the description of the lead
	 *
	 * @return this {@code SurveyLead} if {@code description} is unchanged, or a copy with the new {@code description}.
	 */
	public SurveyLead setDescription(String description) {
		return set(SurveyLead.description, description);
	}
	
	/**
	 * Sets the width of the lead from metacave.
	 *
	 * @param rawWidth - the new value for the width of the lead from metacave
	 *
	 * @return this {@code SurveyLead} if {@code rawWidth} is unchanged, or a copy with the new {@code rawWidth}.
	 */
	public SurveyLead setRawWidth(JsonArray rawWidth) {
		return set(SurveyLead.rawWidth, rawWidth);
	}
	
	/**
	 * Sets the height of the lead from metacave.
	 *
	 * @param rawHeight - the new value for the height of the lead from metacave
	 *
	 * @return this {@code SurveyLead} if {@code rawHeight} is unchanged, or a copy with the new {@code rawHeight}.
	 */
	public SurveyLead setRawHeight(JsonArray rawHeight) {
		return set(SurveyLead.rawHeight, rawHeight);
	}
	
	/**
	 * Sets the width of the lead.
	 *
	 * @param width - the new value for the width of the lead
	 *
	 * @return this {@code SurveyLead} if {@code width} is unchanged, or a copy with the new {@code width}.
	 */
	public SurveyLead setWidth(UnitizedNumber<Length> width) {
		return set(SurveyLead.width, width);
	}
	
	/**
	 * Sets the height of the lead.
	 *
	 * @param height - the new value for the height of the lead
	 *
	 * @return this {@code SurveyLead} if {@code height} is unchanged, or a copy with the new {@code height}.
	 */
	public SurveyLead setHeight(UnitizedNumber<Length> height) {
		return set(SurveyLead.height, height);
	}
	
	/**
	 * Sets whether the lead is done or not.
	 *
	 * @param done - the new value for whether the lead is done or not
	 *
	 * @return this {@code SurveyLead} if {@code done} is unchanged, or a copy with the new {@code done}.
	 */
	public SurveyLead setDone(Boolean done) {
		return set(SurveyLead.done, done);
	}
	
	
	/**
	 * Updates name of cave the lead is in.
	 *
	 * @param updater - {@code Function} that takes the current value of {@code cave} and returns the new value for {@code cave}.
	 *
	 * @return this {@code SurveyLead} if {@code cave} is unchanged, or a copy with the updated {@code cave}.
	 */
	public SurveyLead updateCave(Function<String, String> updater) {
		return update(cave, updater);
	}
	
	/**
	 * Updates the name of the nearest station.
	 *
	 * @param updater - {@code Function} that takes the current value of {@code station} and returns the new value for {@code station}.
	 *
	 * @return this {@code SurveyLead} if {@code station} is unchanged, or a copy with the updated {@code station}.
	 */
	public SurveyLead updateStation(Function<String, String> updater) {
		return update(station, updater);
	}
	
	/**
	 * Updates the description of the lead.
	 *
	 * @param updater - {@code Function} that takes the current value of {@code description} and returns the new value for {@code description}.
	 *
	 * @return this {@code SurveyLead} if {@code description} is unchanged, or a copy with the updated {@code description}.
	 */
	public SurveyLead updateDescription(Function<String, String> updater) {
		return update(description, updater);
	}
	
	/**
	 * Updates the width of the lead from metacave.
	 *
	 * @param updater - {@code Function} that takes the current value of {@code rawWidth} and returns the new value for {@code rawWidth}.
	 *
	 * @return this {@code SurveyLead} if {@code rawWidth} is unchanged, or a copy with the updated {@code rawWidth}.
	 */
	public SurveyLead updateRawWidth(Function<JsonArray, JsonArray> updater) {
		return update(rawWidth, updater);
	}
	
	/**
	 * Updates the height of the lead from metacave.
	 *
	 * @param updater - {@code Function} that takes the current value of {@code rawHeight} and returns the new value for {@code rawHeight}.
	 *
	 * @return this {@code SurveyLead} if {@code rawHeight} is unchanged, or a copy with the updated {@code rawHeight}.
	 */
	public SurveyLead updateRawHeight(Function<JsonArray, JsonArray> updater) {
		return update(rawHeight, updater);
	}
	
	/**
	 * Updates the width of the lead.
	 *
	 * @param updater - {@code Function} that takes the current value of {@code width} and returns the new value for {@code width}.
	 *
	 * @return this {@code SurveyLead} if {@code width} is unchanged, or a copy with the updated {@code width}.
	 */
	public SurveyLead updateWidth(Function<UnitizedNumber<Length>, UnitizedNumber<Length>> updater) {
		return update(width, updater);
	}
	
	/**
	 * Updates the height of the lead.
	 *
	 * @param updater - {@code Function} that takes the current value of {@code height} and returns the new value for {@code height}.
	 *
	 * @return this {@code SurveyLead} if {@code height} is unchanged, or a copy with the updated {@code height}.
	 */
	public SurveyLead updateHeight(Function<UnitizedNumber<Length>, UnitizedNumber<Length>> updater) {
		return update(height, updater);
	}
	
	/**
	 * Updates whether the lead is done or not.
	 *
	 * @param updater - {@code Function} that takes the current value of {@code done} and returns the new value for {@code done}.
	 *
	 * @return this {@code SurveyLead} if {@code done} is unchanged, or a copy with the updated {@code done}.
	 */
	public SurveyLead updateDone(Function<Boolean, Boolean> updater) {
		return update(done, updater);
	}
	
	
		private final DecimalFormat sizeFormat = new DecimalFormat("0.#");
	
	public String describeSize(Unit<Length> unit) {
		UnitizedNumber<Length> width = getWidth();
		UnitizedNumber<Length> height = getHeight();
		StringBuilder builder = new StringBuilder();
		if (width != null) {
			builder.append(sizeFormat.format(width.doubleValue(unit)))
				.append('w');
		}
		if (height != null) {
			if (builder.length() > 0) builder.append(' ');
			builder.append(sizeFormat.format(height.doubleValue(unit)))
				.append('h');
		}
		return builder.length() > 0 ? builder.toString() : null;
	}

}
