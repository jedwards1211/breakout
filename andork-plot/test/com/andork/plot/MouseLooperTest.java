package com.andork.plot;

import javax.swing.JFrame;

public class MouseLooperTest
{
	public static void main( String[ ] args ) throws Exception
	{
		MouseLooper looper = new MouseLooper( );
		
		JFrame frame = new JFrame( );
		frame.getContentPane( ).addMouseMotionListener( looper );
		
		frame.setSize( 400 , 400 );
		frame.setLocationRelativeTo( null );
		frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		frame.setVisible( true );
	}
}
