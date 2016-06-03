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
package org.andork.bind.ui;

import javax.swing.JSpinner;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.andork.bind.Binder;
import org.andork.util.Java7;

public class JSpinnerValueBinder<T> extends Binder<T> implements ChangeListener {
	public static <T> JSpinnerValueBinder<T> bind(JSpinner spinner, Class<T> valueClass, Binder<T> upstream) {
		return new JSpinnerValueBinder<T>(spinner, valueClass).bind(upstream);
	}

	Class<T> valueClass;
	Binder<T> upstream;
	JSpinner spinner;

	boolean updating;

	public JSpinnerValueBinder(JSpinner spinner, Class<T> valueClass) {
		super();
		this.valueClass = valueClass;
		this.spinner = spinner;
		if (spinner != null) {
			spinner.addChangeListener(this);
		}
	}

	public JSpinnerValueBinder<T> bind(Binder<T> upstream) {
		if (this.upstream != upstream) {
			if (this.upstream != null) {
				unbind0(this.upstream, this);
			}
			this.upstream = upstream;
			if (upstream != null) {
				bind0(this.upstream, this);
			}
			update(false);
		}
		return this;
	}

	@Override
	public T get() {
		return spinner == null ? null : valueClass.cast(spinner.getValue());
	}

	@Override
	public void set(T newValue) {
		if (spinner != null && newValue != null) {
			spinner.setValue(newValue);
		}
	}

	@Override
	public void stateChanged(ChangeEvent arg0) {
		if (!updating && upstream != null) {
			upstream.set(get());
		}
	}

	public void unbind() {
		bind(null);
	}

	@Override
	public void update(boolean force) {
		updating = true;
		try {
			T newValue = upstream == null ? null : upstream.get();
			if (force || !Java7.Objects.equals(get(), newValue)) {
				set(newValue);
			}
		} finally {
			updating = false;
		}
	}
}
