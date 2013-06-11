package org.andork.torquescape.model.xform;

import javax.media.j3d.Transform3D;
import javax.vecmath.Vector3d;

import org.andork.j3d.math.J3DTempsPool;
import org.andork.torquescape.model.param.IParamFunction;

public class Bloater implements IXformFunction
{
	private IParamFunction	xScaleFunction;
	private IParamFunction	yScaleFunction;
	private IParamFunction	zScaleFunction;
	
	public Bloater( IParamFunction scaleFunction )
	{
		this( scaleFunction , scaleFunction , scaleFunction );
	}
	
	public Bloater( IParamFunction xScaleFunction , IParamFunction yScaleFunction , IParamFunction zScaleFunction )
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
