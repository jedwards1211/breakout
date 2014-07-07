package org.andork.jogl;

import static org.andork.math3d.Vecmath.*;

import org.andork.jogl.neu.JoglDrawContext;

public class AutoClipOrthoProjectionCalculator implements ProjectionCalculator
{
	public final float[ ]	center			= new float[ 3 ];
	public float			radius			= 1;
	
	public float			hSpan			= 1;
	public float			vSpan			= 1;
	
	public boolean			useNearClipPoint;
	public final float[ ]	nearClipPoint	= { Float.NaN , Float.NaN , Float.NaN };
	
	public boolean			useFarClipPoint;
	public final float[ ]	farClipPoint	= { Float.NaN , Float.NaN , Float.NaN };
	
	@Override
	public void calculate( JoglDrawContext dc , float[ ] pOut )
	{
		float width = dc.getWidth( );
		float height = dc.getHeight( );
		
		float[ ] vi = dc.inverseViewXform( );
		
		float left, right, bottom, top;
		if( vSpan / hSpan > height / width )
		{
			top = vSpan / 2;
			bottom = -top;
			right = top * width / height;
			left = -right;
		}
		else
		{
			right = hSpan / 2;
			left = -right;
			top = right * height / width;
			bottom = -top;
		}
		
		float dist = subDot3( vi , 12 , center , 0 , vi , 8 );
		
		float zNear = dist - radius;
		float zFar = dist + radius;
		
		if( useNearClipPoint )
		{
			zNear = subDot3( vi , 12 , nearClipPoint , 0 , vi , 8 );
		}
		if( useFarClipPoint )
		{
			zFar = subDot3( vi , 12 , farClipPoint , 0 , vi , 8 );
		}
		
		ortho( pOut , left , right , bottom , top , zNear , zFar );
	}
	
}
