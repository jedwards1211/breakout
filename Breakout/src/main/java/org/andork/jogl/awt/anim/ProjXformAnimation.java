package org.andork.jogl.awt.anim;

import java.util.function.Function;

import javax.media.opengl.awt.GLCanvas;

import org.andork.awt.anim.Animation;
import org.andork.jogl.ProjectionCalculator;
import org.andork.jogl.neu.JoglScene;
import org.andork.jogl.neu.awt.BasicJoglSetup;

public class ProjXformAnimation implements Animation
{
	/**
	 * @param setup
	 * @param totalTime
	 * @param function
	 *        a function that takes the animation progress from 0 to 1 and the inverted view matrix as arguments, and returns the new inverted view matrix.
	 */
	public ProjXformAnimation( BasicJoglSetup setup , long totalTime , boolean display , Function<Float, ProjectionCalculator> function )
	{
		this.scene = setup.getScene( );
		this.canvas = setup.getCanvas( );
		this.totalTime = totalTime;
		this.display = display;
		this.function = function;
	}
	
	JoglScene								scene;
	GLCanvas								canvas;
	
	long									elapsedTime	= 0;
	
	long									totalTime;
	boolean									display;
	Function<Float, ProjectionCalculator>	function;
	
	@Override
	public long animate( long animTime )
	{
		elapsedTime += animTime;
		
		float f = Math.min( 1f , ( float ) elapsedTime / totalTime );
		
		scene.setProjectionCalculator( function.apply( f ) );
		if( display )
		{
			canvas.display( );
		}
		
		return Math.min( 30 , Math.max( 0 , totalTime - elapsedTime ) );
	}
}
