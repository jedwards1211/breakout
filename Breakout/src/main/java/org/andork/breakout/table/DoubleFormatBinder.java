package org.andork.breakout.table;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

import org.andork.bind2.Binder;
import org.andork.bind2.Binding;
import org.andork.bind2.Link;

public class DoubleFormatBinder extends Binder<DecimalFormat> implements Binding
{
	public final Link<Character>	decimalSepLink			= new Link<>( this );
	public final Link<Integer>		numFractionDigitsLink	= new Link<>( this );

	private DecimalFormat			format					= ( DecimalFormat ) DecimalFormat.getInstance( );

	@Override
	public void update( boolean force )
	{
		DecimalFormatSymbols symbols = format.getDecimalFormatSymbols( );
		Integer numFractionDigits = numFractionDigitsLink.get( );
		if( numFractionDigits == null )
		{
			numFractionDigits = 1;
		}
		Character decimalSep = decimalSepLink.get( );
		if( decimalSep == null )
		{
			decimalSep = '.';
		}

		boolean changed = format.getMinimumFractionDigits( ) != numFractionDigits ||
			format.getMaximumFractionDigits( ) != numFractionDigits ||
			symbols.getDecimalSeparator( ) != decimalSep;

		if( force || changed )
		{
			format.setMinimumFractionDigits( numFractionDigits );
			format.setMaximumFractionDigits( numFractionDigits );
			symbols.setDecimalSeparator( decimalSep );
			format.setDecimalFormatSymbols( symbols );

			updateBindings( force );
		}
	}

	@Override
	public DecimalFormat get( )
	{
		return format;
	}
}
