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

import static org.andork.util.StringUtils.toStringOrNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.StreamSupport;

import javax.xml.transform.stream.StreamSource;

import org.andork.collect.CollectionUtils;
import org.andork.math.misc.AngleUtils;
import org.andork.math3d.Vecmath;
import org.andork.q.QObject;
import org.andork.q.QSpec;
import org.andork.swing.async.Subtask;
import org.andork.swing.table.AnnotatingTableRowSorter.AbstractTableModelCopier;
import org.andork.swing.table.EasyTableModel;
import org.andork.swing.table.QObjectRowFormat;
import org.andork.util.ArrayUtils;

@SuppressWarnings( "serial" )
public class SurveyTableModel extends EasyTableModel<QObject<SurveyTableModel.Row>>
{
	private Map<Integer, Integer>	shotNumberToRowIndexMap	= CollectionUtils.newHashMap( );
	private final List<Shot>		shots					= new ArrayList<Shot>( );
	
	public SurveyTableModel( )
	{
		super( true );
		setPrototypeFormat( new QObjectRowFormat<Row>( Row.instance ) );
		fixEndRows( );
	}
	
	public static class Row extends QSpec<Row>
	{
		public static final Attribute<String>			from			= newAttribute( String.class , "from" );
		public static final Attribute<String>			to				= newAttribute( String.class , "to" );
		public static final Attribute<String>			distance		= newAttribute( String.class , "dist" );
		public static final Attribute<String>			fsAzm			= newAttribute( String.class , "fsAzm" );
		public static final Attribute<String>			fsInc			= newAttribute( String.class , "fsInc" );
		public static final Attribute<String>			bsAzm			= newAttribute( String.class , "bsAzm" );
		public static final Attribute<String>			bsInc			= newAttribute( String.class , "bsInc" );
		public static final Attribute<CrossSectionType>	xSectionType	= newAttribute( CrossSectionType.class , "xSectionType" );
		public static final Attribute<ShotSide>			xSectionSide	= newAttribute( ShotSide.class , "xSectionSide" );
		public static final Attribute<String>			left			= newAttribute( String.class , "left" );
		public static final Attribute<String>			right			= newAttribute( String.class , "right" );
		public static final Attribute<String>			up				= newAttribute( String.class , "up" );
		public static final Attribute<String>			down			= newAttribute( String.class , "down" );
		public static final Attribute<ShotSide>			positionSide	= newAttribute( ShotSide.class , "positionSide" );
		public static final Attribute<String>			north			= newAttribute( String.class , "north" );
		public static final Attribute<String>			east			= newAttribute( String.class , "east" );
		public static final Attribute<String>			elev			= newAttribute( String.class , "elev" );
		public static final Attribute<String>			desc			= newAttribute( String.class , "desc" );
		public static final Attribute<String>			date			= newAttribute( String.class , "date" );
		public static final Attribute<String>			surveyors		= newAttribute( String.class , "surveyors" );
		public static final Attribute<String>			comment			= newAttribute( String.class , "comment" );
		public static final Attribute<String>			scannedNotes	= newAttribute( String.class , "scannedNotes" );
		
		public static final Row							instance		= new Row( );
		
		private Row( )
		{
			super( );
		}
	}
	
	private boolean isEmpty( int row )
	{
		for( int column = 0 ; column < getColumnCount( ) ; column++ )
		{
			Object value = getValueAt( row , column );
			if( value != null && !"".equals( value ) )
			{
				return false;
			}
		}
		return true;
	}
	
	private void fixEndRows( )
	{
		int startOfEmptyRows = getRowCount( );
		while( startOfEmptyRows > 0 && isEmpty( startOfEmptyRows - 1 ) )
		{
			startOfEmptyRows-- ;
		}
		
		if( startOfEmptyRows == getRowCount( ) )
		{
			addRow( Row.instance.newObject( ) );
		}
		else if( startOfEmptyRows <= getRowCount( ) - 2 )
		{
			removeRows( startOfEmptyRows , getRowCount( ) - 2 );
		}
	}
	
	@Override
	public void setRow( int index , QObject<Row> row )
	{
		while( index >= getRowCount( ) )
		{
			addRow( Row.instance.newObject( ) );
		}
		super.setRow( index , row );
		if( index >= getRowCount( ) - 2 )
		{
			fixEndRows( );
		}
	}
	
