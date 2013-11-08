package org.andork.frf;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JFrame;

@SuppressWarnings( "serial" )
public class FisherRidgeForever extends JFrame
{
	MainPane	mainPane;
	
	public FisherRidgeForever( )
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
		new FisherRidgeForever( ).setVisible( true );
	}
}
