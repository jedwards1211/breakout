package org.andork.ui.test.fixture;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.text.JTextComponent;

import org.andork.swing.DoSwingR2;

public interface ComponentFixture {
	public void click(Component comp);

	public static class Common {
		public static String readText(final Component comp) {
			return new DoSwingR2<String>() {
				@Override
				protected String doRun() {
					if (comp instanceof JLabel) {
						return ((JLabel) comp).getText();
					} else if (comp instanceof JTextComponent) {
						return ((JTextComponent) comp).getText();
					}
					throw new IllegalArgumentException("Can't read text of: " + comp);
				}
			}.result();
		}
	}
}
