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
	Binder<Integer>		upstream;
	JSlider				slider;
	BoundedRangeModel	model;

	boolean				updating;

	public JSliderValueBinder(JSlider slider) {
		super();
		this.slider = slider;
		if (slider != null) {
			slider.addPropertyChangeListener("model", this);
			setModel(slider.getModel());
		}
	}

	public static JSliderValueBinder bind(JSlider slider, Binder<Integer> upstream) {
		return new JSliderValueBinder(slider).bind(upstream);
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

	public void unbind() {
		bind(null);
	}

	@Override
	public Integer get() {
		return model == null ? null : model.getValue();
	}

	@Override
	public void set(Integer newValue) {
		if (model != null && newValue != null) {
			model.setValue(newValue);
		}
	}

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
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getSource() == slider) {
			if ("model".equals(evt.getPropertyName())) {
				setModel(slider.getModel());
			}
		}
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		if (!updating && upstream != null && e.getSource() == model) {
			upstream.set(get());
		}
	}
}
