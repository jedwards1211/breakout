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
package org.andork.jogl.neu;

import static javax.media.opengl.GL.GL_COLOR_ATTACHMENT0;
import static javax.media.opengl.GL.GL_DEPTH_ATTACHMENT;
import static javax.media.opengl.GL.GL_DEPTH_COMPONENT32;
import static javax.media.opengl.GL.GL_FRAMEBUFFER;
import static javax.media.opengl.GL.GL_RENDERBUFFER;
import static org.andork.math3d.Vecmath.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.media.opengl.GL2ES2;
import javax.media.opengl.GL3;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;

import org.andork.event.BasicPropertyChangeSupport;
import org.andork.jogl.PerspectiveProjection;
import org.andork.jogl.Projection;
import org.andork.math3d.PickXform;
import org.andork.math3d.Vecmath;

public class JoglScene implements JoglResourceManager , JoglDrawContext , GLEventListener
{
	public static final Object INITIALIZED = new Object( );

	private boolean initialized;

	/**
	 * The model matrix.
	 */
	protected final float[ ] m = newMat4f( );

	/**
	 * The normal matrix.
	 */
	protected final float[ ] n = new float[ 9 ];

	/**
	 * The view matrix.
	 */
	protected final float[ ] v = newMat4f( );

	/**
	 * The inverse of the view matrix.
	 */
	protected final float[ ] vi = newMat4f( );

	/**
	 * The projection matrix;
	 */
	protected final float[ ] p = newMat4f( );

	/**
	 * Transforms from pixel space coordinates to clipping coordinates.
	 */
	protected final float[ ] screenXform = newMat4f( );

	protected final float[ ] pixelScale = new float[ 2 ];

	private Projection pCalculator = new PerspectiveProjection( ( float ) Math.PI / 2 , 1f , 1e7f );

	private int width , height;

	private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock( );

	private final LinkedList<JoglResource> needDispose = new LinkedList<JoglResource>( );
	private final LinkedList<JoglResource> needInitialize = new LinkedList<JoglResource>( );
	private List<JoglDrawable> drawables = new ArrayList<JoglDrawable>( );
	private List<JoglDrawable> unmodifiableDrawables = Collections.unmodifiableList( drawables );

	private PickXform pickXform = new PickXform( );

	private final float[ ] bgColor =
	{ 0 , 0 , 0 , 1 };
	private boolean bgColorDirty;

	private long lastDisplay;

	private boolean renderToFbo = false;
	private int maxNumSamples = 1;
	private int desiredNumSamples = 1;
	private int currentNumSamples = 1;
	private int targetNumSamples = 1;

	private int renderingFboWidth;
	private int renderingFboHeight;

	private int renderingFbo = -1;
	private int renderingColorBuffer = -1;
	private int renderingDepthBuffer = -1;
	private int renderingFboTex = -1;

