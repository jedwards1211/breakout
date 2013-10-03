package org.andork.j3d;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GraphicsConfiguration;

import javax.media.j3d.AmbientLight;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.Bounds;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.Light;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.View;
import javax.swing.JFrame;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;

import org.andork.j3d.camera.Camera3D;

import com.sun.j3d.exp.swing.JCanvas3D;
import com.sun.j3d.utils.behaviors.vp.OrbitBehavior;
import com.sun.j3d.utils.universe.SimpleUniverse;
import com.sun.j3d.utils.universe.ViewingPlatform;

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
	public Component		canvasComponent;
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
		this.canvasComponent = canvas;
		this.canvas = canvas.getOffscreenCanvas3D( );
		
		init( );
	}
	
	public Sandbox3D( Canvas3D canvas )
	{
		this.canvasComponent = canvas;
		this.canvas = canvas;
		
		init( );
	}
	
	private void init( )
	{
		canvasComponent.setPreferredSize( new Dimension( 400 , 400 ) );
		canvasComponent.setSize( new Dimension( 400 , 400 ) );
		universe = new SimpleUniverse( this.canvas );
		
		frame = new JFrame( );
		frame.getContentPane( ).setLayout( new BorderLayout( ) );
		
		frame.getContentPane( ).add( BorderLayout.CENTER , canvas );
		
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
