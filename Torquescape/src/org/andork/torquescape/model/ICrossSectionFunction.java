package org.andork.torquescape.model;

import org.andork.j3d.math.J3DTempsPool;

public interface ICrossSectionFunction
{
	public float getLowerBound( );
	
	public float getUpperBound( );
	
	public ICrossSectionCurve[ ] eval( float param , J3DTempsPool pool );
}
