package org.andork.j3d;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.media.j3d.AmbientLight;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.Bounds;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.Light;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.View;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;

import org.andork.j3d.camera.Camera3D;

import com.sun.j3d.exp.swing.JCanvas3D;
import com.sun.j3d.utils.behaviors.vp.OrbitBehavior;
import com.sun.j3d.utils.universe.SimpleUniverse;
import com.sun.j3d.utils.universe.ViewingPlatform;

/**
 * @author andy.edwards This was an experiment to figure out how to position the camera so that no root transform is necessary. Currently Tvd3DUniverse applies
 *         a root transform that rotates the scene so that +Z is downward. The problem with this is that the camera location coordinates are virtual world
 *         coordinates (above the root transform), unlike local scene coordinates (which are below the root transform). So it's a pain to position the camera
 *         relative to objects in the scene, or compare its location to objects in the scene. In this example, camera coordinates are in the same frame of
 *         reference as scene coordinates.
 */
public class Sandbox3D
{
	public static void main( String[ ] args )
	{
		Sandbox3D sandbox = new Sandbox3D( );
		sandbox.frame.setVisible( true );
	}
	
	public SimpleUniverse	universe;
	public JFrame			frame;
	public ViewingPlatform	vp;
	public Canvas3D			canvas;
	public Camera3D			camera;
	public XyzAxes2			axes;
	public AmbientLight		ambientLight;
	public Bounds			worldBounds;
	public BranchGroup		worldRoot;
	public TransformGroup	sceneTrans;
	public BranchGroup		sceneRoot;
	public OrbitBehavior	orbiter;
	
	private static Canvas3D createDefaultCanvas( )
	{
		final GraphicsConfiguration config = SimpleUniverse.getPreferredConfiguration( );
		Canvas3D canvas = new Canvas3D( config );
		return canvas;
	}
	
	public Sandbox3D( )
	{
		this( createDefaultCanvas( ) );
	}
	
	public Sandbox3D( final JCanvas3D canvas )
	{
		frame = new JFrame( );
		frame.getContentPane( ).setLayout( new BorderLayout( ) );
		
		canvas.setPreferredSize( new Dimension( 400 , 400 ) );
		canvas.setSize( new Dimension( 400 , 400 ) );
		frame.getContentPane( ).add( "Center" , canvas );
		
		// final GraphicsConfiguration config = SimpleUniverse.getPreferredConfiguration( );
		// canvas = new Canvas3D( config );
		this.canvas = canvas.getOffscreenCanvas3D( );
		
		universe = new SimpleUniverse( canvas.getOffscreenCanvas3D( ) );
		
		vp = universe.getViewingPlatform( );
		camera = new Camera3D( vp );
		camera.setLocation( new Point3d( 0 , 2 , -10 ) , true );
		camera.lookAt( new Point3d( 0 , 0 , 0 ) , true );
		
		sceneRoot = new BranchGroup( );
		sceneRoot.setCapability( BranchGroup.ALLOW_CHILDREN_EXTEND );
		sceneRoot.setCapability( BranchGroup.ALLOW_CHILDREN_READ );
		sceneRoot.setCapability( BranchGroup.ALLOW_CHILDREN_WRITE );
		axes = new XyzAxes2( .05f , 1f );
		sceneRoot.addChild( axes );
		
		ambientLight = new AmbientLight( new Color3f( .3f , .3f , .3f ) );
		worldBounds = new BoundingSphere( new Point3d( ) , 10000.0 );
		ambientLight.setInfluencingBounds( worldBounds );
		ambientLight.setCapability( Light.ALLOW_STATE_WRITE );
		sceneRoot.addChild( ambientLight );
		
		sceneTrans = new TransformGroup( );
		sceneTrans.setCapability( TransformGroup.ALLOW_TRANSFORM_WRITE );
		sceneTrans.addChild( sceneRoot );
		
		worldRoot = new BranchGroup( );
		worldRoot.addChild( sceneTrans );
		
		orbiter = new OrbitBehavior( universe.getCanvas( ) );
		orbiter.setSchedulingBounds( worldBounds );
		vp.setViewPlatformBehavior( orbiter );
		
		universe.addBranchGraph( worldRoot );
		
		JButton disposeButton = new JButton( "Dispose" );
		disposeButton.addActionListener( new ActionListener( )
		{
			@Override
			public void actionPerformed( ActionEvent e )
			{
				frame.getContentPane( ).removeAll( );
				universe.cleanup( );
				universe = null;
				Sandbox3D.this.canvas = null;
				vp = null;
				camera = null;
				axes = null;
				ambientLight = null;
				worldBounds = null;
				worldRoot = null;
				sceneTrans = null;
				sceneRoot = null;
				orbiter = null;
			}
		} );
		
		frame.add( disposeButton , BorderLayout.NORTH );
		
		frame.pack( );
	}
	
