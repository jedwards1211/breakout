package org.breakout.model.raw;

import static org.andork.util.JavaScript.or;

import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

import org.andork.model.DefaultProperty;
import org.andork.unit.Angle;
import org.andork.unit.Length;
import org.andork.unit.Unit;

public class SurveyTrip implements Cloneable {
	public static enum LrudAssociation {
		FROM,
		TO;
	}

	protected String cave; // Cave
	protected String name; // Name
	protected String date; // Date
	protected List<String> attachedFiles; // AttachedFiles
	protected List<String> surveyors; // Surveyors
	protected Unit<Length> distanceUnit = Length.meters; // DistanceUnit
	protected Unit<Angle> angleUnit = Angle.degrees; // AngleUnit
	protected Unit<Angle> overrideFrontAzimuthUnit; // OverrideFrontAzimuthUnit
	protected Unit<Angle> overrideBackAzimuthUnit; // OverrideBackAzimuthUnit
	protected Unit<Angle> overrideFrontInclinationUnit; // OverrideFrontInclinationUnit
	protected Unit<Angle> overrideBackInclinationUnit; // OverrideBackInclinationUnit
	protected boolean backAzimuthsCorrected; // BackAzimuthsCorrected
	protected boolean backInclinationsCorrected; // BackInclinationsCorrected
	protected String declination; // Declination
	protected String distanceCorrection; // DistanceCorrection
	protected String frontAzimuthCorrection; // FrontAzimuthCorrection
	protected String frontInclinationCorrection; // FrontInclinationCorrection
	protected String backAzimuthCorrection; // BackAzimuthCorrection
	protected String backInclinationCorrection; // BackInclinationCorrection
	protected LrudAssociation lrudAssociation; // LrudAssociation
	protected String datum; // Datum
	protected String ellipsoid; // Ellipsoid
	protected String utmZone; // UtmZone

	public SurveyTrip() {
	}

	public SurveyTrip(SurveyTrip other) {
		copy(other);
	}

	public void copy(SurveyTrip other) {
		cave = other.cave;
		name = other.name;
		date = other.date;
		attachedFiles = other.attachedFiles;
		surveyors = other.surveyors;
		distanceUnit = other.distanceUnit;
		angleUnit = other.angleUnit;
		overrideFrontAzimuthUnit = other.overrideFrontAzimuthUnit;
		overrideBackAzimuthUnit = other.overrideBackAzimuthUnit;
		overrideFrontInclinationUnit = other.overrideFrontInclinationUnit;
		overrideBackInclinationUnit = other.overrideBackInclinationUnit;
		backAzimuthsCorrected = other.backAzimuthsCorrected;
		backInclinationsCorrected = other.backInclinationsCorrected;
		declination = other.declination;
		distanceCorrection = other.distanceCorrection;
		frontAzimuthCorrection = other.frontAzimuthCorrection;
		frontInclinationCorrection = other.frontInclinationCorrection;
		backAzimuthCorrection = other.backAzimuthCorrection;
		backInclinationCorrection = other.backInclinationCorrection;
		lrudAssociation = other.lrudAssociation;
		datum = other.datum;
		ellipsoid = other.ellipsoid;
		utmZone = other.utmZone;
	}

	@Override
	public SurveyTrip clone() {
		return new SurveyTrip(this);
	}

