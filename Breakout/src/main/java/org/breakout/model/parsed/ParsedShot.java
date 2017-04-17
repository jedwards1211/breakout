package org.breakout.model.parsed;

import java.util.BitSet;
import java.util.List;

public class ParsedShot {
	public List<ParsedShotMeasurement> measurements;
	private BitSet flags;
	private static final int EXCLUDE_DISTANCE = 0;
	private static final int EXCLUDE_FROM_PLOTTING = 1;

	private void setFlag(int flag, boolean value) {
		if (value) {
			if (flags == null) {
				flags = new BitSet();
			}
			flags.set(flag);
		} else if (flags != null) {
			flags.clear(flag);
		}
	}

	private boolean getFlag(int flag) {
		if (flags == null) {
			return false;
		}
		return flags.get(flag);
	}

	public boolean isExcludeDistance() {
		return getFlag(EXCLUDE_DISTANCE);
	}

	public boolean isExcludeFromPlotting() {
		return getFlag(EXCLUDE_FROM_PLOTTING);
	}

	public void setExcludeDistance(boolean value) {
		setFlag(EXCLUDE_DISTANCE, value);
	}

	public void setExcludeFromPlotting(boolean value) {
		setFlag(EXCLUDE_FROM_PLOTTING, value);
	}

	public ParsedShot overrides;
	public ParsedShot overriddenBy;
}
