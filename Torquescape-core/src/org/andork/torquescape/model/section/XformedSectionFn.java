package org.andork.torquescape.model.section;

import org.andork.torquescape.model.xform.IXformFn;
import org.andork.vecmath.FloatArrayVecmath;

public class XformedSectionFn implements ISectionFn
{
	ISectionFn		sectionFn;
	IXformFn	xformFn;
	
	float[ ]		xform	= new float[ 16 ];
	
	public XformedSectionFn( ISectionFn sectionFn , IXformFn xformFn )
	{
		this.sectionFn = sectionFn;
		this.xformFn = xformFn;
	}

	@Override
	public int getVertexCount( float param )
	{
		return sectionFn.getVertexCount( param );
	}
	
	@Override
	public void eval( float param , int index , float[ ] result )
	{
		sectionFn.eval( param , index , result );
		xformFn.eval( param , xform );
		FloatArrayVecmath.mpmulAffine( xform , result );
	}
}
