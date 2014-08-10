package org.andork.format;

import java.text.CharacterIterator;
import java.text.DecimalFormat;
import java.util.function.IntSupplier;
import java.util.function.ToDoubleFunction;

import org.andork.func.CharSupplier;

public class DoubleParser implements ToDoubleFunction<CharacterIterator>
{
	CharSupplier	groupingSeparator;
	CharSupplier	decimalSeparator;
	IntSupplier		groupSize;
	IntSupplier		lastGroupSize;
	
	public DoubleParser( )
	{
		this( createDefaultGroupingSeparator( ) , createDefaultDecimalSeparator( ) , createDefaultGroupSize( ) , createDefaultGroupSize( ) );
	}
	
	public DoubleParser( CharSupplier groupingSeparator , CharSupplier decimalSeparator , IntSupplier groupSize , IntSupplier lastGroupSize )
	{
		super( );
		this.groupingSeparator = groupingSeparator;
		this.decimalSeparator = decimalSeparator;
		this.groupSize = groupSize;
		this.lastGroupSize = lastGroupSize;
	}

	private static CharSupplier createDefaultGroupingSeparator( )
	{
		DecimalFormat format = ( DecimalFormat ) DecimalFormat.getInstance( );
		final char separator = format.getDecimalFormatSymbols( ).getGroupingSeparator( );
		return ( ) -> separator;
	}
	
	private static CharSupplier createDefaultDecimalSeparator( )
	{
		DecimalFormat format = ( DecimalFormat ) DecimalFormat.getInstance( );
		final char separator = format.getDecimalFormatSymbols( ).getDecimalSeparator( );
		return ( ) -> separator;
	}
	
	private static IntSupplier createDefaultGroupSize( )
	{
		DecimalFormat format = ( DecimalFormat ) DecimalFormat.getInstance( );
		final int groupSize = format.getGroupingSize( );
		return ( ) -> groupSize;
	}
	
	@Override
	public double applyAsDouble( CharacterIterator i )
	{
		char groupingSeparator = this.groupingSeparator.getAsChar( );
		char decimalSeparator = this.decimalSeparator.getAsChar( );
		
		int groupSize = this.groupSize.getAsInt( );
		int lastGroupSize = this.lastGroupSize.getAsInt( );
		
		while( i.current( ) != CharacterIterator.DONE && Character.isWhitespace( i.current( ) ) )
		{
			i.next( );
		}
		
		char c = i.current( );
		
		double result;
		
		boolean grouped = false;
		int currentGroupSize = 0;
		
		if( Character.isDigit( c ) )
		{
			result = 0.0;
			
			while( c != CharacterIterator.DONE )
			{
				if( Character.isDigit( c ) )
				{
					result = 10.0 * result + ( c - '0' );
					c = i.next( );
					currentGroupSize++ ;
				}
				else if( c == groupingSeparator )
				{
					if( currentGroupSize > groupSize || ( grouped && currentGroupSize != groupSize ) )
					{
						throw new IllegalArgumentException( "Invalid digit grouping" );
					}
					grouped = true;
					currentGroupSize = 0;
					c = i.next( );
				}
				else
				{
					break;
				}
			}
		}
		else
		{
			result = Double.NaN;
			if( c != decimalSeparator )
			{
				return result;
			}
		}
		
		if( grouped && currentGroupSize != lastGroupSize )
		{
			throw new IllegalArgumentException( "Invalid digit grouping" );
		}
		
		if( c == decimalSeparator )
		{
			c = i.next( );
			
			if( !Character.isDigit( c ) )
			{
				return result;
			}
			else if( Double.isNaN( result ) )
			{
				result = 0.0;
			}
			
			double divisor = 1.0;
			
			while( c != CharacterIterator.DONE )
			{
				if( Character.isDigit( c ) )
				{
					result = 10.0 * result + ( c - '0' );
					c = i.next( );
					divisor *= 10.0;
				}
				else
				{
					break;
				}
			}
			
			result /= divisor;
		}
		
		return result;
	}
}
