package org.andork.torquescape.model.list;

public class IntList
{
	private IntNode	node;
	private int		size;
	
	public IntList( )
	{
		this( 10 );
	}
	
	public IntList( int capacity )
	{
		node = new IntNode( capacity , null );
	}
	
	public void add( int value )
	{
		if( node.size == node.values.length )
		{
			node = new IntNode( size / 2 , node );
		}
		node.values[ node.size++ ] = value;
		size++ ;
	}
	
	public int[ ] toArray( )
	{
		int[ ] result = new int[ size ];
		
		int i = size - 1;
		while( i >= 0 )
		{
			for( int k = node.size - 1 ; k >= 0 ; k-- )
			{
				result[ i-- ] = node.values[ k ];
			}
			node = node.next;
		}
		return result;
	}
	
	private class IntNode
	{
		public int[ ]	values;
		public int		size;
		public IntNode	next;
		
		public IntNode( int capacity , IntNode next )
		{
			this.values = new int[ capacity ];
			this.next = next;
		}
	}
}
