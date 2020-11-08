package org.andork.util;

import java.text.CharacterIterator;
import java.text.StringCharacterIterator;

public class SizeFormat {
	public static SizeFormat DEFAULT = new SizeFormat();

	public String format(long bytes) {
		if (-1000 < bytes && bytes < 1000) {
			return bytes + " B";
		}
		CharacterIterator ci = new StringCharacterIterator("kMGTPE");
		while (bytes <= -999950 || bytes >= 999950) {
			bytes /= 1000;
			ci.next();
		}
		return String.format("%.1f %cB", bytes / 1000.0, ci.current());
	}

	public String formatProgress(long completed, long total) {
		if (-1000 < total && total < 1000) {
			return total + " B";
		}
		CharacterIterator ci = new StringCharacterIterator("kMGTPE");
		while (total <= -999950 || total >= 999950) {
			completed /= 1000;
			total /= 1000;
			ci.next();
		}
		return String.format("%.1f/%.1f %cB", completed / 1000.0, total / 1000.0, ci.current());
	}
}
