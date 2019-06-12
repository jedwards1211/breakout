package org.breakout;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ButtonGroup;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;

import org.andork.awt.I18n;

@SuppressWarnings("serial")
public class SearchOptionsMenu extends JPopupMenu {
	private SearchMode searchMode;
	private final Map<SearchMode, JRadioButtonMenuItem> items = new HashMap<>();
	
	private final EventListenerList listeners = new EventListenerList();
	
	public SearchOptionsMenu(I18n i18n) {
		ButtonGroup group = new ButtonGroup();
		for (SearchMode mode : SearchMode.values()) {
			JRadioButtonMenuItem item = new JRadioButtonMenuItem();
			items.put(mode, item);
			group.add(item);
			SearchModeItems.setText(item, mode, i18n);
			add(item);
			final SearchMode finalMode = mode;
			item.addItemListener(new ItemListener() {
				@Override
				public void itemStateChanged(ItemEvent e) {
					if (e.getStateChange() == ItemEvent.SELECTED) {
						setSearchMode(finalMode);
					}
				}
			});
		}
		setSearchMode(SearchMode.AUTO);
	}
	
	public void addChangeListener(ChangeListener l) {
		this.listeners.add(ChangeListener.class, l);
	}
	
	public void removeChangeListener(ChangeListener l) {
		this.listeners.remove(ChangeListener.class, l);
	}
	
	public void setSearchMode(SearchMode mode) {
		if (mode == this.searchMode) return; 
		SearchMode prevMode = mode;
		this.searchMode = mode;
		if (mode == null) {
			this.items.get(prevMode).setSelected(false);
		} else {
			this.items.get(mode).setSelected(true);
		}
		ChangeEvent e = new ChangeEvent(this);
		for (ChangeListener l : listeners.getListeners(ChangeListener.class)) {
			l.stateChanged(e);
		}
	}
	
	public SearchMode getSearchMode() {
		return searchMode;
	}
}
