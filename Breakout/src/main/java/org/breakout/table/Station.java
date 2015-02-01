package org.breakout.table;

import java.util.function.Function;

import org.andork.unit.Length;
import org.andork.unit.Unit;
import org.andork.util.PowerCloneable;
import org.andork.util.StringUtils;

/**
 * Data for a survey station.
 * 
 * @author James
 */
public class Station extends SurveyDataRow
{
	private String				name;
	private ParsedText<Double>	north;
	private ParsedText<Double>	east;
	private ParsedText<Double>	up;
	private Unit<Length>		lengthUnit;
	private Object[ ]			custom;

	public String getName( )
	{
		return name;
	}

	public void setName( String stationName )
	{
		this.name = stationName;
	}

	public ParsedText<Double> getNorth( )
	{
		return north;
	}

	public void setNorth( ParsedText<Double> north )
	{
		this.north = north;
	}

	public ParsedText<Double> getEast( )
	{
		return east;
	}

	public void setEast( ParsedText<Double> east )
	{
		this.east = east;
	}

	public ParsedText<Double> getUp( )
	{
		return up;
	}

	public void setUp( ParsedText<Double> up )
	{
		this.up = up;
	}

	public Unit<Length> getLengthUnit( )
	{
		return lengthUnit;
	}

	public void setLengthUnit( Unit<Length> lengthUnit )
	{
		this.lengthUnit = lengthUnit;
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
		result.north = ( ParsedText<Double> ) subcloner.apply( north );
		result.east = ( ParsedText<Double> ) subcloner.apply( east );
		result.up = ( ParsedText<Double> ) subcloner.apply( up );
		result.lengthUnit = lengthUnit;
		result.custom = Cloners.cloneArray( custom , subcloner );
		return result;
	}
}
