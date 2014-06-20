package org.andork.breakout.model;

import java.util.HashMap;
import java.util.Map;

import org.andork.func.Bimapper;

import com.andork.plot.LinearAxisConversion;

public class LinearAxisConversionMapBimapper implements Bimapper<LinearAxisConversion, Object>
{
	private LinearAxisConversionMapBimapper( )
	{
		
	}
	
	public static final LinearAxisConversionMapBimapper	instance	= new LinearAxisConversionMapBimapper( );
	
	@Override
	public Map<String, Double> map( LinearAxisConversion in )
	{
		Map<String, Double> result = new HashMap<String, Double>( );
		result.put( "offset" , in.getOffset( ) );
		result.put( "scale" , in.getScale( ) );
		return result;
	}
	
	@Override
	public LinearAxisConversion unmap( Object out )
	{
		if( out == null || !( out instanceof Map ) )
		{
			return null;
		}
		Map<?, ?> m = ( Map<?, ?> ) out;
		LinearAxisConversion result = new LinearAxisConversion( );
		try
		{
			result.setOffset( Double.parseDouble( String.valueOf( m.get( "offset" ) ) ) );
			result.setScale( Double.parseDouble( String.valueOf( m.get( "scale" ) ) ) );
		}
		catch( Exception ex )
		{
			return null;
		}
		return result;
	}
}
