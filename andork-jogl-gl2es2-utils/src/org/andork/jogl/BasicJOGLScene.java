/*******************************************************************************
 * Breakout Cave Survey Visualizer
 *
 * Copyright (C) 2014 James Edwards
 *
 * jedwards8 at fastmail dot fm
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *******************************************************************************/
package org.andork.jogl;

import static javax.media.opengl.GL.GL_COLOR_ATTACHMENT0;
import static javax.media.opengl.GL.GL_DEPTH_ATTACHMENT;
import static javax.media.opengl.GL.GL_DEPTH_COMPONENT32;
import static javax.media.opengl.GL.GL_FRAMEBUFFER;
import static javax.media.opengl.GL.GL_RENDERBUFFER;
import static org.andork.math3d.Vecmath.invAffineToTranspose3x3;
import static org.andork.math3d.Vecmath.newMat4f;
import static org.andork.math3d.Vecmath.setf;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import javax.media.opengl.GL2ES2;
import javax.media.opengl.GL3;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;

import org.andork.event.BasicPropertyChangeSupport;
import org.andork.math3d.PickXform;
import org.andork.math3d.Vecmath;

public class BasicJOGLScene implements GLEventListener
{
	public static final Object					INITIALIZED				= new Object( );
	
	private boolean								initialized;
	
	/**
	 * The model matrix.
	 */
	private final float[ ]						m						= newMat4f( );
	
	/**
	 * The normal matrix.
	 */
	private final float[ ]						n						= new float[ 9 ];
	
	/**
	 * The view matrix.
	 */
	private final float[ ]						v						= newMat4f( );
	
	/**
	 * The projection matrix;
	 */
	private final float[ ]						p						= newMat4f( );
	
	private OldProjectionCalculator				pCalculator				= new PerspectiveOldProjectionCalculator( ( float ) Math.PI / 2 , 1f , 1e7f );
	
	private int									width , height;
	
	private final List<JOGLObject>				objects					= new ArrayList<JOGLObject>( );
	private final List<JOGLObject>				unmodifiableObjects		= Collections.unmodifiableList( objects );
	
	private final Queue<JOGLObject>				objectsThatNeedInit		= new LinkedList<JOGLObject>( );
	private final Queue<JOGLObject>				objectsThatNeedDestroy	= new LinkedList<JOGLObject>( );
	private final Queue<JOGLRunnable>			doLaters				= new LinkedList<JOGLRunnable>( );
	
	private PickXform							pickXform				= new PickXform( );
	
	private final float[ ]						bgColor					= { 0 , 0 , 0 , 1 };
	private boolean								bgColorDirty;
	
	private long								lastDisplay;
	
	private boolean								renderToFbo				= false;
	private int									maxNumSamples			= 1;
	private int									desiredNumSamples		= 1;
	private int									currentNumSamples		= 1;
	private int									targetNumSamples		= 1;
	
	private int									renderingFboWidth;
	private int									renderingFboHeight;
	
	private int									renderingFbo			= -1;
	private int									renderingColorBuffer	= -1;
	private int									renderingDepthBuffer	= -1;
	private int									renderingFboTex			= -1;
	
	private final BasicPropertyChangeSupport	changeSupport			= new BasicPropertyChangeSupport( );
	
	public BasicPropertyChangeSupport.External changeSupport( )
	{
		return changeSupport.external( );
	}
	
	public boolean isInitialized( )
	{
		return initialized;
	}
	
	public int getMaxNumSamples( )
	{
		return maxNumSamples;
	}
	
	private void updateTargetNumSamples( )
	{
		targetNumSamples = Math.max( 1 , Math.min( desiredNumSamples , maxNumSamples ) );
	}
	
	public void setBgColor( float ... bgColor )
	{
		if( !Arrays.equals( this.bgColor , bgColor ) )
		{
			setf( this.bgColor , bgColor );
			bgColorDirty = true;
		}
	}
	
	public float[ ] getBgColor( float[ ] out )
	{
		setf( out , this.bgColor );
		return out;
	}
	
	public float[ ] getBgColor( )
	{
		return Arrays.copyOf( bgColor , bgColor.length );
	}
	
	public boolean isRenderToFbo( )
	{
		return renderToFbo;
	}
	
	public void setRenderToFbo( boolean renderToFbo )
	{
		this.renderToFbo = renderToFbo;
	}
	
	public int getDesiredNumSamples( )
	{
		return desiredNumSamples;
	}
	
	public void setDesiredNumSamples( int numSamples )
	{
		this.desiredNumSamples = numSamples;
		updateTargetNumSamples( );
	}
	
	public BasicJOGLScene add( JOGLObject object )
	{
		objects.add( object );
		return this;
	}
	
	public BasicJOGLScene remove( JOGLObject object )
	{
		objects.remove( object );
		return this;
	}
	
	public BasicJOGLScene initLater( JOGLObject object )
	{
		objectsThatNeedInit.add( object );
		return this;
	}
	
