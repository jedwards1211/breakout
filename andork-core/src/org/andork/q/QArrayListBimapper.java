package org.andork.q;

import java.util.List;

import org.andork.collect.CollectionUtils;
import org.andork.func.Bimapper;

public class QArrayListBimapper<I, O> implements Bimapper<QArrayList<I>, List<O>>
{
	Bimapper<I, O>	elemBimapper;
	
	private QArrayListBimapper( Bimapper<I, O> elemBimapper )
	{
		super( );
		this.elemBimapper = elemBimapper;
	}
	
	public static <I, O> QArrayListBimapper<I, O> newInstance( Bimapper<I, O> elemBimapper )
	{
		return new QArrayListBimapper<I, O>( elemBimapper );
	}
	
	@Override
	public List<O> map( QArrayList<I> in )
	{
		return in == null ? null : CollectionUtils.toArrayList( CollectionUtils.map( elemBimapper , in ) );
	}
	
	@Override
	public QArrayList<I> unmap( List<O> out )
	{
		if( out == null )
		{
			return null;
		}
		QArrayList<I> result = QArrayList.newInstance( );
		for( O o : out )
		{
			result.add( elemBimapper.unmap( o ) );
		}
		return result;
	}
}
