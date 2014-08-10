package org.andork.breakout.table;

import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.stream.Stream;

public class IdManager
{
	private final PriorityQueue<Integer>	freeIds		= new PriorityQueue<>( );
	int										nextHiId	= 0;
	
	public int nextId( )
	{
		Integer result = freeIds.poll( );
		if( result == null )
		{
			result = nextHiId++ ;
		}
		return result;
	}
	
	public void release( int id )
	{
		freeIds.add( id );
	}
	
	public void reset( Set<Integer> usedIds )
	{
		freeIds.clear( );
		nextHiId = 0;
		
		for( int id : usedIds )
		{
			nextHiId = Math.max( nextHiId , id + 1 );
		}
		
		for( int id = 0 ; id < nextHiId ; id++ )
		{
			if( !usedIds.contains( id ) )
			{
				freeIds.add( id );
			}
		}
	}
	
	public void reset( Stream<Integer> usedIds )
	{
		Set<Integer> usedIdSet = new HashSet<>( );
		usedIds.forEach( id -> {
			if( id != null )
			{
				usedIdSet.add( id );
			}
		} );
		reset( usedIdSet );
	}
}
