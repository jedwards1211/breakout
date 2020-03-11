package org.breakout;

import java.awt.event.ActionEvent;
import java.nio.file.Path;

import javax.swing.AbstractAction;

import org.andork.awt.I18n.Localizer;
import org.andork.swing.OnEDT;

public class EditSwapFileAction extends AbstractAction {
	private static final long serialVersionUID = 4944289444659593701L;

	BreakoutMainView mainView;

	public EditSwapFileAction(BreakoutMainView mainView) {
		this.mainView = mainView;
		OnEDT.onEDT(() -> {
			Localizer localizer = mainView.getI18n().forClass(EditSwapFileAction.this.getClass());
			localizer.setName(EditSwapFileAction.this, "name");
		});
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Path swapFile = mainView.getCurrentSwapFile();
		if (swapFile != null) {
			mainView.openEditor(swapFile);
		}
	}

}
