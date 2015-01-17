package org.andork.breakout.tableimport;

import java.util.Vector;

import javax.swing.table.DefaultTableModel;

/**
 * A {@link DefaultTableModel} whose {@link #getValueAt(int, int)} method allows row {@link Vector}s to have different
 * lengths. If you try to {@code getValueAt(rowIndex, columnIndex)} and the row at {@code rowIndex} has less than
 * {@code columnIndex + 1} elements, it will return {@code null}.
 * 
 * @author James
 */
@SuppressWarnings( { "serial" , "unchecked" , "rawtypes" } )
public class IrregularDefaultTableModel extends DefaultTableModel
{
	public IrregularDefaultTableModel( )
	{
		super( );
	}

	public IrregularDefaultTableModel( int rowCount , int columnCount )
	{
		super( rowCount , columnCount );
	}

	public IrregularDefaultTableModel( Object[ ] columnNames , int rowCount )
	{
		super( columnNames , rowCount );
	}

	public IrregularDefaultTableModel( Object[ ][ ] data , Object[ ] columnNames )
	{
		super( data , columnNames );
	}

	public IrregularDefaultTableModel( Vector columnNames , int rowCount )
	{
		super( columnNames , rowCount );
	}

	public IrregularDefaultTableModel( Vector data , Vector columnNames )
	{
		super( data , columnNames );
	}

	@Override
	public Object getValueAt( int rowIndex , int columnIndex )
	{
		Vector<Object> row = ( Vector<Object> ) dataVector.get( rowIndex );
		return row.size( ) >= columnIndex + 1 ? row.get( columnIndex ) : null;
	}
}
