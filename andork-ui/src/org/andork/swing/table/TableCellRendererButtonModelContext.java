package org.andork.swing.table;

import org.andork.swing.RendererButtonModel;
import org.andork.swing.RendererButtonModel.RendererContext;

public class TableCellRendererButtonModelContext implements RendererContext {
	public TableCellRendererButtonModelContext(TableCellRendererRetargeter retargeter, TableCellRendererTracker tracker) {
		if (retargeter == null) {
			throw new IllegalArgumentException("retargeter must be non-null");
		}
		if (tracker == null) {
			throw new IllegalArgumentException("tracker must be non-null");
		}
		this.retargeter = retargeter;
		this.tracker = tracker;
	}

	TableCellRendererRetargeter	retargeter;
	TableCellRendererTracker	tracker;

	@Override
	public boolean canChangeButtonModelStateNow() {
		return retargeter.isAllowButtonChange();
	}

	@Override
	public boolean isRenderingPressCell() {
		return retargeter.getPressRow() == tracker.getRendererRow() &&
				retargeter.getPressColumn() == tracker.getRendererColumn();
	}

	@Override
	public boolean isRenderingRolloverCell() {
		return retargeter.getRolloverRow() == tracker.getRendererRow() &&
				retargeter.getRolloverColumn() == tracker.getRendererColumn();
	}
}
