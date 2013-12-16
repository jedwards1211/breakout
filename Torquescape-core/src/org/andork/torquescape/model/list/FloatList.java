package org.andork.torquescape.model.list;

public class FloatList
{
	private FloatNode	node;
	private int			size;
	
	public FloatList( )
	{
		this( 10 );
	}
	
	public FloatList( int capacity )
	{
		node = new FloatNode( capacity , null );
	}
	
	public void add( float ... values )
	{
		for( float value : values )
		{
			add( value );
		}
	}
	
	public void add( float value )
	{
		if( node.size == node.values.length )
		{
			node = new FloatNode( size / 2 , node );
		}
		node.values[ node.size++ ] = value;
		size++ ;
	}
	
	public int size( )
	{
		return size;
	}
	
	public float[ ] drain( )
	{
		float[ ] result = new float[ size ];
		
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
	
	private class FloatNode
	{
		public float[ ]		values;
		public int			size;
		public FloatNode	next;
		
		public FloatNode( int capacity , FloatNode next )
		{
			this.values = new float[ capacity ];
			this.next = next;
		}
	}
}
