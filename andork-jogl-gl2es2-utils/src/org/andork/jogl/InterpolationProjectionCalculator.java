package org.andork.jogl;

import org.andork.jogl.neu.JoglDrawContext;
import org.andork.math3d.Vecmath;

public class InterpolationProjectionCalculator implements ProjectionCalculator
{
	ProjectionCalculator	a;
	ProjectionCalculator	b;
	
	public float			f;
	
	float[ ]				aOut	= Vecmath.newMat4f( );
	float[ ]				bOut	= Vecmath.newMat4f( );
	
	public final float[ ]	center	= new float[ 3 ];
	public float			radius	= 1;
	
	public InterpolationProjectionCalculator( ProjectionCalculator a , ProjectionCalculator b , float f )
	{
		super( );
		this.a = a;
		this.b = b;
		this.f = f;
	}
	
	@Override
	public void calculate( JoglDrawContext dc , float[ ] pOut )
	{
		a.calculate( dc , aOut );
		b.calculate( dc , bOut );
		Vecmath.interp( aOut , bOut , f , pOut );
	}
}
