package org.andork.math3d;

import static org.andork.math3d.Vecmath.*;
import static org.andork.math3d.RayIntersectsBoxTester.*;

public class InFrustumTester3f
{
	private final int			sides		= 4;
	public final float[ ]		origin		= new float[ 3 ];
	public final float[ ][ ]	rays		= new float[ sides ][ 3 ];
	private final float[ ][ ]	normals		= new float[ sides ][ 3 ];
	
	private final boolean[ ][ ]	boxInside	= new boolean[ sides ][ 8 ];
	
	private final float[ ]		p			= new float[ 3 ];
	private final float[ ]		p2			= new float[ 3 ];
	private final float[ ]		p3			= new float[ 3 ];
	
	public void setFrustum( float[ ] origin , float[ ] ... rays )
	{
		setf( this.origin , origin );
		for( int side = 0 ; side < sides ; side++ )
		{
			setf( this.rays[ side ] , rays[ side ] );
		}
		computeNormals( );
	}
	
	public void computeNormals( )
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
	
	public boolean intersectsBox( float[ ] box )
	{
		for( int side = 0 ; side < sides ; side++ )
		{
			if( rayIntersects( origin , rays[ side ] , box ) )
			{
				return true;
			}
		}
		
		int k = 0;
		for( int x = 0 ; x <= 3 ; x += 3 )
		{
			p[ 0 ] = box[ x ];
			for( int y = 1 ; y <= 4 ; y += 3 )
			{
				p[ 1 ] = box[ y ];
				for( int z = 2 ; z <= 5 ; z += 3 )
				{
					p[ 2 ] = box[ z ];
					boolean totallyInside = true;
					for( int side = 0 ; side < sides ; side++ )
					{
						totallyInside &= boxInside[ side ][ k ] = subDot3( p , origin , normals[ side ] ) >= 0f;
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
		
		for( int d0 = 0 ; d0 < 3 ; d0++ )
		{
			int d1 = ( d0 + 1 ) % 3;
			int d2 = ( d0 + 2 ) % 3;
			
			if( origin[ d0 ] < box[ d0 ] )
			{
				p[ d0 ] = p2[ d0 ] = box[ d0 ];
				p3[ d0 ] = box[ d0 + 3 ];
			}
			else
			{
				p[ d0 ] = p2[ d0 ] = box[ d0 + 3 ];
				p3[ d0 ] = box[ d0 ];
			}
			
			if( origin[ d1 ] > box[ d1 ] )
			{
				p[ d1 ] = p2[ d1 ] = box[ d1 ];
				p3[ d1 ] = box[ d1 + 3 ];
				p[ d2 ] = box[ d2 ];
				p2[ d2 ] = box[ d2 + 3 ];
				p3[ d2 ] = box[ d2 ];
				
				if( allRaysOutsideBoxSilhouetteEdge( ) )
				{
					return false;
				}
			}
			if( origin[ d1 ] < box[ d1 + 3 ] )
			{
				p[ d1 ] = p2[ d1 ] = box[ d1 + 3 ];
				p3[ d1 ] = box[ d1 ];
				p[ d2 ] = box[ d2 ];
				p2[ d2 ] = box[ d2 + 3 ];
				p3[ d2 ] = box[ d2 ];
				
				if( allRaysOutsideBoxSilhouetteEdge( ) )
				{
					return false;
				}
			}
		}
		
		return true;
	}
	
	private boolean allRaysOutsideBoxSilhouetteEdge( )
	{
		sub3( p , origin , p );
		sub3( p2 , origin , p2 );
		sub3( p3 , origin , p3 );
		cross( p , p2 , p );
		if( dot3( p3 , p ) < 0 )
		{
			negate3( p );
		}
		
		for( int side = 0 ; side < sides ; side++ )
		{
			if( dot3( rays[ side ] , p ) > 0 )
			{
				return false;
			}
		}
		return true;
	}
}
