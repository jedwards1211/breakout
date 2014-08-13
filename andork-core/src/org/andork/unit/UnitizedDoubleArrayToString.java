package org.andork.unit;

import java.util.function.DoubleFunction;
import java.util.function.Function;

public class UnitizedDoubleArrayToString<T extends UnitType<T>> implements Function<UnitizedDoubleArray<T>, String>
{
	String										delimiter;
	Function<UnitizedDoubleArray<T>, Unit<T>>	outputUnit;
	DoubleFunction<String>						doubleToString;
	Function<Unit<T>, String>					unitName;
	
	public UnitizedDoubleArrayToString( String delimiter , Function<UnitizedDoubleArray<T>, Unit<T>> outputUnit , DoubleFunction<String> doubleToString , Function<Unit<T>, String> unitName )
	{
		super( );
		this.delimiter = delimiter;
		this.outputUnit = outputUnit;
		this.doubleToString = doubleToString;
		this.unitName = unitName;
	}
	
	@Override
	public String apply( UnitizedDoubleArray<T> t )
	{
		if( t == null )
		{
			return null;
		}
		
		Unit<T> outputUnit = this.outputUnit.apply( t );
		
		StringBuilder sb = new StringBuilder( );
		for( int i = 0 ; i < t.length( ) ; i++ )
		{
			if( i > 0 )
			{
				sb.append( delimiter );
			}
			sb.append( doubleToString.apply( t.get( i , outputUnit ) ) );
		}
		if( t.length( ) > 0 )
		{
			sb.append( ' ' );
			sb.append( unitName.apply( outputUnit ) );
		}
		
		return sb.toString( );
	}
}
