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
package org.andork.plot;

import org.andork.bind.Binder;
import org.andork.event.BasicPropertyChangeListener;

import com.andork.plot.LinearAxisConversion;
import com.andork.plot.PlotAxis;

public class PlotAxisConversionBinder extends Binder<LinearAxisConversion> implements BasicPropertyChangeListener {
	public static PlotAxisConversionBinder bind(PlotAxis axis, Binder<LinearAxisConversion> upstream) {
		return new PlotAxisConversionBinder(axis).bind(upstream);
	}

	private PlotAxis axis;

	private Binder<LinearAxisConversion> upstream;

	private boolean updating;

	private PlotAxisConversionBinder(PlotAxis axis) {
		super();
		this.axis = axis;
		if (axis != null) {
			axis.changeSupport().addPropertyChangeListener(PlotAxis.Property.AXIS_CONVERSION, this);
		}
	}

	public PlotAxisConversionBinder bind(Binder<LinearAxisConversion> upstream) {
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
	public LinearAxisConversion get() {
		return axis == null ? null : axis.getAxisConversion();
	}

	@Override
	public void propertyChange(Object source, Object property, Object oldValue, Object newValue, int index) {
		if (!updating && upstream != null && source == axis && property == PlotAxis.Property.AXIS_CONVERSION) {
			upstream.set(new LinearAxisConversion(get()));
		}
	}

	@Override
	public void set(LinearAxisConversion newValue) {
		if (axis != null && !newValue.equals(axis.getAxisConversion())) {
			axis.setAxisConversion(new LinearAxisConversion(newValue));
			axis.repaint();
		}
	}

	@Override
	public void update(boolean force) {
		updating = true;
		try {
			LinearAxisConversion conversion = upstream == null ? null : upstream.get();
			if (conversion != null) {
				set(conversion);
			}
		} finally {
			updating = false;
		}
	}
}
