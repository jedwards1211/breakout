package org.andork.jogl.awt.anim;

import javax.media.opengl.awt.GLCanvas;

import org.andork.awt.anim.Animation;
import org.andork.jogl.BasicJOGLScene;
import org.andork.jogl.awt.BasicJOGLSetup;
import org.andork.math3d.Orbiter;
import org.andork.math3d.Vecmath;
import org.andork.util.Reparam;

public class RandomOrbit implements Animation
{
	
	public RandomOrbit( BasicJOGLSetup setup , float[ ] center , float panRate , float minTilt , float maxTilt , int period , int tiltPeriod )
	{
		if( period <= 0 )
		{
			throw new IllegalArgumentException( "period must be > 0" );
		}
		this.scene = setup.getScene( );
		this.canvas = setup.getCanvas( );
		orbiter.setCenter( center );
		this.panRate = panRate;
		this.minTilt = minTilt;
		this.maxTilt = maxTilt;
		this.period = period;
		this.tiltPeriod = tiltPeriod;
	}
	
	BasicJOGLScene	scene;
	GLCanvas		canvas;
	
	final float[ ]	v			= Vecmath.newMat4f( );
	
	Orbiter			orbiter		= new Orbiter( );
	
	int				period;
	int				tiltPeriod;
	
	float			panRate;
	float			minTilt;
	float			maxTilt;
	
	float			startTilt	= Float.NaN;
	float			endTilt		= Float.NaN;
	
	float			tiltParam;
	
	@Override
	public long animate( long animTime )
	{
		tiltParam += Math.PI * 2 * animTime / tiltPeriod;
		
		scene.getViewXform( v );
		
		float currentTilt = orbiter.getTilt( v );
		if( Float.isNaN( startTilt ) )
		{
			if( currentTilt < minTilt )
			{
				startTilt = currentTilt;
				endTilt = maxTilt;
			}
			else if( currentTilt > maxTilt )
			{
				startTilt = minTilt;
				endTilt = maxTilt;
			}
			else
			{
				startTilt = minTilt;
				endTilt = maxTilt;
			}
		}
		
		if( tiltParam >= Math.PI )
		{
			startTilt = minTilt;
			endTilt = maxTilt;
		}
		
		float nextTilt = Reparam.linear( ( float ) Math.cos( tiltParam ) , 1 , -1 , startTilt , endTilt );
		
		orbiter.orbit( v , panRate * animTime / period , nextTilt - currentTilt , v );
		
		orbiter.orbit( v , panRate * animTime / period , 0 , v );
		
		scene.setViewXform( v );
		
		canvas.display( );
		
		return period;
	}
}
