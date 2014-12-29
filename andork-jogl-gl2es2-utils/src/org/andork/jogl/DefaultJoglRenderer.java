package org.andork.jogl;

import javax.media.opengl.GL2ES2;
import javax.media.opengl.GL3;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;

import static javax.media.opengl.GL3.*;
import static org.andork.math3d.Vecmath.*;

public class DefaultJoglRenderer implements GLEventListener
{
	protected JoglViewState		viewState			= new JoglViewState( );
	protected JoglViewSettings	viewSettings		= new JoglViewSettings( );
	protected JoglScene			scene;
	protected GL3Framebuffer	framebuffer;

	protected int				desiredNumSamples	= 1;

	protected float[ ]			m					= newMat4f( );
	protected float[ ]			n					= newMat3f( );

	public DefaultJoglRenderer( JoglScene scene )
	{
		super( );
		this.scene = scene;
	}

	public DefaultJoglRenderer( GL3Framebuffer framebuffer , int desiredNumSamples )
	{
		super( );
		this.framebuffer = framebuffer;
		this.desiredNumSamples = desiredNumSamples;
	}

	public DefaultJoglRenderer( JoglScene scene , GL3Framebuffer framebuffer , int desiredNumSamples )
	{
		super( );
		this.scene = scene;
		this.framebuffer = framebuffer;
		this.desiredNumSamples = desiredNumSamples;
	}

	public JoglViewState getViewState( )
	{
		return viewState;
	}

	public JoglViewSettings getViewSettings( )
	{
		return viewSettings;
	}

	public void setViewSettings( JoglViewSettings viewSettings )
	{
		this.viewSettings = viewSettings;
	}

	public JoglScene getScene( )
	{
		return scene;
	}

	public void setScene( JoglScene scene )
	{
		this.scene = scene;
	}

	public int getDesiredNumSamples( )
	{
		return desiredNumSamples;
	}

	public void setDesiredNumSamples( int desiredNumSamples )
	{
		this.desiredNumSamples = desiredNumSamples;
	}

	@Override
	public void init( GLAutoDrawable drawable )
	{
		GL2ES2 gl = ( GL2ES2 ) drawable.getGL( );

		if( framebuffer != null )
		{
			framebuffer.init( ( GL3 ) gl );
		}
	}

	@Override
	public void dispose( GLAutoDrawable drawable )
	{
		GL2ES2 gl = ( GL2ES2 ) drawable.getGL( );

		if( framebuffer != null )
		{
			framebuffer.dispose( ( GL3 ) gl );
		}
	}

	@Override
	public void display( GLAutoDrawable drawable )
	{
		GL2ES2 gl = ( GL2ES2 ) drawable.getGL( );

		int renderingFbo = -1;

		if( framebuffer != null )
		{
			GL3 gl3 = ( GL3 ) gl;
			renderingFbo = framebuffer.renderingFbo( gl3 , drawable.getSurfaceWidth( ) , drawable.getSurfaceHeight( ) ,
				desiredNumSamples );
			gl3.glBindFramebuffer( GL_DRAW_FRAMEBUFFER , renderingFbo );
		}

		viewState.update( viewSettings , drawable.getSurfaceWidth( ) , drawable.getSurfaceHeight( ) );

		drawScene( drawable );

		if( framebuffer != null )
		{
			GL3 gl3 = ( GL3 ) gl;

			gl3.glBindFramebuffer( GL_DRAW_FRAMEBUFFER , 0 );
			gl3.glBindFramebuffer( GL_READ_FRAMEBUFFER , renderingFbo );
			gl3.glDrawBuffer( GL_BACK );
			gl3.glBlitFramebuffer( 0 , 0 , drawable.getSurfaceWidth( ) , drawable.getSurfaceHeight( ) , 0 , 0 ,
				drawable.getSurfaceWidth( ) , drawable.getSurfaceHeight( ) , GL3.GL_COLOR_BUFFER_BIT , GL_NEAREST );
		}
	}

	protected void drawScene( GLAutoDrawable drawable )
	{
		if( scene != null )
		{
			scene.draw( viewState , (GL2ES2) drawable.getGL( ) , m , n );
		}
	}

	@Override
	public void reshape( GLAutoDrawable drawable , int x , int y , int width , int height )
	{
		GL2ES2 gl = ( GL2ES2 ) drawable.getGL( );
		gl.glViewport( 0 , 0 , width , height );
	}
}
