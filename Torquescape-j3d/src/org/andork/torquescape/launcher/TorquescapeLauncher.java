package org.andork.torquescape.launcher;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.media.j3d.Appearance;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.GeometryArray;
import javax.media.j3d.Material;
import javax.media.j3d.PolygonAttributes;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.View;
import javax.swing.JFrame;
import javax.vecmath.Color3f;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

import org.andork.j3d.DebugVector;
import org.andork.j3d.Sandbox3D;
import org.andork.j3d.math.TransformComputer3f;
import org.andork.torquescape.control.CameraController;
import org.andork.torquescape.control.ControlState;
import org.andork.torquescape.control.ControlStateKeyboardHandler;
import org.andork.torquescape.model.Arena;
import org.andork.torquescape.model.Player;
import org.andork.torquescape.model.Triangle;
import org.andork.torquescape.model.TriangleBasis;
import org.andork.torquescape.model.gen.DefaultTrackSegmentGenerator;
import org.andork.torquescape.model.render.GeometryGenerator;
import org.andork.torquescape.model.render.NormalGenerator;
import org.andork.torquescape.track.Track;

import com.sun.j3d.utils.geometry.Sphere;

public class TorquescapeLauncher
{

	public static void launch(Track track) {
		DefaultTrackSegmentGenerator generator = new DefaultTrackSegmentGenerator( );
		List<List<Triangle>> outTriangles = new ArrayList<List<Triangle>>( );
		
		generator.generate( track.getXformFunction( ) , track.getSectionFunction( ) , 0 , ( float ) Math.PI * 4 , ( float ) Math.PI / 180 , outTriangles );
		
		TorquescapeLauncher.launch(outTriangles);
	}

