/*******************************************************************************
 * Breakout Cave Survey Visualizer
 *
 * Copyright (C) 2014 James Edwards
 *
 * jedwards8 at fastmail dot fm
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *******************************************************************************/
package org.andork.breakout.model;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;

import org.andork.math.misc.AngleUtils;
import org.andork.math3d.Vecmath;

public class Shot
{
	public int			number			= -1;
	public Station		from;
	public Station		to;
	public double		dist			= Double.NaN;
	public double		azm				= Double.NaN;
	public double		inc				= Double.NaN;
	public CrossSection	fromXsection	= new CrossSection( );
	public CrossSection	toXsection		= new CrossSection( );
	public float[ ][ ]	fromSplayPoints;
	public float[ ][ ]	fromSplayNormals;
	public float[ ][ ]	toSplayPoints;
	public float[ ][ ]	toSplayNormals;
	public Date			date;
	
	PriorityEntry		priorityEntry;
	public String		desc;
	
	public float[ ][ ] splayPointsAt( Station station )
	{
		if( station == from )
		{
			return fromSplayPoints;
		}
		else if( station == to )
		{
			return toSplayPoints;
		}
		else
		{
			throw new IllegalArgumentException( "the given station is not one of this shot's stations" );
		}
	}
	
	public float[ ] leftSplayPointAt( Station station )
	{
		if( station == from )
		{
			return fromSplayPoints[ 0 ];
		}
		else if( station == to )
		{
			return toSplayPoints[ 1 ];
		}
		else
		{
			throw new IllegalArgumentException( "the given station is not one of this shot's stations" );
		}
	}
	
	public void setLeftSplayPointAt( Station station , float[ ] splayPoint )
	{
		if( station == from )
		{
			fromSplayPoints[ 0 ] = splayPoint;
		}
		else if( station == to )
		{
			toSplayPoints[ 1 ] = splayPoint;
		}
		else
		{
			throw new IllegalArgumentException( "the given station is not one of this shot's stations" );
		}
	}
	
	public float[ ] rightSplayPointAt( Station station )
	{
		if( station == from )
		{
			return fromSplayPoints[ 1 ];
		}
		else if( station == to )
		{
			return toSplayPoints[ 0 ];
		}
		else
		{
			throw new IllegalArgumentException( "the given station is not one of this shot's stations" );
		}
	}
	
	public void setRightSplayPointAt( Station station , float[ ] splayPoint )
	{
		if( station == from )
		{
			fromSplayPoints[ 1 ] = splayPoint;
		}
		else if( station == to )
		{
			toSplayPoints[ 0 ] = splayPoint;
		}
		else
		{
			throw new IllegalArgumentException( "the given station is not one of this shot's stations" );
		}
	}
	
	public void setSplayPointsAt( Station station , float[ ][ ] splayPoints )
	{
		if( station == from )
		{
			fromSplayPoints = splayPoints;
		}
		else if( station == to )
		{
			toSplayPoints = splayPoints;
		}
		else
		{
			throw new IllegalArgumentException( "the given station is not one of this shot's stations" );
		}
	}
	
	public float[ ][ ] splayNormalsAt( Station station )
	{
		if( station == from )
		{
			return fromSplayNormals;
		}
		else if( station == to )
		{
			return toSplayNormals;
		}
		else
		{
			throw new IllegalArgumentException( "the given station is not one of this shot's stations" );
		}
	}
	
	public void setSplayNormalsAt( Station station , float[ ][ ] splayNormals )
	{
		if( station == from )
		{
			fromSplayNormals = splayNormals;
		}
		else if( station == to )
		{
			toSplayNormals = splayNormals;
		}
		else
		{
			throw new IllegalArgumentException( "the given station is not one of this shot's stations" );
		}
	}
	
