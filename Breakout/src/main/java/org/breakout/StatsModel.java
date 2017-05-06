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
package org.breakout;

import org.andork.q.QObject;
import org.andork.q.QSpec;
import org.andork.unit.Length;
import org.andork.unit.UnitizedNumber;

public class StatsModel extends QSpec<StatsModel> {
	public static class MinAvgMax extends QSpec<MinAvgMax> {
		public static Attribute<UnitizedNumber<Length>> min = newAttribute(UnitizedNumber.class, "min");
		public static Attribute<UnitizedNumber<Length>> avg = newAttribute(UnitizedNumber.class, "avg");
		public static Attribute<UnitizedNumber<Length>> max = newAttribute(UnitizedNumber.class, "max");

		public static final MinAvgMax spec = new MinAvgMax();

		private MinAvgMax() {

		}
	}

	public static Attribute<Integer> numSelected = newAttribute(Integer.class, "numSelected");
	public static Attribute<UnitizedNumber<Length>> totalDistance = newAttribute(UnitizedNumber.class, "totalDistance");
	public static Attribute<QObject<MinAvgMax>> distStats = newAttribute(QObject.class, "distStats");
	public static Attribute<QObject<MinAvgMax>> northStats = newAttribute(QObject.class, "northStats");
	public static Attribute<QObject<MinAvgMax>> eastStats = newAttribute(QObject.class, "eastStats");

	public static Attribute<QObject<MinAvgMax>> depthStats = newAttribute(QObject.class, "depthStats");

	public static final StatsModel spec = new StatsModel();

	private StatsModel() {

	}
}
