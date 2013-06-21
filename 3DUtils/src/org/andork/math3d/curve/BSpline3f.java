package org.andork.math3d.curve;

import java.util.Arrays;

import javax.vecmath.Point3f;

import org.andork.math3d.curve.BSpline1f.Temporaries;
import org.andork.vecmath.VecmathUtils;

public class BSpline3f
{
	private int			degree;
	private float[ ]	knots;
	private Point3f[ ]	controlPoints;
	private BSpline3f	derivative;
	
	public BSpline3f( int degree , float[ ] knots , Point3f[ ] controlPoints )
	{
		if( knots.length != controlPoints.length + degree + 1 )
		{
			throw new IllegalArgumentException( "knots.length (" + knots.length + ") does not equal controlPoints.length (" + controlPoints.length + ") + degree (" + degree + ") + 1" );
		}
		this.degree = degree;
		this.knots = knots;
		this.controlPoints = controlPoints;
	}
	
	public Point3f eval2( float param , Temporaries temp , boolean deBoorPrecomputed , Point3f result )
	{
		if( param == knots[ 0 ] )
		{
			result.set( controlPoints[ 0 ] );
			return result;
		}
		else if( param == knots[ knots.length - 1 ] )
		{
			result.set( controlPoints[ controlPoints.length - 1 ] );
			return result;
		}
		
		if( !deBoorPrecomputed )
		{
			temp.deBoorTemps.setUp( degree , knots , param );
		}
		
		int multiplicity = temp.deBoorTemps.multiplicity;
		
		for( int i = 0 ; i <= degree - multiplicity ; i++ )
		{
			temp.deBoorPoints[ i ].set( controlPoints[ temp.deBoorTemps.index - multiplicity - i ] );
		}
		
		for( int r = 0 ; r < degree - multiplicity ; r++ )
		{
			for( int i = 0 ; i < degree - multiplicity - r ; i++ )
			{
				float a = temp.deBoorTemps.weights[ r ][ i ];
				temp.deBoorPoints[ i ].interpolate( temp.deBoorPoints[ i + 1 ] , temp.deBoorPoints[ i ] , a );
			}
		}
		
		result.set( temp.deBoorPoints[ 0 ] );
		return result;
	}
	
	public Point3f eval( float param , Temporaries temp , Point3f result )
	{
		if( param < knots[ 0 ] || param > knots[ knots.length - 1 ] )
		{
			throw new IllegalArgumentException( "param (" + param + ") is out of range: [" + knots[ 0 ] + ", " + knots[ knots.length - 1 ] + "]" );
		}
		if( param == knots[ 0 ] )
		{
			result.set( controlPoints[ 0 ] );
			return result;
		}
		else if( param == knots[ knots.length - 1 ] )
		{
			result.set( controlPoints[ controlPoints.length - 1 ] );
			return result;
		}
		
		int index = Arrays.binarySearch( knots , param );
		
		int multiplicity = 0;
		
		if( index < 0 )
		{
			index = -( index + 1 );
		}
		
		while( index < knots.length - 1 && knots[ index + 1 ] == param )
		{
			index++ ;
		}
		
		for( int i = index ; i >= 0 && param == knots[ i ] ; i-- )
		{
			multiplicity++ ;
		}
		
		if( index > 0 && knots[ index ] != param )
		{
			index-- ;
		}
		
		int insertionCount = degree - multiplicity;
		
		for( int i = 0 ; i <= degree - multiplicity ; i++ )
		{
			Point3f controlPoint = controlPoints[ index - multiplicity - i ];
			temp.deBoorPoints[ i ].set( controlPoint );
		}
		
		for( int r = 0 ; r < insertionCount ; r++ )
		{
			for( int i = 0 ; i < degree - multiplicity - r ; i++ )
			{
				int ii = index - multiplicity - i;
				float a = ( param - knots[ ii ] ) / ( knots[ ii + degree - r ] - knots[ ii ] );
				temp.deBoorPoints[ i ].interpolate( temp.deBoorPoints[ i + 1 ] , temp.deBoorPoints[ i ] , a );
			}
		}
		
		result.set( temp.deBoorPoints[ 0 ] );
		return result;
	}
	
	public BSpline3f getDerivative( )
	{
		if( degree == 0 )
		{
			throw new IllegalArgumentException( "cannot compute derivative" );
		}
		if( derivative == null )
		{
			float[ ] derivKnots = new float[ knots.length - 2 ];
			System.arraycopy( knots , 1 , derivKnots , 0 , derivKnots.length );
			Point3f[ ] derivControlPoints = VecmathUtils.allocPoint3fArray( controlPoints.length - 1 );
			
			for( int i = 0 ; i < derivControlPoints.length ; i++ )
			{
				float factor = degree / ( knots[ i + degree + 1 ] - knots[ i + 1 ] );
				derivControlPoints[ i ].sub( controlPoints[ i + 1 ] , controlPoints[ i ] );
				derivControlPoints[ i ].scale( factor );
			}
			
			derivative = new BSpline3f( degree - 1 , derivKnots , derivControlPoints );
		}
		return derivative;
	}
	
	public static class Temporaries
	{
		public Temporaries( int degree , DeBoorTemps dbTemps )
		{
			deBoorTemps = dbTemps;
			deBoorPoints = VecmathUtils.allocPoint3fArray( degree + 1 );
		}
		
		private final DeBoorTemps	deBoorTemps;
		private final Point3f[ ]	deBoorPoints;
	}
}
