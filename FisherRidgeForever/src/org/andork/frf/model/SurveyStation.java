package org.andork.frf.model;

import java.util.HashSet;
import java.util.Set;

public class SurveyStation
{
	public String					name;
	public final Set<SurveyShot>	frontsights	= new HashSet<SurveyShot>( );
	public final Set<SurveyShot>	backsights	= new HashSet<SurveyShot>( );
	
	public boolean					traversing	= false;
	
	public final double[ ]			position	= new double[ 3 ];
}
