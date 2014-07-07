package org.andork.math3d;

import static org.andork.math3d.Vecmath.subDot3;

/**
 * Represents a planar hull and provides methods to test if it contains or intersects bounding boxes and points.
 * 
 * @author Andy
 */
public class PlanarHull3f
{
	public final float[ ][ ]	vertices;
	public final float[ ][ ]	origins;
	public final float[ ][ ]	normals;
	
	public PlanarHull3f( )
	{
		this( 6 , 8 );
	}
	
	public PlanarHull3f( int numSides , int numVertices )
	{
		vertices = new float[ numVertices ][ 3 ];
		origins = new float[ numSides ][ 3 ];
		normals = new float[ numSides ][ 3 ];
	}
	
	public boolean containsPoint( float[ ] p )
	{
		return containsPoint( p[ 0 ] , p[ 1 ] , p[ 2 ] );
	}
	
	public boolean containsPoint( float x , float y , float z )
	{
		for( int side = 0 ; side < origins.length ; side++ )
		{
			if( subDot3( x , y , z , origins[ side ] , normals[ side ] ) < 0f )
			{
				return false;
			}
		}
		return true;
	}
	
	public boolean containsBox( float[ ] box )
	{
		for( int xd = 0 ; xd <= 3 ; xd += 3 )
		{
			float x = box[ xd ];
			for( int yd = 1 ; yd <= 4 ; yd += 3 )
			{
				float y = box[ yd ];
				for( int zd = 2 ; zd <= 5 ; zd += 3 )
				{
					float z = box[ zd ];
					if( !containsPoint( x , y , z ) )
					{
						return false;
					}
				}
			}
		}
		return true;
	}
	
	public boolean intersectsBox( float[ ] box )
	{
		for( int side = 0 ; side < origins.length ; side++ )
		{
			if( allPointsOutside( side , box ) )
			{
				return false;
			}
		}
		
		for( int dim = 0 ; dim < 3 ; dim++ )
		{
			boolean allOutside = true;
			for( float[ ] vertex : vertices )
			{
				if( vertex[ dim ] >= box[ dim ] )
				{
					allOutside = false;
					break;
				}
			}
			if( allOutside )
			{
				return false;
			}
			
			allOutside = true;
			for( float[ ] vertex : vertices )
			{
				if( vertex[ dim ] <= box[ dim + 3 ] )
				{
					allOutside = false;
					break;
				}
			}
			if( allOutside )
			{
				return false;
			}
		}
		
		return true;
	}
	
	private boolean allPointsOutside( int side , float[ ] box )
	{
		for( int xd = 0 ; xd <= 3 ; xd += 3 )
		{
			float x = box[ xd ];
			for( int yd = 1 ; yd <= 4 ; yd += 3 )
			{
				float y = box[ yd ];
				for( int zd = 2 ; zd <= 5 ; zd += 3 )
				{
					float z = box[ zd ];
					if( subDot3( x , y , z , origins[ side ] , normals[ side ] ) >= 0 )
					{
						return false;
					}
				}
			}
		}
		return true;
	}
}
