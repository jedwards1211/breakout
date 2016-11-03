package org.breakout.compass.ui;

import java.awt.Dimension;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import org.andork.awt.GridBagWizard;
import org.andork.compass.CompassParseError;
import org.andork.compass.CompassParseError.Severity;
import org.andork.segment.Segment;
import org.andork.swing.QuickTestFrame;
import org.andork.swing.table.ListTableModel;

@SuppressWarnings("serial")
public class CompassParseErrorsPane extends JSplitPane {
	public static void main(String[] args) {
		CompassParseErrorsPane pane = new CompassParseErrorsPane();

		List<CompassParseError> errors = new ArrayList<CompassParseError>();
		errors.add(new CompassParseError(Severity.ERROR, "this is a test",
				new Segment("blah blah blah", "file.txt", 0, 5)));
		errors.add(new CompassParseError(Severity.WARNING, "test 2",
				new Segment("blah blah blah", "fileb.txt", 2, 6)));
		pane.setErrors(errors);

		QuickTestFrame.frame(pane).setVisible(true);
	}

	final JLabel summaryLabel;
	final JTable table;
	final JScrollPane tableScroller;
	final JTextArea contextArea;

	final JScrollPane contextAreaScroller;

	public CompassParseErrorsPane() {
		super(JSplitPane.VERTICAL_SPLIT);

		summaryLabel = new JLabel();
		summaryLabel.setHorizontalAlignment(SwingConstants.LEADING);

		table = new JTable();
		table.setAutoCreateColumnsFromModel(false);
		table.setColumnModel(new CompassParseErrorTableColumnModel());
		tableScroller = new JScrollPane(table);
		GridBagWizard w = GridBagWizard.quickPanel();
		w.put(summaryLabel, tableScroller).x(0).fillx(1.0).intoColumn();
		w.put(tableScroller).fillboth(1.0, 1.0);
		w.getTarget().setPreferredSize(new Dimension(600, 200));
		add(w.getTarget(), JSplitPane.TOP);

		contextArea = new JTextArea();
		contextArea.setEditable(false);
		contextArea.setFont(Font.getFont("Monospaced"));
		contextAreaScroller = new JScrollPane(contextArea);
		contextAreaScroller.setPreferredSize(new Dimension(600, 300));
		add(contextAreaScroller, JSplitPane.BOTTOM);

		table.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (table.getSelectedRow() < 0) {
					contextArea.setText(null);
				} else {
					contextArea.setText(
							((CompassParseError) table.getValueAt(table.getSelectedRow(), 0))
									.getSegment().underlineInContext());
				}
			}
		});
	}

	public void setErrors(List<CompassParseError> errors) {
		if (errors == null) {
			table.setModel(new DefaultTableModel());
			summaryLabel.setText(null);
		}

		long numErrors = errors.stream().filter(e -> e.getSeverity() == Severity.ERROR).count();
		long numWarnings = errors.stream().filter(e -> e.getSeverity() == Severity.WARNING).count();
		summaryLabel.setText(new StringBuilder().append(numErrors).append(" Error")
				.append(numErrors != 1 ? "s, " : ", ").append(numWarnings).append(" Warning")
				.append(numWarnings != 1 ? "s" : "").toString());

		if (errors == ListTableModel.<CompassParseError> getList(table.getModel())) {
			return;
		}
		ListTableModel<CompassParseError> model = new ListTableModel<>(errors);
		model.setEditable(false);
		table.setModel(model);
	}
}
