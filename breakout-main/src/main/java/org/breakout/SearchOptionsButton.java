package org.breakout;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.SwingConstants;

import org.andork.awt.I18n;

@SuppressWarnings("serial")
public class SearchOptionsButton extends JButton {
	private final SearchOptionsMenu menu;
	
	public SearchOptionsMenu menu() {
		return menu;
	}
	
	public SearchOptionsButton(I18n i18n) {
		super(new TriangleIcon(SwingConstants.SOUTH));
		setBackground(null);
		setFocusPainted(false);
		setBorderPainted(false);
		menu = new SearchOptionsMenu(i18n);
		
		class PopupListener extends MouseAdapter {
		    public void mousePressed(MouseEvent e) {
		        maybeShowPopup(e);
		    }

		    public void mouseReleased(MouseEvent e) {
		        maybeShowPopup(e);
		    }

		    private void maybeShowPopup(MouseEvent e) {
				menu.show(e.getComponent(),
				   e.getX(), e.getY());
		    }
		}
		
		addMouseListener(new PopupListener());
	}

	public SearchMode getSearchMode() {
		return menu.getSearchMode();
	}

	public void setSearchMode(SearchMode mode) {
		menu.setSearchMode(mode);
	}
}
