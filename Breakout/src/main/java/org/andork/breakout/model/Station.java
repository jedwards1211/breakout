package org.andork.breakout.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Station
{
	public String								name;
	public final Set<Shot>				frontsights	= new HashSet<Shot>( );
	public final Set<Shot>				backsights	= new HashSet<Shot>( );
	
	public final double[ ]						position	= { Double.NaN , Double.NaN , Double.NaN };
	
	public String toString( )
	{
		return name;
	}
}