	@Override
	public void setValueAt( Object aValue , int row , int column , boolean fireEvent )
	{
		while( row >= getRowCount( ) )
		{
			addRow( Row.instance.newObject( ) );
		}
		Object prevValue = getValueAt( row , column );
		super.setValueAt( aValue , row , column , fireEvent );
		if( ( prevValue == null || "".equals( prevValue ) != ( aValue == null || "".equals( aValue ) ) ) )
		{
			fixEndRows( );
		}
	}
	
	public void clear( )
	{
		setShots( Collections.<Shot>emptyList( ) );
		setRows( Collections.singletonList( Row.instance.newObject( ) ) );
	}
	
	@Override
	public void copyRowsFrom( EasyTableModel<QObject<Row>> src , int srcStart , int srcEnd , int myStart )
	{
		super.copyRowsFrom( src , srcStart , srcEnd , myStart );
		fixEndRows( );
	}
	
	public List<Shot> createShots( Subtask subtask )
	{
		if( subtask != null )
		{
			subtask.setTotal( getRowCount( ) );
		}
		
		Map<String, Station> stations = new LinkedHashMap<String, Station>( );
		Map<String, Shot> shots = new LinkedHashMap<String, Shot>( );
		
		SimpleDateFormat dateFormat = new SimpleDateFormat( "yyyy-MM-dd" );
		
		List<Shot> shotList = new ArrayList<Shot>( );
		
		for( int i = 0 ; i < getRowCount( ) ; i++ )
		{
			QObject<Row> row = getRow( i );
			
			Shot shot = null;
			
			try
			{
				String fromName = toStringOrNull( row.get( Row.from ) );
				String toName = toStringOrNull( row.get( Row.to ) );
				double dist = parse( row.get( Row.distance ) );
				double fsAzm = Math.toRadians( parse( row.get( Row.fsAzm ) ) );
				double bsAzm = Math.toRadians( parse( row.get( Row.bsAzm ) ) );
				double fsInc = Math.toRadians( parse( row.get( Row.fsInc ) ) );
				double bsInc = Math.toRadians( parse( row.get( Row.bsInc ) ) );
				
				CrossSectionType xSectionType = row.get( Row.xSectionType );
				if( xSectionType == null )
				{
					xSectionType = CrossSectionType.LRUD;
				}
				ShotSide xSectionSide = row.get( Row.xSectionSide );
				if( xSectionSide == null )
				{
					xSectionSide = ShotSide.AT_TO;
				}
				
				float left = parseFloat( row.get( Row.left ) );
				float right = parseFloat( row.get( Row.right ) );
				float up = parseFloat( row.get( Row.up ) );
				float down = parseFloat( row.get( Row.down ) );
				
				ShotSide positionSide = row.get( Row.positionSide );
				if( positionSide == null )
				{
					positionSide = ShotSide.AT_FROM;
				}
				
				if( fromName == null || toName == null )
				{
					continue;
				}
				
				shot = shots.get( Shot.getName( fromName , toName ) );
				if( shot == null )
				{
					shot = shots.get( Shot.getName( toName , fromName ) );
					if( shot != null )
					{
						shot = new Shot( );
						String s = fromName;
						fromName = toName;
						toName = s;
						
						double d = fsAzm;
						fsAzm = bsAzm;
						bsAzm = d;
						
						d = fsInc;
						fsInc = bsInc;
						bsInc = d;
						
						xSectionSide = xSectionSide.opposite( );
						positionSide = positionSide.opposite( );
					}
					else
					{
						if( Double.isNaN( dist ) || ( ( Double.isNaN( fsInc ) && Double.isNaN( bsInc ) ) ) )
						{
							continue;
						}
					}
				}
				
				double north = parse( row.get( Row.north ) );
				double east = parse( row.get( Row.east ) );
				double elev = parse( row.get( Row.elev ) );
				
				Station from = getStation( stations , fromName );
				Station to = getStation( stations , toName );
				
				Vecmath.setdNoNaNOrInf( positionSide == ShotSide.AT_FROM ?
						from.position : to.position , east , elev , -north );
				
				shot = new Shot( );
				shot.from = from;
				shot.to = to;
				shot.dist = dist;
				shot.inc = Shot.averageInc( fsInc , bsInc );
				shot.azm = Shot.averageAzm( shot.inc , fsAzm , bsAzm );
				shot.desc = row.get( Row.desc );
				
				try
				{
					shot.date = dateFormat.parse( row.get( Row.date ) );
				}
				catch( Exception ex )
				{
					
				}
				
				CrossSection xSection = xSectionSide == ShotSide.AT_FROM ? shot.fromXsection : shot.toXsection;
				xSection.type = xSectionType;
				xSection.dist[ 0 ] = coalesceNaNOrInf( left , xSection.dist[ 0 ] );
				xSection.dist[ 1 ] = coalesceNaNOrInf( right , xSection.dist[ 1 ] );
				xSection.dist[ 2 ] = coalesceNaNOrInf( up , xSection.dist[ 2 ] );
				xSection.dist[ 3 ] = coalesceNaNOrInf( down , xSection.dist[ 3 ] );
				
				if( subtask != null )
				{
					if( subtask.isCanceling( ) )
					{
						return null;
					}
					subtask.setCompleted( i );
				}
			}
			catch( Exception ex )
			{
				shot = null;
			}
			finally
			{
				if( shot != null )
				{
					shots.put( shotName( shot ) , shot );
				}
				// DO add null shots to shotList
				shotList.add( shot );
			}
		}
		
		for( Shot shot : shots.values( ) )
		{
			shot.from.shots.add( shot );
			shot.to.shots.add( shot );
		}
		
		for( Station station : stations.values( ) )
		{
			updateCrossSections( station );
		}
		
		int number = 0;
		for( Shot shot : shotList )
		{
			if( shot != null )
			{
				shot.number = number++ ;
			}
		}
		
		return shotList;
	}
	
