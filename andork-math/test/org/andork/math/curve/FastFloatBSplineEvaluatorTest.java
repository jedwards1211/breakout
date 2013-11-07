package org.andork.math.curve;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.vecmath.Point3f;

import org.andork.vecmath.Vecmath;

public class FastFloatBSplineEvaluatorTest extends JPanel
{
	FastFloatBSplineEvaluator	evaluator;
	
	float[ ]					points;
	int							pointsStride;
	float[ ]					knots;
	
	float[ ]					out;
	
	int							degree;
	int							dimension;
	
	int							mouseoverIndex	= -1;
	
	Path2D						path;
	
	MouseHandler				mouseHandler;
	
	float						startParam		= 0.3f;
	float						stepSize		= 0.01f;
	float						endParam		= 0.805f;
	
	public static void main( String[ ] args )
	{
		FastFloatBSplineEvaluatorTest panel = new FastFloatBSplineEvaluatorTest( );
		JFrame frame = new JFrame( );
		frame.getContentPane( ).add( panel );
		
		frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		frame.setSize( 400 , 400 );
		frame.setLocationRelativeTo( null );
		frame.setVisible( true );
	}
	
	public FastFloatBSplineEvaluatorTest( )
	{
		init( );
	}
	
	private void init( )
	{
		degree = 3;
		dimension = 3;
		pointsStride = 3;
		
		knots = new float[ ] { 0 , 0.1f , 0.2f , 0.3f , 0.4f , 0.5f , 0.6f , 0.7f , 0.8f , 0.9f , 1.0f , 1.1f };
		points = new float[ ] { 100 , 100 , 1 , 110 , 110 , 1 , 120 , 120 , 1 , 130 , 130 , 1 , 140 , 140 , 1 , 150 , 150 , 1 , 160 , 160 , 1 , 170 , 170 , 1 };
		
		out = new float[ dimension ];
		
		evaluator = new FastFloatBSplineEvaluator( ).points( points ).knots( knots ).dimension( dimension ).degree( degree ).pointsStride( pointsStride );
		
		rebuildPath( );
		
		mouseHandler = new MouseHandler( );
		addMouseListener( mouseHandler );
		addMouseMotionListener( mouseHandler );
	}
	
	private void rebuildPath( )
	{
		path = new Path2D.Float( );
		
		int i;
		for( i = 0 ; startParam + i * stepSize < endParam ; i++ )
		{
			float param = startParam + i * stepSize;
			evaluator.eval( param , out );
			
			if( i == 0 )
			{
				path.moveTo( out[ 0 ] , out[ 1 ] );
			}
			else
			{
				path.lineTo( out[ 0 ] , out[ 1 ] );
			}
		}
	}
	
	@Override
	protected void paintComponent( Graphics g )
	{
		super.paintComponent( g );
		
		Graphics2D g2 = ( Graphics2D ) g;
		
		Rectangle2D r = new Rectangle2D.Float( );
		Line2D l = new Line2D.Float( );
		
		for( int i = 0 ; i < points.length ; i += pointsStride )
		{
			if( i > 0 )
			{
				g2.setColor( Color.GRAY );
				l.setLine( points[ i - pointsStride ] , points[ i - pointsStride + 1 ] , points[ i ] , points[ i + 1 ] );
				g2.draw( l );
			}
			
			g2.setColor( Color.BLACK );
			int radius = i == mouseoverIndex ? 3 : 1;
			r.setFrame( points[ i ] - radius , points[ i + 1 ] - radius , radius * 2 + 1 , radius * 2 + 1 );
			g2.draw( r );
		}
		
		if( path != null )
		{
			g2.setColor( Color.BLACK );
			g2.draw( path );
		}
	}
	
	private static int findMouseoverPoint( float[ ] points , int pointsStride , float x , float y , double maxDistSq )
	{
		int result = -1;
		double resultDistSq = 0;
		
		for( int i = 0 ; i < points.length ; i += pointsStride )
		{
			double dx = x - points[ i ];
			double dy = y - points[ i + 1 ];
			double distSq = dx * dx + dy * dy;
			
			if( distSq < maxDistSq && ( result < 0 || distSq < resultDistSq ) )
			{
				result = i;
				resultDistSq = distSq;
			}
		}
		
		return result;
	}
	
	private class MouseHandler extends MouseAdapter
	{
		double		maxDistSq			= 100;
		int			lastMouseoverIndex	= -1;
		int			pressedIndex		= -1;
		
		MouseEvent	pressEvent			= null;
		float[ ]	origLocation		= new float[ dimension ];
		
		@Override
		public void mousePressed( MouseEvent e )
		{
			mouseMoved( e );
			
			if( e.getModifiersEx( ) == MouseEvent.BUTTON1_DOWN_MASK && mouseoverIndex >= 0 )
			{
				pressEvent = e;
				pressedIndex = mouseoverIndex;
				System.arraycopy( points , pressedIndex , origLocation , 0 , dimension );
			}
		}
		
		@Override
		public void mouseReleased( MouseEvent e )
		{
			if( pressEvent != null && e.getButton( ) == pressEvent.getButton( ) )
			{
				pressedIndex = -1;
				pressEvent = null;
			}
		}
		
		@Override
		public void mouseDragged( MouseEvent e )
		{
			if( pressedIndex >= 0 )
			{
				points[ pressedIndex ] = origLocation[ 0 ] + e.getX( ) - pressEvent.getX( );
				points[ pressedIndex + 1 ] = origLocation[ 1 ] + e.getY( ) - pressEvent.getY( );
				
				rebuildPath( );
				repaint( );
			}
		}
		
		@Override
		public void mouseMoved( MouseEvent e )
		{
			mouseoverIndex = findMouseoverPoint( points , pointsStride , e.getX( ) , e.getY( ) , maxDistSq );
			
			if( mouseoverIndex != lastMouseoverIndex )
			{
				lastMouseoverIndex = mouseoverIndex;
				repaint( );
			}
		}
	}
}
