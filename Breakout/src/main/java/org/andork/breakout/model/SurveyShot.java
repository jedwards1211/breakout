package org.andork.breakout.model;

import java.util.Collection;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;

import org.andork.breakout.model.Survey3dModel.Shot;
import org.andork.math3d.Vecmath;

public class SurveyShot
{
	public int				index	= -1;
	public SurveyStation	from;
	public SurveyStation	to;
	public double			dist	= Double.NaN;
	public double			fsAzm	= Double.NaN;
	public double			bsAzm	= Double.NaN;
	public double			fsInc	= Double.NaN;
	public double			bsInc	= Double.NaN;
	public double			left	= Double.NaN;
	public double			right	= Double.NaN;
	public double			up		= Double.NaN;
	public double			down	= Double.NaN;
	
	private PriorityEntry	priorityEntry;
	
	private static class PriorityEntry implements Comparable<PriorityEntry>
	{
		public final SurveyShot	shot;
		public final double		priority;
		
		public PriorityEntry( SurveyShot shot , double priority )
		{
			super( );
			this.shot = shot;
			this.priority = priority;
		}
		
		@Override
		public int compareTo( PriorityEntry o )
		{
			return Double.compare( priority , o.priority );
		}
	}
	
	public void computeFrom( )
	{
		if( Vecmath.hasNaNsOrInfinites( to.position ) )
		{
			throw new IllegalStateException( "to.position has NaN or infinite values" );
		}
		
		if( !Vecmath.hasNaNsOrInfinites( from.position ) )
		{
			deriveMeasurements( );
		}
		else
		{
			double incDeg = averageInc( fsInc , bsInc );
			double azm = Math.toRadians( averageAzm( incDeg , fsAzm , bsAzm ) );
			double inc = Math.toRadians( incDeg );
			
			if( Double.isNaN( azm ) )
			{
				throw new IllegalStateException( "average azm is NaN" );
			}
			if( Double.isNaN( inc ) || Double.isInfinite( inc ) )
			{
				throw new IllegalStateException( "average inc is NaN or infinite" );
			}
			
			from.position[ 0 ] = to.position[ 0 ] - Math.sin( azm ) * Math.cos( inc ) * dist;
			from.position[ 1 ] = to.position[ 1 ] - Math.sin( inc ) * dist;
			from.position[ 2 ] = to.position[ 2 ] + Math.cos( azm ) * Math.cos( inc ) * dist;
		}
	}
	
	public void computeTo( )
	{
		if( Vecmath.hasNaNsOrInfinites( from.position ) )
		{
			throw new IllegalStateException( "from.position has NaN or infinite values" );
		}
		
		if( !Vecmath.hasNaNsOrInfinites( to.position ) )
		{
			deriveMeasurements( );
		}
		else
		{
			double incDeg = averageInc( fsInc , bsInc );
			double azm = Math.toRadians( averageAzm( incDeg , fsAzm , bsAzm ) );
			double inc = Math.toRadians( incDeg );
			
			if( Double.isNaN( azm ) )
			{
				throw new IllegalStateException( "average azm is NaN" );
			}
			if( Double.isNaN( inc ) || Double.isInfinite( inc ) )
			{
				throw new IllegalStateException( "average inc is NaN or infinite" );
			}
			
			to.position[ 0 ] = from.position[ 0 ] + Math.sin( azm ) * Math.cos( inc ) * dist;
			to.position[ 1 ] = from.position[ 1 ] + Math.sin( inc ) * dist;
			to.position[ 2 ] = from.position[ 2 ] - Math.cos( azm ) * Math.cos( inc ) * dist;
		}
	}
	
	private void deriveMeasurements( )
	{
		if( Double.isNaN( dist ) )
		{
			dist = Vecmath.distance3( to.position , from.position );
		}
		
		double dx = to.position[ 0 ] - from.position[ 0 ];
		double dy = to.position[ 1 ] - from.position[ 1 ];
		double dz = to.position[ 2 ] - from.position[ 2 ];
		double dxz = Math.sqrt( dx * dx + dz * dz );
		
		if( Double.isNaN( fsAzm ) )
		{
			fsAzm = Math.atan2( dx , -dz );
		}
		if( Double.isNaN( bsAzm ) )
		{
			fsAzm = Math.atan2( dx , -dz );
		}
		
		if( Double.isNaN( fsInc ) )
		{
			fsInc = Math.atan2( dy , dxz );
		}
		if( Double.isNaN( bsInc ) )
		{
			bsInc = Math.atan2( dy , dxz );
		}
	}
	
	private static double averageInc( double fsInc , double bsInc )
	{
		if( Double.isNaN( fsInc ) )
		{
			return bsInc;
		}
		else if( Double.isNaN( bsInc ) )
		{
			return fsInc;
		}
		else
		{
			if( Math.signum( fsInc ) != Math.signum( bsInc ) )
			{
				double angle = Math.abs( fsInc ) + Math.abs( bsInc );
				if( angle > 4.0 )
				{
					bsInc = -bsInc;
				}
			}
			// if( Math.abs( fsInc + bsInc ) < Math.abs( fsInc - bsInc ) )
			// {
			// return ( fsInc - bsInc ) * 0.5;
			// }
			// else
			// {
			return ( fsInc + bsInc ) * 0.5;
			// }
		}
	}
	
	private static double angle( double a , double b )
	{
		double a1 = b - a;
		double a2 = b - a - 360.0;
		
		return Math.abs( a1 ) < Math.abs( a2 ) ? a1 : a2;
	}
	
