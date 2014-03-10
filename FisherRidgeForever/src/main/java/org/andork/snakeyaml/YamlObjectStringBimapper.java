package org.andork.snakeyaml;

import java.util.Map;

import org.andork.func.Bimapper;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.DumperOptions.FlowStyle;

public class YamlObjectStringBimapper<S extends YamlSpec<S>> implements Bimapper<YamlObject<S>, String>
{
	S		spec;
	Yaml	yaml;
	
	private YamlObjectStringBimapper( S spec )
	{
		this.spec = spec;
		
		DumperOptions options = new DumperOptions( );
		options.setDefaultFlowStyle( FlowStyle.BLOCK );
		this.yaml = new Yaml( options );
	}
	
	public static <S extends YamlSpec<S>> YamlObjectStringBimapper<S> newInstance( S spec )
	{
		return new YamlObjectStringBimapper<S>( spec );
	}
	
	@Override
	public String map( YamlObject<S> in )
	{
		return yaml.dump( in.toYaml( ) );
	}
	
	@Override
	public YamlObject<S> unmap( String out )
	{
		try
		{
			return spec.fromYaml( ( Map<?, ?> ) yaml.load( out ) );
		}
		catch( Exception ex )
		{
			throw new RuntimeException( ex );
		}
	}
	
}
