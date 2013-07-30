package org.andork.torquescape.control;

import java.util.Iterator;
import java.util.LinkedList;

import javax.media.j3d.Transform3D;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

import org.andork.j3d.math.TransformComputer3f;
import org.andork.torquescape.model.old.Arena;
import org.andork.torquescape.model.old.Player;
import org.andork.vecmath.VecmathUtils;

import com.sun.j3d.utils.universe.ViewingPlatform;

public class CameraController
{
	private Arena						arena;
	private Player						player;
	private final Player				lookaheadPlayer	= new Player( );
	private ViewingPlatform				vp;
	
	private float						lookahead;
	private int							lookaheadIntervals;
	
	private final Transform3D			initXform		= new Transform3D( );
	private final Transform3D			xform			= new Transform3D( );
	
	private final TransformComputer3f	tc				= new TransformComputer3f( );
	
	private Point3f						location		= new Point3f( );
	private Vector3f					forward			= new Vector3f( );
	private Vector3f					up				= new Vector3f( );
	private Vector3f					right			= new Vector3f( );
	
	final LinkedList<Transform3D>		transformQueue	= new LinkedList<Transform3D>( );
	final int							cameraDelay		= 10;
	final int							cameraSmoothing	= 10;
	
	public CameraController( Arena arena , Player player , ViewingPlatform vp , float lookahead , int lookaheadIntervals )
	{
		this.arena = arena;
		this.player = player;
		this.vp = vp;
		this.lookahead = lookahead;
		this.lookaheadIntervals = lookaheadIntervals;
		
		initXform.setTranslation( new Vector3d( 0 , 1 , 3 ) );
		xform.setIdentity( );
		xform.rotX( -Math.PI / 30 );
		initXform.mul( xform , initXform );
	}
	
	public void updateCamera( )
	{
		Transform3D xform = null;
		
		if( transformQueue.size( ) < cameraDelay )
		{
			xform = new Transform3D( );
		}
		else
		{
			xform = transformQueue.poll( );
		}
		tc.orient( VecmathUtils.ZEROF , VecmathUtils.UNIT_YF , VecmathUtils.UNIT_NEG_ZF , player.location , player.basisUp , player.basisForward , xform );
		transformQueue.add( xform );
		
		location.set( 0 , 0 , 0 );
		up.set( 0 , 0 , 0 );
		right.set( 0 , 0 , 0 );
		
		int avgCount = Math.min( cameraSmoothing , transformQueue.size( ) );
		
		Iterator<Transform3D> xiter = transformQueue.iterator( );
		for( int i = 0 ; i < avgCount ; i++ )
		{
			Transform3D next = xiter.next( );
			Point3f p1 = new Point3f( );
			next.transform( p1 );
			location.add( p1 );
			
			Vector3f v1 = new Vector3f( 0 , 1 , 0 );
			next.transform( v1 );
			up.add( v1 );
			
			v1.set( 1 , 0 , 0 );
			next.transform( v1 );
			right.add( v1 );
		}
		
		location.scale( 1.0f / avgCount );
		up.scale( 1.0f / avgCount );
		right.scale( 1.0f / avgCount );
		
		tc.orient( VecmathUtils.ZEROF , VecmathUtils.UNIT_YF , VecmathUtils.UNIT_XF , location , up , right , this.xform );
		this.xform.mul( this.xform , initXform );
		
		vp.getViewPlatformTransform( ).setTransform( this.xform );
	}
}
