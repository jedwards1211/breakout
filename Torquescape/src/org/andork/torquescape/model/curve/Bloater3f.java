package org.andork.torquescape.model.curve;

import javax.media.j3d.Transform3D;
import javax.vecmath.Vector3d;

import org.andork.j3d.math.J3DTempsPool;
import org.andork.math3d.curve.ICurve3f;
import org.andork.torquescape.model.param.IParamFunction;

public class Bloater3f implements ICurve3f
{
	private IParamFunction	xScaleFunction;
	private IParamFunction	yScaleFunction;
	private IParamFunction	zScaleFunction;
	
	public Bloater3f( IParamFunction scaleFunction )
	{
		this( scaleFunction , scaleFunction , scaleFunction );
	}
	
	public Bloater3f( IParamFunction xScaleFunction , IParamFunction yScaleFunction , IParamFunction zScaleFunction )
	{
		super( );
		this.xScaleFunction = xScaleFunction;
		this.yScaleFunction = yScaleFunction;
		this.zScaleFunction = zScaleFunction;
	}
	
	@Override
	public Transform3D eval( float param , J3DTempsPool pool , Transform3D out )
	{
		Vector3d v = pool.getVector3d( );
		v.set( xScaleFunction.eval( param ) , yScaleFunction.eval( param ) , zScaleFunction.eval( param ) );
		
		out.setIdentity( );
		out.setScale( v );
		
		pool.release( v );
		
		return out;
	}
}
