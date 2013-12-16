package org.andork.spatial;

import org.andork.generic.Visitor;

public class RTrees
{
	private RTrees( )
	{
		
	}
	
	public static String dump( RdNode<?> root , String mbrElemFormat )
	{
		StringBuffer sb = new StringBuffer( );
		dump( root , mbrElemFormat , "" , sb );
		return sb.toString( );
	}
	
	private static void dump( RdNode<?> root , String mbrElemFormat , String tabs , StringBuffer sb )
	{
		sb.append( tabs ).append( Rectmath.prettyPrint( root.mbr( ) , mbrElemFormat ) );
		if( root instanceof RdLeaf )
		{
			Object obj = ( ( RdLeaf<?> ) root ).object( );
			sb.append( " : " ).append( obj );
		}
		
		sb.append( '\n' );
		if( root instanceof RdBranch )
		{
			String tabs2 = tabs + "|\t";
			for( RdNode<?> child : ( ( RdBranch<?> ) root ).children( ) )
			{
				dump( child , mbrElemFormat , tabs2 , sb );
			}
		}
	}
	
	public static <T> void nodesIntersecting( RdNode<T> root , double[ ] r , Visitor<T, Boolean> visitor )
	{
		nodesIntersecting0( root , r , visitor );
	}
	
	private static <T> boolean nodesIntersecting0( RdNode<T> root , double[ ] r , Visitor<T, Boolean> visitor )
	{
		if( Rectmath.intersects( r , root.mbr( ) ) )
		{
			if( root instanceof RdLeaf )
			{
				T object = ( ( RdLeaf<T> ) root ).object( );
				if( !visitor.visit( object ) )
				{
					return false;
				}
			}
			else if( root instanceof RdBranch )
			{
				for( RdNode<T> child : ( ( RdBranch<T> ) root ).children( ) )
				{
					if( !nodesIntersecting0( child , r , visitor ) )
					{
						return false;
					}
				}
			}
		}
		return true;
	}
	
	public static <T> void nodesIntersecting( RdNode<T> root , double[ ] rayOrigin , double[ ] rayDirection , Visitor<T, Boolean> visitor )
	{
		nodesIntersecting0( root , rayOrigin , rayDirection , visitor );
	}
	
	private static <T> boolean nodesIntersecting0( RdNode<T> root , double[ ] rayOrigin , double[ ] rayDirection , Visitor<T, Boolean> visitor )
	{
		if( Rectmath.rayIntersects( rayOrigin , rayDirection , root.mbr( ) ) )
		{
			if( root instanceof RdLeaf )
			{
				T object = ( ( RdLeaf<T> ) root ).object( );
				if( !visitor.visit( object ) )
				{
					return false;
				}
			}
			else if( root instanceof RdBranch )
			{
				for( RdNode<T> child : ( ( RdBranch<T> ) root ).children( ) )
				{
					if( !nodesIntersecting0( child , rayOrigin , rayDirection , visitor ) )
					{
						return false;
					}
				}
			}
		}
		return true;
	}
	
	public static <T> void nodesIntersecting( RfNode<T> root , float[ ] rayOrigin , float[ ] rayDirection , Visitor<T, Boolean> visitor )
	{
		nodesIntersecting0( root , rayOrigin , rayDirection , visitor );
	}
	
	private static <T> boolean nodesIntersecting0( RfNode<T> root , float[ ] rayOrigin , float[ ] rayDirection , Visitor<T, Boolean> visitor )
	{
		if( Rectmath.rayIntersects( rayOrigin , rayDirection , root.mbr( ) ) )
		{
			if( root instanceof RfLeaf )
			{
				T object = ( ( RfLeaf<T> ) root ).object( );
				if( !visitor.visit( object ) )
				{
					return false;
				}
			}
			else if( root instanceof RfBranch )
			{
				for( RfNode<T> child : ( ( RfBranch<T> ) root ).children( ) )
				{
					if( !nodesIntersecting0( child , rayOrigin , rayDirection , visitor ) )
					{
						return false;
					}
				}
			}
		}
		return true;
	}
}
