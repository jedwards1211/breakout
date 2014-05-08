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
	protected String					actionCommand;
	protected int						mnemonic		= 0;
	protected ButtonGroup				group;

	protected boolean					armed;
	protected boolean					selected;
	protected boolean					enabled			= true;
	protected boolean					pressed;
	protected boolean					rollover;

	protected final RendererContext		context;

	protected transient ChangeEvent		changeEvent		= null;
	protected final EventListenerList	listenerList	= new EventListenerList();

	public RendererButtonModel(RendererContext context) {
		super();
		this.context = context;
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
	public boolean isSelected() {
		return selected;
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
	public void setArmed(boolean b) {
		if (context.canChangeButtonModelStateNow() && armed != b) {
			armed = b;
			fireStateChanged();
		}
	}

	@Override
	public void setSelected(boolean b) {
		if (context.canChangeButtonModelStateNow() && selected != b) {
			selected = b;
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
	public void setMnemonic(int key) {
		mnemonic = key;
		fireStateChanged();
	}

	@Override
	public int getMnemonic() {
		return mnemonic;
	}

	@Override
	public void setActionCommand(String s) {
		actionCommand = s;
	}

	@Override
	public String getActionCommand() {
		return actionCommand;
	}

	@Override
	public void setGroup(ButtonGroup group) {
		this.group = group;
	}

	@Override
	public void addActionListener(ActionListener l) {
		listenerList.add(ActionListener.class, l);
	}

	@Override
	public void removeActionListener(ActionListener l) {
		listenerList.remove(ActionListener.class, l);
	}

	@Override
	public void addItemListener(ItemListener l) {
		listenerList.add(ItemListener.class, l);
	}

	@Override
	public void removeItemListener(ItemListener l) {
		listenerList.remove(ItemListener.class, l);
	}

	@Override
	public void addChangeListener(ChangeListener l) {
		listenerList.add(ChangeListener.class, l);
	}

	@Override
	public void removeChangeListener(ChangeListener l) {
		listenerList.remove(ChangeListener.class, l);
	}

	protected void fireStateChanged() {
		if (changeEvent == null) {
			changeEvent = new ChangeEvent(this);
		}
		for (ChangeListener listener : listenerList.getListeners(ChangeListener.class)) {
			listener.stateChanged(changeEvent);
		}
	}

	protected void fireActionEvent() {
		ActionEvent e = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, null);
		for (ActionListener listener : listenerList.getListeners(ActionListener.class)) {
			listener.actionPerformed(e);
		}
	}

	public static interface RendererContext {
		public boolean canChangeButtonModelStateNow();

		public boolean isRenderingPressCell();

		public boolean isRenderingRolloverCell();
	}
}