package org.andork.ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Paint;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import org.andork.ui.FilteringTableModel.HighlightingFilterResult;

@SuppressWarnings("serial")
public class HighlightingTableScroller extends JScrollPane {
	private HighlightingTable	table;
	private FilteringTableModel	filteringModel;
	private RepaintHandler		repaintHandler;

	public HighlightingTableScroller(HighlightingTable table) {
		super(table);
		this.table = table;
		repaintHandler = new RepaintHandler();
		repaintHandler.setModel(table.getModel());
		repaintHandler.setSelectionModel(table.getSelectionModel());
		table.addPropertyChangeListener("model", repaintHandler);
		table.addPropertyChangeListener("selectionModel", repaintHandler);
	}

	@Override
	public JScrollBar createVerticalScrollBar() {
		return new HighlightingScrollbar();
	}

	protected class HighlightingScrollbar extends ScrollBar {
		public HighlightingScrollbar() {
			super(JScrollBar.VERTICAL);
		}

		private Color getDarkerColor(Color c, float darkness, int alpha) {
			float[] hsb = new float[4];
			Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), hsb);
			hsb[2] = Math.max(0, hsb[2] - darkness);
			Color result = Color.getHSBColor(hsb[0], hsb[1], hsb[2]);
			return new Color(result.getRed(), result.getGreen(), result.getBlue(), alpha);
		}

		private Color getRowColor(int row) {
			if (table.isRowSelected(row)) {
				return table.getSelectionBackground();
			} else if (filteringModel != null) {
				Object filterResult = filteringModel.getFilterResultForRow(row);
				if (filterResult instanceof HighlightingFilterResult) {
					HighlightingFilterResult hfr = (HighlightingFilterResult) filterResult;
					if (hfr.highlightingFilter != null) {
						return table.getHighlightColor(hfr.highlightingFilter);
					}
				}
			}
			return null;
		}

		@Override
		public void paint(Graphics g) {
			super.paint(g);

			TableModel model = table.getModel();
			if (model instanceof FilteringTableModel) {
				filteringModel = (FilteringTableModel) model;
			} else {
				filteringModel = null;
			}

			Graphics2D g2 = (Graphics2D) g;
			Paint prevPaint = g2.getPaint();

			Insets insets = getInsets();
			int buttonSize = 20;
			int xPadding = 4;

			int scrollArea = getHeight() - insets.top - insets.bottom - buttonSize * 2;

			int markerWidth = getWidth() - insets.left - insets.right - xPadding * 2;

			for (int row = 0; row < table.getRowCount(); row++) {
				Color rowColor = getRowColor(row);

				if (rowColor != null) {
					int startY = buttonSize - 1 + row * scrollArea / table.getRowCount();

					while (row < table.getRowCount() - 1 && rowColor.equals(getRowColor(row + 1))) {
						row++;
					}

					int endY = buttonSize - 1 + (row + 1) * scrollArea / table.getRowCount();
					endY = Math.max(endY, startY + 3);

					g2.setColor(new Color(rowColor.getRed(), rowColor.getGreen(), rowColor.getBlue(), 64));
					g2.fillRect(insets.left + xPadding, startY, markerWidth, endY - startY);
					g2.setColor(getDarkerColor(rowColor, 0.3f, 128));
					g2.drawRect(insets.left + xPadding, startY, markerWidth, endY - startY);
				}
			}

			g2.setPaint(prevPaint);
		}
	}

	private class RepaintHandler implements PropertyChangeListener, TableModelListener, ListSelectionListener {
		private TableModel			model;
		private ListSelectionModel	selectionModel;

		public void setModel(TableModel model) {
			if (this.model != model) {
				if (this.model != null) {
					this.model.removeTableModelListener(this);
				}
				this.model = model;
				if (model != null) {
					model.addTableModelListener(this);
				}

				getVerticalScrollBar().repaint();
			}
		}

		public void setSelectionModel(ListSelectionModel selectionModel) {
			if (this.selectionModel != selectionModel) {
				if (this.selectionModel != null) {
					this.selectionModel.removeListSelectionListener(this);
				}
				this.selectionModel = selectionModel;
				if (selectionModel != null) {
					selectionModel.addListSelectionListener(this);
				}

				getVerticalScrollBar().repaint();
			}
		}

		@Override
		public void valueChanged(ListSelectionEvent e) {
			if (!e.getValueIsAdjusting()) {
				getVerticalScrollBar().repaint();
			}
		}

		@Override
		public void tableChanged(TableModelEvent e) {
			getVerticalScrollBar().repaint();
		}

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			if ("model".equals(evt.getPropertyName())) {
				setModel((TableModel) evt.getNewValue());
			} else if ("selectionModel".equals(evt.getPropertyName())) {
				setSelectionModel((ListSelectionModel) evt.getNewValue());
			}
		}
	}
}