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
package org.andork.awt.layout;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager2;
import java.util.Vector;

/**
 * A <code>BetterCardLayout</code> object is a layout manager for a container.
 * It treats each component in the container as a card. Only one card is visible
 * at a time, and the container acts as a stack of cards. The first component
 * added to a <code>BetterCardLayout</code> object is the visible component when
 * the container is first displayed.
 * <p>
 * The ordering of cards is determined by the container's own internal ordering
 * of its component objects. <code>BetterCardLayout</code> defines a set of
 * methods that allow an application to flip through these cards sequentially,
 * or to show a specified card. The {@link BetterCardLayout#addLayoutComponent}
 * method can be used to associate a string identifier with a given card for
 * fast random access.
 *
 * @version %I% %G%
 * @author Arthur van Hoff
 * @see java.awt.Container
 * @since JDK1.0
 */

public class BetterCardLayout implements LayoutManager2 {
	/*
	 * A pair of Component and String that represents its name.
	 */
	class Card {
		public Object key;
		public Component comp;

		public Card(Object cardKey, Component cardComponent) {
			key = cardKey;
			comp = cardComponent;
		}
	}

	/*
	 * This creates a Vector to store associated pairs of components and their
	 * names.
	 *
	 * @see java.util.Vector
	 */
	Vector<Card> vector = new Vector<Card>();

	private boolean sizeHidden = true;

	/*
	 * Index of Component currently displayed by BetterCardLayout.
	 */
	int currentCard = 0;

	/*
	 * A cards horizontal Layout gap (inset). It specifies the space between the
	 * left and right edges of a container and the current component. This
	 * should be a non negative Integer.
	 *
	 * @see getHgap()
	 *
	 * @see setHgap()
	 */
	int hgap;

	/*
	 * A cards vertical Layout gap (inset). It specifies the space between the
	 * top and bottom edges of a container and the current component. This
	 * should be a non negative Integer.
	 *
	 * @see getVgap()
	 *
	 * @see setVgap()
	 */
	int vgap;

	/**
	 * Creates a new card layout with gaps of size zero.
	 */
	public BetterCardLayout() {
		this(0, 0);
	}

	public BetterCardLayout(boolean sizeHidden) {
		super();
		this.sizeHidden = sizeHidden;
	}

	/**
	 * Creates a new card layout with the specified horizontal and vertical
	 * gaps. The horizontal gaps are placed at the left and right edges. The
	 * vertical gaps are placed at the top and bottom edges.
	 *
	 * @param hgap
	 *            the horizontal gap.
	 * @param vgap
	 *            the vertical gap.
	 */
	public BetterCardLayout(int hgap, int vgap) {
		this.hgap = hgap;
		this.vgap = vgap;
	}

	/**
	 * Adds the specified component to this card layout's internal table of
	 * names. The object specified by <code>constraints</code> must be a string.
	 * The card layout stores this string as a key-value pair that can be used
	 * for random access to a particular card. By calling the <code>show</code>
	 * method, an application can display the component with the specified name.
	 *
	 * @param comp
	 *            the component to be added.
	 * @param constraints
	 *            a tag that identifies a particular card in the layout.
	 * @see java.awt.BetterCardLayout#show(java.awt.Container, java.lang.String)
	 * @exception IllegalArgumentException
	 *                if the constraint is not a string.
	 */
	@Override
	public void addLayoutComponent(Component comp, Object key) {
		synchronized (comp.getTreeLock()) {
			if (!vector.isEmpty()) {
				comp.setVisible(false);
			}
			for (int i = 0; i < vector.size(); i++) {
				if (vector.get(i).key.equals(key)) {
					vector.get(i).comp = comp;
					return;
				}
			}
			vector.add(new Card(key, comp));
		}
	}

	/**
	 * @deprecated replaced by
	 *             <code>addLayoutComponent(Component, Object)</code>.
	 */
	@Override
	@Deprecated
	public void addLayoutComponent(String name, Component comp) {
		addLayoutComponent(comp, name);
	}

