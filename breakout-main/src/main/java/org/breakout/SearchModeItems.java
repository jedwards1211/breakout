package org.breakout;

import javax.swing.AbstractButton;
import javax.swing.JMenuItem;

import org.andork.awt.I18n;
import org.andork.awt.I18n.Localizer;

public class SearchModeItems extends JMenuItem {
	public static void setText(AbstractButton button, SearchMode mode, I18n i18n) {
		Localizer localizer = i18n.forClass(SearchModeItems.class);
		localizer.setText(button, mode.name());
	}
}
