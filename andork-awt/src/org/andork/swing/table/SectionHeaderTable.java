package org.andork.swing.table;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.plaf.UIResource;
import javax.swing.table.TableModel;

public class SectionHeaderTable extends JTable {
	private SectionHeaderModel			sectionHeaderModel		= null;
	private final SectionHeaderRowSet	sectionHeaders;
	private SectionHeaderRenderer		sectionHeaderRenderer	= new DefaultSectionHeaderRenderer();

	private final SectionHeaderPainter	sectionHeaderPainter	= new SectionHeaderPainter();

	public SectionHeaderTable() {
		this(null, null);
	}

	public SectionHeaderTable(TableModel dm) {
		this(dm, dm instanceof SectionHeaderModel ? (SectionHeaderModel) dm : null);
	}

	public SectionHeaderTable(TableModel dm, SectionHeaderModel sectionHeaderModel) {
		super(dm);
		sectionHeaderPainter.setPreferredSize(new Dimension(0, getRowHeight()));
		sectionHeaders = new SectionHeaderRowSet(getModel(), null);
		setSectionHeaderModel(sectionHeaderModel);
	}

	public void setModel(TableModel model) {
		super.setModel(model);
		if (sectionHeaders != null) {
			sectionHeaders.setTableModel(model);
		}
	}

	public void setSectionHeaderModel(SectionHeaderModel model) {
		if (sectionHeaders != null) {
			sectionHeaderModel = model;
			sectionHeaders.setSectionHeaderModel(model);
			repaint();
		}
	}

