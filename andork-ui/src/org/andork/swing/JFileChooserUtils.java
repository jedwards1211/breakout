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
