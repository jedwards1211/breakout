package org.andork.torquescape.model.xform;

import static org.andork.vecmath.FloatArrayVecmath.mmul;
import static org.andork.vecmath.FloatArrayVecmath.setIdentity;

public class CompoundXformFunction implements IXformFunction
{
	private IXformFunction[ ]	curves;
	private float[ ]			tempMat	= new float[ 16 ];
	
	public CompoundXformFunction( IXformFunction ... curves )
	{
		this.curves = curves;
	}
	
	@Override
	public float[ ] eval( float param , float[ ] outXform )
	{
		setIdentity( outXform );
		
		for( IXformFunction curve : curves )
		{
			curve.eval( param , tempMat );
			mmul( outXform , tempMat , outXform );
		}
		
		return outXform;
	}
	
}