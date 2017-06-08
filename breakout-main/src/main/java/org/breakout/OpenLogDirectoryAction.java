package org.breakout;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.AbstractAction;

import org.andork.awt.I18n;
import org.andork.awt.I18n.Localizer;
import org.andork.swing.OnEDT;

@SuppressWarnings("serial")
public class OpenLogDirectoryAction extends AbstractAction {
	private static final Logger logger = Logger.getLogger(OpenLogDirectoryAction.class.getName());
	
	public OpenLogDirectoryAction(I18n i18n) {
		new OnEDT() {
			@Override
			public void run() throws Throwable {
				Localizer localizer = i18n.forClass(OpenLogDirectoryAction.this.getClass());
				localizer.setName(OpenLogDirectoryAction.this, "name");
			}
		};
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		try {
			Desktop.getDesktop().open(BreakoutMain.getLogDirectory().toFile());
		} catch (Exception ex) {
			logger.log(Level.SEVERE, "failed to open log directory", ex);
		}
	}
}
