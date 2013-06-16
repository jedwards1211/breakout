package org.andork.torquescape.model.render;

import javax.vecmath.Point3d;
import javax.vecmath.Vector3f;

import org.andork.j3d.math.J3DTempsPool;
import org.andork.torquescape.model.Arena;
import org.andork.torquescape.model.Triangle;
import org.andork.vecmath.VecmathUtils;

public class NormalGenerator
{
	private Arena			arena;
	private J3DTempsPool	pool;
	private double			minFoldAngle;
	
	public NormalGenerator( Arena arena , J3DTempsPool pool , double minFoldAngle )
	{
		super( );
		this.arena = arena;
		this.pool = pool;
		this.minFoldAngle = minFoldAngle;
	}
	
	public void generateNormals( )
	{
		Point3d[ ] points = VecmathUtils.allocPoint3dArray( 3 );
		
		for( Triangle t : arena.getTriangles( ) )
		{
			setupDefaults( t );
			TriangleRenderingInfo ri = t.getRenderingInfo( );
			
			t.getPoints( points );
			
			for( int i = 0 ; i < 3 ; i++ )
			{
				Vector3f normal = t.getNormal( i );
				if( normal != null )
				{
					continue;
				}
				
				normal = new Vector3f( ri.defaultNormal );
				t.setNormal( i , normal );
				
				addAdjacentNormals( t , points[ 0 ] , points[ 1 ] , points[ 2 ] , normal );
				addAdjacentNormals( t , points[ 0 ] , points[ 2 ] , points[ 1 ] , normal );
				
				normal.normalize( );
			}
		}
	}
	
	void addAdjacentNormals( Triangle t , Point3d p0 , Point3d p1 , Point3d p2 , Vector3f normal )
	{
		Triangle start = t;
		
		Vector3f v1 = pool.getVector3f( );
		
		TriangleRenderingInfo ri = t.getRenderingInfo( );
		
		while( true )
		{
			int p0index = t.indexOf( p0 );
			int p1index = t.indexOf( p1 );
			
			if( p0index < 0 || p1index < 0 )
			{
				break;
			}
			
			t.setNormal( p0index , normal );
			
			int foldIndex = p0index * 2;
			if( p1index == ( p0index + 2 ) % 3 )
			{
				foldIndex = ( foldIndex + 5 ) % 6;
			}
			FoldType foldType = ri.folds[ foldIndex ];
			
			if( foldType == FoldType.FOLDED )
			{
				break;
			}
			
			Point3d p3 = getOtherPoint( arena , p0 , p1 , p2 );
			
			if( p3 == null )
			{
				break;
			}
			
			Triangle t_next = arena.getTriangle( p0 , p1 , p3 );
			
			if( t_next == null || t_next == start )
			{
				break;
			}
			
			setupDefaults( t_next );
			TriangleRenderingInfo ri_next = t_next.getRenderingInfo( );
			
			if( foldType == FoldType.ANGLE_DEPENDENT )
			{
				v1.cross( ri.defaultNormal , ri_next.defaultNormal );
				
				if( Math.asin( v1.length( ) ) > minFoldAngle )
				{
					break;
				}
			}
			
			normal.add( ri_next.defaultNormal );
			
			t = t_next;
			ri = ri_next;
			p2 = p1;
			p1 = p3;
		}
		
		pool.release( v1 );
	}
	
	Point3d getOtherPoint( Arena arena , Point3d p0 , Point3d p1 , Point3d p2 )
	{
		for( Point3d p3 : arena.getConnectedPoints( p0 , p1 ) )
		{
			if( !p3.equals( p2 ) )
			{
				return p3;
			}
		}
		return null;
	}
	
	void setupDefaults( Triangle t )
	{
		TriangleRenderingInfo ri = t.getRenderingInfo( );
		if( ri == null )
		{
			ri = new TriangleRenderingInfo( );
			t.setRenderingInfo( ri );
		}
		if( ri.defaultNormal == null )
		{
			ri.defaultNormal = new Vector3f( );
			t.getDefaultNormal( ri.defaultNormal );
		}
	}
}
