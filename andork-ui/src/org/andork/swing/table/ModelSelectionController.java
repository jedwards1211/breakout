package org.andork.swing.table;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.RowSorterEvent;
import javax.swing.event.RowSorterListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

public class ModelSelectionController implements ListSelectionListener , RowSorterListener , TableModelListener
{
	JTable				table;
	
	ListSelectionModel	viewSelectionModel;
	ListSelectionModel	modelSelectionModel;
	
	public ModelSelectionController( JTable table , ListSelectionModel modelSelectionModel )
	{
		this.table = table;
		this.viewSelectionModel = table.getSelectionModel( );
		this.modelSelectionModel = modelSelectionModel;
		
		table.getModel( ).addTableModelListener( this );
		viewSelectionModel.addListSelectionListener( this );
	}
	
	@Override
	public void sorterChanged( RowSorterEvent e )
	{
		
	}
	
	@Override
	public void valueChanged( ListSelectionEvent e )
	{
		for( int viewIndex = Math.max( 0 , e.getFirstIndex( ) ) ; viewIndex <= Math.min( table.getRowCount( ) - 1 , e.getLastIndex( ) ) ; viewIndex++ )
		{
			int modelIndex = table.convertRowIndexToModel( viewIndex );
			
			if( !viewSelectionModel.isSelectedIndex( viewIndex ) )
			{
				modelSelectionModel.removeSelectionInterval( modelIndex , modelIndex );
			}
			else
			{
				modelSelectionModel.addSelectionInterval( modelIndex , modelIndex );
			}
		}
		
		int anchor = viewSelectionModel.getAnchorSelectionIndex( );
		if( anchor >= 0 )
		{
			anchor = table.convertRowIndexToModel( anchor );
		}
		modelSelectionModel.setAnchorSelectionIndex( anchor );
		
		int lead = viewSelectionModel.getLeadSelectionIndex( );
		if( lead >= 0 )
		{
			lead = table.convertRowIndexToModel( lead );
		}
		modelSelectionModel.setLeadSelectionIndex( lead );
		
		modelSelectionModel.setValueIsAdjusting( viewSelectionModel.getValueIsAdjusting( ) );
	}
	
	@Override
	public void tableChanged( TableModelEvent e )
	{
		if( e.getType( ) == TableModelEvent.INSERT )
		{
			modelSelectionModel.insertIndexInterval( e.getFirstRow( ) , e.getLastRow( ) - e.getFirstRow( ) + 1 , true );
		}
		else if( e.getType( ) == TableModelEvent.DELETE )
		{
			modelSelectionModel.removeIndexInterval( e.getFirstRow( ) , e.getLastRow( ) );
		}
	}
}