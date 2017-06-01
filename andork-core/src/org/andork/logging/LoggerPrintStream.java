package org.andork.logging;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.Locale;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * A {@link PrintStream} that writes a {@link LogRecord} entry to a
 * {@link Logger} every time it is {@link #flush()}ed. It has an
 * {@code autoFlush} option like {@link PrintStream}, but unlike
 * {@link PrintStream}, it will not flush until the end of {@code println} and
 * {@code format} calls, so that each call will output a single
 * {@link LogRecord}, even if it has a multiline message.
 * 
 * @author Andy Edwards
 */
public class LoggerPrintStream extends PrintStream {
	private final Logger logger;
	private final Level level;
	private final ByteArrayOutputStream bytesOut;
	private boolean flushOnNewline = true;
	private final boolean autoFlush = true;
	private final String charsetName;

	public LoggerPrintStream(Logger logger, Level level) {
		this(logger, level, false);
	}

	public LoggerPrintStream(Logger logger, Level level, boolean autoFlush) {
		this(logger, level, autoFlush, null);
	}

	public LoggerPrintStream(Logger logger, Level level, boolean autoFlush, String charsetName) {
		super(new ByteArrayOutputStream(), false);
		this.logger = logger;
		this.level = level;
		this.charsetName = charsetName;
		bytesOut = (ByteArrayOutputStream) out;
	}

	@Override
	public void flush() {
		synchronized (this) {
			super.flush();
			String message = null;
			try {
				message = charsetName == null
						? new String(bytesOut.toByteArray())
						: new String(bytesOut.toByteArray(), charsetName);
				if (message.endsWith("\n")) {
					message = message.substring(0, message.length() - 1);
				}
			} catch (UnsupportedEncodingException e) {
				setError();
			}
			bytesOut.reset();
			if (message != null && !message.trim().isEmpty()) {
				logger.log(level, message);
			}
			for (Handler handler : logger.getHandlers()) {
				handler.flush();
			}
		}
	}

	@Override
	public void close() {
		synchronized (this) {
			flush();
			super.close();
			for (Handler handler : logger.getHandlers()) {
				handler.close();
			}
		}
	}

	/** Check to make sure that the stream has not been closed */
	private void ensureOpen() throws IOException {
		if (out == null) {
			throw new IOException("Stream closed");
		}
	}

	/**
	 * Writes the specified byte to this stream. If the byte is a newline and
	 * automatic flushing is enabled then the <code>flush</code> method will be
	 * invoked.
	 *
	 * <p>
	 * Note that the byte is written as given; to write a character that will be
	 * translated according to the platform's default character encoding, use
	 * the <code>print(char)</code> or <code>println(char)</code> methods.
	 *
	 * @param b
	 *            The byte to be written
	 * @see #print(char)
	 * @see #println(char)
	 */
	public void write(int b) {
		try {
			synchronized (this) {
				ensureOpen();
				out.write(b);
				if ((b == '\n') && flushOnNewline) {
					flush();
				}
			}
		} catch (InterruptedIOException x) {
			Thread.currentThread().interrupt();
		} catch (IOException x) {
			setError();
		}
	}

	/**
	 * Writes <code>len</code> bytes from the specified byte array starting at
	 * offset <code>off</code> to this stream. If automatic flushing is enabled
	 * then the <code>flush</code> method will be invoked.
	 *
	 * <p>
	 * Note that the bytes will be written as given; to write characters that
	 * will be translated according to the platform's default character
	 * encoding, use the <code>print(char)</code> or <code>println(char)</code>
	 * methods.
	 *
	 * @param buf
	 *            A byte array
	 * @param off
	 *            Offset from which to start taking bytes
	 * @param len
	 *            Number of bytes to write
	 */
	public void write(byte buf[], int off, int len) {
		try {
			synchronized (this) {
				ensureOpen();
				out.write(buf, off, len);
				if (flushOnNewline) {
					flush();
				}
			}
		} catch (InterruptedIOException x) {
			Thread.currentThread().interrupt();
		} catch (IOException x) {
			setError();
		}
	}

	/*
	 * The following private methods on the text- and character-output streams
	 * always flush the stream buffers, so that writes to the underlying byte
	 * stream occur as promptly as with the original PrintStream.
	 */

	@Override
	public void print(boolean b) {
		super.print(b ? "true" : "false");
	}

	@Override
	public void print(char c) {
		super.print(String.valueOf(c));
	}

	@Override
	public void print(int i) {
		super.print(String.valueOf(i));
	}

	@Override
	public void print(long l) {
		super.print(String.valueOf(l));
	}

	@Override
	public void print(float f) {
		super.print(String.valueOf(f));
	}

	@Override
	public void print(double d) {
		super.print(String.valueOf(d));
	}

	@Override
	public void print(char[] s) {
		print(new String(s));
	}

	@Override
	public void print(String s) {
		synchronized (this) {
			super.print(s);
			if (flushOnNewline && (s.indexOf('\n') >= 0)) {
				flush();
			}
		}
	}

	@Override
	public void print(Object obj) {
		print(String.valueOf(obj));
	}

	@Override
	public void println() {
		println("");
	}

	@Override
	public void println(boolean x) {
		println(x ? "true" : "false");
	}

	@Override
	public void println(char x) {
		println(String.valueOf(x));
	}

	@Override
	public void println(int x) {
		println(String.valueOf(x));
	}

	@Override
	public void println(long x) {
		println(String.valueOf(x));
	}

	@Override
	public void println(float x) {
		println(String.valueOf(x));
	}

	@Override
	public void println(double x) {
		println(String.valueOf(x));
	}

	@Override
	public void println(char[] x) {
		println(new String(x));
	}

	@Override
	public void println(String x) {
		synchronized (this) {
			try {
				flushOnNewline = false;
				super.println(x);
			} finally {
				flushOnNewline = autoFlush;
				flush();
			}
		}
	}

	@Override
	public void println(Object x) {
		println(String.valueOf(x));
	}

	@Override
	public PrintStream format(String format, Object... args) {
		synchronized (this) {
			try {
				flushOnNewline = false;
				super.format(format, args);
			} finally {
				flushOnNewline = autoFlush;
				flush();
			}
		}
		return this;
	}

	@Override
	public PrintStream format(Locale l, String format, Object... args) {
		synchronized (this) {
			try {
				flushOnNewline = false;
				super.format(l, format, args);
			} finally {
				flushOnNewline = autoFlush;
				flush();
			}
		}
		return this;
	}
}
