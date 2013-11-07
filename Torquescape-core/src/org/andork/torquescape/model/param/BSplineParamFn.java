package org.andork.torquescape.model.param;

import org.andork.math.curve.FastFloatBSplineEvaluator;
import org.andork.math.curve.FloatArrayBSpline;

public class BSplineParamFn implements IParamFn
{
	FastFloatBSplineEvaluator	evaluator	= new FastFloatBSplineEvaluator( );
	float[ ]					out;
	
	public BSplineParamFn( FloatArrayBSpline bspline )
	{
		evaluator.bspline( bspline );
		out = new float[ bspline.dimension ];
	}
	
	@Override
	public float eval( float param )
	{
		evaluator.eval( param , out );
		return out[ 0 ];
	}
}
