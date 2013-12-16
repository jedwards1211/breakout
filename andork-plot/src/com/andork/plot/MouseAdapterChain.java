package com.andork.plot;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

public class MouseAdapterChain extends MouseAdapter
{
	MouseAdapter	first;
	MouseAdapter	second;
	
	public MouseAdapterChain( )
	{
		
	}
	
	public MouseAdapterChain( MouseAdapter first , MouseAdapter second )
	{
		super( );
		this.first = first;
		this.second = second;
	}
	
	public void install( Component c )
	{
		c.addMouseListener( this );
		c.addMouseMotionListener( this );
		c.addMouseWheelListener( this );
	}
	
	public void uninstall( Component c )
	{
		c.removeMouseListener( this );
		c.removeMouseMotionListener( this );
		c.removeMouseWheelListener( this );
	}
	
	public void addMouseAdapter( MouseAdapter adapter )
	{
		if( first == null )
		{
			first = adapter;
		}
		else
		{
			second = new MouseAdapterChain( second , adapter );
		}
	}
	
	@Override
	public void mouseClicked( MouseEvent e )
	{
		if( first != null )
		{
			first.mouseClicked( e );
		}
		if( !e.isConsumed( ) && second != null )
		{
			second.mouseClicked( e );
		}
	}
	
	@Override
	public void mousePressed( MouseEvent e )
	{
		if( first != null )
		{
			first.mousePressed( e );
		}
		if( !e.isConsumed( ) && second != null )
		{
			second.mousePressed( e );
		}
	}
	
	@Override
	public void mouseReleased( MouseEvent e )
	{
		if( first != null )
		{
			first.mouseReleased( e );
		}
		if( !e.isConsumed( ) && second != null )
		{
			second.mouseClicked( e );
		}
	}
	
	@Override
	public void mouseEntered( MouseEvent e )
	{
		if( first != null )
		{
			first.mouseEntered( e );
		}
		if( !e.isConsumed( ) && second != null )
		{
			second.mouseEntered( e );
		}
	}
	
	@Override
	public void mouseExited( MouseEvent e )
	{
		if( first != null )
		{
			first.mouseExited( e );
		}
		if( !e.isConsumed( ) && second != null )
		{
			second.mouseExited( e );
		}
	}
	
	@Override
	public void mouseWheelMoved( MouseWheelEvent e )
	{
		if( first != null )
		{
			first.mouseWheelMoved( e );
		}
		if( !e.isConsumed( ) && second != null )
		{
			second.mouseWheelMoved( e );
		}
	}
	
	@Override
	public void mouseDragged( MouseEvent e )
	{
		if( first != null )
		{
			first.mouseDragged( e );
		}
		if( !e.isConsumed( ) && second != null )
		{
			second.mouseDragged( e );
		}
	}
	
	@Override
	public void mouseMoved( MouseEvent e )
	{
		if( first != null )
		{
			first.mouseMoved( e );
		}
		if( !e.isConsumed( ) && second != null )
		{
			second.mouseMoved( e );
		}
	}
	
}
