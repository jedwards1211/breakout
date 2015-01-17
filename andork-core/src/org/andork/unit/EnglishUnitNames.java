package org.andork.unit;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

public class EnglishUnitNames extends UnitNames
{
	private final Map<Unit<?>, Entry>						map			= new HashMap<>( );

	private final Map<UnitType<?>, Map<String, Unit<?>>>	lookupMap	= new LinkedHashMap<>( );

	public static final EnglishUnitNames					inst		= new EnglishUnitNames( );

	private EnglishUnitNames( )
	{
		super( Locale.ENGLISH );

		Entry kilometersEntry = new Entry( );
		kilometersEntry.put( UnitNameType.FULL , "kilometer" , "kilometers" );
		kilometersEntry.put( UnitNameType.ABBREV , "km" , "km" );
		kilometersEntry.put( UnitNameType.SYMBOL , "km" , "km" );
		map.put( Length.kilometers , kilometersEntry );

		Entry metersEntry = new Entry( );
		metersEntry.put( UnitNameType.FULL , "meter" , "meters" );
		metersEntry.put( UnitNameType.ABBREV , "m" , "m" );
		metersEntry.put( UnitNameType.SYMBOL , "m" , "m" );
		map.put( Length.meters , metersEntry );

		Entry centimetersEntry = new Entry( );
		centimetersEntry.put( UnitNameType.FULL , "centimeter" , "centimeters" );
		centimetersEntry.put( UnitNameType.ABBREV , "cm" , "cm" );
		centimetersEntry.put( UnitNameType.SYMBOL , "cm" , "cm" );
		map.put( Length.centimeters , centimetersEntry );

		Entry milesEntry = new Entry( );
		milesEntry.put( UnitNameType.FULL , "mile" , "miles" );
		milesEntry.put( UnitNameType.ABBREV , "mi" , "mi" );
		milesEntry.put( UnitNameType.SYMBOL , "mi" , "mi" );
		map.put( Length.miles , milesEntry );

		Entry yardsEntry = new Entry( );
		yardsEntry.put( UnitNameType.FULL , "yard" , "yards" );
		yardsEntry.put( UnitNameType.ABBREV , "yd" , "yd" );
		yardsEntry.put( UnitNameType.SYMBOL , "yd" , "yd" );
		map.put( Length.yards , yardsEntry );

		Entry feetEntry = new Entry( );
		feetEntry.put( UnitNameType.FULL , "foot" , "feet" );
		feetEntry.put( UnitNameType.ABBREV , "ft" , "ft" );
		feetEntry.put( UnitNameType.SYMBOL , "'" , "'" );
		map.put( Length.feet , feetEntry );

		Entry inchesEntry = new Entry( );
		inchesEntry.put( UnitNameType.FULL , "inch" , "inches" );
		inchesEntry.put( UnitNameType.ABBREV , "in" , "in" );
		inchesEntry.put( UnitNameType.SYMBOL , "\"" , "\"" );
		map.put( Length.inches , inchesEntry );

		Entry degreesEntry = new Entry( );
		degreesEntry.put( UnitNameType.FULL , "degree" , "degrees" );
		degreesEntry.put( UnitNameType.ABBREV , "deg" , "deg" );
		degreesEntry.put( UnitNameType.SYMBOL , "\u00b0" , "\u00b0" );
		map.put( Angle.degrees , degreesEntry );

		Entry radiansEntry = new Entry( );
		radiansEntry.put( UnitNameType.FULL , "radian" , "radians" );
		radiansEntry.put( UnitNameType.ABBREV , "rad" , "rad" );
		radiansEntry.put( UnitNameType.SYMBOL , "rad" , "rad" );
		map.put( Angle.radians , radiansEntry );

		Entry gradiansEntry = new Entry( );
		gradiansEntry.put( UnitNameType.FULL , "gradian" , "gradians" );
		gradiansEntry.put( UnitNameType.ABBREV , "grad" , "grad" );
		gradiansEntry.put( UnitNameType.SYMBOL , "gon" , "gon" );
		map.put( Angle.gradians , gradiansEntry );

		Entry percentGradeEntry = new Entry( );
		percentGradeEntry.put( UnitNameType.FULL , "percent grade" , "percent grade" );
		percentGradeEntry.put( UnitNameType.ABBREV , "% grade" , "% grade" );
		percentGradeEntry.put( UnitNameType.SYMBOL , "%" , "%" );
		map.put( Angle.percentGrade , percentGradeEntry );

		Entry mils6400Entry = new Entry( );
		mils6400Entry.put( UnitNameType.FULL , "mil (NATO)" , "mils (NATO)" );
		mils6400Entry.put( UnitNameType.ABBREV , "mil" , "mils" );
		mils6400Entry.put( UnitNameType.SYMBOL , "mil" , "mil" );
		map.put( Angle.milsNATO , mils6400Entry );

			for( Map.Entry<Unit<?>, Entry> entry : map.entrySet( ) )
		{
			Map<String, Unit<?>> nameMap = lookupMap.get( entry.getKey( ).type );
			if( nameMap == null )
			{
				nameMap = new LinkedHashMap<>( );
				lookupMap.put( entry.getKey( ).type , nameMap );
			}

			for( Map.Entry<UnitNameType, String> sing : entry.getValue( ).singularNames.entrySet( ) )
			{
				nameMap.put( sing.getValue( ) , entry.getKey( ) );
				nameMap.put( sing.getValue( ).toLowerCase( ) , entry.getKey( ) );
			}

			for( Map.Entry<UnitNameType, String> sing : entry.getValue( ).pluralNames.entrySet( ) )
			{
				nameMap.put( sing.getValue( ) , entry.getKey( ) );
				nameMap.put( sing.getValue( ).toLowerCase( ) , entry.getKey( ) );
			}
		}
	}

	public <T extends UnitType<T>> Unit<T> lookup( String unitText , T unitType )
	{
		Map<String, Unit<?>> nameMap = lookupMap.get( unitType );
		if( nameMap == null )
		{
			return null;
		}
		Unit<?> result = nameMap.get( unitText );
		if( result != null )
		{
			return ( Unit<T> ) result;
		}
		return ( Unit<T> ) nameMap.get( unitText.toLowerCase( ) );
	}

	@Override
	public String getName( Unit<?> unit , Number value , UnitNameType nameType )
	{
		Entry entry = map.get( unit );
		return entry == null ? unit.id : entry.getName( value , nameType );
	}

	private static class Entry
	{
		private Entry( )
		{
			super( );
		}

		public void put( UnitNameType nameType , String singular , String plural )
		{
			singularNames.put( nameType , singular );
			pluralNames.put( nameType , plural );
		}

		private final Map<UnitNameType, String>	singularNames	= new HashMap<>( );
		private final Map<UnitNameType, String>	pluralNames		= new HashMap<>( );

		public String getName( Number value , UnitNameType nameType )
		{
			return ( value.doubleValue( ) == 1.0 ? singularNames : pluralNames ).get( nameType );
		}
	}
}
