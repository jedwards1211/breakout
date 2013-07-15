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
		
		final List<Float> derivs2X = new ArrayList<Float>( );
		final List<Float> derivs2Y = new ArrayList<Float>( );
		
		Evaluator evaluator = new Evaluator( curve.getDegree( ) );
		
		evaluator.nurbs( curve );
		
		long start = System.nanoTime( );
		
		for( float f = 0 ; f <= 1 ; f += 0.0001f )
		{
			evaluator.param( f ).eval( result ).evalDerivative( v );
			xPoints.add( ( int ) result.x );
			yPoints.add( ( int ) result.y );
			
			derivsX.add( v.x );
			derivsY.add( v.y );
			
			evaluator.eval2ndDerivative( v );
			derivs2X.add( v.x );
			derivs2Y.add( v.y );
			
		}
		
		long elapsed = System.nanoTime( ) - start;
		
		System.out.println("Calcs took " + (elapsed / 1e6) + " ms");
		
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
				
				for( int i = 10 ; i < xPoints.size( ) - 1 ; i += 10 )
				{
					g2.setColor( Color.red );
					g2.drawLine( xPoints.get( i ) , yPoints.get( i ) , ( int ) ( xPoints.get( i ) + derivs2X.get( i ) / 10 ) , ( int ) ( yPoints.get( i ) + derivs2Y.get( i ) / 10 ) );
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
	
	public static class Evaluator
	{
		public Evaluator( int maxDegree )
		{
			dbTemps0 = new DeBoorTemps( maxDegree );
			
			eval1 = new BSpline1f.Evaluator( maxDegree );
			eval3 = new BSpline3f.Evaluator( maxDegree );
		}
		
		private final BSpline1f.Evaluator	eval1;
		private final BSpline3f.Evaluator	eval3;
		
		private final DeBoorTemps			dbTemps0;
		
		private Nurbs3f						nurbs;
		private float						param;
		
		private final Point3f				A		= new Point3f( );
		private final Point3f				Ap		= new Point3f( );
		private final Point3f				App		= new Point3f( );
		
		private float						w;
		private float						wp;
		private float						wpp;
		
		private final Point3f				C		= new Point3f( );
		private final Point3f				Cp		= new Point3f( );
		private final Point3f				Cpp		= new Point3f( );
		
		private boolean						valid0	= false;
		private boolean						valid1	= false;
		private boolean						valid2	= false;
		
		private void checkValid0( )
		{
			if( !valid0 )
			{
				dbTemps0.initOrIterate( nurbs.degree , nurbs.knots , param );
				
				nurbs.createWeightedPointSplineIfNecessary( );
				
				eval3.eval( nurbs.weightedPointSpline , dbTemps0 , A );
				w = eval1.eval( nurbs.weightSpline , dbTemps0 );
				
				C.scale( 1 / w , A );
				
				valid0 = true;
			}
		}
		
		private void checkValid1( )
		{
			if( !valid1 )
			{
				checkValid0( );
				
				BSpline1f wpSpline = nurbs.weightSpline.getDerivative( );
				
				eval3.eval( nurbs.weightedPointSpline.getDerivative( ) , dbTemps0 , Ap );
				wp = eval1.eval( wpSpline , dbTemps0 );
				
				Cp.scaleAdd( -wp , C , Ap );
				Cp.scale( 1 / w );
				
				valid1 = true;
			}
		}
		
		private void checkValid2( )
		{
			if( !valid2 )
			{
				checkValid1( );
				
				BSpline1f wppSpline = nurbs.weightSpline.getDerivative( ).getDerivative( );
				
				eval3.eval( nurbs.weightedPointSpline.getDerivative( ).getDerivative( ) , dbTemps0 , App );
				wpp = eval1.eval( wppSpline , dbTemps0 );
				
				Cpp.scale( -wpp , C );
				Cpp.scaleAdd( -2 * wp , Cp , Cpp );
				Cpp.add( App );
				Cpp.scale( 1 / w );
				
				valid2 = true;
			}
		}
		
		public Evaluator nurbs( Nurbs3f nurbs )
		{
			this.nurbs = nurbs;
			valid0 = false;
			valid1 = false;
			valid2 = false;
			return this;
		}
		
		public Evaluator param( float param )
		{
			this.param = param;
			valid0 = false;
			valid1 = false;
			valid2 = false;
			return this;
		}
		
		public Evaluator eval( Point3f result )
		{
			checkValid0( );
			result.set( C );
			return this;
		}
		
		public Evaluator evalDerivative( Vector3f result )
		{
			checkValid1( );
			result.set( Cp );
			return this;
		}
		
		public Evaluator eval2ndDerivative( Vector3f result )
		{
			checkValid2( );
			result.set( Cpp );
			return this;
		}
	}
}
