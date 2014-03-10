package org.andork.math3d;

import static org.andork.math3d.Vecmath.calcClippingPlanes;
import static org.andork.math3d.Vecmath.getColumn3;
import static org.andork.math3d.Vecmath.invAffine;
import static org.andork.math3d.Vecmath.mmulAffine;
import static org.andork.math3d.Vecmath.mpmulAffine;
import static org.andork.math3d.Vecmath.mvmulAffine;
import static org.andork.math3d.Vecmath.newMat4f;

import org.andork.util.Reparam;

public class PickXform
{
	boolean			perspective	= false;
	final float[ ]	vi			= newMat4f( );
	final float[ ]	btlrnf		= new float[ 6 ];
	
	public void calculate( float[ ] p , float[ ] v )
	{
		perspective = p[ 15 ] == 0;
		if( p[ 15 ] == 0 )
		{
			calcClippingPlanes( p , btlrnf );
			invAffine( v , vi );
		}
		else
		{
			mmulAffine( p , v , vi );
			invAffine( vi , vi );
		}
	}
	
	public void xform( float x , float y , float canvasWidth , float canvasHeight , float[ ] origin , float[ ] rayOut )
	{
		if( perspective )
		{
			getColumn3( vi , 3 , origin );
			x = btlrnf[ 2 ] + x / canvasWidth * ( btlrnf[ 3 ] - btlrnf[ 2 ] );
			y = btlrnf[ 1 ] + y / canvasHeight * ( btlrnf[ 0 ] - btlrnf[ 1 ] );
			mvmulAffine( vi , x , y , -btlrnf[ 4 ] , rayOut );
		}
		else
		{
			x = Reparam.linear( x , 0 , canvasWidth , -1 , 1 );
			y = Reparam.linear( y , 0 , canvasHeight , 1 , -1 );
			mpmulAffine( vi , x , y , 0 , origin );
			mvmulAffine( vi , 0 , 0 , 1 , rayOut );
		}
	}
	
	public void xform( float x , float y , float canvasWidth , float canvasHeight , float[ ] origin , int origini , float[ ] rayOut , int rayOuti )
	{
		if( perspective )
		{
			getColumn3( vi , 3 , origin , origini );
			x = btlrnf[ 2 ] + x / canvasWidth * ( btlrnf[ 3 ] - btlrnf[ 2 ] );
			y = btlrnf[ 1 ] + y / canvasHeight * ( btlrnf[ 0 ] - btlrnf[ 1 ] );
			mvmulAffine( vi , x , y , -btlrnf[ 4 ] , rayOut , rayOuti );
		}
		else
		{
			x = Reparam.linear( x , 0 , canvasWidth , -1 , 1 );
			y = Reparam.linear( y , 0 , canvasHeight , 1 , -1 );
			mpmulAffine( vi , x , y , 0 , origin , origini );
			mvmulAffine( vi , 0 , 0 , 1 , rayOut , rayOuti );
		}
	}
}
