package org.andork.torquescape.model.section;

import org.andork.j3d.math.J3DTempsPool;

public interface ICrossSectionFunction
{
	public ICrossSectionCurve[ ] eval( float param , J3DTempsPool pool );
}
