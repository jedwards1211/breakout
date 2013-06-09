
package org.andork.torquescape.model;

import java.util.Comparator;

import javax.vecmath.Point3d;

public class CanonicalPoint3dOrder implements Comparator<Point3d>
{
	public static final CanonicalPoint3dOrder	INSTANCE	= new CanonicalPoint3dOrder( );

	@Override
	public int compare( Point3d o1 , Point3d o2 )
	{
		int result = Double.compare( o1.x , o2.x );
		if( result == 0 )
		{
			result = Double.compare( o1.y , o2.y );
			if( result == 0 )
			{
				result = Double.compare( o1.z , o2.z );
			}
		}
		return result;
	}
}