	public BasicJOGLScene destroyLater( JOGLObject object )
	{
		objectsThatNeedDestroy.add( object );
		return this;
	}
	
	public BasicJOGLScene doLater( JOGLRunnable runnable )
	{
		doLaters.add( runnable );
		return this;
	}
	
	@Override
	public void init( GLAutoDrawable drawable )
	{
		if( initialized )
		{
			return;
		}
		initialized = true;
		
		GL2ES2 gl = ( GL2ES2 ) drawable.getGL( );
		
		if( gl instanceof GL3 )
		{
			int[ ] temp = new int[ 1 ];
			( ( GL3 ) gl ).glGetIntegerv( GL3.GL_MAX_SAMPLES , temp , 0 );
			maxNumSamples = temp[ 0 ];
		}
		else
		{
			maxNumSamples = 1;
		}
		updateTargetNumSamples( );
		
		for( JOGLObject object : objects )
		{
			object.init( gl );
		}
		
		changeSupport.firePropertyChange( this , INITIALIZED , false , true );
	}
	
	@Override
	public void dispose( GLAutoDrawable drawable )
	{
		if( !initialized )
		{
			return;
		}
		initialized = false;
		
		GL2ES2 gl = ( GL2ES2 ) drawable.getGL( );
		
		if( gl instanceof GL3 && renderToFbo )
		{
			GL3 gl3 = ( GL3 ) gl;
			destroyOffscreenBuffers( gl3 );
		}
		
		changeSupport.firePropertyChange( this , INITIALIZED , true , false );
	}
	
	private void destroyOffscreenBuffers( GL3 gl3 )
	{
		int[ ] temps = new int[ 1 ];
		if( renderingFbo >= 0 )
		{
			temps[ 0 ] = renderingFbo;
			gl3.glDeleteFramebuffers( 1 , temps , 0 );
			renderingFbo = -1;
		}
		if( renderingFboTex >= 0 )
		{
			temps[ 0 ] = renderingFboTex;
			gl3.glDeleteTextures( 1 , temps , 0 );
			renderingFboTex = -1;
		}
		if( renderingColorBuffer >= 0 )
		{
			temps[ 0 ] = renderingColorBuffer;
			gl3.glDeleteRenderbuffers( 1 , temps , 0 );
			renderingColorBuffer = -1;
		}
		if( renderingDepthBuffer >= 0 )
		{
			temps[ 0 ] = renderingDepthBuffer;
			gl3.glDeleteRenderbuffers( 1 , temps , 0 );
			renderingDepthBuffer = -1;
		}
	}
	
