package org.andork.jogl.awt;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.media.opengl.awt.GLCanvas;
import javax.swing.Timer;

import org.andork.jogl.BasicJOGLScene;

import static org.andork.math3d.Vecmath.*;
import static org.andork.util.AnimationUtils.*;

public class BasicCameraAnimationController
{
	long			lastTargetChangeTime	= 0;
	float[ ]		current					= newMat4f( );
	float[ ]		target					= newMat4f( );
	
	BasicJOGLScene	scene;
	GLCanvas		canvas;
	
	long			lastAnimTime;
	float			factor;
	float			extra;
	int				delay;
	
	Timer			timer;
	
	public BasicCameraAnimationController( BasicJOGLSetup setup )
	{
		this( setup , .1f , 5f , 30 );
	}
	
	public BasicCameraAnimationController( BasicJOGLSetup setup , float factor , float extra , int delay )
	{
		this.scene = setup.getScene( );
		this.canvas = setup.getCanvas( );
		
		setFactor( factor );
		setExtra( extra );
		setDelay( delay );
		
		timer = new Timer( delay , new AnimationHandler( ) );
	}
	
	public float getFactor( )
	{
		return factor;
	}
	
	public void setFactor( float factor )
	{
		if( factor < 0 )
		{
			throw new IllegalArgumentException( "factor must be >= 0" );
		}
		this.factor = factor;
	}
	
	public float getExtra( )
	{
		return extra;
	}
	
	public void setExtra( float extra )
	{
		if( extra < 0 )
		{
			throw new IllegalArgumentException( "extra must be >= 0" );
		}
		this.extra = extra;
	}
	
	public int getDelay( )
	{
		return delay;
	}
	
	public void setDelay( int delay )
	{
		if( delay < 0 )
		{
			throw new IllegalArgumentException( "delay must be >= 0" );
		}
		this.delay = delay;
	}
	
	public void setTarget( float[ ] target )
	{
		setf( this.target , target );
		lastTargetChangeTime = System.currentTimeMillis( );
		
		if( timer != null )
		{
			timer.start( );
		}
	}
	
	private class AnimationHandler implements ActionListener
	{
		@Override
		public void actionPerformed( ActionEvent e )
		{
			if( scene == null || lastTargetChangeTime < scene.getLastViewXformChange( ) )
			{
				lastAnimTime = 0;
				if( timer != null )
				{
					timer.stop( );
				}
				return;
			}
			
			scene.getViewXform( current );
			
			long time = System.currentTimeMillis( );
			
			long elapsedTime;
			if( lastAnimTime > 0 )
			{
				elapsedTime = time - lastAnimTime;
			}
			else
			{
				elapsedTime = delay;
			}
			lastAnimTime = time;
			
			current[ 12 ] = animate( current[ 12 ] , target[ 12 ] , elapsedTime , factor , extra , delay );
			current[ 13 ] = animate( current[ 13 ] , target[ 13 ] , elapsedTime , factor , extra , delay );
			current[ 14 ] = animate( current[ 14 ] , target[ 14 ] , elapsedTime , factor , extra , delay );
			
			if( current[ 12 ] == target[ 12 ] && current[ 13 ] == target[ 13 ] && current[ 14 ] == target[ 14 ] )
			{
				lastAnimTime = 0;
				if( timer != null )
				{
					timer.stop( );
				}
			}
			
			scene.setViewXform( current );
			
			if( canvas != null )
			{
				canvas.display( );
			}
			
			lastTargetChangeTime = System.currentTimeMillis( );
		}
	}
}
