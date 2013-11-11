package org.andork.frf;

import javax.swing.JOptionPane;

public class MainLauncher
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
		
		MainFrame.main( args );
	}
}
