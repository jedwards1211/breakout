package org.andork.torquescape.model.old.section;

import java.util.ArrayList;

public interface ISectionFunction
{
	public ArrayList<SectionCurve> eval( float param , ArrayList<SectionCurve> out );
}