/*******************************************************************************
 * Breakout Cave Survey Visualizer
 *
 * Copyright (C) 2014 James Edwards
 *
 * jedwards8 at fastmail dot fm
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *******************************************************************************/
package org.andork.io;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * An {@link OutputStream} that writes to multiple downstream
 * {@link OutputStream}s.
 */
public class MultiplexOutputStream extends OutputStream {
	@SuppressWarnings("serial")
	public static final class DownstreamException extends IOException {
		/**
		 *
		 */
		private static final long serialVersionUID = -688964594382950775L;

		/**
		 * Print our stack trace as a cause for the specified stack trace.
		 */
		private static void printStackTraceAsCause(PrintStream s,
				Throwable cause, StackTraceElement[] causedTrace, String tabs) {
			// assert Thread.holdsLock(s);

			// Compute number of frames in common between this and caused
			StackTraceElement[] trace = cause.getStackTrace();
			int m = trace.length - 1, n = causedTrace.length - 1;
			while (m >= 0 && n >= 0 && trace[m].equals(causedTrace[n])) {
				m--;
				n--;
			}
			int framesInCommon = trace.length - 1 - m;

			s.println(tabs + "Caused by: " + cause);
			for (int i = 0; i <= m; i++) {
				s.println(tabs + "\tat " + trace[i]);
			}
			if (framesInCommon != 0) {
				s.println(tabs + "\t... " + framesInCommon + " more");
			}

			// Recurse if we have a cause
			Throwable ourCause = cause.getCause();
			if (ourCause != null) {
				printStackTraceAsCause(s, ourCause, trace, tabs + "  ");
			}
		}

		/**
		 * Print our stack trace as a cause for the specified stack trace.
		 */
		private static void printStackTraceAsCause(PrintWriter s,
				Throwable cause, StackTraceElement[] causedTrace, String tabs) {
			// assert Thread.holdsLock(s);

			// Compute number of frames in common between this and caused
			StackTraceElement[] trace = cause.getStackTrace();
			int m = trace.length - 1, n = causedTrace.length - 1;
			while (m >= 0 && n >= 0 && trace[m].equals(causedTrace[n])) {
				m--;
				n--;
			}
			int framesInCommon = trace.length - 1 - m;

			s.println(tabs + "Caused by: " + cause);
			for (int i = 0; i <= m; i++) {
				s.println(tabs + "\tat " + trace[i]);
			}
			if (framesInCommon != 0) {
				s.println(tabs + "\t... " + framesInCommon + " more");
			}

			// Recurse if we have a cause
			Throwable ourCause = cause.getCause();
			if (ourCause != null) {
				printStackTraceAsCause(s, cause, trace, tabs + "  ");
			}
		}

		private final List<Throwable> causes;

		private DownstreamException(List<Throwable> causes) {
			this.causes = Collections.unmodifiableList(causes);
		}

		public List<Throwable> getCauses() {
			return causes;
		}

		/**
		 * Prints this throwable and its backtrace to the specified print
		 * stream.
		 *
		 * @param s
		 *            <code>PrintStream</code> to use for output
		 */
		@Override
		public void printStackTrace(PrintStream s) {
			synchronized (s) {
				s.println(this);
				StackTraceElement[] trace = getStackTrace();
				for (int i = 0; i < trace.length; i++) {
					s.println("\tat " + trace[i]);
				}

				for (Throwable cause : causes) {
					printStackTraceAsCause(s, cause, trace, "  ");
				}
			}
		}

		/**
		 * Prints this throwable and its backtrace to the specified print
		 * writer.
		 *
		 * @param s
		 *            <code>PrintWriter</code> to use for output
		 * @since JDK1.1
		 */
		@Override
		public void printStackTrace(PrintWriter s) {
			synchronized (s) {
				s.println(this);
				StackTraceElement[] trace = getStackTrace();
				for (int i = 0; i < trace.length; i++) {
					s.println("\tat " + trace[i]);
				}

				for (Throwable cause : causes) {
					printStackTraceAsCause(s, cause, trace, "  ");
				}
			}
		}
	}

	OutputStream[] downstreams;

	public MultiplexOutputStream(OutputStream... downstreams) {
		this.downstreams = Arrays.copyOf(downstreams, downstreams.length);
	}

	@Override
	public void close() throws IOException {
		ArrayList<Throwable> downstreamExceptions = null;
		for (OutputStream target : downstreams) {
			try {
				target.close();
			} catch (Throwable t) {
				if (downstreamExceptions == null) {
					downstreamExceptions = new ArrayList<Throwable>();
				}
				downstreamExceptions.add(t);
			}
		}

		if (downstreamExceptions != null) {
			downstreamExceptions.trimToSize();
			throw new DownstreamException(downstreamExceptions);
		}
	}

	@Override
	public void flush() throws IOException {
		ArrayList<Throwable> downstreamExceptions = null;
		for (OutputStream target : downstreams) {
			try {
				target.flush();
			} catch (Throwable t) {
				if (downstreamExceptions == null) {
					downstreamExceptions = new ArrayList<Throwable>();
				}
				downstreamExceptions.add(t);
			}
		}

		if (downstreamExceptions != null) {
			downstreamExceptions.trimToSize();
			throw new DownstreamException(downstreamExceptions);
		}
	}

	@Override
	public void write(int b) throws IOException {
		ArrayList<Throwable> downstreamExceptions = null;
		for (OutputStream target : downstreams) {
			try {
				target.write(b);
			} catch (Throwable t) {
				if (downstreamExceptions == null) {
					downstreamExceptions = new ArrayList<Throwable>();
				}
				downstreamExceptions.add(t);
			}
		}

		if (downstreamExceptions != null) {
			downstreamExceptions.trimToSize();
			throw new DownstreamException(downstreamExceptions);
		}
	}
}
