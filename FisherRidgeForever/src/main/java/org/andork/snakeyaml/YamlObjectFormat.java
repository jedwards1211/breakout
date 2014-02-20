package org.andork.snakeyaml;

import java.util.Map;

import org.andork.util.Format;

public class YamlObjectFormat<S extends YamlSpec<S>> implements Format<YamlObject<S>>
{
	S			spec;
	YamlFormat	yamlFormat;
	
	public static <S extends YamlSpec<S>> YamlObjectFormat<S> newInstance( S spec )
	{
		return new YamlObjectFormat<S>( spec , new YamlFormat( ) );
	}
	
	public static <S extends YamlSpec<S>> YamlObjectFormat<S> newInstance( S spec , YamlFormat yamlFormat )
	{
		return new YamlObjectFormat<S>( spec , yamlFormat );
	}
	
	private YamlObjectFormat( S spec , YamlFormat yamlFormat )
	{
		super( );
		this.spec = spec;
		this.yamlFormat = yamlFormat;
	}
	
	@Override
	public String format( YamlObject<S> t )
	{
		return t == null ? null : yamlFormat.format( t.toYaml( ) );
	}
	
	@Override
	public YamlObject<S> parse( String s ) throws Exception
	{
		return s == null || "".equals( s ) ? null : spec.fromYaml( ( Map<?, ?> ) yamlFormat.parse( s ) );
	}
	
}
