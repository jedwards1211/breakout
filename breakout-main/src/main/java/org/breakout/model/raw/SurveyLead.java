/**
 * Generated from {@code SurveyLead.record.js} by java-record-generator on 2019-3-16 16:23:05.
 * {@link https://github.com/jedwards1211/java-record-generator#readme}
 */
 
package org.breakout.model.raw;

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
	 * Key for the width of the lead.
	 */
	public static final String width = "width";
	
	/**
	 * Key for the height of the lead.
	 */
	public static final String height = "height";
	
	
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
		 * the width of the lead
		 */
		public static final DefaultProperty<SurveyLead, String> width = create(
			"width", String.class,
			r -> r.get(SurveyLead.width),
			(m, v) -> m.set(SurveyLead.width, v)
		);
		

		/**
		 * the height of the lead
		 */
		public static final DefaultProperty<SurveyLead, String> height = create(
			"height", String.class,
			r -> r.get(SurveyLead.height),
			(m, v) -> m.set(SurveyLead.height, v)
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
	 * @return the width of the lead.
	 */
	public String getWidth() {
		return get(width);
	}
	
	/**
	 * @return the height of the lead.
	 */
	public String getHeight() {
		return get(height);
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
	 * Sets the width of the lead.
	 *
	 * @param width - the new value for the width of the lead
	 * 
	 * @return this {@code SurveyLead} if {@code width} is unchanged, or a copy with the new {@code width}.
	 */
	public SurveyLead setWidth(String width) {
		return set(SurveyLead.width, width);
	}
	
	/**
	 * Sets the height of the lead.
	 *
	 * @param height - the new value for the height of the lead
	 * 
	 * @return this {@code SurveyLead} if {@code height} is unchanged, or a copy with the new {@code height}.
	 */
	public SurveyLead setHeight(String height) {
		return set(SurveyLead.height, height);
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
	 * Updates the width of the lead.
	 *
	 * @param updater - {@code Function} that takes the current value of {@code width} and returns the new value for {@code width}.
	 * 
	 * @return this {@code SurveyLead} if {@code width} is unchanged, or a copy with the updated {@code width}.
	 */
	public SurveyLead updateWidth(Function<String, String> updater) {
		return update(width, updater);
	}
	
	/**
	 * Updates the height of the lead.
	 *
	 * @param updater - {@code Function} that takes the current value of {@code height} and returns the new value for {@code height}.
	 * 
	 * @return this {@code SurveyLead} if {@code height} is unchanged, or a copy with the updated {@code height}.
	 */
	public SurveyLead updateHeight(Function<String, String> updater) {
		return update(height, updater);
	}
	
	
}
