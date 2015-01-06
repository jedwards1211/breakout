package org.andork.breakout.table;

import java.util.function.DoubleFunction;
import java.util.function.Function;

import org.andork.util.StringUtils;

public class OffsetShotVectorFormatter implements Function<OffsetShotVector, String>
{
	DoubleFunction<String>	doubleFormatter;
	int						offsWidth	= 9;

	public OffsetShotVectorFormatter( DoubleFunction<String> doubleFormatter )
	{
		super( );
		this.doubleFormatter = doubleFormatter;
	}

	@Override
	public String apply( OffsetShotVector t )
	{
		StringBuilder sb = new StringBuilder( );
		sb.append( "N: " );
		sb.append( StringUtils.pad( t.n == null ? "--" : doubleFormatter.apply( t.n ) , ' ' , offsWidth , false ) );
		sb.append( "  E: " );
		sb.append( StringUtils.pad( t.e == null ? "--" : doubleFormatter.apply( t.e ) , ' ' , offsWidth , false ) );
		sb.append( "  D: " );
		sb.append( StringUtils.pad( t.d == null ? "--" : doubleFormatter.apply( t.d ) , ' ' , offsWidth , false ) );
		return sb.toString( );
	}
}
