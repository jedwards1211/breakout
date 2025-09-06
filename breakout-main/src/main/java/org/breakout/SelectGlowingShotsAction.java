package org.breakout;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.KeyStroke;

import org.andork.awt.I18n.Localizer;
import org.andork.swing.OnEDT;

@SuppressWarnings("serial")
public class SelectGlowingShotsAction extends AbstractAction {
	BreakoutMainView mainView;
	Variant variant;

	public static enum Variant {
		SET_SELECTION,
		ADD_TO_SELECTION,
		TOGGLE_SELECTION,
	}

	public SelectGlowingShotsAction(BreakoutMainView mainView, Variant variant) {
		super();
		this.mainView = mainView;
		this.variant = variant;

		new OnEDT() {
			@Override
			public void run() throws Throwable {
				Localizer localizer = mainView.getI18n().forClass(SelectGlowingShotsAction.this.getClass());
				switch (variant) {
				case SET_SELECTION:
					putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_H, 0));
					localizer.setName(SelectGlowingShotsAction.this, "name.set");
					break;
				case ADD_TO_SELECTION:
					putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_H, KeyEvent.SHIFT_DOWN_MASK));
					localizer.setName(SelectGlowingShotsAction.this, "name.add");
					break;
				case TOGGLE_SELECTION:
					putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_H, KeyEvent.CTRL_DOWN_MASK));
					localizer.setName(SelectGlowingShotsAction.this, "name.toggle");
					break;
				}
			}
		};
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		mainView
			.selectShots(
				mainView.model3d.getShotsWithGlow(),
				variant == Variant.ADD_TO_SELECTION,
				variant == Variant.TOGGLE_SELECTION);
	}

}
