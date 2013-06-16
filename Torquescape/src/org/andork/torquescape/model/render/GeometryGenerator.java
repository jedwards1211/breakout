package org.andork.torquescape.model.render;

import java.util.List;

import javax.media.j3d.GeometryArray;
import javax.media.j3d.TriangleArray;
import javax.vecmath.Point3d;

import org.andork.torquescape.model.Triangle;
import org.andork.vecmath.VecmathUtils;

public class GeometryGenerator
{
	public static GeometryArray createGeometry( List<Triangle> triangles )
	{
		TriangleArray result = new TriangleArray( triangles.size( ) * 3 , GeometryArray.COORDINATES | GeometryArray.NORMALS );
		
		Point3d[ ] points = VecmathUtils.allocPoint3dArray( 3 );
		
		int k = 0;
		for( Triangle t : triangles )
		{
			t.getPoints( points );
			for( int i = 0 ; i < 3 ; i++ )
			{
				result.setCoordinate( k , points[ i ] );
				if( t.getNormal( i ) != null )
				{
					result.setNormal( k , t.getNormal( i ) );
				}
				else
				{
					System.out.println( "WHOOPS" );
				}
				k++ ;
			}
		}
		
		return result;
	}
}