	@Override
	public int hashCode() {
		return Objects
			.hash(
				angleUnit,
				attachedFiles,
				backAzimuthCorrection,
				backAzimuthsCorrected,
				backInclinationCorrection,
				backInclinationsCorrected,
				cave,
				date,
				datum,
				declination,
				distanceCorrection,
				distanceUnit,
				ellipsoid,
				frontAzimuthCorrection,
				frontInclinationCorrection,
				lrudAssociation,
				name,
				overrideBackAzimuthUnit,
				overrideBackInclinationUnit,
				overrideFrontAzimuthUnit,
				overrideFrontInclinationUnit,
				surveyors,
				utmZone);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SurveyTrip other = (SurveyTrip) obj;
		return Objects.equals(angleUnit, other.angleUnit)
			&& Objects.equals(attachedFiles, other.attachedFiles)
			&& Objects.equals(backAzimuthCorrection, other.backAzimuthCorrection)
			&& backAzimuthsCorrected == other.backAzimuthsCorrected
			&& Objects.equals(backInclinationCorrection, other.backInclinationCorrection)
			&& backInclinationsCorrected == other.backInclinationsCorrected
			&& Objects.equals(cave, other.cave)
			&& Objects.equals(date, other.date)
			&& Objects.equals(datum, other.datum)
			&& Objects.equals(declination, other.declination)
			&& Objects.equals(distanceCorrection, other.distanceCorrection)
			&& Objects.equals(distanceUnit, other.distanceUnit)
			&& Objects.equals(ellipsoid, other.ellipsoid)
			&& Objects.equals(frontAzimuthCorrection, other.frontAzimuthCorrection)
			&& Objects.equals(frontInclinationCorrection, other.frontInclinationCorrection)
			&& lrudAssociation == other.lrudAssociation
			&& Objects.equals(name, other.name)
			&& Objects.equals(overrideBackAzimuthUnit, other.overrideBackAzimuthUnit)
			&& Objects.equals(overrideBackInclinationUnit, other.overrideBackInclinationUnit)
			&& Objects.equals(overrideFrontAzimuthUnit, other.overrideFrontAzimuthUnit)
			&& Objects.equals(overrideFrontInclinationUnit, other.overrideFrontInclinationUnit)
			&& Objects.equals(surveyors, other.surveyors)
			&& Objects.equals(utmZone, other.utmZone);
	}

	protected SurveyTrip toImmutable() {
		return this;
	}

	public Mutable toMutable() {
		return new Mutable(this);
	}

	public SurveyTrip withMutations(Consumer<Mutable> mutator) {
		Mutable mutable = toMutable();
		mutator.accept(mutable);
		return mutable.toImmutable();
	}

	public String getCave() {
		return cave;
	}

	public String getName() {
		return name;
	}

	public String getDate() {
		return date;
	}

	public List<String> getAttachedFiles() {
		return attachedFiles;
	}

	public List<String> getSurveyors() {
		return surveyors;
	}

	public Unit<Length> getDistanceUnit() {
		return distanceUnit;
	}

	public Unit<Angle> getAngleUnit() {
		return angleUnit;
	}

	public Unit<Angle> getOverrideFrontAzimuthUnit() {
		return overrideFrontAzimuthUnit;
	}

	public Unit<Angle> getOverrideBackAzimuthUnit() {
		return overrideBackAzimuthUnit;
	}

	public Unit<Angle> getOverrideFrontInclinationUnit() {
		return overrideFrontInclinationUnit;
	}

	public Unit<Angle> getOverrideBackInclinationUnit() {
		return overrideBackInclinationUnit;
	}

	public boolean areBackAzimuthsCorrected() {
		return backAzimuthsCorrected;
	}

	public boolean areBackInclinationsCorrected() {
		return backInclinationsCorrected;
	}

	public String getDeclination() {
		return declination;
	}

	public String getDistanceCorrection() {
		return distanceCorrection;
	}

	public String getFrontAzimuthCorrection() {
		return frontAzimuthCorrection;
	}

	public String getFrontInclinationCorrection() {
		return frontInclinationCorrection;
	}

	public String getBackAzimuthCorrection() {
		return backAzimuthCorrection;
	}

	public String getBackInclinationCorrection() {
		return backInclinationCorrection;
	}

	public LrudAssociation getLrudAssociation() {
		return lrudAssociation;
	}

	public String getDatum() {
		return datum;
	}

	public String getEllipsoid() {
		return ellipsoid;
	}

	public String getUtmZone() {
		return utmZone;
	}

	public Unit<Angle> getFrontAzimuthUnit() {
		return or(getOverrideFrontAzimuthUnit(), getAngleUnit());
	}

	public Unit<Angle> getBackAzimuthUnit() {
		return or(getOverrideBackAzimuthUnit(), getAngleUnit());
	}

