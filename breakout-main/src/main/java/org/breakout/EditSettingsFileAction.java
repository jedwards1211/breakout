package org.breakout;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import org.andork.awt.I18n.Localizer;
import org.andork.swing.OnEDT;

public class EditSettingsFileAction extends AbstractAction {
	private static final long serialVersionUID = 4944289444659593701L;

	BreakoutMainView mainView;

	public EditSettingsFileAction(BreakoutMainView mainView) {
		this.mainView = mainView;
		OnEDT.onEDT(() -> {
			Localizer localizer = mainView.getI18n().forClass(EditSettingsFileAction.this.getClass());
			localizer.setName(EditSettingsFileAction.this, "name");
		});
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		mainView.openEditor(BreakoutMain.getRootSettingsFile());
	}

}
