package org.breakout.model.parsed;

public class ParseMessage {
	public static enum Severity {
		INFO, WARNING, ERROR
	}

	public final Severity severity;
	public final String text;

	public ParseMessage(Severity severity, String text) {
		super();
		this.severity = severity;
		this.text = text;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		ParseMessage other = (ParseMessage) obj;
		if (text == null) {
			if (other.text != null) {
				return false;
			}
		} else if (!text.equals(other.text)) {
			return false;
		}
		if (severity != other.severity) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ParseMessage [severity=").append(severity).append(", text=").append(text).append("]");
		return builder.toString();
	}
}
