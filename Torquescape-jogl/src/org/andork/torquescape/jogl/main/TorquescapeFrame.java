package org.andork.torquescape.jogl.main;

import org.andork.jogl.basic.BasicGL3Frame;
import org.andork.torquescape.jogl.control.ControlStateKeyboardHandler;

import com.jogamp.opengl.util.FPSAnimator;

public class TorquescapeFrame extends BasicGL3Frame
{
	ControlStateKeyboardHandler	keyboardHandler;
	
	public TorquescapeFrame( )
	{
		super( );
		
		TorquescapeScene scene = ( TorquescapeScene ) this.scene;
		
		keyboardHandler = new ControlStateKeyboardHandler( scene.controlState );
		
		glCanvas.addKeyListener( keyboardHandler );
		
		FPSAnimator animator = new FPSAnimator( glCanvas , 30 );
		animator.start( );
	}
	
	@Override
	protected TorquescapeScene createScene( )
	{
		TorquescapeScene scene = new TorquescapeScene( );
		return scene;
	}
}
