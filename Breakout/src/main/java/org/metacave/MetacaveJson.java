package org.metacave;

import java.util.HashMap;
import java.util.Map;

import org.andork.unit.Length;
import org.andork.unit.Unit;
import org.andork.unit.UnitType;
import org.andork.unit.UnitizedDouble;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class MetacaveJson
{
	private static final Map<String, Unit<?>> unitMap = new HashMap<>( );

	private MetacaveJson( )
	{
	}

	public static <T extends UnitType<T>> UnitizedDouble<T> quantity( JsonNode quantity , String defaultUnit )
	{
		if( quantity instanceof ArrayNode )
		{
			ArrayNode array = ( ArrayNode ) quantity;
			UnitizedDouble<T> result = new UnitizedDouble<>( array.get( 0 ).asDouble( ) , ( Unit<T> ) unitMap.get( array.get( 1 ).asText( ) ) );
			if( array.size( ) > 2 )
			{
				for( int i = 2 ; i < array.size( ) ; i += 2 )
				{
					UnitizedDouble<T> next = new UnitizedDouble<>( array.get( i ).asDouble( ) , ( Unit<T> ) unitMap.get( array.get( i + 1 ).asText( ) ) );
					result = result.add( next );
				}
			}
			return result;
		}
		else
		{
			return new UnitizedDouble<>( quantity.asDouble( ) , ( Unit<T> ) unitMap.get( defaultUnit ) );
		}
	}

	public static UnitizedDouble<Length> dist( ObjectNode shot , ObjectNode trip )
	{
		return quantity( shot.get( "dist" ) ,
			( shot.has( "distUnit" ) ? shot : trip ).get( "distUnit" ).asText( ) );
	}
}
