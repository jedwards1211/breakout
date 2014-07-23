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
package org.andork.ui.debug;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.Container;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.AWTEventListener;
import java.awt.event.ContainerEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JTree;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import org.andork.util.ArrayUtils;

@SuppressWarnings("serial")
public class ComponentTree extends JTree {
	private AWTEventHandler	awtEventHandler				= new AWTEventHandler();
	private boolean			awtEventHandlerRegistered	= false;

	public ComponentTree() {
		super(new DefaultTreeModel(null));
		addTreeExpansionListener(new ExpansionHandler());
	}

	public void setAWTEventHandlerRegistered(boolean registered) {
		if (registered && !awtEventHandlerRegistered) {
			awtEventHandlerRegistered = true;
			Toolkit.getDefaultToolkit().addAWTEventListener(awtEventHandler, AWTEvent.CONTAINER_EVENT_MASK);
		} else if (!registered && awtEventHandlerRegistered) {
			awtEventHandlerRegistered = false;
			Toolkit.getDefaultToolkit().removeAWTEventListener(awtEventHandler);
		}
	}

	private static DefaultMutableTreeNode getNode(TreePath path) {
		return (DefaultMutableTreeNode) path.getLastPathComponent();
	}

	private static DefaultMutableTreeNode getNode(TreeExpansionEvent event) {
		return getNode(event.getPath());
	}

	private static int childIndex(DefaultMutableTreeNode node, Object childUserObject) {
		for (int i = 0; i < node.getChildCount(); i++) {
			DefaultMutableTreeNode child = (DefaultMutableTreeNode) node.getChildAt(i);
			if (child.getUserObject() == childUserObject) {
				return i;
			}
		}
		return -1;
	}

	private static DefaultMutableTreeNode childNode(DefaultMutableTreeNode node, Object childUserObject) {
		int childIndex = childIndex(node, childUserObject);
		return childIndex < 0 ? null : (DefaultMutableTreeNode) node.getChildAt(childIndex);
	}

	private static void setChildren(DefaultMutableTreeNode node, Component[] children) {
		for (int i = 0; i < children.length; i++) {
			int existingIndex = childIndex(node, children[i]);
			if (existingIndex < 0) {
				node.insert(new DefaultMutableTreeNode(children[i]), i);
			} else if (existingIndex != i) {
				DefaultMutableTreeNode existingChild = (DefaultMutableTreeNode) node.getChildAt(existingIndex);
				node.insert(existingChild, i);
			}
		}
		while (node.getChildCount() > children.length) {
			node.remove(children.length);
		}
	}

	private static void updateChildren(DefaultMutableTreeNode node) {
		if (node.getUserObject() instanceof Container) {
			setChildren(node, ((Container) node.getUserObject()).getComponents());
		}
	}

	private static List<Component> getPath(Component c) {
		List<Component> result = new ArrayList<Component>();
		while (c != null) {
			result.add(0, c);
			if (c instanceof Window) {
				break;
			}
			c = c.getParent();
		}
		return result;
	}

	public void setRootComponent(Component rootComp) {
		Object rootUserObject = getRoot() == null ? null : getRoot().getUserObject();
		if (rootComp != rootUserObject) {
			DefaultTreeModel model = (DefaultTreeModel) getModel();
			if (rootComp == null) {
				model.setRoot(null);
			} else {
				DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(rootComp);
				updateChildren(rootNode);
				for (int i = 0; i < rootNode.getChildCount(); i++) {
					updateChildren((DefaultMutableTreeNode) rootNode.getChildAt(i));
				}
				model.setRoot(rootNode);
			}
		}
	}

	public DefaultMutableTreeNode getRoot() {
		return (DefaultMutableTreeNode) getModel().getRoot();
	}

	public DefaultMutableTreeNode getNode(Component c) {
		List<Component> path = getPath(c);
		DefaultMutableTreeNode node = getRoot();
		if (node == null || node.getUserObject() != path.get(0)) {
			return null;
		}
		for (int i = 1; i < path.size() && node != null; i++) {
			Component child = path.get(i);
			node = childNode(node, child);
		}
		return node;
	}

	public void focus(Component c) {
		if (c == null) {
			setRootComponent(null);
			return;
		}

		List<Component> path = getPath(c);
		setRootComponent(path.get(0));
		DefaultMutableTreeNode node = getRoot();
		for (int i = 1; i < path.size() && node != null; i++) {
			expandPath(new TreePath(node.getPath()));
			node = childNode(node, path.get(i));
		}

		if (node != null) {
			TreePath nodePath = new TreePath(node.getPath());
			setSelectionPath(nodePath);
			Rectangle pathBounds = getPathBounds(nodePath);
			if (pathBounds != null) {
				pathBounds.x = 0;
				scrollRectToVisible(pathBounds);
			}
		}
	}

	private class ExpansionHandler implements TreeExpansionListener {
		public void treeExpanded(TreeExpansionEvent event) {
			DefaultMutableTreeNode node = getNode(event);
			for (int i = 0; i < node.getChildCount(); i++) {
				DefaultMutableTreeNode child = (DefaultMutableTreeNode) node.getChildAt(i);
				Component childComp = (Component) child.getUserObject();
				if (childComp instanceof Container) {
					Container childCont = (Container) childComp;
					setChildren(child, childCont.getComponents());
				}
			}
		}

		public void treeCollapsed(TreeExpansionEvent event) {

		}
	}

	private class AWTEventHandler implements AWTEventListener {
		public void eventDispatched(AWTEvent event) {
			if (event instanceof ContainerEvent) {
				ContainerEvent ce = (ContainerEvent) event;
				DefaultMutableTreeNode contNode = getNode(ce.getContainer());
				if (contNode != null) {
					if (event.getID() == ContainerEvent.COMPONENT_ADDED) {
						DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) contNode.getParent();
						if (contNode == getRoot() || isExpanded(new TreePath(parentNode.getPath()))) {
							int childIndex = ArrayUtils.strictIndexOf(ce.getContainer().getComponents(), ce.getChild());
							DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(ce.getChild());
							if (isExpanded(new TreePath(contNode.getPath()))) {
								updateChildren(childNode);
							}
							contNode.insert(childNode, childIndex);
						}
					} else if (event.getID() == ContainerEvent.COMPONENT_REMOVED) {
						int childIndex = childIndex(contNode, ce.getChild());
						if (childIndex >= 0) {
							contNode.remove(childIndex);
						}
					}
				}
			}
		}
	}
}
