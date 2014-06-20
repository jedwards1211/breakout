package org.andork.breakout.model;

import static org.andork.util.StringUtils.toStringOrNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.andork.collect.CollectionUtils;
import org.andork.math3d.Vecmath;
import org.andork.q.QObject;
import org.andork.q.QSpec;
import org.andork.swing.async.Subtask;
import org.andork.swing.table.AnnotatingTableRowSorter.AbstractTableModelCopier;
import org.andork.swing.table.EasyTableModel;
import org.andork.swing.table.QObjectRowFormat;

@SuppressWarnings( "serial" )
public class SurveyTableModel extends EasyTableModel<QObject<SurveyTableModel.Row>>
{
	private Map<Integer, Integer>	shotNumberToRowIndexMap	= CollectionUtils.newHashMap( );
	private final List<SurveyShot>	shots					= new ArrayList<SurveyShot>( );
	
	public SurveyTableModel( )
	{
		super( true );
		setPrototypeFormat( new QObjectRowFormat<Row>( Row.instance ) );
		ensureNumRows( 1 );
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
		public static final Attribute<SurveyShotSide>	xSectionSide	= newAttribute( SurveyShotSide.class , "xSectionSide" );
		public static final Attribute<String>			left			= newAttribute( String.class , "left" );
		public static final Attribute<String>			right			= newAttribute( String.class , "right" );
		public static final Attribute<String>			up				= newAttribute( String.class , "up" );
		public static final Attribute<String>			down			= newAttribute( String.class , "down" );
		public static final Attribute<SurveyShotSide>	positionSide	= newAttribute( SurveyShotSide.class , "positionSide" );
		public static final Attribute<String>			north			= newAttribute( String.class , "north" );
		public static final Attribute<String>			east			= newAttribute( String.class , "east" );
		public static final Attribute<String>			elev			= newAttribute( String.class , "elev" );
		public static final Attribute<String>			desc			= newAttribute( String.class , "desc" );
		public static final Attribute<String>			date			= newAttribute( String.class , "date" );
		public static final Attribute<String>			surveyors		= newAttribute( String.class , "surveyors" );
		public static final Attribute<String>			comment			= newAttribute( String.class , "comment" );
		
		private Row( )
		{
			super( );
		}
		
		public static final Row	instance	= new Row( );
	}
	
	public SurveyShot getShotAtRow( int row )
	{
		return row >= shots.size( ) ? null : shots.get( row );
	}
	
	@Override
	public void setValueAt( Object aValue , int row , int column , boolean fireEvent )
	{
		Object prevValue = getValueAt( row , column );
		if( aValue != null )
		{
			ensureNumRows( row + 2 );
		}
		super.setValueAt( aValue , row , column , fireEvent );
		if( aValue == null || "".equals( aValue ) )
		{
			trimEmptyRows( );
		}
	}
	
	private void trimEmptyRows( )
	{
		for( int row = getRowCount( ) - 2 ; row >= 0 ; row-- )
		{
			for( int column = 0 ; column < getColumnCount( ) ; column++ )
			{
				Object value = getValueAt( row , column );
				if( value != null && !"".equals( value ) )
				{
					return;
				}
			}
			removeRow( row );
		}
	}
	
	public void clear( )
	{
		setShots( Collections.<SurveyShot>emptyList( ) );
		setRows( Collections.singletonList( Row.instance.newObject( ) ) );
	}
	
	private void ensureNumRows( int numRows )
	{
		while( getRowCount( ) < numRows )
		{
			addRow( QObject.newInstance( Row.instance ) );
		}
	}
	
	@Override
	public void copyRowsFrom( EasyTableModel<QObject<Row>> src , int srcStart , int srcEnd , int myStart )
	{
		super.copyRowsFrom( src , srcStart , srcEnd , myStart );
		int row = getRowCount( ) - 1;
		ensureNumRows( 1 );
		for( int col = 0 ; col < getColumnCount( ) ; col++ )
		{
			if( getValueAt( row , col ) != null )
			{
				ensureNumRows( row + 2 );
				break;
			}
		}
	}
	
