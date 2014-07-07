package org.andork.jogl;

import org.andork.jogl.neu.JoglDrawContext;

public interface ProjectionCalculator
{
	public void calculate( JoglDrawContext drawContext , float[ ] pOut );
}
