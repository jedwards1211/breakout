package org.andork.jogl.awt.anim;

import javax.media.opengl.awt.GLCanvas;

import org.andork.awt.anim.Animation;
import org.andork.jogl.BasicJOGLScene;
import org.andork.jogl.awt.BasicJOGLSetup;
import org.andork.math3d.Vecmath;
import org.andork.util.AnimationUtils;

public class SpringTranslation implements Animation
{
	
	public SpringTranslation( BasicJOGLSetup setup , float[ ] target , float factor , float extra , int period )
	{
		if( period <= 0 )
		{
			throw new IllegalArgumentException( "period must be > 0" );
		}
		this.scene = setup.getScene( );
		this.canvas = setup.getCanvas( );
		Vecmath.setf( this.target , target );
		this.factor = factor;
		this.extra = extra;
		this.period = period;
	}
	
	BasicJOGLScene	scene;
	GLCanvas		canvas;
	
	final float[ ]	target	= new float[ 3 ];
	
	final float[ ]	v		= Vecmath.newMat4f( );
	
	int				period;
	
	float			factor;
	float			extra;
	
	@Override
	public long animate( long animTime )
	{
		scene.getViewXform( v );
		Vecmath.invAffine( v );
		
		v[ 12 ] = AnimationUtils.animate( v[ 12 ] , target[ 0 ] , animTime , factor , extra , period );
		v[ 13 ] = AnimationUtils.animate( v[ 13 ] , target[ 1 ] , animTime , factor , extra , period );
		v[ 14 ] = AnimationUtils.animate( v[ 14 ] , target[ 2 ] , animTime , factor , extra , period );
		
		boolean done = v[ 12 ] == target[ 0 ] && v[ 13 ] == target[ 1 ] && v[ 14 ] == target[ 2 ];
		
		Vecmath.invAffine( v );
		
		scene.setViewXform( v );
		canvas.display( );
		
		return done ? 0 : period;
	}
}
