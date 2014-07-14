package org.andork.breakout.table;

public class CrossSection
{
	public final CrossSectionType	type;
	public final double[ ]			values;
	
	public CrossSection( CrossSectionType type , double ... values )
	{
		this.type = type;
		this.values = values;
	}
}
