package org.andork.io;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileUtils
{
	private FileUtils( )
	{
		
	}
	
	public static List<File> split( File f )
	{
		List<File> result = new ArrayList<File>( );
		result.add( new File( f.getName( ) ) );
		f = f.getAbsoluteFile( ).getParentFile( );
		while( f != null )
		{
			result.add( 0 , new File( f.getName( ) ) );
			f = f.getParentFile( );
		}
		return result;
	}
	
	public static File canonicalize( File base , File target )
	{
		if( base.isFile( ) )
		{
			base = base.getParentFile( );
		}
		List<File> baseSplit = split( base );
		List<File> targetSplit = split( target );
		
		if( !baseSplit.get( 0 ).equals( targetSplit.get( 0 ) ) )
		{
			return target.getAbsoluteFile( );
		}
		
		File canonicalized = null;
		int start;
		for( start = 0 ; start < Math.min( targetSplit.size( ) , baseSplit.size( ) ) ; start++ )
		{
			if( !baseSplit.get( start ).equals( targetSplit.get( start ) ) )
			{
				break;
			}
		}
		
		for( int i = start ; i < baseSplit.size( ) ; i++ )
		{
			if( canonicalized == null )
			{
				canonicalized = new File( ".." );
			}
			else
			{
				canonicalized = new File( canonicalized , ".." );
			}
		}
		
		for( int i = start ; i < targetSplit.size( ) ; i++ )
		{
			if( canonicalized == null )
			{
				canonicalized = targetSplit.get( i );
			}
			else
			{
				canonicalized = new File( canonicalized , targetSplit.get( i ).getName( ) );
			}
		}
		
		return canonicalized;
	}
}
