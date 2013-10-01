package org.andork.curves;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.Arrays;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.vecmath.Point2f;

import org.andork.math.curve.BSplineGf;
import org.andork.math.curve.Point2fType;
import org.andork.math.curve.BSplineGf.PointType;

public class SmoothRandomWalk<T>
{
	public static void main( String[ ] args )
	{
		RandomPoint2fGenerator generator = new RandomPoint2fGenerator( 0 , 800 );
		Point2fType pointType = new Point2fType( );
		
		SmoothRandomWalk<Point2f> walk = new SmoothRandomWalk<Point2f>( 3 , 1 , pointType , generator );
		
		Point2f p = new Point2f( );
		
		final int npoints = 1000;
		final float[ ] param = new float[ npoints ];
		final int[ ] xpoints = new int[ npoints ];
		final int[ ] ypoints = new int[ npoints ];
		
		float step = 0.05f;
		
		for( int i = 0 ; i < npoints ; i++ )
		{
			walk.advance( step , p );
			System.out.println( p );
			if( i > 0 )
			{
				param[ i ] = param[ i - 1 ] + step;
			}
			xpoints[ i ] = ( int ) p.x;
			ypoints[ i ] = ( int ) p.y;
		}
		
		JPanel drawPanel = new JPanel( )
		{
			@Override
			protected void paintComponent( Graphics g )
			{
				Graphics2D g2 = ( Graphics2D ) g;
				g2.setColor( Color.BLACK );
				
				g2.drawPolygon( xpoints , ypoints , npoints );
			}
		};
		
		JFrame frame = new JFrame( );
		frame.getContentPane( ).add( drawPanel , BorderLayout.CENTER );
		frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		frame.setSize( 800 , 800 );
		frame.setLocationRelativeTo( null );
		frame.setVisible( true );
	}
	
	private float					interval;
	private float[ ]				knots;
	private T[ ]					controlPoints;
	private PointType<T>			pointType;
	private BSplineGf<T>			spline;
	private RandomPointGenerator<T>	generator;
	
	private float					offset	= 0;
	private BSplineGf.Evaluator<T>	evaluator;
	
	public SmoothRandomWalk( int degree , float interval , PointType<T> pointType , RandomPointGenerator<T> generator )
	{
		this.interval = interval;
		this.pointType = pointType;
		this.generator = generator;
		knots = createKnots( degree , interval );
		controlPoints = createInitialPoints( degree , pointType , generator );
		spline = new BSplineGf<T>( degree , knots , controlPoints , pointType );
		evaluator = new BSplineGf.Evaluator<>( degree , pointType );
	}
	
	private T[ ] createInitialPoints( int degree , PointType<T> pointType , RandomPointGenerator<T> generator )
	{
		T[ ] result = pointType.allocate( degree + 1 );
		for( int i = 0 ; i < result.length ; i++ )
		{
			generator.generateRandomPoint( result[ i ] );
		}
		return result;
	}
	
	private static float[ ] createKnots( int degree , float interval )
	{
		float[ ] result = new float[ ( degree + 1 ) * 2 ];
		for( int i = 0 ; i < result.length ; i++ )
		{
			result[ i ] = interval * ( i - degree );
		}
		
		return result;
	}
	
	public void advance( float amount , T result )
	{
		while( offset + amount > interval )
		{
			for( int i = 0 ; i < controlPoints.length - 1 ; i++ )
			{
				pointType.set( controlPoints[ i ] , controlPoints[ i + 1 ] );
			}
			generator.generateRandomPoint( controlPoints[ controlPoints.length - 1 ] );
			spline = new BSplineGf<T>( spline.getDegree( ) , knots , controlPoints , pointType );
			
			amount -= interval;
		}
		
		// amount may be negative at this point
		offset += amount;
		evaluator.eval( spline , offset , result );
	}
	
	public static interface RandomPointGenerator<T>
	{
		public void generateRandomPoint( T point );
	}
	
	public static class RandomPoint2fGenerator implements RandomPointGenerator<Point2f>
	{
		float	min , max;
		Random	random	= new Random( );
		
		public RandomPoint2fGenerator( float min , float max )
		{
			super( );
			this.min = min;
			this.max = max;
		}
		
		@Override
		public void generateRandomPoint( Point2f point )
		{
			point.x = ( float ) ( min + random.nextDouble( ) * ( max - min ) );
			point.y = ( float ) ( min + random.nextDouble( ) * ( max - min ) );
		}
	}
}
