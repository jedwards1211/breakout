package org.breakout;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.KeyStroke;

import org.andork.awt.I18n.Localizer;
import org.andork.awt.KeyEvents;
import org.andork.swing.JOptionPaneBuilder;
import org.andork.swing.OnEDT;

@SuppressWarnings("serial")
public class FindAction extends AbstractAction {
	BreakoutMainView mainView;

	public FindAction(BreakoutMainView mainView) {
		super();
		this.mainView = mainView;

		new OnEDT() {
			@Override
			public void run() throws Throwable {
				putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_F, KeyEvents.CTRL_OR_META_DOWN_MASK));
				Localizer localizer = mainView.getI18n().forClass(FindAction.this.getClass());
				localizer.setName(FindAction.this, "name");
			}
		};
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		Localizer localizer = mainView.getI18n().forClass(FindAction.this.getClass());

		Object query = new JOptionPaneBuilder()
			.message(localizer.getString("dialog.message"))
			.showInputDialog(mainView.getMainPanel(), localizer.getString("dialog.title"));
	
		if (query == null) return;
		
		mainView.surveyDrawer.searchField().textComponent.setText(query.toString());
		mainView.getSurveyTable().getAnnotatingRowSorter().invokeWhenDoneSorting(() -> {
			mainView.flyToFiltered(mainView.getSurveyTable());
		});
	}
}
