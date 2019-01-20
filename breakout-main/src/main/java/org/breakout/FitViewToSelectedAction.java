package org.breakout;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;

import org.andork.awt.I18n.Localizer;
import org.andork.swing.OnEDT;

@SuppressWarnings("serial")
public class FitViewToSelectedAction extends AbstractAction implements Action {
	BreakoutMainView mainView;

	public FitViewToSelectedAction(BreakoutMainView mainView) {
		super();
		this.mainView = mainView;

		new OnEDT() {
			@Override
			public void run() throws Throwable {
				Localizer localizer = mainView.getI18n().forClass(FitViewToSelectedAction.this.getClass());
				localizer.setName(FitViewToSelectedAction.this, "name");
			}
		};
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		mainView.fitViewToSelected();
	}

}
