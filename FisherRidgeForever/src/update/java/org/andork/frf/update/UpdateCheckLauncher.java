package org.andork.frf.update;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Properties;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

import org.andork.ui.DoSwing;
import org.andork.ui.GenericProgressDialog;
import org.andork.ui.I18n;
import org.andork.ui.I18n.Localizer;
import org.apache.commons.io.FileUtils;

public class UpdateCheckLauncher
{
	public static void main( String[ ] args ) throws IOException
	{
		Properties props = UpdateProperties.getUpdateProperties( );
		
		File updateDir = new File( props.getProperty( UpdateProperties.UPDATE_DIR ) );
		String appNameNoSpaces = props.getProperty( "appName.noSpaces" );
		
		updateCheck( updateDir );
		findAndLaunch( appNameNoSpaces );
	}
	
	private static String formatException( Exception ex )
	{
		return ex.getClass( ).getSimpleName( ) + ": " + ex.getLocalizedMessage( );
	}
	
	public static void updateCheck( File updateDir )
	{
		File archive = null;
		
		if( updateDir.isDirectory( ) )
		{
			for( File file : updateDir.listFiles( ) )
			{
				if( file.getName( ).endsWith( ".zip" ) )
				{
					if( archive == null || file.lastModified( ) > archive.lastModified( ) )
					{
						archive = file;
					}
				}
			}
		}
		
		if( archive == null )
		{
			return;
		}
		class LocalizerCreator extends DoSwing
		{
			Localizer	localizer;
			
			public void run( )
			{
				I18n i18n = new I18n( );
				localizer = i18n.forClass( UpdateCheckLauncher.class );
			}
		}
		
		final Localizer localizer = new LocalizerCreator( ).localizer;
		
		class DialogCreator extends DoSwing
		{
			GenericProgressDialog	dialog;
			
			@Override
			public void run( )
			{
				dialog = new GenericProgressDialog( null );
				dialog.setDefaultCloseOperation( JDialog.DISPOSE_ON_CLOSE );
				localizer.setTitle( dialog , "Fisher Ridge Forever" );
				localizer.setText( dialog.getStatusLabel( ) , "Backing up existing installation..." );
				dialog.getProgressBar( ).setIndeterminate( true );
				dialog.setVisible( true );
			}
		}
		
		final GenericProgressDialog dialog = new DialogCreator( ).dialog;
		
		File currentDir = new File( System.getProperty( "user.dir" ) );
		File backupDir = new File( "backup" );
		
		try
		{
			try
			{
				if( backupDir.exists( ) )
				{
					FileUtils.deleteDirectory( backupDir );
				}
				backupDir.mkdirs( );
				
				FileUtils.copyDirectory( currentDir , backupDir );
			}
			catch( final Exception ex )
			{
				new DoSwing( )
				{
					@Override
					public void run( )
					{
						dialog.dispose( );
						JOptionPane.showMessageDialog( dialog , localizer.getFormattedString( "backupFailed" , formatException( ex ) ) , localizer.getString( "Software Update" ) , JOptionPane.ERROR_MESSAGE );
					}
				};
				return;
			}
			
			final ZipFile zipFile = new ZipFile( archive );
			
			int index = 0;
			Enumeration<? extends ZipEntry> entries = zipFile.entries( );
			try
			{
				while( entries.hasMoreElements( ) )
				{
					final ZipEntry entry = entries.nextElement( );
					final int finalIndex = index;
					
					new DoSwing( )
					{
						@Override
						public void run( )
						{
							localizer.setFormattedText( dialog.getStatusLabel( ) , "dialog.statusLabel.extracting" , entry.getName( ) );
							dialog.getProgressBar( ).setIndeterminate( false );
							dialog.getProgressBar( ).setMaximum( zipFile.size( ) );
							dialog.getProgressBar( ).setValue( finalIndex );
						}
					};
					
					if( entry.isDirectory( ) )
					{
						new File( entry.getName( ) ).mkdirs( );
					}
					else
					{
						InputStream in = null;
						FileOutputStream out = null;
						
						try
						{
							in = zipFile.getInputStream( entry );
							ReadableByteChannel inChannel = Channels.newChannel( in );
							out = new FileOutputStream( entry.getName( ) );
							FileChannel outChannel = out.getChannel( );
							outChannel.transferFrom( inChannel , 0L , Long.MAX_VALUE );
						}
						finally
						{
							if( in != null )
							{
								try
								{
									in.close( );
								}
								catch( Exception ex )
								{
								}
							}
							
							if( out != null )
							{
								try
								{
									out.close( );
								}
								catch( Exception ex )
								{
								}
							}
						}
					}
					
					index++ ;
				}
			}
			catch( final Exception ex )
			{
				new DoSwing( )
				{
					@Override
					public void run( )
					{
						String errorMessage = ex.getClass( ).getSimpleName( ) + ": " + ex.getLocalizedMessage( );
						localizer.setFormattedText( dialog.getStatusLabel( ) , "dialog.statusLabel.restoring" , errorMessage );
						dialog.getProgressBar( ).setIndeterminate( true );
					}
				};
				
				FileUtils.copyDirectory( backupDir , currentDir );
			}
			
			new DoSwing( )
			{
				@Override
				public void run( )
				{
					dialog.dispose( );
				}
			};
		}
		catch( final Exception ex )
		{
			new DoSwing( )
			{
				@Override
				public void run( )
				{
					dialog.dispose( );
					JOptionPane.showMessageDialog( dialog , localizer.getFormattedString( "unexpectedError" , formatException( ex ) ) , localizer.getString( "Software Update" ) , JOptionPane.ERROR_MESSAGE );
				}
			};
		}
		finally
		{
			try
			{
				FileUtils.deleteDirectory( backupDir );
			}
			catch( Exception ex )
			{
				ex.printStackTrace( );
			}
			dialog.dispose( );
		}
	}
	
