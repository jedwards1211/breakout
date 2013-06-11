package org.andork.torquescape.model.section;

import org.andork.j3d.math.J3DTempsPool;

public interface ISectionFunction
{
	public ISectionCurve[ ] eval( float param , J3DTempsPool pool );
}
