package org.andork.torquescape.model.render;

import java.util.List;

import javax.media.j3d.GeometryArray;
import javax.media.j3d.TriangleArray;

import org.andork.torquescape.model.Triangle;

public class GeometryGenerator
{
	public static GeometryArray createGeometry( List<Triangle> triangles )
	{
		TriangleArray result = new TriangleArray( triangles.size( ) * 3 , GeometryArray.COORDINATES | GeometryArray.NORMALS );
		
		int k = 0;
		for( Triangle t : triangles )
		{
			result.setCoordinate( k++ , t.p0 );
			result.setCoordinate( k++ , t.p1 );
			result.setCoordinate( k++ , t.p2 );
			
			if( t.n0 == null || t.n1 == null || t.n2 == null )
			{
				System.out.println( "WHOOPS" );
			}
			else
			{
				k -= 3;
				result.setNormal( k++ , t.n0 );
				result.setNormal( k++ , t.n1 );
				result.setNormal( k++ , t.n2 );
			}
		}
		
		return result;
	}
}
