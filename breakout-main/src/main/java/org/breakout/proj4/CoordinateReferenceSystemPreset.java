package org.breakout.proj4;

import org.locationtech.proj4j.CRSFactory;
import org.locationtech.proj4j.CoordinateReferenceSystem;

public enum CoordinateReferenceSystemPreset {
	WGS84("Lat/Long (WGS84)", "+proj=latlong +ellps=WGS84 +datum=WGS84"),
	NAD83("Lat/Long (NAD83)", "+proj=latlong +ellps=GRS80 +datum=NAD83"),
	NAD27("Lat/Long (NAD27)", "+proj=latlong +ellps=clrk66 +datum=NAD27");

	private final String displayName;
	private final CoordinateReferenceSystem crs;

	private CoordinateReferenceSystemPreset(String displayName, String proj4) {
		this.displayName = displayName;
		this.crs = new CRSFactory().createFromParameters(displayName, proj4);
	}

	public String toString() {
		return displayName;
	}

	public CoordinateReferenceSystem crs() {
		return this.crs;
	}
}
