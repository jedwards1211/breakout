package org.andork.swing;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager;
import java.awt.Rectangle;

import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.plaf.TabbedPaneUI;

public class JTabbedPaneExtrasLayeredPane extends JLayeredPane {
	public static void main(String[] args) {
		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.addTab("Test 1", new JPanel());
		tabbedPane.addTab("Test 2", new JPanel());

		JTextField textField = new JTextField();

		JTabbedPaneExtrasLayeredPane layeredPane = new JTabbedPaneExtrasLayeredPane(tabbedPane, textField);

		JFrame frame = new JFrame();
		frame.getContentPane().add(layeredPane, BorderLayout.CENTER);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(600, 400);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}

	private JTabbedPane	tabbedPane;
	private Component	extras;

	public JTabbedPaneExtrasLayeredPane(JTabbedPane tabbedPane, Component extras) {
		this.tabbedPane = tabbedPane;
		add(tabbedPane);
		setLayer(tabbedPane, JLayeredPane.DEFAULT_LAYER);

		this.extras = extras;
		add(extras);
		setLayer(extras, JLayeredPane.DEFAULT_LAYER + 1);

		setLayout(new Layout());
	}

	private class Layout implements LayoutManager {
		@Override
		public void addLayoutComponent(String name, Component comp) {

		}

		@Override
		public void removeLayoutComponent(Component comp) {

		}

		@Override
		public Dimension preferredLayoutSize(Container parent) {
			return tabbedPane.getPreferredSize();
		}

		@Override
		public Dimension minimumLayoutSize(Container parent) {
			return tabbedPane.getMinimumSize();
		}

		@Override
		public void layoutContainer(Container parent) {
			Rectangle r = SwingUtilities.calculateInnerArea(
					JTabbedPaneExtrasLayeredPane.this, new Rectangle());

			Dimension extrasPrefSize = extras.getPreferredSize();
			
			TabbedPaneUI ui = (TabbedPaneUI) tabbedPane.getUI();
			Rectangle lastTabBounds = ui.getTabBounds(tabbedPane, tabbedPane.getTabCount() - 1);

			int tabSpace = lastTabBounds.y + lastTabBounds.height;
			int extrasHeight = Math.max(tabSpace, extrasPrefSize.height);

			tabbedPane.setBounds(r.x, r.y + extrasHeight - tabSpace,
					r.width, r.height + tabSpace - extrasHeight);

			if (tabbedPane.getTabCount() > 0) {
				int x = lastTabBounds.x + lastTabBounds.width;
				extras.setBounds(x, r.y, r.x + r.width - x, extrasHeight);
			}
		}
	}
}