	private static void updateCrossSections( Station station )
	{
		if( station.shots.size( ) == 2 )
		{
			Iterator<Shot> shotIter = station.shots.iterator( );
			Shot shot1 = shotIter.next( );
			Shot shot2 = shotIter.next( );
			CrossSection sect1 = shot1.crossSectionAt( station );
			CrossSection sect2 = shot2.crossSectionAt( station );
			
			boolean opposite = ( station == shot1.from ) == ( station == shot2.from );
			
			for( int i = 0 ; i < Math.min( sect1.dist.length , sect2.dist.length ) ; i++ )
			{
				int oi = i > 1 ? i : opposite ? 1 - i : i;
				
				if( Double.isNaN( sect1.dist[ i ] ) )
				{
					sect1.dist[ i ] = sect2.dist[ oi ];
				}
				if( Double.isNaN( sect2.dist[ i ] ) )
				{
					sect2.dist[ i ] = sect1.dist[ oi ];
				}
			}
		}
		
		int populatedCount = CollectionUtils.moveToFront( station.shots , shot -> {
			CrossSection section = shot.crossSectionAt( station );
			return section.type == CrossSectionType.LRUD && !Double.isNaN( section.dist[ 0 ] ) && !Double.isNaN( section.dist[ 1 ] );
		} );
		
		for( int i = populatedCount ; i < station.shots.size( ) ; i++ )
		{
			Shot shot = station.shots.get( i );
			CrossSection section = shot.crossSectionAt( station );
			if( section.type == CrossSectionType.LRUD )
			{
				double leftAzm = shot.azm - Math.PI * 0.5;
				double rightAzm = shot.azm + Math.PI * 0.5;
				
				boolean populateLeft = Double.isNaN( section.dist[ 0 ] );
				boolean populateRight = Double.isNaN( section.dist[ 0 ] );
				
				for( int i2 = 0 ; i2 < populatedCount ; i2++ )
				{
					Shot populated = station.shots.get( i2 );
					CrossSection popCrossSection = populated.crossSectionAt( station );
					
					double popLeftAzm = populated.azm - Math.PI * 0.5;
					double popRightAzm = populated.azm + Math.PI * 0.5;
					
					if( populateLeft )
					{
						double candidateLeft;
						candidateLeft = popCrossSection.dist[ 0 ] * Math.cos( AngleUtils.angle( leftAzm , popLeftAzm ) );
						section.dist[ 0 ] = ( float ) Vecmath.nmax( section.dist[ 0 ] , candidateLeft );
						candidateLeft = popCrossSection.dist[ 1 ] * Math.cos( AngleUtils.angle( leftAzm , popRightAzm ) );
						section.dist[ 0 ] = ( float ) Vecmath.nmax( section.dist[ 0 ] , candidateLeft );
					}
					
					if( populateRight )
					{
						double candidateRight;
						candidateRight = popCrossSection.dist[ 0 ] * Math.cos( AngleUtils.angle( rightAzm , popLeftAzm ) );
						section.dist[ 1 ] = ( float ) Vecmath.nmax( section.dist[ 1 ] , candidateRight );
						candidateRight = popCrossSection.dist[ 1 ] * Math.cos( AngleUtils.angle( rightAzm , popRightAzm ) );
						section.dist[ 1 ] = ( float ) Vecmath.nmax( section.dist[ 1 ] , candidateRight );
					}
				}
			}
		}
		
		for( Shot shot : station.shots )
		{
			CrossSection sect1 = shot.crossSectionAt( station );
			CrossSection sect2 = shot.crossSectionAt( shot.otherStation( station ) );
			
			for( int i = 0 ; i < Math.min( sect1.dist.length , sect2.dist.length ) ; i++ )
			{
				if( Double.isNaN( sect1.dist[ i ] ) )
				{
					sect1.dist[ i ] = sect2.dist[ i ];
				}
			}
		}
	}
	
