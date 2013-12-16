package org.andork.jogl.shader;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class ShaderSegment
{
	private final Map<String, Object>	properties			= new HashMap<String, Object>( );
	
	protected static Pattern			PROPERTY_PATTERN	= Pattern.compile( "\\$(\\$|[a-zA-Z_][a-zA-Z0-9_]*)" );
	
	protected static Pattern			VARIABLE_PATTERN	= Pattern.compile( "([a-zA-Z_][a-zA-Z0-9_]*)\\s*;\\s*$" );
	
	public List<String> getVariableDecls( )
	{
		return null;
	}
	
	public String getMainCode( )
	{
		return null;
	}
	
	public List<String> getFunctionDecls( )
	{
		return null;
	}
	
	protected final void setProperty( String propertyName , Object value )
	{
		properties.put( propertyName , value );
	}
	
	protected final String replaceProperties( String text )
	{
		StringBuilder sb = new StringBuilder( );
		
		int lastEnd = 0;
		Matcher m = PROPERTY_PATTERN.matcher( text );
		while( m.find( ) )
		{
			sb.append( text.substring( lastEnd , m.start( ) ) );
			String prop = m.group( 1 );
			if( "$".equals( prop ) )
			{
				sb.append( '$' );
			}
			else
			{
				sb.append( properties.get( prop ) );
			}
			lastEnd = m.end( );
		}
		
		sb.append( text.substring( lastEnd ) );
		
		return sb.toString( );
	}
	
	public static String combine( ShaderSegment ... segments )
	{
		Map<String, String> variables = new LinkedHashMap<String, String>( );
		
		for( ShaderSegment segment : segments )
		{
			List<String> variableDecls = segment.getVariableDecls( );
			if( variableDecls != null )
			{
				for( String variableDecl : variableDecls )
				{
					variableDecl = segment.replaceProperties( variableDecl );
					
					Matcher m = VARIABLE_PATTERN.matcher( variableDecl );
					if( m.matches( ) )
					{
						variables.put( m.group( 1 ) , variableDecl );
					}
					else
					{
						variables.put( variableDecl , variableDecl );
					}
				}
			}
		}
		
		StringBuilder sb = new StringBuilder( );
		
		for( String variable : variables.values( ) )
		{
			sb.append( variable );
		}
		
		sb.append( "void main() {" );
		
		for( ShaderSegment segment : segments )
		{
			String mainCode = segment.getMainCode( );
			if( mainCode != null )
			{
				sb.append( segment.replaceProperties( mainCode ) );
			}
		}
		
		sb.append( "}" );
		
		for( ShaderSegment segment : segments )
		{
			List<String> functionDecls = segment.getFunctionDecls( );
			if( functionDecls != null )
			{
				for( String functionDecl : functionDecls )
				{
					sb.append( segment.replaceProperties( functionDecl ) );
				}
			}
		}
		
		return sb.toString( );
	}
}
