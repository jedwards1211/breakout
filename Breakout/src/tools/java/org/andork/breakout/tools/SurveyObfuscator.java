package org.andork.breakout.tools;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.andork.collect.InputStreamLineIterable;

public class SurveyObfuscator
{
	public static void main( String[ ] args )
	{
		Map<String, String> alphaMap = new HashMap<String, String>( );
		
		for( String line : InputStreamLineIterable.perlDiamond( args ) )
		{
			String[ ] cols = line.split( "\t" );
			if( cols.length > 0 )
			{
				cols[ 0 ] = replaceName( cols[ 0 ] , alphaMap );
			}
			if( cols.length > 1 )
			{
				cols[ 1 ] = replaceName( cols[ 1 ] , alphaMap );
			}
			
			System.out.println( org.andork.util.StringUtils.join( "\t" , cols ) );
		}
	}
	
	private static Random	rand			= new Random( );
	private static Pattern	numberPattern	= Pattern.compile( "\\d+$" );
	
	private static String replaceName( String name , Map<String, String> alphaMap )
	{
		Matcher m = numberPattern.matcher( name );
		String alphaPart;
		String numberPart;
		if( m.find( ) )
		{
			alphaPart = name.substring( 0 , m.start( ) );
			numberPart = m.group( 0 );
		}
		else
		{
			alphaPart = name;
			numberPart = "";
		}
		
		String replacement = alphaMap.get( alphaPart );
		if( replacement == null )
		{
			StringBuilder sb = new StringBuilder( );
			for( int i = 0 ; i < 3 ; i++ )
			{
				sb.append( ( char ) ( 'A' + rand.nextInt( 26 ) ) );
			}
			replacement = sb.toString( );
			alphaMap.put( alphaPart , replacement );
		}
		
		return replacement + numberPart;
	}
}
