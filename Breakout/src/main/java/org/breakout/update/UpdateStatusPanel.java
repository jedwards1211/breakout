/*******************************************************************************
 * Breakout Cave Survey Visualizer
 *
 * Copyright (C) 2014 James Edwards
 *
 * jedwards8 at fastmail dot fm
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *******************************************************************************/
package org.breakout.update;

import static org.breakout.update.UpdateStatus.CHECKING;
import static org.breakout.update.UpdateStatus.STARTING_DOWNLOAD;
import static org.breakout.update.UpdateStatus.UNCHECKED;
import static org.breakout.update.UpdateStatus.UPDATE_AVAILABLE;
import static org.breakout.update.UpdateStatus.UPDATE_DOWNLOADED;
import static org.breakout.update.UpdateStatus.UP_TO_DATE;

import java.awt.Dimension;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.andork.awt.GridBagWizard;
import org.andork.awt.GridBagWizard.DefaultAutoInsets;
import org.andork.awt.I18n;
import org.andork.awt.I18n.Localizer;
import org.andork.awt.IconScaler;
import org.andork.util.Java7.Objects;
import org.breakout.update.UpdateStatus.CheckFailed;
import org.breakout.update.UpdateStatus.ChecksumFailed;
import org.breakout.update.UpdateStatus.DownloadFailed;
import org.breakout.update.UpdateStatus.Downloading;
import org.breakout.update.UpdateStatus.UpdateFailed;
import org.jdesktop.swingx.JXHyperlink;

@SuppressWarnings("serial")
public class UpdateStatusPanel extends JPanel {
	private class DetailsAction extends AbstractAction {
		/**
		 *
		 */
		private static final long serialVersionUID = 746988852453326246L;

