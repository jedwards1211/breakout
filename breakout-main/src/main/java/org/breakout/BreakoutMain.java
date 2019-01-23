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
package org.breakout;

import java.awt.Color;
import java.awt.Frame;
import java.awt.Image;
import java.io.FileInputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

import org.andork.logging.LoggerPrintStream;
import org.andork.swing.OnEDT;
import org.andork.swing.SplashFrame;

public class BreakoutMain {
	private static final Path rootDirectory;
	private static final Path backupDirectory;
	private static final Path rootSettingsFile;
	private static Path logDirectory;
	private static SplashFrame splash;
	private static Image splashImage;
	private static BreakoutMainFrame frame;

	private static final Logger logger = Logger.getLogger(BreakoutMain.class.getName());

	static {
		if (System.getProperty("rootDirectory") != null) {
			rootDirectory = Paths.get(System.getProperty("rootDirectory"));
		} else {
			Path homePath;
			try {
				homePath = Paths.get(System.getProperty("user.home"));
			} catch (Exception ex) {
				logger.log(Level.SEVERE, "Failed to get user home directory!", ex);
				homePath = Paths.get(System.getProperty("user.dir"));
			}
			rootDirectory = homePath.resolve(".breakout");
		}
		rootSettingsFile = rootDirectory.resolve("settings.json");
		backupDirectory = rootDirectory.resolve("backup");
	}

	private BreakoutMain() {

	}

	public static void main(String[] args) {
		createRootDirectory();
//		configureLogging();
		createBackupDirectory();

		logger.info(() -> "rootDirectory:    " + rootDirectory);
		logger.info(() -> "rootSettingsFile: " + rootSettingsFile);
		logger.info(() -> "backupDirectory:  " + backupDirectory);
		logger.info(() -> "logDirectory:     " + logDirectory);

		logger.info("Launching...");

		System.setProperty("apple.eawt.quitStrategy", "CLOSE_ALL_WINDOWS");
		System.setProperty("apple.laf.useScreenMenuBar", "true");
		System.setProperty("com.apple.mrj.application.apple.menu.about.name", "Breakout");

		try {
			splashImage = ImageIO.read(BreakoutMain.class.getResource("splash.png"));
		} catch (Exception e) {
			logger.log(Level.WARNING, "Failed to load splash image", e);
		}

		OnEDT.onEDT(() -> {
			try {
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			} catch (Exception e) {
				logger.log(Level.WARNING, "Failed to set system look and feel", e);
			}

			splash = new SplashFrame();
			splash.setTitle("Breakout");

			splash.getImagePanel().setImage(splashImage);
			splash.getStatusLabel().setForeground(Color.WHITE);
			splash.getStatusLabel().setText("Initializing 3D View...");
			splash.getProgressBar().setIndeterminate(true);

			splash.pack();
			splash.setLocationRelativeTo(null);
			splash.setVisible(true);
		});

		ExecutorService loader = Executors.newSingleThreadExecutor(r -> {
			Thread thread = new Thread(r);
			thread.setName("BreakoutMainView loader");
			thread.setDaemon(true);
			return thread;
		});
		BreakoutMainView view;
		try {
			view = loader.submit(() -> new BreakoutMainView()).get();
		} catch (InterruptedException | ExecutionException e) {
			logger.log(Level.SEVERE, "Failed to create main view", e);
			JOptionPane.showMessageDialog(
					null, "Failed to create main view: " + e.getLocalizedMessage(),
					"Fatal Error", JOptionPane.ERROR_MESSAGE);
			logger.info("exiting with code 1");
			System.exit(1);
			return;
		}
		loader.shutdown();

		try {
			OnEDT.onEDT(() -> {
				if (!splash.isVisible()) {
					// user closed splash; exit
					logger.info("User closed splash");
					logger.info("exiting with code 0");
					System.exit(0);
					return;
				}
				frame = new BreakoutMainFrame(view);
				frame.setTitle("Breakout");
				frame.setExtendedState(Frame.MAXIMIZED_BOTH);
				splash.setVisible(false);
			});
			// unfortunately, making the frame visible on the EDT causes buggy behavior with the drawers due to some
			// obscure bug in JOGL (or jogl-awt)
			frame.setVisible(true);
			logger.info("opened main window");
		} catch (Exception ex) {
			logger.log(Level.SEVERE, "Failed to create or open main window", ex);
		}
	}

	public static Path getRootDirectory() {
		return rootDirectory;
	}

	public static Path getBackupDirectory() {
		return backupDirectory;
	}

	public static Path getRootSettingsFile() {
		return rootSettingsFile;
	}

	public static Path getLogDirectory() {
		return logDirectory;
	}

	private static void createRootDirectory() {
		try {
			Files.createDirectories(rootDirectory);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private static void createBackupDirectory() {
		try {
			Files.createDirectories(backupDirectory);
		} catch (Exception ex) {
			logger.log(Level.SEVERE, "failed to create backup directory", ex);
		}
	}

	private static void configureLogging() {
		try {
			Properties props = new Properties();
			if (System.getProperty("java.util.logging.config.file") != null) {
				props.load(new FileInputStream(System.getProperty("java.util.logging.config.file")));
			} else {
				props.load(BreakoutMain.class.getResourceAsStream("logging.properties"));
			}

			String logfile = props.getProperty("java.util.logging.FileHandler.pattern");
			if (logfile != null && Paths.get(logfile).isAbsolute()) {
				logDirectory = Paths.get(logfile).getParent();
			} else {
				logDirectory = rootDirectory.resolve("log");
				Files.createDirectories(logDirectory);
				if (logfile != null) {
					props.setProperty(
							"java.util.logging.FileHandler.pattern",
							logDirectory.resolve(logfile).toString());
				}
			}

			PipedInputStream configStream = new PipedInputStream();
			PipedOutputStream out = new PipedOutputStream(configStream);
			props.store(out, null);
			out.close();
			LogManager.getLogManager().readConfiguration(configStream);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		String handlers = LogManager.getLogManager().getProperty("handlers");

		if (handlers != null && !handlers.contains("java.util.logging.ConsoleHandler")) {
			try {
				System.setOut(new LoggerPrintStream(Logger.getLogger("System.out"), Level.INFO, true));
				System.setErr(new LoggerPrintStream(Logger.getLogger("System.err"), Level.WARNING, true));
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}
}
