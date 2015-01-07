package org.andork.breakout.table;

import java.text.MessageFormat;
import java.util.function.DoubleFunction;
import java.util.function.Function;

import org.andork.bind2.Binder;
import org.andork.util.StringUtils;

public class NevShotVectorFormatter implements Function<NevShotVector, String>
{
	DoubleFunction<String>	doubleFormatter;
	int						offsWidth	= 9;
	Binder<String>			messageFormatBinder;

	public NevShotVectorFormatter( DoubleFunction<String> doubleFormatter , Binder<String> messageFormatBinder )
	{
		super( );
		this.doubleFormatter = doubleFormatter;
		this.messageFormatBinder = messageFormatBinder;
	}

	@Override
	public String apply( NevShotVector t )
	{
		return MessageFormat.format( messageFormatBinder.get( ) ,
			StringUtils.pad( t.n == null ? "--" : doubleFormatter.apply( t.n ) , ' ' , offsWidth , false ) ,
			StringUtils.pad( t.e == null ? "--" : doubleFormatter.apply( t.e ) , ' ' , offsWidth , false ) ,
			StringUtils.pad( t.v == null ? "--" : doubleFormatter.apply( t.v ) , ' ' , offsWidth , false ) );
	}
}
