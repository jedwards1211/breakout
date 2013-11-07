package com.andork.plot;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import com.andork.plot.Axis.Orientation;

public class AxisController
{
	public AxisController( Axis view )
	{
		this.view = view;
		view.setCursor( Cursor.getPredefinedCursor( Cursor.MOVE_CURSOR ) );
		view.addMouseListener( mouseHandler );
		view.addMouseMotionListener( mouseHandler );
		view.addMouseWheelListener( mouseHandler );
	}
	
	private final Axis			view;
	private final MouseHandler	mouseHandler	= new MouseHandler( );
	
	private double				dragZoomSpeed	= 1.01;
	private double				wheelZoomSpeed	= 1.01;
	
	private class MouseHandler extends MouseAdapter implements MouseWheelListener
	{
		MouseEvent	lastEvent	= null;
		
		@Override
		public void mousePressed( MouseEvent e )
		{
			if( e.getButton( ) == MouseEvent.BUTTON1 )
			{
				lastEvent = e;
			}
		}
		
		@Override
		public void mouseReleased( MouseEvent e )
		{
			if( e.getButton( ) == MouseEvent.BUTTON1 )
			{
				lastEvent = null;
			}
		}
		
		@Override
		public void mouseWheelMoved( MouseWheelEvent e )
		{
			doZoom( e );
		}
		
		@Override
		public void mouseDragged( MouseEvent e )
		{
			if( lastEvent == null )
			{
				return;
			}
			
			LinearAxisConversion axisConversion = view.getAxisConversion( );
			
			int dx = e.getX( ) - lastEvent.getX( );
			int dy = e.getY( ) - lastEvent.getY( );
			
			double oldMouseDomain = axisConversion.invert( view.getOrientation( ) == Orientation.HORIZONTAL ? lastEvent.getX( ) : lastEvent.getY( ) );
			double oldStart = axisConversion.invert( 0 );
			double oldEnd = axisConversion.invert( view.getViewSpan( ) );
			
			double mouseDomain = axisConversion.invert( view.getOrientation( ) == Orientation.HORIZONTAL ? e.getX( ) : e.getY( ) );
			
			double zoom = Math.pow( dragZoomSpeed , view.getOrientation( ) == Orientation.HORIZONTAL ? dy : dx );
			double newStart = oldMouseDomain + ( oldStart - mouseDomain ) * zoom;
			double newEnd = oldMouseDomain + ( oldEnd - mouseDomain ) * zoom;
			setAxisRange( newStart , newEnd );
			
			lastEvent = e;
		}
	}
	
	public void doZoom( MouseWheelEvent e )
	{
		LinearAxisConversion axisConversion = view.getAxisConversion( );
		
		double oldStart = axisConversion.invert( 0 );
		double oldEnd = axisConversion.invert( view.getViewSpan( ) );
		
		double mousePosition = axisConversion.invert( view.getOrientation( ) == Orientation.HORIZONTAL ? e.getX( ) : e.getY( ) );
		
		double zoom = Math.pow( wheelZoomSpeed , e.getUnitsToScroll( ) );
		
		double newStart = mousePosition + ( oldStart - mousePosition ) * zoom;
		double newEnd = mousePosition + ( oldEnd - mousePosition ) * zoom;
		
		setAxisRange( newStart , newEnd );
	}
	
	protected void setAxisRange( double start , double end )
	{
		view.getAxisConversion( ).set( start , 0 , end , view.getViewSpan( ) );
		view.repaint( );
		for( Component plot : view.getPlots( ) )
		{
			plot.repaint( );
		}
	}
	
	public double getDragZoomSpeed( )
	{
		return dragZoomSpeed;
	}
	
	public void setDragZoomSpeed( double dragZoomSpeed )
	{
		this.dragZoomSpeed = dragZoomSpeed;
	}
	
	public double getWheelZoomSpeed( )
	{
		return wheelZoomSpeed;
	}
	
	public void setWheelZoomSpeed( double wheelZoomSpeed )
	{
		this.wheelZoomSpeed = wheelZoomSpeed;
	}
	
	public Axis getView( )
	{
		return view;
	}
}
