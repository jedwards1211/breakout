package org.andork.torquescape.model.list;


public class CharList
{
	private CharNode	node;
	private int			size;
	
	public CharList( )
	{
		this( 10 );
	}
	
	public CharList( int capacity )
	{
		node = new CharNode( capacity , null );
	}
	
	public void add( char value )
	{
		if( node.size == node.values.length )
		{
			node = new CharNode( size / 2 , node );
		}
		node.values[ node.size++ ] = value;
		size++ ;
	}
	
	public char[ ] drain( )
	{
		char[ ] result = new char[ size ];
		
		while( size > 0 )
		{
			while( node.size > 0 )
			{
				result[ --size ] = node.values[ --node.size ];
			}
			node = node.next;
		}
		return result;
	}
	
	private class CharNode
	{
		public char[ ]		values;
		public int			size;
		public CharNode	next;
		
		public CharNode( int capacity , CharNode next )
		{
			this.values = new char[ capacity ];
			this.next = next;
		}
	}
}
