package org.andork.awt.event;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

public class MouseAdapterWrapper extends MouseAdapter
{
	private MouseAdapter	wrapped;
	
	public MouseAdapterWrapper( )
	{
		
	}
	
	private MouseAdapterWrapper( MouseAdapter wrapped )
	{
		super( );
		this.wrapped = wrapped;
	}
	
	public MouseAdapter getWrapped( )
	{
		return wrapped;
	}
	
	public void setWrapped( MouseAdapter wrapped )
	{
		this.wrapped = wrapped;
	}
	
	@Override
	public void mouseClicked( MouseEvent e )
	{
		if( wrapped != null )
			wrapped.mouseClicked( e );
	}
	
	@Override
	public void mousePressed( MouseEvent e )
	{
		if( wrapped != null )
			wrapped.mousePressed( e );
	}
	
	@Override
	public void mouseReleased( MouseEvent e )
	{
		if( wrapped != null )
			wrapped.mouseReleased( e );
	}
	
	@Override
	public void mouseEntered( MouseEvent e )
	{
		if( wrapped != null )
			wrapped.mouseEntered( e );
	}
	
	@Override
	public void mouseExited( MouseEvent e )
	{
		if( wrapped != null )
			wrapped.mouseExited( e );
	}
	
	@Override
	public void mouseWheelMoved( MouseWheelEvent e )
	{
		if( wrapped != null )
			wrapped.mouseWheelMoved( e );
	}
	
	@Override
	public void mouseDragged( MouseEvent e )
	{
		if( wrapped != null )
			wrapped.mouseDragged( e );
	}
	
	@Override
	public void mouseMoved( MouseEvent e )
	{
		if( wrapped != null )
			wrapped.mouseMoved( e );
	}
	
}
