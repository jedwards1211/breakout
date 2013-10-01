package org.andork.jogl.util;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;

import javax.media.opengl.GLAutoDrawable;

import org.andork.util.ArrayUtils;
import org.andork.vecmath.FloatArrayVecmath;

public class BasicOrbiter extends MouseAdapter
{
	
	final BasicGL3Scene	scene;
	MouseEvent			lastEvent	= null;
	final float[ ]		v			= new float[ 3 ];
	final float[ ]		axis		= new float[ 3 ];
	final float[ ]		right		= new float[ 3 ];
	final float[ ]		up			= new float[ 3 ];
	final float[ ]		backward	= new float[ 3 ];
	final float[ ]		p			= new float[ 3 ];
	final float[ ]		center		= new float[ 3 ];
	MouseEvent			pressEvent	= null;
	final float[ ]		m1			= FloatArrayVecmath.newIdentityMatrix( );
	final float[ ]		m2			= FloatArrayVecmath.newIdentityMatrix( );
	final float[ ]		cam			= FloatArrayVecmath.newIdentityMatrix( );
	float				lastPan		= 0;
	boolean				active		= true;
	boolean				callDisplay	= true;
	float				panFactor	= ( float ) Math.PI;
	float				tiltFactor	= ( float ) Math.PI;
	
	public BasicOrbiter( BasicGL3Scene scene )
	{
		super( );
		this.scene = scene;
	}
	
	public boolean isActive( )
	{
		return active;
	}
	
	public void setActive( boolean active )
	{
		this.active = active;
	}
	
	public boolean isCallDisplay( )
	{
		return callDisplay;
	}
	
	public void setCallDisplay( boolean callDisplay )
	{
		this.callDisplay = callDisplay;
	}
	
	public float getPanFactor( )
	{
		return panFactor;
	}
	
	public void setPanFactor( float panFactor )
	{
		this.panFactor = panFactor;
	}
	
	public float getTiltFactor( )
	{
		return tiltFactor;
	}
	
	public void setTiltFactor( float tiltFactor )
	{
		this.tiltFactor = tiltFactor;
	}
	
	@Override
	public void mousePressed( MouseEvent e )
	{
		pressEvent = e;
		lastEvent = e;
	}
	
	@Override
	public void mouseDragged( MouseEvent e )
	{
		if( !active )
		{
			return;
		}
		
		if( pressEvent.getButton( ) == MouseEvent.BUTTON1 && pressEvent.isControlDown( ) )
		{
			int dx = e.getX( ) - lastEvent.getX( );
			int dy = e.getY( ) - lastEvent.getY( );
			lastEvent = e;
			
			Component glCanvas = e.getComponent( );
			
			FloatArrayVecmath.invAffine( scene.v , cam );
			FloatArrayVecmath.mpmulAffine( cam , 0 , 0 , 0 , p );
			FloatArrayVecmath.mvmulAffine( cam , 1 , 0 , 0 , right );
			FloatArrayVecmath.mvmulAffine( cam , 0 , 1 , 0 , up );
			FloatArrayVecmath.mvmulAffine( cam , 0 , 0 , 1 , backward );
			
			FloatArrayVecmath.sub3( p , center , v );
			FloatArrayVecmath.add3( backward , v , backward );
			
			float xz = ( float ) Math.sqrt( v[ 0 ] * v[ 0 ] + v[ 2 ] * v[ 2 ] );
			
			float tilt = ( float ) Math.atan2( v[ 1 ] , xz );
			float pan = Math.abs( tilt ) == Math.PI / 2 ? lastPan : ( float ) Math.atan2( v[ 0 ] , v[ 2 ] );
			
			FloatArrayVecmath.cross( v , 0 , 1 , 0 , axis );
			FloatArrayVecmath.normalize( axis , 0 , 3 );
			
			if( axis[ 0 ] == 0 && axis[ 1 ] == 0 && axis[ 2 ] == 0 )
			{
				axis[ 0 ] = ( float ) Math.cos( lastPan );
				axis[ 2 ] = ( float ) Math.sin( lastPan );
			}
			lastPan = pan;
			
			float dpan = ( float ) ( -dx * panFactor / glCanvas.getWidth( ) );
			float dtilt = ( float ) ( dy * tiltFactor / glCanvas.getHeight( ) );
			
			FloatArrayVecmath.setIdentity( m1 );
			FloatArrayVecmath.setRotation( m1 , axis , dtilt );
			
			FloatArrayVecmath.mvmulAffine( m1 , backward );
			FloatArrayVecmath.mvmulAffine( m1 , v );
			
			FloatArrayVecmath.rotY( m1 , dpan );
			FloatArrayVecmath.mvmulAffine( m1 , v );
			FloatArrayVecmath.mvmulAffine( m1 , backward );
			
			FloatArrayVecmath.sub3( backward , v , backward );
			
			FloatArrayVecmath.cross( 0 , 1 , 0 , backward , right );
			FloatArrayVecmath.normalize( right , 0 , 3 );
			FloatArrayVecmath.cross( backward , right , up );
			
			FloatArrayVecmath.add3( v , center , p );
			
			FloatArrayVecmath.setColumn3( cam , 0 , right );
			FloatArrayVecmath.setColumn3( cam , 1 , up );
			FloatArrayVecmath.setColumn3( cam , 2 , backward );
			FloatArrayVecmath.setColumn3( cam , 3 , p );
			
			FloatArrayVecmath.invAffine( cam , scene.v );
			
			System.out.println( ArrayUtils.prettyPrint( cam , 4 , 0 , 16 , 4 , "%9.2f" ) );
		}
		
		if( callDisplay )
		{
			( ( GLAutoDrawable ) e.getComponent( ) ).display( );
		}
	}
}
