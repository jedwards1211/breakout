package org.andork.jogl.awt.anim;

import javax.media.opengl.awt.GLCanvas;

import org.andork.awt.anim.Animation;
import org.andork.jogl.neu.JoglScene;
import org.andork.jogl.neu.awt.BasicJoglSetup;
import org.andork.math3d.Vecmath;

public class SinusoidalViewTranslationAnimation implements Animation
{
	
	public SinusoidalViewTranslationAnimation( BasicJoglSetup setup , float[ ] end , long period , long duration )
	{
		if( period <= 0 )
		{
			throw new IllegalArgumentException( "period must be > 0" );
		}
		if( duration <= 0 )
		{
			throw new IllegalArgumentException( "duration must be > 0" );
		}
		this.scene = setup.getScene( );
		this.canvas = setup.getCanvas( );
		Vecmath.setf( this.end , end );
		this.period = period;
		this.duration = duration;
	}
	
	JoglScene	scene;
	GLCanvas		canvas;
	
	final float[ ]	start	= new float[ 3 ];
	final float[ ]	end		= new float[ 3 ];
	
	final float[ ]	v		= Vecmath.newMat4f( );
	
	long			period;
	long			duration;
	long			elapsed;
	
	@Override
	public long animate( long animTime )
	{
		scene.getViewXform( v );
		
		Vecmath.invAffine( v );
		
		if( elapsed == 0 )
		{
			Vecmath.getColumn3( v , 3 , start );
		}
		
		elapsed = Math.min( duration , elapsed + animTime );
		
		float f = ( float ) ( 0.5 * ( 1 - Math.cos( Math.PI * elapsed / duration ) ) );
		float rf = 1f - f;
		
		v[ 12 ] = rf * start[ 0 ] + f * end[ 0 ];
		v[ 13 ] = rf * start[ 1 ] + f * end[ 1 ];
		v[ 14 ] = rf * start[ 2 ] + f * end[ 2 ];
		
		Vecmath.invAffine( v );
		
		scene.setViewXform( v );
		
		canvas.display( );
		
		return elapsed >= duration ? 0 : Math.min( period , duration - elapsed );
	}
}
