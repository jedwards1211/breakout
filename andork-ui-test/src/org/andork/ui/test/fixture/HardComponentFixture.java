package org.andork.ui.test.fixture;

import java.awt.Component;
import java.awt.event.MouseEvent;

import javax.swing.AbstractButton;

import org.andork.swing.DoSwing;
import org.andork.ui.test.DefaultGhost;
import org.andork.ui.test.Ghost;

public class HardComponentFixture implements ComponentFixture {
	private static Ghost	ghost	= new DefaultGhost();

	@Override
	public void click(final Component comp) {
		new DoSwing() {
			@Override
			public void run() {
				if (comp instanceof AbstractButton) {
					((AbstractButton) comp).doClick();
				} else {
					ghost.on(comp).click(MouseEvent.BUTTON1, 1);
				}
			}
		};
	}

}
