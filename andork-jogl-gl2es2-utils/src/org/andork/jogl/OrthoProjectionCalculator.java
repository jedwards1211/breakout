package org.andork.jogl;

import org.andork.math3d.Vecmath;

public class OrthoProjectionCalculator implements ProjectionCalculator
{
	public final float[ ]	orthoFrame	= new float[ 6 ];
	
	public OrthoProjectionCalculator( float ... orthoFrame )
	{
		System.arraycopy( orthoFrame , 0 , this.orthoFrame , 0 , 6 );
	}
	
	@Override
	public void calculate( float width , float height , float[ ] pOut )
	{
		Vecmath.ortho( pOut , orthoFrame[ 0 ] , orthoFrame[ 1 ] , orthoFrame[ 2 ] , orthoFrame[ 3 ] , orthoFrame[ 4 ] , orthoFrame[ 5 ] );
	}
	
}
