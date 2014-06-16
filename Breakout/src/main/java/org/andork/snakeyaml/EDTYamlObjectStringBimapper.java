package org.andork.snakeyaml;

import java.util.Map;

import org.andork.func.Bimapper;
import org.andork.swing.FromEDT;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.DumperOptions.FlowStyle;
import org.yaml.snakeyaml.Yaml;

public class EDTYamlObjectStringBimapper<S extends YamlSpec<S>> implements Bimapper<YamlObject<S>, String>
{
	S		spec;
	Yaml	yaml;
	
	private EDTYamlObjectStringBimapper( S spec )
	{
		this.spec = spec;
		
		DumperOptions options = new DumperOptions( );
		options.setDefaultFlowStyle( FlowStyle.BLOCK );
		this.yaml = new Yaml( options );
	}
	
	public static <S extends YamlSpec<S>> EDTYamlObjectStringBimapper<S> newInstance( S spec )
	{
		return new EDTYamlObjectStringBimapper<S>( spec );
	}
	
	@Override
	public String map( final YamlObject<S> in )
	{
		YamlObject<S> clone = new FromEDT<YamlObject<S>>( )
		{
			@Override
			public YamlObject<S> run( ) throws Throwable
			{
				return in.deepClone( );
			}
		}.result( );
		return yaml.dump( clone.toYaml( ) );
	}
	
	@Override
	public YamlObject<S> unmap( String out )
	{
		try
		{
			Map<?, ?> parsed = ( Map<?, ?> ) yaml.load( out );
			return parsed == null ? null : spec.fromYaml( parsed );
		}
		catch( Exception ex )
		{
			throw new RuntimeException( ex );
		}
	}
	
}
