package org.andork.torquescape.model.param;

import org.andork.math.curve.BSpline1f;
import org.andork.math.curve.BSpline1f.Evaluator;

public class BSpline1fParamFn implements IParamFn
{
	BSpline1f			spline;
	BSpline1f.Evaluator	evaluator;
	
	public BSpline1fParamFn( BSpline1f spline )
	{
		this.spline = spline;
		this.evaluator = new Evaluator( spline.getDegree( ) );
	}

	@Override
	public float eval( float param )
	{
		return evaluator.eval( spline , param );
	}
}
