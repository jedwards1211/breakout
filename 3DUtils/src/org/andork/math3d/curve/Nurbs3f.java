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
		float[ ] weights = { 1 , 1 , 1 , .1f , 1 , 1 , 1 };
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
		Vector3f v = new Vector3f( );
		
		final List<Integer> xPoints = new ArrayList<Integer>( );
		final List<Integer> yPoints = new ArrayList<Integer>( );
		
		final List<Float> derivsX = new ArrayList<Float>( );
		final List<Float> derivsY = new ArrayList<Float>( );
		
		Temporaries temp = new Temporaries( curve.getDegree( ) , new DeBoorTemps( curve.getDegree( ) ) );
		
		for( float f = 0 ; f <= 1 ; f += 0.01f )
		{
			curve.eval2( f , temp , result );
			xPoints.add( ( int ) result.x );
			yPoints.add( ( int ) result.y );
			
			curve.evalDerivative2( f , temp , v );
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
	}
	
	public int getDegree( )
	{
		return degree;
	}
	
	public Point3f eval2( float param , Temporaries temp , Point3f result )
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
		
		createWeightedPointSplineIfNecessary( );
		
		temp.deBoorTemps.setUp( degree , knots , param );
		
		weightedPointSpline.eval2( param , temp.temp3 , true , result );
		result.scale( 1 / weightSpline.eval2( param , temp.temp1 , true ) );
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
			float weight = weights[ index - multiplicity - i ];
			temp.deBoorPoints[ i ].set( controlPoint.x * weight , controlPoint.y * weight , controlPoint.z * weight , weight );
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
		
		Point4f p = temp.deBoorPoints[ 0 ];
		result.x = p.x / p.w;
		result.y = p.y / p.w;
		result.z = p.z / p.w;
		return result;
	}
	
	private void createWeightedPointSplineIfNecessary( )
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
		}
	}
	
	public Vector3f evalDerivative2( float param , Temporaries temp , Vector3f result )
	{
		createWeightedPointSplineIfNecessary( );
		weightedPointSpline.eval2( param , temp.temp3 , false , temp.A );
		float w = weightSpline.eval2( param , temp.temp1 , true );
		
		weightedPointSpline.getDerivative( ).eval2( param , temp.temp3 , false , temp.Ap );
		float wp = weightSpline.getDerivative( ).eval2( param , temp.temp1 , true );
		
		temp.C.scale( 1 / w , temp.A );
		result.scaleAdd( -wp , temp.C , temp.Ap );
		result.scale( 1 / w );
		return result;
	}
	
	public Vector3f evalDerivative( float param , Temporaries temp , Vector3f result )
	{
		createWeightedPointSplineIfNecessary( );
		weightedPointSpline.getDerivative( ).eval( param , temp.temp3 , temp.Ap );
		eval( param , temp , temp.C );
		
		result.scaleAdd( -weightSpline.getDerivative( ).eval( param , temp.temp1 ) , temp.C , temp.Ap );
		result.scale( 1f / weightSpline.eval( param , temp.temp1 ) );
		return result;
	}
	
	public Vector3f evalSecondDerivative( float param , Temporaries temp , Vector3f result )
	{
		createWeightedPointSplineIfNecessary( );
		weightedPointSpline.eval2( param , temp.temp3 , false , temp.A );
		float w = weightSpline.eval2( param , temp.temp1 , true );
		
		weightedPointSpline.getDerivative( ).eval2( param , temp.temp3 , false , temp.Ap );
		float wp = weightSpline.getDerivative( ).eval2( param , temp.temp1 , true );
		
		weightedPointSpline.getDerivative( ).getDerivative( ).eval2( param , temp.temp3 , false , temp.App );
		float wpp = weightSpline.getDerivative( ).getDerivative( ).eval2( param , temp.temp1 , true );
		
		temp.C.scale( 1 / w , temp.A );
		result.scaleAdd( -wp , temp.C , temp.Ap );
		result.scale( -2 * wp / w );
		result.scaleAdd( -wpp , temp.C , result );
		result.add( temp.App );
		result.scale( 1 / w );
		return result;
	}
	
	public static class Temporaries
	{
		public Temporaries( int degree , DeBoorTemps dbTemps )
		{
			deBoorTemps = dbTemps;
			deBoorPoints = VecmathUtils.allocPoint4fArray( degree + 1 );
			temp1 = new BSpline1f.Temporaries( degree , dbTemps );
			temp3 = new BSpline3f.Temporaries( degree , dbTemps );
		}
		
		private final DeBoorTemps			deBoorTemps;
		private final BSpline1f.Temporaries	temp1;
		private final BSpline3f.Temporaries	temp3;
		
		private final Point4f[ ]			deBoorPoints;
		private final Point3f				C	= new Point3f( );
		private final Point3f				A	= new Point3f( );
		private final Point3f				Ap	= new Point3f( );
		private final Point3f				App	= new Point3f( );
	}
}
