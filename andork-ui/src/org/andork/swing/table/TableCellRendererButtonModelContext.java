/*******************************************************************************
 * Breakout Cave Survey Visualizer
 *
 * Copyright (C) 2014 James Edwards
 *
 * jedwards8 at fastmail dot fm
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *******************************************************************************/
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
