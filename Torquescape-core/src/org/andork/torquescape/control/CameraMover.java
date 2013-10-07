package org.andork.torquescape.control;

import static org.andork.vecmath.FloatArrayVecmath.add3;
import static org.andork.vecmath.FloatArrayVecmath.mmulAffine;
import static org.andork.vecmath.FloatArrayVecmath.mpmulAffine;
import static org.andork.vecmath.FloatArrayVecmath.mvmulAffine;
import static org.andork.vecmath.FloatArrayVecmath.newIdentityMatrix;
import static org.andork.vecmath.FloatArrayVecmath.rotX;
import static org.andork.vecmath.FloatArrayVecmath.scale3;
import static org.andork.vecmath.FloatArrayVecmath.set;
import static org.andork.vecmath.FloatArrayVecmath.setColumn3;

import java.util.Iterator;
import java.util.LinkedList;

import org.andork.vecmath.FloatTransformComputer;

public class CameraMover
{
	private Vehicle							Vehicle;
	private float							lookahead;
	private int								lookaheadIntervals;
	private final float[ ]					initXform		= newIdentityMatrix( );
	public final float[ ]					xform			= newIdentityMatrix( );
	private final FloatTransformComputer	tc				= new FloatTransformComputer( );
	private float[ ]						location		= new float[ 3 ];
	private float[ ]						forward			= new float[ 3 ];
	private float[ ]						up				= new float[ 3 ];
	private float[ ]						right			= new float[ 3 ];
	final LinkedList<float[ ]>				transformQueue	= new LinkedList<float[ ]>( );
	final int								cameraDelay		= 10;
	final int								cameraSmoothing	= 10;
	
	float[ ]								p1				= new float[ 3 ];
	float[ ]								v1				= new float[ 3 ];
	
	public CameraMover( Vehicle Vehicle , float lookahead , int lookaheadIntervals )
	{
		this.Vehicle = Vehicle;
		this.lookahead = lookahead;
		this.lookaheadIntervals = lookaheadIntervals;
		
		setColumn3( initXform , 3 , 0 , 1 , 3 );
		rotX( xform , ( float ) -Math.PI / 30 );
		mmulAffine( xform , initXform , initXform );
	}
	
	public void updateXform( )
	{
		float[ ] queueXform = null;
		
		if( transformQueue.size( ) < cameraDelay )
		{
			queueXform = newIdentityMatrix( );
		}
		else
		{
			queueXform = transformQueue.poll( );
		}
		tc.orient( 0 , 0 , 0 , /**/0 , 1 , 0 , /**/0 , 0 , -1 , Vehicle.location , Vehicle.basisUp , Vehicle.basisForward , queueXform );
		transformQueue.add( queueXform );
		
		set( location , 0 , 0 , 0 );
		set( up , 0 , 0 , 0 );
		set( right , 0 , 0 , 0 );
		
		int avgCount = Math.min( cameraSmoothing , transformQueue.size( ) );
		
		Iterator<float[ ]> xiter = transformQueue.iterator( );
		for( int i = 0 ; i < avgCount ; i++ )
		{
			float[ ] next = xiter.next( );
			set( p1 , 0 , 0 , 0 );
			mpmulAffine( next , p1 );
			add3( location , p1 , location );
			
			set( v1 , 0 , 1 , 0 );
			mvmulAffine( next , v1 );
			add3( up , v1 , up );
			
			set( v1 , 1 , 0 , 0 );
			mvmulAffine( next , v1 );
			add3( right , v1 , right );
		}
		
		scale3( location , 1.0f / avgCount );
		scale3( up , 1.0f / avgCount );
		scale3( right , 1.0f / avgCount );
		
		tc.orient( 0 , 0 , 0 , /**/0 , 1 , 0 , /**/1 , 0 , 0 , location , up , right , this.xform );
		mmulAffine( this.xform , initXform , this.xform );
	}
}
