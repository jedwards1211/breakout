package org.andork.torquescape.model.xform;

import static org.andork.vecmath.FloatArrayVecmath.mmul;
import static org.andork.vecmath.FloatArrayVecmath.setIdentity;

public class CompoundXformFn implements IXformFn
{
	private IXformFn[ ]	curves;
	private float[ ]			tempMat	= new float[ 16 ];
	
	public CompoundXformFn( IXformFn ... curves )
	{
		this.curves = curves;
	}
	
	@Override
	public float[ ] eval( float param , float[ ] outXform )
	{
		setIdentity( outXform );
		
		for( IXformFn curve : curves )
		{
			curve.eval( param , tempMat );
			mmul( outXform , tempMat , outXform );
		}
		
		return outXform;
	}
	
}