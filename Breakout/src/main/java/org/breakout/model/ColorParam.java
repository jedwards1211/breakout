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

import org.andork.date.DateUtils;

public enum ColorParam {
	DEPTH("Depth", true), DISTANCE_ALONG_SHOTS("Distance (Along Shots)", true) {
		@Override
		public double calcTraversalDistance(Shot shot) {
			return shot.dist;
		}

		@Override
		public boolean isTraversalMetric() {
			return true;
		}
	},
	PASSAGE_WIDTH("Passage Width", false) {
		@Override
		public float calcStationParam(Shot shot, Station station) {
			CrossSection xSection = station == shot.from ? shot.fromXsection : shot.toXsection;
			if (xSection.type == CrossSectionType.NSEW) {
				return Math.max(xSection.dist[0] + xSection.dist[1], xSection.dist[2] + xSection.dist[3]);
			} else {
				return xSection.dist[0] + xSection.dist[1];
			}
		}

		@Override
		public boolean isStationMetric() {
			return true;
		}
	},
	PASSAGE_HEIGHT("Passage Height", false) {
		@Override
		public float calcStationParam(Shot shot, Station station) {
			CrossSection xSection = station == shot.from ? shot.fromXsection : shot.toXsection;
			if (xSection.type == CrossSectionType.NSEW) {
				return (float) shot.dist;
			} else {
				return xSection.dist[2] + xSection.dist[3];
			}
		}

		@Override
		public boolean isStationMetric() {
			return true;
		}
	},

	PASSAGE_MIN("Min(Passage Width, Height)", false) {
		@Override
		public float calcStationParam(Shot shot, Station station) {
			return Math.min(PASSAGE_WIDTH.calcStationParam(shot, station),
					PASSAGE_HEIGHT.calcStationParam(shot, station));
		}

		@Override
		public boolean isStationMetric() {
			return true;
		}
	},
	PASSAGE_AREA("Passage Area", false) {
		@Override
		public float calcStationParam(Shot shot, Station station) {
			return PASSAGE_WIDTH.calcStationParam(shot, station) + PASSAGE_HEIGHT.calcStationParam(shot, station);
		}

		@Override
		public boolean isStationMetric() {
			return true;
		}
	},
	SHOT_LENGTH("Shot Length", false) {
		@Override
		public float calcStationParam(Shot shot, Station station) {
			return (float) shot.dist;
		}

		@Override
		public boolean isStationMetric() {
			return true;
		}
	},
	DATE("Date (days since 1800)", false) {
		Calendar calendar = Calendar.getInstance();

		@Override
		public float calcStationParam(Shot shot, Station station) {
			if (shot.date == null) {
				return Float.NaN;
			}
			calendar.setTime(shot.date);
			return DateUtils.daysSinceTheJesus(calendar) - DateUtils.daysSinceTheJesus(cal1800);
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

	public float calcStationParam(Shot shot, Station station) {
		throw new UnsupportedOperationException();
	}

	public double calcTraversalDistance(Shot shot) {
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

	@Override
	public String toString() {
		return displayName;
	}
}