	public Unit<Angle> getFrontInclinationUnit() {
		return or(getOverrideFrontInclinationUnit(), getAngleUnit());
	}

	public Unit<Angle> getBackInclinationUnit() {
		return or(getOverrideBackInclinationUnit(), getAngleUnit());
	}

	public SurveyTrip setCave(String cave) {
		return toMutable().setCave(cave).toImmutable();
	}

	public SurveyTrip setName(String name) {
		return toMutable().setName(name).toImmutable();
	}

	public SurveyTrip setDate(String date) {
		return toMutable().setDate(date).toImmutable();
	}

	public SurveyTrip setAttachedFiles(List<String> attachedFiles) {
		return toMutable().setAttachedFiles(attachedFiles).toImmutable();
	}

	public SurveyTrip setSurveyors(List<String> surveyors) {
		return toMutable().setSurveyors(surveyors).toImmutable();
	}

	public SurveyTrip setDistanceUnit(Unit<Length> distanceUnit) {
		return toMutable().setDistanceUnit(distanceUnit).toImmutable();
	}

	public SurveyTrip setAngleUnit(Unit<Angle> angleUnit) {
		return toMutable().setAngleUnit(angleUnit).toImmutable();
	}

	public SurveyTrip setOverrideFrontAzimuthUnit(Unit<Angle> overrideFrontAzimuthUnit) {
		return toMutable().setOverrideFrontAzimuthUnit(overrideFrontAzimuthUnit).toImmutable();
	}

	public SurveyTrip setOverrideBackAzimuthUnit(Unit<Angle> overrideBackAzimuthUnit) {
		return toMutable().setOverrideBackAzimuthUnit(overrideBackAzimuthUnit).toImmutable();
	}

	public SurveyTrip setOverrideFrontInclinationUnit(Unit<Angle> overrideFrontInclinationUnit) {
		return toMutable().setOverrideFrontInclinationUnit(overrideFrontInclinationUnit).toImmutable();
	}

	public SurveyTrip setOverrideBackInclinationUnit(Unit<Angle> overrideBackInclinationUnit) {
		return toMutable().setOverrideBackInclinationUnit(overrideBackInclinationUnit).toImmutable();
	}

	public SurveyTrip setBackAzimuthsCorrected(boolean backAzimuthsCorrected) {
		return toMutable().setBackAzimuthsCorrected(backAzimuthsCorrected).toImmutable();
	}

	public SurveyTrip setBackInclinationsCorrected(boolean backInclinationsCorrected) {
		return toMutable().setBackInclinationsCorrected(backInclinationsCorrected).toImmutable();
	}

	public SurveyTrip setDeclination(String declination) {
		return toMutable().setDeclination(declination).toImmutable();
	}

	public SurveyTrip setDistanceCorrection(String distanceCorrection) {
		return toMutable().setDistanceCorrection(distanceCorrection).toImmutable();
	}

	public SurveyTrip setFrontAzimuthCorrection(String frontAzimuthCorrection) {
		return toMutable().setFrontAzimuthCorrection(frontAzimuthCorrection).toImmutable();
	}

	public SurveyTrip setFrontInclinationCorrection(String frontInclinationCorrection) {
		return toMutable().setFrontInclinationCorrection(frontInclinationCorrection).toImmutable();
	}

	public SurveyTrip setBackAzimuthCorrection(String backAzimuthCorrection) {
		return toMutable().setBackAzimuthCorrection(backAzimuthCorrection).toImmutable();
	}

	public SurveyTrip setBackInclinationCorrection(String backInclinationCorrection) {
		return toMutable().setBackInclinationCorrection(backInclinationCorrection).toImmutable();
	}

	public SurveyTrip setLrudAssociation(LrudAssociation lrudAssociation) {
		return toMutable().setLrudAssociation(lrudAssociation).toImmutable();
	}

	public SurveyTrip setDatum(String datum) {
		return toMutable().setDatum(datum).toImmutable();
	}

	public SurveyTrip setEllipsoid(String ellipsoid) {
		return toMutable().setEllipsoid(ellipsoid).toImmutable();
	}

