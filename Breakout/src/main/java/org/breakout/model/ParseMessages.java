package org.breakout.model;

import java.util.List;
import java.util.Objects;

import org.andork.collect.LinkedListMultiMap;

public class ParseMessages extends LinkedListMultiMap<ParseMessages.Key, ParseMessages.Message> {
	public static class Key {
		private Object object;
		private Object property;

		public Key(Object object, Object property) {
			super();
			this.object = Objects.requireNonNull(object);
			this.property = Objects.requireNonNull(property);
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + System.identityHashCode(property);
			result = prime * result + System.identityHashCode(object);
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
			Key other = (Key) obj;
			return object == other.object && property == other.property;
		}

		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("Key [object=").append(object).append(", property=").append(property).append("]");
			return builder.toString();
		}
	}

	public static enum Severity {
		INFO, WARNING, ERROR
	}

	public static Message info(String text) {
		return new Message(Severity.INFO, text);
	}

	public static Message warning(String text) {
		return new Message(Severity.WARNING, text);
	}

	public static Message error(String text) {
		return new Message(Severity.ERROR, text);
	}

	public static class Message {
		private final Severity severity;
		private final String text;

		public Message(Severity severity, String text) {
			super();
			this.severity = severity;
			this.text = text;
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
			Message other = (Message) obj;
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

	public void add(Object object, Object property, Severity severity, String text) {
		put(new Key(object, property), new Message(severity, text));
	}

	public void info(Object object, Object property, String text) {
		add(object, property, Severity.INFO, text);
	}

	public void warning(Object object, Object property, String text) {
		add(object, property, Severity.WARNING, text);
	}

	public void error(Object object, Object property, String text) {
		add(object, property, Severity.ERROR, text);
	}

	public List<Message> get(Object object, Object property) {
		return get(new Key(object, property));
	}
}
