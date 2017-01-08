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
package org.andork.bind;

import org.andork.event.HierarchicalBasicPropertyChangeListener;
import org.andork.model.HasChangeSupport;

public class HierarchicalChangeBinder<T extends HasChangeSupport> extends Binder<T>
		implements HierarchicalBasicPropertyChangeListener {
	private Binder<? extends T> upstream;
	T object;

	public HierarchicalChangeBinder() {
	}

	public HierarchicalChangeBinder<T> bind(Binder<? extends T> upstream) {
		if (this.upstream != upstream) {
			if (this.upstream != null) {
				unbind0(this.upstream, this);
			}
			this.upstream = upstream;
			if (this.upstream != null) {
				bind0(this.upstream, this);
			}

			update(false);
		}
		return this;
	}

	@Override
	public T get() {
		return object;
	}

	@Override
	public void propertyChange(Object source, Object property, Object oldValue, Object newValue, int index) {
		if (source == object) {
			update(false);
		}
	}

	@Override
	public void set(T newValue) {
	}

	public void unbind() {
		bind(null);
	}

	@Override
	public void update(boolean force) {
		T prevObject = object;
		T newObject = upstream == null ? null : upstream.get();
		if (object != newObject) {
			if (object != null) {
				object.changeSupport().removePropertyChangeListener(this);
			}
			object = newObject;
			if (newObject != null) {
				newObject.changeSupport().addPropertyChangeListener(this);
			}
		}
		if (force || prevObject != newObject) {
			updateDownstream(force);
		}
	}

	@Override
	public void childrenChanged(Object source, ChangeType changeType, Object... children) {
		update(true);
	}
}
