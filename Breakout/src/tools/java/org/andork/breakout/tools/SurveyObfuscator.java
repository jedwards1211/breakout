package org.andork.breakout.tools;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.andork.collect.InputStreamLineIterable;

public class SurveyObfuscator
{
	public static void main( String[ ] args )
	{
		Set<String> forbiddenNames = new HashSet<String>( );
		Map<String, String> alphaMap = new HashMap<String, String>( );
		
		Map<Character, Character> charMap = new HashMap<Character, Character>( );
		List<Character> chars = new ArrayList<Character>( );
		for( char c = 'A' ; c <= 'Z' ; c++ )
		{
			chars.add( c );
		}
		Collections.shuffle( chars );
		for( int i = 0 ; i < chars.size( ) ; i++ )
		{
			charMap.put( ( char ) ( 'A' + i ) , chars.get( i ) );
		}
		
		List<String[ ]> lines = new ArrayList<String[ ]>( );
		
		for( String line : InputStreamLineIterable.perlDiamond( args ) )
		{
			String[ ] cols = line.split( "\t" );
			lines.add( cols );
			
			forbiddenNames.add( alphaPart( cols[ 0 ] ) );
			forbiddenNames.add( alphaPart( cols[ 1 ] ) );
		}
		
		for( String[ ] cols : lines )
		{
			if( cols.length > 0 )
			{
				// cols[ 0 ] = replaceName( cols[ 0 ] , alphaMap , forbiddenNames );
				cols[ 0 ] = replaceName2( cols[ 0 ] , charMap );
			}
			if( cols.length > 1 )
			{
				// cols[ 1 ] = replaceName( cols[ 1 ] , alphaMap , forbiddenNames );
				cols[ 1 ] = replaceName2( cols[ 1 ] , charMap );
			}
			
			System.out.println( org.andork.util.StringUtils.join( "\t" , cols ) );
		}
	}
	
	private static Random	rand	= new Random( );
	
	private static String alphaPart( String name )
	{
		for( int i = 0 ; i < name.length( ) ; i++ )
		{
			if( Character.isDigit( name.charAt( i ) ) )
			{
				return name.substring( 0 , i );
			}
		}
		return name;
	}
	
	private static String replaceName2( String name , Map<Character, Character> charMap )
	{
		StringBuilder sb = new StringBuilder( );
		for( int i = 0 ; i < name.length( ) ; i++ )
		{
			char c = name.charAt( i );
			Character replacement = charMap.get( c );
			sb.append( replacement == null ? c : replacement );
		}
		return sb.toString( );
	}
	
	private static String replaceName( String name , Map<String, String> alphaMap , Set<String> forbiddenNames )
	{
		String alphaPart = alphaPart( name );
		String numberPart = name.substring( alphaPart.length( ) );
		
		String replacement = alphaMap.get( alphaPart );
		if( alphaPart.isEmpty( ) )
		{
			replacement = "";
		}
		
		if( replacement == null )
		{
			do
			{
				StringBuilder sb = new StringBuilder( );
				for( int i = 0 ; i < alphaPart.length( ) ; i++ )
				{
					sb.append( ( char ) ( 'A' + rand.nextInt( 26 ) ) );
				}
				replacement = sb.toString( );
			} while( alphaMap.containsKey( replacement ) || forbiddenNames.contains( replacement ) );
			alphaMap.put( alphaPart , replacement );
		}
		
		return replacement + numberPart;
	}
}
