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

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class UpdateProperties
{
	
	public static final String	UPDATE_DIR			= "updateDir";
	public static final String	SOURCE				= "source";
	public static final String	LAST_UPDATE_FILE	= "lastUpdateFile";
	
	public static Properties getUpdateProperties( )
	{
		try
		{
			Properties props = new Properties( );
			props.load( new FileInputStream( "update.properties" ) );
			return props;
		}
		catch( IOException e )
		{
			return null;
		}
	}
}
