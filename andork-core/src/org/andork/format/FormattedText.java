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
package org.andork.format;

public class FormattedText {
	String text;
	Object value;
	Format format;
	Exception formatException;

	public FormattedText() {

	}

	public FormattedText(Format format) {
		this.format = format;
		format();
	}

	public FormattedText(String text, Format format) {
		this.text = text;
		this.format = format;
		format();
	}

	private void format() {
		if (format != null) {
			try {
				value = format.parse(text);
				formatException = null;
			} catch (FormatWarning warning) {
				value = warning.getFormattedValue();
				formatException = warning;
			} catch (Exception ex) {
				value = null;
				formatException = ex;
			}
		} else {
			value = null;
			formatException = null;
		}
	}

	public Format getFormat() {
		return format;
	}

	public Exception getFormatException() {
		return formatException;
	}

	public String getText() {
		return text;
	}

	public Object getValue() {
		return value;
	}

	public void setFormat(Format format) {
		if (this.format != format) {
			this.format = format;
			format();
		}
	}

	public void setText(String text) {
		this.text = text;
		format();
	}

	@Override
	public String toString() {
		return value == null || format == null ? text : format.format(value);
	}
}
