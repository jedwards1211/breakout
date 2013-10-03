package org.andork.math.curve;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.vecmath.Point2f;

import org.andork.math.curve.SmoothRandomWalk.RandomPoint2fGenerator;

public class SmoothRandomWalkTest
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
	
}
