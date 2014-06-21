package org.andork.breakout;

import java.awt.Color;
import java.awt.Image;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.andork.swing.DoSwing;
import org.andork.swing.SplashFrame;

public class BreakoutMainLauncher
{
	public static void main( String[ ] args )
	{
		String[ ] versionPieces = System.getProperty( "java.version" ).split( "\\." );
		int v0 = Integer.valueOf( versionPieces[ 0 ] );
		int v1 = Integer.valueOf( versionPieces[ 1 ] );
		
		if( v0 == 1 && v1 < 6 )
		{
			JOptionPane.showMessageDialog( null , "<html>FisherRidgeForever requires Java version 1.6+ to run.<br>Please download and install Java 1.6 or a later version.</html>" , "Fisher Ridge Forever" , JOptionPane.ERROR_MESSAGE );
			System.exit( 1 );
		}
		
		new DoSwing( )
		{
			Image	image	= null;
			
			@Override
			public void run( )
			{
				try
				{
					UIManager.setLookAndFeel( UIManager.getSystemLookAndFeelClassName( ) );
				}
				catch( Exception e )
				{
					e.printStackTrace( );
				}
				
				final SplashFrame splash = new SplashFrame( );
				splash.setTitle( "Breakout" );
				
				final Thread loaderThread = new Thread( "MapsView loader" )
				{
					public void run( )
					{
						final BreakoutMainView view = new BreakoutMainView( );
						new DoSwing( )
						{
							@Override
							public void run( )
							{
								if( splash.isVisible( ) )
								{
									BreakoutMainFrame frame = new BreakoutMainFrame( view );
									frame.setTitle( "Breakout" );
									frame.setExtendedState( JFrame.MAXIMIZED_BOTH );
									frame.setVisible( true );
									splash.setVisible( false );
								}
							}
						};
					}
				};
				
				loaderThread.setDaemon( true );
				loaderThread.start( );
				
				try
				{
					image = ImageIO.read( getClass( ).getResource( "splash.png" ) );
				}
				catch( Exception e )
				{
					e.printStackTrace( );
				}
				
				splash.getImagePanel( ).setImage( image );
				splash.getStatusLabel( ).setForeground( Color.WHITE );
				splash.getStatusLabel( ).setText( "Initializing 3D View..." );
				splash.getProgressBar( ).setIndeterminate( true );
				
				splash.pack( );
				splash.setLocationRelativeTo( null );
				splash.setVisible( true );
			}
		};
	}
}
