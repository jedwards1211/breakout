package org.andork.torquescape.model2.list;

public class CharList
{
	private CharNode	node;
	private int			size;
	
	public void add( char value )
	{
		node = new CharNode( value , node );
		size++ ;
	}
	
	public char[ ] toArray( )
	{
		char[ ] result = new char[ size ];
		
		int i = size - 1;
		while( i >= 0 )
		{
			result[ i-- ] = node.value;
			node = node.next;
		}
		return result;
	}
	
	private class CharNode
	{
		public char		value;
		public CharNode	next;
		
		public CharNode( char value , CharNode next )
		{
			super( );
			this.value = value;
			this.next = next;
		}
	}
}