	private final BasicPropertyChangeSupport changeSupport = new BasicPropertyChangeSupport( );

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
		lock.writeLock( ).lock( );
		try
		{
			if( !Arrays.equals( this.bgColor , bgColor ) )
			{
				setf( this.bgColor , bgColor );
				bgColorDirty = true;
			}
		}
		finally
		{
			lock.writeLock( ).unlock( );
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

	public void add( JoglDrawable drawable )
	{
		drawables.add( drawable );
	}

	public void remove( JoglDrawable drawable )
	{
		drawables.remove( drawable );
	}

	public void setDrawablesDirect( List<JoglDrawable> drawables )
	{
		this.drawables = drawables;
		unmodifiableDrawables = Collections.unmodifiableList( drawables );
	}

	public List<JoglDrawable> getDrawablesDirect( )
	{
		return this.drawables;
	}

	public List<JoglDrawable> getDrawables( )
	{
		return unmodifiableDrawables;
	}

	public void initLater( JoglResource resource )
	{
		needInitialize.add( resource );
	}

	public void disposeLater( JoglResource resource )
	{
		needDispose.add( resource );
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

			if( renderingFbo < 0 || renderingFboWidth < width || renderingFboHeight < height || targetNumSamples != currentNumSamples
					|| ( elapsed >= 1000 && ( renderingFboWidth != width || renderingFboHeight != height ) ) )
			{
				destroyOffscreenBuffers( gl3 );

				int[ ] temps = new int[ 2 ];
				renderingFboWidth = width;
				renderingFboHeight = height;

				gl3.glGenFramebuffers( 1 , temps , 0 );
				renderingFbo = temps[ 0 ];
				gl3.glBindFramebuffer( GL3.GL_FRAMEBUFFER , renderingFbo );

				gl3.glGenRenderbuffers( 2 , temps , 0 );
				renderingColorBuffer = temps[ 0 ];
				renderingDepthBuffer = temps[ 1 ];

				currentNumSamples = targetNumSamples;

				// Color Render Buffer

				gl3.glBindRenderbuffer( GL_RENDERBUFFER , renderingColorBuffer );

				if( currentNumSamples > 1 )
				{
					gl3.glRenderbufferStorageMultisample( GL_RENDERBUFFER , currentNumSamples , GL3.GL_RGBA8 , renderingFboWidth , renderingFboHeight );
				}
				else
				{
					gl3.glRenderbufferStorage( GL_RENDERBUFFER , GL3.GL_RGBA8 , renderingFboWidth , renderingFboHeight );
				}

				gl3.glFramebufferRenderbuffer( GL_FRAMEBUFFER , GL_COLOR_ATTACHMENT0 , GL_RENDERBUFFER , renderingColorBuffer );

				// Depth Render Buffer

				gl3.glBindRenderbuffer( GL_RENDERBUFFER , renderingDepthBuffer );

				if( currentNumSamples > 1 )
				{
					gl3.glRenderbufferStorageMultisample( GL_RENDERBUFFER , currentNumSamples , GL_DEPTH_COMPONENT32 , renderingFboWidth , renderingFboHeight );
				}
				else
				{
					gl3.glRenderbufferStorage( GL_RENDERBUFFER , GL_DEPTH_COMPONENT32 , renderingFboWidth , renderingFboHeight );
				}

				gl3.glFramebufferRenderbuffer( GL_FRAMEBUFFER , GL_DEPTH_ATTACHMENT , GL_RENDERBUFFER , renderingDepthBuffer );
			}

			gl3.glBindFramebuffer( GL3.GL_DRAW_FRAMEBUFFER , renderingFbo );
		}

		if( bgColorDirty )
		{
			gl.glClearColor( bgColor[ 0 ] , bgColor[ 1 ] , bgColor[ 2 ] , bgColor[ 3 ] );
			bgColorDirty = false;
		}
		gl.glClear( GL2ES2.GL_COLOR_BUFFER_BIT | GL2ES2.GL_DEPTH_BUFFER_BIT );

		invAffineToTranspose3x3( m , n );

		pickXform.calculate( p , v );

		drawObjects( gl );

		if( gl instanceof GL3 && renderToFbo )
		{
			GL3 gl3 = ( GL3 ) gl;

			gl3.glBindFramebuffer( GL3.GL_DRAW_FRAMEBUFFER , 0 );
			gl3.glBindFramebuffer( GL3.GL_READ_FRAMEBUFFER , renderingFbo );
			gl3.glDrawBuffer( GL3.GL_BACK );
			gl3.glBlitFramebuffer( 0 , 0 , renderingFboWidth , renderingFboHeight , 0 , 0 , renderingFboWidth , renderingFboHeight , GL3.GL_COLOR_BUFFER_BIT ,
					GL3.GL_NEAREST );
		}
	}

	public void drawObjects( GL2ES2 gl )
	{
		JoglResource resource;

		while( ( resource = needInitialize.poll( ) ) != null )
		{
			resource.init( gl );
		}
		while( ( resource = needDispose.poll( ) ) != null )
		{
			resource.dispose( gl );
		}

		for( JoglDrawable drawable2 : drawables )
		{
			drawable2.draw( this , gl , m , n );
		}
	}

	@Override
	public void reshape( GLAutoDrawable drawable , int x , int y , int width , int height )
	{
		this.width = width;
		this.height = height;

		GL2ES2 gl = ( GL2ES2 ) drawable.getGL( );
		gl.glViewport( 0 , 0 , width , height );

		ortho( screenXform , 0 , width , 0 , height , 1 , -1 );
		pixelScale[ 0 ] = screenXform[ 0 ];
		pixelScale[ 1 ] = screenXform[ 5 ];

		recalculateProjection( );
	}

	public void recalculateProjection( )
	{
		pCalculator.calculate( this , p );
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
		invAffine( v , vi );
		recalculateProjection( );
	}

	public PickXform pickXform( )
	{
		return pickXform;
	}

	public int width( )
	{
		return width;
	}

	public int height( )
	{
		return height;
	}

	public void setProjectionCalculator( Projection calculator )
	{
		pCalculator = calculator;
		recalculateProjection( );
	}

	public Projection getProjectionCalculator( )
	{
		return pCalculator;
	}

	@Override
	public float[ ] inverseViewXform( )
	{
		return vi;
	}

	@Override
	public float[ ] viewXform( )
	{
		return v;
	}

	@Override
	public float[ ] projXform( )
	{
		return p;
	}

	@Override
	public float[ ] screenXform( )
	{
		return screenXform;
	}

	@Override
	public float[ ] pixelScale( )
	{
		return pixelScale;
	}
}
