package org.breakout.compass.ui;

import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;

import org.andork.awt.GridBagWizard;
import org.andork.awt.I18n;
import org.andork.awt.I18n.I18nUpdater;
import org.andork.awt.I18n.Localizer;
import org.andork.compass.CompassParseError;
import org.andork.swing.OnEDT;
import org.breakout.SurveyTable;
import org.breakout.model.SurveyTableModel;

public class CompassParseResultsDialog extends JDialog {
	private static final long serialVersionUID = 1L;

	public static void main(String[] args) {
		OnEDT.onEDT(() -> {
			CompassParseResultsDialog dialog = new CompassParseResultsDialog(new I18n());
			dialog.setSize(Toolkit.getDefaultToolkit().getScreenSize());
			dialog.setVisible(true);
		});
	}

	CompassParseErrorsPane errorsPane;
	SurveyTable surveyTable;
	JScrollPane surveyTableScroller;
	JTabbedPane tabbedPane;
	JButton importAsNewProjectButton;
	JButton cancelButton;
	JButton addToCurrentProjectButton;

	final I18nUpdater<CompassParseResultsDialog> i18nUpdater = new I18nUpdater<CompassParseResultsDialog>() {
		@Override
		public void updateI18n(Localizer localizer, CompassParseResultsDialog localizedObject) {
			setTitle(localizer.getString("title"));
			tabbedPane.setTitleAt(0, localizer.getString("errorsTab.title"));
			tabbedPane.setTitleAt(1, localizer.getString("dataTab.title"));
			cancelButton.setText(localizer.getString("cancelButton.text"));
			importAsNewProjectButton.setText(localizer.getString("importAsNewProjectButton.text"));
			addToCurrentProjectButton.setText(localizer.getString("addToCurrentProjectButton.text"));
		}
	};

	public CompassParseResultsDialog(I18n i18n) {
		Localizer localizer = i18n.forClass(CompassParseResultsDialog.class);

		errorsPane = new CompassParseErrorsPane();
		surveyTable = new SurveyTable();
		surveyTable.setShowData(true);
		JScrollPane surveyTableScroller = new JScrollPane(surveyTable);
		tabbedPane = new JTabbedPane();
		tabbedPane.addTab("Errors", errorsPane);
		tabbedPane.addTab("Data", surveyTableScroller);

		cancelButton = new JButton();
		importAsNewProjectButton = new JButton();
		addToCurrentProjectButton = new JButton();

		Box buttonBox = Box.createHorizontalBox();
		buttonBox.add(Box.createGlue());
		buttonBox.add(cancelButton);
		buttonBox.add(importAsNewProjectButton);
		buttonBox.add(addToCurrentProjectButton);

		localizer.register(this, i18nUpdater);

		GridBagWizard w = GridBagWizard.create(getContentPane());
		w.put(tabbedPane, new JSeparator(), buttonBox).x(0).fillx(1.0).intoColumn();
		w.put(tabbedPane).fillboth(1.0, 1.0);
		w.put(buttonBox).insets(5, 0, 5, 0);

		cancelButton.addActionListener(e -> dispose());

		pack();
		setLocationRelativeTo(null);
	}

	public void onAddToCurrentProject(ActionListener listener) {
		addToCurrentProjectButton.addActionListener(listener);
	}

	public void onImportAsNewProject(ActionListener listener) {
		importAsNewProjectButton.addActionListener(listener);
	}

	public CompassParseResultsDialog setErrors(List<CompassParseError> errors) {
		errorsPane.setErrors(errors);
		return this;
	}

	public CompassParseResultsDialog setSurveyTableModel(SurveyTableModel model) {
		surveyTable.setModel(model);
		return this;
	}
}