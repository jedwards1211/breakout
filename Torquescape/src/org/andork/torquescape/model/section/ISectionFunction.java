package org.andork.torquescape.model.section;

import java.util.ArrayList;

import org.andork.j3d.math.J3DTempsPool;

public interface ISectionFunction
{
	public ArrayList<SectionCurve> eval( float param , J3DTempsPool pool , ArrayList<SectionCurve> out );
}
