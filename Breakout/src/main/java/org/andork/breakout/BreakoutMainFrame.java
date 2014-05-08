package org.andork.breakout;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JFrame;

@SuppressWarnings( "serial" )
public class BreakoutMainFrame extends JFrame
{
	public BreakoutMainFrame( BreakoutMainView breakoutMainView )
	{
		super( "Breakout" );
		getContentPane( ).add( breakoutMainView.getMainPanel( ) , BorderLayout.CENTER );
		Dimension screenSize = Toolkit.getDefaultToolkit( ).getScreenSize( );
		setSize( screenSize.width * 2 / 3 , screenSize.height * 2 / 3 );
		setDefaultCloseOperation( EXIT_ON_CLOSE );
		setLocationRelativeTo( null );
	}
}
