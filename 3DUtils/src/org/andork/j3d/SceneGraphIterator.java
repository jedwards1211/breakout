
package org.andork.j3d;

import java.util.Iterator;
import java.util.Stack;

import javax.media.j3d.Group;
import javax.media.j3d.Node;

import org.omg.CORBA.IntHolder;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * Iterates through the scene graph. There are two types of SceneGraphIterators: bounded and unbounded. A bounded iterator will only iterate through the start
 * node an all of its descendants. An unbounded iterator will continue a depth-first iteration through the entire scene graph after it finishes with the start
 * node and its descendants.
 */
public class SceneGraphIterator implements Iterator<Node>
{
	public SceneGraphIterator( Node start )
	{
		this( start , false );
	}
	
	public SceneGraphIterator( Node start , boolean bounded )
	{
		current = start;
		if( !bounded )
		{
			initStack( current );
		}
	}
	
	void initStack( Node start )
	{
		Node child = start;
		Node parent = child.getParent( );
		
		final Stack<IntHolder> revStack = new Stack<IntHolder>( );
		
		while( parent != null )
		{
			if( parent instanceof Group )
			{
				revStack.push( new IntHolder( ( ( Group ) parent ).indexOfChild( child ) ) );
			}
			child = parent;
			parent = child.getParent( );
		}
		
		while( !revStack.isEmpty( ) )
		{
			indexStack.push( revStack.pop( ) );
		}
	}
	
	Node				current;
	Stack<IntHolder>	indexStack	= new Stack<IntHolder>( );
	
	@Override
	public boolean hasNext( )
	{
		return current != null;
	}
	
	public static Node next( Node node )
	{
		if( node instanceof Group )
		{
			return ( ( Group ) node ).getChild( 0 );
		}
		
		Node current = node;
		while( current != null )
		{
			final Node parent = current.getParent( );
			if( parent == null )
			{
				return null;
			}
			
			if( parent instanceof Group )
			{
				final Group g = ( Group ) parent;
				
				final int nextIndex = g.indexOfChild( current ) + 1;
				if( nextIndex < g.numChildren( ) )
				{
					return g.getChild( nextIndex );
				}
			}
			current = parent;
		}
		
		return null;
	}
	
	@Override
	public Node next( )
	{
		final Node result = current;
		
		if( current instanceof Group && ( ( Group ) current ).numChildren( ) > 0 )
		{
			indexStack.push( new IntHolder( 0 ) );
			current = ( ( Group ) current ).getChild( 0 );
			return result;
		}
		
		while( current != null && !indexStack.isEmpty( ) )
		{
			final Node parent = current.getParent( );
			final IntHolder index = indexStack.peek( );
			if( parent == null )
			{
				indexStack.clear( );
				current = null;
				break;
			}
			
			if( parent instanceof Group )
			{
				final Group g = ( Group ) parent;
				index.value++ ;
				if( index.value < g.numChildren( ) )
				{
					current = g.getChild( index.value );
					break;
				}
			}
			
			indexStack.pop( );
			current = parent;
		}
		
		if( indexStack.isEmpty( ) )
		{
			current = null;
		}
		
		return result;
	}
	
	@Override
	public void remove( )
	{
		throw new NotImplementedException( );
	}
	
	public static Iterable<Node> boundedIterable( final Node start )
	{
		return new Iterable<Node>( )
		{
			@Override
			public Iterator<Node> iterator( )
			{
				return new SceneGraphIterator( start , true );
			}
		};
	}
	
	public static Iterable<Node> unboundedIterable( final Node start )
	{
		return new Iterable<Node>( )
		{
			@Override
			public Iterator<Node> iterator( )
			{
				return new SceneGraphIterator( start );
			}
		};
	}
	
	public static Iterator<Node> childIterator( final Group g )
	{
		return new Iterator<Node>( )
		{
			int	i	= 0;
			
			@Override
			public boolean hasNext( )
			{
				return i < g.numChildren( );
			}
			
			@Override
			public Node next( )
			{
				return g.getChild( i++ );
			}
			
			@Override
			public void remove( )
			{
				g.removeChild( i - 1 );
			}
		};
	}
	
	public static Iterable<Node> childIterable( final Group g )
	{
		return new Iterable<Node>( )
		{
			@Override
			public Iterator<Node> iterator( )
			{
				return childIterator( g );
			}
		};
	}
}
