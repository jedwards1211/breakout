package org.andork.swing.event;

import javax.swing.event.TableModelEvent;
import javax.swing.table.TableModel;

public enum TableModelEventInterpretation
{
	TABLE_STRUCTURE_CHANGED ,
	TABLE_DATA_CHANGED ,
	ROWS_INSERTED ,
	ROWS_DELETED ,
	ROWS_UPDATED ,
	COLUMN_INSERTED ,
	COLUMN_DELETED ,
	COLUMN_UPDATED ,
	CELLS_UPDATED;
	
	public static TableModelEventInterpretation interpret( TableModelEvent event )
	{
		if( event.getColumn( ) == TableModelEvent.ALL_COLUMNS )
		{
			switch( event.getType( ) )
			{
				case TableModelEvent.INSERT:
					if( event.getFirstRow( ) < 0 )
					{
						break;
					}
					return ROWS_INSERTED;
				case TableModelEvent.UPDATE:
					if( event.getFirstRow( ) == TableModelEvent.HEADER_ROW )
					{
						return TABLE_STRUCTURE_CHANGED;
					}
					if( event.getFirstRow( ) == 0 && event.getLastRow( ) >= ( ( TableModel ) event.getSource( ) ).getRowCount( ) )
					{
						return TABLE_DATA_CHANGED;
					}
					return ROWS_UPDATED;
				case TableModelEvent.DELETE:
					if( event.getFirstRow( ) < 0 )
					{
						break;
					}
					return ROWS_DELETED;
			}
		}
		else
		{
			switch( event.getType( ) )
			{
				case TableModelEvent.INSERT:
					if( event.getFirstRow( ) != TableModelEvent.HEADER_ROW )
					{
						break;
					}
					return COLUMN_INSERTED;
				case TableModelEvent.UPDATE:
					return event.getFirstRow( ) == TableModelEvent.HEADER_ROW ? COLUMN_UPDATED : CELLS_UPDATED;
				case TableModelEvent.DELETE:
					if( event.getFirstRow( ) != TableModelEvent.HEADER_ROW )
					{
						break;
					}
					return COLUMN_DELETED;
			}
		}
		throw new IllegalArgumentException( "What the hell is this? " + event );
	}
}
