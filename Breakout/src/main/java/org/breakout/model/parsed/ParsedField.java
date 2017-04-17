package org.breakout.model.parsed;

import org.breakout.model.parsed.ParseMessage.Severity;

public class ParsedField<V> {
	public final V value;
	public ParseMessage message;

	public ParsedField(V value) {
		super();
		this.value = value;
	}

	public ParsedField(V value, ParseMessage message) {
		super();
		this.value = value;
		this.message = message;
	}

	public ParsedField(V value, Severity severity, String message) {
		this(value, new ParseMessage(severity, message));
	}

	public ParsedField(Severity severity, String message) {
		this(null, new ParseMessage(severity, message));
	}

	public static boolean hasValue(ParsedField<?> field) {
		return field != null && field.value != null;
	}

	public static <V> V getValue(ParsedField<V> field) {
		return field == null ? null : field.value;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ParsedField [value=").append(value).append(", message=").append(message).append("]");
		return builder.toString();
	}
}
