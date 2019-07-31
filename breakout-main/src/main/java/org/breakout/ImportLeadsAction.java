package org.breakout;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import org.andork.awt.I18n.Localizer;
import org.andork.swing.OnEDT;

public class ImportLeadsAction extends AbstractAction {
	private static final long serialVersionUID = 8950696926766549483L;

	BreakoutMainView mainView;

	public ImportLeadsAction(final BreakoutMainView mainView) {
		super();
		this.mainView = mainView;

		new OnEDT() {
			@Override
			public void run() throws Throwable {
				Localizer localizer = mainView.getI18n().forClass(ImportLeadsAction.this.getClass());
				localizer.setName(ImportLeadsAction.this, "name");
			}
		};
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		mainView.rebuildTaskService.submit(new ImportLeadsTask(mainView));
	}
}
