package org.andork.math3d.curve;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.vecmath.Point3f;
import javax.vecmath.Point4f;

import org.andork.vecmath.VecmathUtils;

public class BSpline1f
{
	private int			degree;
	private float[ ]	knots;
	private float[ ]	controlPoints;
	private float[ ]	deBoorPoints;
	private BSpline1f	derivative;
	
	public BSpline1f( int degree , float[ ] knots , float[ ] controlPoints )
	{
		if( knots.length != controlPoints.length + degree + 1 )
		{
			throw new IllegalArgumentException( "knots.length (" + knots.length + ") does not equal controlPoints.length (" + controlPoints.length + ") + degree (" + degree + ") + 1" );
		}
		this.degree = degree;
		this.knots = knots;
		this.controlPoints = controlPoints;
		
		deBoorPoints = new float[ degree + 1 ];
	}
	
	public float eval( float param )
	{
		if( param < knots[ 0 ] || param > knots[ knots.length - 1 ] )
		{
			throw new IllegalArgumentException( "param (" + param + ") is out of range: [" + knots[ 0 ] + ", " + knots[ knots.length - 1 ] + "]" );
		}
		if( param == knots[ 0 ] )
		{
			return controlPoints[ 0 ];
		}
		else if( param == knots[ knots.length - 1 ] )
		{
			return controlPoints[ controlPoints.length - 1 ];
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
			deBoorPoints[ i ] = controlPoints[ index - multiplicity - i ];
		}
		
		for( int r = 0 ; r < insertionCount ; r++ )
		{
			for( int i = 0 ; i < degree - multiplicity - r ; i++ )
			{
				int ii = index - multiplicity - i;
				float a = ( param - knots[ ii ] ) / ( knots[ ii + degree - r ] - knots[ ii ] );
				deBoorPoints[ i ] = ( 1 - a ) * deBoorPoints[ i + 1 ] + a * deBoorPoints[ i ];
			}
		}
		
		return deBoorPoints[ 0 ];
	}
	
	public BSpline1f getDerivative( )
	{
		if( degree == 0 )
		{
			throw new IllegalArgumentException( "cannot compute derivative" );
		}
		if( derivative == null )
		{
			float[ ] derivKnots = new float[ knots.length - 2 ];
			System.arraycopy( knots , 1 , derivKnots , 0 , derivKnots.length );
			float[ ] derivControlPoints = new float[ controlPoints.length - 1 ];
			
			for( int i = 0 ; i < derivControlPoints.length ; i++ )
			{
				float factor = degree / ( knots[ i + degree + 1 ] - knots[ i + 1 ] );
				derivControlPoints[ i ] = factor * ( controlPoints[ i + 1 ] - controlPoints[ i ] );
			}
			
			derivative = new BSpline1f( degree - 1 , derivKnots , derivControlPoints );
		}
		return derivative;
	}
}
