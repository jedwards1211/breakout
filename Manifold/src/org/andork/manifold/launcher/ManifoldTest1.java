package org.andork.manifold.launcher;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.LinkedList;

import javax.media.j3d.Appearance;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.GeometryArray;
import javax.media.j3d.IndexedGeometryArray;
import javax.media.j3d.IndexedTriangleArray;
import javax.media.j3d.Material;
import javax.media.j3d.PolygonAttributes;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.View;
import javax.swing.JFrame;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

import org.andork.j3d.DebugVector;
import org.andork.j3d.Sandbox3D;
import org.andork.j3d.math.TransformComputer3d;
import org.andork.manifold.model.Arena;
import org.andork.manifold.model.Player;
import org.andork.manifold.model.Triangle;
import org.andork.manifold.model.TriangleBasis;
import org.andork.math3d.EdgeNormalComputer;

import com.sun.j3d.utils.geometry.Sphere;

public class ManifoldTest1
{
	public static void main( String[ ] args )
	{
		final Sandbox3D sandbox = new Sandbox3D( );
		View view = sandbox.universe.getViewer( ).getView( );
		view.setBackClipDistance( view.getBackClipDistance( ) * 10 );
		// IndexedGeometryArray torusGeom = createTorus( 5 , 180 , 0.5 , 72 );
		IndexedGeometryArray torusGeom = createTorus( 50 , 720 , 2 , 3 );
		
		final Arena arena = new Arena( );
		setupArena( torusGeom , arena );
		
		Triangle triangle = arena.getTriangles( ).iterator( ).next( );
		TriangleBasis basis = new TriangleBasis( );
		basis.set( triangle );
		final Point3d location = new Point3d( 0.25 , 0.25 , 0 );
		final Vector3d forward = new Vector3d( -.25 , -.24 , 0 );
		final Vector3d actualForward = new Vector3d( 0 , 0 , 1 );
		final Vector3d targetUp = new Vector3d( 0 , 0 , 1 );
		final Vector3d up = new Vector3d( 0 , 0 , 1 );
		final Vector3d right = new Vector3d( );
		
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
		player1.setCameraUp( up );
		player1.setModelUp( up );
		// player1.setVelocity( 5 );
		// player1.setAngularVelocity( Math.PI / 2 );
		arena.addPlayer( player1 );
		
		Appearance torusAppearance = new Appearance( );
		torusAppearance.setMaterial( new Material( new Color3f( 0.3f , 0 , 0 ) , new Color3f( ) , new Color3f( 1 , 0 , 0 ) , new Color3f( 1 , 1 , 1 ) , 64f ) );
		torusAppearance.setPolygonAttributes( new PolygonAttributes( PolygonAttributes.POLYGON_LINE , PolygonAttributes.CULL_BACK , 0f ) );
		Shape3D torusShape = new Shape3D( torusGeom , torusAppearance );
		BranchGroup bg = new BranchGroup( );
		bg.addChild( torusShape );
		sandbox.sceneRoot.addChild( bg );
		
		double height = 0.1;
		Sphere sphere = new Sphere( ( float ) height / 2 );
		
		final Transform3D sphereInitXform = new Transform3D( );
		final Transform3D sphereXform = new Transform3D( );
		final Transform3D x2 = new Transform3D( );
		
		sphereInitXform.setScale( new Vector3d( 2 , 1 , 4 ) );
		x2.setTranslation( new Vector3d( 0 , height / 2 , 0 ) );
		sphereInitXform.mul( x2 , sphereInitXform );
		
		final TransformComputer3d tc = new TransformComputer3d( );
		final Transform3D orientation = new Transform3D( );
		tc.orient( new Point3d( ) , new Vector3d( 0 , 0 , -1 ) , new Vector3d( 0 , 1 , 0 ) , location , forward , up , orientation );
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
		cameraInitXform.setTranslation( new Vector3d( 0 , 1 , 10 ) );
		x2.setIdentity( );
		x2.rotX( -Math.PI / 18 );
		cameraInitXform.mul( x2 , cameraInitXform );
		cameraXform.mul( orientation , cameraInitXform );
		sandbox.vp.getViewPlatformTransform( ).setTransform( cameraXform );
		
		sandbox.frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		sandbox.frame.setVisible( true );
		
		class ControlState
		{
			long	lastUpdate;
			double	acceleration;
			double	braking;
			double	leftTurning;
			double	rightTurning;
			
			boolean	upPressed;
			boolean	downPressed;
			boolean	leftPressed;
			boolean	rightPressed;
			
			void update( long time )
			{
				long lastUpdate = this.lastUpdate;
				double timestep = ( time - lastUpdate ) / 1e9;
				this.lastUpdate = time;
				
				if( lastUpdate != 0 )
				{
					if( upPressed )
					{
						acceleration += timestep;
					}
					if( downPressed )
					{
						braking += timestep;
					}
					if( leftPressed )
					{
						leftTurning += timestep;
					}
					if( rightPressed )
					{
						rightTurning += timestep;
					}
				}
			}
			
			void clear( )
			{
				acceleration = 0;
				braking = 0;
				leftTurning = 0;
				rightTurning = 0;
			}
		}
		
		final ControlState controlState = new ControlState( );
		
		sandbox.canvas.addKeyListener( new KeyListener( )
		{
			@Override
			public void keyPressed( KeyEvent e )
			{
				synchronized( controlState )
				{
					controlState.update( System.nanoTime( ) );
					
					switch( e.getKeyCode( ) )
					{
						case KeyEvent.VK_UP:
							controlState.upPressed = true;
							break;
						case KeyEvent.VK_DOWN:
							controlState.downPressed = true;
							break;
						case KeyEvent.VK_LEFT:
							controlState.leftPressed = true;
							break;
						case KeyEvent.VK_RIGHT:
							controlState.rightPressed = true;
							break;
					}
				}
			}
			
			@Override
			public void keyReleased( KeyEvent e )
			{
				synchronized( controlState )
				{
					controlState.update( System.nanoTime( ) );
					
					switch( e.getKeyCode( ) )
					{
						case KeyEvent.VK_UP:
							controlState.upPressed = false;
							break;
						case KeyEvent.VK_DOWN:
							controlState.downPressed = false;
							break;
						case KeyEvent.VK_LEFT:
							controlState.leftPressed = false;
							break;
						case KeyEvent.VK_RIGHT:
							controlState.rightPressed = false;
							break;
					}
				}
			}
			
			@Override
			public void keyTyped( KeyEvent e )
			{
			}
		} );
		
		final LinkedList<Transform3D> transformQueue = new LinkedList<Transform3D>( );
		final int cameraDelay = 7;
		final int cameraSmoothing = 7;
		
		final DebugVector upVector = new DebugVector( new Point3d( ) , new Vector3d( 1 , 0 , 0 ) , new Color3f( 1 , 1 , 0 ) );
		final DebugVector targetUpVector = new DebugVector( new Point3d( ) , new Vector3d( 1 , 0 , 0 ) , new Color3f( 1 , 1 , 1 ) );
		final DebugVector rightVector = new DebugVector( new Point3d( ) , new Vector3d( 1 , 0 , 0 ) , new Color3f( 0 , 1 , 0 ) );
		final DebugVector forwardVector = new DebugVector( new Point3d( ) , new Vector3d( 1 , 0 , 0 ) , new Color3f( 1 , 0 , 0 ) );
		final DebugVector actualForwardVector = new DebugVector( new Point3d( ) , new Vector3d( 1 , 0 , 0 ) , new Color3f( 1 , 0 , 1 ) );
		
		// sandbox.sceneRoot.addChild( upVector );
		// sandbox.sceneRoot.addChild( targetUpVector );
		// sandbox.sceneRoot.addChild( rightVector );
		// sandbox.sceneRoot.addChild( forwardVector );
		// sandbox.sceneRoot.addChild( actualForwardVector );
		
		new javax.swing.Timer( 30 , new ActionListener( )
		{
			long		lastTimeNS	= 0;
			double[ ]	matrix		= new double[ 16 ];
			
			@Override
			public void actionPerformed( ActionEvent e )
			{
				long currentTime = System.nanoTime( );
				
				double acceleration, braking, leftTurning, rightTurning;
				
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
					double timestep = ( currentTime - lastTimeNS ) / 1e9;
					
					player1.updateVelocity( timestep , acceleration , braking , leftTurning , rightTurning );
					
					arena.updatePlayers( timestep );
					
					player1.getLocation( location );
					player1.getModelForward( forward );
					player1.getModelUp( up );
					right.cross( forward , up );
					right.normalize( );
					
					tc.orient( new Point3d( ) , new Vector3d( 0 , 1 , 0 ) , new Vector3d( 0 , 0 , -1 ) , location , up , forward , orientation );
					sphereXform.mul( orientation , sphereInitXform );
					
					sphereTG.setTransform( sphereXform );
					
					tc.orient( new Point3d( ) , new Vector3d( 0 , 1 , 0 ) , new Vector3d( 0 , 0 , -1 ) , location , up , forward , orientation );
					
					Transform3D xform = null;
					
					if( transformQueue.size( ) < cameraDelay )
					{
						xform = new Transform3D( );
					}
					else
					{
						xform = transformQueue.poll( );
					}
					xform.set( orientation );
					transformQueue.add( xform );
					
					location.set( 0 , 0 , 0 );
					up.set( 0 , 0 , 0 );
					right.set( 0 , 0 , 0 );
					
					int avgCount = Math.min( cameraSmoothing , transformQueue.size( ) );
					
					for( int i = 0 ; i < avgCount ; i++ )
					{
						Transform3D next = transformQueue.get( i );
						Point3d p1 = new Point3d( );
						next.transform( p1 );
						location.add( p1 );
						
						Vector3d v1 = new Vector3d( 0 , 1 , 0 );
						next.transform( v1 );
						up.add( v1 );
						
						v1.set( 1 , 0 , 0 );
						next.transform( v1 );
						right.add( v1 );
					}
					
					location.scale( 1.0 / avgCount );
					up.scale( 1.0 / avgCount );
					right.scale( 1.0 / avgCount );
					
					tc.orient( new Point3d( ) , new Vector3d( 0 , 1 , 0 ) , new Vector3d( 1 , 0 , 0 ) , location , up , right , x2 );
					
					cameraXform.mul( x2 , cameraInitXform );
					sandbox.vp.getViewPlatformTransform( ).setTransform( cameraXform );
					
					player1.getLocation( location );
					player1.getModelUp( up );
					player1.getBasisUp( targetUp );
					player1.getBasisForward( forward );
					right.cross( forward , up );
					actualForward.cross( up , right );
					
					double debugVecScale = 0.5;
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
	
	public static void setupArena( IndexedGeometryArray geom , Arena arena )
	{
		Point3d[ ] coords = new Point3d[ geom.getVertexCount( ) ];
		for( int i = 0 ; i < coords.length ; i++ )
		{
			coords[ i ] = new Point3d( );
		}
		geom.getCoordinates( 0 , coords );
		
		Vector3f[ ] normals = new Vector3f[ geom.getVertexCount( ) ];
		for( int i = 0 ; i < normals.length ; i++ )
		{
			normals[ i ] = new Vector3f( );
		}
		geom.getNormals( 0 , normals );
		
		for( int index = 0 ; index < geom.getIndexCount( ) ; index += 3 )
		{
			int ci0 = geom.getCoordinateIndex( index );
			int ci1 = geom.getCoordinateIndex( index + 1 );
			int ci2 = geom.getCoordinateIndex( index + 2 );
			int ni0 = geom.getNormalIndex( index );
			int ni1 = geom.getNormalIndex( index + 1 );
			int ni2 = geom.getNormalIndex( index + 2 );
			
			Triangle triangle = new Triangle( coords[ ci0 ] , coords[ ci1 ] , coords[ ci2 ] , normals[ ni0 ] , normals[ ni1 ] , normals[ ni2 ] );
			arena.add( triangle );
		}
	}
	
	public static IndexedGeometryArray createTorus( double majorRadius , int majorDivs , double minorRadius , int minorDivs )
	{
		int coordCount = majorDivs * minorDivs;
		int indexCount = coordCount * 6;
		int normalCount = coordCount * 2;
		int vertexCount = normalCount;
		int vertexFormat = GeometryArray.COORDINATES | GeometryArray.NORMALS;
		IndexedTriangleArray triangleArray = new IndexedTriangleArray( vertexCount , vertexFormat , indexCount );
		
		double majorStep = Math.PI * 2 / majorDivs;
		double minorStep = Math.PI * 2 / minorDivs;
		
		Point3d p = new Point3d( );
		Vector3d v3d = new Vector3d( );
		Vector3f v3f = new Vector3f( );
		
		int ci = 0;
		
		for( int major = 0 ; major < majorDivs ; major++ )
		{
			double majorAngle = major * majorStep;
			double majorX = Math.cos( majorAngle ) * majorRadius;
			double majorZ = Math.sin( majorAngle ) * majorRadius;
			
			double minorRadius2 = minorRadius * ( 1 + 0.7 * Math.sin( majorAngle * 3 ) );
			
			for( int minor = 0 ; minor < minorDivs ; minor++ )
			{
				double minorAngle = minor * minorStep + majorAngle * 4;
				double minorXZ = Math.cos( minorAngle ) * minorRadius2;
				double minorX = Math.cos( majorAngle ) * minorXZ;
				double minorZ = Math.sin( majorAngle ) * minorXZ;
				double minorY = Math.sin( minorAngle ) * minorRadius2 + Math.sin( majorAngle * 2 ) * majorRadius / 5;
				
				p.set( majorX + minorX , minorY , majorZ + minorZ );
				v3d.set( Math.cos( majorAngle ) * Math.cos( minorAngle ) , Math.sin( minorAngle ) , Math.sin( majorAngle ) * Math.cos( minorAngle ) );
				v3f.set( v3d );
				
				int vertexIndex = major * minorDivs + minor;
				
				triangleArray.setCoordinate( vertexIndex , p );
				
				int tl = vertexIndex;
				int bl = major * minorDivs + ( minor + 1 ) % minorDivs;
				int tr = ( tl + minorDivs ) % coordCount;
				int br = ( bl + minorDivs ) % coordCount;
				
				triangleArray.setCoordinateIndex( ci++ , tl );
				triangleArray.setCoordinateIndex( ci++ , bl );
				triangleArray.setCoordinateIndex( ci++ , tr );
				triangleArray.setCoordinateIndex( ci++ , tr );
				triangleArray.setCoordinateIndex( ci++ , bl );
				triangleArray.setCoordinateIndex( ci++ , br );
			}
		}
		
		EdgeNormalComputer enc = new EdgeNormalComputer( );
		Point3f tcp = new Point3f( );
		Point3f tlp = new Point3f( );
		Point3f trp = new Point3f( );
		Point3f bcp = new Point3f( );
		Point3f blp = new Point3f( );
		Point3f brp = new Point3f( );
		
		ci = 0;
		
		for( int major = 0 ; major < majorDivs ; major++ )
		{
			for( int minor = 0 ; minor < minorDivs ; minor++ )
			{
				int vertexIndex = major * minorDivs + minor;
				int normalIndex = vertexIndex * 2;
				
				int tc = vertexIndex;
				int bc = major * minorDivs + ( minor + 1 ) % minorDivs;
				int tr = ( tc + minorDivs ) % coordCount;
				int br = ( bc + minorDivs ) % coordCount;
				int tl = ( tc + ( majorDivs - 1 ) * minorDivs ) % coordCount;
				int bl = ( bc + ( majorDivs - 1 ) * minorDivs ) % coordCount;
				
				triangleArray.getCoordinate( tl , tlp );
				triangleArray.getCoordinate( tc , tcp );
				triangleArray.getCoordinate( tr , trp );
				triangleArray.getCoordinate( bl , blp );
				triangleArray.getCoordinate( bc , bcp );
				triangleArray.getCoordinate( br , brp );
				
				triangleArray.setNormal( normalIndex , enc.edgeNormal( tcp , bcp , trp , tlp , v3f ) );
				triangleArray.setNormal( normalIndex + 1 , enc.edgeNormal( bcp , tcp , blp , brp , v3f ) );
				
				int tcni = normalIndex;
				int bcni = normalIndex + 1;
				int trni = ( normalIndex + minorDivs * 2 ) % normalCount;
				int brni = ( normalIndex + minorDivs * 2 + 1 ) % normalCount;
				
				triangleArray.setNormalIndex( ci++ , tcni );
				triangleArray.setNormalIndex( ci++ , bcni );
				triangleArray.setNormalIndex( ci++ , trni );
				triangleArray.setNormalIndex( ci++ , trni );
				triangleArray.setNormalIndex( ci++ , bcni );
				triangleArray.setNormalIndex( ci++ , brni );
			}
		}
		
		return triangleArray;
	}
}
