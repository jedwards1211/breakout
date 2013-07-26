package org.andork.torquescape.model2.list;

public class PointList
{
	private PointNode	node;
	private int			size;
	
	public void add( float x , float y , float z )
	{
		node = new PointNode( node , x , y , z );
		size++ ;
	}
	
	public int size( )
	{
		return size;
	}
	
	public float[ ] toArray( )
	{
		float[ ] result = new float[ size * 3 ];
		int k = result.length - 1;
		while( k > 0 )
		{
			result[ k-- ] = node.z;
			result[ k-- ] = node.y;
			result[ k-- ] = node.x;
			node = node.next;
		}
		return result;
	}
	
	private static class PointNode
	{
		public PointNode( PointNode next , float x , float y , float z )
		{
			this.next = next;
			this.x = x;
			this.y = y;
			this.z = z;
		}
		
		public PointNode	next;
		
		public float		x;
		public float		y;
		public float		z;
	}
}
