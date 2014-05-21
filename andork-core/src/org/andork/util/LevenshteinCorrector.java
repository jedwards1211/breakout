package org.andork.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LevenshteinCorrector
{
	private final List<String>			expected	= new ArrayList<String>( );
	private final Map<String, String>	corrections	= new HashMap<String, String>( );
	
	public void addExpected( Collection<String> expected )
	{
		this.expected.addAll( expected );
	}
	
	public void addCorrection( String s , String correction )
	{
		corrections.put( s.toLowerCase( ) , correction );
	}
	
	public String correct( String s )
	{
		s = s.toLowerCase( );
		String correction = corrections.get( s );
		if( correction == null )
		{
			correction = s;
			int bestDist = s.length( ) * 3 / 4;
			for( String exp : expected )
			{
				String explc = exp.toLowerCase( );
				int dist = Levenshtein.distance( s , explc );
				if( s.startsWith( explc.substring( 0 , 1 ) ) && dist < bestDist )
				{
					correction = exp;
					bestDist = dist;
				}
			}
			System.out.println(s + " -> " + correction);
			corrections.put( s , correction );
		}
		return correction;
	}
}
