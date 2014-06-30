package org.andork.spatial;

import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class RTraversal
{
	public static <R, T> boolean traverse( RNode<R, T> root , Predicate<RNode<R, T>> onNodes , Predicate<RLeaf<R, T>> onLeaves )
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
	
	public static <R, T, V> V traverse( RNode<R, T> node , Predicate<RNode<R, T>> onNodes , Function<RLeaf<R, T>, V> onLeaves , Supplier<V> initialValue , BinaryOperator<V> combiner )
	{
		if( onNodes.test( node ) )
		{
			if( node instanceof RBranch )
			{
				RBranch<R, T> branch = ( RBranch<R, T> ) node;
				V value = initialValue.get( );
				for( int i = 0 ; i < branch.numChildren( ) ; i++ )
				{
					V childValue = traverse( branch.childAt( i ) , onNodes , onLeaves , initialValue , combiner );
					value = combiner.apply( value , childValue );
				}
				return value;
			}
			else if( node instanceof RLeaf )
			{
				return onLeaves.apply( ( RLeaf<R, T> ) node );
			}
		}
		return initialValue.get( );
	}
}