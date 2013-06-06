package org.andork.manifold.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.media.j3d.Transform3D;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

import org.andork.j3d.math.TransformComputer3d;

import com.google.common.collect.LinkedHashMultimap;

public class Arena
{
	private final LinkedHashMultimap<Point3d, Triangle>	points		= LinkedHashMultimap.create( );
	private final LinkedHashMultimap<Edge, Triangle>	edges		= LinkedHashMultimap.create( );
	private final Map<Triangle, Triangle>				triangles	= new HashMap<Triangle, Triangle>( );
	
	private final List<Player>							players		= new ArrayList<Player>( );
	
	private final Point3d								p1			= new Point3d( );
	private final Vector3d								v1			= new Vector3d( );
	private final Vector3d								v2			= new Vector3d( );
	private final Vector3d								v3			= new Vector3d( );
	private final Vector3d								v4			= new Vector3d( );
	private final Vector3d								v5			= new Vector3d( );
	private final Vector3d								v6			= new Vector3d( );
	private final Vector3f								v3f1		= new Vector3f( );
	
	private final TransformComputer3d					tc			= new TransformComputer3d( );
	private final Transform3D							orient		= new Transform3D( );
	
	private final UVIntersector							intersector	= new UVIntersector( );
	
	public void addPlayer( Player player )
	{
		if( !players.contains( player ) )
		{
			players.add( player );
		}
	}
	
	public void updatePlayers( double timestep )
	{
		for( Player player : players )
		{
			updatePlayer( player , timestep );
		}
	}
	
