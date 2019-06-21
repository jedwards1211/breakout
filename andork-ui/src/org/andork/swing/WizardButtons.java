package org.andork.swing;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;

import org.andork.awt.I18n;
import org.andork.awt.I18n.Localizer;

@SuppressWarnings("serial")
public class WizardButtons extends JComponent {
	public final JButton backButton;
	public final JButton nextButton;
	public final JButton okButton;
	public final JButton cancelButton;

	public WizardButtons(I18n i18n) {
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		
		Localizer localizer = i18n.forClass(WizardButtons.class);
		
		backButton = new JButton();
		localizer.setText(backButton, "backButton.text");
		nextButton = new JButton();
		localizer.setText(nextButton, "nextButton.text");
		okButton = new JButton();
		localizer.setText(okButton, "okButton.text");
		cancelButton = new JButton();
		localizer.setText(cancelButton, "cancelButton.text");
		
		add(backButton);
		add(Box.createHorizontalStrut(5));
		add(Box.createHorizontalGlue());
		add(nextButton);
		add(Box.createHorizontalStrut(5));
		add(okButton);
		add(Box.createHorizontalStrut(5));
		add(cancelButton);
	}

}
