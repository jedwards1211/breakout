package org.breakout;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.ButtonGroup;
import javax.swing.JRadioButton;
import javax.swing.KeyStroke;

import org.andork.awt.I18n.Localizer;
import org.andork.awt.KeyEvents;
import org.andork.swing.JOptionPaneBuilder;
import org.andork.swing.OnEDT;
import org.andork.util.ArrayUtils;
import org.breakout.model.RootModel;

@SuppressWarnings("serial")
public class FindAction extends AbstractAction {
	BreakoutMainView mainView;

	public FindAction(BreakoutMainView mainView) {
		super();
		this.mainView = mainView;

		new OnEDT() {
			@Override
			public void run() throws Throwable {
				putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_F, KeyEvents.CTRL_OR_META_DOWN_MASK));
				Localizer localizer = mainView.getI18n().forClass(FindAction.this.getClass());
				localizer.setName(FindAction.this, "name");
			}
		};
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		Localizer localizer = mainView.getI18n().forClass(FindAction.this.getClass());
		
		final SearchMode origSearchMode = mainView.getRootModel().get(RootModel.searchMode);
		
		ButtonGroup modeGroup = new ButtonGroup();
		JRadioButton[] searchModeButtons = ArrayUtils.map(SearchMode.values(), new JRadioButton[SearchMode.values().length], mode -> {
			JRadioButton button = new JRadioButton();
			SearchModeItems.setText(button, mode, mainView.getI18n());
			modeGroup.add(button);
			button.setSelected(mode == origSearchMode);
			return button;
		});

		Object query = new JOptionPaneBuilder()
			.message(new Object[] { localizer.getString("dialog.message"), searchModeButtons })
			.showInputDialog(mainView.getMainPanel(), localizer.getString("dialog.title"));
	
		if (query == null) return;

		for (int i = 0; i < SearchMode.values().length; i++) {
			if (searchModeButtons[i].isSelected()) {
				mainView.getRootModel().set(RootModel.searchMode, SearchMode.values()[i]);
				break;
			}
		}
		
		mainView.surveyDrawer.searchField().textComponent.setText(query.toString());
		mainView.getSurveyTable().getAnnotatingRowSorter().invokeWhenDoneSorting(() -> {
			mainView.flyToFiltered(mainView.getSurveyTable());
		});
	}
}
