package org.andork.spatial;

import java.util.LinkedList;

public class SIBranch<T> implements SINode<T>
{
	private SIBranch<T>				parent;
	private BBox					bbox			= new BBox( );
	private boolean					needsRecompute	= false;
	private boolean					isValid			= false;
	private LinkedList<SINode<T>>	children		= new LinkedList<SINode<T>>( );
	
	public SIBranch<T> getParent( )
	{
		return parent;
	}
	
	public void setParent( SIBranch<T> parent )
	{
		this.parent = parent;
	}
	
	public BBox getBBox( )
	{
		if( !isValid )
		{
			validate( );
		}
		return bbox;
	}
	
	public boolean isValid( )
	{
		return isValid;
	}
	
	public void invalidate( )
	{
		isValid = false;
		if( parent != null )
		{
			parent.invalidate( );
		}
	}
	
	public void validate( )
	{
		if( !needsRecompute )
		{
			for( SINode<T> child : children )
			{
				if( !child.isValid( ) )
				{
					BBox cbbox = child.getBBox( );
					
					double lx = cbbox.getLow( 0 );
					double ly = cbbox.getLow( 1 );
					double lz = cbbox.getLow( 2 );
					double ux = cbbox.getHigh( 0 );
					double uy = cbbox.getHigh( 1 );
					double uz = cbbox.getHigh( 2 );
					
					child.validate( );
					cbbox = child.getBBox( );
					
					if( cbbox.getLow( 0 ) <= lx && cbbox.getLow( 1 ) <= ly && cbbox.getLow( 2 ) <= lz && cbbox.getHigh( 0 ) >= ux && cbbox.getHigh( 1 ) >= uy && cbbox.getHigh( 2 ) >= uz )
					{
						bbox.union( bbox , cbbox );
					}
					else
					{
						needsRecompute = true;
						break;
					}
				}
			}
		}
		
		if( needsRecompute )
		{
			bbox.setVoid( );
			for( SINode<T> child : children )
			{
				bbox.union( bbox , child.getBBox( ) );
			}
			needsRecompute = false;
		}
		
		isValid = true;
	}
	
	public void addChild( SINode<T> child )
	{
		if( child.getParent( ) != null )
		{
			throw new IllegalArgumentException( "child already has a parent" );
		}
		child.setParent( this );
		children.add( child );
		invalidate( );
	}
	
	public void removeChild( SINode<T> child )
	{
		children.remove( child );
		child.setParent( null );
		needsRecompute = true;
		invalidate( );
	}
	
	public LinkedList<SINode<T>> getChildren( )
	{
		return children;
	}
}
