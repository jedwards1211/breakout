package org.breakout.model.calc;

import java.util.Iterator;
import java.util.Map;

import org.breakout.model.StationKey;

public class CalcStation {
	public String name;
	public String cave;
	public float date;
	private CalcShot originatingShot;

	public CalcStation() {

	}

	public CalcStation(String name) {
		this.name = name;
	}

	public final double[] position = { Double.NaN, Double.NaN, Double.NaN };

	public Map<StationKey, CalcShot> shots;
	public int numShots = 0;

	public StationKey key() {
		return new StationKey(cave, name);
	}

	public boolean hasPosition() {
		return Double.isFinite(position[0]) && Double.isFinite(position[1]) && Double.isFinite(position[2]);
	}

	public boolean isDeadEnd() {
		return shots.size() == 1;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("CalcStation [cave=").append(cave).append(", name=").append(name).append("]");
		return builder.toString();
	}

	/**
	 * @return The shot that originated this station.
	 */
	public CalcShot originatingShot() {
		if (originatingShot == null) {
			if (shots == null || shots.isEmpty())
				return null;
			Iterator<CalcShot> i = shots.values().iterator();
			originatingShot = i.next();
			while (i.hasNext()) {
				CalcShot other = i.next();
				if (other.date < originatingShot.date) {
					originatingShot = other;
				}
			}
		}
		return originatingShot;
	}
}
