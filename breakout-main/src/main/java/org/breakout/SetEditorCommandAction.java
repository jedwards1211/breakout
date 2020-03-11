package org.breakout;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import org.andork.awt.I18n.Localizer;
import org.andork.q.QObject;
import org.andork.swing.OnEDT;
import org.breakout.model.RootModel;

public class SetEditorCommandAction extends AbstractAction {
	private static final long serialVersionUID = 4944289444659593701L;

	BreakoutMainView mainView;
	Localizer localizer;

	public SetEditorCommandAction(BreakoutMainView mainView) {
		this.mainView = mainView;
		OnEDT.onEDT(() -> {
			localizer = mainView.getI18n().forClass(SetEditorCommandAction.this.getClass());
			localizer.setName(SetEditorCommandAction.this, "name");
		});
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		QObject<RootModel> rootModel = mainView.getRootModel();
		String command = rootModel.get(RootModel.editorCommand);
		String newCommand = JOptionPane.showInputDialog(localizer.getString("inputDialog.message"), command);
		if (newCommand != null) {
			rootModel.set(RootModel.editorCommand, newCommand);
		}
	}

}
