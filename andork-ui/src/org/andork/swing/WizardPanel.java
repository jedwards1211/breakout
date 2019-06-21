package org.andork.swing;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Window;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.andork.awt.GridBagWizard;
import org.andork.awt.I18n;
import org.andork.ref.Ref;

@SuppressWarnings("serial")
public class WizardPanel extends JComponent {
	public final CardPanel cardPanel;
	public final WizardButtons buttons;

	private boolean useNextButton = true;

	public WizardPanel(I18n i18n) {
		cardPanel = new CardPanel();
		buttons = new WizardButtons(i18n);
		GridBagWizard w = GridBagWizard.create(this);
		w.put(cardPanel).xy(0, 0).fillboth(1, 1).insets(10, 10, 0, 10);
		w.put(buttons).belowLast().fillx(1).insets(10, 10, 10, 10);
		
		buttons.backButton.addActionListener(e -> {
			back();
		});
	}
	
	public boolean isUseNextButton() {
		return useNextButton;
	}

	public void setUseNextButton(boolean useNextButton) {
		this.useNextButton = useNextButton;
	}

	protected void update() {
		buttons.backButton.setVisible(cardPanel.getCurrentCardIndex() > 0);
		buttons.nextButton.setVisible(useNextButton && cardPanel.getCurrentCardIndex() < cardPanel.getComponentCount() - 1);
		buttons.okButton.setVisible(cardPanel.getCurrentCardIndex() == cardPanel.getComponentCount() - 1);
	}
	
	public void next() {
		cardPanel.next();
		update();
	}
	
	public void back() {
		cardPanel.previous();
		update();
	}
	
	public void addCard(Component card) {
		cardPanel.add(card);
		update();
	}
	
	/**
	 * Shows this WizardPanel in a dialog.
	 * @param parentComponent
	 * @param title
	 * @return JOptionPane.OK_OPTION if the user clicked okay, otherwise JOptionPane.CANCEL_OPTION.
	 */
	public int showDialog(Component parentComponent, String title) {
        final JDialog dialog;

        Window window = SwingUtilities.getWindowAncestor(parentComponent);
        if (window instanceof Frame) {
            dialog = new JDialog((Frame)window, title, true);
        } else {
            dialog = new JDialog((Dialog)window, title, true);
        }

        dialog.setComponentOrientation(this.getComponentOrientation());
        dialog.setResizable(false);
        dialog.getContentPane().setLayout(new BorderLayout());
        dialog.getContentPane().add(this, BorderLayout.CENTER);
        dialog.pack();
        dialog.setLocationRelativeTo(parentComponent);
        
        Ref<Integer> option = new Ref<>(JOptionPane.CANCEL_OPTION);
        
        ActionListener cancelListener = e -> {
        	dialog.setVisible(false);
        };
        ActionListener okListener = e -> {
        	option.value = JOptionPane.OK_OPTION;
        	dialog.setVisible(false);
        };
        
        dialog.addWindowListener(new WindowAdapter() {
            @Override
			public void windowOpened(WindowEvent e) {
            	buttons.okButton.addActionListener(okListener);
            	buttons.cancelButton.addActionListener(cancelListener);
			}

            @Override
			public void windowClosing(WindowEvent we) {
            	buttons.okButton.removeActionListener(okListener);
            	buttons.cancelButton.removeActionListener(cancelListener);
            }
        });
        
        dialog.setVisible(true);
        
        return option.value;
	}
}
