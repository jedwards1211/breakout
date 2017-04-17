package org.breakout.model.parsed;

import java.util.HashMap;
import java.util.Map;

import org.breakout.model.ShotKey;

public class ParsedProject {
	public final Map<String, ParsedCave> caves = new HashMap<>();
	public final Map<ShotKey, ParsedShot> shots = new HashMap<>();
}
