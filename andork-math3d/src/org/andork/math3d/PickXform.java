package org.andork.math3d;

import static org.andork.math3d.Vecmath.calcClippingPlanes;
import static org.andork.math3d.Vecmath.getColumn3;
import static org.andork.math3d.Vecmath.invAffine;
import static org.andork.math3d.Vecmath.mvmulAffine;
import static org.andork.math3d.Vecmath.newMat4f;

public class PickXform
{
	final float[ ]	vi		= newMat4f( );
	final float[ ]	btlrnf	= new float[ 6 ];
	
	public void calculate( float[ ] p , float[ ] v )
	{
		calcClippingPlanes( p , btlrnf );
		invAffine( v , vi );
	}
	
	public void getOrigin( float[ ] out )
	{
		getColumn3( vi , 3 , out );
	}
	
	public void xform( float x , float y , float canvasWidth , float canvasHeight , float[ ] rayOut , int rayOuti )
	{
		x = btlrnf[ 2 ] + x / canvasWidth * ( btlrnf[ 3 ] - btlrnf[ 2 ] );
		y = btlrnf[ 1 ] + y / canvasHeight * ( btlrnf[ 0 ] - btlrnf[ 1 ] );
		mvmulAffine( vi , x , y , -btlrnf[ 4 ] , rayOut , rayOuti );
	}
}
