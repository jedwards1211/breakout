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
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

import org.andork.swing.OnEDT;
import org.andork.swing.SplashFrame;

public class Breakout {
	public static void main(String[] args) {
		String[] versionPieces = System.getProperty("java.version").split("\\.");
		int v0 = Integer.valueOf(versionPieces[0]);
		int v1 = Integer.valueOf(versionPieces[1]);

		if (v0 == 1 && v1 < 6) {
			JOptionPane
					.showMessageDialog(
							null,
							"<html>FisherRidgeForever requires Java version 1.6+ to run.<br>Please download and install Java 1.6 or a later version.</html>",
							"Fisher Ridge Forever", JOptionPane.ERROR_MESSAGE);
			System.exit(1);
		}

		BlockingQueue<Runnable> runnables = new LinkedBlockingQueue<>();

		System.setProperty("apple.laf.useScreenMenuBar", "true");
		System.setProperty("com.apple.mrj.application.apple.menu.about.name", "Breakout");

		OnEDT.onEDT(() -> {
			Image image = null;

			try {
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			} catch (Exception e) {
				e.printStackTrace();
			}

			final SplashFrame splash = new SplashFrame();
			splash.setTitle("Breakout");

			final Thread loaderThread = new Thread(() -> {
				final BreakoutMainView view = new BreakoutMainView();
				OnEDT.onEDT(() -> {
					if (splash.isVisible()) // i.e. user didn't change their
											// mind and close the splash
					{
						BreakoutMainFrame frame = new BreakoutMainFrame(view);
						frame.setTitle("Breakout");
						frame.setExtendedState(Frame.MAXIMIZED_BOTH);
						runnables.put(() -> {
							// unfortunately the positioning of GLCanvas and
							// NewtCanvasAWT bug out
							// if the frame is set visible on the EDT...
							frame.setVisible(true);
							splash.setVisible(false);
						});
					}
				});
			}, "BreakoutMainView loader");

			loaderThread.setDaemon(true);
			loaderThread.start();

			try {
				image = ImageIO.read(Breakout.class.getResource("splash.png"));
			} catch (Exception e) {
				e.printStackTrace();
			}

			splash.getImagePanel().setImage(image);
			splash.getStatusLabel().setForeground(Color.WHITE);
			splash.getStatusLabel().setText("Initializing 3D View...");
			splash.getProgressBar().setIndeterminate(true);

			splash.pack();
			splash.setLocationRelativeTo(null);
			splash.setVisible(true);
		});

		while (true) {
			try {
				runnables.take().run();
			} catch (InterruptedException e) {
				e.printStackTrace();
				break;
			}
		}
	}
}