	/**
	 * Make sure that the Container really has a BetterCardLayout installed.
	 * Otherwise havoc can ensue!
	 */
	void checkLayout(Container parent) {
		if (parent.getLayout() != this) {
			throw new IllegalArgumentException("wrong parent for BetterCardLayout");
		}
	}

	/**
	 * Flips to the first card of the container.
	 *
	 * @param parent
	 *            the parent container in which to do the layout
	 * @see java.awt.BetterCardLayout#last
	 */
	public void first(Container parent) {
		synchronized (parent.getTreeLock()) {
			checkLayout(parent);
			int ncomponents = parent.getComponentCount();
			for (int i = 0; i < ncomponents; i++) {
				Component comp = parent.getComponent(i);
				if (comp.isVisible()) {
					comp.setVisible(false);
					break;
				}
			}
			if (ncomponents > 0) {
				currentCard = 0;
				parent.getComponent(0).setVisible(true);
				parent.validate();
			}
		}
	}

	public Object getCurrentCardKey() {
		return currentCard < 0 || currentCard >= vector.size() ? null : vector.get(currentCard).key;
	}

	/**
	 * Gets the horizontal gap between components.
	 *
	 * @return the horizontal gap between components.
	 * @see java.awt.BetterCardLayout#setHgap(int)
	 * @see java.awt.BetterCardLayout#getVgap()
	 * @since JDK1.1
	 */
	public int getHgap() {
		return hgap;
	}

	/**
	 * Returns the alignment along the x axis. This specifies how the component
	 * would like to be aligned relative to other components. The value should
	 * be a number between 0 and 1 where 0 represents alignment along the
	 * origin, 1 is aligned the furthest away from the origin, 0.5 is centered,
	 * etc.
	 */
	@Override
	public float getLayoutAlignmentX(Container parent) {
		return 0.5f;
	}

	/**
	 * Returns the alignment along the y axis. This specifies how the component
	 * would like to be aligned relative to other components. The value should
	 * be a number between 0 and 1 where 0 represents alignment along the
	 * origin, 1 is aligned the furthest away from the origin, 0.5 is centered,
	 * etc.
	 */
	@Override
	public float getLayoutAlignmentY(Container parent) {
		return 0.5f;
	}

	/**
	 * Gets the vertical gap between components.
	 *
	 * @return the vertical gap between components.
	 * @see java.awt.BetterCardLayout#setVgap(int)
	 * @see java.awt.BetterCardLayout#getHgap()
	 */
	public int getVgap() {
		return vgap;
	}

	/**
	 * Invalidates the layout, indicating that if the layout manager has cached
	 * information it should be discarded.
	 */
	@Override
	public void invalidateLayout(Container target) {
	}

	public boolean isSizeHidden() {
		return sizeHidden;
	}

	/**
	 * Flips to the last card of the container.
	 *
	 * @param parent
	 *            the parent container in which to do the layout
	 * @see java.awt.BetterCardLayout#first
	 */
	public void last(Container parent) {
		synchronized (parent.getTreeLock()) {
			checkLayout(parent);
			int ncomponents = parent.getComponentCount();
			for (int i = 0; i < ncomponents; i++) {
				Component comp = parent.getComponent(i);
				if (comp.isVisible()) {
					comp.setVisible(false);
					break;
				}
			}
			if (ncomponents > 0) {
				currentCard = ncomponents - 1;
				parent.getComponent(currentCard).setVisible(true);
				parent.validate();
			}
		}
	}

	/**
	 * Lays out the specified container using this card layout.
	 * <p>
	 * Each component in the <code>parent</code> container is reshaped to be the
	 * size of the container, minus space for surrounding insets, horizontal
	 * gaps, and vertical gaps.
	 *
	 * @param parent
	 *            the parent container in which to do the layout
	 * @see java.awt.Container#doLayout
	 */
	@Override
	public void layoutContainer(Container parent) {
		synchronized (parent.getTreeLock()) {
			Insets insets = parent.getInsets();
			int ncomponents = parent.getComponentCount();
			Component comp = null;

			for (int i = 0; i < ncomponents; i++) {
				comp = parent.getComponent(i);
				comp.setBounds(hgap + insets.left, vgap + insets.top,
						parent.getWidth() - (hgap * 2 + insets.left + insets.right),
						parent.getHeight() - (vgap * 2 + insets.top + insets.bottom));
			}
		}
	}

