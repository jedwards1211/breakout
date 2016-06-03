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
package org.andork.q2;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

public abstract class QElement {
	private List<QListener> listeners;

	protected void addListener(QListener listener) {
		if (listeners == null) {
			listeners = new LinkedList<>();
		}
		if (!listeners.contains(listener)) {
			listeners.add(listener);
		}
	}

	protected <L extends QListener> void forEachListener(Class<? extends L> type, Consumer<L> consumer) {
		forEachListener(l -> {
			if (type.isInstance(l)) {
				try {
					consumer.accept(type.cast(l));
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		});
	}

	protected void forEachListener(Consumer<QListener> consumer) {
		if (listeners != null) {
			for (QListener listener : listeners) {
				try {
					consumer.accept(listener);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}
	}

	protected void removeListener(QListener listener) {
		if (listeners != null) {
			listeners.remove(listener);
			if (listeners.isEmpty()) {
				listeners = null;
			}
		}
	}
}
