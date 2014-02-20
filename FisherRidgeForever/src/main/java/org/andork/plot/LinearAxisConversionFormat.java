package org.andork.plot;

import java.util.HashMap;
import java.util.Map;

import org.andork.snakeyaml.YamlSpec.Format;
import org.yaml.snakeyaml.Yaml;

import com.andork.plot.LinearAxisConversion;

public class LinearAxisConversionFormat implements Format<LinearAxisConversion>
{
	Yaml											yaml		= new Yaml( );
	
	public static final LinearAxisConversionFormat	instance	= new LinearAxisConversionFormat( );
	
	@Override
	public Object format( LinearAxisConversion t )
	{
		if( t == null )
		{
			return null;
		}
		Map<String, Object> m = new HashMap<String, Object>( );
		m.put( "offset" , t.getOffset( ) );
		m.put( "scale" , t.getScale( ) );
		return yaml.dump( m );
	}
	
	@Override
	public LinearAxisConversion parse( Object s ) throws Exception
	{
		if( s == null )
		{
			return null;
		}
		Map<?, ?> m = ( Map<?, ?> ) yaml.load( s.toString( ) );
		LinearAxisConversion conversion = new LinearAxisConversion( );
		conversion.setOffset( ( Double ) m.get( "offset" ) );
		conversion.setScale( ( Double ) m.get( "scale" ) );
		return conversion;
	}
	
}
