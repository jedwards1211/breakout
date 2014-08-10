package org.andork.unit;

import java.util.HashMap;
import java.util.Map;

public class Angle extends UnitType<Angle>
{
	public static final Angle										type;
	
	public static final Unit<Angle>									degrees;
	public static final Unit<Angle>									radians;
	public static final Unit<Angle>									gradians;
	
	private static final Map<Unit<Angle>, Map<Unit<Angle>, Double>>	doubleConversions	= new HashMap<>( );
	
	static
	{
		type = new Angle( );
		type.addUnit( degrees = new Unit<Angle>( type , "deg" ) );
		type.addUnit( radians = new Unit<Angle>( type , "rad" ) );
		type.addUnit( gradians = new Unit<Angle>( type , "grad" ) );
		
		Map<Unit<Angle>, Double> degreeConversions = new HashMap<>( );
		degreeConversions.put( radians , Math.PI / 180.0 );
		degreeConversions.put( gradians , 400.0 / 360.0 );
		doubleConversions.put( degrees , degreeConversions );
		
		Map<Unit<Angle>, Double> radianConversions = new HashMap<>( );
		radianConversions.put( degrees , 180.0 / Math.PI );
		radianConversions.put( gradians , 200.0 / Math.PI );
		doubleConversions.put( radians , radianConversions );
		
		Map<Unit<Angle>, Double> gradianConversions = new HashMap<>( );
		gradianConversions.put( degrees , 360.0 / 400.0 );
		gradianConversions.put( radians , Math.PI / 200.0 );
		doubleConversions.put( gradians , gradianConversions );
	}
	
	private Angle( )
	{
		
	}
	
	@Override
	public double convert( double d , Unit<Angle> from , Unit<Angle> to )
	{
		return d * doubleConversions.get( from ).get( to );
	}
	
}
