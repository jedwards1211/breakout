package org.andork.vecmath;

import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

public class EdgeNormalComputer
{
	private final Vector3f	v1	= new Vector3f( );
	private final Vector3f	v2	= new Vector3f( );
	private final Vector3f	v3	= new Vector3f( );
	
	public Vector3f edgeNormal( Point3f e1 , Point3f e2 , Point3f c1 , Point3f c2 , Vector3f result )
	{
		v2.sub( e2 , e1 );
		v3.sub( c1 , e1 );
		v1.cross( v2 , v3 );
		v1.normalize( );
		
		v3.sub( c2 , e1 );
		v2.cross( v3 , v2 );
		v2.normalize( );
		
		result.add( v1 , v2 );
		result.normalize( );
		
		return result;
	}
}
