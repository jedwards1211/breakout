package org.andork.frf;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Toolkit;

import javax.swing.JFrame;

import org.andork.swing.DoSwing;

@SuppressWarnings( "serial" )
public class MainFrame extends JFrame
{
	MainPane	mainPane;
	
	public MainFrame( )
	{
		super( "Fisher Ridge Forever" );
		mainPane = new MainPane( );
		getContentPane( ).add( mainPane , BorderLayout.CENTER );
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
				MainFrame frame = new MainFrame( );
				frame.setVisible( true );
				frame.setExtendedState( JFrame.MAXIMIZED_BOTH );
			}
		};
	}
}
