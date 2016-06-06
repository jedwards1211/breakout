package org.andork.swing.list;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.border.EmptyBorder;

import org.andork.awt.GridBagWizard;
import org.andork.func.Bimapper;
import org.andork.func.Bimappers;
import org.andork.q.QArrayList;
import org.andork.q.QList;
import org.andork.swing.QuickTestFrame;

public class QListEditor<T> extends JPanel {
	private static final long serialVersionUID = -7659154410942818791L;

	public static void main(String[] args) {
		QListEditor<String> view = new QListEditor<String>(Bimappers.<String> identity());
		view.setModel(new QArrayList<>());
		QuickTestFrame.frame(view).setVisible(true);
	}

	boolean listening;
	QList<T, ?> model;
	Bimapper<T, String> bimapper;
	JList<T> list;
	JScrollPane listScroller;
	JButton removeButton;
	JButton addButton;
	Action addAction;
	Action removeAction;

	JTextField textField;

	public QListEditor(Bimapper<T, String> bimapper) {
		this.bimapper = bimapper;
		setBorder(new EmptyBorder(5, 5, 5, 5));
		initComponents();
		initLayout();
		initActions();
		initListeners();
	}

	@SuppressWarnings("serial")
	void initActions() {
		addAction = new AbstractAction("Add") {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (model != null) {
					model.add(bimapper.unmap(textField.getText()));
				}
				textField.setText("");
			}

		};

		removeAction = new AbstractAction("Remove Selected") {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (model != null) {
					int[] selectedIndices = list.getSelectedIndices();
					for (int i = selectedIndices.length - 1; i >= 0; i--) {
						model.remove(i);
					}
					list.clearSelection();
				}
			}
		};
	}

	@SuppressWarnings("serial")
	void initComponents() {
		list = new JList<>();
		list.setCellRenderer(new DefaultListCellRenderer() {
			@Override
			public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
					boolean cellHasFocus) {
				@SuppressWarnings("unchecked")
				String strValue = bimapper.map((T) value);
				return super.getListCellRendererComponent(list, strValue, index, isSelected, cellHasFocus);
			}
		});
		listScroller = new JScrollPane(list);
		addButton = new JButton("Add");
		removeButton = new JButton("Remove Selected");
		textField = new JTextField();
	}

	void initLayout() {
		GridBagWizard w = GridBagWizard.create(this);
		w.put(textField).xy(0, 0).fillx().weightx(1.0);
		w.put(addButton).rightOf(textField);
		w.put(listScroller).below(textField, addButton).fillboth().weighty(1.0);
		listScroller.setPreferredSize(new Dimension(200, 300));
		w.put(removeButton).below(listScroller).fillx();
	}

	void initListeners() {
		textField.setAction(addAction);
		addButton.setAction(addAction);
		removeButton.setAction(removeAction);
		list.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), "removeItem");
		list.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, 0), "removeItem");
		list.getActionMap().put("removeItem", removeAction);
	}

	public void setModel(QList<T, ?> model) {
		if (this.model != model) {
			this.model = model;
			list.setModel(model != null
					? new QListListModel<>(model)
					: new DefaultListModel<>());
		}
	}
}
