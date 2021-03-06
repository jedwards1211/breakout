/*******************************************************************************
 * Breakout Cave Survey Visualizer
 *
 * Copyright (C) 2014 James Edwards
 *
 * jedwards8 at fastmail dot fm
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *******************************************************************************/
package org.andork.swing;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;

import javax.swing.ButtonGroup;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;

/**
 * Do what I want!!! This is to prevent the tracker button state from being
 * changed by the list rendering process calling removeAll(), and to prevent
 * rows from showing state for another row.
 *
 * @author andy.edwards
 */
public class RendererButtonModel implements javax.swing.ButtonModel {
	public static interface RendererContext {
		public boolean canChangeButtonModelStateNow();

		public boolean isRenderingPressCell();

		public boolean isRenderingRolloverCell();
	}

	protected String actionCommand;
	protected int mnemonic = 0;

	protected ButtonGroup group;
	protected boolean armed;
	protected boolean selected;
	protected boolean enabled = true;
	protected boolean pressed;

	protected boolean rollover;

	protected final RendererContext context;
	protected transient ChangeEvent changeEvent = null;

	protected final EventListenerList listenerList = new EventListenerList();

	public RendererButtonModel(RendererContext context) {
		super();
		this.context = context;
	}

	@Override
	public void addActionListener(ActionListener l) {
		listenerList.add(ActionListener.class, l);
	}

	@Override
	public void addChangeListener(ChangeListener l) {
		listenerList.add(ChangeListener.class, l);
	}

	@Override
	public void addItemListener(ItemListener l) {
		listenerList.add(ItemListener.class, l);
	}

	protected void fireActionEvent() {
		ActionEvent e = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, null);
		for (ActionListener listener : listenerList.getListeners(ActionListener.class)) {
			listener.actionPerformed(e);
		}
	}

	protected void fireStateChanged() {
		if (changeEvent == null) {
			changeEvent = new ChangeEvent(this);
		}
		for (ChangeListener listener : listenerList.getListeners(ChangeListener.class)) {
			listener.stateChanged(changeEvent);
		}
	}

	@Override
	public String getActionCommand() {
		return actionCommand;
	}

	@Override
	public int getMnemonic() {
		return mnemonic;
	}

	@Override
	public Object[] getSelectedObjects() {
		return null;
	}

	@Override
	public boolean isArmed() {
		return enabled && armed && context.isRenderingPressCell();
	}

	@Override
	public boolean isEnabled() {
		return enabled;
	}

	@Override
	public boolean isPressed() {
		return enabled && pressed && context.isRenderingPressCell();
	}

	@Override
	public boolean isRollover() {
		return enabled && rollover && context.isRenderingRolloverCell();
	}

	@Override
	public boolean isSelected() {
		return selected;
	}

	@Override
	public void removeActionListener(ActionListener l) {
		listenerList.remove(ActionListener.class, l);
	}

	@Override
	public void removeChangeListener(ChangeListener l) {
		listenerList.remove(ChangeListener.class, l);
	}

	@Override
	public void removeItemListener(ItemListener l) {
		listenerList.remove(ItemListener.class, l);
	}

	@Override
	public void setActionCommand(String s) {
		actionCommand = s;
	}

	@Override
	public void setArmed(boolean b) {
		if (context.canChangeButtonModelStateNow() && armed != b) {
			armed = b;
			fireStateChanged();
		}
	}

	@Override
	public void setEnabled(boolean b) {
		if (enabled != b) {
			enabled = b;
			fireStateChanged();
		}
	}

	@Override
	public void setGroup(ButtonGroup group) {
		this.group = group;
	}

	@Override
	public void setMnemonic(int key) {
		mnemonic = key;
		fireStateChanged();
	}

	@Override
	public void setPressed(boolean b) {
		if (context.canChangeButtonModelStateNow()) {
			if (b && !pressed) {
				pressed = true;
				fireStateChanged();
			}
			if (!b && pressed) {
				pressed = false;
				if (armed) {
					fireActionEvent();
				}
				fireStateChanged();
			}
		}
	}

	@Override
	public void setRollover(boolean b) {
		if (context.canChangeButtonModelStateNow()) {
			if (rollover != b) {
				rollover = b;
				fireStateChanged();
			}
		}
	}

	@Override
	public void setSelected(boolean b) {
		if (context.canChangeButtonModelStateNow() && selected != b) {
			selected = b;
			fireStateChanged();
		}
	}
}
