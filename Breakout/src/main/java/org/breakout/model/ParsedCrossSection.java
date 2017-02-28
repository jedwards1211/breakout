package org.breakout.model;

import org.andork.unit.Length;
import org.andork.unit.UnitizedDouble;

public class ParsedCrossSection {
	public CrossSectionType type = CrossSectionType.LRUD;
	public UnitizedDouble<Length>[] measurements;
}