	public SectionHeaderModel getSectionHeaderModel() {
		return sectionHeaderModel;
	}

	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		paintSectionHeaders(g);
	}

	private void paintSectionHeaders(Graphics g) {
		Rectangle clip = g.getClipBounds();

		Rectangle bounds = getBounds();
		// account for the fact that the graphics has already been translated
		// into the table's bounds
		bounds.x = bounds.y = 0;

		if (getRowCount() <= 0 || getColumnCount() <= 0 ||
		// this check prevents us from painting the entire table
		// when the clip doesn't intersect our bounds at all
		!bounds.intersects(clip)) {
			return;
		}

		Point upperLeft = clip.getLocation();
		Point lowerRight = new Point(clip.x + clip.width - 1, clip.y + clip.height - 1);
		int rMin = rowAtPoint(upperLeft);
		int rMax = rowAtPoint(lowerRight);
		// This should never happen (as long as our bounds intersect the clip,
		// which is why we bail above if that is the case).
		if (rMin == -1) {
			rMin = 0;
		}
		// If the table does not have enough rows to fill the view we'll get -1.
		// (We could also get -1 if our bounds don't intersect the clip,
		// which is why we bail above if that is the case).
		// Replace this with the index of the last row.
		if (rMax == -1) {
			rMax = getRowCount() - 1;
		}

		if (sectionHeaderRenderer != null) {
			for (int row = rMin; row <= rMax; row++) {
				if (sectionHeaders.isSectionHeaderRow(row)) {
					Rectangle leftRect = getCellRect(row, 0, true);
					Rectangle rightRect = getCellRect(row, getColumnCount() - 1, true);
					Rectangle rowRect = leftRect.union(rightRect);

					sectionHeaderRenderer.paintSectionHeader(g, this, row, rowRect);
				}
			}
		}
	}

	private Rectangle getRowRect(int row, boolean includeSpacing) {
		int adjRow = Math.max(0, row);
		Rectangle leftRect = getCellRect(adjRow, 0, includeSpacing);
		Rectangle rightRect = getCellRect(adjRow, getColumnCount() - 1, includeSpacing);
		Rectangle rowRect = leftRect.union(rightRect);
		if (row < 0) {
			rowRect.y -= rowRect.height;
		}
		return rowRect;
	}

	/**
	 * If this <code>JTable</code> is the <code>viewportView</code> of an
	 * enclosing <code>JScrollPane</code> (the usual situation), configure this
	 * <code>ScrollPane</code> by, amongst other things, installing the table's
	 * <code>tableHeader</code> as the <code>columnHeaderView</code> of the
	 * scroll pane. When a <code>JTable</code> is added to a
	 * <code>JScrollPane</code> in the usual way, using
	 * <code>new JScrollPane(myTable)</code>, <code>addNotify</code> is called
	 * in the <code>JTable</code> (when the table is added to the viewport).
	 * <code>JTable</code>'s <code>addNotify</code> method in turn calls this
	 * method, which is protected so that this default installation procedure
	 * can be overridden by a subclass.
	 * 
	 * @see #addNotify
	 */
	protected void configureEnclosingScrollPane() {
		Container p = getParent();
		if (p instanceof JViewport) {
			Container gp = p.getParent();
			if (gp instanceof JScrollPane) {
				JScrollPane scrollPane = (JScrollPane) gp;
				// Make certain we are the viewPort's view and not, for
				// example, the rowHeaderView of the scrollPane -
				// an implementor of fixed columns might do this.
				JViewport viewport = scrollPane.getViewport();
				if (viewport == null || viewport.getView() != this) {
					return;
				}
				JPanel columnHeader = new JPanel(new BorderLayout());
				columnHeader.add(getTableHeader(), BorderLayout.NORTH);
				columnHeader.add(sectionHeaderPainter, BorderLayout.CENTER);
				scrollPane.setColumnHeaderView(columnHeader);
				// scrollPane.getViewport().setBackingStoreEnabled(true);
				Border border = scrollPane.getBorder();
				if (border == null || border instanceof UIResource) {
					scrollPane.setBorder(UIManager.getBorder("Table.scrollPaneBorder"));
				}
			}
		}
	}

	public static interface SectionHeaderRenderer {
		public void paintSectionHeader(Graphics g, SectionHeaderTable table, int row, Rectangle bounds);
	}

	public static class DefaultSectionHeaderRenderer extends JLabel implements SectionHeaderRenderer {
		public void paintSectionHeader(Graphics g, SectionHeaderTable table, int row, Rectangle bounds) {
			SectionHeaderModel model = table.getSectionHeaderModel();
			if (model == null) {
				return;
			}
			Object sectionHeader = model.getSectionHeader(row);
			if (sectionHeader == null) {
				return;
			}
			setText(sectionHeader.toString());
			setBackground(Color.GRAY);
			setSize(bounds.getSize());
			setOpaque(true);
			g.translate(bounds.x, bounds.y);
			paintComponent(g);
			g.translate(-bounds.x, -bounds.y);
		}
	}

	private class SectionHeaderPainter extends JComponent {
		@Override
		protected void paintComponent(Graphics g) {
			if (getRowCount() <= 0 || getColumnCount() <= 0 || sectionHeaderRenderer == null) {
				return;
			}

			Rectangle visible = SectionHeaderTable.this.getVisibleRect();

			Point upperLeft = visible.getLocation();
			Point lowerRight = new Point(visible.x + visible.width - 1, visible.y + visible.height - 1);
			int rMin = rowAtPoint(upperLeft);
			int rMax = rowAtPoint(lowerRight);
			// This should never happen (as long as our bounds intersect the
			// clip,
			// which is why we bail above if that is the case).
			if (rMin == -1) {
				rMin = 0;
			}
			// If the table does not have enough rows to fill the view we'll get
			// -1.
			// (We could also get -1 if our bounds don't intersect the clip,
			// which is why we bail above if that is the case).
			// Replace this with the index of the last row.
			if (rMax == -1) {
				rMax = getRowCount() - 1;
			}

			int headerRow = sectionHeaders.getSectionHeaderRowFor(rMin);

			Rectangle headerRect = SwingUtilities.convertRectangle(SectionHeaderTable.this, getRowRect(headerRow, true), this);

			if (headerRow == rMin) {
				int pushedHeaderRow = sectionHeaders.getSectionHeaderRowFor(headerRow - 1);

				Rectangle pushedHeaderRect = SwingUtilities.convertRectangle(SectionHeaderTable.this, getRowRect(pushedHeaderRow, true), this);
				pushedHeaderRect.y = headerRect.y - pushedHeaderRect.height;

				sectionHeaderRenderer.paintSectionHeader(g, SectionHeaderTable.this, pushedHeaderRow, pushedHeaderRect);
			} else {
				headerRect.y = 0;
			}

			sectionHeaderRenderer.paintSectionHeader(g, SectionHeaderTable.this, headerRow, headerRect);
		}
	}

}
