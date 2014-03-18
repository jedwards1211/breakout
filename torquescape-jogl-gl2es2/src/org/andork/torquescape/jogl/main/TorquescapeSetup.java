package org.andork.torquescape.jogl.main;

import org.andork.jogl.BasicJOGLSetup;
import org.andork.torquescape.jogl.control.ControlStateKeyboardHandler;

import com.jogamp.opengl.util.FPSAnimator;

public class TorquescapeSetup extends BasicJOGLSetup
{
	ControlStateKeyboardHandler	keyboardHandler;
	
	public TorquescapeSetup( )
	{
		super( );
		
		TorquescapeScene scene = ( TorquescapeScene ) this.scene;
		
		keyboardHandler = new ControlStateKeyboardHandler( scene.controlState );
		glWindow.addKeyListener( keyboardHandler );
		
		FPSAnimator animator = new FPSAnimator( glWindow , 30 );
		animator.start( );
	}
	
	@Override
	protected TorquescapeScene createScene( )
	{
		TorquescapeScene scene = new TorquescapeScene( );
		return scene;
	}
}
