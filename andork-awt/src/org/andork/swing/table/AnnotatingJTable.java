package org.andork.swing.table;

import java.awt.Component;
import java.util.Vector;

import javax.swing.DefaultListSelectionModel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.RowSorter;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.RowSorterEvent;
import javax.swing.event.RowSorterEvent.Type;
import javax.swing.event.TableModelEvent;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import org.andork.swing.AnnotatingRowSorter;

/**
 * A {@link BetterJTable} with the following added features:
 * <ul>
 * <li>It will invoke {@link AnnotatingTableCellRenderer}s with annotations from an {@link AnnotatingRowSorter} if one is installed.
 * </ul>
 * 
 * @author Andy
 */
@SuppressWarnings( "serial" )
public class AnnotatingJTable extends BetterJTable
{
	public AnnotatingJTable( )
	{
		super( );
		init( );
	}
	
	public AnnotatingJTable( int numRows , int numColumns )
	{
		super( numRows , numColumns );
		init( );
	}
	
	public AnnotatingJTable( Object[ ][ ] rowData , Object[ ] columnNames )
	{
		super( rowData , columnNames );
		init( );
	}
	
	public AnnotatingJTable( TableModel dm , TableColumnModel cm , ListSelectionModel sm )
	{
		super( dm , cm , sm );
		init( );
	}
	
	public AnnotatingJTable( TableModel dm , TableColumnModel cm )
	{
		super( dm , cm );
		init( );
	}
	
	public AnnotatingJTable( TableModel dm )
	{
		super( dm );
		init( );
	}
	
	public AnnotatingJTable( Vector rowData , Vector columnNames )
	{
		super( rowData , columnNames );
		init( );
	}
	
	@Override
	public Component prepareRenderer( TableCellRenderer renderer , int row , int column )
	{
		if( renderer instanceof AnnotatingTableCellRenderer )
		{
			Object value = getValueAt( row , column );
			
			boolean isSelected = false;
			boolean hasFocus = false;
			
			// Only indicate the selection and focused cell if not printing
			if( !isPaintingForPrint( ) )
			{
				isSelected = isCellSelected( row , column );
				
				boolean rowIsLead =
						( selectionModel.getLeadSelectionIndex( ) == row );
				boolean colIsLead =
						( columnModel.getSelectionModel( ).getLeadSelectionIndex( ) == column );
				
				hasFocus = ( rowIsLead && colIsLead ) && isFocusOwner( );
			}
			
			return ( ( AnnotatingTableCellRenderer ) renderer ).getTableCellRendererComponent(
					this , value , getAnnotation( row ) , isSelected , hasFocus , row , column );
		}
		
		return super.prepareRenderer( renderer , row , column );
	}
	
	public Object getAnnotation( int row )
	{
		if( getRowSorter( ) instanceof AnnotatingRowSorter )
		{
			return ( ( AnnotatingRowSorter<?, ?, ?> ) getRowSorter( ) ).getAnnotation( row );
		}
		return null;
	}
}
