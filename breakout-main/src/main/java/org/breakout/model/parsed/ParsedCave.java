package org.breakout.model.parsed;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.breakout.model.raw.SurveyLead;

public class ParsedCave {
	public final List<ParseMessage> messages = new ArrayList<>();
	public ParsedField<String> name;
	public final Map<String, List<SurveyLead>> leads = new HashMap<>();
	public final List<ParsedTrip> trips = new ArrayList<>();

	public void addLead(String station, SurveyLead lead) {
		List<SurveyLead> leads = this.leads.get(station);
		if (leads == null) {
			leads = new ArrayList<>();
			this.leads.put(station, leads);
		}
		leads.add(lead);
	}
}
