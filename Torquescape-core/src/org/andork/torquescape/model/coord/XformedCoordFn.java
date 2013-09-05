package org.andork.torquescape.model.coord;

import org.andork.torquescape.model.xform.IXformFn;
import org.andork.vecmath.FloatArrayVecmath;

public class XformedCoordFn implements ICoordFn
{
	ICoordFn	coordFn;
	IXformFn	xformFn;
	
	float[ ]	xform	= new float[ 16 ];
	
	public XformedCoordFn( ICoordFn coordFn , IXformFn xformFn )
	{
		this.coordFn = coordFn;
		this.xformFn = xformFn;
	}
	
	@Override
	public int getCoordCount( float param )
	{
		return coordFn.getCoordCount( param );
	}
	
	@Override
	public void eval( float param , int index , float[ ] result )
	{
		coordFn.eval( param , index , result );
		xformFn.eval( param , xform );
		FloatArrayVecmath.mpmulAffine( xform , result );
	}
}
