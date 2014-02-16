package org.andork.swing.table;

import java.awt.Component;
import java.util.Vector;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import org.andork.swing.AnnotatingRowSorter;

/**
 * A {@link JTable} that will invoke {@link AnnotatingTableCellRenderer}s with annotations from an {@link AnnotatingRowSorter} if one is installed.
 * 
 * @author Andy
 */
public class AnnotatingJTable extends JTable
{
	public AnnotatingJTable( )
	{
		super( );
	}
	
	public AnnotatingJTable( int numRows , int numColumns )
	{
		super( numRows , numColumns );
	}
	
	public AnnotatingJTable( Object[ ][ ] rowData , Object[ ] columnNames )
	{
		super( rowData , columnNames );
	}
	
	public AnnotatingJTable( TableModel dm , TableColumnModel cm , ListSelectionModel sm )
	{
		super( dm , cm , sm );
	}
	
	public AnnotatingJTable( TableModel dm , TableColumnModel cm )
	{
		super( dm , cm );
	}
	
	public AnnotatingJTable( TableModel dm )
	{
		super( dm );
	}
	
	public AnnotatingJTable( Vector rowData , Vector columnNames )
	{
		super( rowData , columnNames );
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
			return ( ( AnnotatingRowSorter ) getRowSorter( ) ).getAnnotation( row );
		}
		return null;
	}
}
