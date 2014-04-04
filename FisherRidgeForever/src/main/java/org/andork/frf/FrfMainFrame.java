package org.andork.frf;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Toolkit;

import javax.swing.JFrame;

import org.andork.swing.DoSwing;

@SuppressWarnings( "serial" )
public class FrfMainFrame extends JFrame
{
	FrfMainPane	frfMainPane;
	
	public FrfMainFrame( )
	{
		super( "Fisher Ridge Forever" );
		frfMainPane = new FrfMainPane( );
		getContentPane( ).add( frfMainPane , BorderLayout.CENTER );
		Dimension screenSize = Toolkit.getDefaultToolkit( ).getScreenSize( );
		setSize( screenSize.width * 2 / 3 , screenSize.height * 2 / 3 );
		setDefaultCloseOperation( EXIT_ON_CLOSE );
		setLocationRelativeTo( null );
	}
	
	public static void main( String[ ] args )
	{
		new DoSwing( )
		{
			@Override
			public void run( )
			{
				FrfMainFrame frame = new FrfMainFrame( );
				frame.setExtendedState( JFrame.MAXIMIZED_BOTH );
				frame.setVisible( true );
			}
		};
	}
}
