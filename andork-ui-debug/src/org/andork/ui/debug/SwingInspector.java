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
package org.andork.ui.debug;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Paint;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.AWTEventListener;
import java.awt.event.ContainerEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.border.Border;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

/**
 * A debug tool that figures out what component is under the mouse when you hold
 * Alt and displays its component hierarchy (path to root ancestor) in a list.
 * The selected component in the list is highlighted by setting its border.<br>
 * <br>
 *
 * SwingInspector works by listening to all AWT Mouse events with an
 * {@link AWTEventListener}.
 *
 * @author james.a.edwards
 */
public class SwingInspector extends JFrame {
	private class EventHandler implements AWTEventListener {
		@Override
		public void eventDispatched(AWTEvent event) {
			if (event instanceof MouseEvent) {
				MouseEvent me = (MouseEvent) event;
				if ((me.getModifiersEx() & InputEvent.ALT_DOWN_MASK) != 0) {
					Component deepest = SwingUtilities.getDeepestComponentAt(
							me.getComponent(), me.getX(), me.getY());
					setDisplayedComponent(deepest);
				}
			} else if (event instanceof ContainerEvent) {
				ContainerEvent ce = (ContainerEvent) event;
				if (ce.getID() == ContainerEvent.COMPONENT_ADDED) {
					stackTraces.put(ce.getChild(), new RuntimeException().getStackTrace());
				} else if (ce.getID() == ContainerEvent.COMPONENT_REMOVED) {
					stackTraces.remove(ce.getChild());
				}
			} else if (event instanceof KeyEvent) {
				KeyEvent ke = (KeyEvent) event;
				if (ke.getKeyCode() == KeyEvent.VK_D && (ke.getModifiersEx()
						& (InputEvent.CTRL_DOWN_MASK | InputEvent.ALT_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK)) != 0) {
					Window window = SwingUtilities.getWindowAncestor(ke.getComponent());
					if (window instanceof JDialog) {
						JDialog dialog = (JDialog) window;
						dialog.setModal(false);
						dialog.setVisible(false);
						dialog.setVisible(true);
					}
				}
			}
		}
	}

	/**
	 * A border with a red outline and semitransparent fill to show which
	 * component is selected in the hierarchy list. Keeps a reference to the
	 * component's original border and draws it beneath the highlight,
	 * preserving the insets.
	 *
	 * @author james.a.edwards
	 */
	private class HighlightBorder implements Border {
		Border inner = null;

		public HighlightBorder(Border inner) {
			this.inner = inner;
		}

		@Override
		public Insets getBorderInsets(Component c) {
			return inner == null ? new Insets(0, 0, 0, 0) : inner
					.getBorderInsets(c);
		}

		@Override
		public boolean isBorderOpaque() {
			return inner == null ? false : inner.isBorderOpaque();
		}

		@Override
		public void paintBorder(Component c, Graphics g, int x, int y,
				int width, int height) {
			Graphics2D g2 = (Graphics2D) g;
			Paint prevPaint = g2.getPaint();
			g2.setColor(new Color(255, 0, 0, 128));
			g2.fillRect(x, y, width - 1, height - 1);
			g2.setColor(Color.RED);
			g2.drawRect(x, y, width - 1, height - 1);
			g2.setPaint(prevPaint);
			if (inner != null) {
				inner.paintBorder(c, g2, x, y, width, height);
			}
		}
	}

	/**
	 *
	 */
	private static final long serialVersionUID = 2594539320189073849L;
	private ComponentTree componentTree;

	private JScrollPane componentTreeScroller;
	private JTextArea stackTraceArea;
	private JScrollPane stackTraceAreaScroller;
	private Component displayedComponent;

	private Component highlightedComponent;
	private JTable attributesTable;
	private DefaultTableModel attributesTableModel;

	private JScrollPane attributesTableScroller;
	private JSplitPane topSplitPane;

	private JSplitPane mainSplitPane;
	private EventHandler eventHandler;

	private boolean listening = false;

