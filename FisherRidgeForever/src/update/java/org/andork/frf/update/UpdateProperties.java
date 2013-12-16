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