		public DetailsAction() {
			super("Details...");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (status instanceof DownloadFailed) {
				JOptionPane.showMessageDialog(SwingUtilities.getWindowAncestor(UpdateStatusPanel.this),
						((DownloadFailed) status).message, localizer.getString("downloadFailedDialog.title"),
						JOptionPane.ERROR_MESSAGE);
			} else if (status instanceof UpdateFailed) {
				JOptionPane.showMessageDialog(SwingUtilities.getWindowAncestor(UpdateStatusPanel.this),
						((UpdateFailed) status).message, localizer.getString("updateFailedDialog.title"),
						JOptionPane.ERROR_MESSAGE);
			} else if (status instanceof CheckFailed) {
				JOptionPane.showMessageDialog(SwingUtilities.getWindowAncestor(UpdateStatusPanel.this),
						((CheckFailed) status).message, localizer.getString("checkFailedDialog.title"),
						JOptionPane.ERROR_MESSAGE);
			} else if (status instanceof ChecksumFailed) {
				JOptionPane.showMessageDialog(SwingUtilities.getWindowAncestor(UpdateStatusPanel.this),
						((ChecksumFailed) status).message, localizer.getString("checksumFailedDialog.title"),
						JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	/**
	 *
	 */
	private static final long serialVersionUID = -6487674183840798349L;
	private UpdateStatus status;

	private String latestVersion;
	private JLabel messageLabel;
	private JProgressBar downloadProgressBar;
	private JXHyperlink downloadHyperlink;
	private JXHyperlink checkForUpdateHyperlink;
	private JXHyperlink detailsHyperlink;
	private JXHyperlink cancelDownloadHyperlink;

	private JXHyperlink installHyperlink;

	private Localizer localizer;
	private Icon infoIcon;

	private Icon errorIcon;

	public UpdateStatusPanel(I18n i18n) {
		if (i18n == null) {
			i18n = new I18n();
		}
		localizer = i18n.forClass(UpdateStatusPanel.class);
		init();
		modelToView();
	}

	public String getLatestVersion() {
		return latestVersion;
	}

	public Localizer getLocalizer() {
		return localizer;
	}

	public UpdateStatus getStatus() {
		return status;
	}

	private void init() {
		messageLabel = new JLabel();
		downloadProgressBar = new JProgressBar();
		downloadProgressBar.setPreferredSize(new Dimension(75, 15));
		downloadProgressBar.setMinimumSize(downloadProgressBar.getPreferredSize());
		downloadHyperlink = new JXHyperlink();
		checkForUpdateHyperlink = new JXHyperlink();
		localizer.setText(checkForUpdateHyperlink, "checkForUpdateHyperlink.text");
		detailsHyperlink = new JXHyperlink();
		detailsHyperlink.setAction(new DetailsAction());
		localizer.setText(detailsHyperlink, "detailsHyperlink.text");
		cancelDownloadHyperlink = new JXHyperlink();
		localizer.setText(cancelDownloadHyperlink, "cancelDownloadHyperlink.text");
		installHyperlink = new JXHyperlink();
		localizer.setText(installHyperlink, "installHyperlink.text");

		GridBagWizard g = GridBagWizard.create(this);
		g.defaults().autoinsets(new DefaultAutoInsets(5, 2));
		g.put(messageLabel, downloadHyperlink, detailsHyperlink, checkForUpdateHyperlink,
				cancelDownloadHyperlink, installHyperlink, downloadProgressBar).intoRow();
		g.put(downloadProgressBar).fillx(1.0);

		infoIcon = IconScaler.rescale(UIManager.getIcon("OptionPane.informationIcon"), 1000, 20);
		errorIcon = IconScaler.rescale(UIManager.getIcon("OptionPane.errorIcon"), 1000, 20);
	}

	private void modelToView() {
		downloadProgressBar.setVisible(status == CHECKING || status == STARTING_DOWNLOAD
				|| status instanceof Downloading);
		downloadProgressBar.setIndeterminate(status == CHECKING || status == STARTING_DOWNLOAD);
		detailsHyperlink.setVisible(status instanceof UpdateFailed || status instanceof CheckFailed
				|| status instanceof DownloadFailed || status instanceof ChecksumFailed);
		downloadHyperlink.setVisible(status == UPDATE_AVAILABLE || status instanceof DownloadFailed
				|| status instanceof ChecksumFailed);
		checkForUpdateHyperlink.setVisible(status == null || status == UNCHECKED || status instanceof CheckFailed
				|| status == UP_TO_DATE || status instanceof UpdateFailed);
		cancelDownloadHyperlink.setVisible(status instanceof Downloading || status == STARTING_DOWNLOAD);
		installHyperlink.setVisible(status == UPDATE_DOWNLOADED);

		if (status == null || status == UNCHECKED) {
			messageLabel.setText(null);
			messageLabel.setIcon(null);
		} else if (status == STARTING_DOWNLOAD) {
			localizer.setText(messageLabel, "messageLabel.text.startingDownload");
			messageLabel.setIcon(null);
		} else if (status == CHECKING) {
			localizer.setText(messageLabel, "messageLabel.text.checking");
			messageLabel.setIcon(null);
		} else if (status == UPDATE_AVAILABLE) {
			localizer.setFormattedText(messageLabel, "messageLabel.text.updateAvailable", latestVersion);
			localizer.setText(downloadHyperlink, "downloadHyperlink.text.downloadNow");
			messageLabel.setIcon(infoIcon);
		} else if (status == UP_TO_DATE) {
			localizer.setText(messageLabel, "messageLabel.text.upToDate");
			messageLabel.setIcon(null);
		} else if (status == UPDATE_DOWNLOADED) {
			localizer.setFormattedText(messageLabel, "messageLabel.text.updateDownloaded", latestVersion);
			messageLabel.setIcon(infoIcon);
		} else if (status instanceof Downloading) {
			localizer.setFormattedText(messageLabel, "messageLabel.text.downloading", latestVersion);
			messageLabel.setIcon(null);
			downloadProgressBar.setMaximum((int) ((Downloading) status).totalNumBytes);
			downloadProgressBar.setValue((int) ((Downloading) status).numBytesDownloaded);
		} else if (status instanceof CheckFailed) {
			localizer.setText(messageLabel, "messageLabel.text.checkFailed");
			messageLabel.setIcon(errorIcon);
		} else if (status instanceof DownloadFailed) {
			localizer.setText(messageLabel, "messageLabel.text.downloadFailed");
			messageLabel.setIcon(errorIcon);
			localizer.setText(downloadHyperlink, "downloadHyperlink.text.retry");
		} else if (status instanceof ChecksumFailed) {
			localizer.setText(messageLabel, "messageLabel.text.checksumFailed");
			messageLabel.setIcon(errorIcon);
			localizer.setText(downloadHyperlink, "downloadHyperlink.text.retry");
		} else if (status instanceof UpdateFailed) {
			localizer.setText(messageLabel, "messageLabel.text.updateFailed");
			messageLabel.setIcon(errorIcon);
		}
	}

	public void setCancelDownloadAction(Action action) {
		String text = cancelDownloadHyperlink.getText();
		cancelDownloadHyperlink.setAction(action);
		cancelDownloadHyperlink.setText(text);
	}

	public void setCheckForUpdatesAction(Action action) {
		String text = checkForUpdateHyperlink.getText();
		checkForUpdateHyperlink.setAction(action);
		checkForUpdateHyperlink.setText(text);
	}

	public void setDownloadAction(Action action) {
		String text = downloadHyperlink.getText();
		downloadHyperlink.setAction(action);
		downloadHyperlink.setText(text);
	}

	public void setInstallAction(Action action) {
		String text = installHyperlink.getText();
		installHyperlink.setAction(action);
		installHyperlink.setText(text);
	}

	public void setLatestVersion(String latestVersion) {
		if (!Objects.equals(this.latestVersion, latestVersion)) {
			this.latestVersion = latestVersion;
			modelToView();
		}
	}

	public void setStatus(UpdateStatus newStatus) {
		if (status != newStatus) {
			status = newStatus;
			modelToView();
		}
	}

	public void showInstallInstructionsDialog() {
		JOptionPane
				.showMessageDialog(
						SwingUtilities.getWindowAncestor(this),
						localizer.getFormattedString("installInstructionsDialog.message", getLatestVersion()),
						localizer.getString("installInstructionsDialog.title"),
						JOptionPane.INFORMATION_MESSAGE);
	}
}
