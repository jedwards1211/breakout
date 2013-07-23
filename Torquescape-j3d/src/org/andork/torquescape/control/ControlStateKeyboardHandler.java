package org.andork.torquescape.control;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import org.andork.torquescape.control.ControlState;

public class ControlStateKeyboardHandler implements KeyListener
{
	private ControlState	controlState;
	
	public ControlStateKeyboardHandler( ControlState controlState )
	{
		super( );
		this.controlState = controlState;
	}
	
	@Override
	public void keyPressed( KeyEvent e )
	{
		synchronized( controlState )
		{
			controlState.update( System.nanoTime( ) );
			
			switch( e.getKeyCode( ) )
			{
				case KeyEvent.VK_UP:
					controlState.upPressed = true;
					break;
				case KeyEvent.VK_DOWN:
					controlState.downPressed = true;
					break;
				case KeyEvent.VK_LEFT:
					controlState.leftPressed = true;
					break;
				case KeyEvent.VK_RIGHT:
					controlState.rightPressed = true;
					break;
			}
		}
	}
	
	@Override
	public void keyReleased( KeyEvent e )
	{
		synchronized( controlState )
		{
			controlState.update( System.nanoTime( ) );
			
			switch( e.getKeyCode( ) )
			{
				case KeyEvent.VK_UP:
					controlState.upPressed = false;
					break;
				case KeyEvent.VK_DOWN:
					controlState.downPressed = false;
					break;
				case KeyEvent.VK_LEFT:
					controlState.leftPressed = false;
					break;
				case KeyEvent.VK_RIGHT:
					controlState.rightPressed = false;
					break;
			}
		}
	}
	
	@Override
	public void keyTyped( KeyEvent e )
	{
	}
}