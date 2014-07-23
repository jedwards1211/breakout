/*******************************************************************************
 * Breakout Cave Survey Visualizer
 *
 * Copyright (C) 2014 James Edwards
 *
 * jedwards8 at fastmail dot fm
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *******************************************************************************/
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