	public List<SurveyShot> createShots( Subtask subtask )
	{
		if( subtask != null )
		{
			subtask.setTotal( getRowCount( ) );
		}
		
		Map<String, SurveyStation> stations = new LinkedHashMap<String, SurveyStation>( );
		Map<String, SurveyShot> shots = new LinkedHashMap<String, SurveyShot>( );
		
		List<SurveyShot> shotList = new ArrayList<SurveyShot>( );
		
		for( int i = 0 ; i < getRowCount( ) ; i++ )
		{
			QObject<Row> row = getRow( i );
			
			SurveyShot shot = null;
			
			try
			{
				String fromName = toStringOrNull( row.get( Row.from ) );
				String toName = toStringOrNull( row.get( Row.to ) );
				double dist = parse( row.get( Row.distance ) );
				double fsAzm = parse( row.get( Row.fsAzm ) );
				double bsAzm = parse( row.get( Row.bsAzm ) );
				double fsInc = parse( row.get( Row.fsInc ) );
				double bsInc = parse( row.get( Row.bsInc ) );
				
				CrossSectionType xSectionType = row.get( Row.xSectionType );
				if( xSectionType == null )
				{
					xSectionType = CrossSectionType.LRUD;
				}
				SurveyShotSide xSectionSide = row.get( Row.xSectionSide );
				if( xSectionSide == null )
				{
					xSectionSide = SurveyShotSide.AT_FROM;
				}
				
				float left = parseFloat( row.get( Row.left ) );
				float right = parseFloat( row.get( Row.right ) );
				float up = parseFloat( row.get( Row.up ) );
				float down = parseFloat( row.get( Row.down ) );
				
				SurveyShotSide positionSide = row.get( Row.positionSide );
				if( positionSide == null )
				{
					positionSide = SurveyShotSide.AT_FROM;
				}
				
				if( fromName == null || toName == null )
				{
					continue;
				}
				
				shot = shots.get( SurveyShot.getName( fromName , toName ) );
				if( shot == null )
				{
					shot = shots.get( SurveyShot.getName( toName , fromName ) );
					if( shot != null )
					{
						shot = new SurveyShot( );
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
				
				SurveyStation from = getStation( stations , fromName );
				SurveyStation to = getStation( stations , toName );
				
				Vecmath.setdNoNaNOrInf( positionSide == SurveyShotSide.AT_FROM ?
						from.position : to.position , east , elev , -north );
				
				shot = new SurveyShot( );
				shot.from = from;
				shot.to = to;
				shot.dist = dist;
				shot.fsAzm = fsAzm;
				shot.bsAzm = bsAzm;
				shot.fsInc = fsInc;
				shot.bsInc = bsInc;
				
				CrossSection xSection = xSectionSide == SurveyShotSide.AT_FROM ? shot.fromXsection : shot.toXsection;
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
		
		for( SurveyShot shot : shots.values( ) )
		{
			shot.from.frontsights.add( shot );
			shot.to.backsights.add( shot );
		}
		
		int number = 0;
		for( SurveyShot shot : shotList )
		{
			if( shot != null )
			{
				shot.number = number++ ;
			}
		}
		
		return shotList;
	}
	
	private static SurveyStation getStation( Map<String, SurveyStation> stations , String name )
	{
		SurveyStation station = stations.get( name );
		if( station == null )
		{
			station = new SurveyStation( );
			station.name = name;
			stations.put( name , station );
		}
		return station;
	}
	
	private static float coalesceNaNOrInf( float a , float b )
	{
		return Float.isNaN( a ) || Float.isInfinite( a ) ? b : a;
	}
	
	protected String shotName( SurveyShot shot )
	{
		return shot.from.name + " - " + shot.to.name;
	}
	
	public SurveyShot shotAtRow( int rowIndex )
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
	
	public void setShots( List<SurveyShot> shotList )
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
			SurveyShot shot = shots.get( i );
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
