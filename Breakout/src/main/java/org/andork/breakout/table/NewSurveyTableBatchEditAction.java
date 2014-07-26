package org.andork.breakout.table;

import java.awt.event.ActionEvent;
import java.util.Arrays;

import javax.swing.AbstractAction;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableCellEditor;

@SuppressWarnings( "serial" )
public class NewSurveyTableBatchEditAction extends AbstractAction implements CellEditorListener
{
	NewSurveyTable	table;
	int				row;
	int				column;
	
	TableCellEditor	editor;
	
	public NewSurveyTableBatchEditAction( NewSurveyTable table )
	{
		this( table , -1 , -1 );
	}
	
	public NewSurveyTableBatchEditAction( NewSurveyTable table , int row , int column )
	{
		super( "Edit All Selected Rows" );
		this.table = table;
		setCell( row , column );
	}
	
	public void setCell( int row , int column )
	{
		this.row = row;
		this.column = column;
		setEnabled( row >= 0 && column >= 0 && table.isCellEditable( row , column ) );
	}
	
	@Override
	public void actionPerformed( ActionEvent e )
	{
		if( row < 0 || column < 0 )
		{
			return;
		}
		
		table.editCellAt( row , column );
		editor = table.getCellEditor( );
		if( editor != null )
		{
			editor.addCellEditorListener( this );
		}
	}
	
	@Override
	public void editingStopped( ChangeEvent e )
	{
		if( editor == null )
		{
			return;
		}
		
		int[ ] selRows = table.getSelectedRows( );
		
		Object[ ][ ] values = new Object[ selRows.length ][ 1 ];
		for( int i = 0 ; i < selRows.length ; i++ )
		{
			values[ i ][ 0 ] = editor.getCellEditorValue( );
		}
		
		editor.removeCellEditorListener( this );
		editor = null;
		
		table.getModel( ).blockSetValues( Arrays.asList( values ) ,
				selRowIndex -> table.convertRowIndexToModel( selRows[ selRowIndex ] ) ,
				c -> table.convertColumnIndexToModel( column ) );
	}
	
	@Override
	public void editingCanceled( ChangeEvent e )
	{
		if( editor != null )
		{
			editor.removeCellEditorListener( this );
			editor = null;
		}
	}
}
