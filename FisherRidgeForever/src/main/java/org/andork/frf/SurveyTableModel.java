package org.andork.frf;

import java.util.Vector;

import javax.swing.table.DefaultTableModel;

public class SurveyTableModel extends DefaultTableModel
{
	public SurveyTableModel( )
	{
		super( new Object[ ] { "From" , "To" , "Distance" , "FS Azm" , "FS Inc" , "BS Azm" , "BS Inc" , "L" , "R" , "U" , "D" , "Shot" } , 1 );
	}
	
	@Override
	public void setValueAt( Object aValue , int row , int column )
	{
		if( aValue != null )
		{
			ensureNumRows( row + 2 );
		}
		super.setValueAt( aValue , row , column );
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
}
