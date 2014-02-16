package org.andork.swing;

import java.awt.Dimension;
import java.awt.Insets;

import javax.swing.AbstractButton;
import javax.swing.ImageIcon;

public class ModernStyleClearButton {
	public static void createClearButton(AbstractButton clearButton) {
		clearButton.setMargin(new Insets(0, 0, 0, 0));
		clearButton.setIcon(new ImageIcon(ModernStyleClearButton.class.getResource("xicon-normal.png")));
		clearButton.setRolloverIcon(new ImageIcon(ModernStyleClearButton.class.getResource("xicon-rollover.png")));
		clearButton.setPressedIcon(new ImageIcon(ModernStyleClearButton.class.getResource("xicon-pressed.png")));
		clearButton.setPreferredSize(new Dimension(clearButton.getIcon().getIconWidth(), clearButton.getIcon().getIconHeight()));
		clearButton.setFocusPainted(false);
		clearButton.setBorderPainted(false);
		clearButton.setContentAreaFilled(false);
		clearButton.setOpaque(false);
	}
}
