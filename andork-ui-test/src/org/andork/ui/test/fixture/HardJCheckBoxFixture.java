package org.andork.ui.test.fixture;

import javax.swing.JCheckBox;

import org.andork.swing.DoSwing;

public class HardJCheckBoxFixture extends HardComponentFixture implements JCheckBoxFixture {
	@Override
	public void setSelected(final JCheckBox cb, final boolean selected) {
		new DoSwing() {
			@Override
			public void run() {
				cb.setSelected(selected);
			}
		};
	}
}