	private void updatePlayer( Player player , double timestep )
	{
		double angleChange = player.angularVelocity * timestep;
		if( angleChange != 0 )
		{
			player.getBasisForward( v1 );
			player.basis.getXYZToEFGDirect( ).transform( v1 );
			double ePrime = Math.cos( angleChange ) * v1.x - Math.sin( angleChange ) * v1.y;
			double fPrime = Math.sin( angleChange ) * v1.x + Math.cos( angleChange ) * v1.y;
			v1.set( ePrime , fPrime , 0 );
			player.basis.getEFGToXYZDirect( ).transform( v1 );
			v1.normalize( );
			player.setBasisForward( v1 );
		}
		double remainingDist = Math.abs( player.velocity * timestep );
		
		while( remainingDist > 0 )
		{
			player.getLocation( p1 );
			player.getBasisForward( v1 );
			
			if( player.velocity < 0 )
			{
				v1.negate( );
			}
			
			player.basis.getXYZToUVNDirect( ).transform( p1 );
			player.basis.getXYZToUVNDirect( ).transform( v1 );
			p1.z = 0;
			v1.z = 0;
			
			intersector.intersect( p1.x , p1.y , v1.x , v1.y );
			if( Double.isNaN( intersector.t[ 0 ] ) || Double.isInfinite( intersector.t[ 0 ] ) || intersector.t[ 0 ] <= 0 )
			{
				break;
			}
			
			if( intersector.t[ 0 ] > remainingDist )
			{
				p1.scaleAdd( remainingDist , v1 , p1 );
				player.basis.interpolateNormals( ( float ) p1.x , ( float ) p1.y , v3f1 );
				v3f1.normalize( );
				v2.set( v3f1 );
				player.setBasisUp( v2 );
				player.setCameraUp( v2 );
				player.basis.getUVNToXYZDirect( ).transform( p1 );
				player.setLocation( p1 );
				remainingDist = 0;
			}
			else
			{
				remainingDist -= intersector.t[ 0 ];
				Edge edge = player.basis.triangle.getEdge( intersector.edgeIndices[ 0 ] );
				Triangle next = null;
				for( Triangle other : edges.get( edge.canonical( ) ) )
				{
					if( !other.canonical( ).equals( player.basis.triangle.canonical( ) ) )
					{
						try
						{
							next = other.reorder( edge.p1 , edge.p0 );
							break;
						}
						catch( Exception ex )
						{
							ex.printStackTrace( );
						}
					}
				}
				if( next != null )
				{
					player.getLocation( p1 );
					player.getBasisForward( v1 );
					
					p1.scaleAdd( Math.signum( player.velocity ) * intersector.t[ 0 ] , v1 , p1 );
					
					v2.set( 0 , 0 , 1 );
					v3.set( 0 , 0 , 1 );
					player.basis.getEFGToXYZDirect( ).transform( v2 );
					
					player.basis.set( next );
					player.basis.getEFGToXYZDirect( ).transform( v3 );
					
					tc.orient( v2 , v3 , orient );
					orient.transform( v1 );
					
					player.basis.getXYZToUVNDirect( ).transform( p1 );
					player.basis.getXYZToUVNDirect( ).transform( v1 );
					
					player.basis.interpolateNormals( ( float ) p1.x , ( float ) p1.y , v3f1 );
					v2.set( v3f1 );
					
					p1.y = 0;
					p1.z = 0;
					v1.z = 0;
					
					player.basis.getUVNToXYZDirect( ).transform( p1 );
					player.basis.getUVNToXYZDirect( ).transform( v1 );
					
					v1.normalize( );
					v2.normalize( );
					v3.normalize( );
					
					player.setLocation( p1 );
					player.setBasisForward( v1 );
					player.setCameraUp( v2 );
					player.setBasisUp( v2 );
				}
			}
		}
		
		player.getModelUp( v2 );
		player.getBasisUp( v5 );
		player.getBasisForward( v6 );
		
		if( v2.epsilonEquals( v5 , 0.1 ) )
		{
			tc.orient( v2 , v5 , orient );
			player.setModelUp( v5 );
			player.setModelForward( v6 );
		}
		else
		{
			player.getModelForward( v3 );
			
			v1.cross( v2 , v3 );
			v1.normalize( );
			v4.cross( v5 , v6 );
			v4.normalize( );
			
			v1.cross( v1 , v4 );
			
			double rotationRate = Math.PI * 2;
			double planeRotationAmount = rotationRate * timestep;
			double inPlaneRotationAmount = planeRotationAmount;
			
			double targetPlaneRotation = Math.asin( v1.length( ) );
			
			if( targetPlaneRotation > 0 )
			{
				rotate( v2 , v1 , targetPlaneRotation );
				rotate( v3 , v1 , targetPlaneRotation );
			}
			
			v4.cross( v2 , v5 );
			double targetInPlaneRotation = Math.asin( v4.length( ) );
			
			player.getModelUp( v2 );
			player.getModelForward( v3 );
			
			if( targetPlaneRotation > 0 && targetPlaneRotation > targetInPlaneRotation )
			{
				inPlaneRotationAmount = planeRotationAmount * targetInPlaneRotation / targetPlaneRotation;
			}
			else if( targetInPlaneRotation > 0 && targetInPlaneRotation > targetPlaneRotation )
			{
				planeRotationAmount = inPlaneRotationAmount * targetPlaneRotation / targetInPlaneRotation;
			}
			
			if( targetPlaneRotation < planeRotationAmount && targetInPlaneRotation < inPlaneRotationAmount )
			{
				tc.orient( v2 , v5 , orient );
				player.setModelUp( v5 );
				player.setModelForward( v6 );
			}
			else
			{
				if( inPlaneRotationAmount > 0 )
				{
					rotate( v2 , v4 , inPlaneRotationAmount );
					rotate( v3 , v4 , inPlaneRotationAmount );
				}
				if( planeRotationAmount > 0 )
				{
					rotate( v2 , v1 , planeRotationAmount );
					rotate( v3 , v1 , planeRotationAmount );
				}
				player.setModelUp( v2 );
				player.setModelForward( v3 );
			}
		}
	}
	
	private void rotate( Vector3d v , Vector3d axis , double rotationAmount )
	{
		tc.orient( axis , new Vector3d( 1 , 0 , 0 ) , orient );
		orient.transform( v );
		orient.rotX( rotationAmount );
		orient.transform( v );
		tc.orient( new Vector3d( 1 , 0 , 0 ) , axis , orient );
		orient.transform( v );
	}
	
	public Collection<Triangle> getTriangles( )
	{
		return Collections.unmodifiableCollection( triangles.values( ) );
	}
	
	public void add( Triangle triangle )
	{
		Triangle canonical = triangle.canonical( );
		if( triangles.put( canonical , triangle ) == null )
		{
			for( Edge e : triangle.getEdges( ) )
			{
				edges.put( e.canonical( ) , canonical );
			}
			for( Point3d p : triangle.getPoints( ) )
			{
				points.put( p , canonical );
			}
		}
	}
	
	public void remove( Triangle triangle )
	{
		Triangle canonical = triangle.canonical( );
		
		if( triangles.remove( canonical ) != null )
		{
			for( Edge e : triangle.getEdges( ) )
			{
				edges.remove( e.canonical( ) , canonical );
			}
			
			for( Point3d p : triangle.getPoints( ) )
			{
				points.remove( p , canonical );
			}
		}
	}
}
