package org.andork.breakout.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.andork.collect.CollectionUtils;
import org.andork.math3d.Vecmath;
import org.andork.snakeyaml.YamlObject;
import org.andork.snakeyaml.YamlSpec;
import org.andork.swing.async.Subtask;
import org.andork.swing.table.AnnotatingTableRowSorter.AbstractTableModelCopier;
import org.andork.swing.table.EasyTableModel;

@SuppressWarnings( "serial" )
public class SurveyTableModel extends EasyTableModel<YamlObject<SurveyTableModel.Row>>
{
	private Map<Integer, Integer>	shotNumberToRowIndexMap	= CollectionUtils.newHashMap( );
	private final List<SurveyShot>	shots					= new ArrayList<SurveyShot>( );
	
	public SurveyTableModel( )
	{
		super( true );
		setPrototypeFormat( new YamlObjectRowFormat<Row>( Row.instance ) );
		ensureNumRows( 1 );
	}
	
	public static class Row extends YamlSpec<Row>
	{
		public static final Attribute<String>	from		= stringAttribute( "from" );
		public static final Attribute<String>	to			= stringAttribute( "to" );
		public static final Attribute<String>	distance	= stringAttribute( "dist" );
		public static final Attribute<String>	fsAzm		= stringAttribute( "fsAzm" );
		public static final Attribute<String>	fsInc		= stringAttribute( "fsInc" );
		public static final Attribute<String>	bsAzm		= stringAttribute( "bsAzm" );
		public static final Attribute<String>	bsInc		= stringAttribute( "bsInc" );
		public static final Attribute<String>	left		= stringAttribute( "left" );
		public static final Attribute<String>	right		= stringAttribute( "right" );
		public static final Attribute<String>	up			= stringAttribute( "up" );
		public static final Attribute<String>	down		= stringAttribute( "down" );
		public static final Attribute<String>	fwd			= stringAttribute( "fwd" );
		public static final Attribute<String>	back		= stringAttribute( "back" );
		public static final Attribute<String>	north		= stringAttribute( "north" );
		public static final Attribute<String>	east		= stringAttribute( "east" );
		public static final Attribute<String>	elev		= stringAttribute( "elev" );
		public static final Attribute<String>	leftNorth	= stringAttribute( "leftNorth" );
		public static final Attribute<String>	leftEast	= stringAttribute( "leftEast" );
		public static final Attribute<String>	leftElev	= stringAttribute( "leftElev" );
		public static final Attribute<String>	rightNorth	= stringAttribute( "rightNorth" );
		public static final Attribute<String>	rightEast	= stringAttribute( "rightEast" );
		public static final Attribute<String>	rightElev	= stringAttribute( "rightElev" );
		public static final Attribute<String>	fwdNorth	= stringAttribute( "fwdNorth" );
		public static final Attribute<String>	fwdEast		= stringAttribute( "fwdEast" );
		public static final Attribute<String>	fwdElev		= stringAttribute( "fwdElev" );
		public static final Attribute<String>	backNorth	= stringAttribute( "backNorth" );
		public static final Attribute<String>	backEast	= stringAttribute( "backEast" );
		public static final Attribute<String>	backElev	= stringAttribute( "backElev" );
		public static final Attribute<String>	desc		= stringAttribute( "desc" );
		public static final Attribute<String>	date		= stringAttribute( "date" );
		public static final Attribute<String>	surveyors	= stringAttribute( "surveyors" );
		public static final Attribute<String>	comment		= stringAttribute( "comment" );
		
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
		setRows( Collections.singletonList( Row.instance.newObject( ) ) );
	}
	
	private void ensureNumRows( int numRows )
	{
		while( getRowCount( ) < numRows )
		{
			addRow( YamlObject.newInstance( Row.instance ) );
		}
	}
	
	@Override
	public void copyRowsFrom( EasyTableModel<YamlObject<Row>> src , int srcStart , int srcEnd , int myStart )
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
			YamlObject<Row> row = getRow( i );
			
			SurveyShot shot = null;
			
			try
			{
				Object fromName = row.get( Row.from );
				Object toName = row.get( Row.to );
				double dist = parse( row.get( Row.distance ) );
				double fsAzm = parse( row.get( Row.fsAzm ) );
				double bsAzm = parse( row.get( Row.bsAzm ) );
				double fsInc = parse( row.get( Row.fsInc ) );
				double bsInc = parse( row.get( Row.bsInc ) );
				
				if( fromName == null || toName == null || Double.isNaN( dist ) ||
						( Double.isNaN( fsInc ) && Double.isNaN( bsInc ) ) )
				{
					continue;
				}
				
				double left = parse( row.get( Row.left ) );
				double right = parse( row.get( Row.right ) );
				double up = parse( row.get( Row.up ) );
				double down = parse( row.get( Row.down ) );
				
				double north = parse( row.get( Row.north ) );
				double east = parse( row.get( Row.east ) );
				double elev = parse( row.get( Row.elev ) );
				
				SurveyStation from = stations.get( fromName.toString( ) );
				if( from == null )
				{
					from = new SurveyStation( );
					Vecmath.setd( from.position , east , elev , -north );
					from.name = fromName.toString( );
					stations.put( from.name , from );
				}
				if( !Double.isNaN( north ) && !Double.isInfinite( north ) )
				{
					from.position[ 2 ] = -north;
				}
				if( !Double.isNaN( east ) && !Double.isInfinite( east ) )
				{
					from.position[ 0 ] = east;
				}
				if( !Double.isNaN( elev ) && !Double.isInfinite( elev ) )
				{
					from.position[ 1 ] = elev;
				}
				
				SurveyStation to = stations.get( toName.toString( ) );
				if( to == null )
				{
					to = new SurveyStation( );
					to.name = toName.toString( );
					stations.put( to.name , to );
					
					Arrays.fill( to.position , Double.NaN );
				}
				
				shot = new SurveyShot( );
				shot.from = from;
				shot.to = to;
				shot.dist = dist;
				shot.fsAzm = fsAzm;
				shot.bsAzm = bsAzm;
				shot.fsInc = fsInc;
				shot.bsInc = bsInc;
				shot.left = Double.isNaN( left ) ? 0.0 : left;
				shot.right = Double.isNaN( right ) ? 0.0 : right;
				shot.up = Double.isNaN( up ) ? 0.0 : up;
				shot.down = Double.isNaN( down ) ? 0.0 : down;
				
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
					shots.put( shot.from.name + " - " + shot.to.name , shot );
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
