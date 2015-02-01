package org.andork.unit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Locale.LanguageRange;
import java.util.Map;

import org.andork.util.StringUtils;

public abstract class UnitNames
{
	public final Locale							locale;

	private static final Map<Locale, UnitNames>	map	= new HashMap<>( );

	static
	{
		map.put( Locale.ENGLISH , EnglishUnitNames.inst );
	}

	public UnitNames( Locale locale )
	{
		super( );
		this.locale = locale;
	}

	public static UnitNames getNames( Locale locale )
	{
		UnitNames names = map.get( locale );
		if( names == null )
		{
			Locale origLocale = locale;
			List<LanguageRange> ranges = new ArrayList<>( );
			if( !StringUtils.isNullOrEmpty( locale.getLanguage( ) ) )
			{
				ranges.add( new LanguageRange( locale.getLanguage( ) + "-*" ) );
			}
			if( !StringUtils.isNullOrEmpty( locale.getCountry( ) ) )
			{
				ranges.add( new LanguageRange( "*-" + locale.getCountry( ) ) );
			}
			locale = Locale.lookup( ranges , map.keySet( ) );
			if( locale == null )
			{
				locale = EnglishUnitNames.inst.locale;
			}
			names = map.get( locale );
			map.put( origLocale , names );
		}
		return names;
	}

	public abstract String getName( Unit<?> unit , Number value , UnitNameType nameType );

	public static String getName( Locale locale , Unit<?> unit , Number value , UnitNameType nameType )
	{
		UnitNames names = getNames( locale );
		return names.getName( unit , value , nameType );
	}

	public abstract <T extends UnitType<T>> Unit<T> lookup( String unitText , T unitType );

	public static <T extends UnitType<T>> Unit<T> lookup( Locale locale , String unitText , T unitType )
	{
		UnitNames names = getNames( locale );
		return names.lookup( unitText , unitType );
	}
}
