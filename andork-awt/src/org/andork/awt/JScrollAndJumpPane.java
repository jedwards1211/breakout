package org.andork.awt;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager;
import java.awt.Rectangle;

import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.border.MatteBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * A {@link JScrollPane} with a built-in {@link JumpBar} parallel to the
 * vertical scroll bar.
 * 
 * @author andy.edwards
 */
@SuppressWarnings("serial")
public class JScrollAndJumpPane extends JPanel implements LayoutManager {
	JScrollPane	scrollPane;
	JumpBar		jumpBar;

	public JScrollAndJumpPane() {
		this(null);
	}

	public JScrollAndJumpPane(Component view) {
		if (view != null) {
			scrollPane = new JScrollPane(view);
		} else {
			scrollPane = new JScrollPane();
		}
		jumpBar = createJumpBar(scrollPane.getVerticalScrollBar());
		scrollPane.getViewport().addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				revalidate();
			}
		});
		add(scrollPane);
		add(jumpBar);
		setBorder(scrollPane.getBorder());
		scrollPane.setBorder(new MatteBorder(0, 0, 0, 1, new Color(224, 224, 224)));
		setLayout(this);
	}

	public JumpBar getJumpBar() {
		return jumpBar;
	}

	public JScrollPane getScrollPane() {
		return scrollPane;
	}

	protected JumpBar createJumpBar(JScrollBar scrollBar) {
		return new JumpBar(scrollBar);
	}

	@Override
	public void addLayoutComponent(String name, Component comp) {

	}

	@Override
	public void removeLayoutComponent(Component comp) {

	}

	@Override
	public Dimension preferredLayoutSize(Container parent) {
		Dimension size = scrollPane.getPreferredSize();
		size.width += jumpBar.getPreferredSize().width;
		return size;
	}

	@Override
	public Dimension minimumLayoutSize(Container parent) {
		Dimension size = scrollPane.getMinimumSize();
		size.width += jumpBar.getMinimumSize().width;
		return size;
	}

	@Override
	public void layoutContainer(Container parent) {
		Rectangle b = SwingUtilities.calculateInnerArea(this, null);
		scrollPane.setBounds(b.x, b.y, b.width, b.height);
		boolean scroll = scrollPane.getVerticalScrollBar().isVisible();
		int highlightBarWidth = scroll ? jumpBar.getPreferredSize().width : 0;
		scrollPane.setBounds(b.x, b.y, b.width - highlightBarWidth - 1, b.height);
		if (scroll) {
			jumpBar.setBounds(b.width - highlightBarWidth, b.y + scrollPane.getVerticalScrollBar().getY(),
					highlightBarWidth, scrollPane.getVerticalScrollBar().getHeight());
		}
		jumpBar.setVisible(scroll);
	}
}
