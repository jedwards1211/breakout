package org.andork.torquescape.model.section;


public class SectionCurve
{
	public final float[ ]	outsideDirection	= new float[ 3 ];
	public final float[ ]	points;
	public final boolean[ ]	smoothFlags;
	
	public SectionCurve( int pointCount )
	{
		points = new float[ pointCount * 3 ];
		smoothFlags = new boolean[ pointCount ];
	}
}
