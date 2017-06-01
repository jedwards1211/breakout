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
import java.io.IOException;
import java.io.PrintStream;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.swing.UIManager;

import org.andork.io.MultiplexOutputStream;
import org.andork.logging.LoggerPrintStream;
import org.andork.swing.OnEDT;
import org.andork.swing.SplashFrame;

public class Breakout {
	private static SplashFrame splash;
	private static Image splashImage;
	private static BreakoutMainFrame frame;

	private Breakout() {

	}

	public static void main(String[] args)
			throws InterruptedException, ExecutionException, SecurityException, IOException {
		configureLogging();

		System.setProperty("apple.laf.useScreenMenuBar", "true");
		System.setProperty("com.apple.mrj.application.apple.menu.about.name", "Breakout");

		try {
			splashImage = ImageIO.read(Breakout.class.getResource("splash.png"));
		} catch (Exception e) {
			e.printStackTrace();
		}

		OnEDT.onEDT(() -> {
			try {
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			} catch (Exception e) {
				e.printStackTrace();
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
		BreakoutMainView view = loader.submit(() -> new BreakoutMainView()).get();
		loader.shutdown();

		OnEDT.onEDT(() -> {
			if (!splash.isVisible()) {
				// user closed splash; exit
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
	}

	private static void configureLogging() {
		try {
			if (System.getProperty("java.util.logging.config.file") == null) {
				LogManager.getLogManager().readConfiguration(Breakout.class.getResourceAsStream("logging.properties"));
			} else {
				LogManager.getLogManager().readConfiguration();
			}
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
