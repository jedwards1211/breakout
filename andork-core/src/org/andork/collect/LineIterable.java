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
package org.andork.collect;

import java.io.File;
import java.net.URL;
import java.util.Iterator;

/**
 * Absolutely the easiest way to read a file or URL line-by-line:
 *
 * <pre>
 * for (String line : new LineIterable(&quot;test.txt&quot;)) {
 * 	System.out.println(line);
 * }
 * </pre>
 *
 * @author andy.edwards
 */
public class LineIterable implements Iterable<String> {
	public static LineIterable linesOf(File file) {
		return new LineIterable(file);
	}

	public static LineIterable linesOf(String file) {
		return new LineIterable(file);
	}

	public static LineIterable linesOf(URL url) {
		return new LineIterable(url);
	}

	File file;

	URL url;

	public LineIterable(File file) {
		super();
		this.file = file;
	}

	public LineIterable(String file) {
		this(new File(file));
	}

	public LineIterable(URL url) {
		super();
		this.url = url;
	}

	@Override
	public Iterator<String> iterator() {
		if (file != null) {
			return new LineIterator(file);
		}
		if (url != null) {
			return new LineIterator(url);
		}
		return null;
	}
}
