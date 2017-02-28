package org.breakout.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParsedProject {
	public final List<ParsedRow> rows = new ArrayList<>();
	public final Map<ShotKey, ParsedRow> shots = new HashMap<>();
	public final Map<StationKey, ParsedStation> stations = new HashMap<>();
	public final ParseMessages messages = new ParseMessages();
}
