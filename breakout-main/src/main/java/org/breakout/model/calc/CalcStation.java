package org.breakout.model.calc;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.breakout.model.StationKey;
import org.breakout.model.parsed.Lead;

public class CalcStation {
	public String name;
	public String cave;

	public CalcStation() {

	}

	public CalcStation(String name) {
		this.name = name;
	}

	public final double[] position = { Double.NaN, Double.NaN, Double.NaN };

	public final Map<StationKey, CalcShot> shots = new LinkedHashMap<>(3);
	public List<Lead> leads;

	public StationKey key() {
		return new StationKey(cave, name);
	}

	public boolean hasPosition() {
		for (int i = 0; i < 3; i++) {
			if (!Double.isFinite(position[i])) {
				return false;
			}
		}
		return true;
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
}
