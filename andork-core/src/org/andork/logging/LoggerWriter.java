package org.andork.logging;

import java.io.IOException;
import java.io.Writer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A writer that logs to a given {@link Logger} using a given {@link Level}. It
 * will only log a record when {@code '\n'} is written after a non-{@code'\n'}
 * character.
 * 
 * @author andy.edwards
 */
public class LoggerWriter extends Writer {
	Logger			logger;
	Level			level;
	StringBuilder	builder	= new StringBuilder();

	public LoggerWriter(Logger logger, Level level) {
		if (logger == null) {
			throw new IllegalArgumentException("logger must be non-null");
		}
		if (level == null) {
			throw new IllegalArgumentException("level must be non-null");
		}
		this.logger = logger;
		this.level = level;
	}

	@Override
	public void write(char[] cbuf, int off, int len) throws IOException {
		if (logger == null) {
			throw new IOException("LoggerWriter has been closed");
		}
		for (int i = off; i < off + len; i++) {
			char c = cbuf[i];
			if (c == '\r') {
				continue;
			}
			if (c == '\n') {
				logger.log(level, builder.toString());
				builder.setLength(0);
			} else {
				builder.append(c);
			}
		}
	}

	@Override
	public void flush() throws IOException {
		synchronized (lock) {
			if (logger == null) {
				throw new IOException("LoggerWriter has been closed");
			}
			if (builder.length() > 0) {
				logger.log(level, builder.toString());
				builder.setLength(0);
			}
		}
	}

	@Override
	public void close() throws IOException {
		synchronized (lock) {
			flush();
			logger = null;
			level = null;
			builder = null;
		}
	}
}
