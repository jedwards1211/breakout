/*******************************************************************************
 * Breakout Cave Survey Visualizer
 *
 * Copyright (C) 2014 James Edwards
 *
 * jedwards8 at fastmail dot fm
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *******************************************************************************/
package org.breakout.model;

import java.util.Calendar;
import java.util.Date;

import org.andork.date.DateUtils;
import org.andork.unit.Area;
import org.andork.unit.Length;
import org.andork.unit.UnitType;
import org.breakout.model.calc.CalcCrossSection;
import org.breakout.model.calc.CalcShot;
import org.breakout.model.calc.CalcStation;

public enum ColorParam {
	DEPTH("Depth", true) {
		@Override
		public UnitType<?> getUnitType() {
			return Length.type;
		}
	},
	DISTANCE_ALONG_SHOTS("Distance (Along Shots)", true) {
		@Override
		public double calcTraversalDistance(CalcShot shot) {
			return shot.distance;
		}

		@Override
		public boolean isTraversalMetric() {
			return true;
		}

		@Override
		public UnitType<?> getUnitType() {
			return Length.type;
		}
	},
	PASSAGE_WIDTH("Passage Width", false) {
		@Override
		public float calcStationParam(CalcShot shot, CalcStation station) {
			CalcCrossSection xSection = shot.getCrossSectionAt(station);
			if (xSection == null) {
				return 0;
			}
			double[] m = xSection.measurements;
			if (xSection.type == CrossSectionType.NSEW) {
				return (float) Math.max(m[0] + m[1], m[2] + m[3]);
			}
			else {
				return (float) (m[0] + m[1]);
			}
		}

		@Override
		public boolean isStationMetric() {
			return true;
		}

		@Override
		public UnitType<?> getUnitType() {
			return Length.type;
		}
	},
	PASSAGE_HEIGHT("Passage Height", false) {
		@Override
		public float calcStationParam(CalcShot shot, CalcStation station) {
			CalcCrossSection xSection = shot.getCrossSectionAt(station);
			if (xSection == null) {
				return 0;
			}
			if (xSection.type == CrossSectionType.NSEW) {
				return (float) shot.distance;
			}
			else {
				return (float) (xSection.measurements[2] + xSection.measurements[3]);
			}
		}

		@Override
		public boolean isStationMetric() {
			return true;
		}

		@Override
		public UnitType<?> getUnitType() {
			return Length.type;
		}
	},

	PASSAGE_MIN("Min(Passage Width, Height)", false) {
		@Override
		public float calcStationParam(CalcShot shot, CalcStation station) {
			return Math
				.min(PASSAGE_WIDTH.calcStationParam(shot, station), PASSAGE_HEIGHT.calcStationParam(shot, station));
		}

		@Override
		public boolean isStationMetric() {
			return true;
		}

		@Override
		public UnitType<?> getUnitType() {
			return Length.type;
		}
	},
	PASSAGE_AREA("Passage Area", false) {
		@Override
		public float calcStationParam(CalcShot shot, CalcStation station) {
			return PASSAGE_WIDTH.calcStationParam(shot, station) * PASSAGE_HEIGHT.calcStationParam(shot, station) / 2;
		}

		@Override
		public boolean isStationMetric() {
			return true;
		}

		@Override
		public UnitType<?> getUnitType() {
			return Area.type;
		}
	},
	SHOT_LENGTH("CalcShot Length", false) {
		@Override
		public float calcStationParam(CalcShot shot, CalcStation station) {
			return (float) shot.distance;
		}

		@Override
		public boolean isStationMetric() {
			return true;
		}

		@Override
		public UnitType<?> getUnitType() {
			return Length.type;
		}
	},
	DATE("Date (days since 1800)", false) {
		@Override
		public float calcStationParam(CalcShot shot, CalcStation station) {
			return shot.date;
		}

		@Override
		public boolean isStationMetric() {
			return true;
		}
	},
	HAS_SURVEY_NOTES("Has Survey Notes", false) {
		@Override
		public float calcStationParam(CalcShot shot, CalcStation station) {
			return shot.hasSurveyNotes() ? 1f : 0f;
		}

		@Override
		public boolean isStationMetric() {
			return true;
		}
	};

	private static final Calendar cal1800;

	static {
		cal1800 = Calendar.getInstance();
		cal1800.set(Calendar.YEAR, 1800);
		cal1800.set(Calendar.DAY_OF_YEAR, 1);
		cal1800.set(Calendar.HOUR_OF_DAY, 0);
		cal1800.set(Calendar.MINUTE, 0);
		cal1800.set(Calendar.SECOND, 0);
		cal1800.set(Calendar.MILLISECOND, 0);
	}

	private final String displayName;
	private final boolean loIsBright;

	private ColorParam(String displayName, boolean loIsBright) {
		this.displayName = displayName;
		this.loIsBright = loIsBright;
	}

	public float calcStationParam(CalcShot shot, CalcStation station) {
		throw new UnsupportedOperationException();
	}

	public double calcTraversalDistance(CalcShot shot) {
		throw new UnsupportedOperationException();
	}

	public String getDisplayName() {
		return displayName;
	}

	public boolean isLoBright() {
		return loIsBright;
	}

	public boolean isStationMetric() {
		return false;
	}

	public boolean isTraversalMetric() {
		return false;
	}

	public UnitType<?> getUnitType() {
		return null;
	}

	@Override
	public String toString() {
		return displayName;
	}

	static Calendar calendar = Calendar.getInstance();

	public static float calcDaysSince1800(Date date) {
		if (date == null) {
			return Float.NaN;
		}
		calendar.setTime(date);
		return DateUtils.daysSinceTheJesus(calendar) - DateUtils.daysSinceTheJesus(cal1800);
	}

	public static Date calcDateFromDaysSince1800(Float daysSince1800) {
		if (daysSince1800 == null || Float.isNaN(daysSince1800))
			return null;
		calendar.set(1800, 0, 1, 0, 0, 0);
		calendar.add(Calendar.DATE, (int) Math.floor(daysSince1800));
		calendar.add(Calendar.SECOND, Math.round((daysSince1800 % 1) * 86400));
		return calendar.getTime();
	}
}
