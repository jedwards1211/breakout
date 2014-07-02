package org.andork.breakout.model;

import java.util.Collection;

public interface TiltAxisInferrer
{
	public float[ ] inferTiltAxis( Collection<? extends Shot> shots );
}
