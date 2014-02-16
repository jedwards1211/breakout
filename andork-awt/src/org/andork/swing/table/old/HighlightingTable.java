package org.andork.swing.table.old;

import java.awt.Color;
import java.awt.Component;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

import org.andork.swing.table.old.FilteringTableModel.Filter;
import org.andork.swing.table.old.FilteringTableModel.HighlightingFilterResult;

@SuppressWarnings("serial")
public class HighlightingTable extends JTable {
	HighlightingTable() {
		super();
	}

	public HighlightingTable(TableModel dm) {
		super(dm);
	}

	public void setHighlightColors(Map<Filter, Color> highlightColors) {
		this.highlightColors.clear();
		this.highlightColors.putAll(highlightColors);
		repaint();
	}

	private final Map<Filter, Color>	highlightColors	= new HashMap<Filter, Color>();

	public Color getHighlightColor(Filter filter) {
		return highlightColors.get(filter);
	}

	@Override
	public Component prepareRenderer(TableCellRenderer renderer, int row,
			int column) {
		Component rend = super.prepareRenderer(renderer, row, column);
		if (getModel() instanceof FilteringTableModel) {
			FilteringTableModel model = (FilteringTableModel) getModel();
			if (!isRowSelected(row)) {
				rend.setBackground(getBackground());
				Object filterResult = model.getFilterResultForRow(row);
				if (filterResult != null
						&& filterResult instanceof HighlightingFilterResult) {
					HighlightingFilterResult hfr = (HighlightingFilterResult) filterResult;
					if (hfr.highlightingFilter != null) {
						rend.setBackground(highlightColors.get(hfr.highlightingFilter));
					}
				}
			}
		}
		return rend;
	}
}
