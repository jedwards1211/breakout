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
import javax.vecmath.Vector3f;

import org.andork.vecmath.VecmathUtils;

public class Nurbs3f
{
	public static void main( String[ ] args )
	{
		float[ ] knots = { 0 , 0 , 0 , 0 , .5f , .55f , .95f , 1 , 1 , 1 , 1 };
		float[ ] weights = { 1 , 1 , 5 , 1 , 1 , 1 , 1 };
		final Point3f[ ] controlPoints = VecmathUtils.allocPoint3fArray( 7 );
		int k = 0;
		controlPoints[ k++ ].set( 0 , 0 , 0 );
		controlPoints[ k++ ].set( 0 , 100 , 0 );
		controlPoints[ k++ ].set( 100 , 100 , 0 );
		controlPoints[ k++ ].set( 200 , 100 , 0 );
		controlPoints[ k++ ].set( 200 , 150 , 0 );
		controlPoints[ k++ ].set( 150 , 300 , 0 );
		controlPoints[ k++ ].set( 100 , 400 , 0 );
		
		Nurbs3f curve = new Nurbs3f( 3 , knots , controlPoints , weights );
		
		Point3f result = new Point3f( );
		Vector3f v = new Vector3f();
		
		final List<Integer> xPoints = new ArrayList<Integer>( );
		final List<Integer> yPoints = new ArrayList<Integer>( );
		
		final List<Float> derivsX = new ArrayList<Float>( );
		final List<Float> derivsY = new ArrayList<Float>( );
		
		for( float f = 0 ; f <= 1 ; f += 0.01f )
		{
			curve.eval( f , result );
			xPoints.add( ( int ) result.x );
			yPoints.add( ( int ) result.y );
			
			curve.evalDerivative( f , v );
			derivsX.add( v.x );
			derivsY.add( v.y );
		}
		
		final int[ ] xPointsArray = new int[ xPoints.size( ) ];
		final int[ ] yPointsArray = new int[ yPoints.size( ) ];
		
		for( int i = 0 ; i < xPoints.size( ) ; i++ )
		{
			xPointsArray[ i ] = xPoints.get( i );
			yPointsArray[ i ] = yPoints.get( i );
		}
		
		final Polygon p = new Polygon( xPointsArray , yPointsArray , xPoints.size( ) );
		
		JPanel panel = new JPanel( )
		{
			protected void paintComponent( Graphics g )
			{
				Graphics2D g2 = ( Graphics2D ) g;
				
				for( int i = 1 ; i < xPoints.size( ) - 1 ; i++ )
				{
					g2.setColor( Color.black );
					g2.drawLine( xPoints.get( i ) , yPoints.get( i ) , xPoints.get( i + 1 ) , yPoints.get( i + 1 ) );
				}
				
				for( int i = 10 ; i < xPoints.size( ) - 1 ; i += 10 )
				{
					g2.setColor( Color.blue );
					g2.drawLine( xPoints.get( i ) , yPoints.get( i ) , ( int ) ( xPoints.get( i ) + derivsX.get( i ) / 10 ) , ( int ) ( yPoints.get( i ) + derivsY.get( i ) / 10 ) );
				}
				
				for( Point3f p : controlPoints )
				{
					int x = ( int ) p.x;
					int y = ( int ) p.y;
					g2.drawOval( x - 1 , y - 1 , 3 , 3 );
				}
			}
		};
		
		JFrame frame = new JFrame( );
		frame.getContentPane( ).add( panel , BorderLayout.CENTER );
		frame.setSize( 400 , 400 );
		frame.setLocationRelativeTo( null );
		frame.setVisible( true );
	}
	
	private int			degree;
	private float[ ]	knots;
	private float[ ]	weights;
	private Point3f[ ]	controlPoints;
	private Point4f[ ]	deBoorPoints;
	
	private Point3f		p1;
	private BSpline3f	weightedPointSpline;
	private BSpline1f	weightSpline;
	
	public Nurbs3f( int degree , float[ ] knots , Point3f[ ] controlPoints , float[ ] weights )
	{
		if( knots.length != controlPoints.length + degree + 1 )
		{
			throw new IllegalArgumentException( "knots.length (" + knots.length + ") does not equal controlPoints.length (" + controlPoints.length + ") + degree (" + degree + ") + 1" );
		}
		if( weights.length != controlPoints.length )
		{
			throw new IllegalArgumentException( "weights.length (" + weights.length + ") does not equal controlPoints.length (" + controlPoints.length + ")" );
		}
		this.degree = degree;
		this.knots = knots;
		this.controlPoints = controlPoints;
		this.weights = weights;
		
		deBoorPoints = VecmathUtils.allocPoint4fArray( degree + 1 );
	}
	
	public Point3f eval( float param , Point3f result )
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
			float weight = weights[ index - multiplicity - i ];
			deBoorPoints[ i ].set( controlPoint.x * weight , controlPoint.y * weight , controlPoint.z * weight , weight );
		}
		
		for( int r = 0 ; r < insertionCount ; r++ )
		{
			for( int i = 0 ; i < degree - multiplicity - r ; i++ )
			{
				int ii = index - multiplicity - i;
				float a = ( param - knots[ ii ] ) / ( knots[ ii + degree - r ] - knots[ ii ] );
				deBoorPoints[ i ].interpolate( deBoorPoints[ i + 1 ] , deBoorPoints[ i ] , a );
			}
		}
		
		Point4f p = deBoorPoints[ 0 ];
		result.x = p.x / p.w;
		result.y = p.y / p.w;
		result.z = p.z / p.w;
		return result;
	}
	
	public Vector3f evalDerivative( float param , Vector3f result )
	{
		if( weightedPointSpline == null )
		{
			Point3f[ ] weightedControlPoints = VecmathUtils.allocPoint3fArray( controlPoints.length );
			for( int i = 0 ; i < controlPoints.length ; i++ )
			{
				weightedControlPoints[ i ].scale( weights[ i ] , controlPoints[ i ] );
			}
			
			weightedPointSpline = new BSpline3f( degree , knots , weightedControlPoints );
			weightSpline = new BSpline1f( degree , knots , weights );
			p1 = new Point3f( );
		}
		weightedPointSpline.getDerivative( ).eval( param , p1 );
		result.set( p1 );
		eval( param , p1 );
		
		result.scaleAdd( -weightSpline.getDerivative( ).eval( param ) , p1 , result );
		result.scale( weightSpline.eval( param ) );
		return result;
	}
}
