package org.andork.swing.list;

import org.andork.swing.RendererButtonModel;
import org.andork.swing.RendererButtonModel.RendererContext;

public class ListCellRendererButtonModelContext implements RendererContext {
	public ListCellRendererButtonModelContext(ListCellRendererRetargeter retargeter, ListCellRendererTracker tracker) {
		if (retargeter == null) {
			throw new IllegalArgumentException("retargeter must be non-null");
		}
		if (tracker == null) {
			throw new IllegalArgumentException("tracker must be non-null");
		}
		this.retargeter = retargeter;
		this.tracker = tracker;
	}

	ListCellRendererRetargeter	retargeter;
	ListCellRendererTracker	tracker;

	@Override
	public boolean canChangeButtonModelStateNow() {
		return retargeter.isAllowButtonChange();
	}

	@Override
	public boolean isRenderingPressCell() {
		return retargeter.getPressIndex() == tracker.getRendererIndex();
	}

	@Override
	public boolean isRenderingRolloverCell() {
		return retargeter.getRolloverIndex() == tracker.getRendererIndex();
	}
}