	public float[ ] leftSplayNormalAt( Station station )
	{
		if( station == from )
		{
			return fromSplayNormals[ 0 ];
		}
		else if( station == to )
		{
			return toSplayNormals[ 1 ];
		}
		else
		{
			throw new IllegalArgumentException( "the given station is not one of this shot's stations" );
		}
	}
	
	public void setLeftSplayNormalAt( Station station , float[ ] splayNormal )
	{
		if( station == from )
		{
			fromSplayNormals[ 0 ] = splayNormal;
		}
		else if( station == to )
		{
			toSplayNormals[ 1 ] = splayNormal;
		}
		else
		{
			throw new IllegalArgumentException( "the given station is not one of this shot's stations" );
		}
	}
	
	public float[ ] rightSplayNormalAt( Station station )
	{
		if( station == from )
		{
			return fromSplayNormals[ 1 ];
		}
		else if( station == to )
		{
			return toSplayNormals[ 0 ];
		}
		else
		{
			throw new IllegalArgumentException( "the given station is not one of this shot's stations" );
		}
	}
	
	public void setRightSplayNormalAt( Station station , float[ ] splayNormal )
	{
		if( station == from )
		{
			fromSplayNormals[ 1 ] = splayNormal;
		}
		else if( station == to )
		{
			toSplayNormals[ 0 ] = splayNormal;
		}
		else
		{
			throw new IllegalArgumentException( "the given station is not one of this shot's stations" );
		}
	}
	
	public double leftAt( Station station )
	{
		if( station == from )
		{
			if( fromXsection.type != CrossSectionType.LRUD )
			{
				throw new IllegalArgumentException( "Invalid cross section type" );
			}
			return fromXsection.dist[ 0 ];
		}
		else if( station == to )
		{
			if( toXsection.type != CrossSectionType.LRUD )
			{
				throw new IllegalArgumentException( "Invalid cross section type" );
			}
			return toXsection.dist[ 1 ];
		}
		else
		{
			throw new IllegalArgumentException( "the given station is not one of this shot's stations" );
		}
	}
	
	public double rightAt( Station station )
	{
		if( station == from )
		{
			if( fromXsection.type != CrossSectionType.LRUD )
			{
				throw new IllegalArgumentException( "Invalid cross section type" );
			}
			return fromXsection.dist[ 1 ];
		}
		else if( station == to )
		{
			if( toXsection.type != CrossSectionType.LRUD )
			{
				throw new IllegalArgumentException( "Invalid cross section type" );
			}
			return toXsection.dist[ 0 ];
		}
		else
		{
			throw new IllegalArgumentException( "the given station is not one of this shot's stations" );
		}
	}
	
	public CrossSection crossSectionAt( Station station )
	{
		if( station == from )
		{
			return fromXsection;
		}
		else if( station == to )
		{
			return toXsection;
		}
		else
		{
			throw new IllegalArgumentException( "the given station is not one of this shot's stations" );
		}
	}
	
	public void setCrossSectionAt( Station station , CrossSection xSection )
	{
		if( station == from )
		{
			fromXsection = xSection;
		}
		else if( station == to )
		{
			toXsection = xSection;
		}
		else
		{
			throw new IllegalArgumentException( "the given station is not one of this shot's stations" );
		}
	}
	
	private static class PriorityEntry implements Comparable<PriorityEntry>
	{
		public final Shot	shot;
		public final double	priority;
		
		public PriorityEntry( Shot shot , double priority )
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
	
	public Station otherStation( Station station )
	{
		if( station == from )
		{
			return to;
		}
		else if( station == to )
		{
			return from;
		}
		else
		{
			throw new IllegalArgumentException( "not one of this shot's stations" );
		}
	}
	
