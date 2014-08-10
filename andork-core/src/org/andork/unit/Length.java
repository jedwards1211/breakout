package org.andork.unit;

import java.util.HashMap;
import java.util.Map;

public class Length extends UnitType<Length>
{
	public static final Length											type;
	
	public static final Unit<Length>									meters;
	public static final Unit<Length>									feet;
	
	private static final Map<Unit<Length>, Map<Unit<Length>, Double>>	doubleConversions	= new HashMap<>( );
	
	static
	{
		type = new Length( );
		type.addUnit( meters = new Unit<Length>( type , "m" ) );
		type.addUnit( feet = new Unit<Length>( type , "ft" ) );
		
		Map<Unit<Length>, Double> meterConversions = new HashMap<>( );
		meterConversions.put( feet , 3.280839895013123 );
		doubleConversions.put( meters , meterConversions );
		
		Map<Unit<Length>, Double> feetConversions = new HashMap<>( );
		feetConversions.put( meters , 0.3048 );
		doubleConversions.put( feet , feetConversions );
	}
	
	private Length( )
	{
		
	}
	
	@Override
	public double convert( double d , Unit<Length> from , Unit<Length> to )
	{
		return d * doubleConversions.get( from ).get( to );
	}
	
}
