package org.metacave;

import java.util.HashMap;
import java.util.Map;

import org.andork.unit.Angle;
import org.andork.unit.Length;
import org.andork.unit.Unit;
import org.andork.unit.UnitType;
import org.andork.unit.UnitizedDouble;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

public class MetacaveJson
{
	private static final Map<String, Unit<Length>> lengthUnitMap = new HashMap<>( );
	private static final Map<String, Unit<Angle>> angleUnitMap = new HashMap<>( );

	static
	{
		lengthUnitMap.put( "m" , Length.meters );
		lengthUnitMap.put( "ft" , Length.feet );
		lengthUnitMap.put( "in" , Length.inches );

		angleUnitMap.put( "deg" , Angle.degrees );
		angleUnitMap.put( "rad" , Angle.radians );
		angleUnitMap.put( "grad" , Angle.gradians );
		angleUnitMap.put( "mil" , Angle.milsNATO );
		angleUnitMap.put( "%" , Angle.percentGrade );
	}

	private MetacaveJson( )
	{
	}

	private static <T extends UnitType<T>> UnitizedDouble<T> quantity( JsonNode quantity , String defaultUnit , Map<String, Unit<T>> unitMap )
	{
		if( quantity == null )
		{
			return null;
		}
		else if( quantity instanceof ArrayNode )
		{
			UnitizedDouble<T> result = new UnitizedDouble<>( quantity.get( 0 ).asDouble( ) , ( Unit<T> ) unitMap.get( quantity.get( 1 ).asText( ) ) );
			if( quantity.size( ) > 2 )
			{
				for( int i = 2 ; i < quantity.size( ) ; i += 2 )
				{
					UnitizedDouble<T> next = new UnitizedDouble<>( quantity.get( i ).asDouble( ) , ( Unit<T> ) unitMap.get( quantity.get( i + 1 ).asText( ) ) );
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

	public static UnitizedDouble<Length> length( JsonNode quantity , String defaultUnit )
	{
		return quantity( quantity , defaultUnit , lengthUnitMap );
	}

	public static UnitizedDouble<Angle> angle( JsonNode quantity , String defaultUnit )
	{
		return quantity( quantity , defaultUnit , angleUnitMap );
	}

	@SuppressWarnings( "unchecked" )
	public static <T extends UnitType<T>> UnitizedDouble<T> quantity( JsonNode data , JsonNode parent , String prop , UnitType<T> type )
	{
		if( type == Length.type )
		{
			return ( UnitizedDouble<T> ) length( data , parent , prop );
		}
		else if( type == Angle.type )
		{
			return ( UnitizedDouble<T> ) angle( data , parent , prop );
		}
		throw new IllegalArgumentException( "Unsupported unit type: " + type );
	}

	public static UnitizedDouble<Length> dist( JsonNode shot , JsonNode trip )
	{
		return quantity( shot.get( "dist" ) , trip.get( "distUnit" ).asText( ) , lengthUnitMap );
	}

	public static UnitizedDouble<Angle> fsAzm( JsonNode shot , JsonNode trip )
	{
		String defaultUnit = ( trip.has( "fsAzmUnit" ) ? trip.get( "fsAzmUnit" ) : trip.get( "angleUnit" ) ).asText( );
		return quantity( shot.get( "fsAzm" ) , defaultUnit , angleUnitMap );
	}

	public static UnitizedDouble<Angle> bsAzm( JsonNode shot , JsonNode trip )
	{
		String defaultUnit = ( trip.has( "bsAzmUnit" ) ? trip.get( "bsAzmUnit" ) : trip.get( "angleUnit" ) ).asText( );
		return quantity( shot.get( "bsAzm" ) , defaultUnit , angleUnitMap );
	}

	public static UnitizedDouble<Angle> fsInc( JsonNode shot , JsonNode trip )
	{
		String defaultUnit = ( trip.has( "fsIncUnit" ) ? trip.get( "fsIncUnit" ) : trip.get( "angleUnit" ) ).asText( );
		return quantity( shot.get( "fsInc" ) , defaultUnit , angleUnitMap );
	}

	public static UnitizedDouble<Angle> bsInc( JsonNode shot , JsonNode trip )
	{
		String defaultUnit = ( trip.has( "bsIncUnit" ) ? trip.get( "bsIncUnit" ) : trip.get( "angleUnit" ) ).asText( );
		return quantity( shot.get( "bsInc" ) , defaultUnit , angleUnitMap );
	}

	public static UnitizedDouble<Length> lrudElem( JsonNode station , JsonNode trip , int index )
	{
		JsonNode lrud = station.get( "lrud" );
		if( lrud == null )
		{
			return null;
		}
		return quantity( lrud.get( index ) , trip.get( "distUnit" ).asText( ) , lengthUnitMap );
	}

	public static UnitizedDouble<Length> lrudOrNsewElem( JsonNode station , JsonNode trip , int index )
	{
		JsonNode lrud = station.get( "lrud" );
		if( lrud == null )
		{
			lrud = station.get( "nsew" );
			if( lrud == null )
			{
				return null;
			}
		}
		return quantity( lrud.get( index ) , trip.get( "distUnit" ).asText( ) , lengthUnitMap );
	}

	public static UnitizedDouble<Angle> lrudAngle( JsonNode station , JsonNode trip )
	{
		return quantity( station.get( "lrudAngle" ) , trip.get( "angleUnit" ).asText( ) , angleUnitMap );
	}

	public static UnitizedDouble<Length> nsewElem( JsonNode station , JsonNode trip , int index )
	{
		JsonNode nsew = station.get( "nsew" );
		if( nsew == null )
		{
			return null;
		}
		return quantity( nsew.get( index ) , trip.get( "distUnit" ).asText( ) , lengthUnitMap );
	}

	public static UnitizedDouble<Length> length( JsonNode data , JsonNode parent , String prop )
	{
		String propUnit = prop + "Unit";
		return quantity( data.get( prop ) , parent.get( propUnit ).asText( ) , lengthUnitMap );
	}

	public static UnitizedDouble<Angle> angle( JsonNode data , JsonNode parent , String prop )
	{
		String propUnit = prop + "Unit";
		return quantity( data.get( prop ) , parent.get( propUnit ).asText( ) , angleUnitMap );
	}
}
