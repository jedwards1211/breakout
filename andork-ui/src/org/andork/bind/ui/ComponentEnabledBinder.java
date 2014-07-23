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
package org.andork.bind.ui;

import java.awt.Component;

import org.andork.bind.Binder;
import org.andork.util.Java7;

public class ComponentEnabledBinder extends Binder<Boolean> {
	Binder<Boolean>	upstream;
	Component		component;
	boolean			updating;

	public ComponentEnabledBinder(Component component) {
		super();
		this.component = component;
	}

	public static ComponentEnabledBinder bind(Component component, Binder<Boolean> upstream) {
		return new ComponentEnabledBinder(component).bind(upstream);
	}

	public ComponentEnabledBinder bind(Binder<Boolean> upstream) {
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

	public void unbind() {
		bind(null);
	}

	@Override
	public Boolean get() {
		return component == null ? null : component.isEnabled();
	}

	@Override
	public void set(Boolean newValue) {
		if (component != null && newValue != null) {
			component.setEnabled(newValue);
		}
	}

	public void update(boolean force) {
		updating = true;
		try {
			Boolean newValue = upstream == null ? null : upstream.get();
			if (force || !Java7.Objects.equals(get(), newValue)) {
				set(newValue);
			}
		} finally {
			updating = false;
		}
	}
}
