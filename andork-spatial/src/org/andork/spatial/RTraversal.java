package org.andork.spatial;

import java.util.function.Predicate;

public class RTraversal
{
	public static <R, T> boolean traverse( RNode<R, T> root , Predicate<RNode<R, T>> onNodes , Predicate<RNode<R, T>> onLeaves )
	{
		if( onNodes.test( root ) )
		{
			if( root instanceof RBranch )
			{
				RBranch<R, T> branch = ( RBranch<R, T> ) root;
				for( int i = 0 ; i < branch.numChildren( ) ; i++ )
				{
					if( !traverse( branch.childAt( i ) , onNodes , onLeaves ) )
					{
						return false;
					}
				}
			}
			else if( root instanceof RLeaf )
			{
				return onLeaves.test( ( RLeaf<R, T> ) root );
			}
		}
		return true;
	}
}