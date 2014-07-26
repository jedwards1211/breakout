package org.andork.breakout.table;

import java.awt.event.ActionEvent;
import java.util.Arrays;

import javax.swing.AbstractAction;
import javax.swing.table.TableCellEditor;

import org.andork.format.FormattedText;
import org.andork.swing.FormatAndDisplayInfo;

@SuppressWarnings( "serial" )
public class NewSurveyTableBatchSetFormatAction extends AbstractAction
{
	NewSurveyTable			table;
	FormatAndDisplayInfo<?>	format;
	int						column;
	
	TableCellEditor			editor;
	
	public NewSurveyTableBatchSetFormatAction( NewSurveyTable table , int column , FormatAndDisplayInfo<?> format )
	{
		putValue( NAME , format.description( ) );
		putValue( SMALL_ICON , format.icon( ) );
		putValue( LARGE_ICON_KEY , format.icon( ) );
		this.table = table;
		this.column = column;
		this.format = format;
	}
	
	@Override
	public void actionPerformed( ActionEvent e )
	{
		int[ ] selRows = table.getSelectedRows( );
		
		if( selRows.length == 0 )
		{
			return;
		}
		
		Object[ ][ ] newValues = new Object[ selRows.length ][ 1 ];
		
		for( int i = 0 ; i < selRows.length ; i++ )
		{
			newValues[ i ][ 0 ] = table.getValueAt( selRows[ i ] , column );
			if( newValues[ i ][ 0 ] instanceof FormattedText )
			{
				newValues[ i ][ 0 ] = new FormattedText( ( ( FormattedText ) newValues[ i ][ 0 ] ).getText( ) , format );
			}
		}
		
		table.getModel( ).blockSetValues( Arrays.asList( newValues ) ,
				selRowIndex -> table.convertRowIndexToModel( selRows[ selRowIndex ] ) ,
				c -> column );
	}
}
