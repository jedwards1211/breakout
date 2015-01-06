package org.andork.breakout.table;

import java.util.function.DoubleFunction;
import java.util.function.Function;

import org.andork.util.StringUtils;

public class DaiShotVectorFormatter implements Function<DaiShotVector, String>
{
	DoubleFunction<String>		doubleFormatter;
	TwoElemFormatter<Double>	twoElemFormatter;
	int							distWidth	= 9;

	public DaiShotVectorFormatter( DoubleFunction<String> doubleFormatter )
	{
		super( );
		this.doubleFormatter = doubleFormatter;
		this.twoElemFormatter = new TwoElemFormatter<>( d -> doubleFormatter.apply( d ) );
	}

	@Override
	public String apply( DaiShotVector t )
	{
		StringBuilder sb = new StringBuilder( );
		sb.append( "Dist: " );
		sb.append( StringUtils.pad( t.dist == null ? "--" : doubleFormatter.apply( t.dist ) , ' ' , distWidth , false ) );
		sb.append( "  Azm: " );
		sb.append( twoElemFormatter.apply( t.azmFs , t.azmBs ) );
		sb.append( "  Inc: " );
		sb.append( twoElemFormatter.apply( t.incFs , t.incBs ) );
		return sb.toString( );
	}
}
