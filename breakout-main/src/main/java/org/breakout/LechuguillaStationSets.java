package org.breakout;

import static java.lang.Character.*;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LechuguillaStationSets {
	private LechuguillaStationSets() {
	}

	private static final Pattern stationPattern = Pattern.compile("[^-,\\s]+");

	
	public static Set<String> parse(String stations) {
		// fix for typo in R-EY1A-6A (what they meant was REY1A-6A)
		stations = stations.replaceAll("(((?<![,-])\\s+|^)[\\p{L}])-(?=[\\p{L}]{2})", "$1");

		Set<String> result = new HashSet<>();
		
		Matcher m = stationPattern.matcher(stations);
		
		String prevStation = null;
		int prevEnd = -1;

		while (m.find()) {
			String nextStation = m.group();
			if (prevStation != null && prevEnd + 1 == m.start() && (
				stations.charAt(prevEnd) == '-' || stations.charAt(prevEnd) == ',' ||
				!isLetter(nextStation.charAt(0)) || nextStation.length() == 1)) {
				
				int prevIndex = getSpliceIndex(prevStation, nextStation);
				int nextIndex = 0;
				if (stations.charAt(prevEnd) == '-') {
					while (prevIndex + 1 < prevStation.length() && nextIndex + 1 < nextStation.length()) {
						if (prevStation.charAt(prevIndex) != nextStation.charAt(nextIndex) ||
							(isDigit(prevStation.charAt(prevIndex)) &&
							parseInt(prevStation, prevIndex) != parseInt(nextStation, nextIndex)) ||
							(isLetter(prevStation.charAt(prevIndex)) &&
							parseBase26(prevStation, prevIndex) != parseBase26(nextStation, nextIndex))) {
							break;
						}
						prevIndex++;
						nextIndex++;
					}

					String prefix = prevStation.substring(0, prevIndex);

					if (isDigit(nextStation.charAt(nextIndex))) {
						int trailingIndex = nextIndex;
						while (trailingIndex < nextStation.length() &&
								isDigit(nextStation.charAt(trailingIndex))) {
							trailingIndex++;
						}
						String suffix = nextStation.substring(trailingIndex);
						if (!prevStation.endsWith(suffix)) suffix = "";

						int prevNum = isDigit(prevStation.charAt(prevIndex))
							? parseInt(prevStation, prevIndex)
							: 1;
						int nextNum = parseInt(nextStation, nextIndex);
						// this ensures that EY56A-3 is treated as EY56A EY56A1-3 instead of
						// EY56A EY3
						if (prevNum > nextNum) {
							prevNum = 1;
							prevIndex = prevStation.length();
							prefix = prevStation;
						}
						for (int num = prevNum; num <= nextNum; num++) {
							result.add(prefix + num + suffix);
						}
					} else if (isLetter(prevStation.charAt(prevIndex)) &&
							isLetter(nextStation.charAt(nextIndex)) &&
							nextStation.endsWith(prevStation.substring(nonLetterIndex(prevStation, prevIndex + 1)))) {
						String suffix = nextStation.substring(nextIndex + 1);
						if (!prevStation.endsWith(suffix)) suffix = "";

						int prevNum = parseBase26(prevStation, prevIndex);
						int nextNum = parseBase26(nextStation, nextIndex);
						// heuristic to prevent typos like A-LD (which should be A-L)
						// from getting interpreted as tons of stations
						if (nextNum - prevNum > 200) {
							prevNum = (prevStation.charAt(prevIndex) - 'A') + 1;
							nextNum = (nextStation.charAt(nextIndex) - 'A') + 1;
						}
						for (int num = prevNum; num <= nextNum; num++) {
							result.add(prefix + stringifyBase26(num) + suffix);
						}
					}
				}
				nextStation = prevStation.substring(0, prevIndex) + nextStation.substring(nextIndex);
			}
			result.add(nextStation);
			
			prevStation = nextStation;
			prevEnd = m.end();
		}
		
		return result;
	}
	
	private static int nonLetterIndex(String s, int i) {
		while (i < s.length() && isLetter(s.charAt(i))) i++;
		return i;
	}
	
	private static int parseInt(String s, int i) {
		int result = 0;
		while (i < s.length() && isDigit(s.charAt(i))) {
			result = 10 * result + (s.charAt(i++) - '0');
		}
		return result;
	}
	
	static int parseBase26(String s, int i) {
		int result = 0;
		while (i < s.length() && isLetter(s.charAt(i))) {
			result = 26 * result + (s.charAt(i++) - 'A') + 1;
		}
		return result;
	}
	
	static String stringifyBase26(int num) {
		String result = String.valueOf((char)('A' + ((num - 1) % 26)));
		while ((num /= 26) > 0) {
			result = String.valueOf((char)('A' + ((num - 1) % 26))) + result;
		}
		return result;
	}
	
	private static int getSpliceIndex(String prevStation, String nextStation) {
		CharType targetCharType = getCharType(nextStation.charAt(0));
		int i = prevStation.length() - 1;
		while (i > 0 && getCharType(prevStation.charAt(i)) != targetCharType) i--;
		while (i > 0 && getCharType(prevStation.charAt(i - 1)) == targetCharType) i--;
		return i > 0 ? i : prevStation.length();
	}

	private enum CharType {
		LETTER, DIGIT, SYMBOL;
	}
	
	private static CharType getCharType(char c) {
		if (Character.isLetter(c)) return CharType.LETTER;
		if (Character.isDigit(c)) return CharType.DIGIT;
		return CharType.SYMBOL;
	}
}