	/**
	 * Returns the maximum dimensions for this layout given the components in
	 * the specified target container.
	 *
	 * @param target
	 *            the component which needs to be laid out
	 * @see Container
	 * @see #minimumLayoutSize
	 * @see #preferredLayoutSize
	 */
	@Override
	public Dimension maximumLayoutSize(Container target) {
		return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
	}

	/**
	 * Calculates the minimum size for the specified panel.
	 *
	 * @param parent
	 *            the parent container in which to do the layout
	 * @return the minimum dimensions required to lay out the subcomponents of
	 *         the specified container
	 * @see java.awt.Container#doLayout
	 * @see java.awt.BetterCardLayout#preferredLayoutSize
	 */
	@Override
	public Dimension minimumLayoutSize(Container parent) {
		synchronized (parent.getTreeLock()) {
			Insets insets = parent.getInsets();
			int ncomponents = parent.getComponentCount();
			int w = 0;
			int h = 0;

			boolean anyVisible = false;

			for (int i = 0; i < ncomponents; i++) {
				Component comp = parent.getComponent(i);
				anyVisible |= comp.isVisible();
				if (comp.isVisible() || sizeHidden) {
					Dimension d = comp.getMinimumSize();
					if (d.width > w) {
						w = d.width;
					}
					if (d.height > h) {
						h = d.height;
					}
				}
			}
			if (!anyVisible) {
				return new Dimension(0, 0);
			}
			return new Dimension(insets.left + insets.right + w + hgap * 2,
					insets.top + insets.bottom + h + vgap * 2);
		}
	}

	/**
	 * Flips to the next card of the specified container. If the currently
	 * visible card is the last one, this method flips to the first card in the
	 * layout.
	 *
	 * @param parent
	 *            the parent container in which to do the layout
	 * @see java.awt.BetterCardLayout#previous
	 */
	public void next(Container parent) {
		synchronized (parent.getTreeLock()) {
			checkLayout(parent);
			int ncomponents = parent.getComponentCount();
			for (int i = 0; i < ncomponents; i++) {
				Component comp = parent.getComponent(i);
				if (comp.isVisible()) {
					comp.setVisible(false);
					currentCard = (i + 1) % ncomponents;
					comp = parent.getComponent(currentCard);
					comp.setVisible(true);
					parent.validate();
					return;
				}
			}
			showDefaultComponent(parent);
		}
	}

	/**
	 * Determines the preferred size of the container argument using this card
	 * layout.
	 *
	 * @param parent
	 *            the parent container in which to do the layout
	 * @return the preferred dimensions to lay out the subcomponents of the
	 *         specified container
	 * @see java.awt.Container#getPreferredSize
	 * @see java.awt.BetterCardLayout#minimumLayoutSize
	 */
	@Override
	public Dimension preferredLayoutSize(Container parent) {
		synchronized (parent.getTreeLock()) {
			Insets insets = parent.getInsets();
			int ncomponents = parent.getComponentCount();
			int w = 0;
			int h = 0;

			boolean anyVisible = false;

			for (int i = 0; i < ncomponents; i++) {
				Component comp = parent.getComponent(i);
				anyVisible |= comp.isVisible();
				if (comp.isVisible() || sizeHidden) {
					Dimension d = comp.getPreferredSize();
					if (d.width > w) {
						w = d.width;
					}
					if (d.height > h) {
						h = d.height;
					}
				}
			}
			if (!anyVisible) {
				return new Dimension(0, 0);
			}
			return new Dimension(insets.left + insets.right + w + hgap * 2,
					insets.top + insets.bottom + h + vgap * 2);
		}
	}

