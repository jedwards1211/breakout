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
	int			degree;
	float[ ]	knots;
	float[ ]	controlPoints;
	BSpline1f	derivative;
	
	public BSpline1f( int degree , float[ ] knots , float[ ] controlPoints )
	{
		if( knots.length != controlPoints.length + degree + 1 )
		{
			throw new IllegalArgumentException( "knots.length (" + knots.length + ") does not equal controlPoints.length (" + controlPoints.length + ") + degree (" + degree + ") + 1" );
		}
		this.degree = degree;
		this.knots = knots;
		this.controlPoints = controlPoints;
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
	
	public static class Evaluator
	{
		public Evaluator( int degree )
		{
			deBoorPoints = new float[ degree + 1 ];
		}
		
		public float eval( BSpline1f s , DeBoorTemps t )
		{
			if( t.param == s.knots[ 0 ] )
			{
				return s.controlPoints[ 0 ];
			}
			else if( t.param == s.knots[ s.knots.length - 1 ] )
			{
				return s.controlPoints[ s.controlPoints.length - 1 ];
			}
			
			for( int i = 0 ; i <= s.degree - t.multiplicity ; i++ )
			{
				deBoorPoints[ i ] = s.controlPoints[ t.index - t.multiplicity - i ];
			}
			
			for( int r = 0 ; r < s.degree - t.multiplicity ; r++ )
			{
				for( int i = 0 ; i < s.degree - t.multiplicity - r ; i++ )
				{
					float a = t.weights[ r ][ i ];
					deBoorPoints[ i ] = ( 1 - a ) * deBoorPoints[ i + 1 ] + a * deBoorPoints[ i ];
				}
			}
			
			return deBoorPoints[ 0 ];
		}
		
		private final float[ ]	deBoorPoints;
	}
}
