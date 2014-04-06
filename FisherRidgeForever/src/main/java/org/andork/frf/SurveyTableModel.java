package org.andork.frf;

import java.util.Map;

import org.andork.collect.CollectionUtils;
import org.andork.frf.model.SurveyShot;
import org.andork.snakeyaml.YamlObject;
import org.andork.snakeyaml.YamlSpec;
import org.andork.swing.table.AnnotatingTableRowSorter.AbstractTableModelCopier;
import org.andork.swing.table.EasyTableModel;

public class SurveyTableModel extends EasyTableModel<YamlObject<SurveyTableModel.Row>>
{
	public SurveyTableModel( )
	{
		super( true );
		setPrototypeFormat( new YamlObjectRowFormat<Row>( Row.instance ) );
		ensureNumRows( 1 );
	}
	
	public static class Row extends YamlSpec<Row>
	{
		public static final Attribute<String>		from		= stringAttribute( "from" );
		public static final Attribute<String>		to			= stringAttribute( "to" );
		public static final Attribute<String>		distance	= stringAttribute( "dist" );
		public static final Attribute<String>		fsAzm		= stringAttribute( "fsAzm" );
		public static final Attribute<String>		fsInc		= stringAttribute( "fsInc" );
		public static final Attribute<String>		bsAzm		= stringAttribute( "bsAzm" );
		public static final Attribute<String>		bsInc		= stringAttribute( "bsInc" );
		public static final Attribute<String>		left		= stringAttribute( "left" );
		public static final Attribute<String>		right		= stringAttribute( "right" );
		public static final Attribute<String>		up			= stringAttribute( "up" );
		public static final Attribute<String>		down		= stringAttribute( "down" );
		public static final Attribute<String>		fwd			= stringAttribute( "fwd" );
		public static final Attribute<String>		back		= stringAttribute( "back" );
		public static final Attribute<String>		north		= stringAttribute( "north" );
		public static final Attribute<String>		east		= stringAttribute( "east" );
		public static final Attribute<String>		elev		= stringAttribute( "elev" );
		public static final Attribute<String>		leftNorth	= stringAttribute( "leftNorth" );
		public static final Attribute<String>		leftEast	= stringAttribute( "leftEast" );
		public static final Attribute<String>		leftElev	= stringAttribute( "leftElev" );
		public static final Attribute<String>		rightNorth	= stringAttribute( "rightNorth" );
		public static final Attribute<String>		rightEast	= stringAttribute( "rightEast" );
		public static final Attribute<String>		rightElev	= stringAttribute( "rightElev" );
		public static final Attribute<String>		fwdNorth	= stringAttribute( "fwdNorth" );
		public static final Attribute<String>		fwdEast		= stringAttribute( "fwdEast" );
		public static final Attribute<String>		fwdElev		= stringAttribute( "fwdElev" );
		public static final Attribute<String>		backNorth	= stringAttribute( "backNorth" );
		public static final Attribute<String>		backEast	= stringAttribute( "backEast" );
		public static final Attribute<String>		backElev	= stringAttribute( "backElev" );
		public static final Attribute<SurveyShot>	shot		= Attribute.newInstance( SurveyShot.class , "shot" , NullBimapper.instance );
		
		private Row( )
		{
			super( );
		}
		
		public static final Row	instance	= new Row( );
		
	}
	
	private Map<Integer, Integer>	shotIndexToRowMap	= CollectionUtils.newHashMap( );
	
	public SurveyShot getShotAtRow( int row )
	{
		return ( SurveyShot ) getValueAt( row , SurveyTable.SHOT_COLUMN );
	}
	
	@Override
	public void setValueAt( Object aValue , int row , int column )
	{
		Object prevValue = getValueAt( row , column );
		if( aValue != null )
		{
			ensureNumRows( row + 2 );
		}
		super.setValueAt( aValue , row , column );
		if( aValue == null || "".equals( aValue ) )
		{
			trimEmptyRows( );
		}
		
		if( column == SurveyTable.SHOT_COLUMN )
		{
			SurveyShot shot = ( SurveyShot ) aValue;
			if( prevValue != null )
			{
				shotIndexToRowMap.remove( ( ( SurveyShot ) prevValue ).index );
			}
			if( shot != null )
			{
				shotIndexToRowMap.put( shot.index , row );
			}
		}
	}
	
	public void rebuildShotIndexToRowMap( )
	{
		shotIndexToRowMap.clear( );
		
		for( int i = 0 ; i < getRowCount( ) ; i++ )
		{
			YamlObject<Row> row = getRow( i );
			SurveyShot shot = row.get( Row.shot );
			if( shot != null )
			{
				shotIndexToRowMap.put( shot.index , i );
			}
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
	
	private void ensureNumRows( int numRows )
	{
		while( getRowCount( ) < numRows )
		{
			addRow( YamlObject.newInstance( Row.instance ) );
		}
	}
	
	public int rowOfShot( int shotIndex )
	{
		Integer row = shotIndexToRowMap.get( shotIndex );
		return row == null ? -1 : row;
	}
	
	public static class SurveyTableModelCopier extends AbstractTableModelCopier<SurveyTableModel>
	{
		@Override
		public SurveyTableModel createEmptyCopy( SurveyTableModel model )
		{
			return new SurveyTableModel( );
		}
	}
}
