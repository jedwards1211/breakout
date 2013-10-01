package org.andork.jogl.util;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import javax.media.opengl.GLAutoDrawable;

import org.andork.util.ArrayUtils;
import org.andork.vecmath.FloatArrayVecmath;

public class BasicNavigator extends MouseAdapter
{
	final BasicGL3Scene	scene;
	
	MouseEvent			lastEvent	= null;
	float[ ]			v			= new float[ 3 ];
	MouseEvent			pressEvent	= null;
	
	final float[ ]		temp		= FloatArrayVecmath.newIdentityMatrix( );
	final float[ ]		cam			= FloatArrayVecmath.newIdentityMatrix( );
	
	float				lastPan		= 0;
	
	boolean				active		= true;
	boolean				callDisplay	= true;
	
	float				moveFactor	= 1;
	float				panFactor	= ( float ) Math.PI;
	float				tiltFactor	= ( float ) Math.PI;
	float				wheelFactor	= 5;
	
	public BasicNavigator( BasicGL3Scene scene )
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
	
	public float getMoveFactor( )
	{
		return moveFactor;
	}
	
	public void setMoveFactor( float moveFactor )
	{
		this.moveFactor = moveFactor;
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
	
	public float getWheelFactor( )
	{
		return wheelFactor;
	}
	
	public void setWheelFactor( float wheelFactor )
	{
		this.wheelFactor = wheelFactor;
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
		if( !active || pressEvent == null || pressEvent.isControlDown( ) )
		{
			return;
		}
		
		int dx = e.getX( ) - lastEvent.getX( );
		int dy = e.getY( ) - lastEvent.getY( );
		lastEvent = e;
		
		Component glCanvas = e.getComponent( );
		
		if( pressEvent.getButton( ) == MouseEvent.BUTTON1 )
		{
			FloatArrayVecmath.invAffine( scene.v , cam );
			FloatArrayVecmath.mvmulAffine( cam , 0 , 0 , 1 , v );
			
			float xz = ( float ) Math.sqrt( v[ 0 ] * v[ 0 ] + v[ 2 ] * v[ 2 ] );
			
			float tilt = ( float ) Math.atan2( v[ 1 ] , xz );
			float pan = Math.abs( tilt ) == Math.PI / 2 ? lastPan : ( float ) Math.atan2( v[ 0 ] , v[ 2 ] );
			
			lastPan = pan;
			
			float dpan = ( float ) ( dx * panFactor / glCanvas.getWidth( ) );
			float dtilt = ( float ) ( dy * tiltFactor / glCanvas.getHeight( ) );
			
			float newTilt = ( float ) Math.max( -Math.PI / 2 , Math.min( Math.PI / 2 , tilt + dtilt ) );
			
			FloatArrayVecmath.rotY( temp , dpan );
			FloatArrayVecmath.mmulRotational( temp , cam , cam );
			
			FloatArrayVecmath.mvmulAffine( cam , 1 , 0 , 0 , v );
			FloatArrayVecmath.setRotation( temp , v , dtilt );
			FloatArrayVecmath.mmulRotational( temp , cam , cam );
			
			FloatArrayVecmath.invAffine( cam , scene.v );
		}
		else if( pressEvent.getButton( ) == MouseEvent.BUTTON2 )
		{
			FloatArrayVecmath.invAffine( scene.v , cam );
			cam[ 3 ] += cam[ 2 ] * dy * moveFactor;
			cam[ 7 ] += cam[ 6 ] * dy * moveFactor;
			cam[ 11 ] += cam[ 10 ] * dy * moveFactor;
			FloatArrayVecmath.invAffine( cam , scene.v );
		}
		else if( pressEvent.getButton( ) == MouseEvent.BUTTON3 )
		{
			FloatArrayVecmath.invAffine( scene.v , cam );
			cam[ 3 ] += cam[ 0 ] * -dx * moveFactor + cam[ 1 ] * dy * moveFactor;
			cam[ 7 ] += cam[ 4 ] * -dx * moveFactor + cam[ 5 ] * dy * moveFactor;
			cam[ 11 ] += cam[ 8 ] * -dx * moveFactor + cam[ 9 ] * dy * moveFactor;
			FloatArrayVecmath.invAffine( cam , scene.v );
		}
		
		if( callDisplay )
		{
			( ( GLAutoDrawable ) e.getComponent( ) ).display( );
		}
	}
	
	@Override
	public void mouseWheelMoved( MouseWheelEvent e )
	{
		if( !active )
		{
			return;
		}
		
		float distance = e.getWheelRotation( ) * wheelFactor;
		
		cam[ 3 ] += cam[ 2 ] * distance;
		cam[ 7 ] += cam[ 6 ] * distance;
		cam[ 11 ] += cam[ 10 ] * distance;
		
		FloatArrayVecmath.invAffine( cam , scene.v );
		
		if( callDisplay )
		{
			( ( GLAutoDrawable ) e.getComponent( ) ).display( );
		}
	}
}
