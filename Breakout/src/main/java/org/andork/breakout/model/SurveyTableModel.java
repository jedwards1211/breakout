package org.andork.breakout.model;

import static org.andork.util.StringUtils.toStringOrNull;

import java.text.SimpleDateFormat;
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
	private final List<Shot>	shots					= new ArrayList<Shot>( );
	
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
		public static final Attribute<ShotSide>	xSectionSide	= newAttribute( ShotSide.class , "xSectionSide" );
		public static final Attribute<String>			left			= newAttribute( String.class , "left" );
		public static final Attribute<String>			right			= newAttribute( String.class , "right" );
		public static final Attribute<String>			up				= newAttribute( String.class , "up" );
		public static final Attribute<String>			down			= newAttribute( String.class , "down" );
		public static final Attribute<ShotSide>	positionSide	= newAttribute( ShotSide.class , "positionSide" );
		public static final Attribute<String>			north			= newAttribute( String.class , "north" );
		public static final Attribute<String>			east			= newAttribute( String.class , "east" );
		public static final Attribute<String>			elev			= newAttribute( String.class , "elev" );
		public static final Attribute<String>			desc			= newAttribute( String.class , "desc" );
		public static final Attribute<String>			date			= newAttribute( String.class , "date" );
		public static final Attribute<String>			surveyors		= newAttribute( String.class , "surveyors" );
		public static final Attribute<String>			comment			= newAttribute( String.class , "comment" );
		
		public static final Row							instance		= new Row( );
		
		private Row( )
		{
			super( );
		}
	}
	
	public Shot getShotAtRow( int row )
	{
		return row >= shots.size( ) ? null : shots.get( row );
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
				double fsAzm = parse( row.get( Row.fsAzm ) );
				double bsAzm = parse( row.get( Row.bsAzm ) );
				double fsInc = parse( row.get( Row.fsInc ) );
				double bsInc = parse( row.get( Row.bsInc ) );
				
				CrossSectionType xSectionType = row.get( Row.xSectionType );
				if( xSectionType == null )
				{
					xSectionType = CrossSectionType.LRUD;
				}
				ShotSide xSectionSide = row.get( Row.xSectionSide );
				if( xSectionSide == null )
				{
					xSectionSide = ShotSide.AT_FROM;
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
				shot.fsAzm = fsAzm;
				shot.bsAzm = bsAzm;
				shot.fsInc = fsInc;
				shot.bsInc = bsInc;
				
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
			shot.from.frontsights.add( shot );
			shot.to.backsights.add( shot );
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
