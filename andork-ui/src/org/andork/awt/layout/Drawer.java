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

import static org.andork.bind.ui.BindUI.bindSelected;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLayeredPane;
import javax.swing.JToggleButton;
import javax.swing.plaf.basic.BasicButtonUI;

import org.andork.model.Cell;
import org.andork.q.QObject;
import org.andork.swing.PaintablePanel;

@SuppressWarnings("serial")
public class Drawer extends PaintablePanel {
	/**
	 *
	 */
	private static final long serialVersionUID = 6679300156512691283L;
	DrawerLayoutDelegate delegate;
	DrawerHolder holder;
	JToggleButton pinButton;
	TabLayoutDelegate pinButtonDelegate;
	JToggleButton maxButton;
	Component mainResizeHandle;
	ResizeKnobHandler mainResizeHandler;

	Supplier<QObject<DrawerModel>> getModel;
	List<AutoCloseable> autoruns = new ArrayList<>();

	public Drawer() {
		setOpaque(true);
		setLayout(new BorderLayout());
		delegate = new DrawerLayoutDelegate(this, Side.TOP);
		holder = new DrawerHolder(delegate, true);
	}

	public Drawer(Component content) {
		this();
		add(content, BorderLayout.CENTER);
	}

	public void addTo(Container layeredPane) {
		if (!(layeredPane.getLayout() instanceof DelegatingLayoutManager)) {
			layeredPane.setLayout(new DelegatingLayoutManager());
		}

		// layeredPane.setLayer( this , layer );
		layeredPane.add(this, delegate);
		if (pinButtonDelegate != null) {
			// layeredPane.setLayer( pinButton( ) , layer + 1 );
			layeredPane.add(pinButton(), pinButtonDelegate);
		}
	}

	public void addTo(JLayeredPane layeredPane, int layer) {
		if (!(layeredPane.getLayout() instanceof DelegatingLayoutManager)) {
			layeredPane.setLayout(new DelegatingLayoutManager());
		}

		layeredPane.setLayer(this, layer);
		layeredPane.add(this, delegate);
		if (pinButtonDelegate != null) {
			layeredPane.setLayer(pinButton(), layer + 1);
			layeredPane.add(pinButton(), pinButtonDelegate);
		}
	}

	public DrawerLayoutDelegate delegate() {
		return delegate;
	}

	public Drawer delegate(DrawerLayoutDelegate delegate) {
		this.delegate = delegate;
		return this;
	}

	public DrawerHolder holder() {
		return holder;
	}

	public Component mainResizeHandle() {
		if (mainResizeHandle == null) {
			JButton handle = new JButton();
			handle.setUI(new BasicButtonUI());
			handle.setMargin(new Insets(0, 0, 0, 0));
			handle.setPreferredSize(new Dimension(5, 5));
			handle.setCursor(delegate.dockingSide().opposite().resizeCursor());
			add(handle, delegate.dockingSide().opposite().borderLayoutAnchor());

			mainResizeHandler = new ResizeKnobHandler(this, delegate.dockingSide().opposite());
			handle.addMouseListener(mainResizeHandler);
			handle.addMouseMotionListener(mainResizeHandler);

			mainResizeHandle = handle;
		}
		return mainResizeHandle;
	}

	public JToggleButton maxButton() {
		if (maxButton == null) {
			maxButton = new JToggleButton();
			maxButton.setMargin(new Insets(2, 2, 2, 2));
			maxButton.setIcon(new ImageIcon(Drawer.class.getResource("maximize.png")));
			maxButton.setSelectedIcon(new ImageIcon(Drawer.class.getResource("restore.png")));
			maxButton.addItemListener(new ItemListener() {
				@Override
				public void itemStateChanged(ItemEvent e) {
					delegate.setMaximized(maxButton.isSelected());
				}
			});
		}
		return maxButton;
	}

	public JToggleButton pinButton() {
		if (pinButton == null) {
			pinButton = new JToggleButton();
			pinButton.setMargin(new Insets(2, 2, 2, 2));
			pinButton.setIcon(new ImageIcon(Drawer.class.getResource("unpinned.png")));
			pinButton.setSelectedIcon(new ImageIcon(Drawer.class.getResource("pinned.png")));
			pinButton.addItemListener(new ItemListener() {
				@Override
				public void itemStateChanged(ItemEvent e) {
					if (pinButton.isSelected()) {
						holder.hold(pinButton);
					}
					else {
						holder.releaseAll();
					}
				}
			});
		}
		return pinButton;
	}

	public TabLayoutDelegate pinButtonDelegate() {
		if (pinButtonDelegate == null) {
			pinButtonDelegate =
				new TabLayoutDelegate(
					this,
					Corner
						.fromSides(
							delegate.dockingSide().opposite(),
							delegate.dockingSide().axis().opposite().lowerSide()),
					delegate.dockingSide().opposite());
		}
		return pinButtonDelegate;
	}

	public void setModel(Supplier<QObject<DrawerModel>> getModel) {
		if (this.getModel == getModel)
			return;
		this.getModel = getModel;
		List<AutoCloseable> autoruns = new ArrayList<>(this.autoruns);
		this.autoruns.clear();
		for (AutoCloseable autorun : autoruns) {
			try {
				autorun.close();
			}
			catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		if (getModel != null) {
			this.autoruns.add(bindSelected(maxButton(), Cell.from(() -> {
				QObject<DrawerModel> model = getModel.get();
				if (model == null)
					return null;
				return model.get(DrawerModel.maximized);
			}, (newValue) -> {
				QObject<DrawerModel> model = getModel.get();
				if (model == null)
					return;
				model.set(DrawerModel.maximized, newValue);
			})));
			this.autoruns.add(bindSelected(pinButton(), Cell.from(() -> {
				QObject<DrawerModel> model = getModel.get();
				if (model == null)
					return null;
				return model.get(DrawerModel.pinned);
			}, (newValue) -> {
				QObject<DrawerModel> model = getModel.get();
				if (model == null)
					return;
				model.set(DrawerModel.pinned, newValue);
			})));
		}
	}
}
