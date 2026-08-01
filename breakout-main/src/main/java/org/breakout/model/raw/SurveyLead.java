package org.breakout.model.raw;

import java.text.DecimalFormat;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

import org.andork.model.DefaultProperty;
import org.andork.unit.Length;
import org.andork.unit.Unit;
import org.andork.unit.UnitizedNumber;

import com.google.gson.JsonArray;

public class SurveyLead implements Cloneable {
	private static final DecimalFormat sizeFormat = new DecimalFormat("0.#");

	protected static class Data implements Cloneable {
		protected String cave;
		protected String station;
		protected String description;
		protected JsonArray rawWidth;
		protected JsonArray rawHeight;
		protected UnitizedNumber<Length> width;
		protected UnitizedNumber<Length> height;
		protected boolean isDone;

		public void copy(Data other) {
			cave = other.cave;
			station = other.station;
			description = other.description;
			rawWidth = other.rawWidth;
			rawHeight = other.rawHeight;
			width = other.width;
			isDone = other.isDone;
		}

		@Override
		public Data clone() {
			Data clone = new Data();
			clone.copy(this);
			return clone;
		}

		@Override
		public int hashCode() {
			return Objects.hash(cave, description, height, isDone, rawHeight, rawWidth, station, width);
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (!(obj instanceof Data))
				return false;
			Data other = (Data) obj;
			return Objects.equals(cave, other.cave)
				&& Objects.equals(description, other.description)
				&& Objects.equals(height, other.height)
				&& isDone == other.isDone
				&& Objects.equals(rawHeight, other.rawHeight)
				&& Objects.equals(rawWidth, other.rawWidth)
				&& Objects.equals(station, other.station)
				&& Objects.equals(width, other.width);
		}
	}
	
	protected Data data;

	public SurveyLead() {
		this.data = new Data();
	}

	public SurveyLead(SurveyLead other) {
		this.data = other.data;
	}
	
	@Override
	public SurveyLead clone() {
		return new SurveyLead(this);
	}

	public int hashCode() {
		return data.hashCode();
	}

	public boolean equals(Object obj) {
		return data.equals(obj);
	}

	protected SurveyLead toImmutable() {
		return this;
	}

	public Mutable toMutable() {
		return new Mutable(this);
	}

	public SurveyLead withMutations(Consumer<Mutable> mutator) {
		Mutable mutable = toMutable();
		mutator.accept(mutable);
		return mutable.toImmutable();
	}

	/**
	 * name of cave the lead is in
	 */
	public String getCave() {
		return data.cave;
	}

	/**
	 * the name of the nearest station
	 */
	public String getStation() {
		return data.station;
	}

	/**
	 * the description of the lead
	 */
	public String getDescription() {
		return data.description;
	}

	/**
	 * the width of the lead from metacave
	 */
	public JsonArray getRawWidth() {
		return data.rawWidth;
	}

	/**
	 * the height of the lead from metacave
	 */
	public JsonArray getRawHeight() {
		return data.rawHeight;
	}

	/**
	 * the width of the lead
	 */
	public UnitizedNumber<Length> getWidth() {
		return data.width;
	}

	/**
	 * the height of the lead
	 */
	public UnitizedNumber<Length> getHeight() {
		return data.height;
	}

	/**
	 * whether the lead is done or not
	 */
	public boolean isDone() {
		return data.isDone;
	}

	public SurveyLead setCave(String cave) {
		return toMutable().setCave(cave).toImmutable();
	}

	public SurveyLead setStation(String station) {
		return toMutable().setStation(station).toImmutable();
	}

	public SurveyLead setDescription(String description) {
		return toMutable().setDescription(description).toImmutable();
	}

	public SurveyLead setRawWidth(JsonArray rawWidth) {
		return toMutable().setRawWidth(rawWidth).toImmutable();
	}

	public SurveyLead setRawHeight(JsonArray rawHeight) {
		return toMutable().setRawHeight(rawHeight).toImmutable();
	}

	public SurveyLead setWidth(UnitizedNumber<Length> width) {
		return toMutable().setWidth(width).toImmutable();
	}

	public SurveyLead setHeight(UnitizedNumber<Length> height) {
		return toMutable().setHeight(height).toImmutable();
	}

	public SurveyLead setDone(boolean isDone) {
		return toMutable().setDone(isDone).toImmutable();
	}


