package org.breakout.importui;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.List;

import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;

import org.andork.awt.GridBagWizard;
import org.andork.awt.I18n;
import org.andork.awt.I18n.I18nUpdater;
import org.andork.awt.I18n.Localizer;
import org.andork.swing.OnEDT;
import org.breakout.SurveyTable;
import org.breakout.SurveyTable.Aspect;
import org.breakout.model.SurveyTableModel;

public class ImportResultsDialog extends JDialog {
	private static final long serialVersionUID = 1L;

	public static void main(String[] args) {
		OnEDT.onEDT(() -> {
			ImportResultsDialog dialog = new ImportResultsDialog(null, new I18n(), "title.compass");
			dialog.setSize(Toolkit.getDefaultToolkit().getScreenSize());
			dialog.setVisible(true);
		});
	}

	final String titleKey;
	ImportErrorsPane errorsPane;
	SurveyTable surveyTable;
	JScrollPane surveyTableScroller;
	JTabbedPane tabbedPane;
	JButton cancelButton;
	JButton importButton;

	JRadioButton shotsButton;
	JRadioButton nevButton;

	final I18nUpdater<ImportResultsDialog> i18nUpdater = new I18nUpdater<ImportResultsDialog>() {
		@Override
		public void updateI18n(Localizer localizer, ImportResultsDialog localizedObject) {
			setTitle(localizer.getString(titleKey));
			tabbedPane.setTitleAt(0, localizer.getString("errorsTab.title"));
			tabbedPane.setTitleAt(1, localizer.getString("dataTab.title"));
			cancelButton.setText(localizer.getString("cancelButton.text"));
			importButton.setText(localizer.getString("addToCurrentProjectButton.text"));
		}
	};

	public ImportResultsDialog(Window owner, I18n i18n, String titleKey) {
		super(owner);
		Localizer localizer = i18n.forClass(ImportResultsDialog.class);
		this.titleKey = titleKey;

		errorsPane = new ImportErrorsPane();
		surveyTable = new SurveyTable();
		surveyTable.setAspect(Aspect.SHOTS);
		JScrollPane surveyTableScroller = new JScrollPane(surveyTable);
		tabbedPane = new JTabbedPane();
		tabbedPane.addTab("Errors", errorsPane);

		cancelButton = new JButton();
		importButton = new JButton();

		shotsButton = new JRadioButton("Shots");
		shotsButton.setSelected(true);
		nevButton = new JRadioButton("NEV");
		ButtonGroup aspectGroup = new ButtonGroup();
		aspectGroup.add(shotsButton);
		aspectGroup.add(nevButton);
		Box aspectBox = Box.createHorizontalBox();
		aspectBox.add(Box.createGlue());
		aspectBox.add(shotsButton);
		aspectBox.add(nevButton);

		ItemListener aspectListener = e -> {
			if (e.getStateChange() != ItemEvent.SELECTED) {
				return;
			}
			surveyTable.setAspect(e.getSource() == nevButton ? Aspect.NEV : Aspect.SHOTS);
		};
		shotsButton.addItemListener(aspectListener);
		nevButton.addItemListener(aspectListener);

		JPanel dataPanel = new JPanel();
		GridBagWizard w = GridBagWizard.create(dataPanel);
		w.put(aspectBox, surveyTableScroller).fillx(1.0).intoColumn();
		w.put(surveyTableScroller).fillboth(1.0, 1.0);
		tabbedPane.addTab("Data", dataPanel);

		Box buttonBox = Box.createHorizontalBox();
		buttonBox.add(Box.createGlue());
		buttonBox.add(cancelButton);
		buttonBox.add(importButton);

		localizer.register(this, i18nUpdater);

		w = GridBagWizard.create(getContentPane());
		w.put(tabbedPane, new JSeparator(), buttonBox).x(0).fillx(1.0).intoColumn();
		w.put(tabbedPane).fillboth(1.0, 1.0);
		w.put(buttonBox).insets(5, 0, 5, 0);

		cancelButton.addActionListener(e -> dispose());

		pack();
		Dimension screensize = Toolkit.getDefaultToolkit().getScreenSize();
		setSize(new Dimension(screensize.width * 4 / 5, screensize.height * 4 / 5));
		setLocationRelativeTo(null);
	}

	public void onImport(ActionListener listener) {
		importButton.addActionListener(listener);
	}

	public ImportResultsDialog setErrors(List<ImportError> errors) {
		errorsPane.setErrors(errors);
		return this;
	}

	public ImportResultsDialog setSurveyTableModel(SurveyTableModel model) {
		surveyTable.setModel(model);
		return this;
	}
}
