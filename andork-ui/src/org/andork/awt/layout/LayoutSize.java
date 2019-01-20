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
package org.andork.awt.layout;

import java.awt.Component;
import java.awt.Dimension;

import javax.swing.text.View;

public enum LayoutSize {
	MINIMUM {
		@Override
		public Dimension get(Component comp) {
			return comp.getMinimumSize();
		}

		@Override
		public boolean isSet(Component comp) {
			return comp.isMinimumSizeSet();
		}

		@Override
		public void set(Component comp, Dimension size) {
			comp.setMinimumSize(size);
		}

		@Override
		public float getSpan(View view, int axis) {
			return view.getMinimumSpan(axis);
		}
	},
	PREFERRED {
		@Override
		public Dimension get(Component comp) {
			return comp.getPreferredSize();
		}

		@Override
		public boolean isSet(Component comp) {
			return comp.isPreferredSizeSet();
		}

		@Override
		public void set(Component comp, Dimension size) {
			comp.setPreferredSize(size);
		}

		@Override
		public float getSpan(View view, int axis) {
			return view.getPreferredSpan(axis);
		}
	},
	MAXIMUM {
		@Override
		public Dimension get(Component comp) {
			return comp.getMaximumSize();
		}

		@Override
		public boolean isSet(Component comp) {
			return comp.isMaximumSizeSet();
		}

		@Override
		public void set(Component comp, Dimension size) {
			comp.setMaximumSize(size);
		}

		@Override
		public float getSpan(View view, int axis) {
			return view.getMaximumSpan(axis);
		}
	},
	ACTUAL {
		@Override
		public Dimension get(Component comp) {
			return comp.getSize();
		}

		@Override
		public boolean isSet(Component comp) {
			throw new UnsupportedOperationException("the actual size is always set");
		}

		@Override
		public void set(Component comp, Dimension size) {
			comp.setSize(size);
		}

		@Override
		public float getSpan(View view, int axis) {
			return view.getContainer().getHeight();
		}
	};

	public abstract Dimension get(Component comp);

	public abstract boolean isSet(Component comp);

	public abstract void set(Component comp, Dimension size);

	public abstract float getSpan(View view, int axis);
}
