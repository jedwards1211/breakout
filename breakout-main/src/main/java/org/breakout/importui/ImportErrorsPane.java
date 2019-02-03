package org.breakout.importui;

import java.awt.Component;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import org.andork.awt.GridBagWizard;
import org.andork.awt.IconScaler;
import org.andork.segment.Segment;
import org.andork.swing.QuickTestFrame;
import org.andork.swing.table.ListTableColumn;
import org.andork.swing.table.ListTableModel;
import org.andork.swing.table.ListTableModel.Column;
import org.andork.swing.table.ListTableModel.ColumnBuilder;
import org.breakout.importui.ImportError.Severity;

@SuppressWarnings("serial")
public class ImportErrorsPane extends JSplitPane {
	private static final Logger logger = Logger.getLogger(ImportErrorsPane.class.getName());

	public static void main(String[] args) {
		ImportErrorsPane pane = new ImportErrorsPane();

		List<ImportError> errors = new ArrayList<ImportError>();
		errors.add(new ImportError(Severity.ERROR, "this is a test",
				new Segment("blah blah blah", "file.txt", 0, 5)));
		errors.add(new ImportError(Severity.WARNING, "test 2",
				new Segment("blah blah blah", "fileb.txt", 2, 6)));
		pane.setErrors(errors);

		QuickTestFrame.frame(pane).setVisible(true);
	}

	final JLabel summaryLabel;
	final JTable table;
	final JScrollPane tableScroller;
	final JTextArea contextArea;

	final JScrollPane contextAreaScroller;

	public ImportErrorsPane() {
		super(JSplitPane.VERTICAL_SPLIT);

		summaryLabel = new JLabel();
		summaryLabel.setHorizontalAlignment(SwingConstants.LEADING);

		table = new JTable();
		table.setAutoCreateColumnsFromModel(false);
		table.setColumnModel(new ErrorTableColumnModel());
		tableScroller = new JScrollPane(table);
		GridBagWizard w = GridBagWizard.quickPanel();
		w.put(summaryLabel, tableScroller).x(0).fillx(1.0).intoColumn();
		w.put(tableScroller).fillboth(1.0, 1.0);
		w.getTarget().setPreferredSize(new Dimension(600, 200));
		add(w.getTarget(), JSplitPane.TOP);

		contextArea = new JTextArea();
		contextArea.setEditable(false);
		contextArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 11));
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
					@SuppressWarnings("unchecked")
					ListTableModel<ImportError> model = (ListTableModel<ImportError>) table.getModel();
					contextArea.setText(
							model.getRowAt(table.getSelectedRow()).getSegment().underlineInContext());
				}
			}
		});
		table.addMouseListener(new MouseAdapter() {
			@SuppressWarnings("unchecked")
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() != 2) return;
				int row = table.rowAtPoint(e.getPoint());
				if (row < 0) return;
				
				row = table.convertRowIndexToModel(row);
				
				ListTableModel<ImportError> model = (ListTableModel<ImportError>) table.getModel();
				Object source = model.getRowAt(row).getSegment().source;
				if (source == null) return;
				File file = new File(String.valueOf(source));

				try {
					Desktop.getDesktop().open(file);
				} catch (Exception ex) {
					logger.log(Level.SEVERE, "Failed to open survey notes", ex);
					JOptionPane.showMessageDialog(null, "Failed to open file '" + file + "': " + ex,
							"Error", JOptionPane.ERROR_MESSAGE);
				}					
			}
		});
	}

	static class ErrorTableColumnModel extends DefaultTableColumnModel {
		public static class Columns {
			private static <V> ListTableColumn<ImportError, V> column(
					ListTableModel.Column<ImportError, V> modelColumn) {
				return new ListTableColumn<>(modelColumn);
			}

			public static ListTableColumn<ImportError, Severity> severity = column(ErrorTableColumns.severity)
					.headerValue("")
					.renderer(new DefaultTableCellRenderer() {
						final Map<String, ImageIcon> icons = new HashMap<>();

						private ImageIcon getIcon(String name, int height) {
							String key = name + "-" + height;
							if (!icons.containsKey(key)) {
								icons.put(key, IconScaler.rescale(UIManager.getIcon(name), height, height));
							}
							return icons.get(key);
						}

						@Override
						public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
								boolean hasFocus, int row, int column) {
							JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected,
									hasFocus,
									row, column);
							label.setIcon(getIcon(
									value == Severity.WARNING ? "OptionPane.warningIcon" : "OptionPane.errorIcon",
									table.getRowHeight(row)));
							label.setText(null);
							return label;
						}
					}).maxWidth(35);
			public static ListTableColumn<ImportError, String> message = column(ErrorTableColumns.message)
					.headerValue("Problem");
			public static ListTableColumn<ImportError, Object> source = column(ErrorTableColumns.source)
					.headerValue("File");
			public static ListTableColumn<ImportError, Integer> line = column(ErrorTableColumns.line)
					.headerValue("Line")
					.maxWidth(50)
					.renderer(numberCellRenderer);
			public static ListTableColumn<ImportError, Integer> col = column(ErrorTableColumns.col)
					.headerValue("Column")
					.maxWidth(50)
					.renderer(numberCellRenderer);
		}

		private static final TableCellRenderer numberCellRenderer = new DefaultTableCellRenderer();

		static {
			((JLabel) numberCellRenderer).setHorizontalAlignment(SwingConstants.RIGHT);
		}

		public ErrorTableColumnModel() {
			addColumn(Columns.severity);
			addColumn(Columns.message);
			addColumn(Columns.source);
			addColumn(Columns.line);
			addColumn(Columns.col);
		}
	}

	public static class ErrorTableColumns {
		public static final Column<ImportError, Severity> severity;
		public static final Column<ImportError, String> message;
		public static final Column<ImportError, Object> source;
		public static final Column<ImportError, Integer> line;
		public static final Column<ImportError, Integer> col;

		static <V> ColumnBuilder<ImportError, V> column(Class<V> columnClass) {
			return new ColumnBuilder<ImportError, V>().columnClass(columnClass);
		}

		static {
			severity = column(Severity.class).getter(e -> e.getSeverity()).create();
			message = column(String.class).getter(e -> e.getMessage()).create();
			source = column(Object.class).getter(e -> e.getSegment().source).create();
			line = column(Integer.class).getter(e -> e.getSegment().startLine + 1).create();
			col = column(Integer.class).getter(e -> e.getSegment().startCol + 1).create();
		}

		static List<Column<ImportError, ?>> list = Arrays.asList(
				severity, message, source, line, col);
	}

	public void setErrors(List<ImportError> errors) {
		if (errors == null) {
			table.setModel(new DefaultTableModel());
			summaryLabel.setText(null);
		}

		long numErrors = errors.stream().filter(e -> e.getSeverity() == Severity.ERROR).count();
		long numWarnings = errors.stream().filter(e -> e.getSeverity() == Severity.WARNING).count();
		summaryLabel.setText(new StringBuilder().append(numErrors).append(" Error")
				.append(numErrors != 1 ? "s, " : ", ").append(numWarnings).append(" Warning")
				.append(numWarnings != 1 ? "s" : "").toString());

		if (errors == ListTableModel.<ImportError> getList(table.getModel())) {
			return;
		}
		ListTableModel<ImportError> model = new ListTableModel<>(ErrorTableColumns.list, errors);
		model.setEditable(false);
		table.setModel(model);
		ListTableColumn.updateModelIndices(table);
	}
}
