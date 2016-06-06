package org.breakout;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import org.andork.q.QArrayList;
import org.andork.swing.list.QFileListEditor;
import org.breakout.model.ProjectModel;

@SuppressWarnings("serial")
public class EditSurveyScanPathsAction extends AbstractAction {

	private BreakoutMainView mainView;

	public EditSurveyScanPathsAction(BreakoutMainView mainView) {
		super("Edit Survey Scan Search Directories...");
		this.mainView = mainView;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		QArrayList<File> surveyScanPaths = mainView.getProjectModel().get(ProjectModel.surveyScanPaths);
		if (surveyScanPaths == null) {
			surveyScanPaths = new QArrayList<>();
			mainView.getProjectModel().set(ProjectModel.surveyScanPaths, surveyScanPaths);
		}
		QFileListEditor editor = new QFileListEditor();
		editor.setModel(surveyScanPaths);
		editor.setPreferredSize(new Dimension(600, 400));

		JOptionPane.showMessageDialog(mainView.getMainPanel(), editor, "Survey Scan Search Directories",
				JOptionPane.PLAIN_MESSAGE);

		editor.setModel(null);
	}
}
