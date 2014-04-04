package org.andork.func;

import java.io.File;

public class FileStringBimapper implements Bimapper<File, String>
{
	public static final FileStringBimapper	instance	= new FileStringBimapper( );
	
	@Override
	public String map( File in )
	{
		return in.toString( );
	}
	
	@Override
	public File unmap( String out )
	{
		return new File( out );
	}
}
