package org.andork.frf.model;

import java.util.Collection;

public interface TiltAxisInferrer
{
	public float[ ] inferTiltAxis( Collection<? extends SurveyShot> shots );
}