	private static double averageAzm( double avgIncDegrees , double fsAzm , double bsAzm )
	{
		if( Math.abs( avgIncDegrees ) == 90.0 )
		{
			return 0.0;
		}
		if( Double.isInfinite( fsAzm ) || Double.isInfinite( bsAzm ) )
		{
			throw new IllegalArgumentException( "fsAzm and bsAzm must be non-infinite" );
		}
		
		if( Double.isNaN( fsAzm ) )
		{
			return bsAzm;
		}
		if( Double.isNaN( bsAzm ) )
		{
			return fsAzm;
		}
		
		fsAzm %= 360.0;
		if( fsAzm < 0 )
		{
			fsAzm += 360.0;
		}
		bsAzm %= 360.0;
		if( bsAzm < 0 )
		{
			bsAzm += 360.0;
		}
		double bsAzm180 = ( bsAzm + 180.0 ) % 360.0;
		if( Math.abs( angle( fsAzm , bsAzm180 ) ) < Math.abs( angle( fsAzm , bsAzm ) ) )
		{
			bsAzm = bsAzm180;
		}
		
		return fsAzm + angle( fsAzm , bsAzm ) * 0.5;
	}
	
	public static void computeConnected( Collection<SurveyStation> stations )
	{
		Set<SurveyStation> visited = new HashSet<SurveyStation>( );
		
		for( SurveyStation station : stations )
		{
			if( !Vecmath.hasNaNsOrInfinites( station.position ) )
			{
				if( !visited.contains( station ) )
				{
					computeConnected( station , visited );
				}
			}
		}
	}
	
	public static void computeConnected( SurveyStation start , Set<SurveyStation> visited )
	{
		if( Vecmath.hasNaNsOrInfinites( start.position ) )
		{
			throw new IllegalArgumentException( "start's position has NaN or infinite values" );
		}
		
		PriorityQueue<PriorityEntry> heap = new PriorityQueue<SurveyShot.PriorityEntry>( 10 );
		
		for( SurveyShot shot : start.frontsights )
		{
			if( Double.isNaN( shot.dist ) )
			{
				if( Vecmath.hasNaNsOrInfinites( shot.to.position ) )
				{
					continue;
				}
				shot.dist = Vecmath.distance3( shot.from.position , shot.to.position );
			}
			
			shot.priorityEntry = new PriorityEntry( shot , shot.dist );
			heap.add( shot.priorityEntry );
		}
		for( SurveyShot shot : start.backsights )
		{
			if( Double.isNaN( shot.dist ) )
			{
				if( Vecmath.hasNaNsOrInfinites( shot.from.position ) )
				{
					continue;
				}
				shot.dist = Vecmath.distance3( shot.from.position , shot.to.position );
			}
			
			shot.priorityEntry = new PriorityEntry( shot , shot.dist );
			heap.add( shot.priorityEntry );
		}
		
		visited.add( start );
		
		while( !heap.isEmpty( ) )
		{
			SurveyStation station = null;
			
			SurveyShot shot = heap.poll( ).shot;
			if( visited.add( shot.from ) )
			{
				try
				{
					shot.computeFrom( );
				}
				catch( Exception ex )
				{
					System.err.println( "Can't caltulate: " + shot );
				}
				station = shot.from;
			}
			if( visited.add( shot.to ) )
			{
				try
				{
					shot.computeTo( );
				}
				catch( Exception ex )
				{
					System.err.println( "Can't caltulate: " + shot );
				}
				station = shot.to;
			}
			
			if( station != null )
			{
				for( SurveyShot nextShot : station.frontsights )
				{
					if( nextShot == shot )
					{
						continue;
					}
					if( Double.isNaN( nextShot.dist ) )
					{
						if( Vecmath.hasNaNsOrInfinites( nextShot.to.position ) )
						{
							continue;
						}
						nextShot.dist = Vecmath.distance3( nextShot.from.position , shot.to.position );
					}
					double dist = shot.priorityEntry.priority + nextShot.dist;
					if( nextShot.priorityEntry != null )
					{
						if( dist < nextShot.priorityEntry.priority )
						{
							heap.remove( nextShot.priorityEntry );
							nextShot.priorityEntry = new PriorityEntry( nextShot , dist );
							heap.add( nextShot.priorityEntry );
						}
					}
					else
					{
						nextShot.priorityEntry = new PriorityEntry( nextShot , dist );
						heap.add( nextShot.priorityEntry );
					}
				}
				for( SurveyShot nextShot : station.backsights )
				{
					if( nextShot == shot )
					{
						continue;
					}
					if( Double.isNaN( nextShot.dist ) )
					{
						if( Vecmath.hasNaNsOrInfinites( nextShot.from.position ) )
						{
							continue;
						}
						nextShot.dist = Vecmath.distance3( nextShot.from.position , shot.to.position );
					}
					double dist = shot.priorityEntry.priority + nextShot.dist;
					if( nextShot.priorityEntry != null )
					{
						if( dist < nextShot.priorityEntry.priority )
						{
							heap.remove( nextShot.priorityEntry );
							nextShot.priorityEntry = new PriorityEntry( nextShot , dist );
							heap.add( nextShot.priorityEntry );
						}
					}
					else
					{
						nextShot.priorityEntry = new PriorityEntry( nextShot , dist );
						heap.add( nextShot.priorityEntry );
					}
				}
			}
		}
	}
	
	public String toString( )
	{
		return String.valueOf( from ) + " - " + String.valueOf( to );
	}
}
