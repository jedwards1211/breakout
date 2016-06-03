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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.BoundedRangeModel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.andork.bind.Binder;
import org.andork.util.Java7;

public class JSliderValueBinder extends Binder<Integer> implements ChangeListener, PropertyChangeListener {
	public static JSliderValueBinder bind(JSlider slider, Binder<Integer> upstream) {
		return new JSliderValueBinder(slider).bind(upstream);
	}

	Binder<Integer> upstream;
	JSlider slider;

	BoundedRangeModel model;

	boolean updating;

	public JSliderValueBinder(JSlider slider) {
		super();
		this.slider = slider;
		if (slider != null) {
			slider.addPropertyChangeListener("model", this);
			setModel(slider.getModel());
		}
	}

	public JSliderValueBinder bind(Binder<Integer> upstream) {
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
	public Integer get() {
		return model == null ? null : model.getValue();
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getSource() == slider) {
			if ("model".equals(evt.getPropertyName())) {
				setModel(slider.getModel());
			}
		}
	}

	@Override
	public void set(Integer newValue) {
		if (model != null && newValue != null) {
			model.setValue(newValue);
		}
	}

	protected void setModel(BoundedRangeModel model) {
		if (this.model != model) {
			if (this.model != null) {
				this.model.removeChangeListener(this);
			}
			this.model = model;
			if (model != null) {
				model.addChangeListener(this);
			}

			update(false);
		}
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		if (!updating && upstream != null && e.getSource() == model) {
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
			Integer newValue = upstream == null ? null : upstream.get();
			if (force || !Java7.Objects.equals(get(), newValue)) {
				set(newValue);
			}
		} finally {
			updating = false;
		}
	}
}
