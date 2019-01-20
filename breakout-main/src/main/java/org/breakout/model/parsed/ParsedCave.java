package org.breakout.model.parsed;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParsedCave {
	public final List<ParseMessage> messages = new ArrayList<>();
	public ParsedField<String> name;
	public final Map<String, ParsedFixedStation> fixedStations = new HashMap<>();
	public final Map<String, List<Lead>> leads = new HashMap<>();
	public final List<ParsedTrip> trips = new ArrayList<>();

	public void addLead(String station, Lead lead) {
		List<Lead> leads = this.leads.get(station);
		if (leads == null) {
			leads = new ArrayList<>();
			this.leads.put(station, leads);
		}
		leads.add(lead);
	}
}
