package org.andork.math3d;

public class InConeTester3f
{
	private final float[ ]	segmentDirection	= new float[ 3 ];
	private final float[ ]	w0					= new float[ 3 ];
	private final float[ ]	w					= new float[ 3 ];
	
	private final float[ ]	opposite			= new float[ 3 ];
	private final float[ ]	adjacent			= new float[ 3 ];
	
	public float			s;
	public float			t;
	public float			lateralDistance;
	
	private final float[ ]	edgeStart			= new float[ 3 ];
	private final float[ ]	edgeEnd				= new float[ 3 ];
	
	public boolean isPointInCone( float[ ] point , float[ ] coneApex , float[ ] coneDirection , float coneAngle )
	{
		opposite[ 0 ] = point[ 0 ] - coneApex[ 0 ];
		opposite[ 1 ] = point[ 1 ] - coneApex[ 1 ];
		opposite[ 2 ] = point[ 2 ] - coneApex[ 2 ];
		
		if( Vecmath.dot3( opposite , coneDirection ) < 0f )
		{
			return false;
		}
		Vecmath.vvproj3( opposite , coneDirection , adjacent );
		opposite[ 0 ] -= adjacent[ 0 ];
		opposite[ 1 ] -= adjacent[ 1 ];
		opposite[ 2 ] -= adjacent[ 2 ];
		
		lateralDistance = Vecmath.length3( opposite );
		t = Vecmath.length3( adjacent );
		return lateralDistance <= coneAngle * t / 2f;
	}
	
	public static void main( String[ ] args )
	{
		System.out.println( new InConeTester3f( ).isLineSegmentInCone(
				new float[ ] { -100 , .1f , 5 } , new float[ ] { 100 , .1f , 5 } ,
				new float[ ] { 0 , 0 , 0 } , new float[ ] { 0 , 0 , 1 } , ( float ) Math.PI / 3 ) );
	}
	
	public boolean isLineSegmentInCone( float[ ] segmentStart , float[ ] segmentEnd , float[ ] coneApex , float[ ] coneDirection , float coneAngle )
	{
		boolean startIn = isPointInCone( segmentStart , coneApex , coneDirection , coneAngle );
		float startT = t;
		float startLateralDistance = lateralDistance;
		boolean endIn = isPointInCone( segmentEnd , coneApex , coneDirection , coneAngle );
		float endT = t;
		float endLateralDistance = lateralDistance;
		
		if( startIn && ( !endIn || startT < endT ) )
		{
			s = 0f;
			t = startT;
			lateralDistance = startLateralDistance;
			return true;
		}
		else if( endIn )
		{
			s = 1f;
			t = endT;
			lateralDistance = endLateralDistance;
			return true;
		}
		
		Vecmath.sub3( segmentEnd , segmentStart , segmentDirection );
		Vecmath.sub3( segmentStart , coneApex , w0 );
		
		float a = Vecmath.dot3( segmentDirection , segmentDirection );
		float b = Vecmath.dot3( segmentDirection , coneDirection );
		float c = Vecmath.dot3( coneDirection , coneDirection );
		float d = Vecmath.dot3( segmentDirection , w0 );
		float e = Vecmath.dot3( coneDirection , w0 );
		
		float denom = a * c - b * b;
		
		if( denom <= 0 )
		{
			return false;
		}
		
		s = ( b * e - c * d ) / denom;
		t = ( a * e - b * d ) / denom;
		
		if( s < 0 || s > 1 || t < 0 )
		{
			return false;
		}
		
		float radius = t * coneAngle / 2f;
		w[ 0 ] = w0[ 0 ] + s * segmentDirection[ 0 ] - t * coneDirection[ 0 ];
		w[ 1 ] = w0[ 1 ] + s * segmentDirection[ 1 ] - t * coneDirection[ 1 ];
		w[ 2 ] = w0[ 2 ] + s * segmentDirection[ 2 ] - t * coneDirection[ 2 ];
		
		lateralDistance = Vecmath.length3( w );
		
		return lateralDistance < radius;
	}
	
	public boolean boxIntersectsCone( float[ ] box , float[ ] coneApex , float[ ] coneDirection , float coneAngle )
	{
		if( RayIntersectsBoxTester.rayIntersects( coneApex , coneDirection , box ) )
		{
			return true;
		}
		
		for( int d0 = 0 ; d0 < 3 ; d0++ )
		{
			if( coneApex[ d0 ] < box[ d0 ] )
			{
				edgeStart[ d0 ] = edgeEnd[ d0 ] = box[ d0 ];
			}
			else if( coneApex[ d0 ] > box[ d0 + 3 ] )
			{
				edgeStart[ d0 ] = edgeEnd[ d0 ] = box[ d0 + 3 ];
			}
			else
			{
				continue;
			}
			
			for( int i = 1 ; i < 3 ; i++ )
			{
				int d1 = ( d0 + i ) % 3;
				int d2 = ( d1 + 1 ) % 3;
				
				edgeStart[ d2 ] = box[ d2 ];
				edgeEnd[ d2 ] = box[ d2 + 3 ];
				
				if( coneApex[ d1 ] >= box[ d1 ] )
				{
					edgeStart[ d1 ] = edgeEnd[ d1 ] = box[ d1 ];
					if( isLineSegmentInCone( edgeStart , edgeEnd , coneApex , coneDirection , coneAngle ) )
					{
						return true;
					}
				}
				if( coneApex[ d1 ] <= box[ d1 + 3 ] )
				{
					edgeStart[ d1 ] = edgeEnd[ d1 ] = box[ d1 + 3 ];
					if( isLineSegmentInCone( edgeStart , edgeEnd , coneApex , coneDirection , coneAngle ) )
					{
						return true;
					}
				}
			}
		}
		
		return false;
	}
	
}
