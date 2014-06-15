package org.andork.swing;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

public class JFileChooserUtils
{
	public static File correctSelectedFileExtension( JFileChooser chooser )
	{
		File file = chooser.getSelectedFile( );
		if( file == null )
		{
			return null;
		}
		String name = file.getName( );
		String newName = name;
		if( name.matches( "\"[^\"]+\"" ) )
		{
			newName = name.substring( 1 , name.length( ) - 1 );
		}
		else if( chooser.getFileFilter( ) instanceof FileNameExtensionFilter )
		{
			String ext = ( ( FileNameExtensionFilter ) chooser.getFileFilter( ) ).getExtensions( )[ 0 ];
			if( !name.endsWith( "." + ext ) )
			{
				newName = name + "." + ext;
			}
		}
		if( !newName.equals( name ) )
		{
			File parent = file.getParentFile( );
			if( parent != null )
			{
				file = new File( parent , newName );
			}
			else
			{
				file = new File( newName );
			}
		}
		
		return file;
	}
}
