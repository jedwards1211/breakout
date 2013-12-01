package org.andork.frf;

import java.io.File;
import java.io.IOException;

import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.andork.awt.DoSwing;
import org.apache.commons.io.FileUtils;

public class MainLauncher
{
	public static void main( String[ ] args )
	{
		new DoSwing( )
		{
			@Override
			public void run( )
			{
				try
				{
					UIManager.setLookAndFeel( UIManager.getSystemLookAndFeelClassName( ) );
				}
				catch( Exception e )
				{
					e.printStackTrace();
				}
			}
		};

		String[ ] versionPieces = System.getProperty( "java.version" ).split( "\\." );
		int v0 = Integer.valueOf( versionPieces[ 0 ] );
		int v1 = Integer.valueOf( versionPieces[ 1 ] );
		
		if( v0 == 1 && v1 < 6 )
		{
			JOptionPane.showMessageDialog( null , "<html>FisherRidgeForever requires Java version 1.6+ to run.<br>Please download and install Java 1.6 or a later version.</html>" , "Fisher Ridge Forever" , JOptionPane.ERROR_MESSAGE );
			System.exit( 1 );
		}
		
		if( new File( "update" ).exists( ) )
		{
			new Thread( "update folder deleter" )
			{
				@Override
				public void run( )
				{
					try
					{
						FileUtils.deleteDirectory( new File( "update" ) );
					}
					catch( IOException e )
					{
						e.printStackTrace( );
					}
				}
			}.start( );
		}
		
		MainFrame.main( args );
	}
}
