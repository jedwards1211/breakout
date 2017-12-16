package org.breakout.util;

public class StationNames {
	private StationNames() {

	}

	/**
	 * @param stationName
	 *            the name of a station
	 * @return the best guess at the survey designation for the station
	 */
	public static final String getSurveyDesignation(String stationName) {
		int i = stationName.length() - 1;
		while (i >= 0 &&
				!Character.isDigit(stationName.charAt(i)) &&
				!Character.isLetter(stationName.charAt(i))) {
			i--;
		}
		while (i >= 0 && Character.isLetter(stationName.charAt(i))) {
			i--;
		}
		while (i >= 0 && Character.isDigit(stationName.charAt(i))) {
			i--;
		}
		return i < 0 ? stationName : stationName.substring(0, i + 1);
	}

}
