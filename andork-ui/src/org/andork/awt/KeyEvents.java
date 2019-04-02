package org.andork.awt;

import java.awt.event.KeyEvent;
import java.util.regex.Pattern;

public class KeyEvents {
	private static final boolean isMacOS = Pattern.compile("\\bmac|\\bos\\s*x\\b", Pattern.CASE_INSENSITIVE)
		.matcher(System.getProperty("os.name")).find();

	public static final int CTRL_OR_META_DOWN_MASK = isMacOS
		? KeyEvent.META_DOWN_MASK
		: KeyEvent.CTRL_DOWN_MASK;
}
