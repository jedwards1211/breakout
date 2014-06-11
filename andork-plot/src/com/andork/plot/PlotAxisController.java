package com.andork.plot;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import com.andork.plot.PlotAxis.Orientation;

public class PlotAxisController
{
	public PlotAxisController( PlotAxis view )
	{
		this.view = view;
		view.setCursor( Cursor.getPredefinedCursor( Cursor.MOVE_CURSOR ) );
		view.addMouseListener( mouseLooper );
		view.addMouseMotionListener( mouseLooper );
		view.addMouseWheelListener( mouseLooper );
		mouseLooper.addMouseAdapter( mouseHandler );
	}
	
	private final PlotAxis		view;
	private final MouseHandler	mouseHandler	= new MouseHandler( );
	private final MouseLooper	mouseLooper		= new MouseLooper( );
	
	private double				dragZoomSpeed	= 1.01;
	private double				wheelZoomSpeed	= 1.01;
	
	private boolean				enableZoom		= true;
	
	private class MouseHandler extends MouseAdapter implements MouseWheelListener
	{
		MouseEvent	pressEvent	= null;
		MouseEvent	lastEvent	= null;
		
		@Override
		public void mousePressed( MouseEvent e )
		{
			if( pressEvent != null )
			{
				return;
			}
			if( e.getButton( ) == MouseEvent.BUTTON1 || e.getButton( ) == MouseEvent.BUTTON3 )
			{
				pressEvent = lastEvent = e;
			}
		}
		
		@Override
		public void mouseReleased( MouseEvent e )
		{
			if( pressEvent != null && e.getButton( ) == pressEvent.getButton( ) )
			{
				lastEvent = null;
				pressEvent = null;
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
			
			boolean horiz = view.getOrientation( ) == Orientation.HORIZONTAL;
			
			LinearAxisConversion axisConversion = view.getAxisConversion( );
			
			int dx = e.getX( ) - lastEvent.getX( );
			int dy = e.getY( ) - lastEvent.getY( );
			
			double oldMouseDomain = axisConversion.invert( horiz ? lastEvent.getX( ) : lastEvent.getY( ) );
			double oldStart = axisConversion.invert( 0 );
			double oldEnd = axisConversion.invert( view.getViewSpan( ) );
			
			double newMouseDomain = axisConversion.invert( horiz ? e.getX( ) : e.getY( ) );
			double newStart = oldStart;
			double newEnd = oldEnd;
			
			if( pressEvent.getButton( ) == MouseEvent.BUTTON1 || !enableZoom )
			{
				double zoom = enableZoom ? Math.pow( dragZoomSpeed , horiz ? dy : dx ) : 1.0;
				newStart = oldMouseDomain + ( oldStart - newMouseDomain ) * zoom;
				newEnd = oldMouseDomain + ( oldEnd - newMouseDomain ) * zoom;
			}
			else if( pressEvent.getButton( ) == MouseEvent.BUTTON3 )
			{
				if( horiz ? pressEvent.getX( ) > view.getWidth( ) / 2 : pressEvent.getY( ) > view.getHeight( ) / 2 )
				{
					if( newMouseDomain != oldStart )
					{
						newEnd = newStart + ( newEnd - newStart ) * ( oldMouseDomain - oldStart ) / ( newMouseDomain - oldStart );
					}
				}
				else
				{
					if( newMouseDomain != oldEnd )
					{
						newStart = newEnd + ( newStart - newEnd ) * ( oldMouseDomain - oldEnd ) / ( newMouseDomain - oldEnd );
					}
				}
			}
			
			if( newStart != newEnd )
			{
				setAxisRange( newStart , newEnd );
			}
			
			lastEvent = e;
		}
	}
	
	public void doZoom( MouseWheelEvent e )
	{
		LinearAxisConversion axisConversion = view.getAxisConversion( );
		
		double oldStart = axisConversion.invert( 0 );
		double oldEnd = axisConversion.invert( view.getViewSpan( ) );
		
		double mousePosition = axisConversion.invert( view.getOrientation( ) == Orientation.HORIZONTAL ? e.getX( ) : e.getY( ) );
		
		double zoom = enableZoom ? Math.pow( wheelZoomSpeed , e.getUnitsToScroll( ) ) : 1.0;
		
		double newStart = mousePosition + ( oldStart - mousePosition ) * zoom;
		double newEnd = mousePosition + ( oldEnd - mousePosition ) * zoom;
		
		setAxisRange( newStart , newEnd );
	}
	
	protected void setAxisRange( double start , double end )
	{
		view.getAxisConversion( ).set( start , 0 , end , view.getViewSpan( ) );
		// fire an event
		view.setAxisConversion( view.getAxisConversion( ) );
		view.repaint( );
		for( Component plot : view.getPlots( ) )
		{
			plot.repaint( );
		}
	}
	
	public void removeMouseWheelListener( )
	{
		view.removeMouseWheelListener( mouseLooper );
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
	
	public PlotAxis getView( )
	{
		return view;
	}
	
	public boolean isEnableZoom( )
	{
		return enableZoom;
	}
	
	public void setEnableZoom( boolean enableZoom )
	{
		this.enableZoom = enableZoom;
	}
	
	public MouseAdapter getMouseHandler( )
	{
		return mouseHandler;
	}
}
