package org.breakout.table;

import java.util.function.Function;

import org.andork.unit.Length;
import org.andork.unit.Unit;
import org.andork.unit.UnitizedDouble;
import org.andork.util.StringUtils;

/**
 * Data for a survey station.
 * 
 * @author James
 */
public class Station extends SurveyDataRow
{
	private String								name;
	private ParsedText<UnitizedDouble<Length>>	north;
	private ParsedText<UnitizedDouble<Length>>	east;
	private ParsedText<UnitizedDouble<Length>>	up;
	private Object[ ]							custom;

	public String getName( )
	{
		return name;
	}

	public void setName( String stationName )
	{
		this.name = stationName;
	}

	public ParsedText<UnitizedDouble<Length>> getNorth( )
	{
		return north;
	}

	public void setNorth( ParsedText<UnitizedDouble<Length>> north )
	{
		this.north = north;
	}

	public ParsedText<UnitizedDouble<Length>> getEast( )
	{
		return east;
	}

	public void setEast( ParsedText<UnitizedDouble<Length>> east )
	{
		this.east = east;
	}

	public ParsedText<UnitizedDouble<Length>> getUp( )
	{
		return up;
	}

	public void setUp( ParsedText<UnitizedDouble<Length>> up )
	{
		this.up = up;
	}

	public Object[ ] getCustom( )
	{
		return custom;
	}

	public void setCustom( Object[ ] custom )
	{
		this.custom = custom;
	}

	/**
	 * @return {@code true} if this {@code Shot} contains no text nor values in any fields (the only non-null data it
	 *         may contain is format type selections).
	 */
	public boolean isEmpty( )
	{
		if( !StringUtils.isNullOrEmpty( name ) ||
			( north != null && !north.isEmpty( ) ) ||
			( east != null && !east.isEmpty( ) ) ||
			( up != null && !up.isEmpty( ) ) )
		{
			return false;
		}

		if( custom == null )
		{
			return true;
		}

		for( int i = 0 ; i < custom.length ; i++ )
		{
			if( custom[ i ] instanceof String )
			{
				if( !"".equals( custom[ i ] ) )
					return false;
			}
			else if( custom[ i ] instanceof ParsedText )
			{
				if( ! ( ( ParsedText<?> ) custom[ i ] ).isEmpty( ) )
				{
					return false;
				}
			}
			else if( custom[ i ] != null )
			{
				return false;
			}
		}

		return true;
	}

	@SuppressWarnings( "unchecked" )
	@Override
	public Station clone( Function<Object, Object> subcloner )
	{
		Station result = new Station( );

		result.name = name;
		result.north = ( ParsedText<UnitizedDouble<Length>> ) subcloner.apply( north );
		result.east = ( ParsedText<UnitizedDouble<Length>> ) subcloner.apply( east );
		result.up = ( ParsedText<UnitizedDouble<Length>> ) subcloner.apply( up );
		result.custom = Cloners.cloneArray( custom , subcloner );
		return result;
	}
}
