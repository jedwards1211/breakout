package org.andork.torquescape.app;

import java.awt.BorderLayout;

import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLCanvas;
import javax.swing.JFrame;

import com.jogamp.opengl.util.AnimatorBase;
import com.jogamp.opengl.util.FPSAnimator;

public class TorquescapeEditorFrame extends JFrame
{
	private GLCanvas		glcanvas;
	private AnimatorBase	animator;
	
	public TorquescapeEditorFrame( )
	{
		init( );
	}
	
	private void init( )
	{
		GLProfile glprofile = GLProfile.getDefault( );
		GLCapabilities glcapabilities = new GLCapabilities( glprofile );
		glcanvas = new GLCanvas( glcapabilities );
		
		final TorquescapeRenderer renderer = new TorquescapeRenderer( );
		
		glcanvas.addGLEventListener( new GLEventListener( )
		{
			
			@Override
			public void reshape( GLAutoDrawable glautodrawable , int x , int y , int width , int height )
			{
				renderer.setup( glautodrawable.getGL( ).getGL2( ) , width , height );
			}
			
			@Override
			public void init( GLAutoDrawable glautodrawable )
			{
			}
			
			@Override
			public void dispose( GLAutoDrawable glautodrawable )
			{
			}
			
			@Override
			public void display( GLAutoDrawable glautodrawable )
			{
				renderer.update();
				renderer.render( glautodrawable.getGL( ).getGL2( ) , glautodrawable.getWidth( ) , glautodrawable.getHeight( ) );
			}
		} );
		
		getContentPane( ).add( glcanvas , BorderLayout.CENTER );
	}
	
	public void startAnimation( )
	{
		animator = new FPSAnimator( glcanvas , 60 );
		animator.start( );
	}
}