	private Map<Component, StackTraceElement[]> stackTraces = new HashMap<Component, StackTraceElement[]>();

	public SwingInspector() {
		super("Swing Inspector (Ctrl + Alt + Shift + S)");
		init();
	}

	private int getAttrEnd(String attrStr, Matcher m, int start) {
		int level = 0;
		int i = start;
		do {
			int nextOpen = attrStr.indexOf('[', i);
			int nextClose = attrStr.indexOf(']', i);
			int nextAttr = -1;
			if (m.find(i)) {
				nextAttr = m.start();
			}

			if (nextOpen < 0) {
				nextOpen = attrStr.length();
			}
			if (nextClose < 0) {
				nextClose = attrStr.length();
			}
			if (nextAttr < 0 || level > 0) {
				nextAttr = attrStr.length();
			}

			if (nextOpen < nextClose && nextOpen < nextAttr) {
				level++;
				i = nextOpen + 1;
			} else if (nextClose < nextOpen && nextClose < nextAttr) {
				level--;
				i = nextClose + 1;
			} else {
				level = 0;
				i = nextAttr;
			}

		} while (level > 0);

		return Math.min(attrStr.length() - 1, i);
	}

	private void init() {
		componentTree = new ComponentTree();
		componentTree.setAWTEventHandlerRegistered(true);
		componentTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		componentTreeScroller = new JScrollPane(componentTree);

		componentTree.addTreeSelectionListener(new TreeSelectionListener() {
			@Override
			public void valueChanged(TreeSelectionEvent e) {
				TreePath newPath = e.getNewLeadSelectionPath();
				if (newPath == null) {
					setHighlightedComponent(null);
				} else {
					DefaultMutableTreeNode lastNode = (DefaultMutableTreeNode) newPath.getLastPathComponent();
					setHighlightedComponent((Component) lastNode.getUserObject());
				}
			}
		});

		eventHandler = new EventHandler();
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		toolkit.addAWTEventListener(eventHandler, AWTEvent.KEY_EVENT_MASK | AWTEvent.MOUSE_EVENT_MASK
				| AWTEvent.MOUSE_MOTION_EVENT_MASK | AWTEvent.CONTAINER_EVENT_MASK);

		attributesTableModel = new DefaultTableModel(new Object[] { "Attribute", "Value" }, 0) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		attributesTable = new JTable(attributesTableModel);
		attributesTableScroller = new JScrollPane(attributesTable);
		attributesTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		attributesTable.getColumnModel().getColumn(0).setPreferredWidth(200);
		attributesTable.getColumnModel().getColumn(1).setPreferredWidth(2000);

		JPanel hierarchyPanel = new JPanel(new BorderLayout());

		JLabel hierarchyLabel = new JLabel("Holt Alt and drag mouse over components to see component hierarchy!");
		hierarchyLabel.setFont(hierarchyLabel.getFont().deriveFont(Font.BOLD));

		hierarchyPanel.add(hierarchyLabel, BorderLayout.NORTH);
		// hierarchyPanel.add(hierarchyListScroller, BorderLayout.CENTER);
		hierarchyPanel.add(componentTreeScroller, BorderLayout.CENTER);

		topSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		topSplitPane.setLeftComponent(hierarchyPanel);
		topSplitPane.setRightComponent(attributesTableScroller);
		topSplitPane.setResizeWeight(0.5);

		stackTraceArea = new JTextArea();
		stackTraceArea.setEditable(false);
		stackTraceArea.setForeground(Color.RED);
		stackTraceArea.setFont(new Font("Monospaced", Font.PLAIN, 11));

		stackTraceAreaScroller = new JScrollPane(stackTraceArea);

		JPanel stackTracePanel = new JPanel(new BorderLayout());
		JLabel stackTraceLabel = new JLabel("Stack trace where component was added:");
		stackTraceLabel.setFont(stackTraceLabel.getFont().deriveFont(Font.BOLD));
		stackTracePanel.add(stackTraceLabel, BorderLayout.NORTH);
		stackTracePanel.add(stackTraceAreaScroller, BorderLayout.CENTER);

		mainSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		mainSplitPane.setResizeWeight(0.5);

		mainSplitPane.setTopComponent(topSplitPane);
		mainSplitPane.setBottomComponent(stackTracePanel);

		getContentPane().add(mainSplitPane, BorderLayout.CENTER);

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

		setSize(screenSize.width * 2 / 3, screenSize.height * 2 / 3);
		mainSplitPane.setDividerLocation(getHeight() / 2);
		topSplitPane.setDividerLocation(getWidth() / 2);

		setLocationRelativeTo(null);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
	}

