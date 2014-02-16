package org.andork.ui.debug;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

@SuppressWarnings("serial")
public class ClassChooserDialog extends JDialog {
	private ClassChooserPane	chooserPane;
	private JOptionPane			optionPane;

	private static class GlobalInstanceHolder {
		private static ClassChooserDialog	INSTANCE	= new ClassChooserDialog();
	}

	public static String showDialog(String title, Component parent) {
		return GlobalInstanceHolder.INSTANCE.show(title, parent);
	}

	public static void main(String[] args) {
		String s = ClassChooserDialog.showDialog(null, null);
		System.out.println(s);
	}

	public ClassChooserDialog() {
		init();
	}

	public void init() {
		chooserPane = new ClassChooserPane();
		optionPane = new JOptionPane(chooserPane,
				JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_CANCEL_OPTION, null);

		PropertyChangeListener optionPaneListener = new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				setVisible(false);
			}
		};
		optionPane.addPropertyChangeListener(JOptionPane.VALUE_PROPERTY, optionPaneListener);

		chooserPane.getMatchList().addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2 && chooserPane.getMatchList().getSelectedIndex() >= 0) {
					optionPane.setValue(JOptionPane.OK_OPTION);
				}
			}
		});

		getContentPane().add(optionPane, BorderLayout.CENTER);
		pack();
	}

	public String show(String title, Component parent) {
		setTitle(title);
		chooserPane.setTypePrefix("");
		optionPane.setValue(null);
		setLocationRelativeTo(parent);

		setModalityType(ModalityType.APPLICATION_MODAL);
		setVisible(true);

		return optionPane.getValue() != null &&
				((Integer) optionPane.getValue()) == JOptionPane.OK_OPTION ?
				chooserPane.getSelectedClassName() : null;
	}
}
