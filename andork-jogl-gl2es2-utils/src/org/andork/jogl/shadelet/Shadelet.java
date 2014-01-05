package org.andork.jogl.shadelet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class Shadelet
{
	private final Map<String, Object>	properties					= new HashMap<String, Object>( );
	
	protected static Pattern			PROPERTY_PATTERN			= Pattern.compile( "\\$(\\$|[a-zA-Z_][a-zA-Z0-9_]*)" );
	
	protected static Pattern			VARIABLE_PATTERN			= Pattern.compile( "([a-zA-Z_][a-zA-Z0-9_]*)\\s*(\\[\\s*(\\S+)\\s*\\])?\\s*;" );
	
	protected static Pattern			VARIABLE_DECL_PATTERN		= Pattern.compile( "(/\\*\\s*(vertex|fragment)\\s*\\*/)?\\s*(uniform|attribute|varying)?\\s*([a-zA-Z][a-zA-Z0-9_]+)\\s*([a-zA-Z_][a-zA-Z0-9_]*)\\s*(\\[\\s*(\\S+)\\s*\\])?\\s*;" );
	protected static Pattern			VERTEX_COMMENT_PATTERN		= Pattern.compile( "/\\*\\s*vertex\\s*\\*/" );
	protected static Pattern			FRAGMENT_COMMENT_PATTERN	= Pattern.compile( "/\\*\\s*fragment\\s*\\*/" );
	
	protected static String variableKey( String variableDecl )
	{
		Matcher m = VARIABLE_PATTERN.matcher( variableDecl );
		if( m.find( ) )
		{
			return m.group( 1 );
		}
		return variableDecl;
	}
	
	protected List<String> getDefaultVariableDecls( boolean forVertex , boolean forFragment , boolean forLocal )
	{
		List<String> result = new ArrayList<String>( );
		
		for( Map.Entry<String, Object> entry : properties.entrySet( ) )
		{
			if( entry.getKey( ).endsWith( "Declaration" ) )
			{
				
				String declaration = replaceProperties( "$" + entry.getKey( ) );
				Matcher m = VARIABLE_PATTERN.matcher( declaration );
				if( m.find( ) )
				{
					boolean varying = declaration.contains( "varying" );
					boolean attribute = declaration.contains( "attribute" );
					boolean uniform = declaration.contains( "uniform" );
					boolean local = !varying && !attribute && !uniform;
					boolean vertexOnly = VERTEX_COMMENT_PATTERN.matcher( declaration ).find( );
					boolean fragmentOnly = FRAGMENT_COMMENT_PATTERN.matcher( declaration ).find( );
					
					if( local == forLocal &&
							( forVertex || !attribute ) &&
							( ( forVertex && !fragmentOnly ) ||
							( forFragment && !vertexOnly ) ) )
					{
						if( vertexOnly )
						{
							declaration = declaration.replaceAll( VERTEX_COMMENT_PATTERN.pattern( ) , "" );
						}
						if( fragmentOnly )
						{
							declaration = declaration.replaceAll( FRAGMENT_COMMENT_PATTERN.pattern( ) , "" );
						}
						result.add( declaration );
					}
				}
			}
		}
		
		return result;
	}
	
	public Collection<String> getVertexShaderVariableDecls( )
	{
		return getDefaultVariableDecls( true , false , false );
	}
	
	public Collection<String> getVertexShaderLocalDecls( )
	{
		return getDefaultVariableDecls( true , false , true );
	}
	
	public Collection<String> getFragmentShaderVariableDecls( )
	{
		return getDefaultVariableDecls( false , true , false );
	}
	
	public Collection<String> getFragmentShaderLocalDecls( )
	{
		return getDefaultVariableDecls( false , true , true );
	}
	
	public String getVertexShaderMainCode( )
	{
		return "";
	}
	
	public String getFragmentShaderMainCode( )
	{
		return "";
	}
	
	public Collection<String> getVertexShaderFunctionDecls( )
	{
		return Collections.emptyList( );
	}
	
	public Collection<String> getFragmentShaderFunctionDecls( )
	{
		return Collections.emptyList( );
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
				Object value = properties.get( prop );
				if( value != null )
				{
					sb.append( replaceProperties( value.toString( ) ) );
				}
			}
			lastEnd = m.end( );
		}
		
		sb.append( text.substring( lastEnd ) );
		
		return sb.toString( );
	}
	
	protected final List<String> replaceProperties( Collection<String> text )
	{
		List<String> result = new ArrayList<String>( );
		for( String s : text )
		{
			result.add( replaceProperties( s ) );
		}
		return result;
	}
	
	public String createVertexShaderCode( )
	{
		StringBuffer sb = new StringBuffer( );
		for( String variableDecl : getVertexShaderVariableDecls( ) )
		{
			sb.append( variableDecl );
		}
		sb.append( "void main() {" );
		for( String variableDecl : getVertexShaderLocalDecls( ) )
		{
			sb.append( variableDecl );
		}
		sb.append( getVertexShaderMainCode( ) ).append( '}' );
		for( String functionDecl : getVertexShaderFunctionDecls( ) )
		{
			sb.append( functionDecl );
		}
		
		return replaceProperties( sb.toString( ) );
	}
	
	public String createFragmentShaderCode( )
	{
		StringBuffer sb = new StringBuffer( );
		for( String variableDecl : getFragmentShaderVariableDecls( ) )
		{
			sb.append( variableDecl );
		}
		sb.append( "void main() {" );
		for( String variableDecl : getFragmentShaderLocalDecls( ) )
		{
			sb.append( variableDecl );
		}
		sb.append( getFragmentShaderMainCode( ) ).append( '}' );
		for( String functionDecl : getFragmentShaderFunctionDecls( ) )
		{
			sb.append( functionDecl );
		}
		
		return replaceProperties( sb.toString( ) );
	}
	
	private static Pattern	STATEMENT_PATTERN	= Pattern.compile( "^\\s*([^{};]+)\\s*;" );
	private static Pattern	BLOCK_START_PATTERN	= Pattern.compile( "^\\s*(for\\s*\\(\\s*[^{};]*\\s*;\\s*[^{};]*\\s*;\\s*[^{};]*\\s*\\)|[^{};]+)\\s*\\{" );
	private static Pattern	BLOCK_END_PATTERN	= Pattern.compile( "^\\s*\\}" );
	
	public static String prettyPrint( String code )
	{
		StringBuffer sb = new StringBuffer( );
		
		String tabs = "";
		
		Matcher m;
		while( code.length( ) > 0 )
		{
			m = BLOCK_START_PATTERN.matcher( code );
			if( m.find( ) )
			{
				sb.append( tabs ).append( m.group( 1 ) ).append( '\n' ).append( tabs ).append( "{\n" );
				tabs += "  ";
				code = code.substring( m.end( ) );
				continue;
			}
			
			m = STATEMENT_PATTERN.matcher( code );
			if( m.find( ) )
			{
				int i = 0;
				for( String s : m.group( 1 ).split( "\\s*\r\n|\n\r|\r|\n\\s*" ) )
				{
					if( i++ > 0 )
					{
						sb.append( '\n' );
					}
					sb.append( tabs ).append( s );
				}
				sb.append( ";\n" );
				code = code.substring( m.end( ) );
				continue;
			}
			
			m = BLOCK_END_PATTERN.matcher( code );
			if( m.find( ) )
			{
				tabs = tabs.substring( 2 );
				sb.append( tabs ).append( "}\n" );
				code = code.substring( m.end( ) );
				continue;
			}
			
			sb.append( code );
			code = "";
		}
		
		return sb.toString( );
	}
	
	public static void main( String[ ] args )
	{
		Matcher m = VARIABLE_DECL_PATTERN.matcher( "uniform float a[  hello]; // test" );
		m.find( );
		System.out.println( m.group( ) );
		System.out.println( m.group( 1 ) );
		System.out.println( m.group( 2 ) );
		System.out.println( m.group( 3 ) );
		System.out.println( m.group( 4 ) );
		System.out.println( m.group( 5 ) );
		System.out.println( m.group( 6 ) );
		System.out.println( prettyPrint( "int test; void main() { // this is a test\n test = test1; test2 = test3; for (x = 1; ; x++) { int test3; } }" ) );
	}
}
