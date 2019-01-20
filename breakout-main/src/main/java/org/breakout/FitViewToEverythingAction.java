package org.breakout;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;

import org.andork.awt.I18n.Localizer;
import org.andork.swing.OnEDT;

@SuppressWarnings("serial")
public class FitViewToEverythingAction extends AbstractAction implements Action {
	BreakoutMainView mainView;

	public FitViewToEverythingAction(BreakoutMainView mainView) {
		super();
		this.mainView = mainView;

		new OnEDT() {
			@Override
			public void run() throws Throwable {
				Localizer localizer = mainView.getI18n().forClass(FitViewToEverythingAction.this.getClass());
				localizer.setName(FitViewToEverythingAction.this, "name");
			}
		};
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		mainView.fitViewToEverything();
	}

}