	public SurveyTrip setUtmZone(String utmZone) {
		return toMutable().setUtmZone(utmZone).toImmutable();
	}

	public static class Mutable extends SurveyTrip {
		private SurveyTrip original;

		public Mutable() {
		}

		public Mutable(SurveyTrip original) {
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

		public SurveyTrip toImmutable() {
			if (original == null)
				original = new SurveyTrip(this);
			return original;
		}

		public Mutable setCave(String cave) {
			if (original != null && !Objects.equals(original.cave, cave)) {
				detach();
			}
			this.cave = cave;
			return this;
		}

		public Mutable setName(String name) {
			if (original != null && !Objects.equals(original.name, name)) {
				detach();
			}
			this.name = name;
			return this;
		}

		public Mutable setDate(String date) {
			if (original != null && !Objects.equals(original.date, date)) {
				detach();
			}
			this.date = date;
			return this;
		}

		public Mutable setAttachedFiles(List<String> attachedFiles) {
			if (original != null && !Objects.equals(original.attachedFiles, attachedFiles)) {
				detach();
			}
			this.attachedFiles = attachedFiles;
			return this;
		}

		public Mutable setSurveyors(List<String> surveyors) {
			if (original != null && !Objects.equals(original.surveyors, surveyors)) {
				detach();
			}
			this.surveyors = surveyors;
			return this;
		}

		public Mutable setDistanceUnit(Unit<Length> distanceUnit) {
			if (original != null && !Objects.equals(original.distanceUnit, distanceUnit)) {
				detach();
			}
			this.distanceUnit = distanceUnit;
			return this;
		}

		public Mutable setAngleUnit(Unit<Angle> angleUnit) {
			if (original != null && !Objects.equals(original.angleUnit, angleUnit)) {
				detach();
			}
			this.angleUnit = angleUnit;
			return this;
		}

		public Mutable setOverrideFrontAzimuthUnit(Unit<Angle> overrideFrontAzimuthUnit) {
			if (original != null && !Objects.equals(original.overrideFrontAzimuthUnit, overrideFrontAzimuthUnit)) {
				detach();
			}
			this.overrideFrontAzimuthUnit = overrideFrontAzimuthUnit;
			return this;
		}

		public Mutable setOverrideBackAzimuthUnit(Unit<Angle> overrideBackAzimuthUnit) {
			if (original != null && !Objects.equals(original.overrideBackAzimuthUnit, overrideBackAzimuthUnit)) {
				detach();
			}
			this.overrideBackAzimuthUnit = overrideBackAzimuthUnit;
			return this;
		}

		public Mutable setOverrideFrontInclinationUnit(Unit<Angle> overrideFrontInclinationUnit) {
			if (original != null
				&& !Objects.equals(original.overrideFrontInclinationUnit, overrideFrontInclinationUnit)) {
				detach();
			}
			this.overrideFrontInclinationUnit = overrideFrontInclinationUnit;
			return this;
		}

		public Mutable setOverrideBackInclinationUnit(Unit<Angle> overrideBackInclinationUnit) {
			if (original != null
				&& !Objects.equals(original.overrideBackInclinationUnit, overrideBackInclinationUnit)) {
				detach();
			}
			this.overrideBackInclinationUnit = overrideBackInclinationUnit;
			return this;
		}

		public Mutable setBackAzimuthsCorrected(boolean backAzimuthsCorrected) {
			if (original != null && original.backAzimuthsCorrected != backAzimuthsCorrected) {
				detach();
			}
			this.backAzimuthsCorrected = backAzimuthsCorrected;
			return this;
		}

		public Mutable setBackInclinationsCorrected(boolean backInclinationsCorrected) {
			if (original != null && original.backInclinationsCorrected != backInclinationsCorrected) {
				detach();
			}
			this.backInclinationsCorrected = backInclinationsCorrected;
			return this;
		}

		public Mutable setDeclination(String declination) {
			if (original != null && !Objects.equals(original.declination, declination)) {
				detach();
			}
			this.declination = declination;
			return this;
		}

		public Mutable setDistanceCorrection(String distanceCorrection) {
			if (original != null && !Objects.equals(original.distanceCorrection, distanceCorrection)) {
				detach();
			}
			this.distanceCorrection = distanceCorrection;
			return this;
		}

		public Mutable setFrontAzimuthCorrection(String frontAzimuthCorrection) {
			if (original != null && !Objects.equals(original.frontAzimuthCorrection, frontAzimuthCorrection)) {
				detach();
			}
			this.frontAzimuthCorrection = frontAzimuthCorrection;
			return this;
		}

		public Mutable setFrontInclinationCorrection(String frontInclinationCorrection) {
			if (original != null && !Objects.equals(original.frontInclinationCorrection, frontInclinationCorrection)) {
				detach();
			}
			this.frontInclinationCorrection = frontInclinationCorrection;
			return this;
		}

		public Mutable setBackAzimuthCorrection(String backAzimuthCorrection) {
			if (original != null && !Objects.equals(original.backAzimuthCorrection, backAzimuthCorrection)) {
				detach();
			}
			this.backAzimuthCorrection = backAzimuthCorrection;
			return this;
		}

		public Mutable setBackInclinationCorrection(String backInclinationCorrection) {
			if (original != null && !Objects.equals(original.backInclinationCorrection, backInclinationCorrection)) {
				detach();
			}
			this.backInclinationCorrection = backInclinationCorrection;
			return this;
		}

		public Mutable setLrudAssociation(LrudAssociation lrudAssociation) {
			if (original != null && !Objects.equals(original.lrudAssociation, lrudAssociation)) {
				detach();
			}
			this.lrudAssociation = lrudAssociation;
			return this;
		}

		public Mutable setDatum(String datum) {
			if (original != null && !Objects.equals(original.datum, datum)) {
				detach();
			}
			this.datum = datum;
			return this;
		}

		public Mutable setEllipsoid(String ellipsoid) {
			if (original != null && !Objects.equals(original.ellipsoid, ellipsoid)) {
				detach();
			}
			this.ellipsoid = ellipsoid;
			return this;
		}

		public Mutable setUtmZone(String utmZone) {
			if (original != null && !Objects.equals(original.utmZone, utmZone)) {
				detach();
			}
			this.utmZone = utmZone;
			return this;
		}
	}

