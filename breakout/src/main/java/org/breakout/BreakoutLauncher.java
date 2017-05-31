package org.breakout;

import java.lang.reflect.Method;

import javax.swing.JOptionPane;

public class BreakoutLauncher {
	public static void main(String[] args) throws Exception {
		checkJavaVersion();
		Class breakout = Class.forName("org.breakout.Breakout");
		Method main = breakout.getMethod("main", new Class[] { String[].class });
		main.invoke(null, new Object[] { args });
	}

	private static int MAJOR = 1;
	private static int MINOR = 8;
	private static String MESSAGE = "Breakout requires Java version " + MAJOR + "." + MINOR + "+ to run.\n" +
			"You are running version " + System.getProperty("java.version") + ".\n" +
			"Please download and install Java " + MAJOR + "." + MINOR + " or a later version.";

	private static void checkJavaVersion() {
		String[] versionPieces = System.getProperty("java.version").split("\\.");
		int v0 = Integer.parseInt(versionPieces[0]);
		int v1 = Integer.parseInt(versionPieces[1]);

		if (v0 < MAJOR || (v0 == MAJOR && v1 < MINOR)) {
			System.err.println(MESSAGE);
			JOptionPane.showMessageDialog(null,
					"<html>" + MESSAGE.replaceAll("\n", "<br>") + "</html>",
					"Unsupported Java Version", JOptionPane.ERROR_MESSAGE);
			System.exit(1);
		}
	}
}
