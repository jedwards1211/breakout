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
package org.andork.event;

public class SourcePath {
	public final Object parent;
	public final Object child;

	public SourcePath(Object parent, Object child) {
		super();
		this.parent = parent;
		this.child = child;
	}

	@Override
	public String toString() {
		return getClass().getName() + "[" + toStringHelper(this) + "]";
	}

	private String toStringHelper(Object source) {
		if (source instanceof SourcePath) {
			SourcePath path = (SourcePath) source;
			return path.parent + ", " + toStringHelper(path.child);
		}
		return source.toString();
	}
}
