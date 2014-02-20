package org.andork.snakeyaml;

import org.andork.util.Format;
import org.yaml.snakeyaml.Yaml;

public class YamlFormat implements Format<Object>
{
	Yaml	yaml;
	
	public YamlFormat( )
	{
		this( new Yaml( ) );
	}
	
	public YamlFormat( Yaml yaml )
	{
		this.yaml = yaml;
	}
	
	@Override
	public String format( Object t )
	{
		return t == null ? null : yaml.dump( t );
	}
	
	@Override
	public Object parse( String s ) throws Exception
	{
		return s == null || "".equals( s ) ? null : yaml.load( s );
	}
}
