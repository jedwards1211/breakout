package org.andork.breakout.table;

import java.util.function.Function;

import org.andork.unit.Angle;
import org.andork.unit.Length;
import org.andork.unit.Unit;
import org.andork.util.PowerCloneable;
import org.andork.util.StringUtils;

/**
 * Survey data for a segment of cave between two survey stations.
 * 
 * @author James
 */
public class Shot implements PowerCloneable
{
	private String							fromStationName;
	private String							toStationName;
	private ParsedTextWithType<ShotVector>	vector;
	private ParsedTextWithType<XSection>	xSectionAtFrom;
	private ParsedTextWithType<XSection>	xSectionAtTo;
	private Unit<Length>					lengthUnit;
	private Unit<Angle>						angleUnit;
	private Object[ ]						custom;

	public String getFromStationName( )
	{
		return fromStationName;
	}

	public void setFromStationName( String fromStationName )
	{
		this.fromStationName = fromStationName;
	}

	public String getToStationName( )
	{
		return toStationName;
	}

	public void setToStationName( String toStationName )
	{
		this.toStationName = toStationName;
	}

	public ParsedTextWithType<ShotVector> getVector( )
	{
		return vector;
	}

	public void setVector( ParsedTextWithType<ShotVector> vector )
	{
		this.vector = vector;
	}

	public ParsedTextWithType<XSection> getXSectionAtFrom( )
	{
		return xSectionAtFrom;
	}

	public void setXSectionAtFrom( ParsedTextWithType<XSection> xSectionAtFrom )
	{
		this.xSectionAtFrom = xSectionAtFrom;
	}

	public ParsedTextWithType<XSection> getXSectionAtTo( )
	{
		return xSectionAtTo;
	}

	public void setXSectionAtTo( ParsedTextWithType<XSection> xSectionAtTo )
	{
		this.xSectionAtTo = xSectionAtTo;
	}

	public Unit<Length> getLengthUnit( )
	{
		return lengthUnit;
	}

	public void setLengthUnit( Unit<Length> lengthUnit )
	{
		this.lengthUnit = lengthUnit;
	}

	public Unit<Angle> getAngleUnit( )
	{
		return angleUnit;
	}

	public void setAngleUnit( Unit<Angle> angleUnit )
	{
		this.angleUnit = angleUnit;
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
		if( !StringUtils.isNullOrEmpty( fromStationName ) ||
			!StringUtils.isNullOrEmpty( toStationName ) ||
			( vector != null && !vector.isEmpty( ) ) ||
			( xSectionAtFrom != null && !xSectionAtFrom.isEmpty( ) ) ||
			( xSectionAtTo != null && !xSectionAtTo.isEmpty( ) ) )
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
	public Shot clone( Function<Object, Object> subcloner )
	{
		Shot result = new Shot( );

		result.fromStationName = fromStationName;
		result.toStationName = toStationName;
		result.vector = ( ParsedTextWithType<ShotVector> ) subcloner.apply( vector );
		result.xSectionAtFrom = ( ParsedTextWithType<XSection> ) subcloner.apply( xSectionAtFrom );
		result.xSectionAtTo = ( ParsedTextWithType<XSection> ) subcloner.apply( xSectionAtTo );
		result.lengthUnit = lengthUnit;
		result.angleUnit = angleUnit;
		result.custom = Cloners.cloneArray( custom , subcloner );
		return result;
	}
}
