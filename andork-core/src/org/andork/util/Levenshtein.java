package org.andork.util;

public class Levenshtein {
	private Levenshtein() {

	}

	public static int distance(String a, String b) {
		int d[][] = new int[a.length() + 1][b.length() + 1];

		for (int i = 1; i <= a.length(); i++) {
			d[i][0] = i;
		}
		for (int j = 1; j <= b.length(); j++) {
			d[0][j] = j;
		}

		for (int j = 1; j <= b.length(); j++) {
			for (int i = 1; i <= a.length(); i++) {
				if (a.charAt(i - 1) == b.charAt(j - 1)) {
					d[i][j] = d[i - 1][j - 1];
				} else {
					d[i][j] = Math.min(d[i - 1][j] + 1, Math.min(d[i][j - 1] + 1, d[i - 1][j - 1] + 1));
				}
			}
		}

		return d[a.length()][b.length()];
	}
}
