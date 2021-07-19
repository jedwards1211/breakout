package org.breakout.model.parsed;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.breakout.model.ShotKey;
import org.breakout.model.raw.SurveyLead;

public class ParsedProject {
	public final Map<String, ParsedCave> caves = new HashMap<>();
	public final Map<ShotKey, ParsedShot> shots = new HashMap<>();
	public final List<SurveyLead> leads = new ArrayList<>();
}
