package org.breakout.model;

import java.util.Comparator;

public interface HasStationKey {
	StationKey stationKey();

	public static Comparator<HasStationKey> comparator = (a, b) -> StationKey.compare(a.stationKey(), b.stationKey());
}