	public Sandbox3D( Canvas3D canvas )
	{
		frame = new JFrame( );
		frame.getContentPane( ).setLayout( new BorderLayout( ) );
		
		// final GraphicsConfiguration config = SimpleUniverse.getPreferredConfiguration( );
		// canvas = new Canvas3D( config );
		this.canvas = canvas;
		canvas.setPreferredSize( new Dimension( 400 , 400 ) );
		frame.getContentPane( ).add( "Center" , canvas );
		
		universe = new SimpleUniverse( canvas );
		
		vp = universe.getViewingPlatform( );
		camera = new Camera3D( vp );
		camera.setLocation( new Point3d( 0 , 2 , 0 ) , true );
		camera.lookAt( new Point3d( 0 , 0 , 0 ) , true );
		
		sceneRoot = new BranchGroup( );
		sceneRoot.setCapability( BranchGroup.ALLOW_CHILDREN_EXTEND );
		sceneRoot.setCapability( BranchGroup.ALLOW_CHILDREN_READ );
		sceneRoot.setCapability( BranchGroup.ALLOW_CHILDREN_WRITE );
		axes = new XyzAxes2( .05f , 1f );
		sceneRoot.addChild( axes );
		
		ambientLight = new AmbientLight( new Color3f( .3f , .3f , .3f ) );
		worldBounds = new BoundingSphere( new Point3d( ) , 10000.0 );
		ambientLight.setInfluencingBounds( worldBounds );
		ambientLight.setCapability( Light.ALLOW_STATE_WRITE );
		sceneRoot.addChild( ambientLight );
		
		sceneTrans = new TransformGroup( );
		sceneTrans.setCapability( TransformGroup.ALLOW_TRANSFORM_WRITE );
		sceneTrans.addChild( sceneRoot );
		
		worldRoot = new BranchGroup( );
		worldRoot.addChild( sceneTrans );
		
		orbiter = new OrbitBehavior( universe.getCanvas( ) );
		orbiter.setSchedulingBounds( worldBounds );
		vp.setViewPlatformBehavior( orbiter );
		
		universe.addBranchGraph( worldRoot );
		
		JButton disposeButton = new JButton( "Dispose" );
		disposeButton.addActionListener( new ActionListener( )
		{
			@Override
			public void actionPerformed( ActionEvent e )
			{
				frame.getContentPane( ).removeAll( );
				universe.cleanup( );
				universe = null;
				Sandbox3D.this.canvas = null;
				vp = null;
				camera = null;
				axes = null;
				ambientLight = null;
				worldBounds = null;
				worldRoot = null;
				sceneTrans = null;
				sceneRoot = null;
				orbiter = null;
			}
		} );
		
		frame.add( disposeButton , BorderLayout.NORTH );
		
		frame.pack( );
	}
	
	/**
	 * @param scale
	 */
	public void scaleParameters( double scale )
	{
		View v = universe.getViewer( ).getView( );
		v.setBackClipDistance( v.getBackClipDistance( ) * scale );
		v.setFrontClipDistance( v.getFrontClipDistance( ) * scale );
		
		orbiter.setTransFactors( orbiter.getTransXFactor( ) * scale , orbiter.getTransYFactor( ) * scale );
		orbiter.setZoomFactor( orbiter.getZoomFactor( ) * scale );
	}
}
