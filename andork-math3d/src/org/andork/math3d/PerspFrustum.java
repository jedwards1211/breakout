package org.andork.math3d;

import static org.andork.math3d.Vecmath.*;

public class PerspFrustum
{
	final float[ ]	origin			= new float[ 3 ];
	final float[ ]	corners			= new float[ 12 ];
	
	boolean			sidesUpToDate	= false;
	final float[ ]	sides			= new float[ 12 ];
	
	boolean			normalsUpToDate	= false;
	final float[ ]	normals			= new float[ 12 ];
	
	public void set( PickXform xform )
	{
		xform.xform( 0f , 0f , 1f , 1f , origin , 0 , corners , 0 );
		xform.xform( 0f , 1f , 1f , 1f , origin , 0 , corners , 0 );
		xform.xform( 1f , 1f , 1f , 1f , origin , 0 , corners , 0 );
		xform.xform( 1f , 0f , 1f , 1f , origin , 0 , corners , 0 );
		
		sidesUpToDate = false;
		normalsUpToDate = false;
	}
	
	public void getOrigin( float[ ] out , int outi )
	{
		out[ outi ] = origin[ 0 ];
		out[ outi + 1 ] = origin[ 1 ];
		out[ outi + 2 ] = origin[ 2 ];
	}
	
	private void updateSides( )
	{
		if( !sidesUpToDate )
		{
			for( int i = 0 ; i < 12 ; i += 3 )
			{
				add3( corners , i , corners , ( i + 3 ) % 12 , sides , i );
				normalize3( sides , i , sides , i );
			}
			sidesUpToDate = true;
		}
	}
	
	private void updateNormals( )
	{
		if( !normalsUpToDate )
		{
			for( int i = 0 ; i < 12 ; i += 3 )
			{
				cross( corners , ( i + 3 ) % 12 , corners , i , normals , i );
				normalize3( normals , i , normals , i );
			}
			normalsUpToDate = true;
		}
	}
	
	public void accomodatePoint( float x , float y , float z )
	{
		updateSides( );
		updateNormals( );
		
		for( int i = 0 ; i < 12 ; i += 3 )
		{
			float d2 = ( x - origin[ 0 ] ) * normals[ i ] +
					( y - origin[ 1 ] ) * normals[ i + 1 ] +
					( z - origin[ 2 ] ) * normals[ i + 2 ];
			
			if( d2 > 0f )
			{
				int si = ( i + 6 ) % 12;
				float f = d2 / dot3( sides , si , normals , i );
				origin[ 0 ] += f * sides[ si ];
				origin[ 1 ] += f * sides[ si + 1 ];
				origin[ 2 ] += f * sides[ si + 2 ];
			}
		}
	}
}
