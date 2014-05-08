package org.andork.swing;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;

import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.table.TableCellRenderer;

/**
 * 
 * @author andy.edwards
 * 
 */
@SuppressWarnings("serial")
public abstract class CellRendererWithButtons extends JPanel implements CellRendererWithContentArea {
	protected int				spacing		= 1;

	protected AbstractButton[]	buttons;
	protected Rectangle			contentArea	= new Rectangle();

	protected Component			content;

	public CellRendererWithButtons() {
		buttons = initButtons();
		for (AbstractButton button : buttons) {
			add(button);
		}
	}

	protected abstract AbstractButton[] initButtons();

	@Override
	protected void paintChildren(Graphics g) {
		doLayout();
		super.paintChildren(g);
	}

	protected Dimension getSizeForLayout(AbstractButton button) {
		return new Dimension(getHeight(), getHeight());
	}

	public void doLayout() {
		int x = getWidth();
		for (int i = buttons.length - 1; i >= 0; i--) {
			if (!buttons[i].isVisible()) {
				continue;
			}
			Dimension size = getSizeForLayout(buttons[i]);
			buttons[i].setBounds(x - size.width, 0, size.width, size.height);
			x -= size.width - spacing;
		}
		contentArea.setBounds(spacing, spacing, x - spacing * 2, getHeight() - spacing * 2);
		if (content != null) {
			content.setBounds(contentArea);
		}
	}

	public void setContent(Component content) {
		this.content = content;
		add(content);

		if (content instanceof JComponent) {
			JComponent jc = (JComponent) content;
			setBorder(jc.getBorder());
			jc.setBorder(null);
		}
		setBackground(content.getBackground());
	}

	public Component getContent() {
		return content;
	}

	public Rectangle getContentArea() {
		return contentArea;
	}
}