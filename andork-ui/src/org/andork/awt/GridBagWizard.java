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
package org.andork.awt;

import java.awt.Component;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.andork.util.Java7;

/**
 * The absurdly powerful, better alternative to {@link GridBagHelper}.
 *
 * @author andy.edwards
 */
public class GridBagWizard {
	/**
	 * Automatically sets the insets of a component's constraints whenever the
	 * component's {@link GridBagConstraints#gridx gridx} or
	 * {@link GridBagConstraints#gridx gridx} is changed.
	 *
	 * @author andy.edwards
	 */
	public static interface AutoInsets {
		public void autoinsets(Component comp, GridBagConstraints gbc);
	}

	/**
	 * Allows you to set {@link GridBagConstraints} in builder-like fashion,
	 * with many convenience methods.
	 *
	 * @author andy.edwards
	 */
	public class Constrainer implements IConstrainer {
		private GridBagConstraints gbc;
		private Component comp;

		private AutoInsets autoinsets = null;
		private boolean xset;
		private boolean widthSet;
		private boolean yset;
		private boolean heightSet;

		private Constrainer(Component comp) {
			this.comp = comp;
			gbc = (GridBagConstraints) defaultConstrainer.gbc.clone();
			autoinsets = defaultConstrainer.autoinsets;
			update();
		}

		private Constrainer(GridBagConstraints gbc) {
			this.gbc = gbc;
			comp = null;
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see
		 * com.cybertrader.streamer.common.IConstrainer#above(java.awt.Component
		 * )
		 */
		@Override
		public Constrainer above(Object obj) {
			if (xset) {
				return y(minRow(obj) - gbc.gridheight);
			}
			return above(new Object[] { obj });
		}

