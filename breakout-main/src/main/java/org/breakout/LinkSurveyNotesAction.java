package org.breakout;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import org.andork.awt.I18n.Localizer;
import org.andork.swing.OnEDT;

public class LinkSurveyNotesAction extends AbstractAction {
	private static final long serialVersionUID = 8950696926766549483L;

	BreakoutMainView mainView;

	public LinkSurveyNotesAction(final BreakoutMainView mainView) {
		super();
		this.mainView = mainView;

		new OnEDT() {
			@Override
			public void run() throws Throwable {
				Localizer localizer = mainView.getI18n().forClass(LinkSurveyNotesAction.this.getClass());
				localizer.setName(LinkSurveyNotesAction.this, "name");
			}
		};
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		mainView.rebuildTaskService.submit(new LinkSurveyNotesTask(mainView));
	}
}
