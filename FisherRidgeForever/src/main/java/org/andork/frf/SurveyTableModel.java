package org.andork.frf;

import java.util.Map;
import java.util.Vector;

import javax.swing.table.DefaultTableModel;

import org.andork.collect.CollectionUtils;
import org.andork.frf.model.SurveyShot;
import org.andork.swing.table.AnnotatingTableRowSorter.AbstractDefaultTableModelCopier;

public class SurveyTableModel extends DefaultTableModel
{
	public SurveyTableModel( )
	{
		super( new Object[ ] { "From" , "To" , "Distance" , "FS Azm" , "FS Inc" , "BS Azm" , "BS Inc" , "L" , "R" , "U" , "D" , "Shot" } , 1 );
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
			Vector<Object> row = new Vector<Object>( );
			for( int column = 0 ; column < getColumnCount( ) ; column++ )
			{
				row.add( null );
			}
			addRow( row );
		}
	}
	
	public int rowOfShot( int shotIndex )
	{
		Integer row = shotIndexToRowMap.get( shotIndex );
		return row == null ? -1 : row;
	}
	
	public static class SurveyTableModelCopier extends AbstractDefaultTableModelCopier<SurveyTableModel>
	{
		@Override
		public SurveyTableModel createEmptyCopy( SurveyTableModel model )
		{
			return new SurveyTableModel( );
		}
	}
}
