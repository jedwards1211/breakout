package org.andork.swing.list;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.Arrays;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.andork.awt.GridBagWizard;
import org.andork.q.QArrayList;
import org.andork.q.QList;
import org.andork.swing.QuickTestFrame;

public class QFileListEditor extends JPanel {
	private static final long serialVersionUID = -7659154410942818791L;

	public static void main(String[] args) {
		QFileListEditor view = new QFileListEditor();
		view.setModel(new QArrayList<>());
		QuickTestFrame.frame(view).setVisible(true);
	}

	boolean listening;
	QList<File, ?> model;
	JList<File> list;
	JScrollPane listScroller;
	JButton removeButton;
	JButton addButton;
	Action addAction;
	Action removeAction;
	JFileChooser fileChooser;

	public QFileListEditor() {
		setBorder(new EmptyBorder(5, 5, 5, 5));
		initComponents();
		initLayout();
		initActions();
		initListeners();
	}

	@SuppressWarnings("serial")
	void initActions() {
		addAction = new AbstractAction("Add...") {
			@Override
			public void actionPerformed(ActionEvent e) {
				int option = fileChooser.showOpenDialog(SwingUtilities.getWindowAncestor(QFileListEditor.this));
				if (option == JFileChooser.APPROVE_OPTION && model != null) {
					model.addAll(Arrays.asList(fileChooser.getSelectedFiles()));
				}
			}
		};

		removeAction = new AbstractAction("Remove Selected") {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (model != null) {
					int[] selectedIndices = list.getSelectedIndices();
					for (int i = selectedIndices.length - 1; i >= 0; i--) {
						model.remove(selectedIndices[i]);
					}
					list.clearSelection();
				}
			}
		};
	}

	void initComponents() {
		list = new JList<>();
		listScroller = new JScrollPane(list);
		addButton = new JButton("Add");
		removeButton = new JButton("Remove Selected");
		removeButton.setEnabled(false);
		fileChooser = new JFileChooser();
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		fileChooser.setMultiSelectionEnabled(true);
	}

	void initLayout() {
		GridBagWizard w = GridBagWizard.create(this);
		listScroller.setPreferredSize(new Dimension(200, 300));
		w.put(addButton, removeButton).intoRow().y(0).fillx(1.0);
		w.put(listScroller).above(addButton, removeButton).fillboth(0.0, 1.0);
	}

	void initListeners() {
		addButton.setAction(addAction);
		removeButton.setAction(removeAction);
		list.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), "removeItem");
		list.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, 0), "removeItem");
		list.getActionMap().put("removeItem", removeAction);
		list.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				removeButton.setEnabled(!list.isSelectionEmpty());
			}
		});
	}

	public void setModel(QList<File, ?> model) {
		if (this.model != model) {
			this.model = model;
			list.setModel(model != null
					? new QListListModel<>(model)
					: new DefaultListModel<>());
			removeButton.setEnabled(!list.isSelectionEmpty());
		}
	}
}
