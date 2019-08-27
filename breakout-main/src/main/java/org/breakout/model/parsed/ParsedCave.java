package org.breakout.model.parsed;

import java.util.ArrayList;
import java.util.List;

public class ParsedCave {
	public final List<ParseMessage> messages = new ArrayList<>();
	public ParsedField<String> name;
	public final List<ParsedTrip> trips = new ArrayList<>();
}
