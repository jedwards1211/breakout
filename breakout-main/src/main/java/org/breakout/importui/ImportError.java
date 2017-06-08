package org.breakout.importui;

import java.util.zip.InflaterInputStream;

import org.andork.compass.CompassParseError;
import org.andork.segment.Segment;
import org.andork.segment.SegmentParseException;
import org.andork.walls.WallsMessage;

public class ImportError {
	public static enum Severity {
		ERROR, WARNING
	}

	private final Severity severity;
	private final String message;
	private final Segment segment;

	public ImportError(SegmentParseException e) {
		this(Severity.ERROR, e.getMessage(), e.getSegment());
	}

	public ImportError(CompassParseError error) {
		this(convertSeverity(error.getSeverity()), error.getMessage(), error.getSegment());
	}

	private static Severity convertSeverity(org.andork.compass.CompassParseError.Severity inputSeverity) {
		switch (inputSeverity) {
		case ERROR:
			return Severity.ERROR;
		case WARNING:
			return Severity.WARNING;
		default:
			throw new IllegalArgumentException("Unknown severity: " + inputSeverity);
		}
	}

	public ImportError(Severity severity, String message, Segment segment) {
		super();
		this.severity = severity;
		this.message = message;
		this.segment = segment;
	}

	public ImportError(WallsMessage message) {
		this(convertSeverity(message.severity), message.message, message.segment);
	}

	private static Severity convertSeverity(String severity) {
		if (severity.equalsIgnoreCase("error")) {
			return Severity.ERROR;
		} else if (severity.equalsIgnoreCase("warning")) {
			return Severity.WARNING;
		} else {
			throw new IllegalArgumentException("Unknown severity: " + severity);
		}
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
		ImportError other = (ImportError) obj;
		if (message == null) {
			if (other.message != null) {
				return false;
			}
		} else if (!message.equals(other.message)) {
			return false;
		}
		if (segment == null) {
			if (other.segment != null) {
				return false;
			}
		} else if (!segment.equals(other.segment)) {
			return false;
		}
		if (severity != other.severity) {
			return false;
		}
		return true;
	}

	public String getMessage() {
		return message;
	}

	public Segment getSegment() {
		return segment;
	}

	public Severity getSeverity() {
		return severity;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (message == null ? 0 : message.hashCode());
		result = prime * result + (segment == null ? 0 : segment.hashCode());
		result = prime * result + (severity == null ? 0 : severity.hashCode());
		return result;
	}

	@Override
	public String toString() {
		return severity.toString().toLowerCase() + ": " + message +
				" (in " + segment.source + ", line " + (segment.startLine + 1) +
				", column " + (segment.startCol + 1) + "):\n" +
				segment.underlineInContext();
	}
}
