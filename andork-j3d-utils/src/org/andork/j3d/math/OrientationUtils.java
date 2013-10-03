package org.andork.j3d.math;

import javax.vecmath.Quat4f;

public class OrientationUtils
{
	public static void taitBryanToQuat( float yaw , float pitch , float roll , Quat4f q )
	{
		float cphi = ( float ) Math.cos( roll / 2 );
		float ctheta = ( float ) Math.cos( pitch / 2 );
		float cpsi = ( float ) Math.cos( yaw / 2 );
		float sphi = ( float ) Math.sin( roll / 2 );
		float stheta = ( float ) Math.sin( pitch / 2 );
		float spsi = ( float ) Math.sin( yaw / 2 );
		
		q.w = cphi * ctheta * cpsi + sphi * stheta * spsi;
		q.x = sphi * ctheta * cpsi - cphi * stheta * spsi;
		q.y = cphi * stheta * cpsi - sphi * ctheta * spsi;
		q.z = cphi * ctheta * spsi - sphi * stheta * cpsi;
	}
}