	public static final class Properties {
		private static <V> DefaultProperty<SurveyTrip, V> create(
			String name,
			Class<? super V> valueClass,
			Function<SurveyTrip, ? extends V> getter,
			BiFunction<SurveyTrip, V, SurveyTrip> setter) {
			return new DefaultProperty<SurveyTrip, V>(name, valueClass, getter, setter);
		}

		public static final DefaultProperty<SurveyTrip, String> cave =
			create("cave", String.class, r -> r.getCave(), (m, v) -> m.setCave(v));

		public static final DefaultProperty<SurveyTrip, String> name =
			create("name", String.class, r -> r.getName(), (m, v) -> m.setName(v));

		public static final DefaultProperty<SurveyTrip, String> date =
			create("date", String.class, r -> r.getDate(), (m, v) -> m.setDate(v));

		public static final DefaultProperty<SurveyTrip, List<String>> attachedFiles =
			create("attachedFiles", List.class, r -> r.getAttachedFiles(), (m, v) -> m.setAttachedFiles(v));

		public static final DefaultProperty<SurveyTrip, List<String>> surveyors =
			create("surveyors", List.class, r -> r.getSurveyors(), (m, v) -> m.setSurveyors(v));

		public static final DefaultProperty<SurveyTrip, Unit<Length>> distanceUnit =
			create("distanceUnit", Unit.class, r -> r.getDistanceUnit(), (m, v) -> m.setDistanceUnit(v));

		public static final DefaultProperty<SurveyTrip, Unit<Angle>> angleUnit =
			create("angleUnit", Unit.class, r -> r.getAngleUnit(), (m, v) -> m.setAngleUnit(v));

