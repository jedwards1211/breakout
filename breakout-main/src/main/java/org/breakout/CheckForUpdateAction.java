package org.breakout;

import java.awt.BorderLayout;
import java.awt.Dialog.ModalityType;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import org.andork.awt.I18n.Localizer;
import org.andork.bind.QObjectAttributeBinder;
import org.andork.bind.ui.ButtonSelectedBinder;
import org.andork.io.Downloader;
import org.andork.swing.FromEDT;
import org.andork.swing.JOptionPaneBuilder;
import org.andork.swing.OnEDT;
import org.andork.swing.async.SelfReportingTask;
import org.andork.swing.async.TaskPane;
import org.andork.task.Task;
import org.andork.util.SizeFormat;
import org.breakout.model.RootModel;
import org.breakout.update.UpdateCheck;

public class CheckForUpdateAction extends AbstractAction {
	private static final long serialVersionUID = 4944289444659593701L;

	private static final Logger logger = Logger.getLogger(CheckForUpdateAction.class.getName());

	BreakoutMainView mainView;

	public CheckForUpdateAction(BreakoutMainView mainView) {
		this.mainView = mainView;
		OnEDT.onEDT(() -> {
			Localizer localizer = mainView.getI18n().forClass(CheckForUpdateAction.this.getClass());
			localizer.setName(CheckForUpdateAction.this, "name");
		});
	}

	public void checkForUpdate(boolean foreground) {
		Task<?> task = new Task<Void>() {
			@Override
			protected Void work() throws Exception {
				return null;
			}
		};
		task.setStatus("Checking for updates");
		task.setIndeterminate(true);

		TaskPane taskPane = new TaskPane(task);

		Window owner = SwingUtilities.getWindowAncestor(mainView.getMainPanel());
		JDialog checkingDialog = new JDialog(owner);
		checkingDialog.setTitle("Software Update");
		checkingDialog.setModalityType(ModalityType.DOCUMENT_MODAL);
		checkingDialog.getContentPane().add(taskPane, BorderLayout.CENTER);
		checkingDialog.pack();
		checkingDialog.setLocationRelativeTo(owner);
		checkingDialog.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

		UpdateCheck.checkForUpdateInBackground(result -> {
			UpdateCheck.AvailableResult available = FromEDT.fromEDT(() -> {
				checkingDialog.setVisible(false);
				checkingDialog.dispose();

				if (task.isCanceled())
					return null;

				if (result == UpdateCheck.UpToDateResult) {
					if (foreground) {
						new JOptionPaneBuilder()
							.message("Your version of Breakout is up to date!")
							.defaultOption()
							.showDialog(mainView.getMainPanel(), "Software Update");
					}
					return null;
				}
				if (result instanceof UpdateCheck.FailedResult) {
					if (foreground) {
						new JOptionPaneBuilder()
							.message(
								"Update check failed: "
									+ ((UpdateCheck.FailedResult) result).cause.getLocalizedMessage())
							.error()
							.defaultOption()
							.showDialog(mainView.getMainPanel(), "Software Update");
					}
					return null;
				}
				if (result instanceof UpdateCheck.AvailableResult) {
					JCheckBox checkForUpdatesOnStartupCheckbox = new JCheckBox("Check for Updates on Startup");
					new ButtonSelectedBinder(checkForUpdatesOnStartupCheckbox)
						.bind(
							new QObjectAttributeBinder<>(RootModel.checkForUpdatesOnStartup)
								.bind(mainView.rootModelBinder));

					int choice =
						new JOptionPaneBuilder()
							.message(
								"A new version of Breakout is available.  Do you want to update now?",
								checkForUpdatesOnStartupCheckbox)
							.yesNo()
							.showDialog(mainView.getMainPanel(), "Software Update");

					if (choice == JOptionPane.YES_OPTION) {
						logger.info("User chose to update");
						return ((UpdateCheck.AvailableResult) result);
					}
					else {
						logger.info("User chose not to update");
						return null;
					}
				}
				return null;
			});

			if (available != null) {
				File downloadDir = new File(System.getProperty("user.home"), "Downloads");
				SelfReportingTask<Void> downloadTask = new SelfReportingTask<Void>(mainView.getMainPanel()) {
					@Override
					protected JDialog createDialog(Window owner) {
						JDialog dialog = super.createDialog(owner);
						dialog.setTitle("Software Update");
						return dialog;
					}

					@Override
					protected Void workDuringDialog() throws Exception {
						setStatus("Downloading " + available.asset.getName());

						File destFile = new File(downloadDir, available.asset.getName());
						Downloader downloader =
							new Downloader().url(available.asset.getBrowserDownloadUrl()).destFile(destFile);
						logger.info(() -> "Downloading " + available.asset.getBrowserDownloadUrl() + " to " + destFile);
						downloader.addPropertyChangeListener(e -> {
							setStatus(
								"Downloading "
									+ available.asset.getName()
									+ " ("
									+ SizeFormat.DEFAULT
										.formatProgress(downloader.getNumBytesDownloaded(), downloader.getTotalSize())
									+ ")");
							setTotal(downloader.getTotalSize());
							setCompleted(downloader.getNumBytesDownloaded());
						});

						downloader.download();

						logger.info(() -> "Downloaded " + available.asset.getBrowserDownloadUrl() + " to " + destFile);

						return null;
					}
				};

				try {
					downloadTask.call();
				}
				catch (Exception ex) {
					logger.log(Level.SEVERE, "Failed to download update", ex);
					OnEDT.onEDT(() -> {
						new JOptionPaneBuilder()
							.message(
								"Failed to download " + available.asset.getName() + ": " + ex.getLocalizedMessage())
							.error()
							.showDialog(mainView.getMainPanel(), "Software Update");
					});
					return;
				}
			}
		});

		if (foreground)
			checkingDialog.setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		logger.info("User requested check for updates");
		checkForUpdate(true);
	}
}
