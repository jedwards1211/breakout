package org.andork.math3d;

import static org.andork.math3d.Vecmath.*;
import static org.andork.math3d.RayIntersectsBoxTester.*;

/**
 * Represents a planar hull and provides methods to test if it contains or intersects bounding boxes and points.
 * 
 * @author Andy
 */
public class PlanarHull3f
{
	public final int			sides;
	public final float[ ][ ]	origins;
	public final float[ ][ ]	rays;
	public final float[ ][ ]	normals;
	
	private final boolean[ ][ ]	boxInside;
	
	private final float[ ]		p1	= new float[ 3 ];
	private final float[ ]		p2	= new float[ 3 ];
	private final float[ ]		p3	= new float[ 3 ];
	
	public PlanarHull3f( )
	{
		this( 6 );
	}
	
	public PlanarHull3f( int sides )
	{
		this.sides = sides;
		
		origins = new float[ sides ][ 3 ];
		rays = new float[ sides ][ 3 ];
		normals = new float[ sides ][ 3 ];
		
		boxInside = new boolean[ sides ][ 8 ];
	}
	
	public void computeNormalsForOrtho( )
	{
		for( int side = 0 ; side < sides ; side++ )
		{
			int side2 = ( side + 1 ) % sides;
			subCross( origins[ side2 ] , origins[ side ] , rays[ side ] , normals[ side ] );
		}
		if( subDot3( origins[ 2 ] , origins[ 0 ] , normals[ 0 ] ) < 0 )
		{
			for( int side = 0 ; side < sides ; side++ )
			{
				negate3( normals[ side ] );
			}
		}
	}
	
	public void computeNormalsForPerspective( )
	{
		for( int side = 0 ; side < sides ; side++ )
		{
			cross( rays[ side ] , rays[ ( side + 1 ) % sides ] , normals[ side ] );
		}
		if( dot3( rays[ 2 ] , normals[ 0 ] ) < 0f )
		{
			for( int side = 0 ; side < sides ; side++ )
			{
				negate3( normals[ side ] );
			}
		}
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
	
	public boolean containsPoint( float[ ] p )
	{
		return containsPoint( p[ 0 ] , p[ 1 ] , p[ 2 ] );
	}
	
	public boolean containsPoint( float x , float y , float z )
	{
		for( int side = 0 ; side < sides ; side++ )
		{
			if( subDot3( x , y , z , origins[ side ] , normals[ side ] ) < 0f )
			{
				return false;
			}
		}
		return true;
	}
	
	public boolean intersectsBox( float[ ] box )
	{
		for( int side = 0 ; side < sides ; side++ )
		{
			if( rayIntersects( origins[ side ] , rays[ side ] , box ) )
			{
				return true;
			}
		}
		
		int k = 0;
		for( int x = 0 ; x <= 3 ; x += 3 )
		{
			p1[ 0 ] = box[ x ];
			for( int y = 1 ; y <= 4 ; y += 3 )
			{
				p1[ 1 ] = box[ y ];
				for( int z = 2 ; z <= 5 ; z += 3 )
				{
					p1[ 2 ] = box[ z ];
					boolean totallyInside = true;
					for( int side = 0 ; side < sides ; side++ )
					{
						totallyInside &= boxInside[ side ][ k ] = subDot3( p1 , origins[ side ] , normals[ side ] ) >= 0f;
					}
					if( totallyInside )
					{
						return true;
					}
					k++ ;
				}
			}
		}
		
		// if all the box vertices are outside of one of the frustum planes, no intersection
		
		sideLoop: for( int side = 0 ; side < sides ; side++ )
		{
			for( k = 0 ; k < 8 ; k++ )
			{
				if( boxInside[ side ][ k ] )
				{
					continue sideLoop;
				}
			}
			return false;
		}
		
		// if all the frustum rays are outside one of the box silhouette edge planes, no intersection
		
		for( int side = 0 ; side < sides ; side++ )
		{
			for( int d0 = 0 ; d0 < 3 ; d0++ )
			{
				int d1 = ( d0 + 1 ) % 3;
				int d2 = ( d0 + 2 ) % 3;
				
				if( origins[ side ][ d0 ] < box[ d0 ] )
				{
					p1[ d0 ] = p2[ d0 ] = box[ d0 ];
					p3[ d0 ] = box[ d0 + 3 ];
				}
				else if( origins[ side ][ d0 ] > box[ d0 + 3 ] )
				{
					p1[ d0 ] = p2[ d0 ] = box[ d0 + 3 ];
					p3[ d0 ] = box[ d0 ];
				}
				else
				{
					continue;
				}
				
				if( origins[ side ][ d1 ] > box[ d1 ] )
				{
					p1[ d1 ] = p2[ d1 ] = box[ d1 ];
					p3[ d1 ] = box[ d1 + 3 ];
					p1[ d2 ] = box[ d2 ];
					p2[ d2 ] = box[ d2 + 3 ];
					p3[ d2 ] = box[ d2 ];
					
					if( rayInsideBoxSilhouetteEdge( side ) )
					{
						return true;
					}
				}
				if( origins[ side ][ d1 ] < box[ d1 + 3 ] )
				{
					p1[ d1 ] = p2[ d1 ] = box[ d1 + 3 ];
					p3[ d1 ] = box[ d1 ];
					p1[ d2 ] = box[ d2 ];
					p2[ d2 ] = box[ d2 + 3 ];
					p3[ d2 ] = box[ d2 ];
					
					if( rayInsideBoxSilhouetteEdge( side ) )
					{
						return true;
					}
				}
			}
		}
		
		return false;
	}
	
	private boolean rayInsideBoxSilhouetteEdge( int side )
	{
		sub3( p1 , origins[ side ] , p1 );
		sub3( p2 , origins[ side ] , p2 );
		sub3( p3 , origins[ side ] , p3 );
		cross( p1 , p2 , p1 );
		
		return dot3( rays[ side ] , p1 ) > 0 == dot3( p3 , p1 ) > 0;
	}
}
