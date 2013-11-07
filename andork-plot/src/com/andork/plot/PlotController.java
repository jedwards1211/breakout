package com.andork.plot;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import javax.swing.SwingUtilities;

import com.andork.plot.Axis.Orientation;

public class PlotController extends MouseAdapter
{
	private Component	view;
	private Axis		haxis;
	private Axis		vaxis;
	
	public PlotController( Component view , Axis haxis , Axis vaxis )
	{
		super( );
		this.view = view;
		this.haxis = haxis;
		this.vaxis = vaxis;
	}
	
	private MouseEvent convertForAxis( MouseEvent e , Axis axis )
	{
		Point p = SwingUtilities.convertPoint( e.getComponent( ) , e.getPoint( ) , axis );
		if( axis.getOrientation( ) == Orientation.HORIZONTAL )
		{
			p.y = axis.getHeight( ) / 2;
		}
		else
		{
			p.x = axis.getWidth( ) / 2;
		}
		
		if( e instanceof MouseWheelEvent )
		{
			MouseWheelEvent we = ( MouseWheelEvent ) e;
			return new MouseWheelEvent( axis , e.getID( ) , e.getWhen( ) , e.getModifiers( ) , p.x , p.y , e.getClickCount( ) ,
					e.isPopupTrigger( ) , we.getScrollType( ) , we.getScrollAmount( ) , we.getWheelRotation( ) );
		}
		else
		{
			int xAbs = e.getXOnScreen( ) + p.x - e.getX( );
			int yAbs = e.getYOnScreen( ) + p.y - e.getY( );
			return new MouseEvent( axis , e.getID( ) , e.getWhen( ) , e.getModifiers( ) , p.x , p.y , xAbs , yAbs ,
					e.getClickCount( ) , e.isPopupTrigger( ) , e.getButton( ) );
		}
	}
	
	private void retarget( MouseEvent e , Axis axis )
	{
		axis.dispatchEvent( convertForAxis( e , axis ) );
	}
	
	private void retarget( MouseEvent e )
	{
		if( haxis != null )
		{
			retarget( e , haxis );
		}
		if( vaxis != null )
		{
			retarget( e , vaxis );
		}
	}
	
	@Override
	public void mousePressed( MouseEvent e )
	{
		retarget( e );
	}
	
	@Override
	public void mouseReleased( MouseEvent e )
	{
		retarget( e );
	}
	
	@Override
	public void mouseWheelMoved( MouseWheelEvent e )
	{
		retarget( e );
	}
	
	@Override
	public void mouseDragged( MouseEvent e )
	{
		retarget( e );
	}
}
