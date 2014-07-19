package org.andork.breakout;

import org.andork.q.QObject;
import org.andork.q.QSpec;

public class StatsModel extends QSpec<StatsModel>
{
	public static Attribute<Integer>			numSelected		= newAttribute( Integer.class , "numSelected" );
	public static Attribute<Double>				totalDistance	= newAttribute( Double.class , "totalDistance" );
	public static Attribute<QObject<MinAvgMax>>	distStats		= newAttribute( QObject.class , "distStats" );
	public static Attribute<QObject<MinAvgMax>>	northStats		= newAttribute( QObject.class , "northStats" );
	public static Attribute<QObject<MinAvgMax>>	eastStats		= newAttribute( QObject.class , "eastStats" );
	public static Attribute<QObject<MinAvgMax>>	depthStats		= newAttribute( QObject.class , "depthStats" );
	
	public static final StatsModel				spec			= new StatsModel( );
	
	private StatsModel( )
	{
		
	}
	
	public static class MinAvgMax extends QSpec<MinAvgMax>
	{
		public static Attribute<Double>	min		= newAttribute( Double.class , "min" );
		public static Attribute<Double>	avg		= newAttribute( Double.class , "avg" );
		public static Attribute<Double>	max		= newAttribute( Double.class , "max" );
		
		public static final MinAvgMax	spec	= new MinAvgMax( );
		
		private MinAvgMax( )
		{
			
		}
	}
}
