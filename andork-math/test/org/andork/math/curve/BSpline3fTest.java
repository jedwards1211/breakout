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

import org.andork.math.curve.BSpline3f.Evaluator;

public class BSpline3fTest extends JPanel
{
	BSpline3f			spline;
	BSpline3f.Evaluator	evaluator;
	
	int					mouseoverIndex	= -1;
	
	Path2D				path;
	
	MouseHandler		mouseHandler;
	
	float				startParam		= 0.3f;
	float				stepSize		= 0.01f;
	float				endParam		= 0.805f;
	
	public static void main( String[ ] args )
	{
		BSpline3fTest panel = new BSpline3fTest( );
		JFrame frame = new JFrame( );
		frame.getContentPane( ).add( panel );
		
		frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		frame.setSize( 400 , 400 );
		frame.setLocationRelativeTo( null );
		frame.setVisible( true );
	}
	
	public BSpline3fTest( )
	{
		init( );
	}
	
	private void init( )
	{
		mouseHandler = new MouseHandler( );
		addMouseListener( mouseHandler );
		addMouseMotionListener( mouseHandler );
		
		// spline = new BSpline3f( 3 , new float[ ] { 0 , 0 , 0 , 0 , 0.2f , 0.4f , 0.6f , 0.8f , 1 , 1 , 1 , 1 } , new Point3f[ ] {
		spline = new BSpline3f( 3 , new float[ ] { 0 , 0.1f , 0.2f , 0.3f , 0.4f , 0.5f , 0.6f , 0.7f , 0.8f , 0.9f , 1.0f , 1.1f } , new Point3f[ ] {
				new Point3f( 100 , 100 , 1 ) ,
				new Point3f( 110 , 110 , 1 ) ,
				new Point3f( 120 , 120 , 1 ) ,
				new Point3f( 130 , 130 , 1 ) ,
				new Point3f( 140 , 140 , 1 ) ,
				new Point3f( 150 , 150 , 1 ) ,
				new Point3f( 160 , 160 , 1 ) ,
				new Point3f( 170 , 170 , 1 )
		} );
		
		evaluator = new Evaluator( 3 );
		
		rebuildPath( );
	}
	
	private void rebuildPath( )
	{
		path = new Path2D.Float( );
		
		Point3f result = new Point3f( );
		
		if( evaluator != null && spline != null )
		{
			int i;
			for( i = 0 ; startParam + i * stepSize < endParam ; i++ )
			{
				float param = startParam + i * stepSize;
				evaluator.eval( spline , param , result );
				
				if( i == 0 )
				{
					path.moveTo( result.x , result.y );
				}
				else
				{
					path.lineTo( result.x , result.y );
				}
			}
		}
	}
	
	@Override
	protected void paintComponent( Graphics g )
	{
		super.paintComponent( g );
		
		Graphics2D g2 = ( Graphics2D ) g;
		
		if( spline != null )
		{
			Rectangle2D r = new Rectangle2D.Float( );
			Line2D l = new Line2D.Float( );
			
			Point3f[ ] controlPoints = spline.controlPoints;
			
			for( int i = 0 ; i < controlPoints.length ; i++ )
			{
				if( i > 0 )
				{
					g2.setColor( Color.GRAY );
					l.setLine( controlPoints[ i - 1 ].x , controlPoints[ i - 1 ].y , controlPoints[ i ].x , controlPoints[ i ].y );
					g2.draw( l );
				}
				
				g2.setColor( Color.BLACK );
				int radius = i == mouseoverIndex ? 3 : 1;
				r.setFrame( controlPoints[ i ].x - radius , controlPoints[ i ].y - radius , radius * 2 + 1 , radius * 2 + 1 );
				g2.draw( r );
			}
		}
		
		if( path != null )
		{
			g2.setColor( Color.BLACK );
			g2.draw( path );
		}
	}
	
	private static int findMouseoverPoint( BSpline3f spline , float x , float y , double maxDistSq )
	{
		int result = -1;
		double resultDistSq = 0;
		
		if( spline != null )
		{
			for( int i = 0 ; i < spline.controlPoints.length ; i++ )
			{
				double dx = x - spline.controlPoints[ i ].x;
				double dy = y - spline.controlPoints[ i ].y;
				double distSq = dx * dx + dy * dy;
				
				if( distSq < maxDistSq && ( result < 0 || distSq < resultDistSq ) )
				{
					result = i;
					resultDistSq = distSq;
				}
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
		Point3f		origLocation		= new Point3f( );
		
		@Override
		public void mousePressed( MouseEvent e )
		{
			mouseMoved( e );
			
			if( e.getModifiersEx( ) == MouseEvent.BUTTON1_DOWN_MASK && mouseoverIndex >= 0 )
			{
				pressEvent = e;
				pressedIndex = mouseoverIndex;
				origLocation.set( spline.controlPoints[ pressedIndex ] );
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
				Point3f controlPoint = spline.controlPoints[ pressedIndex ];
				controlPoint.set( origLocation );
				controlPoint.x += e.getX( ) - pressEvent.getX( );
				controlPoint.y += e.getY( ) - pressEvent.getY( );
				
				rebuildPath( );
				repaint( );
			}
		}
		
		@Override
		public void mouseMoved( MouseEvent e )
		{
			mouseoverIndex = findMouseoverPoint( spline , e.getX( ) , e.getY( ) , maxDistSq );
			
			if( mouseoverIndex != lastMouseoverIndex )
			{
				lastMouseoverIndex = mouseoverIndex;
				repaint( );
			}
		}
	}
}
