package org.breakout;

import java.awt.Component;
import java.text.NumberFormat;
import java.text.ParseException;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

public class RawNumberCellRenderer extends DefaultTableCellRenderer {
	private static final long serialVersionUID = 3115504068270453371L;

	private final NumberFormat numberFormat;

	public RawNumberCellRenderer(NumberFormat format) {
		numberFormat = format;
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int column) {
		boolean invalid = false;
		if (value != null) {
			String str = "";
			try {
				str = value.toString();
				value = numberFormat.format(numberFormat.parse(str));
			} catch (ParseException e) {
				if (!str.isEmpty()) {
					invalid = true;
				}
			}
		}
		JLabel label = (JLabel) super.getTableCellRendererComponent(
				table, value, isSelected, hasFocus, row, column);
		label.setHorizontalAlignment(SwingConstants.RIGHT);
		return label;
	}
}
