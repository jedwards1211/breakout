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
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import org.andork.awt.GridBagWizard;
import org.andork.awt.I18n;
import org.andork.awt.I18n.I18nUpdater;
import org.andork.awt.I18n.Localizer;
import org.andork.compass.CompassParseError;
import org.andork.swing.OnEDT;
import org.breakout.SurveyTable;
import org.breakout.SurveyTable.Aspect;
import org.breakout.model.SurveyTableModel;

public class CompassPlotParseResultsDialog extends JDialog {
	private static final long serialVersionUID = 1L;

	public static void main(String[] args) {
		OnEDT.onEDT(() -> {
			CompassPlotParseResultsDialog dialog = new CompassPlotParseResultsDialog(new I18n());
			dialog.setSize(Toolkit.getDefaultToolkit().getScreenSize());
			dialog.setVisible(true);
		});
	}

	CompassParseErrorsPane errorsPane;
	SurveyTable surveyTable;
	JScrollPane surveyTableScroller;
	JTabbedPane tabbedPane;
	JButton importButton;
	JButton cancelButton;

	final I18nUpdater<CompassPlotParseResultsDialog> i18nUpdater = new I18nUpdater<CompassPlotParseResultsDialog>() {
		@Override
		public void updateI18n(Localizer localizer, CompassPlotParseResultsDialog localizedObject) {
			setTitle(localizer.getString("title"));
			tabbedPane.setTitleAt(0, localizer.getString("errorsTab.title"));
			tabbedPane.setTitleAt(1, localizer.getString("dataTab.title"));
			cancelButton.setText(localizer.getString("cancelButton.text"));
			importButton.setText(localizer.getString("importButton.text"));
		}
	};

	@SuppressWarnings("serial")
	public CompassPlotParseResultsDialog(I18n i18n) {
		Localizer localizer = i18n.forClass(CompassPlotParseResultsDialog.class);

		errorsPane = new CompassParseErrorsPane();
		surveyTable = new SurveyTable() {
			@Override
			public void createDefaultColumnsFromModel() {
				TableModel m = getModel();
				if (m != null) {
					// Remove any current columns
					TableColumnModel cm = getColumnModel();
					while (cm.getColumnCount() > 0) {
						cm.removeColumn(cm.getColumn(0));
					}

					columns = new Columns();

					addColumn(columns.fromCave);
					addColumn(columns.fromStation);
					addColumn(columns.northing);
					addColumn(columns.easting);
					addColumn(columns.elevation);
				}
			}
		};
		surveyTable.setAspect(Aspect.NEV);
		JScrollPane surveyTableScroller = new JScrollPane(surveyTable);
		tabbedPane = new JTabbedPane();
		tabbedPane.addTab("Errors", errorsPane);
		tabbedPane.addTab("Data", surveyTableScroller);

		cancelButton = new JButton();
		importButton = new JButton();

		Box buttonBox = Box.createHorizontalBox();
		buttonBox.add(Box.createGlue());
		buttonBox.add(cancelButton);
		buttonBox.add(importButton);

		localizer.register(this, i18nUpdater);

		GridBagWizard w = GridBagWizard.create(getContentPane());
		w.put(tabbedPane, new JSeparator(), buttonBox).x(0).fillx(1.0).intoColumn();
		w.put(tabbedPane).fillboth(1.0, 1.0);
		w.put(buttonBox).insets(5, 0, 5, 0);

		cancelButton.addActionListener(e -> dispose());

		pack();
		setLocationRelativeTo(null);
	}

	public void onImport(ActionListener listener) {
		importButton.addActionListener(listener);
	}

	public CompassPlotParseResultsDialog setErrors(List<CompassParseError> errors) {
		errorsPane.setErrors(errors);
		return this;
	}

	public CompassPlotParseResultsDialog setSurveyTableModel(SurveyTableModel model) {
		surveyTable.setModel(model);
		return this;
	}
}
