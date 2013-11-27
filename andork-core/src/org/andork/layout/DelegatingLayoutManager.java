package org.andork.layout;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager2;
import java.awt.Rectangle;
import java.util.HashMap;
import java.util.Map;

import javax.swing.SwingUtilities;

public class DelegatingLayoutManager implements LayoutManager2
{
	private final Map<Component, LayoutDelegate>	layoutDelegates	= new HashMap<Component, LayoutDelegate>( );
	
	@Override
	public void addLayoutComponent( String name , Component comp )
	{
	}
	
	@Override
	public void removeLayoutComponent( Component comp )
	{
		layoutDelegates.remove( comp );
	}
	
	private Dimension layoutSize( Container parent , LayoutSize size )
	{
		Rectangle bounds = new Rectangle( );
		for( Component comp : parent.getComponents( ) )
		{
			LayoutDelegate layoutDelegate = layoutDelegates.get( comp );
			if( layoutDelegate != null )
			{
				bounds.add( layoutDelegate.desiredBounds( parent , comp , size ) );
			}
			else
			{
				bounds.add( new Rectangle( size.get( comp ) ) );
			}
		}
		
		bounds.width += bounds.x;
		bounds.x = 0;
		bounds.y += bounds.height;
		bounds.y = 0;
		
		return bounds.getSize( );
	}
	
	@Override
	public Dimension preferredLayoutSize( Container parent )
	{
		return layoutSize( parent , LayoutSize.PREFERRED );
	}
	
	@Override
	public Dimension minimumLayoutSize( Container parent )
	{
		return layoutSize( parent , LayoutSize.MINIMUM );
	}
	
	@Override
	public void layoutContainer( Container parent )
	{
		Rectangle defaultBounds = new Rectangle( parent.getSize( ) );
		Insets insets = parent.getInsets( );
		if( insets != null )
		{
			defaultBounds.x += insets.left;
			defaultBounds.width = defaultBounds.width - insets.left - insets.right;
			defaultBounds.y += insets.top;
			defaultBounds.height = defaultBounds.height - insets.top - insets.bottom;
		}
		
		for( Component comp : parent.getComponents( ) )
		{
			LayoutDelegate layoutDelegate = layoutDelegates.get( comp );
			if( layoutDelegate != null )
			{
				layoutDelegate.layoutComponent( parent , comp );
			}
			else
			{
				comp.setBounds( defaultBounds );
			}
		}
	}
	
	public static interface LayoutDelegate
	{
		public Rectangle desiredBounds( Container parent , Component target , LayoutSize layoutSize );
		
		public void layoutComponent( Container parent , Component target );
	}
	
	@Override
	public void addLayoutComponent( Component comp , Object constraints )
	{
		layoutDelegates.put( comp , ( LayoutDelegate ) constraints );
	}
	
	@Override
	public Dimension maximumLayoutSize( Container target )
	{
		return new Dimension( Integer.MAX_VALUE , Integer.MAX_VALUE );
	}
	
	@Override
	public float getLayoutAlignmentX( Container target )
	{
		return 0;
	}
	
	@Override
	public float getLayoutAlignmentY( Container target )
	{
		return 0;
	}
	
	@Override
	public void invalidateLayout( Container target )
	{
		
	}
}
