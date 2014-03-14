package org.andork.swing.table;

import java.awt.Color;
import java.awt.Component;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.swing.ListSelectionModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import org.andork.swing.AnnotatingRowSorter;
import org.andork.swing.RowAnnotator;

/**
 * A {@link BetterJTable} with the following added features:
 * <ul>
 * <li>It will invoke {@link AnnotatingTableCellRenderer}s with annotations from an {@link AnnotatingRowSorter} if one is installed.
 * </ul>
 * 
 * @author Andy
 */
@SuppressWarnings( "serial" )
public class AnnotatingJTable<M extends TableModel, A> extends BetterJTable
{
	protected final Map<Object, Color>	annotationColors	= new HashMap<Object, Color>( );
	
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
		Component comp = null;
		
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
		
		if( renderer instanceof AnnotatingTableCellRenderer )
		{
			comp = ( ( AnnotatingTableCellRenderer ) renderer ).getTableCellRendererComponent(
					this , value , getAnnotation( row ) , isSelected , hasFocus , row , column );
		}
		// this isn't nice but it's the only way I can figure out to reset the component background
		// and still have the renderer update it afterward
		comp = super.prepareRenderer( renderer , row , column );
		comp.setBackground( getBackground( ) );
		comp = super.prepareRenderer( renderer , row , column );
		
		if( !annotationColors.isEmpty( ) && !isSelected )
		{
			Object annotation = getAnnotation( row );
			if( annotation != null )
			{
				Color color = annotationColors.get( annotation );
				if( color != null )
				{
					comp.setBackground( color );
				}
			}
		}
		
		return comp;
	}
	
	public void setAnnotationColors( Map<?, Color> annotationColors )
	{
		this.annotationColors.clear( );
		this.annotationColors.putAll( annotationColors );
	}
	
	public Object getAnnotation( int row )
	{
		if( getRowSorter( ) instanceof AnnotatingRowSorter )
		{
			return ( ( AnnotatingRowSorter<?, ?, ?> ) getRowSorter( ) ).getAnnotation( row );
		}
		return null;
	}
	
	public AnnotatingRowSorter<M, Integer, A> getAnnotatingRowSorter( )
	{
		if( getRowSorter( ) instanceof AnnotatingRowSorter )
		{
			return ( AnnotatingRowSorter<M, Integer, A> ) getRowSorter( );
		}
		return null;
	}
}
