package org.andork.torquescape.jogl.control;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import org.andork.torquescape.control.ControlState;

public class ControlStateKeyboardHandler implements KeyListener
{
	private ControlState	controlState;
	
	private boolean			upPressed		= false;
	private boolean			downPressed		= false;
	private boolean			leftPressed		= false;
	private boolean			rightPressed	= false;
	
	public ControlStateKeyboardHandler( ControlState controlState )
	{
		super( );
		this.controlState = controlState;
	}
	
	private void updateControlState( )
	{
		synchronized( controlState )
		{
			controlState.update( System.nanoTime( ) );
			controlState.forwardBackward = upPressed == downPressed ? 0 : upPressed ? 1 : -1;
			controlState.rightLeft = rightPressed == leftPressed ? 0 : rightPressed ? 1 : -1;
		}
	}
	
	@Override
	public void keyPressed( KeyEvent e )
	{
		switch( e.getKeyCode( ) )
		{
			case KeyEvent.VK_UP:
				upPressed = true;
				break;
			case KeyEvent.VK_DOWN:
				downPressed = true;
				break;
			case KeyEvent.VK_LEFT:
				leftPressed = true;
				break;
			case KeyEvent.VK_RIGHT:
				rightPressed = true;
				break;
		}
		
		updateControlState( );
	}
	
	@Override
	public void keyReleased( KeyEvent e )
	{
		switch( e.getKeyCode( ) )
		{
			case KeyEvent.VK_UP:
				upPressed = false;
				break;
			case KeyEvent.VK_DOWN:
				downPressed = false;
				break;
			case KeyEvent.VK_LEFT:
				leftPressed = false;
				break;
			case KeyEvent.VK_RIGHT:
				rightPressed = false;
				break;
		}
		
		updateControlState( );
	}
	
	@Override
	public void keyTyped( KeyEvent e )
	{
	}
}