	public String describeSize(Unit<Length> unit) {
		UnitizedNumber<Length> width = getWidth();
		UnitizedNumber<Length> height = getHeight();
		StringBuilder builder = new StringBuilder();
		if (width != null) {
			builder.append(sizeFormat.format(width.doubleValue(unit))).append('w');
		}
		if (height != null) {
			if (builder.length() > 0)
				builder.append(' ');
			builder.append(sizeFormat.format(height.doubleValue(unit))).append('h');
		}
		return builder.length() > 0 ? builder.toString() : null;
	}

	public static class Mutable extends SurveyLead {
		private SurveyLead original;

		public Mutable() {
		}

		public Mutable(SurveyLead original) {
			super(original);
			this.original = original.toImmutable();
		}

		private void detach() {
			data = data.clone();
			original = null;
		}

		public Mutable clone() {
			return new Mutable(this);
		}

		public SurveyLead toImmutable() {
			if (original == null)
				original = new SurveyLead(this);
			return original;
		}

		public Mutable setCave(String cave) {
			if (original != null && !Objects.equals(data.cave, cave)) {
				detach();
			}
			data.cave = cave;
			return this;
		}

		public Mutable setStation(String station) {
			if (original != null && !Objects.equals(data.station, station)) {
				detach();
			}
			data.station = station;
			return this;
		}

		public Mutable setDescription(String description) {
			if (original != null && !Objects.equals(data.description, description)) {
				detach();
			}
			data.description = description;
			return this;
		}

		public Mutable setRawWidth(JsonArray rawWidth) {
			if (original != null && !Objects.equals(data.rawWidth, rawWidth)) {
				detach();
			}
			data.rawWidth = rawWidth;
			return this;
		}

		public Mutable setRawHeight(JsonArray rawHeight) {
			if (original != null && !Objects.equals(data.rawHeight, rawHeight)) {
				detach();
			}
			data.rawHeight = rawHeight;
			return this;
		}

		public Mutable setWidth(UnitizedNumber<Length> width) {
			if (original != null && !Objects.equals(data.width, width)) {
				detach();
			}
			data.width = width;
			return this;
		}

		public Mutable setHeight(UnitizedNumber<Length> height) {
			if (original != null && !Objects.equals(data.height, height)) {
				detach();
			}
			data.height = height;
			return this;
		}

		public Mutable setDone(boolean isDone) {
			if (original != null && data.isDone != isDone) {
				detach();
			}
			data.isDone = isDone;
			return this;
		}
	}

	public static final class Properties {
		private static <V> DefaultProperty<SurveyLead, V> create(
			String name,
			Class<? super V> valueClass,
			Function<SurveyLead, ? extends V> getter,
			BiFunction<SurveyLead, V, SurveyLead> setter) {
			return new DefaultProperty<SurveyLead, V>(name, valueClass, getter, setter);
		}

		/**
		 * name of cave the lead is in
		 */
		public static final DefaultProperty<SurveyLead, String> cave =
			create("cave", String.class, r -> r.getCave(), (m, v) -> m.setCave(v));

		/**
		 * the name of the nearest station
		 */
		public static final DefaultProperty<SurveyLead, String> station =
			create("station", String.class, r -> r.getStation(), (m, v) -> m.setStation(v));

		/**
		 * the description of the lead
		 */
		public static final DefaultProperty<SurveyLead, String> description =
			create("description", String.class, r -> r.getDescription(), (m, v) -> m.setDescription(v));

		/**
		 * the width of the lead from metacave
		 */
		public static final DefaultProperty<SurveyLead, JsonArray> rawWidth =
			create("rawWidth", JsonArray.class, r -> r.getRawWidth(), (m, v) -> m.setRawWidth(v));

		/**
		 * the height of the lead from metacave
		 */
		public static final DefaultProperty<SurveyLead, JsonArray> rawHeight =
			create("rawHeight", JsonArray.class, r -> r.getRawHeight(), (m, v) -> m.setRawHeight(v));

		/**
		 * the width of the lead
		 */
		public static final DefaultProperty<SurveyLead, UnitizedNumber<Length>> width =
			create("width", UnitizedNumber.class, r -> r.getWidth(), (m, v) -> m.setWidth(v));

		/**
		 * the height of the lead
		 */
		public static final DefaultProperty<SurveyLead, UnitizedNumber<Length>> height =
			create("height", UnitizedNumber.class, r -> r.getHeight(), (m, v) -> m.setHeight(v));

		/**
		 * whether the lead is done or not
		 */
		public static final DefaultProperty<SurveyLead, Boolean> done =
			create("done", Boolean.class, r -> r.isDone(), (m, v) -> m.setDone(v));
	}
}