	/**
	 * Sets the component whose path to root is displayed in the list, and
	 * selects that component (which will be last) in the list.
	 *
	 * @param comp
	 *            the new component to show.
	 */
	public void setDisplayedComponent(final Component comp) {
		if (displayedComponent != comp) {
			displayedComponent = comp;
			componentTree.focus(comp);
		}
	}

	/**
	 * Sets the highlighted component. The highlight border will be removed from
	 * the old highlighted component, and a highlight border will be added to
	 * the new one.
	 *
	 * @param selectedValue
	 */
	private void setHighlightedComponent(Component selectedValue) {
		if (selectedValue != highlightedComponent) {
			componentTree.focus(selectedValue);

			if (highlightedComponent != null
					&& highlightedComponent instanceof JComponent) {
				JComponent jsel = (JComponent) highlightedComponent;
				jsel.putClientProperty("highlightedBySwingInspector", null);
				if (jsel.getBorder() instanceof HighlightBorder) {
					jsel.setBorder(((HighlightBorder) jsel.getBorder()).inner);
				}
			}

			highlightedComponent = selectedValue;

			if (highlightedComponent != null
					&& highlightedComponent instanceof JComponent) {
				JComponent jsel = (JComponent) highlightedComponent;
				try {
					jsel.setBorder(new HighlightBorder(jsel.getBorder()));
				} catch (Exception ex) {

				}
			}

			stackTraceArea.setText(null);

			StackTraceElement[] trace = stackTraces.get(highlightedComponent);
			if (trace != null) {
				StringBuffer sb = new StringBuffer();
				for (StackTraceElement elem : trace) {
					if (sb.length() > 0) {
						sb.append("\n\t");
					}
					sb.append(elem);
				}

				stackTraceArea.setText(sb.toString());
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						stackTraceAreaScroller.getVerticalScrollBar().setValue(0);
					}
				});
			}

			attributesTableModel.setRowCount(0);

			if (highlightedComponent != null) {
				if (highlightedComponent instanceof JComponent) {
					JComponent jsel = (JComponent) highlightedComponent;
					jsel.putClientProperty("highlightedBySwingInspector", true);
				}

				String attrStr = highlightedComponent.toString();
				Pattern attrPat = Pattern.compile(",\\w[a-zA-Z0-9_]*=");
				Matcher m = attrPat.matcher(attrStr);

				HashMap<String, String> attrMap = new HashMap<String, String>();

				int start = 0;
				while (m.find(start)) {
					int valueEnd = getAttrEnd(attrStr, attrPat.matcher(attrStr), m.end());
					attrMap.put(attrStr.substring(m.start() + 1, m.end() - 1), attrStr.substring(m.end(), valueEnd));
					start = valueEnd;
				}

				Pattern basicsPat = Pattern.compile("^.*\\[(.*),(\\d+),(\\d+),(\\d+)x(\\d+)");
				m = basicsPat.matcher(attrStr);
				if (m.find()) {
					attrMap.put("name", m.group(1));
					attrMap.put("x", m.group(2));
					attrMap.put("y", m.group(3));
					attrMap.put("width", m.group(4));
					attrMap.put("height", m.group(5));
				}

				String[] attrs = attrMap.keySet().toArray(new String[attrMap.size()]);
				Arrays.sort(attrs);

				for (String attr : attrs) {
					attributesTableModel.addRow(new Object[] { attr, attrMap.get(attr) });
				}
			}
		}
	}
}
