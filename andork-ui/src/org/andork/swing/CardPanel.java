package org.andork.swing;

import javax.swing.JPanel;

import org.andork.awt.layout.BetterCardLayout;

@SuppressWarnings("serial")
public class CardPanel extends JPanel {
	public CardPanel() {
		setLayout(new BetterCardLayout());
	}
		
	public void show(String key) {
		getCardLayout().show(this, key);
	}

	private BetterCardLayout getCardLayout() {
		return (BetterCardLayout) getLayout();
	}
	
	public int getCurrentCardIndex() {
		return getCardLayout().getCurrentCardIndex();
	}
	
	public Object getCurrentCardKey() {
		return getCardLayout().getCurrentCardKey();
	}


	public void first() {
		getCardLayout().first(this);
	}
	
	public void last() {
		getCardLayout().last(this);
	}
	
	public void next() {
		getCardLayout().next(this);
	}
	
	public void previous() {
		getCardLayout().previous(this);
	}

}
