package org.andork.swing;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;

import javax.swing.JList;
import javax.swing.ListModel;

public class DropLocationFinder implements DropTargetListener {
	Point	dropLocation	= null;

	public static int getCell(JList list, Point point) {
		ListModel model = list.getModel();
		for (int i = 0; i < model.getSize(); i++) {
			Rectangle bounds = list.getCellBounds(i, i);
			if (bounds.y + bounds.height >= point.y) {
				return i;
			}
		}
		return -1;
	}

	private Point getLocation(DropTargetEvent dte) {
		if (dte instanceof DropTargetDragEvent) {
			return ((DropTargetDragEvent) dte).getLocation();
		} else if (dte instanceof DropTargetDropEvent) {
			return ((DropTargetDropEvent) dte).getLocation();
		}
		return null;
	}

	public Point getDropLocation() {
		return new Point(dropLocation);
	}

	public void dragEnter(DropTargetDragEvent dtde) {
		dropLocation = getLocation(dtde);
	}

	public void dragOver(DropTargetDragEvent dtde) {
		dropLocation = getLocation(dtde);
	}

	public void dropActionChanged(DropTargetDragEvent dtde) {
	}

	public void dragExit(DropTargetEvent dte) {
		dropLocation = null;
	}

	public void drop(DropTargetDropEvent dtde) {
		dropLocation = getLocation(dtde);
	}

}