	public static void launch(List<List<Triangle>> triangles) {
		
		final Sandbox3D sandbox = new Sandbox3D( );
		View view = sandbox.universe.getViewer( ).getView( );
		view.setFieldOfView( Math.PI * 0.6 );
		view.setBackClipDistance( view.getBackClipDistance( ) * 10 );
		
		final Arena arena = new Arena( );
		for( List<Triangle> group : triangles )
		{
			for( Triangle t : group )
			{
				arena.add( t );
			}
		} 
		
		new NormalGenerator( arena , Math.PI / 2 ).generateNormals( );
		
		for( List<Triangle> group : triangles )
		{
			GeometryArray geom = GeometryGenerator.createGeometry( group );
			
			Appearance groupApp = new Appearance( );
			groupApp.setMaterial( new Material( new Color3f( 0.3f , 0 , 0 ) , new Color3f( ) , new Color3f( 1 , 0 , 0 ) , new Color3f( 1 , 1 , 1 ) , 64f ) );
			groupApp.setPolygonAttributes( new PolygonAttributes( PolygonAttributes.POLYGON_FILL , PolygonAttributes.CULL_BACK , 0f ) );
			Shape3D groupShape = new Shape3D( geom , groupApp );
			BranchGroup bg = new BranchGroup( );
			bg.addChild( groupShape );
			sandbox.sceneRoot.addChild( bg );
		}
		
		// setupArena( torusGeom , arena );
		
		Triangle triangle = arena.getTriangles( ).iterator( ).next( );
		TriangleBasis basis = new TriangleBasis( );
		basis.set( triangle );
		final Point3f location = new Point3f( 0.25f , 0.25f , 0 );
		final Vector3f forward = new Vector3f( -.25f , -.24f , 0 );
		final Vector3f actualForward = new Vector3f( 0 , 0 , 1 );
		final Vector3f targetUp = new Vector3f( 0 , 0 , 1 );
		final Vector3f up = new Vector3f( 0 , 0 , 1 );
		final Vector3f right = new Vector3f( );
		
		basis.getUVNToXYZDirect( ).transform( location );
		basis.getUVNToXYZDirect( ).transform( forward );
		basis.getUVNToXYZDirect( ).transform( targetUp );
		basis.getUVNToXYZDirect( ).transform( up );
		
		forward.normalize( );
		up.normalize( );
		right.cross( forward , up );
		
		final Player player1 = new Player( );
		player1.setBasis( triangle );
		player1.setLocation( location );
		player1.setBasisForward( forward );
		player1.setModelForward( forward );
		player1.setBasisUp( up );
		player1.setModelUp( up );
		// player1.setVelocity( 5 );
		// player1.setAngularVelocity( Math.PI / 2 );
		arena.addPlayer( player1 );
		
		double height = 0.1;
		Sphere sphere = new Sphere( ( float ) height / 2 );
		
		final Transform3D sphereInitXform = new Transform3D( );
		final Transform3D sphereXform = new Transform3D( );
		final Transform3D x2 = new Transform3D( );
		
		sphereInitXform.setScale( new Vector3d( 2 , 1 , 4 ) );
		x2.setTranslation( new Vector3d( 0 , height / 2 , 0 ) );
		sphereInitXform.mul( x2 , sphereInitXform );
		
		final TransformComputer3f tc = new TransformComputer3f( );
		final Transform3D orientation = new Transform3D( );
		tc.orient( new Point3f( ) , new Vector3f( 0 , 0 , -1 ) , new Vector3f( 0 , 1 , 0 ) , location , forward , up , orientation );
		sphereXform.mul( orientation , sphereInitXform );
		
		final TransformGroup sphereTG = new TransformGroup( );
		sphereTG.setCapability( TransformGroup.ALLOW_TRANSFORM_WRITE );
		sphereTG.setTransform( sphereXform );
		sphereTG.addChild( sphere );
		
		BranchGroup bg2 = new BranchGroup( );
		bg2.addChild( sphereTG );
		sandbox.sceneRoot.addChild( bg2 );
		
		final Transform3D cameraInitXform = new Transform3D( );
		final Transform3D cameraXform = new Transform3D( );
		cameraInitXform.setTranslation( new Vector3d( 0 , 0.5 , 2 ) );
		x2.setIdentity( );
		x2.rotX( -Math.PI / 22 );
		cameraInitXform.mul( x2 , cameraInitXform );
		cameraXform.mul( orientation , cameraInitXform );
		sandbox.vp.getViewPlatformTransform( ).setTransform( cameraXform );
		
		sandbox.frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		sandbox.frame.setVisible( true );
		sandbox.frame.setExtendedState( JFrame.MAXIMIZED_BOTH );
		
		final ControlState controlState = new ControlState( );
		
		sandbox.canvas.addKeyListener( new ControlStateKeyboardHandler( controlState ) );
		
		final DebugVector upVector = new DebugVector( new Point3f( ) , new Vector3f( 1 , 0 , 0 ) , new Color3f( 1 , 1 , 0 ) );
		final DebugVector targetUpVector = new DebugVector( new Point3f( ) , new Vector3f( 1 , 0 , 0 ) , new Color3f( 1 , 1 , 1 ) );
		final DebugVector rightVector = new DebugVector( new Point3f( ) , new Vector3f( 1 , 0 , 0 ) , new Color3f( 0 , 1 , 0 ) );
		final DebugVector forwardVector = new DebugVector( new Point3f( ) , new Vector3f( 1 , 0 , 0 ) , new Color3f( 1 , 0 , 0 ) );
		final DebugVector actualForwardVector = new DebugVector( new Point3f( ) , new Vector3f( 1 , 0 , 0 ) , new Color3f( 1 , 0 , 1 ) );
		
		// sandbox.sceneRoot.addChild( upVector );
		// sandbox.sceneRoot.addChild( targetUpVector );
		// sandbox.sceneRoot.addChild( rightVector );
		// sandbox.sceneRoot.addChild( forwardVector );
		// sandbox.sceneRoot.addChild( actualForwardVector );
		
		final CameraController cameraController = new CameraController( arena , player1 , sandbox.vp , .25f , 10 );
		
		new javax.swing.Timer( 30 , new ActionListener( )
		{
			long	lastTimeNS	= 0;
			
			@Override
			public void actionPerformed( ActionEvent e )
			{
				long currentTime = System.nanoTime( );
				
				float acceleration, braking, leftTurning, rightTurning;
				
				synchronized( controlState )
				{
					controlState.update( currentTime );
					acceleration = controlState.acceleration;
					braking = controlState.braking;
					leftTurning = controlState.leftTurning;
					rightTurning = controlState.rightTurning;
					controlState.clear( );
				}
				
				if( lastTimeNS != 0 )
				{
					float timestep = ( float ) ( ( ( double ) currentTime - lastTimeNS ) / 1e9 );
					
					player1.updateVelocity( timestep , acceleration , braking , leftTurning , rightTurning );
					
					arena.updatePlayers( timestep );
					
					player1.getLocation( location );
					player1.getModelForward( forward );
					player1.getModelUp( up );
					right.cross( forward , up );
					right.normalize( );
					
					tc.orient( new Point3f( ) , new Vector3f( 0 , 1 , 0 ) , new Vector3f( 0 , 0 , -1 ) , location , up , forward , orientation );
					sphereXform.mul( orientation , sphereInitXform );
					
					sphereTG.setTransform( sphereXform );
					
					cameraController.updateCamera( );
					
					player1.getLocation( location );
					player1.getModelUp( up );
					player1.getBasisUp( targetUp );
					player1.getBasisForward( forward );
					right.cross( forward , up );
					actualForward.cross( up , right );
					
					float debugVecScale = 0.5f;
					up.scale( debugVecScale );
					forward.scale( debugVecScale );
					targetUp.scale( debugVecScale );
					right.scale( debugVecScale );
					actualForward.scale( debugVecScale );
					
					upVector.setVector( location , up );
					forwardVector.setVector( location , forward );
					rightVector.setVector( location , right );
					targetUpVector.setVector( location , targetUp );
					actualForwardVector.setVector( location , actualForward );
				}
				lastTimeNS = currentTime;
			}
		} ).start( );
	}
	
}
