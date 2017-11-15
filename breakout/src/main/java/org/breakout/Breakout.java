package org.breakout;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;

import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class Breakout {
	public static void main(String[] args) throws Exception {
		try {
			checkJavaVersion();
			Class breakout = Class.forName("org.breakout.BreakoutMain");
			Method main = breakout.getMethod("main", new Class[] { String[].class });
			main.invoke(null, new Object[] { args });
		} catch (Throwable t) {
			t.printStackTrace();
			StringWriter stackTraceWriter = new StringWriter();
			t.printStackTrace(new PrintWriter(stackTraceWriter));
			JTextArea textArea = new JTextArea();
			textArea.setEditable(false);
			textArea.setText(stackTraceWriter.toString());
			textArea.setFont(new Font("Monospaced", Font.PLAIN, 11));
			textArea.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
			JScrollPane errorPane = new JScrollPane(textArea);
			errorPane.setPreferredSize(new Dimension(400, 300));
			Object[] message = {
				"An unexpected error is preventing Breakout from starting.",
				"Please submit a bug report and copy the following error into it:",
				errorPane
			};
			JOptionPane.showMessageDialog(null, message, "Breakout", JOptionPane.ERROR_MESSAGE);
			System.exit(1);
		}
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
