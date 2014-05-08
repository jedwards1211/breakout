package org.andork.jogl.awt.anim;

import static java.lang.Math.atan2;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static org.andork.math3d.Vecmath.cross;
import static org.andork.math3d.Vecmath.dot3;
import static org.andork.math3d.Vecmath.invAffine;
import static org.andork.math3d.Vecmath.mmulAffine;
import static org.andork.math3d.Vecmath.mvmulAffine;
import static org.andork.math3d.Vecmath.normalize3;
import static org.andork.math3d.Vecmath.rotY;
import static org.andork.math3d.Vecmath.setColumn3;
import static org.andork.math3d.Vecmath.setIdentity;
import static org.andork.math3d.Vecmath.setRotation;

import javax.media.opengl.awt.GLCanvas;

import org.andork.awt.anim.Animation;
import org.andork.jogl.BasicJOGLScene;
import org.andork.jogl.awt.BasicJOGLSetup;
import org.andork.math3d.Vecmath;
import org.andork.util.AnimationUtils;

public class SpringOrbit implements Animation
{
	
	public SpringOrbit( BasicJOGLSetup setup , float[ ] center , float targetPan , float targetTilt , float factor , float extra , int period )
	{
		if( period <= 0 )
		{
			throw new IllegalArgumentException( "period must be > 0" );
		}
		this.scene = setup.getScene( );
		this.canvas = setup.getCanvas( );
		Vecmath.setf( this.center , center );
		this.targetPan = targetPan;
		this.targetTilt = targetTilt;
		this.factor = factor;
		this.extra = extra;
		this.period = period;
	}
	
	BasicJOGLScene	scene;
	GLCanvas		canvas;
	
	final float[ ]	right			= new float[ 3 ];
	final float[ ]	forward			= new float[ 3 ];
	
	final float[ ]	targetRight		= new float[ 3 ];
	final float[ ]	targetForward	= new float[ 3 ];
	final float[ ]	targetUp		= new float[ 3 ];
	
	final float[ ]	center			= new float[ 3 ];
	float			targetPan;
	float			targetTilt;
	
	final float[ ]	v				= Vecmath.newMat4f( );
	final float[ ]	m1				= Vecmath.newMat4f( );
	final float[ ]	m2				= Vecmath.newMat4f( );
	
	int				period;
	float			factor;
	float			extra;
	
	@Override
	public long animate( long animTime )
	{
		scene.getViewXform( v );
		invAffine( v , m1 );
		mvmulAffine( m1 , 0 , 0 , -1 , forward );
		normalize3( forward );
		mvmulAffine( m1 , 1 , 0 , 0 , right );
		normalize3( right );
		
		targetForward[ 0 ] = right[ 2 ];
		targetForward[ 1 ] = 0;
		targetForward[ 2 ] = -right[ 0 ];
		
		setRotation( m2 , right , targetTilt );
		mvmulAffine( m2 , targetForward );
		cross( right , targetForward , targetUp );
		
		float dTilt = ( float ) atan2( dot3( forward , targetUp ) , dot3( forward , targetForward ) );
		
		targetForward[ 0 ] = ( float ) -sin( targetPan );
		targetForward[ 1 ] = 0;
		targetForward[ 2 ] = ( float ) -cos( targetPan );
		
		targetRight[ 0 ] = -targetForward[ 2 ];
		targetRight[ 1 ] = 0;
		targetRight[ 2 ] = targetForward[ 0 ];
		
		float dPan = ( float ) atan2( dot3( right , targetForward ) , dot3( right , targetRight ) );
		
		float ratio;
		
		if( dPan == 0 && dTilt == 0 )
		{
			return 0;
		}
		else if( Math.abs( dPan ) > Math.abs( dTilt ) )
		{
			float nextPan = AnimationUtils.animate( dPan , 0 , animTime , factor , extra , period );
			ratio = nextPan / dPan;
		}
		else
		{
			float nextTilt = AnimationUtils.animate( dTilt , 0 , animTime , factor , extra , period );
			ratio = nextTilt / dTilt;
		}
		
		boolean done = ratio == 0;
		
		float nextPan = dPan * ratio;
		float nextTilt = dTilt * ratio;
		
		float panAmount = nextPan - dPan;
		float tiltAmount = nextTilt - dTilt;
		
		setIdentity( m1 );
		setIdentity( m2 );
		
		m2[ 12 ] = -center[ 0 ];
		m2[ 13 ] = -center[ 1 ];
		m2[ 14 ] = -center[ 2 ];
		
		rotY( m1 , -panAmount );
		mmulAffine( m1 , m2 , m2 );
		
		setRotation( m1 , right , -tiltAmount );
		mmulAffine( m1 , m2 , m2 );
		
		setIdentity( m1 );
		setColumn3( m1 , 3 , center );
		
		mmulAffine( m1 , m2 , m2 );
		mmulAffine( v , m2 , v );
		scene.setViewXform( v );
		
		canvas.display( );
		
		return done ? 0 : period;
	}
}
