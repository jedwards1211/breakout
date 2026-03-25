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
	protected String cave;
	protected String station;
	protected String description;
	protected JsonArray rawWidth;
	protected JsonArray rawHeight;
	protected UnitizedNumber<Length> width;
	protected UnitizedNumber<Length> height;
	protected boolean isDone;

	public SurveyLead() {
	}

	public SurveyLead(SurveyLead other) {
		copy(other);
	}

	public void copy(SurveyLead other) {
		cave = other.cave;
		station = other.station;
		description = other.description;
		rawWidth = other.rawWidth;
		rawHeight = other.rawHeight;
		width = other.width;
		isDone = other.isDone;
	}

	@Override
	public SurveyLead clone() {
		return new SurveyLead(this);
	}

	@Override
	public int hashCode() {
		return Objects.hash(cave, description, height, isDone, rawHeight, rawWidth, sizeFormat, station, width);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof SurveyLead))
			return false;
		SurveyLead other = (SurveyLead) obj;
		return Objects.equals(cave, other.cave)
			&& Objects.equals(description, other.description)
			&& Objects.equals(height, other.height)
			&& isDone == other.isDone
			&& Objects.equals(rawHeight, other.rawHeight)
			&& Objects.equals(rawWidth, other.rawWidth)
			&& Objects.equals(sizeFormat, other.sizeFormat)
			&& Objects.equals(station, other.station)
			&& Objects.equals(width, other.width);
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
		return cave;
	}

	/**
	 * the name of the nearest station
	 */
	public String getStation() {
		return station;
	}

	/**
	 * the description of the lead
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * the width of the lead from metacave
	 */
	public JsonArray getRawWidth() {
		return rawWidth;
	}

	/**
	 * the height of the lead from metacave
	 */
	public JsonArray getRawHeight() {
		return rawHeight;
	}

	/**
	 * the width of the lead
	 */
	public UnitizedNumber<Length> getWidth() {
		return width;
	}

	/**
	 * the height of the lead
	 */
	public UnitizedNumber<Length> getHeight() {
		return height;
	}

	/**
	 * whether the lead is done or not
	 */
	public boolean isDone() {
		return isDone;
	}

	public SurveyLead setCave(String cave) {
		return this.toMutable().setCave(cave).toImmutable();
	}

	public SurveyLead setStation(String station) {
		return this.toMutable().setStation(station).toImmutable();
	}

	public SurveyLead setDescription(String description) {
		return this.toMutable().setDescription(description).toImmutable();
	}

	public SurveyLead setRawWidth(JsonArray rawWidth) {
		return this.toMutable().setRawWidth(rawWidth).toImmutable();
	}

	public SurveyLead setRawHeight(JsonArray rawHeight) {
		return this.toMutable().setRawHeight(rawHeight).toImmutable();
	}

	public SurveyLead setWidth(UnitizedNumber<Length> width) {
		return this.toMutable().setWidth(width).toImmutable();
	}

	public SurveyLead setHeight(UnitizedNumber<Length> height) {
		return this.toMutable().setHeight(height).toImmutable();
	}

	public SurveyLead setDone(boolean isDone) {
		return this.toMutable().setDone(isDone).toImmutable();
	}

	private final DecimalFormat sizeFormat = new DecimalFormat("0.#");

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
			super();
			this.original = original.toImmutable();
		}

		private void detach() {
			copy(original);
			this.original = null;
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
			if (original != null && !Objects.equals(original.cave, cave)) {
				detach();
			}
			this.cave = cave;
			return this;
		}

		public Mutable setStation(String station) {
			if (original != null && !Objects.equals(original.station, station)) {
				detach();
			}
			this.station = station;
			return this;
		}

		public Mutable setDescription(String description) {
			if (original != null && !Objects.equals(original.description, description)) {
				detach();
			}
			this.description = description;
			return this;
		}

		public Mutable setRawWidth(JsonArray rawWidth) {
			if (original != null && !Objects.equals(original.rawWidth, rawWidth)) {
				detach();
			}
			this.rawWidth = rawWidth;
			return this;
		}

		public Mutable setRawHeight(JsonArray rawHeight) {
			if (original != null && !Objects.equals(original.rawHeight, rawHeight)) {
				detach();
			}
			this.rawHeight = rawHeight;
			return this;
		}

		public Mutable setWidth(UnitizedNumber<Length> width) {
			if (original != null && !Objects.equals(original.width, width)) {
				detach();
			}
			this.width = width;
			return this;
		}

		public Mutable setHeight(UnitizedNumber<Length> height) {
			if (original != null && !Objects.equals(original.height, height)) {
				detach();
			}
			this.height = height;
			return this;
		}

		public Mutable setDone(boolean isDone) {
			if (original != null && original.isDone != isDone) {
				detach();
			}
			this.isDone = isDone;
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
