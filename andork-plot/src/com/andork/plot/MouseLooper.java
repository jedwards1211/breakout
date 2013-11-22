package com.andork.plot;

import java.awt.AWTException;
import java.awt.Component;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import static java.awt.event.MouseEvent.*;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;

public class MouseLooper extends MouseAdapter
{
	Robot		robot;
	MouseEvent	pressEvent;
	Point		lastDragPoint;
	int			xOffset;
	int			yOffset;
	
	public MouseLooper( ) throws AWTException
	{
		this( new Robot( ) );
	}
	
	public MouseLooper( Robot robot )
	{
		this.robot = robot;
	}
	
	@Override
	public void mousePressed( MouseEvent e )
	{
		if( pressEvent == null )
		{
			pressEvent = e;
			lastDragPoint = null;
			xOffset = 0;
			yOffset = 0;
		}
	}
	
	@Override
	public void mouseReleased( MouseEvent e )
	{
		if( ( e.getModifiersEx( ) & ( BUTTON1_DOWN_MASK | BUTTON2_DOWN_MASK | BUTTON3_DOWN_MASK ) ) == 0 )
		{
			pressEvent = null;
			lastDragPoint = null;
		}
	}
	
	@Override
	public void mouseDragged( MouseEvent e )
	{
		Component c = e.getComponent( );
		
		Rectangle bounds;
		if( c instanceof JComponent )
		{
			bounds = ( ( JComponent ) c ).getVisibleRect( );
		}
		else
		{
			bounds = SwingUtilities.getLocalBounds( c );
		}
		Point p = new Point( );
		SwingUtilities.convertPointToScreen( p , c );
		bounds.setLocation( p );
		
		Rectangle screenBounds = c.getGraphicsConfiguration( ).getBounds( );
		screenBounds.x++ ;
		screenBounds.y++ ;
		screenBounds.width -= 2;
		screenBounds.height -= 2;
		
		bounds = bounds.intersection( screenBounds );
		bounds.x -= p.x;
		bounds.y -= p.y;
		
		if( !bounds.contains( e.getPoint( ) ) )
		{
			int newX = ( e.getX( ) - bounds.x ) % bounds.width;
			if( newX < 0 )
			{
				newX += bounds.width;
			}
			newX += bounds.x;
			
			int newY = ( e.getY( ) - bounds.y ) % bounds.height;
			if( newY < 0 )
			{
				newY += bounds.height;
			}
			newY += bounds.y;
			
			Point newLoc = new Point( );
			SwingUtilities.convertPointToScreen( newLoc , c );
			
			robot.mouseMove( newLoc.x + newX , newLoc.y + newY );
		}
		
		if( lastDragPoint != null )
		{
			xOffset += e.getX( ) - lastDragPoint.x;
			yOffset += e.getY( ) - lastDragPoint.y;
			
			lastDragPoint = e.getPoint( );
		}
		
		MouseEvent fakeEvent = new MouseEvent( e.getComponent( ) , e.getID( ) , e.getWhen( ) , e.getModifiers( ) ,
				pressEvent.getX( ) + xOffset , pressEvent.getY( ) + yOffset ,
				pressEvent.getXOnScreen( ) + xOffset , pressEvent.getYOnScreen( ) + yOffset ,
				pressEvent.getClickCount( ) , pressEvent.isPopupTrigger( ) , pressEvent.getButton( ) );
	}
}