	@Override
	public void display( GLAutoDrawable drawable )
	{
		long lastDisplay = this.lastDisplay;
		this.lastDisplay = System.currentTimeMillis( );
		long elapsed = this.lastDisplay - lastDisplay;
		
		GL2ES2 gl = ( GL2ES2 ) drawable.getGL( );
		
		if( gl instanceof GL3 && renderToFbo )
		{
			GL3 gl3 = ( GL3 ) gl;
			
			if( renderingFbo < 0 || renderingFboWidth < width || renderingFboHeight < height || targetNumSamples != currentNumSamples ||
					( elapsed >= 1000 && ( renderingFboWidth != width || renderingFboHeight != height ) ) )
			{
				destroyOffscreenBuffers( gl3 );
				
				int[ ] temps = new int[ 1 ];
				renderingFboWidth = width;
				renderingFboHeight = height;
				
				gl3.glGenFramebuffers( 1 , temps , 0 );
				renderingFbo = temps[ 0 ];
				gl3.glBindFramebuffer( GL3.GL_FRAMEBUFFER , renderingFbo );
				
				gl3.glGenRenderbuffers( 1 , temps , 0 );
				renderingDepthBuffer = temps[ 0 ];
				
				currentNumSamples = targetNumSamples;
				
				if( currentNumSamples > 1 )
				{
					gl3.glGenTextures( 1 , temps , 0 );
					renderingFboTex = temps[ 0 ];
					gl3.glBindTexture( GL3.GL_TEXTURE_2D_MULTISAMPLE , renderingFboTex );
					
					gl3.glTexImage2DMultisample( GL3.GL_TEXTURE_2D_MULTISAMPLE , currentNumSamples , GL3.GL_RGBA8 , renderingFboWidth , renderingFboHeight , false );
					gl3.glFramebufferTexture2D( GL3.GL_FRAMEBUFFER , GL3.GL_COLOR_ATTACHMENT0 , GL3.GL_TEXTURE_2D_MULTISAMPLE , renderingFboTex , 0 );
					
					gl3.glBindRenderbuffer( GL_RENDERBUFFER , renderingDepthBuffer );
					gl3.glRenderbufferStorageMultisample( GL_RENDERBUFFER , currentNumSamples , GL_DEPTH_COMPONENT32 , renderingFboWidth , renderingFboHeight );
					gl3.glFramebufferRenderbuffer( GL_FRAMEBUFFER , GL_DEPTH_ATTACHMENT , GL_RENDERBUFFER , renderingDepthBuffer );
				}
				else
				{
					gl3.glGenRenderbuffers( 1 , temps , 0 );
					renderingColorBuffer = temps[ 0 ];
					gl3.glBindRenderbuffer( GL_RENDERBUFFER , renderingColorBuffer );
					gl3.glRenderbufferStorage( GL_RENDERBUFFER , GL3.GL_RGBA8 , renderingFboWidth , renderingFboHeight );
					gl3.glFramebufferRenderbuffer( GL_FRAMEBUFFER , GL_COLOR_ATTACHMENT0 , GL_RENDERBUFFER , renderingColorBuffer );
					
					gl3.glBindRenderbuffer( GL_RENDERBUFFER , renderingDepthBuffer );
					gl3.glRenderbufferStorage( GL_RENDERBUFFER , GL_DEPTH_COMPONENT32 , renderingFboWidth , renderingFboHeight );
					gl3.glFramebufferRenderbuffer( GL_FRAMEBUFFER , GL_DEPTH_ATTACHMENT , GL_RENDERBUFFER , renderingDepthBuffer );
				}
				
			}
			
			gl3.glBindFramebuffer( GL3.GL_DRAW_FRAMEBUFFER , renderingFbo );
		}
		
		if( bgColorDirty )
		{
			gl.glClearColor( bgColor[ 0 ] , bgColor[ 1 ] , bgColor[ 2 ] , bgColor[ 3 ] );
			bgColorDirty = false;
		}
		gl.glClear( GL2ES2.GL_COLOR_BUFFER_BIT | GL2ES2.GL_DEPTH_BUFFER_BIT );
		
		while( !objectsThatNeedInit.isEmpty( ) )
		{
			objectsThatNeedInit.poll( ).init( gl );
		}
		
		while( !objectsThatNeedDestroy.isEmpty( ) )
		{
			objectsThatNeedDestroy.poll( ).destroy( gl );
		}
		
		invAffineToTranspose3x3( m , n );
		
		pickXform.calculate( p , v );
		
		while( !doLaters.isEmpty( ) )
		{
			try
			{
				doLaters.poll( ).run( gl );
			}
			catch( Exception ex )
			{
				ex.printStackTrace( );
			}
		}
		
		drawObjects( gl );
		
		if( gl instanceof GL3 && renderToFbo )
		{
			GL3 gl3 = ( GL3 ) gl;
			
			gl3.glBindFramebuffer( GL3.GL_DRAW_FRAMEBUFFER , 0 );
			gl3.glBindFramebuffer( GL3.GL_READ_FRAMEBUFFER , renderingFbo );
			gl3.glDrawBuffer( GL3.GL_BACK );
			gl3.glBlitFramebuffer( 0 , 0 , renderingFboWidth , renderingFboHeight , 0 , 0 , renderingFboWidth , renderingFboHeight , GL3.GL_COLOR_BUFFER_BIT , GL3.GL_NEAREST );
		}
	}
	
	public void drawObjects( GL2ES2 gl )
	{
		for( JOGLObject object : objects )
		{
			object.draw( gl , m , n , v , p );
		}
	}
	
	@Override
	public void reshape( GLAutoDrawable drawable , int x , int y , int width , int height )
	{
		this.width = width;
		this.height = height;
		
		GL2ES2 gl = ( GL2ES2 ) drawable.getGL( );
		gl.glViewport( 0 , 0 , width , height );
		
		recalculateProjection( );
	}
	
	public void recalculateProjection( )
	{
		pCalculator.calculate( width , height , p );
	}
	
	public List<JOGLObject> getObjects( )
	{
		return unmodifiableObjects;
	}
	
	public void clear( )
	{
		objects.clear( );
	}
	
	public void getModelXform( float[ ] out )
	{
		System.arraycopy( m , 0 , out , 0 , 16 );
	}
	
	public void setModelXform( float[ ] m )
	{
		if( Vecmath.hasNaNsOrInfinites( m ) )
		{
			throw new IllegalArgumentException( "m must not contain NaN or Infinite values" );
		}
		
		System.arraycopy( m , 0 , this.m , 0 , 16 );
	}
	
	public void getViewXform( float[ ] out )
	{
		System.arraycopy( v , 0 , out , 0 , 16 );
	}
	
	public void setViewXform( float[ ] v )
	{
		if( Vecmath.hasNaNsOrInfinites( v ) )
		{
			throw new IllegalArgumentException( "v must not contain NaN or Infinite values" );
		}
		
		System.arraycopy( v , 0 , this.v , 0 , 16 );
	}
	
	public PickXform pickXform( )
	{
		return pickXform;
	}
	
	public int getWidth( )
	{
		return width;
	}
	
	public int getHeight( )
	{
		return height;
	}
	
	public void setProjectionCalculator( OldProjectionCalculator calculator )
	{
		if( pCalculator != calculator )
		{
			pCalculator = calculator;
			recalculateProjection( );
		}
	}
	
	public OldProjectionCalculator getProjectionCalculator( )
	{
		return pCalculator;
	}
}
