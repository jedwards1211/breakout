package org.breakout.model.parsed;

import org.breakout.model.StationKey;

public class ParsedFixedStation {
	public ParsedField<String> cave;
	public ParsedField<String> name;

	public Object location;

	public StationKey key() {
		return new StationKey(cave.value, name.value);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ParsedFixedStation [cave=").append(cave).append(", name=").append(name).append(", location=")
				.append(location).append("]");
		return builder.toString();
	}
}
