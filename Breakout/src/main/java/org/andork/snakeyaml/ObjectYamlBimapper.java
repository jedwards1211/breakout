package org.andork.snakeyaml;

import org.andork.func.Bimapper;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.DumperOptions.FlowStyle;

public class ObjectYamlBimapper implements Bimapper<Object, String>
{
	Yaml	yaml;
	
	public ObjectYamlBimapper( )
	{
		DumperOptions options = new DumperOptions( );
		options.setDefaultFlowStyle( FlowStyle.BLOCK );
		yaml = new Yaml( options );
	}
	
	@Override
	public String map( Object in )
	{
		return yaml.dump( in );
	}
	
	@Override
	public Object unmap( String out )
	{
		return yaml.load( out );
	}
}