		public static final DefaultProperty<SurveyTrip, Unit<Angle>> overrideFrontAzimuthUnit =
			create(
				"overrideFrontAzimuthUnit",
				Unit.class,
				r -> r.getOverrideFrontAzimuthUnit(),
				(m, v) -> m.setOverrideFrontAzimuthUnit(v));

		public static final DefaultProperty<SurveyTrip, Unit<Angle>> overrideBackAzimuthUnit =
			create(
				"overrideBackAzimuthUnit",
				Unit.class,
				r -> r.getOverrideBackAzimuthUnit(),
				(m, v) -> m.setOverrideBackAzimuthUnit(v));

		public static final DefaultProperty<SurveyTrip, Unit<Angle>> overrideFrontInclinationUnit =
			create(
				"overrideFrontInclinationUnit",
				Unit.class,
				r -> r.getOverrideFrontInclinationUnit(),
				(m, v) -> m.setOverrideFrontInclinationUnit(v));

		public static final DefaultProperty<SurveyTrip, Unit<Angle>> overrideBackInclinationUnit =
			create(
				"overrideBackInclinationUnit",
				Unit.class,
				r -> r.getOverrideBackInclinationUnit(),
				(m, v) -> m.setOverrideBackInclinationUnit(v));

		public static final DefaultProperty<SurveyTrip, Boolean> backAzimuthsCorrected =
			create(
				"backAzimuthsCorrected",
				Boolean.class,
				r -> r.areBackAzimuthsCorrected(),
				(m, v) -> m.setBackAzimuthsCorrected(v));
		public static final DefaultProperty<SurveyTrip, Boolean> backInclinationsCorrected =

			create(
				"backInclinationsCorrected",
				Boolean.class,
				r -> r.areBackInclinationsCorrected(),
				(m, v) -> m.setBackInclinationsCorrected(v));

		public static final DefaultProperty<SurveyTrip, String> declination =
			create("declination", String.class, r -> r.getDeclination(), (m, v) -> m.setDeclination(v));

		public static final DefaultProperty<SurveyTrip, String> distanceCorrection =
			create(
				"distanceCorrection",
				String.class,
				r -> r.getDistanceCorrection(),
				(m, v) -> m.setDistanceCorrection(v));

		public static final DefaultProperty<SurveyTrip, String> frontAzimuthCorrection =
			create(
				"frontAzimuthCorrection",
				String.class,
				r -> r.getFrontAzimuthCorrection(),
				(m, v) -> m.setFrontAzimuthCorrection(v));

		public static final DefaultProperty<SurveyTrip, String> frontInclinationCorrection =
			create(
				"frontInclinationCorrection",
				String.class,
				r -> r.getFrontInclinationCorrection(),
				(m, v) -> m.setFrontInclinationCorrection(v));

		public static final DefaultProperty<SurveyTrip, String> backAzimuthCorrection =
			create(
				"backAzimuthCorrection",
				String.class,
				r -> r.getBackAzimuthCorrection(),
				(m, v) -> m.setBackAzimuthCorrection(v));

		public static final DefaultProperty<SurveyTrip, String> backInclinationCorrection =
			create(
				"backInclinationCorrection",
				String.class,
				r -> r.getBackInclinationCorrection(),
				(m, v) -> m.setBackInclinationCorrection(v));

		public static final DefaultProperty<SurveyTrip, LrudAssociation> lrudAssociation =
			create(
				"lrudAssociation",
				LrudAssociation.class,
				r -> r.getLrudAssociation(),
				(m, v) -> m.setLrudAssociation(v));

		public static final DefaultProperty<SurveyTrip, String> datum =
			create("datum", String.class, r -> r.getDatum(), (m, v) -> m.setDatum(v));

		public static final DefaultProperty<SurveyTrip, String> ellipsoid =
			create("ellipsoid", String.class, r -> r.getEllipsoid(), (m, v) -> m.setEllipsoid(v));

		public static final DefaultProperty<SurveyTrip, String> utmZone =
			create("utmZone", String.class, r -> r.getUtmZone(), (m, v) -> m.setUtmZone(v));
	}
}
