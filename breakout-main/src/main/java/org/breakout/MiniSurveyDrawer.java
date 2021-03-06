package org.breakout;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.function.Consumer;

import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableColumnModel;

import org.andork.awt.GridBagWizard;
import org.andork.awt.I18n;
import org.andork.awt.layout.Drawer;
import org.andork.swing.TextComponentWithHintAndClear;
import org.andork.swing.table.AnnotatingJTable;
import org.andork.swing.table.AnnotatingTableRowSorter;
import org.andork.swing.table.DefaultAnnotatingJTableSetup;
import org.andork.swing.table.ListTableColumn;
import org.breakout.model.SurveyTableModel;
import org.breakout.model.SurveyTableModel.SurveyTableModelCopier;

public class MiniSurveyDrawer extends Drawer {
	private static final long serialVersionUID = -4890482486147486271L;

	private JPanel content;

	private AnnotatingJTable table;
	private DefaultAnnotatingJTableSetup tableSetup;

	private SearchOptionsButton searchOptionsButton;
	private TextComponentWithHintAndClear searchField;
	private JRadioButton filterButton;
	private JRadioButton highlightButton;

	private StatsPanel statsPanel;

	public MiniSurveyDrawer(I18n i18n, Consumer<Runnable> sortRunner) {
		initComponents(i18n, sortRunner);
		initLayout();
	}
	
	public SearchOptionsButton searchOptionsButton() {
		return searchOptionsButton;
	}

	public TextComponentWithHintAndClear searchField() {
		return searchField;
	}

	public JRadioButton filterButton() {
		return filterButton;
	}

	public JRadioButton highlightButton() {
		return highlightButton;
	}

	private void initComponents(I18n i18n, Consumer<Runnable> sortRunner) {
		content = new JPanel();

		DefaultTableColumnModel quickTableColumnModel = new DefaultTableColumnModel();
		SurveyTable.Columns columns = new SurveyTable.Columns();
		quickTableColumnModel.addColumn(columns.fromStation);
		quickTableColumnModel.addColumn(columns.toStation);

		table = new AnnotatingJTable(new SurveyTableModel(),
				quickTableColumnModel);
		ListTableColumn.updateModelIndices(table);

		tableSetup = new DefaultAnnotatingJTableSetup(table, sortRunner);
		((AnnotatingTableRowSorter<SurveyTableModel>) table.getAnnotatingRowSorter())
				.setModelCopier(new SurveyTableModelCopier());

		searchOptionsButton = new SearchOptionsButton(i18n);
		searchField = new TextComponentWithHintAndClear("Enter search terms");
		searchField.getAdornments().add(searchOptionsButton);
		highlightButton = new JRadioButton("Highlight");
		filterButton = new JRadioButton("Filter");

		ButtonGroup searchGroup = new ButtonGroup();
		searchGroup.add(highlightButton);
		searchGroup.add(filterButton);

		highlightButton.setSelected(true);

		statsPanel = new StatsPanel();
		statsPanel.setBorder(new EmptyBorder(5, 5, 5, 0));
	}

	private void initLayout() {
		add(content, BorderLayout.CENTER);

		setPreferredSize(new Dimension(250, 500));
		GridBagWizard gbw = GridBagWizard.create(content);
		gbw.put(searchField).xy(0, 0).width(2).fillx(1.0).insets(2, 2, 0, 0);
		gbw.put(pinButton()).rightOf(searchField).filly().insets(2, 0, 0, 0);
		gbw.put(highlightButton, filterButton).below(searchField).width(1).intoRow().insets(2, 2, 2, 0);
		gbw.put(tableSetup.scrollPane).below(highlightButton, pinButton())
				.fillboth(1.0, 1.0);
		gbw.put(statsPanel).below(tableSetup.scrollPane).fillx(1.0);
	}

	public StatsPanel statsPanel() {
		return statsPanel;
	}

	public AnnotatingJTable table() {
		return table;
	}

	public DefaultAnnotatingJTableSetup tableSetup() {
		return tableSetup;
	}
}
