package org.andork.jogl.basic;

import org.andork.vecmath.Vecmath;

import com.jogamp.newt.Window;
import com.jogamp.newt.event.MouseAdapter;
import com.jogamp.newt.event.MouseEvent;
import com.jogamp.newt.opengl.GLWindow;

public class BasicNavigator extends MouseAdapter
{
	final GLWindow			window;
	final BasicJOGLScene	scene;
	
	MouseEvent				lastEvent	= null;
	float[ ]				v			= new float[ 3 ];
	MouseEvent				pressEvent	= null;
	
	final float[ ]			temp		= Vecmath.newMat4f( );
	final float[ ]			cam			= Vecmath.newMat4f( );
	
	float					lastPan		= 0;
	
	boolean					active		= true;
	boolean					callDisplay	= true;
	
	float					moveFactor	= 0.05f;
	float					panFactor	= ( float ) Math.PI;
	float					tiltFactor	= ( float ) Math.PI;
	float					wheelFactor	= 1f;
	
	float					sensitivity	= 1f;
	
	public BasicNavigator( BasicJOGLSetup setup )
	{
		super( );
		this.window = setup.glWindow;
		this.scene = setup.scene;
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
		
		scene.getViewXform( cam );
		Vecmath.invAffine( cam );
		
		Window window = ( Window ) e.getSource( );
		
		float scaledMoveFactor = moveFactor * sensitivity;
		
		if( pressEvent.getButton( ) == MouseEvent.BUTTON1 )
		{
			Vecmath.mvmulAffine( cam , 0 , 0 , 1 , v );
			
			float xz = ( float ) Math.sqrt( v[ 0 ] * v[ 0 ] + v[ 2 ] * v[ 2 ] );
			
			float tilt = ( float ) Math.atan2( v[ 1 ] , xz );
			float pan = Math.abs( tilt ) == Math.PI / 2 ? lastPan : ( float ) Math.atan2( v[ 0 ] , v[ 2 ] );
			
			lastPan = pan;
			
			float dpan = ( float ) ( dx * panFactor * sensitivity / window.getWidth( ) );
			float dtilt = ( float ) ( dy * tiltFactor * sensitivity / window.getHeight( ) );
			
			Vecmath.rotY( temp , dpan );
			Vecmath.mmulRotational( temp , cam , cam );
			
			Vecmath.mvmulAffine( cam , 1 , 0 , 0 , v );
			Vecmath.setRotation( temp , v , dtilt );
			Vecmath.mmulRotational( temp , cam , cam );
			
			Vecmath.invAffine( cam );
			scene.setViewXform( cam );
		}
		else if( pressEvent.getButton( ) == MouseEvent.BUTTON2 )
		{
			cam[ 12 ] += cam[ 8 ] * dy * scaledMoveFactor;
			cam[ 13 ] += cam[ 9 ] * dy * scaledMoveFactor;
			cam[ 14 ] += cam[ 10 ] * dy * scaledMoveFactor;
			Vecmath.invAffine( cam );
			scene.setViewXform( cam );
		}
		else if( pressEvent.getButton( ) == MouseEvent.BUTTON3 )
		{
			cam[ 12 ] += cam[ 0 ] * -dx * scaledMoveFactor + cam[ 4 ] * dy * scaledMoveFactor;
			cam[ 13 ] += cam[ 1 ] * -dx * scaledMoveFactor + cam[ 5 ] * dy * scaledMoveFactor;
			cam[ 14 ] += cam[ 2 ] * -dx * scaledMoveFactor + cam[ 6 ] * dy * scaledMoveFactor;
			Vecmath.invAffine( cam );
			scene.setViewXform( cam );
		}
		
		if( callDisplay )
		{
			this.window.display( );
		}
	}
	
	@Override
	public void mouseWheelMoved( MouseEvent e )
	{
		if( !active || e.isControlDown( ) )
		{
			return;
		}
		
		scene.getViewXform( cam );
		Vecmath.invAffine( cam );
		
		float distance = -e.getRotation( )[ 1 ] * wheelFactor * sensitivity;
		
		cam[ 12 ] += cam[ 8 ] * distance;
		cam[ 13 ] += cam[ 9 ] * distance;
		cam[ 14 ] += cam[ 10 ] * distance;
		
		Vecmath.invAffine( cam );
		scene.setViewXform( cam );
		
		if( callDisplay )
		{
			this.window.display( );
		}
	}
}
