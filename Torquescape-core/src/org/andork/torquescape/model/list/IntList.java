package org.andork.torquescape.model.list;


public class IntList
{
	private IntNode	node;
	private int		size;
	
	public void add( int value )
	{
		node = new IntNode( value , node );
		size++ ;
	}
	
	public int[ ] toArray( )
	{
		int[ ] result = new int[ size ];
		
		int i = size - 1;
		while( i >= 0 )
		{
			result[ i-- ] = node.value;
			node = node.next;
		}
		return result;
	}
	
	private class IntNode
	{
		public int		value;
		public IntNode	next;
		
		public IntNode( int value , IntNode next )
		{
			super( );
			this.value = value;
			this.next = next;
		}
	}
}
