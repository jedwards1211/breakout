package org.andork.j3d.math;

import java.util.ArrayDeque;
import java.util.Deque;

import javax.media.j3d.Transform3D;
import javax.vecmath.Point3d;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

public class J3DTempsPool
{
	
	private Deque<PoolTransformComputer3f>	tc3fs		= new ArrayDeque<PoolTransformComputer3f>( );
	private Deque<PoolTransformComputer3d>	tc3ds		= new ArrayDeque<PoolTransformComputer3d>( );
	private Deque<PoolPoint3f>				point3fs	= new ArrayDeque<PoolPoint3f>( );
	private Deque<PoolVector3f>				vector3fs	= new ArrayDeque<PoolVector3f>( );
	private Deque<PoolPoint3d>				point3ds	= new ArrayDeque<PoolPoint3d>( );
	private Deque<PoolVector3d>				vector3ds	= new ArrayDeque<PoolVector3d>( );
	private Deque<PoolTransform3D>			t3Ds		= new ArrayDeque<PoolTransform3D>( );
	
	public TransformComputer3f getTransformComputer3f( )
	{
		synchronized( tc3fs )
		{
			PoolTransformComputer3f p = tc3fs.isEmpty( ) ? new PoolTransformComputer3f( ) : tc3fs.pop( );
			p.inUse = true;
			return p;
		}
	}
	
	public void release( TransformComputer3f p )
	{
		if( p instanceof PoolTransformComputer3f )
		{
			synchronized( tc3fs )
			{
				( ( PoolTransformComputer3f ) p ).checkin( this );
			}
		}
	}
	
	public TransformComputer3d getTransformComputer3d( )
	{
		synchronized( tc3ds )
		{
			PoolTransformComputer3d p = tc3ds.isEmpty( ) ? new PoolTransformComputer3d( ) : tc3ds.pop( );
			p.inUse = true;
			return p;
		}
	}
	
	public void release( TransformComputer3d p )
	{
		if( p instanceof PoolTransformComputer3d )
		{
			synchronized( tc3ds )
			{
				( ( PoolTransformComputer3d ) p ).checkin( this );
			}
		}
	}
	
	public Point3f getPoint3f( )
	{
		synchronized( point3fs )
		{
			PoolPoint3f p = point3fs.isEmpty( ) ? new PoolPoint3f( ) : point3fs.pop( );
			p.inUse = true;
			return p;
		}
	}
	
	public void release( Point3f p )
	{
		if( p instanceof PoolPoint3f )
		{
			synchronized( point3fs )
			{
				( ( PoolPoint3f ) p ).checkin( this );
			}
		}
	}
	
	public Vector3f getVector3f( )
	{
		synchronized( vector3fs )
		{
			PoolVector3f p = vector3fs.isEmpty( ) ? new PoolVector3f( ) : vector3fs.pop( );
			p.inUse = true;
			return p;
		}
	}
	
	public void release( Vector3f p )
	{
		if( p instanceof PoolVector3f )
		{
			synchronized( vector3fs )
			{
				( ( PoolVector3f ) p ).checkin( this );
			}
		}
	}
	
	public Point3d getPoint3d( )
	{
		synchronized( point3ds )
		{
			PoolPoint3d p = point3ds.isEmpty( ) ? new PoolPoint3d( ) : point3ds.pop( );
			p.inUse = true;
			return p;
		}
	}
	
	public void release( Point3d p )
	{
		if( p instanceof PoolPoint3d )
		{
			synchronized( point3ds )
			{
				( ( PoolPoint3d ) p ).checkin( this );
			}
		}
	}
	
	public Vector3d getVector3d( )
	{
		synchronized( vector3ds )
		{
			PoolVector3d p = vector3ds.isEmpty( ) ? new PoolVector3d( ) : vector3ds.pop( );
			p.inUse = true;
			return p;
		}
	}
	
	public void release( Vector3d p )
	{
		if( p instanceof PoolVector3d )
		{
			synchronized( vector3ds )
			{
				( ( PoolVector3d ) p ).checkin( this );
			}
		}
	}
	
	public Transform3D getTransform3D( )
	{
		synchronized( t3Ds )
		{
			PoolTransform3D p = t3Ds.isEmpty( ) ? new PoolTransform3D( ) : t3Ds.pop( );
			p.inUse = true;
			return p;
		}
	}
	
	public void release( Transform3D p )
	{
		if( p instanceof PoolTransform3D )
		{
			synchronized( t3Ds )
			{
				( ( PoolTransform3D ) p ).checkin( this );
			}
		}
	}
	
	private class PoolTransformComputer3f extends TransformComputer3f
	{
		private boolean	inUse	= false;
		
		public void checkin( J3DTempsPool pool )
		{
			if( pool == J3DTempsPool.this && inUse )
			{
				inUse = false;
				pool.tc3fs.push( this );
			}
		}
	}
	
	private class PoolTransformComputer3d extends TransformComputer3d
	{
		private boolean	inUse	= false;
		
		public void checkin( J3DTempsPool pool )
		{
			if( pool == J3DTempsPool.this && inUse )
			{
				inUse = false;
				pool.tc3ds.push( this );
			}
		}
	}
	
	private class PoolPoint3f extends Point3f
	{
		private boolean	inUse	= false;
		
		public void checkin( J3DTempsPool pool )
		{
			if( pool == J3DTempsPool.this && inUse )
			{
				inUse = false;
				pool.point3fs.push( this );
			}
		}
	}
	
	private class PoolVector3f extends Vector3f
	{
		private boolean	inUse	= false;
		
		public void checkin( J3DTempsPool pool )
		{
			if( pool == J3DTempsPool.this && inUse )
			{
				inUse = false;
				pool.vector3fs.push( this );
			}
		}
	}
	
	private class PoolPoint3d extends Point3d
	{
		private boolean	inUse	= false;
		
		public void checkin( J3DTempsPool pool )
		{
			if( pool == J3DTempsPool.this && inUse )
			{
				inUse = false;
				pool.point3ds.push( this );
			}
		}
	}
	
	private class PoolVector3d extends Vector3d
	{
		private boolean	inUse	= false;
		
		public void checkin( J3DTempsPool pool )
		{
			if( pool == J3DTempsPool.this && inUse )
			{
				inUse = false;
				pool.vector3ds.push( this );
			}
		}
	}
	
	private class PoolTransform3D extends Transform3D
	{
		private boolean	inUse	= false;
		
		public void checkin( J3DTempsPool pool )
		{
			if( pool == J3DTempsPool.this && inUse )
			{
				inUse = false;
				pool.t3Ds.push( this );
			}
		}
	}
	
}