	public static void findAndLaunch( String appNameNoSpaces )
	{
		File wd = new File( System.getProperty( "user.dir" ) );
		
		File jarFile = null;
		
		for( File file : glob( wd , Pattern.compile( appNameNoSpaces + "-main.*\\.jar" ) , true ) )
		{
			jarFile = file;
			break;
		}
		
		File javaw = new File( new File( new File( System.getProperty( "java.home" ) ) , "bin" ) , "javaw" );
		
		try
		{
			ProcessBuilder proc = new ProcessBuilder( ).directory( wd ).command( javaw.toString( ) , "-jar" , jarFile.toString( ) );
			proc.environment( ).putAll( System.getenv( ) );
			proc.start( );
		}
		catch( IOException e )
		{
			e.printStackTrace( );
		}
	}
	
	public static Iterable<File> glob( final File directory , final Pattern pat , boolean recursive )
	{
		return new Iterable<File>( )
		{
			@Override
			public Iterator<File> iterator( )
			{
				return new Iterator<File>( )
				{
					LinkedList<File>	files	= new LinkedList<File>( Arrays.asList( directory.listFiles( ) ) );
					
					private void findNext( )
					{
						while( !files.isEmpty( ) )
						{
							File file = files.peek( );
							if( file.isDirectory( ) )
							{
								files.poll( );
								files.addAll( Arrays.asList( file.listFiles( ) ) );
							}
							else if( !pat.matcher( file.getName( ) ).matches( ) )
							{
								files.poll( );
							}
							else
							{
								break;
							}
						}
					}
					
					@Override
					public boolean hasNext( )
					{
						findNext( );
						return !files.isEmpty( );
					}
					
					@Override
					public File next( )
					{
						findNext( );
						return files.poll( );
					}
					
					@Override
					public void remove( )
					{
						
					}
				};
			}
		};
	}
}
