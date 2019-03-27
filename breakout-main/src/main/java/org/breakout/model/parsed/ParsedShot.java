package org.breakout.model.parsed;

import java.util.BitSet;
import java.util.List;

public class ParsedShot {
	public List<ParsedShotMeasurement> measurements;
	private int flags = 0;
	private static final int EXCLUDE_DISTANCE = 0;
	private static final int EXCLUDE_FROM_PLOTTING = 1;
	private static final int HAS_SURVEY_NOTES = 2;

	private void setFlag(int flag, boolean value) {
		if (value) flags |= flag;
		else flags &= ~flag;
	}

	private boolean getFlag(int flag) {
		return (flags & flag) != 0;
	}

	public boolean isExcludeDistance() {
		return getFlag(EXCLUDE_DISTANCE);
	}

	public boolean isExcludeFromPlotting() {
		return getFlag(EXCLUDE_FROM_PLOTTING);
	}
	
	public boolean hasSurveyNotes() {
		return getFlag(HAS_SURVEY_NOTES);
	}

	public void setExcludeDistance(boolean value) {
		setFlag(EXCLUDE_DISTANCE, value);
	}

	public void setExcludeFromPlotting(boolean value) {
		setFlag(EXCLUDE_FROM_PLOTTING, value);
	}

	public void setHasSurveyNotes(boolean value) {
		setFlag(HAS_SURVEY_NOTES, value);
	}

	public ParsedShot overrides;
	public ParsedShot overriddenBy;
}
