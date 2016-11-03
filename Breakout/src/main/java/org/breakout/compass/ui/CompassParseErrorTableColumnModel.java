package org.breakout.compass.ui;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableCellRenderer;

import org.andork.compass.CompassParseError;
import org.andork.compass.CompassParseError.Severity;
import org.andork.swing.table.ListTableColumn;

@SuppressWarnings("serial")
public class CompassParseErrorTableColumnModel extends DefaultTableColumnModel {
	public static class Columns {
		public static ListTableColumn<CompassParseError, CompassParseError.Severity> severity = new ListTableColumn<CompassParseError, CompassParseError.Severity>()
				.getter(error -> error.getSeverity()).renderer(new DefaultTableCellRenderer() {
					@Override
					public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
							boolean hasFocus, int row, int column) {
						JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus,
								row, column);
						label.setIcon(UIManager.getIcon(
								value == Severity.WARNING ? "OptionPane.warningIcon" : "OptionPane.errorIcon"));
						return label;
					}
				}).maxWidth(35);
		public static ListTableColumn<CompassParseError, String> message = new ListTableColumn<CompassParseError, String>()
				.headerValue("Problem")
				.getter(error -> error.getMessage());
		public static ListTableColumn<CompassParseError, Object> source = new ListTableColumn<CompassParseError, Object>()
				.headerValue("File")
				.getter(error -> error.getSegment().source);
		public static ListTableColumn<CompassParseError, Integer> line = new ListTableColumn<CompassParseError, Integer>()
				.headerValue("Line")
				.maxWidth(50)
				.renderer(numberCellRenderer)
				.getter(error -> error.getSegment().startLine);
		public static ListTableColumn<CompassParseError, Integer> col = new ListTableColumn<CompassParseError, Integer>()
				.headerValue("Column")
				.maxWidth(50)
				.renderer(numberCellRenderer)
				.getter(error -> error.getSegment().startCol);
	}

	private static final TableCellRenderer numberCellRenderer = new DefaultTableCellRenderer();

	static {
		((JLabel) numberCellRenderer).setHorizontalAlignment(SwingConstants.RIGHT);
	}

	public CompassParseErrorTableColumnModel() {
		addColumn(Columns.severity);
		addColumn(Columns.message);
		addColumn(Columns.source);
		addColumn(Columns.line);
		addColumn(Columns.col);
	}
}