	public double azimuthAt( Station station )
	{
		if( station == from )
		{
			return azm;
		}
		if( station == to )
		{
			return AngleUtils.oppositeAngle( azm );
		}
		else
		{
			throw new IllegalArgumentException( "not one of this shot's stations" );
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
			if( Double.isNaN( azm ) || Double.isInfinite( azm ) )
			{
				throw new IllegalStateException( "azm is NaN or infinite" );
			}
			if( Double.isNaN( inc ) || Double.isInfinite( inc ) )
			{
				throw new IllegalStateException( "inc is NaN or infinite" );
			}
			
			from.calcedFrom = to;
			
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
			if( Double.isNaN( azm ) || Double.isInfinite( azm ) )
			{
				throw new IllegalStateException( "azm is NaN or infinite" );
			}
			if( Double.isNaN( inc ) || Double.isInfinite( inc ) )
			{
				throw new IllegalStateException( "inc is NaN or infinite" );
			}
			
			to.calcedFrom = from;
			
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
		
		if( Double.isNaN( azm ) )
		{
			azm = Math.atan2( dx , -dz );
		}
		
		if( Double.isNaN( inc ) )
		{
			inc = Math.atan2( dy , dxz );
		}
	}
	
	public static double averageInc( double fsInc , double bsInc )
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
				if( angle > Math.toRadians( 4.0 ) )
				{
					bsInc = -bsInc;
				}
			}
			return ( fsInc + bsInc ) * 0.5;
		}
	}
	
	public static double averageAzm( double avgIncDegrees , double fsAzm , double bsAzm )
	{
		if( Math.abs( avgIncDegrees ) == Math.toRadians( 90.0 ) )
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
		
		fsAzm %= Math.PI * 2.0;
		if( fsAzm < 0 )
		{
			fsAzm += Math.PI * 2.0;
		}
		bsAzm %= Math.PI * 2.0;
		if( bsAzm < 0 )
		{
			bsAzm += Math.PI * 2.0;
		}
		double bsAzm180 = ( bsAzm + Math.PI ) % Math.PI * 2.0;
		if( Math.abs( angle( fsAzm , bsAzm180 ) ) < Math.abs( angle( fsAzm , bsAzm ) ) )
		{
			bsAzm = bsAzm180;
		}
		
		return fsAzm + angle( fsAzm , bsAzm ) * 0.5;
	}
	
	private static double angle( double a , double b )
	{
		double a1 = b - a;
		double a2 = b - a - Math.PI * 2.0;
		
		return Math.abs( a1 ) < Math.abs( a2 ) ? a1 : a2;
	}
	
	public static void computeConnected( Collection<Station> stations )
	{
		Set<Station> visited = new HashSet<Station>( );
		
		for( Station station : stations )
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
	
	public static void computeConnected( Station start , Set<Station> visited )
	{
		if( Vecmath.hasNaNsOrInfinites( start.position ) )
		{
			throw new IllegalArgumentException( "start's position has NaN or infinite values" );
		}
		
		PriorityQueue<PriorityEntry> heap = new PriorityQueue<Shot.PriorityEntry>( 10 );
		
		for( Shot shot : start.shots )
		{
			if( Double.isNaN( shot.dist ) )
			{
				if( Vecmath.hasNaNsOrInfinites( shot.otherStation( start ).position ) )
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
			Station station = null;
			
			Shot shot = heap.poll( ).shot;
			
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
			else if( visited.add( shot.to ) )
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
				if( station.toString( ).equals( "BH80" ) )
				{
					Station temp = station;
					while( temp != null )
					{
						System.out.println( temp );
						temp = temp.calcedFrom;
					}
				}
				
				for( Shot nextShot : station.shots )
				{
					if( nextShot == shot )
					{
						continue;
					}
					if( Double.isNaN( nextShot.dist ) )
					{
						if( Vecmath.hasNaNsOrInfinites( nextShot.otherStation( station ).position ) )
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
	
	public static String getName( String fromName , String toName )
	{
		return String.valueOf( fromName ) + " - " + String.valueOf( toName );
	}
	
	public String getName( )
	{
		return getName( from.name , to.name );
	}
	
	public String toString( )
	{
		return getName( );
	}
}
