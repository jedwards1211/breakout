/*******************************************************************************
 * Breakout Cave Survey Visualizer
 *
 * Copyright (C) 2014 James Edwards
 *
 * jedwards8 at fastmail dot fm
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *******************************************************************************/
package org.andork.frf.update;

import java.awt.BorderLayout;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EmptyBorder;

import org.apache.commons.io.FileUtils;

public class UpgradeInstaller
{
	public static void main( String[ ] args ) throws SecurityException , IOException, InterruptedException, InvocationTargetException
	{
		SwingUtilities.invokeAndWait( new Runnable( )
		{
			@Override
			public void run( )
			{
				try
				{
					UIManager.setLookAndFeel( UIManager.getSystemLookAndFeelClassName( ) );
				}
				catch( ClassNotFoundException e )
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				catch( InstantiationException e )
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				catch( IllegalAccessException e )
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				catch( UnsupportedLookAndFeelException e )
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} );
		
		Logger logger = Logger.getLogger( UpgradeInstaller.class.getName( ) );
		LogManager.getLogManager( ).readConfiguration( UpgradeInstaller.class.getResourceAsStream( "logging.properties" ) );
		
		for( int i = 0 ; i < args.length ; i++ )
		{
			logger.fine( "args[" + i + "]: " + args[ i ] );
		}
		
		if( args.length != 4 )
		{
			logger.warning( "args.length != 4" );
			return;
		}
		
		JDialog dialog = new JDialog( );
		dialog.setDefaultCloseOperation( JDialog.DO_NOTHING_ON_CLOSE );
		
		JLabel statusLabel = new JLabel( );
		JProgressBar progressBar = new JProgressBar( );
		progressBar.setIndeterminate( true );
		
		statusLabel.setBorder( new EmptyBorder( 10 , 10 , 10 , 10 ) );
		progressBar.setBorder( new EmptyBorder( 10 , 10 , 10 , 10 ) );
		
		dialog.getContentPane( ).setLayout( new BorderLayout( ) );
		dialog.getContentPane( ).add( statusLabel , BorderLayout.NORTH );
		dialog.getContentPane( ).add( progressBar , BorderLayout.CENTER );
		
		dialog.pack( );
		dialog.setLocationRelativeTo( null );
		dialog.setVisible( true );
		
		File waitFile = new File( args[ 0 ] );
		File installDir = new File( args[ 1 ] );
		File upgradeDir = new File( args[ 2 ] );
		String command = args[ 3 ];
		
		if( !installDir.exists( ) || !installDir.isDirectory( )
				|| !upgradeDir.exists( ) || !upgradeDir.isDirectory( ) )
		{
			return;
		}
		
		long startTime = System.currentTimeMillis( );
		
		while( waitFile.exists( ) )
		{
			logger.fine( "waitFile exists; sleeping for 100 ms" );
			try
			{
				Thread.sleep( 100 );
			}
			catch( InterruptedException e )
			{
				e.printStackTrace( );
			}
			
			if( System.currentTimeMillis( ) - startTime >= 10000 )
			{
				logger.fine( "timed out waiting for waitFile to be deleted" );
				System.exit( 1 );
			}
		}
		
		File backupDir = installDir;
		while( backupDir.exists( ) )
		{
			backupDir = new File( "_" + backupDir.getName( ) );
		}
		
		boolean success = false;
		
		try
		{
			statusLabel.setText( "Backing up existing installation..." );
			logger.info( "Copying " + installDir + " to " + backupDir );
			FileUtils.copyDirectory( installDir , backupDir );
			
			statusLabel.setText( "Installing upgrade..." );
			logger.info( "Deleting " + installDir );
			FileUtils.deleteDirectory( installDir );
			
			logger.info( "Copying " + upgradeDir + " to " + installDir );
			FileUtils.copyDirectory( upgradeDir , installDir );
			
			statusLabel.setText( "Deleting backup..." );
			logger.info( "Deleting " + backupDir );
			FileUtils.deleteDirectory( backupDir );
			
			success = true;
		}
		catch( IOException ex )
		{
			logger.log( Level.SEVERE , "Failed to finish upgrade" , ex );
			ex.printStackTrace( );
		}
		
		if( success )
		{
			try
			{
				logger.fine( "Executing " + command );
				Runtime.getRuntime( ).exec( command );
				logger.fine( "Exiting with code 0" );
				System.exit( 0 );
			}
			catch( IOException e )
			{
				logger.log( Level.SEVERE , "Failed to execute command" , e );
				e.printStackTrace( );
				logger.fine( "Exiting with code 1" );
				System.exit( 1 );
			}
		}
		else
		{
			try
			{
				logger.info( "Deleting " + installDir );
				FileUtils.deleteDirectory( installDir );
				logger.info( "Copying " + backupDir + " to " + installDir );
				FileUtils.copyDirectory( backupDir , installDir );
			}
			catch( IOException ex )
			{
				logger.log( Level.SEVERE , "Failed to revert failed upgrade" , ex );
				ex.printStackTrace( );
			}
			logger.fine( "Exiting with code 2" );
			System.exit( 2 );
		}
	}
}