		/**
		 * Sets the {@link GridBagConstraints#gridx gridx} of the target
		 * {@link Component} so that it is above all the {@link Component}s,
		 * {@link Constrainer}s, and or {@link Group}s in {@code objs}. If the
		 * {@link GridBagConstraints#gridwidth gridwidth} for the target
		 * component has not been set yet, it will be set so that the target
		 * component spans all of the columns they occupy.
		 *
		 * @param objs
		 *            the {@link Component}s, {@link Constrainer}s, and/or
		 *            {@link Group}s to place the target component above.
		 * @return this {@link Constrainer}, for chaining.
		 */
		public Constrainer above(Object... objs) {
			y(minRow(objs) - gbc.gridheight);

			if (!xset) {
				x(minColumn(objs));
			}
			if (!widthSet) {
				width(GridBagWizard.this.width(objs));
			}

			return this;
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see com.cybertrader.streamer.common.IConstrainer#aboveLast()
		 */
		@Override
		public Constrainer aboveLast() {
			return above(last);
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see com.cybertrader.streamer.common.IConstrainer#addToInsets(int,
		 * int, int, int)
		 */
		@Override
		public Constrainer addToInsets(int top, int left, int bottom, int right) {
			autoinsets();
			Insets i = gbc.insets;
			if (i == null) {
				i = new Insets(0, 0, 0, 0);
			}
			set(gbc.insets, i.top + top, i.left + left, i.bottom + bottom, i.right + right);
			update();
			return this;
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see
		 * com.cybertrader.streamer.common.IConstrainer#alignBottom(java.awt
		 * .Component)
		 */
		@Override
		public Constrainer alignBottom(Object obj) {
			return y(maxRow(obj) - gbc.gridheight);
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see com.cybertrader.streamer.common.IConstrainer#alignBottomToLast()
		 */
		@Override
		public Constrainer alignBottomToLast() {
			return alignBottom(last);
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see
		 * com.cybertrader.streamer.common.IConstrainer#alignRight(java.awt.
		 * Component)
		 */
		@Override
		public Constrainer alignRight(Object obj) {
			return x(maxColumn(obj) - gbc.gridwidth);
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see com.cybertrader.streamer.common.IConstrainer#alignRightToLast()
		 */
		@Override
		public Constrainer alignRightToLast() {
			return alignRight(last);
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see com.cybertrader.streamer.common.IConstrainer#anchor(int)
		 */
		@Override
		public Constrainer anchor(int anchor) {
			gbc.anchor = anchor;
			update();
			return this;
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see com.cybertrader.streamer.common.IConstrainer#autoinsets()
		 */
		@Override
		public Constrainer autoinsets() {
			if (this != defaultConstrainer) {
				if (autoinsets != null) {
					autoinsets.autoinsets(comp, gbc);
				}
				update();
			}
			return this;
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see com.cybertrader.streamer.common.IConstrainer#autoinsets(com.
		 * cybertrader .streamer.common.GridBagWizard.AutoInsets)
		 */
		@Override
		public Constrainer autoinsets(AutoInsets autoinsets) {
			this.autoinsets = autoinsets;
			return autoinsets();
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see
		 * com.cybertrader.streamer.common.IConstrainer#autoinsets(java.awt.
		 * Component)
		 */
		@Override
		public Constrainer autoinsets(Object obj) {
			return autoinsets(GridBagWizard.this.autoinsets(obj));
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see
		 * com.cybertrader.streamer.common.IConstrainer#below(java.awt.Component
		 * )
		 */
		@Override
		public Constrainer below(Object obj) {
			if (xset) {
				return y(maxRow(obj) + 1);
			}
			return below(new Object[] { obj });
		}

		/**
		 * Sets the {@link GridBagConstraints#gridx gridx} of the target
		 * {@link Component} so that it is below all the {@link Component}s,
		 * {@link Constrainer}s, and or {@link Group}s in {@code objs}. If the
		 * {@link GridBagConstraints#gridwidth gridwidth} for the target
		 * component has not been set yet, it will be set so that the target
		 * component spans all of the columns they occupy.
		 *
		 * @param objs
		 *            the {@link Component}s, {@link Constrainer}s, and/or
		 *            {@link Group}s to place the target component below.
		 * @return this {@link Constrainer}, for chaining.
		 */
		public Constrainer below(Object... objs) {
			y(maxRow(objs) + 1);

			if (!xset) {
				x(minColumn(objs));
			}
			if (!widthSet) {
				width(GridBagWizard.this.width(objs));
			}

			return this;
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see com.cybertrader.streamer.common.IConstrainer#belowAll()
		 */
		@Override
		public Constrainer belowAll() {
			return y(numRows());
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see com.cybertrader.streamer.common.IConstrainer#belowLast()
		 */
		@Override
		public Constrainer belowLast() {
			return below(last);
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see
		 * com.cybertrader.streamer.common.IConstrainer#defaultAutoinsets(int,
		 * int)
		 */
		@Override
		public Constrainer defaultAutoinsets(int xspacing, int yspacing) {
			return autoinsets(new DefaultAutoInsets(xspacing, yspacing));
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see com.cybertrader.streamer.common.IConstrainer#east()
		 */
		@Override
		public Constrainer east() {
			return anchor(GridBagConstraints.EAST);
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see com.cybertrader.streamer.common.IConstrainer#fill(int)
		 */
		@Override
		public Constrainer fill(int fill) {
			gbc.fill = fill;
			update();
			return this;
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see com.cybertrader.streamer.common.IConstrainer#fillboth()
		 */
		@Override
		public Constrainer fillboth() {
			return fill(GridBagConstraints.BOTH);
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see com.cybertrader.streamer.common.IConstrainer#fillboth(double,
		 * double)
		 */
		@Override
		public Constrainer fillboth(double weightx, double weighty) {
			gbc.weightx = weightx;
			gbc.weighty = weighty;
			return fillboth();
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see com.cybertrader.streamer.common.IConstrainer#fillx()
		 */
		@Override
		public Constrainer fillx() {
			if (gbc.fill == GridBagConstraints.VERTICAL) {
				return fillboth();
			}
			return fill(GridBagConstraints.HORIZONTAL);
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see com.cybertrader.streamer.common.IConstrainer#fillx(double)
		 */
		@Override
		public Constrainer fillx(double weightx) {
			gbc.weightx = weightx;
			return fillx();
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see com.cybertrader.streamer.common.IConstrainer#filly()
		 */
		@Override
		public Constrainer filly() {
			return fill(GridBagConstraints.VERTICAL);
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see com.cybertrader.streamer.common.IConstrainer#filly(double)
		 */
		@Override
		public Constrainer filly(double weighty) {
			gbc.fill = GridBagConstraints.VERTICAL;
			return filly();
		}

		@Override
		public Constrainer growDown(int numRows) {
			return height(gbc.gridheight + numRows);
		}

		@Override
		public IConstrainer growLeft(int numColumns) {
			return x(gbc.gridx - numColumns).width(gbc.gridwidth + numColumns);
		}

		@Override
		public Constrainer growRight(int numColumns) {
			return width(gbc.gridwidth + numColumns);
		}

		@Override
		public Constrainer growUp(int numRows) {
			return y(gbc.gridy - numRows).height(gbc.gridheight + numRows);
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see com.cybertrader.streamer.common.IConstrainer#height(int)
		 */
		@Override
		public Constrainer height(int height) {
			gbc.gridheight = height;
			heightSet = true;
			update();
			return this;
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see
		 * com.cybertrader.streamer.common.IConstrainer#insets(java.awt.Insets)
		 */
		@Override
		public Constrainer insets(Insets insets) {
			set(gbc.insets, insets);
			update();
			return this;
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see com.cybertrader.streamer.common.IConstrainer#insets(int, int,
		 * int, int)
		 */
		@Override
		public Constrainer insets(int top, int left, int bottom, int right) {
			set(gbc.insets, top, left, bottom, right);
			update();
			return this;
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see com.cybertrader.streamer.common.IConstrainer#ipad(int, int)
		 */
		@Override
		public Constrainer ipad(int ipadx, int ipady) {
			gbc.ipadx = ipadx;
			gbc.ipady = ipady;
			update();
			return this;
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see com.cybertrader.streamer.common.IConstrainer#ipadx(int)
		 */
		@Override
		public Constrainer ipadx(int ipadx) {
			gbc.ipadx = ipadx;
			update();
			return this;
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see com.cybertrader.streamer.common.IConstrainer#ipady(int)
		 */
		@Override
		public Constrainer ipady(int ipady) {
			gbc.ipady = ipady;
			update();
			return this;
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see com.cybertrader.streamer.common.IConstrainer#lastAnchor()
		 */
		@Override
		public Constrainer lastAnchor() {
			return sameAnchor(last);
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see com.cybertrader.streamer.common.IConstrainer#lastAutoinsets()
		 */
		@Override
		public Constrainer lastAutoinsets() {
			return autoinsets(last);
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see com.cybertrader.streamer.common.IConstrainer#lastFill()
		 */
		@Override
		public Constrainer lastFill() {
			return sameFill(last);
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see com.cybertrader.streamer.common.IConstrainer#lastHeight()
		 */
		@Override
		public Constrainer lastHeight() {
			return sameHeight(last);
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see com.cybertrader.streamer.common.IConstrainer#lastInsets()
		 */
		@Override
		public Constrainer lastInsets() {
			return sameInsets(last);
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see com.cybertrader.streamer.common.IConstrainer#lastIpad()
		 */
		@Override
		public Constrainer lastIpad() {
			return sameIpad(last);
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see com.cybertrader.streamer.common.IConstrainer#lastIpadx()
		 */
		@Override
		public Constrainer lastIpadx() {
			return sameIpadx(last);
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see com.cybertrader.streamer.common.IConstrainer#lastIpady()
		 */
		@Override
		public Constrainer lastIpady() {
			return sameIpady(last);
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see com.cybertrader.streamer.common.IConstrainer#lastWeight()
		 */
		@Override
		public Constrainer lastWeight() {
			return sameWeight(last);
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see com.cybertrader.streamer.common.IConstrainer#lastWeightx()
		 */
		@Override
		public Constrainer lastWeightx() {
			return sameWeightx(last);
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see com.cybertrader.streamer.common.IConstrainer#lastWeighty()
		 */
		@Override
		public Constrainer lastWeighty() {
			return sameWeighty(last);
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see com.cybertrader.streamer.common.IConstrainer#lastWidth()
		 */
		@Override
		public Constrainer lastWidth() {
			return sameWidth(last);
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see com.cybertrader.streamer.common.IConstrainer#lastX()
		 */
		@Override
		public Constrainer lastX() {
			return sameX(last);
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see com.cybertrader.streamer.common.IConstrainer#lastY()
		 */
		@Override
		public Constrainer lastY() {
			return sameY(last);
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see com.cybertrader.streamer.common.IConstrainer#leftOf(java.awt.
		 * Component )
		 */
		@Override
		public Constrainer leftOf(Object obj) {
			if (yset) {
				return x(minColumn(obj) - gbc.gridwidth);
			}
			return leftOf(new Object[] { obj });
		}

		/**
		 * Sets the {@link GridBagConstraints#gridy gridy} of the target
		 * {@link Component} so that it is left of all the {@link Component}s,
		 * {@link Constrainer} s, and or {@link Group}s in {@code objs}. If the
		 * {@link GridBagConstraints#gridheight gridheight} for the target
		 * component has not been set yet, it will be set so that the target
		 * component spans all of the rows they occupy.
		 *
		 * @param objs
		 *            the {@link Component}s, {@link Constrainer}s, and/or
		 *            {@link Group}s to place the target component left of.
		 * @return this {@link Constrainer}, for chaining.
		 */
		public Constrainer leftOf(Object... objs) {
			x(minColumn(objs) - gbc.gridwidth);

			if (!yset) {
				y(minRow(objs));
			}
			if (!heightSet) {
				height(GridBagWizard.this.height(objs));
			}

			return this;
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see com.cybertrader.streamer.common.IConstrainer#leftOfLast()
		 */
		@Override
		public Constrainer leftOfLast() {
			return leftOf(last);
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see com.cybertrader.streamer.common.IConstrainer#nofill()
		 */
		@Override
		public Constrainer nofill() {
			return fill(GridBagConstraints.NONE);
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see com.cybertrader.streamer.common.IConstrainer#north()
		 */
		@Override
		public Constrainer north() {
			return anchor(GridBagConstraints.NORTH);
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see com.cybertrader.streamer.common.IConstrainer#northeast()
		 */
		@Override
		public Constrainer northeast() {
			return anchor(GridBagConstraints.NORTHEAST);
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see com.cybertrader.streamer.common.IConstrainer#northwest()
		 */
		@Override
		public Constrainer northwest() {
			return anchor(GridBagConstraints.NORTHWEST);
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see com.cybertrader.streamer.common.IConstrainer#remHeight()
		 */
		@Override
		public Constrainer remHeight() {
			return height(GridBagConstraints.REMAINDER);
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see com.cybertrader.streamer.common.IConstrainer#remWidth()
		 */
		@Override
		public Constrainer remWidth() {
			return width(GridBagConstraints.REMAINDER);
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see com.cybertrader.streamer.common.IConstrainer#rightOf(java.awt.
		 * Component )
		 */
		@Override
		public Constrainer rightOf(Object obj) {
			if (yset) {
				return x(maxColumn(obj) + 1);
			}
			return rightOf(new Object[] { obj });
		}

		/**
		 * Sets the {@link GridBagConstraints#gridy gridy} of the target
		 * {@link Component} so that it is right of all the {@link Component}s,
		 * {@link Constrainer}s, and or {@link Group}s in {@code objs}. If the
		 * {@link GridBagConstraints#gridheight gridheight} for the target
		 * component has not been set yet, it will be set so that the target
		 * component spans all of the rows they occupy.
		 *
		 * @param objs
		 *            the {@link Component}s, {@link Constrainer}s, and/or
		 *            {@link Group}s to place the target component right of.
		 * @return this {@link Constrainer}, for chaining.
		 */
		public Constrainer rightOf(Object... objs) {
			x(maxColumn(objs) + 1);

			if (!yset) {
				y(minRow(objs));
			}
			if (!heightSet) {
				height(GridBagWizard.this.height(objs));
			}

			return this;
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see com.cybertrader.streamer.common.IConstrainer#rightOfAll()
		 */
		@Override
		public Constrainer rightOfAll() {
			return x(numCols());
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see com.cybertrader.streamer.common.IConstrainer#rightOfLast()
		 */
		@Override
		public Constrainer rightOfLast() {
			return rightOf(last);
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see
		 * com.cybertrader.streamer.common.IConstrainer#sameAnchor(java.awt.
		 * Component)
		 */
		@Override
		public Constrainer sameAnchor(Object obj) {
			return anchor(GridBagWizard.this.anchor(obj));
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see com.cybertrader.streamer.common.IConstrainer#sameFill(java.awt.
		 * Component )
		 */
		@Override
		public Constrainer sameFill(Object obj) {
			return fill(GridBagWizard.this.fill(obj));
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see
		 * com.cybertrader.streamer.common.IConstrainer#sameHeight(java.awt.
		 * Component)
		 */
		@Override
		public Constrainer sameHeight(Object obj) {
			return height(GridBagWizard.this.height(obj));
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see
		 * com.cybertrader.streamer.common.IConstrainer#sameInsets(java.awt.
		 * Component)
		 */
		@Override
		public Constrainer sameInsets(Object obj) {
			return insets(GridBagWizard.this.insets(obj));
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see com.cybertrader.streamer.common.IConstrainer#sameIpad(java.awt.
		 * Component )
		 */
		@Override
		public Constrainer sameIpad(Object obj) {
			return sameIpadx(obj).sameIpady(obj);
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see com.cybertrader.streamer.common.IConstrainer#sameIpadx(java.awt.
		 * Component )
		 */
		@Override
		public Constrainer sameIpadx(Object obj) {
			return ipadx(GridBagWizard.this.ipadx(obj));
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see com.cybertrader.streamer.common.IConstrainer#sameIpady(java.awt.
		 * Component )
		 */
		@Override
		public Constrainer sameIpady(Object obj) {
			return ipady(GridBagWizard.this.ipady(obj));
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see
		 * com.cybertrader.streamer.common.IConstrainer#sameWeight(java.awt.
		 * Component)
		 */
		@Override
		public Constrainer sameWeight(Object obj) {
			return sameWeightx(obj).sameWeighty(obj);
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see
		 * com.cybertrader.streamer.common.IConstrainer#sameWeightx(java.awt
		 * .Component)
		 */
		@Override
		public Constrainer sameWeightx(Object obj) {
			return weightx(GridBagWizard.this.weightx(obj));
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see
		 * com.cybertrader.streamer.common.IConstrainer#sameWeighty(java.awt
		 * .Component)
		 */
		@Override
		public Constrainer sameWeighty(Object obj) {
			return weighty(GridBagWizard.this.weighty(obj));
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see com.cybertrader.streamer.common.IConstrainer#sameWidth(java.awt.
		 * Component )
		 */
		@Override
		public Constrainer sameWidth(Object obj) {
			return width(GridBagWizard.this.width(obj));
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see
		 * com.cybertrader.streamer.common.IConstrainer#sameX(java.awt.Component
		 * )
		 */
		@Override
		public Constrainer sameX(Object obj) {
			return x(GridBagWizard.this.x(obj));
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see
		 * com.cybertrader.streamer.common.IConstrainer#sameY(java.awt.Component
		 * )
		 */
		@Override
		public Constrainer sameY(Object obj) {
			return y(GridBagWizard.this.y(obj));
		}

		@Override
		public Constrainer shrinkDown(int numRows) {
			return y(gbc.gridy + numRows).height(gbc.gridheight - numRows);
		}

		@Override
		public Constrainer shrinkLeft(int numColumns) {
			return width(gbc.gridwidth - numColumns);
		}

		@Override
		public Constrainer shrinkRight(int numColumns) {
			return x(gbc.gridx + numColumns).width(gbc.gridwidth - numColumns);
		}

		@Override
		public Constrainer shrinkUp(int numRows) {
			return height(gbc.gridheight - numRows);
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see com.cybertrader.streamer.common.IConstrainer#south()
		 */
		@Override
		public Constrainer south() {
			return anchor(GridBagConstraints.SOUTH);
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see com.cybertrader.streamer.common.IConstrainer#southeast()
		 */
		@Override
		public Constrainer southeast() {
			return anchor(GridBagConstraints.SOUTHEAST);
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see com.cybertrader.streamer.common.IConstrainer#southwest()
		 */
		@Override
		public Constrainer southwest() {
			return anchor(GridBagConstraints.SOUTHWEST);
		}

		private void update() {
			if (this != defaultConstrainer) {
				target.add(comp, gbc);
			}
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see com.cybertrader.streamer.common.IConstrainer#weight(double,
		 * double)
		 */
		@Override
		public Constrainer weight(double weightx, double weighty) {
			gbc.weightx = weightx;
			gbc.weighty = weighty;
			update();
			return this;
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see com.cybertrader.streamer.common.IConstrainer#weightx(double)
		 */
		@Override
		public Constrainer weightx(double weightx) {
			gbc.weightx = weightx;
			update();
			return this;
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see com.cybertrader.streamer.common.IConstrainer#weighty(double)
		 */
		@Override
		public Constrainer weighty(double weighty) {
			gbc.weighty = weighty;
			update();
			return this;
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see com.cybertrader.streamer.common.IConstrainer#west()
		 */
		@Override
		public Constrainer west() {
			return anchor(GridBagConstraints.WEST);
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see com.cybertrader.streamer.common.IConstrainer#width(int)
		 */
		@Override
		public Constrainer width(int width) {
			gbc.gridwidth = width;
			widthSet = true;
			update();
			return this;
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see com.cybertrader.streamer.common.IConstrainer#x(int)
		 */
		@Override
		public Constrainer x(int x) {
			gbc.gridx = x;
			xset = true;
			return autoinsets();
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see com.cybertrader.streamer.common.IConstrainer#xy(int, int)
		 */
		@Override
		public Constrainer xy(int x, int y) {
			gbc.gridx = x;
			gbc.gridy = y;
			xset = yset = true;
			return autoinsets();
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see com.cybertrader.streamer.common.IConstrainer#y(int)
		 */
		@Override
		public Constrainer y(int y) {
			gbc.gridy = y;
			yset = true;
			return autoinsets();
		}
	}

	/**
	 * An {@link AutoInsets} with fixed column and row spacing. It will only
	 * apply insets between columns and rows, so components will touch the top,
	 * left, bottom, and right edges of the target container; use an
	 * {@link EmptyBorder} if necessary on the target container.
	 *
	 * @author andy.edwards
	 */
	public static class DefaultAutoInsets implements AutoInsets {
		private int xspacing;
		private int yspacing;

		public DefaultAutoInsets(int xspacing, int yspacing) {
			super();
			this.xspacing = xspacing;
			this.yspacing = yspacing;
		}

		@Override
		public void autoinsets(Component comp, GridBagConstraints gbc) {
			if (gbc.insets == null) {
				gbc.insets = new Insets(0, 0, 0, 0);
			}
			gbc.insets.left = gbc.gridx > 0 ? xspacing : 0;
			gbc.insets.top = gbc.gridy > 0 ? yspacing : 0;
			gbc.insets.bottom = 0;
			gbc.insets.right = 0;
		}
	}

	/**
	 * Allows you to set multiple {@link GridBagConstraints} at once.
	 *
	 * @author andy.edwards
	 */
	public class Group implements IConstrainer {
		private IConstrainer[] constrainers;

		private Group(IConstrainer[] constrainers) {
			super();
			this.constrainers = constrainers;
		}

		@Override
		public Group above(Object obj) {
			for (IConstrainer constrainer : constrainers) {
				constrainer.above(obj);
			}
			return this;
		}

		@Override
		public Group aboveLast() {
			for (IConstrainer constrainer : constrainers) {
				constrainer.aboveLast();
			}
			return this;
		}

		@Override
		public Group addToInsets(int top, int left, int bottom, int right) {
			for (IConstrainer constrainer : constrainers) {
				constrainer.addToInsets(top, left, bottom, right);
			}
			return this;
		}

		@Override
		public Group alignBottom(Object obj) {
			for (IConstrainer constrainer : constrainers) {
				constrainer.alignBottom(obj);
			}
			return this;
		}

		@Override
		public Group alignBottomToLast() {
			for (IConstrainer constrainer : constrainers) {
				constrainer.alignBottomToLast();
			}
			return this;
		}

		@Override
		public Group alignRight(Object obj) {
			for (IConstrainer constrainer : constrainers) {
				constrainer.alignRight(obj);
			}
			return this;
		}

		@Override
		public Group alignRightToLast() {
			for (IConstrainer constrainer : constrainers) {
				constrainer.alignRightToLast();
			}
			return this;
		}

		@Override
		public Group anchor(int anchor) {
			for (IConstrainer constrainer : constrainers) {
				constrainer.anchor(anchor);
			}
			return this;
		}

		@Override
		public Group autoinsets() {
			for (IConstrainer constrainer : constrainers) {
				constrainer.autoinsets();
			}
			return this;
		}

		@Override
		public Group autoinsets(AutoInsets autoinsets) {
			for (IConstrainer constrainer : constrainers) {
				constrainer.autoinsets(autoinsets);
			}
			return this;
		}

		@Override
		public Group autoinsets(Object obj) {
			for (IConstrainer constrainer : constrainers) {
				constrainer.autoinsets(obj);
			}
			return this;
		}

		@Override
		public Group below(Object obj) {
			for (IConstrainer constrainer : constrainers) {
				constrainer.below(obj);
			}
			return this;
		}

		@Override
		public Group belowAll() {
			for (IConstrainer constrainer : constrainers) {
				constrainer.belowAll();
			}
			return this;
		}

		@Override
		public Group belowLast() {
			for (IConstrainer constrainer : constrainers) {
				constrainer.belowLast();
			}
			return this;
		}

		@Override
		public Group defaultAutoinsets(int hgap, int vgap) {
			for (IConstrainer constrainer : constrainers) {
				constrainer.defaultAutoinsets(hgap, vgap);
			}
			return this;
		}

		@Override
		public Group east() {
			for (IConstrainer constrainer : constrainers) {
				constrainer.east();
			}
			return this;
		}

		private void ensureXset(IConstrainer constrainer) {
			if (constrainer instanceof Constrainer) {
				Constrainer c = (Constrainer) constrainer;
				if (!c.xset) {
					c.x(0);
				}
			} else if (constrainer instanceof Group) {
				ensureXset(constrainer);
			}
		}

		private void ensureYset(IConstrainer constrainer) {
			if (constrainer instanceof Constrainer) {
				Constrainer c = (Constrainer) constrainer;
				if (!c.yset) {
					c.y(0);
				}
			} else if (constrainer instanceof Group) {
				ensureYset(constrainer);
			}
		}

		@Override
		public Group fill(int fill) {
			for (IConstrainer constrainer : constrainers) {
				constrainer.fill(fill);
			}
			return this;
		}

		@Override
		public Group fillboth() {
			for (IConstrainer constrainer : constrainers) {
				constrainer.fillboth();
			}
			return this;
		}

		@Override
		public Group fillboth(double weightx, double weighty) {
			for (IConstrainer constrainer : constrainers) {
				constrainer.fillboth(weightx, weighty);
			}
			return this;
		}

		@Override
		public Group fillx() {
			for (IConstrainer constrainer : constrainers) {
				constrainer.fillx();
			}
			return this;
		}

		@Override
		public Group fillx(double weightx) {
			for (IConstrainer constrainer : constrainers) {
				constrainer.fillx(weightx);
			}
			return this;
		}

		@Override
		public Group filly() {
			for (IConstrainer constrainer : constrainers) {
				constrainer.filly();
			}
			return this;
		}

		@Override
		public Group filly(double weighty) {
			for (IConstrainer constrainer : constrainers) {
				constrainer.filly(weighty);
			}
			return this;
		}

		@Override
		public Group growDown(int numRows) {
			for (IConstrainer c : constrainers) {
				c.growDown(numRows);
			}
			return this;
		}

		@Override
		public Group growLeft(int numColumns) {
			for (IConstrainer c : constrainers) {
				c.growLeft(numColumns);
			}
			return this;
		}

		@Override
		public Group growRight(int numColumns) {
			for (IConstrainer c : constrainers) {
				c.growRight(numColumns);
			}
			return this;
		}

		@Override
		public Group growUp(int numRows) {
			for (IConstrainer c : constrainers) {
				c.growUp(numRows);
			}
			return this;
		}

		@Override
		public Group height(int height) {
			for (IConstrainer constrainer : constrainers) {
				constrainer.height(height);
			}
			return this;
		}

		@Override
		public Group insets(Insets insets) {
			for (IConstrainer constrainer : constrainers) {
				constrainer.insets(insets);
			}
			return this;
		}

		@Override
		public Group insets(int top, int left, int bottom, int right) {
			for (IConstrainer constrainer : constrainers) {
				constrainer.insets(top, left, bottom, right);
			}
			return this;
		}

		/**
		 * Uses {@link Constrainer#below(Component)} to arrange the components
		 * in a column.
		 */
		public Group intoColumn() {
			ensureYset(constrainers[0]);
			int maxWidth = GridBagWizard.this.width((Object[]) constrainers);
			for (int i = 1; i < constrainers.length; i++) {
				constrainers[i].below(constrainers[i - 1]);
			}
			for (int i = 0; i < constrainers.length; i++) {
				if (constrainers[i] instanceof Constrainer) {
					constrainers[i].width(maxWidth);
				}
			}
			return this;
		}

		/**
		 * Uses {@link Constrainer#rightOf(Component)} to arrange the components
		 * in a row.
		 */
		public Group intoRow() {
			ensureXset(constrainers[0]);
			int maxHeight = GridBagWizard.this.height((Object[]) constrainers);
			for (int i = 1; i < constrainers.length; i++) {
				constrainers[i].rightOf(constrainers[i - 1]);
			}
			for (int i = 1; i < constrainers.length; i++) {
				if (constrainers[i] instanceof Constrainer) {
					constrainers[i].height(maxHeight);
				}
			}
			return this;
		}

		@Override
		public Group ipad(int ipadx, int ipady) {
			for (IConstrainer constrainer : constrainers) {
				constrainer.ipad(ipadx, ipady);
			}
			return this;
		}

		@Override
		public Group ipadx(int ipadx) {
			for (IConstrainer constrainer : constrainers) {
				constrainer.ipadx(ipadx);
			}
			return this;
		}

		@Override
		public Group ipady(int ipady) {
			for (IConstrainer constrainer : constrainers) {
				constrainer.ipady(ipady);
			}
			return this;
		}

		@Override
		public Group lastAnchor() {
			for (IConstrainer constrainer : constrainers) {
				constrainer.lastAnchor();
			}
			return this;
		}

		@Override
		public Group lastAutoinsets() {
			for (IConstrainer constrainer : constrainers) {
				constrainer.lastAutoinsets();
			}
			return this;
		}

		@Override
		public Group lastFill() {
			for (IConstrainer constrainer : constrainers) {
				constrainer.lastFill();
			}
			return this;
		}

		@Override
		public Group lastHeight() {
			for (IConstrainer constrainer : constrainers) {
				constrainer.lastHeight();
			}
			return this;
		}

		@Override
		public Group lastInsets() {
			for (IConstrainer constrainer : constrainers) {
				constrainer.lastInsets();
			}
			return this;
		}

		@Override
		public Group lastIpad() {
			for (IConstrainer constrainer : constrainers) {
				constrainer.lastIpad();
			}
			return this;
		}

		@Override
		public Group lastIpadx() {
			for (IConstrainer constrainer : constrainers) {
				constrainer.lastIpadx();
			}
			return this;
		}

		@Override
		public Group lastIpady() {
			for (IConstrainer constrainer : constrainers) {
				constrainer.lastIpady();
			}
			return this;
		}

		@Override
		public Group lastWeight() {
			for (IConstrainer constrainer : constrainers) {
				constrainer.lastWeight();
			}
			return this;
		}

		@Override
		public Group lastWeightx() {
			for (IConstrainer constrainer : constrainers) {
				constrainer.lastWeightx();
			}
			return this;
		}

		@Override
		public Group lastWeighty() {
			for (IConstrainer constrainer : constrainers) {
				constrainer.lastWeighty();
			}
			return this;
		}

		@Override
		public Group lastWidth() {
			for (IConstrainer constrainer : constrainers) {
				constrainer.lastWidth();
			}
			return this;
		}

		@Override
		public Group lastX() {
			for (IConstrainer constrainer : constrainers) {
				constrainer.lastX();
			}
			return this;
		}

		@Override
		public Group lastY() {
			for (IConstrainer constrainer : constrainers) {
				constrainer.lastY();
			}
			return this;
		}

		@Override
		public Group leftOf(Object obj) {
			for (IConstrainer constrainer : constrainers) {
				constrainer.leftOf(obj);
			}
			return this;
		}

		@Override
		public Group leftOfLast() {
			for (IConstrainer constrainer : constrainers) {
				constrainer.leftOfLast();
			}
			return this;
		}

		@Override
		public Group nofill() {
			for (IConstrainer constrainer : constrainers) {
				constrainer.nofill();
			}
			return this;
		}

		@Override
		public Group north() {
			for (IConstrainer constrainer : constrainers) {
				constrainer.north();
			}
			return this;
		}

		@Override
		public Group northeast() {
			for (IConstrainer constrainer : constrainers) {
				constrainer.northeast();
			}
			return this;
		}

		@Override
		public Group northwest() {
			for (IConstrainer constrainer : constrainers) {
				constrainer.northwest();
			}
			return this;
		}

		@Override
		public Group remHeight() {
			for (IConstrainer constrainer : constrainers) {
				constrainer.remHeight();
			}
			return this;
		}

		@Override
		public Group remWidth() {
			for (IConstrainer constrainer : constrainers) {
				constrainer.remWidth();
			}
			return this;
		}

		@Override
		public Group rightOf(Object obj) {
			for (IConstrainer constrainer : constrainers) {
				constrainer.rightOf(obj);
			}
			return this;
		}

		@Override
		public Group rightOfAll() {
			for (IConstrainer constrainer : constrainers) {
				constrainer.rightOfAll();
			}
			return this;
		}

		@Override
		public Group rightOfLast() {
			for (IConstrainer constrainer : constrainers) {
				constrainer.rightOfLast();
			}
			return this;
		}

		@Override
		public Group sameAnchor(Object obj) {
			for (IConstrainer constrainer : constrainers) {
				constrainer.sameAnchor(obj);
			}
			return this;
		}

		@Override
		public Group sameFill(Object obj) {
			for (IConstrainer constrainer : constrainers) {
				constrainer.sameFill(obj);
			}
			return this;
		}

		@Override
		public Group sameHeight(Object obj) {
			for (IConstrainer constrainer : constrainers) {
				constrainer.sameHeight(obj);
			}
			return this;
		}

		@Override
		public Group sameInsets(Object obj) {
			for (IConstrainer constrainer : constrainers) {
				constrainer.sameInsets(obj);
			}
			return this;
		}

		@Override
		public Group sameIpad(Object obj) {
			for (IConstrainer constrainer : constrainers) {
				constrainer.sameIpad(obj);
			}
			return this;
		}

		@Override
		public Group sameIpadx(Object obj) {
			for (IConstrainer constrainer : constrainers) {
				constrainer.sameIpadx(obj);
			}
			return this;
		}

		@Override
		public Group sameIpady(Object obj) {
			for (IConstrainer constrainer : constrainers) {
				constrainer.sameIpady(obj);
			}
			return this;
		}

		@Override
		public Group sameWeight(Object obj) {
			for (IConstrainer constrainer : constrainers) {
				constrainer.sameWeight(obj);
			}
			return this;
		}

		@Override
		public Group sameWeightx(Object obj) {
			for (IConstrainer constrainer : constrainers) {
				constrainer.sameWeightx(obj);
			}
			return this;
		}

		@Override
		public Group sameWeighty(Object obj) {
			for (IConstrainer constrainer : constrainers) {
				constrainer.sameWeighty(obj);
			}
			return this;
		}

		@Override
		public Group sameWidth(Object obj) {
			for (IConstrainer constrainer : constrainers) {
				constrainer.sameWidth(obj);
			}
			return this;
		}

		@Override
		public Group sameX(Object obj) {
			for (IConstrainer constrainer : constrainers) {
				constrainer.sameX(obj);
			}
			return this;
		}

		@Override
		public Group sameY(Object obj) {
			for (IConstrainer constrainer : constrainers) {
				constrainer.sameY(obj);
			}
			return this;
		}

		@Override
		public Group shrinkDown(int numRows) {
			for (IConstrainer c : constrainers) {
				c.shrinkDown(numRows);
			}
			return this;
		}

		@Override
		public Group shrinkLeft(int numColumns) {
			for (IConstrainer c : constrainers) {
				c.shrinkLeft(numColumns);
			}
			return this;
		}

		@Override
		public Group shrinkRight(int numColumns) {
			for (IConstrainer c : constrainers) {
				c.shrinkRight(numColumns);
			}
			return this;
		}

		@Override
		public Group shrinkUp(int numRows) {
			for (IConstrainer c : constrainers) {
				c.shrinkUp(numRows);
			}
			return this;
		}

		@Override
		public Group south() {
			for (IConstrainer constrainer : constrainers) {
				constrainer.south();
			}
			return this;
		}

		@Override
		public Group southeast() {
			for (IConstrainer constrainer : constrainers) {
				constrainer.southeast();
			}
			return this;
		}

		@Override
		public Group southwest() {
			for (IConstrainer constrainer : constrainers) {
				constrainer.southwest();
			}
			return this;
		}

		@Override
		public Group weight(double weightx, double weighty) {
			for (IConstrainer constrainer : constrainers) {
				constrainer.weight(weightx, weighty);
			}
			return this;
		}

		@Override
		public Group weightx(double weightx) {
			for (IConstrainer constrainer : constrainers) {
				constrainer.weightx(weightx);
			}
			return this;
		}

		@Override
		public Group weighty(double weighty) {
			for (IConstrainer constrainer : constrainers) {
				constrainer.weighty(weighty);
			}
			return this;
		}

		@Override
		public Group west() {
			for (IConstrainer constrainer : constrainers) {
				constrainer.west();
			}
			return this;
		}

		@Override
		public Group width(int width) {
			for (IConstrainer constrainer : constrainers) {
				constrainer.width(width);
			}
			return this;
		}

		@Override
		public Group x(int x) {
			for (IConstrainer constrainer : constrainers) {
				constrainer.x(x);
			}
			return this;
		}

		@Override
		public Group xy(int x, int y) {
			for (IConstrainer constrainer : constrainers) {
				constrainer.xy(x, y);
			}
			return this;
		}

		@Override
		public Group y(int y) {
			for (IConstrainer constrainer : constrainers) {
				constrainer.y(y);
			}
			return this;
		}
	}

	public interface IConstrainer {

		/**
		 * Places the target component above the given component: sets the
		 * target component's {@link GridBagConstraints#gridy gridy} so that it
		 * is directly above the given component (accounting for
		 * {@link GridBagConstraints#gridheight gridheight}), and if the target
		 * component's {@link GridBagConstraints#gridx gridx} has not been set,
		 * sets it to the same value as for the given component. If the target
		 * component's {@code gridheight} is {@link GridBagConstraints#REMAINDER
		 * REMAINDER}, the result is undefined.
		 *
		 * @param comp
		 *            the component to place the target component above.
		 * @return this {@link IConstrainer}, for chaining.
		 */
		public abstract IConstrainer above(Object obj);

		/**
		 * Places the target component above the last
		 *
		 * {@link GridBagWizard#put(Component) added} component or
		 * {@link GridBagWizard#put(Object...) components}: sets the target
		 * component's {@link GridBagConstraints#gridy gridy} so that it is
		 * directly above the given component (accounting for
		 * {@link GridBagConstraints#gridheight gridheight}), and if the target
		 * component's {@link GridBagConstraints#gridx gridx} has not been set,
		 * sets it to the same value as for the given component. If the target
		 * component's {@code gridheight} is {@link GridBagConstraints#REMAINDER
		 * REMAINDER}, the result is undefined.
		 *
		 * @return this {@link IConstrainer}, for chaining.
		 */
		public abstract IConstrainer aboveLast();

		/**
		 * Adds to the {@link GridBagConstraints#insets insets} of the target
		 * {@link Component}(s).
		 *
		 * @param top
		 *            the value to add to {@link Insets#top top}.
		 * @param left
		 *            the value to add to {@link Insets#left left}.
		 * @param bottom
		 *            the value to add to {@link Insets#bottom bottom}.
		 * @param right
		 *            the value to add to {@link Insets#right right}.
		 * @return this {@link IConstrainer}, for chaining.
		 */
		public abstract IConstrainer addToInsets(int top, int left, int bottom, int right);

		/**
		 * Bottom-aligns the target component with the given component: sets the
		 * target component's {@link GridBagConstraints#gridy gridy} so that
		 * both components occupy the same bottommost column (accounting for
		 * {@link GridBagConstraints#gridheight gridheight}).
		 *
		 * @param comp
		 *            the component to align the target component to the bottom
		 *            of.
		 * @return this {@link IConstrainer}, for chaining.
		 */
		public abstract IConstrainer alignBottom(Object obj);

		/**
		 * Bottom-aligns the target component with the last
		 *
		 * {@link GridBagWizard#put(Component) added} component or
		 * {@link GridBagWizard#put(Object...) components}: sets the target
		 * component's {@link GridBagConstraints#gridy gridy} so that both
		 * components occupy the same bottommost column (accounting for
		 * {@link GridBagConstraints#gridheight gridheight}).
		 *
		 * @return this {@link IConstrainer}, for chaining.
		 */
		public abstract IConstrainer alignBottomToLast();

		/**
		 * Right-aligns the target component with the given component: sets the
		 * target component's {@link GridBagConstraints#gridx gridx} so that
		 * both components occupy the same rightmost column (accounting for
		 * {@link GridBagConstraints#gridwidth gridwidth}).
		 *
		 * @param comp
		 *            the component to align the target component to the right
		 *            of.
		 * @return this {@link IConstrainer}, for chaining.
		 */
		public abstract IConstrainer alignRight(Object obj);

		/**
		 * Right-aligns the target component with the last
		 *
		 * {@link GridBagWizard#put(Component) added} component or
		 * {@link GridBagWizard#put(Object...) components}: sets the target
		 * component's {@link GridBagConstraints#gridx gridx} so that both
		 * components occupy the same rightmost column (accounting for
		 * {@link GridBagConstraints#gridwidth gridwidth} ).
		 *
		 * @return this {@link IConstrainer}, for chaining.
		 */
		public abstract IConstrainer alignRightToLast();

		/**
		 * Sets the {@link GridBagConstraints#anchor anchor} of the target
		 * {@link Component}(s).
		 *
		 * @param anchor
		 *            the new value for {@code anchor}.
		 * @return this {@link IConstrainer}, for chaining.
		 */
		public abstract IConstrainer anchor(int anchor);

		/**
		 * Sets the {@link GridBagConstraints#insets insets} automatically,
		 * based on the {@link #autoinsets()}.
		 *
		 * @return this {@link IConstrainer}, for chaining.
		 */
		public abstract IConstrainer autoinsets();

		/**
		 * Sets the {@link AutoInsets}. If non-{@code null}, it will
		 * automatically set the {@link GridBagConstraints#insets insets} now
		 * and whenever {@link GridBagConstraints#gridx gridx} or
		 * {@link GridBagConstraints#gridy gridy} is changed via a
		 * {@link Constrainer} method.
		 *
		 * @param autoinsets
		 *            the new {@code AutoInsets} to use.
		 * @return this {@link IConstrainer}, for chaining.
		 */
		public abstract IConstrainer autoinsets(AutoInsets autoinsets);

		/**
		 * Sets the {@link AutoInsets} to the same as the given component.
		 *
		 * @param comp
		 *            the component to get the {@code AutoInsets} from.
		 * @return this {@link IConstrainer}, for chaining.
		 * @see #autoinsets(AutoInsets)
		 */
		public abstract IConstrainer autoinsets(Object obj);

		/**
		 * Places the target component below the given component: sets the
		 * target component's {@link GridBagConstraints#gridy gridy} so that it
		 * is directly below the given component (accounting for
		 * {@link GridBagConstraints#gridheight gridheight}), and if the target
		 * component's {@link GridBagConstraints#gridx gridx} has not been set,
		 * sets it to the same value as for the given component. If the given
		 * component's {@code gridheight} is {@link GridBagConstraints#REMAINDER
		 * REMAINDER}, the result is undefined.
		 *
		 * @param comp
		 *            the component to place the target component below.
		 * @return this {@link IConstrainer}, for chaining.
		 */
		public abstract IConstrainer below(Object obj);

		/**
		 * Places the target component below the bottommost component: sets the
		 * target component's {@link GridBagConstraints#gridy gridy} so that it
		 * is directly below the bottommost component (accounting for
		 * {@link GridBagConstraints#gridheight gridheight}).
		 *
		 * @return this {@link IConstrainer}, for chaining.
		 */
		public abstract IConstrainer belowAll();

		/**
		 * Places the target component below the last
		 *
		 * {@link GridBagWizard#put(Component) added} component or
		 * {@link GridBagWizard#put(Object...) components}: sets the target
		 * component's {@link GridBagConstraints#gridy gridy} so that it is
		 * directly below the given component (accounting for
		 * {@link GridBagConstraints#gridheight gridheight}), and if the target
		 * component's {@link GridBagConstraints#gridx gridx} has not been set,
		 * sets it to the same value as for the given component. If the given
		 * component's {@code gridheight} is {@link GridBagConstraints#REMAINDER
		 * REMAINDER}, the result is undefined.
		 *
		 * @return this {@link IConstrainer}, for chaining.
		 */
		public abstract IConstrainer belowLast();

		/**
		 * Sets the {@link #autoinsets() autoinsets} to a
		 * {@link DefaultAutoInsets} with the given spacings.
		 *
		 * @param xspacing
		 *            the desired spacing between columns.
		 * @param yspacing
		 *            the desired spacing between rows.
		 * @return this {@link IConstrainer}, for chaining.
		 */
		public abstract IConstrainer defaultAutoinsets(int xspacing, int yspacing);

		/**
		 * Sets the {@link GridBagConstraints#anchor anchor} of the target
		 * {@link Component}(s) to {@link GridBagConstraints#EAST EAST}.
		 *
		 * @return this {@link IConstrainer}, for chaining.
		 */
		public abstract IConstrainer east();

		/**
		 * Sets the {@link GridBagConstraints#fill fill} of the target
		 * {@link Component}(s).
		 *
		 * @param fill
		 *            the new value for {@code fill}.
		 * @return this {@link IConstrainer}, for chaining.
		 */
		public abstract IConstrainer fill(int fill);

		/**
		 * Sets the {@link GridBagConstraints#fill fill} of the target
		 * {@link Component}(s) to {@link GridBagConstraints#BOTH BOTH}.
		 *
		 * @return this {@link IConstrainer}, for chaining.
		 */
		public abstract IConstrainer fillboth();

		/**
		 * Sets the {@link GridBagConstraints#fill fill} of the target
		 * {@link Component}(s) to {@link GridBagConstraints#BOTH BOTH} and sets
		 * the {@link GridBagConstraints#weightx} and
		 * {@link GridBagConstraints#weighty}.
		 *
		 * @param weightx
		 *            the new value for {@code weightx}.
		 * @param weighty
		 *            the new value for {@code weighty}.
		 *
		 * @return this {@link IConstrainer}, for chaining.
		 */
		public abstract IConstrainer fillboth(double weightx, double weighty);

		/**
		 * Sets the {@link GridBagConstraints#fill fill} of the target
		 * {@link Component}(s) to {@link GridBagConstraints#HORIZONTAL
		 * HORIZONTAL} .
		 *
		 * @return this {@link IConstrainer}, for chaining.
		 */
		public abstract IConstrainer fillx();

		/**
		 * Sets the {@link GridBagConstraints#fill fill} of the target
		 * {@link Component}(s) to {@link GridBagConstraints#HORIZONTAL
		 * HORIZONTAL} and sets the {@link GridBagConstraints#weightx}.
		 *
		 * @param weightx
		 *            the new value for {@code weightx}.
		 *
		 * @return this {@link IConstrainer}, for chaining.
		 */
		public abstract IConstrainer fillx(double weightx);

		/**
		 * Sets the {@link GridBagConstraints#fill fill} of the target
		 * {@link Component}(s) to {@link GridBagConstraints#VERTICAL VERTICAL}.
		 *
		 * @return this {@link IConstrainer}, for chaining.
		 */
		public abstract IConstrainer filly();

		/**
		 * Sets the {@link GridBagConstraints#fill fill} of the target
		 * {@link Component}(s) to {@link GridBagConstraints#VERTICAL VERTICAL}
		 * and sets the {@link GridBagConstraints#weighty}.
		 *
		 * @param weighty
		 *            the new value for {@code weighty}.
		 *
		 * @return this {@link IConstrainer}, for chaining.
		 */
		public abstract IConstrainer filly(double weighty);

		/**
		 * Increases the {@link GridBagConstraints#gridheight gridheight} of the
		 * target {@link Component}(s) to by the given number of rows.
		 *
		 * @return this {@link IConstrainer}, for chaining.
		 */
		public abstract IConstrainer growDown(int numRows);

		/**
		 * Increases the {@link GridBagConstraints#gridwidth gridwidth} and
		 * decreases the {@link GridBagConstraints#gridx gridx} of the target
		 * {@link Component} (s) to by the given number of columns.
		 *
		 * @return this {@link IConstrainer}, for chaining.
		 */
		public abstract IConstrainer growLeft(int numColumns);

		/**
		 * Increases the {@link GridBagConstraints#gridwidth gridwidth} of the
		 * target {@link Component}(s) to by the given number of columns.
		 *
		 * @return this {@link IConstrainer}, for chaining.
		 */
		public abstract IConstrainer growRight(int numColumns);

		/**
		 * Increases the {@link GridBagConstraints#gridheight gridheight} and
		 * decreases the {@link GridBagConstraints#gridy gridy} of the target
		 * {@link Component}(s) to by the given number of rows.
		 *
		 * @return this {@link IConstrainer}, for chaining.
		 */
		public abstract IConstrainer growUp(int numRows);

		/**
		 * Sets the {@link GridBagConstraints#gridheight gridheight} of the
		 * target {@link Component}(s).
		 *
		 * @param height
		 *            the new value for {@code gridheight}.
		 * @return this {@link IConstrainer}, for chaining.
		 */
		public abstract IConstrainer height(int height);

		/**
		 * Sets the {@link GridBagConstraints#insets insets} of the target
		 * {@link Component}(s).
		 *
		 * @param insets
		 *            the new value for {@code insets}.
		 * @return this {@link IConstrainer}, for chaining.
		 */
		public abstract IConstrainer insets(Insets insets);

		/**
		 * Sets the {@link GridBagConstraints#insets insets} of the target
		 * {@link Component}(s).
		 *
		 * @param top
		 *            the value for {@link Insets#top top}.
		 * @param left
		 *            the value for {@link Insets#left left}.
		 * @param bottom
		 *            the value for {@link Insets#bottom bottom}.
		 * @param right
		 *            the value for {@link Insets#right right}.
		 * @return this {@link IConstrainer}, for chaining.
		 */
		public abstract IConstrainer insets(int top, int left, int bottom, int right);

		/**
		 * Sets the {@link GridBagConstraints#ipady ipadx} and
		 * {@link GridBagConstraints#ipady ipady} of the target
		 * {@link Component}(s).
		 *
		 * @param ipadx
		 *            the new value for {@code ipadx}.
		 * @param ipady
		 *            the new value for {@code ipady}.
		 * @return this {@link IConstrainer}, for chaining.
		 */
		public abstract IConstrainer ipad(int ipadx, int ipady);

		/**
		 * Sets the {@link GridBagConstraints#ipadx ipadx} of the target
		 * {@link Component}(s).
		 *
		 * @param ipadx
		 *            the new value for {@code ipadx}.
		 * @return this {@link IConstrainer}, for chaining.
		 */
		public abstract IConstrainer ipadx(int ipadx);

		/**
		 * Sets the {@link GridBagConstraints#ipady ipady} of the target
		 * {@link Component}(s).
		 *
		 * @param ipady
		 *            the new value for {@code ipady}.
		 * @return this {@link IConstrainer}, for chaining.
		 */
		public abstract IConstrainer ipady(int ipady);

		/**
		 * Sets the {@link GridBagConstraints#anchor anchor} of the target
		 * {@link Component}(s) to the same value as the last
		 *
		 * {@link GridBagWizard#put(Component) added} component or
		 * {@link GridBagWizard#put(Object...) components} has.
		 *
		 * @return this {@link IConstrainer}, for chaining.
		 */
		public abstract IConstrainer lastAnchor();

		/**
		 * Sets the {@link AutoInsets} to the same as the last
		 * {@link GridBagWizard#put(Component) added} component or
		 * {@link GridBagWizard#put(Object...) components}.
		 *
		 * @return this {@link IConstrainer}, for chaining.
		 * @see #autoinsets(AutoInsets)
		 */
		public abstract IConstrainer lastAutoinsets();

		/**
		 * Sets the {@link GridBagConstraints#fill fill} of the target
		 * {@link Component}(s) to the same value as the last
		 *
		 * {@link GridBagWizard#put(Component) added} component or
		 * {@link GridBagWizard#put(Object...) components} has.
		 *
		 * @return this {@link IConstrainer}, for chaining.
		 */
		public abstract IConstrainer lastFill();

		/**
		 * Sets the {@link GridBagConstraints#gridheight gridheight} of the
		 * target {@link Component}(s) to the same value as the last
		 *
		 * {@link GridBagWizard#put(Component) added} component or
		 * {@link GridBagWizard#put(Object...) components} has.
		 *
		 * @return this {@link IConstrainer}, for chaining.
		 */
		public abstract IConstrainer lastHeight();

		/**
		 * Sets the {@link GridBagConstraints#insets insets} of the target
		 * {@link Component}(s) to the same value as the last
		 *
		 * {@link GridBagWizard#put(Component) added} component or
		 * {@link GridBagWizard#put(Object...) components} has.
		 *
		 * @return this {@link IConstrainer}, for chaining.
		 */
		public abstract IConstrainer lastInsets();

		/**
		 * Sets the {@link GridBagConstraints#ipady ipadx} and
		 * {@link GridBagConstraints#ipady ipady} of the target
		 * {@link Component}(s) to the same value as the last
		 *
		 * {@link GridBagWizard#put(Component) added} component or
		 * {@link GridBagWizard#put(Object...) components} has.
		 *
		 * @return this {@link IConstrainer}, for chaining.
		 */
		public abstract IConstrainer lastIpad();

		/**
		 * Sets the {@link GridBagConstraints#ipadx ipadx} of the target
		 * {@link Component}(s) to the same value as the last
		 *
		 * {@link GridBagWizard#put(Component) added} component or
		 * {@link GridBagWizard#put(Object...) components} has.
		 *
		 * @return this {@link IConstrainer}, for chaining.
		 */
		public abstract IConstrainer lastIpadx();

		/**
		 * Sets the {@link GridBagConstraints#ipady ipady} of the target
		 * {@link Component}(s) to the same value as the last
		 *
		 * {@link GridBagWizard#put(Component) added} component or
		 * {@link GridBagWizard#put(Object...) components} has.
		 *
		 * @return this {@link IConstrainer}, for chaining.
		 */
		public abstract IConstrainer lastIpady();

		/**
		 * Sets the {@link GridBagConstraints#weighty weightx} and
		 * {@link GridBagConstraints#weighty weighty} of the target
		 * {@link Component}(s) to the same value as the last
		 *
		 * {@link GridBagWizard#put(Component) added} component or
		 * {@link GridBagWizard#put(Object...) components} has.
		 *
		 * @return this {@link IConstrainer}, for chaining.
		 */
		public abstract IConstrainer lastWeight();

		/**
		 * Sets the {@link GridBagConstraints#weightx weightx} of the target
		 * {@link Component}(s) to the same value as the last
		 *
		 * {@link GridBagWizard#put(Component) added} component or
		 * {@link GridBagWizard#put(Object...) components} has.
		 *
		 * @return this {@link IConstrainer}, for chaining.
		 */
		public abstract IConstrainer lastWeightx();

		/**
		 * Sets the {@link GridBagConstraints#weighty weighty} of the target
		 * {@link Component}(s) to the same value as the last
		 *
		 * {@link GridBagWizard#put(Component) added} component or
		 * {@link GridBagWizard#put(Object...) components} has.
		 *
		 * @return this {@link IConstrainer}, for chaining.
		 */
		public abstract IConstrainer lastWeighty();

		/**
		 * Sets the {@link GridBagConstraints#gridwidth gridwidth} of the target
		 * {@link Component}(s) to the same value as the last
		 *
		 * {@link GridBagWizard#put(Component) added} component or
		 * {@link GridBagWizard#put(Object...) components} has.
		 *
		 * @return this {@link IConstrainer}, for chaining.
		 */
		public abstract IConstrainer lastWidth();

		/**
		 * Sets the {@link GridBagConstraints#gridx gridx} of the target
		 * {@link Component}(s) to the same value as the last
		 *
		 * {@link GridBagWizard#put(Component) added} component or
		 * {@link GridBagWizard#put(Object...) components}' {@code gridx}.
		 *
		 * @return this {@link IConstrainer}, for chaining.
		 */
		public abstract IConstrainer lastX();

		/**
		 * Sets the {@link GridBagConstraints#gridy gridy} of the target
		 * {@link Component}(s) to the same value as the last
		 *
		 * {@link GridBagWizard#put(Component) added} component or
		 * {@link GridBagWizard#put(Object...) components}' {@code gridy}.
		 *
		 * @return this {@link IConstrainer}, for chaining.
		 */
		public abstract IConstrainer lastY();

		/**
		 * Places the target component left of the given component: sets the
		 * target component's {@link GridBagConstraints#gridx gridx} so that it
		 * is directly left of the given component (accounting for
		 * {@link GridBagConstraints#gridwidth gridwidth}), and if the target
		 * component's {@link GridBagConstraints#gridy gridy} has not been set,
		 * sets it to the same value as for the given component. If the target
		 * component's {@code gridwidth} is {@link GridBagConstraints#REMAINDER
		 * REMAINDER}, the result is undefined.
		 *
		 * @param comp
		 *            the component to place the target component left of.
		 * @return this {@link IConstrainer}, for chaining.
		 */
		public abstract IConstrainer leftOf(Object obj);

		/**
		 * Places the target component left of the last
		 *
		 * {@link GridBagWizard#put(Component) added} component or
		 * {@link GridBagWizard#put(Object...) components}: sets the target
		 * component's {@link GridBagConstraints#gridx gridx} so that it is
		 * directly left of the given component (accounting for
		 * {@link GridBagConstraints#gridwidth gridwidth}), and if the target
		 * component's {@link GridBagConstraints#gridy gridy} has not been set,
		 * sets it to the same value as for the given component. If the target
		 * component's {@code gridwidth} is {@link GridBagConstraints#REMAINDER
		 * REMAINDER}, the result is undefined.
		 *
		 * @return this {@link IConstrainer}, for chaining.
		 */
		public abstract IConstrainer leftOfLast();

		/**
		 * Sets the {@link GridBagConstraints#fill fill} of the target
		 * {@link Component}(s) to {@link GridBagConstraints#NONE NONE}.
		 *
		 * @return this {@link IConstrainer}, for chaining.
		 */
		public abstract IConstrainer nofill();

		/**
		 * Sets the {@link GridBagConstraints#anchor anchor} of the target
		 * {@link Component}(s) to {@link GridBagConstraints#NORTH NORTH}.
		 *
		 * @return this {@link IConstrainer}, for chaining.
		 */
		public abstract IConstrainer north();

		/**
		 * Sets the {@link GridBagConstraints#anchor anchor} of the target
		 * {@link Component}(s) to {@link GridBagConstraints#NORTHEAST
		 * NORTHEAST}.
		 *
		 * @return this {@link IConstrainer}, for chaining.
		 */
		public abstract IConstrainer northeast();

		/**
		 * Sets the {@link GridBagConstraints#anchor anchor} of the target
		 * {@link Component}(s) to {@link GridBagConstraints#NORTHWEST
		 * NORTHWEST}.
		 *
		 * @return this {@link IConstrainer}, for chaining.
		 */
		public abstract IConstrainer northwest();

		/**
		 * Sets the {@link GridBagConstraints#gridheight gridheight} of the
		 * target {@link Component}(s) to {@link GridBagConstraints#REMAINDER
		 * REMAINDER}.
		 *
		 * @return this {@link IConstrainer}, for chaining.
		 */
		public abstract IConstrainer remHeight();

		/**
		 * Sets the {@link GridBagConstraints#gridwidth gridwidth} of the target
		 * {@link Component}(s) to {@link GridBagConstraints#REMAINDER
		 * REMAINDER}.
		 *
		 * @return this {@link IConstrainer}, for chaining.
		 */
		public abstract IConstrainer remWidth();

		/**
		 * Places the target component right of the given component: sets the
		 * target component's {@link GridBagConstraints#gridx gridx} so that it
		 * is directly right of the given component (accounting for
		 * {@link GridBagConstraints#gridwidth gridwidth}), and if the target
		 * component's {@link GridBagConstraints#gridy gridy} has not been set,
		 * sets it to the same value as for the given component. If the given
		 * component's {@code gridwidth} is {@link GridBagConstraints#REMAINDER
		 * REMAINDER}, the result is undefined.
		 *
		 * @param comp
		 *            the component to place the target component right of.
		 * @return this {@link IConstrainer}, for chaining.
		 */
		public abstract IConstrainer rightOf(Object obj);

		/**
		 * Places the target component right of the rightmost component: sets
		 * the target component's {@link GridBagConstraints#gridx gridx} so that
		 * it is directly right of the rightmost component (accounting for
		 * {@link GridBagConstraints#gridwidth gridwidth}).
		 *
		 * @return this {@link IConstrainer}, for chaining.
		 */
		public abstract IConstrainer rightOfAll();

		/**
		 * Places the target component right of the last
		 *
		 * {@link GridBagWizard#put(Component) added} component or
		 * {@link GridBagWizard#put(Object...) components}: sets the target
		 * component's {@link GridBagConstraints#gridx gridx} so that it is
		 * directly right of the given component (accounting for
		 * {@link GridBagConstraints#gridwidth gridwidth}), and if the target
		 * component's {@link GridBagConstraints#gridy gridy} has not been set,
		 * sets it to the same value as for the given component. If the given
		 * component's {@code gridwidth} is {@link GridBagConstraints#REMAINDER
		 * REMAINDER}, the result is undefined.
		 *
		 * @return this {@link IConstrainer}, for chaining.
		 */
		public abstract IConstrainer rightOfLast();

		/**
		 * Sets the {@link GridBagConstraints#anchor anchor} of the target
		 * {@link Component}(s) to the same value as the given component has.
		 *
		 * @param comp
		 *            the {@link Component}(s) to get the {@code anchor} from.
		 * @return this {@link IConstrainer}, for chaining.
		 */
		public abstract IConstrainer sameAnchor(Object obj);

		/**
		 * Sets the {@link GridBagConstraints#fill fill} of the target
		 * {@link Component}(s) to the same value as the given component has.
		 *
		 * @param comp
		 *            the {@link Component}(s) to get the {@code fill} from.
		 * @return this {@link IConstrainer}, for chaining.
		 */
		public abstract IConstrainer sameFill(Object obj);

		/**
		 * Sets the {@link GridBagConstraints#gridheight gridheight} of the
		 * target {@link Component}(s) to the same value as the given component
		 * has.
		 *
		 * @param comp
		 *            the {@link Component}(s) to get the {@code gridheight}
		 *            from.
		 * @return this {@link IConstrainer}, for chaining.
		 */
		public abstract IConstrainer sameHeight(Object obj);

		/**
		 * Sets the {@link GridBagConstraints#insets insets} of the target
		 * {@link Component}(s) to the same value as the given component has.
		 *
		 * @param comp
		 *            the {@link Component}(s) to get the {@code insets} from.
		 * @return this {@link IConstrainer}, for chaining.
		 */
		public abstract IConstrainer sameInsets(Object obj);

		/**
		 * Sets the {@link GridBagConstraints#ipady ipadx} and
		 * {@link GridBagConstraints#ipady ipady} of the target
		 * {@link Component}(s) to the same value as the given component has.
		 *
		 * @param comp
		 *            the {@link Component}(s) to get the {@code ipadx} and
		 *            {@code ipady} from.
		 * @return this {@link IConstrainer}, for chaining.
		 */
		public abstract IConstrainer sameIpad(Object obj);

		/**
		 * Sets the {@link GridBagConstraints#ipadx ipadx} of the target
		 * {@link Component}(s) to the same value as the given component has.
		 *
		 * @param comp
		 *            the {@link Component}(s) to get the {@code ipadx} from.
		 * @return this {@link IConstrainer}, for chaining.
		 */
		public abstract IConstrainer sameIpadx(Object obj);

		/**
		 * Sets the {@link GridBagConstraints#ipady ipady} of the target
		 * {@link Component}(s) to the same value as the given component has.
		 *
		 * @param comp
		 *            the {@link Component}(s) to get the {@code ipady} from.
		 * @return this {@link IConstrainer}, for chaining.
		 */
		public abstract IConstrainer sameIpady(Object obj);

		/**
		 * Sets the {@link GridBagConstraints#weighty weightx} and
		 * {@link GridBagConstraints#weighty weighty} of the target
		 * {@link Component}(s) to the same value as the given component has.
		 *
		 * @param comp
		 *            the {@link Component}(s) to get the {@code weightx} and
		 *            {@code weighty} from.
		 * @return this {@link IConstrainer}, for chaining.
		 */
		public abstract IConstrainer sameWeight(Object obj);

		/**
		 * Sets the {@link GridBagConstraints#weightx weightx} of the target
		 * {@link Component}(s) to the same value as the given component has.
		 *
		 * @param comp
		 *            the {@link Component}(s) to get the {@code weightx} from.
		 * @return this {@link IConstrainer}, for chaining.
		 */
		public abstract IConstrainer sameWeightx(Object obj);

		/**
		 * Sets the {@link GridBagConstraints#weighty weighty} of the target
		 * {@link Component}(s) to the same value as the given component has.
		 *
		 * @param comp
		 *            the {@link Component}(s) to get the {@code weighty} from.
		 * @return this {@link IConstrainer}, for chaining.
		 */
		public abstract IConstrainer sameWeighty(Object obj);

		/**
		 * Sets the {@link GridBagConstraints#gridwidth gridwidth} of the target
		 * {@link Component}(s) to the same value as the given component has.
		 *
		 * @param comp
		 *            the {@link Component}(s) to get the {@code gridwidth}
		 *            from.
		 * @return this {@link IConstrainer}, for chaining.
		 */
		public abstract IConstrainer sameWidth(Object obj);

		/**
		 * Sets the {@link GridBagConstraints#gridx gridx} of the target
		 * {@link Component}(s) to the same value as another component's
		 * {@code gridx}.
		 *
		 * @param comp
		 *            the component to get the {@code gridx} from.
		 * @return this {@link IConstrainer}, for chaining.
		 */
		public abstract IConstrainer sameX(Object obj);

		/**
		 * Sets the {@link GridBagConstraints#gridy gridy} of the target
		 * {@link Component}(s) to the same value as the last
		 *
		 * {@link GridBagWizard#put(Component) added} component or
		 * {@link GridBagWizard#put(Object...) components}' {@code gridy}.
		 *
		 * @param comp
		 *            the component to get the {@code gridy} from.
		 * @return this {@link IConstrainer}, for chaining.
		 */
		public abstract IConstrainer sameY(Object obj);

		/**
		 * Decreases the {@link GridBagConstraints#gridheight gridheight} and
		 * increases the {@link GridBagConstraints#gridy gridy} of the target
		 * {@link Component}(s) to by the given number of rows.
		 *
		 * @return this {@link IConstrainer}, for chaining.
		 */
		public abstract IConstrainer shrinkDown(int numRows);

		/**
		 * Decreases the {@link GridBagConstraints#gridwidth gridwidth} of the
		 * target {@link Component}(s) to by the given number of columns.
		 *
		 * @return this {@link IConstrainer}, for chaining.
		 */
		public abstract IConstrainer shrinkLeft(int numColumns);

		/**
		 * Decreases the {@link GridBagConstraints#gridwidth gridwidth} and
		 * increases the {@link GridBagConstraints#gridx gridx} of the target
		 * {@link Component} (s) to by the given number of columns.
		 *
		 * @return this {@link IConstrainer}, for chaining.
		 */
		public abstract IConstrainer shrinkRight(int numColumns);

		/**
		 * Decreases the {@link GridBagConstraints#gridheight gridheight} of the
		 * target {@link Component}(s) to by the given number of rows.
		 *
		 * @return this {@link IConstrainer}, for chaining.
		 */
		public abstract IConstrainer shrinkUp(int numRows);

		/**
		 * Sets the {@link GridBagConstraints#anchor anchor} of the target
		 * {@link Component}(s) to {@link GridBagConstraints#SOUTH SOUTH}.
		 *
		 * @return this {@link IConstrainer}, for chaining.
		 */
		public abstract IConstrainer south();

		/**
		 * Sets the {@link GridBagConstraints#anchor anchor} of the target
		 * {@link Component}(s) to {@link GridBagConstraints#SOUTHEAST
		 * SOUTHEAST}.
		 *
		 * @return this {@link IConstrainer}, for chaining.
		 */
		public abstract IConstrainer southeast();

		/**
		 * Sets the {@link GridBagConstraints#anchor anchor} of the target
		 * {@link Component}(s) to {@link GridBagConstraints#SOUTHWEST
		 * SOUTHWEST}.
		 *
		 * @return this {@link IConstrainer}, for chaining.
		 */
		public abstract IConstrainer southwest();

		/**
		 * Sets the {@link GridBagConstraints#weighty weightx} and
		 * {@link GridBagConstraints#weighty weighty} of the target
		 * {@link Component}(s).
		 *
		 * @param weightx
		 *            the new value for {@code weightx}.
		 * @param weighty
		 *            the new value for {@code weighty}.
		 * @return this {@link IConstrainer}, for chaining.
		 */
		public abstract IConstrainer weight(double weightx, double weighty);

		/**
		 * Sets the {@link GridBagConstraints#weightx weightx} of the target
		 * {@link Component}(s).
		 *
		 * @param weightx
		 *            the new value for {@code weightx}.
		 * @return this {@link IConstrainer}, for chaining.
		 */
		public abstract IConstrainer weightx(double weightx);

		/**
		 * Sets the {@link GridBagConstraints#weighty weighty} of the target
		 * {@link Component}(s).
		 *
		 * @param weighty
		 *            the new value for {@code weighty}.
		 * @return this {@link IConstrainer}, for chaining.
		 */
		public abstract IConstrainer weighty(double weighty);

		/**
		 * Sets the {@link GridBagConstraints#anchor anchor} of the target
		 * {@link Component}(s) to {@link GridBagConstraints#WEST WEST}.
		 *
		 * @return this {@link IConstrainer}, for chaining.
		 */
		public abstract IConstrainer west();

		/**
		 * Sets the {@link GridBagConstraints#gridwidth gridwidth} of the target
		 * {@link Component}(s).
		 *
		 * @param width
		 *            the new value for {@code gridwidth}.
		 * @return this {@link IConstrainer}, for chaining.
		 */
		public abstract IConstrainer width(int width);

		/**
		 * Sets the {@link GridBagConstraints#gridx gridx} of the target
		 * {@link Component}(s).
		 *
		 * @param x
		 *            the new value for {@code gridx}.
		 * @return this {@link IConstrainer}, for chaining.
		 */
		public abstract IConstrainer x(int x);

		/**
		 * Sets the {@link GridBagConstraints#gridx gridx} and
		 * {@link GridBagConstraints#gridy gridy} of the target
		 * {@link Component}(s).
		 *
		 * @param x
		 *            the new value for {@code gridx}.
		 * @param y
		 *            the new value for {@code gridy}.
		 * @return this {@link IConstrainer}, for chaining.
		 */
		public abstract IConstrainer xy(int x, int y);

		/**
		 * Sets the {@link GridBagConstraints#gridy gridy} of the target
		 * {@link Component}(s).
		 *
		 * @param y
		 *            the new value for {@code gridy}.
		 * @return this {@link IConstrainer}, for chaining.
		 */
		public abstract IConstrainer y(int y);
	}

	/**
	 * Creates a {@code GridBagWizard} for adding components to the given target
	 * {@link Container}. If the container's layout is not a
	 * {@link GridBagLayout}, it will be replaced with a new
	 * {@link GridBagLayout}, and all previous layout information will be lost.
	 *
	 * @param target
	 *            the container to create a {@code GridBagWizard} for.
	 * @return a new {@code GridBagWizard} that {@link #put(Component) adds}
	 *         components to {@code target}.
	 */
	public static GridBagWizard create(Container target) {
		if (!(target.getLayout() instanceof GridBagLayout)) {
			target.setLayout(new GridBagLayout());
		}
		GridBagWizard gbh = new GridBagWizard(target);
		return gbh;
	}

	public static GridBagWizard quickPanel() {
		JPanel panel = new JPanel();
		panel.setOpaque(false);
		return GridBagWizard.create(panel);
	}

	private static void set(Insets dest, Insets src) {
		dest.top = src.top;
		dest.left = src.left;
		dest.bottom = src.bottom;
		dest.right = src.right;
	}

	private static void set(Insets dest, int top, int left, int bottom, int right) {
		dest.top = top;
		dest.left = left;
		dest.bottom = bottom;
		dest.right = right;
	}

	private final Constrainer defaultConstrainer = new Constrainer(new GridBagConstraints());

	private final Map<Component, Constrainer> constrainers = new HashMap<Component, Constrainer>();

	private final Container target;

	private final GridBagLayout layout;

	private IConstrainer last;

	private IConstrainer current;

	private GridBagWizard(Container target) {
		this.target = target;
		layout = (GridBagLayout) target.getLayout();
	}

	/**
	 * Gets the {@link GridBagConstraints#anchor anchor} set for a given
	 * component or {@link IConstrainer}.
	 *
	 * @param obj
	 *            the component or {@link IConstrainer} to get the
	 *            {@code anchor} of.
	 * @return the {@code anchor} of {@code obj}'s {@link GridBagConstraints}
	 */
	public int anchor(Object obj) {
		if (obj instanceof Component) {
			return layout.getConstraints((Component) obj).anchor;
		} else if (obj instanceof Constrainer) {
			return ((Constrainer) obj).gbc.anchor;
		} else if (obj instanceof Group) {
			Integer anchor = null;
			Group g = (Group) obj;
			for (IConstrainer constrainer : g.constrainers) {
				int nextanchor = anchor(constrainer);
				if (anchor == null) {
					anchor = nextanchor;
				} else if (anchor != nextanchor) {
					throw new IllegalArgumentException("anchor is ambiguous; Group members have various anchor values");
				}
			}
		}
		throw new IllegalArgumentException("obj must be a Component or IConstrainer");
	}

	/**
	 * Gets the {@link AutoInsets} set for a given component or
	 * {@link IConstrainer}.
	 *
	 * @param obj
	 *            the component or {@link IConstrainer} to get the
	 *            {@code AutoInsets} of.
	 * @return the {@code AutoInsets} set for {@code obj}.
	 */
	public AutoInsets autoinsets(Object obj) {
		if (obj instanceof Component) {
			Constrainer constrainer = constrainers.get(obj);
			return constrainer != null ? constrainer.autoinsets : defaultConstrainer.autoinsets;
		} else if (obj instanceof Constrainer) {
			return ((Constrainer) obj).autoinsets;
		} else if (obj instanceof Group) {
			AutoInsets autoinsets = null;
			Group g = (Group) obj;
			for (IConstrainer constrainer : g.constrainers) {
				AutoInsets nextautoinsets = autoinsets(constrainer);
				if (autoinsets == null) {
					autoinsets = nextautoinsets;
				} else if (!Java7.Objects.equals(autoinsets, nextautoinsets)) {
					throw new IllegalArgumentException(
							"autoinsets is ambiguous; Group members have various autoinsets values");
				}
			}
		}
		throw new IllegalArgumentException("obj must be a Component or IConstrainer");
	}

	/**
	 * @return a {@link Constrainer} for setting the default constraints.
	 */
	public Constrainer defaults() {
		return defaultConstrainer;
	}

	/**
	 * Gets the {@link GridBagConstraints#fill fill} set for a given component
	 * or {@link IConstrainer}.
	 *
	 * @param obj
	 *            the component or {@link IConstrainer} to get the {@code fill}
	 *            of.
	 * @return the {@code fill} of {@code obj}'s {@link GridBagConstraints}
	 */
	public int fill(Object obj) {
		if (obj instanceof Component) {
			return layout.getConstraints((Component) obj).fill;
		} else if (obj instanceof Constrainer) {
			return ((Constrainer) obj).gbc.fill;
		} else if (obj instanceof Group) {
			Integer fill = null;
			Group g = (Group) obj;
			for (IConstrainer constrainer : g.constrainers) {
				int nextfill = fill(constrainer);
				if (fill == null) {
					fill = nextfill;
				} else if (fill != nextfill) {
					throw new IllegalArgumentException("fill is ambiguous; Group members have various fill values");
				}
			}
		}
		throw new IllegalArgumentException("obj must be a Component or IConstrainer");
	}

	public Container getTarget() {
		return target;
	}

	/**
	 * Gets the {@link GridBagConstraints#gridheight gridheight} set for a given
	 * component or {@link IConstrainer}.
	 *
	 * @param obj
	 *            the component or {@link IConstrainer} to get the
	 *            {@code gridheight} of.
	 * @return the {@code gridheight} of {@code obj}'s
	 *         {@link GridBagConstraints}
	 */
	public int height(Object obj) {
		if (obj instanceof Component) {
			return layout.getConstraints((Component) obj).gridheight;
		} else if (obj instanceof Constrainer) {
			return ((Constrainer) obj).gbc.gridheight;
		} else if (obj instanceof Group) {
			Integer height = null;
			Group g = (Group) obj;
			for (IConstrainer constrainer : g.constrainers) {
				int nextheight = height(constrainer);
				if (height == null) {
					height = nextheight;
				} else if (height != nextheight) {
					throw new IllegalArgumentException("height is ambiguous; Group members have various height values");
				}
			}
		}
		throw new IllegalArgumentException("obj must be a Component or IConstrainer");
	}

	/**
	 * Gets the number of rows spanned by the given components and/or
	 * {@link IConstrainer}s.
	 *
	 * @param objs
	 *            the components and/or {@link IConstrainer}s to get the width
	 *            of.
	 * @return the number of rows spanned by the given components and/or
	 *         {@link IConstrainer}s.
	 */
	public int height(Object... objs) {
		return maxRow(objs) - minRow(objs) + 1;
	}

	/**
	 * Gets the {@link GridBagConstraints#insets insets} set for a given
	 * component or {@link IConstrainer}.
	 *
	 * @param obj
	 *            the component {@link IConstrainer} to get the {@code insets}
	 *            of.
	 * @return the {@code insets} of {@code obj}'s {@link GridBagConstraints}
	 */
	public Insets insets(Object obj) {
		if (obj instanceof Component) {
			return layout.getConstraints((Component) obj).insets;
		} else if (obj instanceof Constrainer) {
			return ((Constrainer) obj).gbc.insets;
		} else if (obj instanceof Group) {
			Insets insets = null;
			Group g = (Group) obj;
			for (IConstrainer constrainer : g.constrainers) {
				Insets nextinsets = insets(constrainer);
				if (insets == null) {
					insets = nextinsets;
				} else if (!Java7.Objects.equals(insets, nextinsets)) {
					throw new IllegalArgumentException("insets is ambiguous; Group members have various insets values");
				}
			}
		}
		throw new IllegalArgumentException("obj must be a Component or IConstrainer");
	}

	/**
	 * Gets the {@link GridBagConstraints#ipadx ipadx} set for a given component
	 * or {@link IConstrainer}.
	 *
	 * @param obj
	 *            the component or {@link IConstrainer} to get the {@code ipadx}
	 *            of.
	 * @return the {@code ipadx} of {@code obj}'s {@link GridBagConstraints}
	 */
	public int ipadx(Object obj) {
		if (obj instanceof Component) {
			return layout.getConstraints((Component) obj).ipadx;
		} else if (obj instanceof Constrainer) {
			return ((Constrainer) obj).gbc.ipadx;
		} else if (obj instanceof Group) {
			Integer ipadx = null;
			Group g = (Group) obj;
			for (IConstrainer constrainer : g.constrainers) {
				int nextipadx = ipadx(constrainer);
				if (ipadx == null) {
					ipadx = nextipadx;
				} else if (ipadx != nextipadx) {
					throw new IllegalArgumentException("ipadx is ambiguous; Group members have various ipadx values");
				}
			}
		}
		throw new IllegalArgumentException("obj must be a Component or IConstrainer");
	}

	/**
	 * Gets the {@link GridBagConstraints#ipady ipady} set for a given component
	 * or {@link IConstrainer}.
	 *
	 * @param obj
	 *            the component {@link IConstrainer} to get the {@code ipady}
	 *            of.
	 * @return the {@code ipady} of {@code obj}'s {@link GridBagConstraints}
	 */
	public int ipady(Object obj) {
		if (obj instanceof Component) {
			return layout.getConstraints((Component) obj).ipady;
		} else if (obj instanceof Constrainer) {
			return ((Constrainer) obj).gbc.ipady;
		} else if (obj instanceof Group) {
			Integer ipady = null;
			Group g = (Group) obj;
			for (IConstrainer constrainer : g.constrainers) {
				int nextipady = ipady(constrainer);
				if (ipady == null) {
					ipady = nextipady;
				} else if (ipady != nextipady) {
					throw new IllegalArgumentException("ipady is ambiguous; Group members have various ipady values");
				}
			}
		}
		throw new IllegalArgumentException("obj must be a Component or IConstrainer");
	}

	/**
	 * Gets the rightmost column occupied by the given components and/or
	 * {@link IConstrainer}s.
	 *
	 * @param objs
	 *            the components and/or {@link IConstrainer}s
	 */
	public int maxColumn(Object... objs) {
		int result = -1;
		for (Object obj : objs) {
			int column = -1;

			if (obj instanceof Component) {
				Component comp = (Component) obj;
				column = x(comp) + width(comp) - 1;
			} else if (obj instanceof Constrainer) {
				Constrainer c = (Constrainer) obj;
				column = x(c.comp) + width(c.comp) - 1;
			} else if (obj instanceof Group) {
				Group g = (Group) obj;
				column = maxColumn((Object[]) g.constrainers);
			}
			if (column > result) {
				result = column;
			}
		}
		return result;
	}

	/**
	 * Gets the bottommost row occupied by the given components and/or
	 * {@link IConstrainer}s.
	 *
	 * @param objs
	 *            the components and/or {@link IConstrainer}s
	 */
	public int maxRow(Object... objs) {
		int result = -1;
		for (Object obj : objs) {
			int Row = -1;

			if (obj instanceof Component) {
				Component comp = (Component) obj;
				Row = y(comp) + height(comp) - 1;
			} else if (obj instanceof Constrainer) {
				Constrainer c = (Constrainer) obj;
				Row = y(c.comp) + height(c.comp) - 1;
			} else if (obj instanceof Group) {
				Group g = (Group) obj;
				Row = maxRow((Object[]) g.constrainers);
			}
			if (Row > result) {
				result = Row;
			}
		}
		return result;
	}

	/**
	 * Gets the leftmost column occupied by the given components and/or
	 * {@link IConstrainer}s.
	 *
	 * @param objs
	 *            the components and/or {@link IConstrainer}s
	 */
	public int minColumn(Object... objs) {
		int result = -1;
		for (Object obj : objs) {
			int column = -1;

			if (obj instanceof Component) {
				Component comp = (Component) obj;
				column = x(comp);
			} else if (obj instanceof Constrainer) {
				Constrainer c = (Constrainer) obj;
				column = x(c.comp);
			} else if (obj instanceof Group) {
				Group g = (Group) obj;
				column = minColumn((Object[]) g.constrainers);
			}
			if (column >= 0 && (result < 0 || column < result)) {
				result = column;
			}
		}
		return result;
	}

	/**
	 * Gets the topmost row occupied by the given components and/or
	 * {@link IConstrainer}s.
	 *
	 * @param objs
	 *            the components and/or {@link IConstrainer}s
	 */
	public int minRow(Object... objs) {
		int result = -1;
		for (Object obj : objs) {
			int Row = -1;

			if (obj instanceof Component) {
				Component comp = (Component) obj;
				Row = y(comp);
			} else if (obj instanceof Constrainer) {
				Constrainer c = (Constrainer) obj;
				Row = y(c.comp);
			} else if (obj instanceof Group) {
				Group g = (Group) obj;
				Row = minRow((Object[]) g.constrainers);
			}
			if (Row >= 0 && (result < 0 || Row < result)) {
				result = Row;
			}
		}
		return result;
	}

	/**
	 * @return the number of columns in the layout so far.
	 */
	public int numCols() {
		int cols = 0;
		for (int i = 0; i < target.getComponentCount(); i++) {
			GridBagConstraints gbc = layout.getConstraints(target.getComponent(i));
			cols = Math.max(cols, gbc.gridx + Math.max(1, gbc.gridwidth));
		}
		return cols;
	}

	/**
	 * @return the number of rows in the layout so far.
	 */
	public int numRows() {
		int rows = 0;
		for (int i = 0; i < target.getComponentCount(); i++) {
			GridBagConstraints gbc = layout.getConstraints(target.getComponent(i));
			rows = Math.max(rows, gbc.gridy + Math.max(1, gbc.gridheight));
		}
		return rows;
	}

	/**
	 * Adds the given {@link Component} to the target {@link Container} with the
	 * {@link #defaults() default constraints}.
	 *
	 * @param comp
	 *            the {@code Component} to add.
	 * @return a {@link Constrainer} for setting constraints on {@code comp}.
	 */
	public Constrainer put(Component comp) {
		if (current != null) {
			last = current;
		}
		Constrainer constrainer = constrainers.get(comp);
		if (constrainer == null) {
			constrainer = new Constrainer(comp);
			constrainers.put(comp, constrainer);
		}
		current = constrainer;
		return constrainer;
	}

	/**
	 * Adds the given {@link Component Components} and/or {@link IConstrainer
	 * IConstrainers} to the target {@link Container} with the
	 * {@link #defaults() default constraints}. Allows you to set
	 * {@link GridBagConstraints} for all of them at once using the returned
	 * {@link Group}.
	 *
	 * @param items
	 *            the {@code Components} and/or {@code IConstrainers} to add.
	 * @return a {@link Group} for setting constraints on {@code items} in mass.
	 */
	public Group put(Object... items) {
		if (current != null) {
			last = current;
		}
		IConstrainer[] constrainers = new IConstrainer[items.length];
		for (int i = 0; i < items.length; i++) {
			if (items[i] instanceof IConstrainer) {
				constrainers[i] = (IConstrainer) items[i];
			} else {
				Component comp = (Component) items[i];
				Constrainer constrainer = this.constrainers.get(comp);
				if (constrainer == null) {
					constrainer = new Constrainer(comp);
					this.constrainers.put(comp, constrainer);
				}
				constrainers[i] = constrainer;
			}
		}
		Group g = new Group(constrainers);
		current = g;
		return g;
	}

	/**
	 * Gets the {@link GridBagConstraints#weightx weightx} set for a given
	 * component or {@link IConstrainer}.
	 *
	 * @param obj
	 *            the component or {@link IConstrainer} to get the
	 *            {@code weightx} of.
	 * @return the {@code weightx} of {@code obj}'s {@link GridBagConstraints}
	 */
	public double weightx(Object obj) {
		if (obj instanceof Component) {
			return layout.getConstraints((Component) obj).weightx;
		} else if (obj instanceof Constrainer) {
			return ((Constrainer) obj).gbc.weightx;
		} else if (obj instanceof Group) {
			Double weightx = null;
			Group g = (Group) obj;
			for (IConstrainer constrainer : g.constrainers) {
				double nextweightx = weightx(constrainer);
				if (weightx == null) {
					weightx = nextweightx;
				} else if (weightx != nextweightx) {
					throw new IllegalArgumentException(
							"weightx is ambiguous; Group members have various weightx values");
				}
			}
		}
		throw new IllegalArgumentException("obj must be a Component or IConstrainer");
	}

	/**
	 * Gets the {@link GridBagConstraints#weighty weighty} set for a given
	 * component or {@link IConstrainer}.
	 *
	 * @param obj
	 *            the component or {@link IConstrainer} to get the
	 *            {@code weighty} of.
	 * @return the {@code weighty} of {@code obj}'s {@link GridBagConstraints}
	 */
	public double weighty(Object obj) {
		if (obj instanceof Component) {
			return layout.getConstraints((Component) obj).weighty;
		} else if (obj instanceof Constrainer) {
			return ((Constrainer) obj).gbc.weighty;
		} else if (obj instanceof Group) {
			Double weighty = null;
			Group g = (Group) obj;
			for (IConstrainer constrainer : g.constrainers) {
				double nextweighty = weighty(constrainer);
				if (weighty == null) {
					weighty = nextweighty;
				} else if (weighty != nextweighty) {
					throw new IllegalArgumentException(
							"weighty is ambiguous; Group members have various weighty values");
				}
			}
		}
		throw new IllegalArgumentException("obj must be a Component or IConstrainer");
	}

	/**
	 * Gets the {@link GridBagConstraints#gridwidth gridwidth} set for a given
	 * component or {@link IConstrainer}.
	 *
	 * @param obj
	 *            the component or {@link IConstrainer} to get the
	 *            {@code gridwidth} of.
	 * @return the {@code gridwidth} of {@code obj}'s {@link GridBagConstraints}
	 */
	public int width(Object obj) {
		if (obj instanceof Component) {
			return layout.getConstraints((Component) obj).gridwidth;
		} else if (obj instanceof Constrainer) {
			return ((Constrainer) obj).gbc.gridwidth;
		} else if (obj instanceof Group) {
			Integer width = null;
			Group g = (Group) obj;
			for (IConstrainer constrainer : g.constrainers) {
				int nextWidth = width(constrainer);
				if (width == null) {
					width = nextWidth;
				} else if (width != nextWidth) {
					throw new IllegalArgumentException("width is ambiguous; Group members have various width values");
				}
			}
		}
		throw new IllegalArgumentException("obj must be a Component or IConstrainer");
	}

	/**
	 * Gets the number of columns spanned by the given components and/or
	 * {@link IConstrainer}s.
	 *
	 * @param objs
	 *            the components and/or {@link IConstrainer}s to get the width
	 *            of.
	 * @return the number of columns spanned by the given components and/or
	 *         {@link IConstrainer}s.
	 */
	public int width(Object... objs) {
		return maxColumn(objs) - minColumn(objs) + 1;
	}

	/**
	 * Gets the {@link GridBagConstraints#gridx gridx} set for a given component
	 * or {@link IConstrainer}.
	 *
	 * @param obj
	 *            the component or {@link IConstrainer} to get the {@code gridx}
	 *            of.
	 * @return the {@code gridx} of {@code obj}'s {@link GridBagConstraints}
	 */
	public int x(Object obj) {
		if (obj instanceof Component) {
			return layout.getConstraints((Component) obj).gridx;
		} else if (obj instanceof Constrainer) {
			return ((Constrainer) obj).gbc.gridx;
		} else if (obj instanceof Group) {
			Integer x = null;
			Group g = (Group) obj;
			for (IConstrainer constrainer : g.constrainers) {
				int nextX = x(constrainer);
				if (x == null) {
					x = nextX;
				} else if (x != nextX) {
					throw new IllegalArgumentException("x is ambiguous; Group members have various x values");
				}
			}
		}
		throw new IllegalArgumentException("obj must be a Component or IConstrainer");
	}

	/**
	 * Gets the {@link GridBagConstraints#gridy gridy} set for a given component
	 * or {@link IConstrainer}.
	 *
	 * @param obj
	 *            the component or {@link IConstrainer} to get the {@code gridy}
	 *            of.
	 * @return the {@code gridy} of {@code obj}'s {@link GridBagConstraints}
	 */
	public int y(Object obj) {
		if (obj instanceof Component) {
			return layout.getConstraints((Component) obj).gridy;
		} else if (obj instanceof Constrainer) {
			return ((Constrainer) obj).gbc.gridy;
		} else if (obj instanceof Group) {
			Integer y = null;
			Group g = (Group) obj;
			for (IConstrainer constrainer : g.constrainers) {
				int nextY = y(constrainer);
				if (y == null) {
					y = nextY;
				} else if (y != nextY) {
					throw new IllegalArgumentException("y is ambiguous; Group members have various y values");
				}
			}
		}
		throw new IllegalArgumentException("obj must be a Component or IConstrainer");
	}
}
