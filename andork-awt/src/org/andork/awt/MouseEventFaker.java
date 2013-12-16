package org.andork.awt;

import static java.awt.event.MouseEvent.MOUSE_CLICKED;
import static java.awt.event.MouseEvent.MOUSE_DRAGGED;
import static java.awt.event.MouseEvent.MOUSE_ENTERED;
import static java.awt.event.MouseEvent.MOUSE_EXITED;
import static java.awt.event.MouseEvent.MOUSE_MOVED;
import static java.awt.event.MouseEvent.MOUSE_PRESSED;
import static java.awt.event.MouseEvent.MOUSE_RELEASED;
import static java.awt.event.MouseEvent.MOUSE_WHEEL;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

public class MouseEventFaker
{
	public static void dispatch( MouseEvent e , MouseAdapter adapter )
	{
		switch( e.getID( ) )
		{
			case MOUSE_PRESSED:
				adapter.mousePressed( e );
				return;
			case MOUSE_RELEASED:
				adapter.mouseReleased( e );
				return;
			case MOUSE_CLICKED:
				adapter.mouseClicked( e );
				return;
			case MOUSE_MOVED:
				adapter.mouseMoved( e );
				return;
			case MOUSE_DRAGGED:
				adapter.mouseDragged( e );
				return;
			case MOUSE_ENTERED:
				adapter.mouseEntered( e );
				return;
			case MOUSE_EXITED:
				adapter.mouseExited( e );
				return;
			case MOUSE_WHEEL:
				adapter.mouseWheelMoved( ( MouseWheelEvent ) e );
				return;
		}
	}
}
