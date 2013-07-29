package org.andork.torquescape.model;

import java.util.ArrayList;
import java.util.List;

import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

public class Model2Converter
{
	public static List<Triangle> convert( float[ ] verts , char[ ] indices )
	{
		List<Triangle> result = new ArrayList<Triangle>( );
		
		int i = 0;
		while( i < indices.length )
		{
			char i0 = indices[ i++ ];
			char i1 = indices[ i++ ];
			char i2 = indices[ i++ ];
			
			Point3f p0 = new Point3f( verts[ i0++ ] , verts[ i0++ ] , verts[ i0++ ] );
			Vector3f n0 = new Vector3f( verts[ i0++ ] , verts[ i0++ ] , verts[ i0++ ] );
			Point3f p1 = new Point3f( verts[ i1++ ] , verts[ i1++ ] , verts[ i1++ ] );
			Vector3f n1 = new Vector3f( verts[ i1++ ] , verts[ i1++ ] , verts[ i1++ ] );
			Point3f p2 = new Point3f( verts[ i2++ ] , verts[ i2++ ] , verts[ i2++ ] );
			Vector3f n2 = new Vector3f( verts[ i2++ ] , verts[ i2++ ] , verts[ i2++ ] );
			
			Triangle triangle = new Triangle( p0 , p1 , p2 );
			triangle.n0 = n0;
			triangle.n1 = n1;
			triangle.n2 = n2;
			
			result.add( triangle );
		}
		
		return result;
	}
}