	private static Station getStation( Map<String, Station> stations , String name )
	{
		Station station = stations.get( name );
		if( station == null )
		{
			station = new Station( );
			station.name = name;
			stations.put( name , station );
		}
		return station;
	}
	
	private static float coalesceNaNOrInf( float a , float b )
	{
		return Float.isNaN( a ) || Float.isInfinite( a ) ? b : a;
	}
	
	protected String shotName( Shot shot )
	{
		return shot.from.name + " - " + shot.to.name;
	}
	
	public Shot shotAtRow( int rowIndex )
	{
		if( rowIndex < 0 )
		{
			throw new IndexOutOfBoundsException( "row index out of bounds: " + rowIndex + " < 0" );
		}
		if( rowIndex >= getRowCount( ) )
		{
			throw new IndexOutOfBoundsException( "row index out of bounds: " + rowIndex + " >= " + getRowCount( ) );
		}
		return rowIndex < shots.size( ) ? shots.get( rowIndex ) : null;
	}
	
	public void setShots( List<Shot> shotList )
	{
		this.shots.clear( );
		this.shots.addAll( shotList );
		rebuildShotNumberToRowMap( );
	}
	
	public void rebuildShotNumberToRowMap( )
	{
		shotNumberToRowIndexMap.clear( );
		
		for( int i = 0 ; i < shots.size( ) ; i++ )
		{
			Shot shot = shots.get( i );
			if( shot != null )
			{
				shotNumberToRowIndexMap.put( shot.number , i );
			}
		}
	}
	
	public int rowOfShot( int shotNumber )
	{
		Integer row = shotNumberToRowIndexMap.get( shotNumber );
		return row == null ? -1 : row;
	}
	
	private static double parse( Object o )
	{
		if( o == null )
		{
			return Double.NaN;
		}
		try
		{
			return Double.valueOf( o.toString( ) );
		}
		catch( Exception ex )
		{
			return Double.NaN;
		}
	}
	
	private static float parseFloat( Object o )
	{
		if( o == null )
		{
			return Float.NaN;
		}
		try
		{
			return Float.valueOf( o.toString( ) );
		}
		catch( Exception ex )
		{
			return Float.NaN;
		}
	}
	
	public static class SurveyTableModelCopier extends AbstractTableModelCopier<SurveyTableModel>
	{
		@Override
		public SurveyTableModel createEmptyCopy( SurveyTableModel model )
		{
			return new SurveyTableModel( );
		}
		
		public SurveyTableModel copy( SurveyTableModel src )
		{
			SurveyTableModel dest = createEmptyCopy( src );
			for( int row = 0 ; row < src.getRowCount( ) ; row++ )
			{
				copyRow( src , row , dest );
			}
			return dest;
		}
	}
}
