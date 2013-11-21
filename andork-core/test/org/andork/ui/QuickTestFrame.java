package org.andork.ui;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.JFrame;

public class QuickTestFrame
{
	public static JFrame frame( Component content )
	{
		return frame( content.getClass( ).getSimpleName( ) , content );
	}
	
	public static JFrame frame( String title , Component content )
	{
		JFrame frame = new JFrame( title );
		frame.getContentPane( ).add( content , BorderLayout.CENTER );
		frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		frame.pack( );
		frame.setLocationRelativeTo( null );
		return frame;
	}
}
