package org.andork.breakout.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SurveyStation
{
	public String								name;
	public final Set<SurveyShot>				frontsights	= new HashSet<SurveyShot>( );
	public final Set<SurveyShot>				backsights	= new HashSet<SurveyShot>( );
	
	public final double[ ]						position	= { Double.NaN , Double.NaN , Double.NaN };
	
	public String toString( )
	{
		return name;
	}
}