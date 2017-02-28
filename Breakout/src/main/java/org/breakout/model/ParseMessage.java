package org.breakout.model;

public class ParseMessage {
	public static enum Severity {
		INFO, WARNING, ERROR
	}

	private final Severity severity;
	private final String text;

	public ParseMessage(Severity severity, String text) {
		super();
		this.severity = severity;
		this.text = text;
	}

	public static ParseMessage info(String text) {
		return new ParseMessage(Severity.INFO, text);
	}

	public static ParseMessage warning(String text) {
		return new ParseMessage(Severity.WARNING, text);
	}

	public static ParseMessage error(String text) {
		return new ParseMessage(Severity.ERROR, text);
	}

	public Severity getSeverity() {
		return severity;
	}

	public String getText() {
		return text;
	}

	@Override
	public String toString() {
		return severity + ": " + text;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (severity == null ? 0 : severity.hashCode());
		result = prime * result + (text == null ? 0 : text.hashCode());
		return result;
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
		if (severity != other.severity) {
			return false;
		}
		if (text == null) {
			if (other.text != null) {
				return false;
			}
		} else if (!text.equals(other.text)) {
			return false;
		}
		return true;
	}
}
