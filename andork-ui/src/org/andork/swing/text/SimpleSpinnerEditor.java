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
package org.andork.swing.text;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * A simple base class for more specialized editors
 * that displays a read-only view of the model's current
 * value with a <code>JTextField<code>.  Subclasses
 * can configure the <code>JTextField<code> to create
 * an editor that's appropriate for the type of model they
 * support and they may want to override
 * the <code>stateChanged</code> and <code>propertyChanged</code> methods, which keep the model and the text field in sync.
 * <p>
 * This class defines a <code>dismiss</code> method that removes the editors <code>ChangeListener</code> from the <code>JSpinner</code> that it's part of. The <code>setEditor</code> method knows about <code>DefaultEditor.dismiss</code>, so if the developer replaces an editor that's derived from <code>JSpinner.DefaultEditor</code> its <code>ChangeListener</code> connection back to the <code>JSpinner</code> will be removed. However after that, it's up to the developer to manage their editor listeners. Similarly, if a subclass overrides <code>createEditor</code>, it's up to the subclasser to deal with their editor subsequently being replaced (with <code>setEditor</code>). We expect that in most cases, and in editor installed with <code>setEditor</code> or created by a <code>createEditor</code> override, will not be replaced anyway.
 * <p>
 * This class is the <code>LayoutManager<code> for it's single
 * <code>JTextField</code> child. By default the child is just centered with the parents insets.
 */
