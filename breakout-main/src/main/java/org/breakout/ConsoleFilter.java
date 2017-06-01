package org.breakout;

import java.util.logging.Filter;
import java.util.logging.LogRecord;
import java.util.regex.Pattern;

public class ConsoleFilter implements Filter {
	private static final Pattern excludePattern = Pattern.compile("^System.(out|err)$");

	@Override
	public boolean isLoggable(LogRecord record) {
		return !excludePattern.matcher(record.getLoggerName()).find();
	}
}
