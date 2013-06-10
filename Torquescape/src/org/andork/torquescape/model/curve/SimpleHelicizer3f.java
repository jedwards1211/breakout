package org.andork.torquescape.model.curve;

import javax.media.j3d.Transform3D;
import javax.vecmath.Vector3f;

import org.andork.j3d.math.J3DTempsPool;
import org.andork.math3d.curve.ICurve3f;
import org.andork.torquescape.model.param.IParamFunction;

public class SimpleHelicizer3f implements ICurve3f
{
	IParamFunction	radiusFunction;
	IParamFunction	angleFunction;
	
	public SimpleHelicizer3f( IParamFunction radiusFunction , IParamFunction angleFunction )
	{
		super( );
		this.radiusFunction = radiusFunction;
		this.angleFunction = angleFunction;
	}

	@Override
	public Transform3D eval( float param , J3DTempsPool pool , Transform3D out )
	{
		Transform3D x1 = pool.getTransform3D( );
		Vector3f v = pool.getVector3f( );
		v.set( radiusFunction.eval( param ) , 0 , 0 );
		
		x1.setIdentity( );
		x1.setTranslation( v );
		
		out.setIdentity( );
		out.rotZ( angleFunction.eval( param ) );
		out.mul( x1 );
		
		pool.release( x1 );
		pool.release( v );
		
		return out;
	}
	
}
