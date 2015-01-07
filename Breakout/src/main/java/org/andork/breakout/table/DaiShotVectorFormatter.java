package org.andork.breakout.table;

import java.text.MessageFormat;
import java.util.function.DoubleFunction;
import java.util.function.Function;

import org.andork.bind2.Binder;
import org.andork.util.StringUtils;

public class DaiShotVectorFormatter implements Function<DaiShotVector, String>
{
	DoubleFunction<String>	doubleFormatter;
	Binder<String>			messageFormatBinder;

	int						distWidth	= 9;
	int						azmWidth	= 5;
	int						incWidth	= 5;

	public DaiShotVectorFormatter( DoubleFunction<String> doubleFormatter , Binder<String> messageFormatBinder )
	{
		super( );
		this.doubleFormatter = doubleFormatter;
		this.messageFormatBinder = messageFormatBinder;
	}

	@Override
	public String apply( DaiShotVector t )
	{
		return MessageFormat.format( messageFormatBinder.get( ) ,
			StringUtils.pad( t.dist == null ? "--" : doubleFormatter.apply( t.dist ) , ' ' , distWidth , false ) ,
			StringUtils.pad( t.azmFs == null ? "-" : doubleFormatter.apply( t.azmFs ) , ' ' , azmWidth , false ) ,
			StringUtils.pad( t.azmBs == null ? "-" : doubleFormatter.apply( t.azmBs ) , ' ' , azmWidth , false ) ,
			StringUtils.pad( t.incFs == null ? "-" : doubleFormatter.apply( t.incFs ) , ' ' , incWidth , false ) ,
			StringUtils.pad( t.incBs == null ? "-" : doubleFormatter.apply( t.incBs ) , ' ' , incWidth , false )
			);
	}
}
