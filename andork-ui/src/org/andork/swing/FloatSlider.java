package org.andork.swing;

import javax.swing.JSlider;

@SuppressWarnings("serial")
public class FloatSlider extends JSlider {
	protected float floatMinimum = 0;
	protected float floatMaximum = 1;
	protected float floatValue = 0;
	protected boolean updatingIntValue = false;

	public FloatSlider() {
		this(0, 1, 0);
	}

	public FloatSlider(float minimum, float maximum, float value) {
		super(0, 10000);
		this.floatMinimum = minimum;
		this.floatMaximum = maximum;
		this.floatValue = value;
		addChangeListener(e -> {
			if (!updatingIntValue)
				this.updateFloatValueFromInts();
		});
		updateFloatValueFromInts();

	}

	protected void updateFloatValueFromInts() {
		int min = getMinimum();
		int value = getValue();
		int max = getMaximum();
		float f = ((float) value - min) / ((float) max - min);
		float newFloatValue = (1 - f) * floatMinimum + f * floatMaximum;
		if (newFloatValue != floatValue) {
			float oldFloatValue = floatValue;
			floatValue = newFloatValue;
			firePropertyChange("floatValue", oldFloatValue, newFloatValue);
		}
	}

	public float getFloatMinimum() {
		return floatMinimum;
	}

	public void setFloatMinimum(float floatMinimum) {
		this.floatMinimum = floatMinimum;
		this.updateIntValue();
	}

	public float getFloatMaximum() {
		return floatMaximum;
	}

	public void setFloatMaximum(float floatMaximum) {
		this.floatMaximum = floatMaximum;
		this.updateIntValue();
	}

	public float getFloatValue() {
		return floatValue;
	}

	public void setFloatValue(float value) {
		if (this.floatValue == value)
			return;
		this.floatValue = value;
		this.updateIntValue();
	}

	private void updateIntValue() {
		updatingIntValue = true;
		try {
			float f = (floatValue - floatMinimum) / (floatMaximum - floatMinimum);
			this.setValue((int) (getMinimum() * (1 - f) + getMaximum() * f));
		} finally {
			updatingIntValue = false;
		}

	}
}