public class SimpleSpinnerEditor extends JPanel
		implements ChangeListener, PropertyChangeListener, LayoutManager
{
	private static final Action	DISABLED_ACTION	= new DisabledAction();

	private boolean				updatingField;

	/**
	 * Constructs an editor component for the specified <code>JSpinner</code>.
	 * This <code>DefaultEditor</code> is it's own layout manager and
	 * it is added to the spinner's <code>ChangeListener</code> list.
	 * The constructor creates a single <code>JTextField<code> child,
	 * initializes it's value to be the spinner model's current value
	 * and adds it to <code>this</code> <code>DefaultEditor</code>.
	 * 
	 * @param spinner
	 *            the spinner whose model <code>this</code> editor will monitor
	 * @see #getTextField
	 * @see JSpinner#addChangeListener
	 */
	public SimpleSpinnerEditor(JSpinner spinner) {
		super(null);

		JTextField ftf = new JTextField();
		ftf.setName("Spinner.textField");
		ftf.putClientProperty("value", spinner.getValue());
		ftf.addPropertyChangeListener(this);
		ftf.setHorizontalAlignment(JTextField.RIGHT);

		String toolTipText = spinner.getToolTipText();
		if (toolTipText != null) {
			ftf.setToolTipText(toolTipText);
		}

		add(ftf);

		setLayout(this);
		spinner.addChangeListener(this);

		// We want the spinner's increment/decrement actions to be
		// active vs those of the JTextField. As such we
		// put disabled actions in the JTextField's actionmap.
		// A binding to a disabled action is treated as a nonexistant
		// binding.
		ActionMap ftfMap = ftf.getActionMap();

		if (ftfMap != null) {
			ftfMap.put("increment", DISABLED_ACTION);
			ftfMap.put("decrement", DISABLED_ACTION);
		}
	}

	/**
	 * Disconnect <code>this</code> editor from the specified <code>JSpinner</code>. By default, this method removes
	 * itself from the spinners <code>ChangeListener</code> list.
	 * 
	 * @param spinner
	 *            the <code>JSpinner</code> to disconnect this
	 *            editor from; the same spinner as was passed to the constructor.
	 */
	public void dismiss(JSpinner spinner) {
		spinner.removeChangeListener(this);
	}

	/**
	 * Returns the <code>JSpinner</code> ancestor of this editor or null.
	 * Typically the editor's parent is a <code>JSpinner</code> however
	 * subclasses of <codeJSpinner</code> may override the
	 * the <code>createEditor</code> method and insert one or more containers
	 * between the <code>JSpinner</code> and it's editor.
	 * 
	 * @return <code>JSpinner</code> ancestor
	 * @see JSpinner#createEditor
	 */
	public JSpinner getSpinner() {
		for (Component c = this; c != null; c = c.getParent()) {
			if (c instanceof JSpinner) {
				return (JSpinner) c;
			}
		}
		return null;
	}

	/**
	 * Returns the <code>JTextField</code> child of this
	 * editor. By default the text field is the first and only
	 * child of editor.
	 * 
	 * @return the <code>JTextField</code> that gives the user
	 *         access to the <code>SpinnerDateModel's</code> value.
	 * @see #getSpinner
	 * @see #getModel
	 */
	public JTextField getTextField() {
		return (JTextField) getComponent(0);
	}
	
	private void updateField(Object value) {
		updatingField = true;
		try {
			getTextField().putClientProperty("value", value);
		} finally {
			updatingField = false;
		}
	}

	/**
	 * This method is called when the spinner's model's state changes.
	 * It sets the <code>value</code> of the text field to the current
	 * value of the spinners model.
	 * 
	 * @param e
	 *            not used
	 * @see #getTextField
	 * @see JSpinner#getValue
	 */
	public void stateChanged(ChangeEvent e) {
		JSpinner spinner = (JSpinner) (e.getSource());
		updateField(spinner.getValue());
	}

	/**
	 * Called by the <code>JTextField</code> <code>PropertyChangeListener</code>. When the <code>"value"</code> property changes, which implies that the user has typed a new
	 * number, we set the value of the spinners model.
	 * <p>
	 * This class ignores <code>PropertyChangeEvents</code> whose source is not the <code>JTextField</code>, so subclasses may safely make <code>this</code> <code>DefaultEditor</code> a <code>PropertyChangeListener</code> on other objects.
	 * 
	 * @param e
	 *            the <code>PropertyChangeEvent</code> whose source is
	 *            the <code>JTextField</code> created by this class.
	 * @see #getTextField
	 */
	public void propertyChange(PropertyChangeEvent e)
	{
		if (updatingField) {
			return;
		}

		JSpinner spinner = getSpinner();

		if (spinner == null) {
			// Indicates we aren't installed anywhere.
			return;
		}

		Object source = e.getSource();
		String name = e.getPropertyName();
		if ((source instanceof JTextField) && "value".equals(name)) {
			Object lastValue = spinner.getValue();

			// Try to set the new value
			try {
				spinner.setValue(getTextField().getClientProperty("value"));
			} catch (IllegalArgumentException iae) {
				// SpinnerModel didn't like new value, reset
				try {
					updateField(lastValue);
				} catch (IllegalArgumentException iae2) {
					// Still bogus, nothing else we can do, the
					// SpinnerModel and JTextField are now out
					// of sync.
				}
			}
		}
	}

	/**
	 * This <code>LayoutManager</code> method does nothing. We're
	 * only managing a single child and there's no support
	 * for layout constraints.
	 * 
	 * @param name
	 *            ignored
	 * @param child
	 *            ignored
	 */
	public void addLayoutComponent(String name, Component child) {}

	/**
	 * This <code>LayoutManager</code> method does nothing. There
	 * isn't any per-child state.
	 * 
	 * @param child
	 *            ignored
	 */
	public void removeLayoutComponent(Component child) {}

	/**
	 * Returns the size of the parents insets.
	 */
	private Dimension insetSize(Container parent) {
		Insets insets = parent.getInsets();
		int w = insets.left + insets.right;
		int h = insets.top + insets.bottom;
		return new Dimension(w, h);
	}

	/**
	 * Returns the preferred size of first (and only) child plus the
	 * size of the parents insets.
	 * 
	 * @param parent
	 *            the Container that's managing the layout
	 * @return the preferred dimensions to lay out the subcomponents
	 *         of the specified container.
	 */
	public Dimension preferredLayoutSize(Container parent) {
		Dimension preferredSize = insetSize(parent);
		if (parent.getComponentCount() > 0) {
			Dimension childSize = getComponent(0).getPreferredSize();
			preferredSize.width += childSize.width;
			preferredSize.height += childSize.height;
		}
		return preferredSize;
	}

	/**
	 * Returns the minimum size of first (and only) child plus the
	 * size of the parents insets.
	 * 
	 * @param parent
	 *            the Container that's managing the layout
	 * @return the minimum dimensions needed to lay out the subcomponents
	 *         of the specified container.
	 */
	public Dimension minimumLayoutSize(Container parent) {
		Dimension minimumSize = insetSize(parent);
		if (parent.getComponentCount() > 0) {
			Dimension childSize = getComponent(0).getMinimumSize();
			minimumSize.width += childSize.width;
			minimumSize.height += childSize.height;
		}
		return minimumSize;
	}

	/**
	 * Resize the one (and only) child to completely fill the area
	 * within the parents insets.
	 */
	public void layoutContainer(Container parent) {
		if (parent.getComponentCount() > 0) {
			Insets insets = parent.getInsets();
			int w = parent.getWidth() - (insets.left + insets.right);
			int h = parent.getHeight() - (insets.top + insets.bottom);
			getComponent(0).setBounds(insets.left, insets.top, w, h);
		}
	}

	/**
	 * An Action implementation that is always disabled.
	 */
	private static class DisabledAction implements Action {
		public Object getValue(String key) {
			return null;
		}

		public void putValue(String key, Object value) {}

		public void setEnabled(boolean b) {}

		public boolean isEnabled() {
			return false;
		}

		public void addPropertyChangeListener(PropertyChangeListener l) {}

		public void removePropertyChangeListener(PropertyChangeListener l) {}

		public void actionPerformed(ActionEvent ae) {}
	}
}
