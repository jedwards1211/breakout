package org.andork.torquescape.control;

import static org.andork.vecmath.Vecmath.add3;
import static org.andork.vecmath.Vecmath.mmulAffine;
import static org.andork.vecmath.Vecmath.mpmulAffine;
import static org.andork.vecmath.Vecmath.mvmulAffine;
import static org.andork.vecmath.Vecmath.newMat4f;
import static org.andork.vecmath.Vecmath.rotX;
import static org.andork.vecmath.Vecmath.scale3;
import static org.andork.vecmath.Vecmath.setColumn3;
import static org.andork.vecmath.Vecmath.setd;
import static org.andork.vecmath.Vecmath.setf;

import java.util.Iterator;
import java.util.LinkedList;

import org.andork.vecmath.FloatOrientComputer;

public class CameraMover
{
	private Vehicle							vehicle;
	private float							lookahead;
	private int								lookaheadIntervals;
	private final float[ ]					initXform		= newMat4f( );
	public final float[ ]					xform			= newMat4f( );
	private final FloatOrientComputer	tc				= new FloatOrientComputer( );
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
		this.vehicle = Vehicle;
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
			queueXform = newMat4f( );
		}
		else
		{
			queueXform = transformQueue.poll( );
		}
		setd( location , vehicle.location );
		setd( forward , vehicle.basisForward );
		setd( up , vehicle.basisUp );
		tc.orient( 0 , 0 , 0 , /**/0 , 1 , 0 , /**/0 , 0 , -1 , location , up , forward , queueXform );
		transformQueue.add( queueXform );
		
		setf( location , 0 , 0 , 0 );
		setf( up , 0 , 0 , 0 );
		setf( right , 0 , 0 , 0 );
		
		int avgCount = Math.min( cameraSmoothing , transformQueue.size( ) );
		
		Iterator<float[ ]> xiter = transformQueue.iterator( );
		for( int i = 0 ; i < avgCount ; i++ )
		{
			float[ ] next = xiter.next( );
			setf( p1 , 0 , 0 , 0 );
			mpmulAffine( next , p1 );
			add3( location , p1 , location );
			
			setf( v1 , 0 , 1 , 0 );
			mvmulAffine( next , v1 );
			add3( up , v1 , up );
			
			setf( v1 , 1 , 0 , 0 );
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
