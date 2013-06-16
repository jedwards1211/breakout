
package org.andork.torquescape.model;

import java.util.Comparator;

import javax.vecmath.Point3f;

public class CanonicalPoint3fOrder implements Comparator<Point3f>
{
	public static final CanonicalPoint3fOrder	INSTANCE	= new CanonicalPoint3fOrder( );

	@Override
	public int compare( Point3f o1 , Point3f o2 )
	{
		int result = Float.compare( o1.x , o2.x );
		if( result == 0 )
		{
			result = Float.compare( o1.y , o2.y );
			if( result == 0 )
			{
				result = Float.compare( o1.z , o2.z );
			}
		}
		return result;
	}
}
