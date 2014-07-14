package org.andork.breakout.table;

import org.andork.util.Format;

public abstract class CrossSectionFormat implements Format<CrossSection>
{
	protected Format<Double>	distanceFormat;
	
	public CrossSectionFormat( )
	{
		this( new DefaultDistanceFormat( ) );
	}
	
	public CrossSectionFormat( DefaultDistanceFormat distanceFormat )
	{
		this.distanceFormat = distanceFormat;
	}
	
}