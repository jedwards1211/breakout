package org.andork.math3d;

import static org.andork.math3d.Vecmath.*;

import org.andork.math3d.LinePlaneIntersection3f.ResultType;

public class InTriangularPrismTester3f
{
	final float[ ]								origin		= new float[ 3 ];
	final float[ ][ ]							rays		= new float[ 3 ][ 3 ];
	final LinePlaneIntersection3f				lpx			= new LinePlaneIntersection3f( );
	final float[ ]								pu			= new float[ 3 ];
	final float[ ]								pv			= new float[ 3 ];
	final LinePlaneIntersection3f.ResultType[]	resultTypes	= new LinePlaneIntersection3f.ResultType[ 3 ];
	
	public void setOrigin( float[ ] origin )
	{
		Vecmath.setf( this.origin , origin );
	}
	
	public void setUpRays( float[ ] dir1 , float[ ] dir2 , float[ ] dir3 )
	{
		setf( rays[ 0 ] , dir1 );
		setf( rays[ 1 ] , dir2 );
		setf( rays[ 2 ] , dir3 );
	}
	
	public boolean intersectsBox( float[ ] box )
	{
		if( origin[ 0 ] >= box[ 0 ] && origin[ 0 ] <= box[ 3 ] &&
				origin[ 1 ] >= box[ 1 ] && origin[ 1 ] <= box[ 4 ] &&
				origin[ 2 ] >= box[ 2 ] && origin[ 2 ] <= box[ 5 ] )
		{
			return true;
		}
		
		for( int d0 = 0 ; d0 < 3 ; d0++ )
		{
			if( origin[ d0 ] < box[ d0 ] )
			{
				int r;
				for( r = 0 ; r < 3 ; r++ )
				{
					if( rays[ r ][ d0 ] > 0 )
					{
						break;
					}
				}
				if( r == 3 )
				{
					return false;
				}
			}
			else if( origin[ d0 ] > box[ d0 + 3 ] )
			{
				int r;
				for( r = 0 ; r < 3 ; r++ )
				{
					if( rays[ r ][ d0 ] < 0 )
					{
						break;
					}
				}
				if( r == 3 )
				{
					return false;
				}
			}
		}
		
		for( int d0 = 0 ; d0 < 3 ; d0++ )
		{
			int d1 = ( d0 + 1 ) % 3;
			int d2 = ( d1 + 1 ) % 3;
			
			if( origin[ d0 ] < box[ d0 ] )
			{
				lpx.po[ d0 ] = box[ d0 ];
			}
			else if( origin[ d0 ] > box[ d0 + 3 ] )
			{
				lpx.po[ d0 ] = box[ d0 + 3 ];
			}
			else
			{
				continue;
			}
			
			lpx.po[ d1 ] = box[ d1 ];
			lpx.po[ d2 ] = box[ d2 ];
			
			lpx.pu[ d0 ] = lpx.pv[ d0 ] = 0;
			lpx.pu[ d1 ] = box[ d1 + 3 ] - box[ d1 ];
			lpx.pv[ d1 ] = 0;
			lpx.pu[ d2 ] = 0;
			lpx.pv[ d2 ] = box[ d2 + 3 ] - box[ d2 ];
			
			cross( lpx.pu , lpx.pv , lpx.pn );
			
			setf( lpx.lo , origin );
			
			for( int r = 0 ; r < 3 ; r++ )
			{
				setf( lpx.lt , rays[ r ] );
				lpx.findIntersection( );
				if( lpx.isInRhombus( ) || lpx.isInPlane( ) )
				{
					return true;
				}
				resultTypes[ r ] = lpx.resultType;
				pu[r] = lpx.u;
				pv[r] = lpx.v;
			}
			
			for( int r0 = 0 ; r0 < 3 ; r0++ )
			{
				int r1 = ( r0 + 1 ) % 3;
				if( resultTypes[ r0 ] == ResultType.POINT && resultTypes[ r1 ] == ResultType.POINT )
				{
					if( intersectsUnitSquare( pu[ r0 ] , pv[ r0 ] , pu[ r1 ] , pv[ r1 ] ) )
					{
						return true;
					}
				}
			}
		}
		
		return false;
	}
	
	public static void main( String[ ] args0 )
	{
		System.out.println( intersectsUnitSquare( .5f , .5f , .6f , .6f ) );
		System.out.println( intersectsUnitSquare( -.5f , .5f , -.4f , .5f ) );
		System.out.println( intersectsUnitSquare( 0 , 2 , 2 , 0 ) );
		System.out.println( intersectsUnitSquare( 0 , 2 , 2.1f , 0 ) );
		System.out.println( intersectsUnitSquare( -1 , 0 , 1 , 2 ) );
		System.out.println( intersectsUnitSquare( -1 , 0 , 1 , 2.1f ) );
		
		InTriangularPrismTester3f tester = new InTriangularPrismTester3f( );
		
		tester.setOrigin( new float[ ] { 0 , 0 , 2 } );

		tester.setUpRays(
				new float[ ] { -5 , 0 , -1 } ,
				new float[ ] { 5 , 0.2f , -1 } ,
				new float[ ] { 5 , -0.2f , -1 } );
		System.out.println( tester.intersectsBox( new float[ ] { -1 , -1 , -1 , 1 , 1 , 1 } ) );

		tester.setUpRays(
				new float[ ] { -5 , 0 , -1 } ,
				new float[ ] { -4 , 0.2f , -1 } ,
				new float[ ] { -2 , -0.2f , -1 } );
		System.out.println( tester.intersectsBox( new float[ ] { -1 , -1 , -1 , 1 , 1 , 1 } ) );
	}
	
	private static boolean intersectsUnitSquare( float x1 , float y1 , float x2 , float y2 )
	{
		if( ( x1 < 0 && x2 < 0 ) ||
				( y1 < 0 && y2 < 0 ) ||
				( x1 > 1 && x2 > 1 ) ||
				( y1 > 1 && y2 > 1 ) )
		{
			return false;
		}
		
		float m = ( y2 - y1 ) / ( x2 - x1 );
		float b = y1 - m * x1;
		
		int count = 0;
		if( b == 0 || 1 - b == 0 || -m - b == 0 || 1 - m - b == 0 )
		{
			return true;
		}
		if( -b > 0 )
		{
			count++ ;
		}
		if( 1 - b > 0 )
		{
			count++ ;
		}
		if( -m - b > 0 )
		{
			count++ ;
		}
		if( 1 - m - b > 0 )
		{
			count++ ;
		}
		return count != 0 && count != 4;
	}
}
