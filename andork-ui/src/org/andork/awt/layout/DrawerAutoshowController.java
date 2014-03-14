package org.andork.awt.layout;

import java.awt.Component;
import java.awt.Container;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.SwingUtilities;

import org.andork.awt.layout.DelegatingLayoutManager.LayoutDelegate;

public class DrawerAutoshowController extends MouseAdapter
{
	int			autoshowDistance	= 30;
	
	public int getAutoshowDistance( )
	{
		return autoshowDistance;
	}
	
	public void setAutoshowDistance( int autoshowDistance )
	{
		this.autoshowDistance = autoshowDistance;
	}
	
	@Override
	public void mouseMoved( MouseEvent e )
	{
		Component c = e.getComponent( );
		
		while( c != null && ( !( c instanceof Container ) ||
				!( ( ( Container ) c ).getLayout( ) instanceof DelegatingLayoutManager ) ) )
		{
			c = c.getParent( );
		}
		
		if( c == null )
		{
			return;
		}
		
		Container parent = ( Container ) c;
		
		DelegatingLayoutManager layout = ( DelegatingLayoutManager ) ( ( Container ) parent ).getLayout( );
		
		for( Component comp : parent.getComponents( ) )
		{
			LayoutDelegate delegate = layout.getDelegate( comp );
			if( delegate instanceof DrawerLayoutDelegate )
			{
				DrawerLayoutDelegate drawerDelegate = ( DrawerLayoutDelegate ) delegate;
				
				Point p = SwingUtilities.convertPoint( e.getComponent( ) , e.getPoint( ) , comp );
				if( RectangleUtils.rectilinearDistance( SwingUtilities.getLocalBounds( comp ) , p ) < autoshowDistance )
				{
					drawerDelegate.open( );
				}
				else if( !drawerDelegate.isPinned( ) )
				{
					drawerDelegate.close( );
				}
			}
		}
	}
}
