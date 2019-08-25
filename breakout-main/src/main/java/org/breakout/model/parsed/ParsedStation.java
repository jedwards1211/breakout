package org.breakout.model.parsed;

import static org.andork.util.JavaScript.falsy;

import java.util.BitSet;
import java.util.List;

import org.breakout.model.StationKey;
import org.breakout.model.raw.SurveyLead;

public class ParsedStation {
	public List<ParseMessage> messages;
	public ParsedField<String> cave;
	public ParsedField<String> name;

	public ParsedCrossSection crossSection;
	public List<ParsedSplayShot> splays;
	public List<SurveyLead> leads;

	private BitSet flags;
	private static final int IS_ENTRANCE = 0;
	private static final int IS_ABOVE_GROUND = 1;

	private void setFlag(int flag, boolean value) {
		if (value) {
			if (flags == null) {
				flags = new BitSet();
			}
			flags.set(flag);
		}
		else if (flags != null) {
			flags.clear(flag);
		}
	}

	private boolean getFlag(int flag) {
		if (flags == null) {
			return false;
		}
		return flags.get(flag);
	}

	public boolean isEntrance() {
		return getFlag(IS_ENTRANCE);
	}

	public boolean isAboveGround() {
		return getFlag(IS_ABOVE_GROUND);
	}

	public void setIsEntrance(boolean value) {
		setFlag(IS_ENTRANCE, value);
	}

	public void setIsAboveGround(boolean value) {
		setFlag(IS_ABOVE_GROUND, value);
	}

	public StationKey key() {
		String name = ParsedField.getValue(this.name);
		return falsy(name) ? null : new StationKey(ParsedField.getValue(cave), name);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ParsedStation [cave=").append(cave).append(", name=").append(name).append("]");
		return builder.toString();
	}
}