	/**
	 * Flips to the previous card of the specified container. If the currently
	 * visible card is the first one, this method flips to the last card in the
	 * layout.
	 *
	 * @param parent
	 *            the parent container in which to do the layout
	 * @see java.awt.BetterCardLayout#next
	 */
	public void previous(Container parent) {
		synchronized (parent.getTreeLock()) {
			checkLayout(parent);
			int ncomponents = parent.getComponentCount();
			for (int i = 0; i < ncomponents; i++) {
				Component comp = parent.getComponent(i);
				if (comp.isVisible()) {
					comp.setVisible(false);
					currentCard = i > 0 ? i - 1 : ncomponents - 1;
					comp = parent.getComponent(currentCard);
					comp.setVisible(true);
					parent.validate();
					return;
				}
			}
			showDefaultComponent(parent);
		}
	}

	/**
	 * Removes the specified component from the layout. If the card was visible
	 * on top, the next card underneath it is shown.
	 *
	 * @param comp
	 *            the component to be removed.
	 * @see java.awt.Container#remove(java.awt.Component)
	 * @see java.awt.Container#removeAll()
	 */
	@Override
	public void removeLayoutComponent(Component comp) {
		synchronized (comp.getTreeLock()) {
			for (int i = 0; i < vector.size(); i++) {
				if (vector.get(i).comp == comp) {
					// if we remove current component we should show next one
					if (comp.isVisible() && comp.getParent() != null) {
						next(comp.getParent());
					}

					vector.remove(i);

					// correct currentCard if this is necessary
					if (currentCard > i) {
						currentCard--;
					}
					break;
				}
			}
		}
	}

	/**
	 * Sets the horizontal gap between components.
	 *
	 * @param hgap
	 *            the horizontal gap between components.
	 * @see java.awt.BetterCardLayout#getHgap()
	 * @see java.awt.BetterCardLayout#setVgap(int)
	 * @since JDK1.1
	 */
	public void setHgap(int hgap) {
		this.hgap = hgap;
	}

	public void setSizeHidden(boolean sizeHidden) {
		this.sizeHidden = sizeHidden;
	}

	/**
	 * Sets the vertical gap between components.
	 *
	 * @param vgap
	 *            the vertical gap between components.
	 * @see java.awt.BetterCardLayout#getVgap()
	 * @see java.awt.BetterCardLayout#setHgap(int)
	 * @since JDK1.1
	 */
	public void setVgap(int vgap) {
		this.vgap = vgap;
	}

	/**
	 * Flips to the component that was added to this layout with the specified
	 * <code>name</code>, using <code>addLayoutComponent</code>. If no such
	 * component exists, then nothing happens.
	 *
	 * @param parent
	 *            the parent container in which to do the layout
	 * @param key
	 *            the component name
	 * @see java.awt.BetterCardLayout#addLayoutComponent(java.awt.Component,
	 *      java.lang.Object)
	 */
	public void show(Container parent, Object key) {
		synchronized (parent.getTreeLock()) {
			checkLayout(parent);
			Component next = null;
			int ncomponents = vector.size();
			for (int i = 0; i < ncomponents; i++) {
				Card card = vector.get(i);
				if (card.key.equals(key)) {
					next = card.comp;
					currentCard = i;
					break;
				}
			}
			ncomponents = parent.getComponentCount();
			for (int i = 0; i < ncomponents; i++) {
				Component comp = parent.getComponent(i);
				if (comp.isVisible()) {
					comp.setVisible(false);
					break;
				}
			}
			if (next != null && !next.isVisible()) {
				next.setVisible(true);
			}
			parent.invalidate();
		}
	}

	void showDefaultComponent(Container parent) {
		if (parent.getComponentCount() > 0) {
			currentCard = 0;
			parent.getComponent(0).setVisible(true);
			parent.validate();
		}
	}

	/**
	 * Returns a string representation of the state of this card layout.
	 *
	 * @return a string representation of this card layout.
	 */
	@Override
	public String toString() {
		return getClass().getName() + "[hgap=" + hgap + ",vgap=" + vgap + "]";
	}
}
