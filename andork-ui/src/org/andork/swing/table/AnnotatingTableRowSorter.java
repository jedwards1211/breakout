/*
 * Copyright (c) 2005, 2006, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */
package org.andork.swing.table;

import java.text.Collator;
import java.util.Arrays;
import java.util.Comparator;

import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableStringConverter;

import org.andork.swing.AnnotatingRowSorter;

/**
 * An implementation of <code>RowSorter</code> that provides sorting and filtering using a <code>TableModel</code>. The following example shows adding sorting
 * to a <code>JTable</code>:
 * 
 * <pre>
 * TableModel myModel = createMyTableModel( );
 * JTable table = new JTable( myModel );
 * table.setRowSorter( new AnnotatingTableRowSorter( myModel ) );
 * </pre>
 * 
 * This will do all the wiring such that when the user does the appropriate gesture, such as clicking on the column header, the table will visually sort.
 * <p>
 * <code>JTable</code>'s row-based methods and <code>JTable</code>'s selection model refer to the view and not the underlying model. Therefore, it is necessary
 * to convert between the two. For example, to get the selection in terms of <code>myModel</code> you need to convert the indices:
 * 
 * <pre>
 * int[ ] selection = table.getSelectedRows( );
 * for( int i = 0 ; i &lt; selection.length ; i++ )
 * {
 * 	selection[ i ] = table.convertRowIndexToModel( selection[ i ] );
 * }
 * </pre>
 * 
 * Similarly to select a row in <code>JTable</code> based on a coordinate from the underlying model do the inverse:
 * 
 * <pre>
 * table.setRowSelectionInterval( table.convertRowIndexToView( row ) ,
 * 		table.convertRowIndexToView( row ) );
 * </pre>
 * <p>
 * The previous example assumes you have not enabled filtering. If you have enabled filtering <code>convertRowIndexToView</code> will return -1 for locations
 * that are not visible in the view.
 * <p>
 * <code>AnnotatingTableRowSorter</code> uses <code>Comparator</code>s for doing comparisons. The following defines how a <code>Comparator</code> is chosen for
 * a column:
 * <ol>
 * <li>If a <code>Comparator</code> has been specified for the column by the <code>setComparator</code> method, use it.
 * <li>If the column class as returned by <code>getColumnClass</code> is <code>String</code>, use the <code>Comparator</code> returned by
 * <code>Collator.getInstance()</code>.
 * <li>If the column class implements <code>Comparable</code>, use a <code>Comparator</code> that invokes the <code>compareTo</code> method.
 * <li>If a <code>TableStringConverter</code> has been specified, use it to convert the values to <code>String</code>s and then use the <code>Comparator</code>
 * returned by <code>Collator.getInstance()</code>.
 * <li>Otherwise use the <code>Comparator</code> returned by <code>Collator.getInstance()</code> on the results from calling <code>toString</code> on the
 * objects.
 * </ol>
 * <p>
 * In addition to sorting <code>AnnotatingTableRowSorter</code> provides the ability to filter. A filter is specified using the <code>setFilter</code> method.
 * The following example will only show rows containing the string "foo":
 * 
 * <pre>
 * TableModel myModel = createMyTableModel( );
 * AnnotatingTableRowSorter sorter = new AnnotatingTableRowSorter( myModel );
 * sorter.setRowFilter( RowFilter.regexFilter( &quot;.*foo.*&quot; ) );
 * JTable table = new JTable( myModel );
 * table.setRowSorter( sorter );
 * </pre>
 * <p>
 * If the underlying model structure changes (the <code>modelStructureChanged</code> method is invoked) the following are reset to their default values:
 * <code>Comparator</code>s by column, current sort order, and whether each column is sortable. The default sort order is natural (the same as the model), and
 * columns are sortable by default.
 * <p>
 * <code>AnnotatingTableRowSorter</code> has one formal type parameter: the type of the model. Passing in a type that corresponds exactly to your model allows
 * you to filter based on your model without casting. Refer to the documentation of <code>RowFilter</code> for an example of this.
 * <p>
 * <b>WARNING:</b> <code>DefaultTableModel</code> returns a column class of <code>Object</code>. As such all comparisons will be done using
 * <code>toString</code>. This may be unnecessarily expensive. If the column only contains one type of value, such as an <code>Integer</code>, you should
 * override <code>getColumnClass</code> and return the appropriate <code>Class</code>. This will dramatically increase the performance of this class.
 * 
 * @param <M>
 *            the type of the model, which must be an implementation of <code>TableModel</code>
 * @see javax.swing.JTable
 * @see javax.swing.RowFilter
 * @see javax.swing.table.DefaultTableModel
 * @see java.text.Collator
 * @see java.util.Comparator
 * @since 1.6
 */
public class AnnotatingTableRowSorter<M extends TableModel, A> extends AnnotatingRowSorter<M, Integer, A>
{
	/**
	 * Comparator that uses compareTo on the contents.
	 */
	private static final Comparator	COMPARABLE_COMPARATOR	=
																	new ComparableComparator( );
	/**
	 * For toString conversions.
	 */
	private TableStringConverter	stringConverter;
	
	/**
	 * Creates a <code>AnnotatingTableRowSorter</code> with an empty model.
	 */
	public AnnotatingTableRowSorter( JTable table , SortRunner sortRunner )
	{
		this( table , ( M ) table.getModel( ) , sortRunner );
	}
	
	/**
	 * Creates a <code>AnnotatingTableRowSorter</code> using <code>model</code> as the underlying <code>TableModel</code>.
	 * 
	 * @param model
	 *            the underlying <code>TableModel</code> to use, <code>null</code> is treated as an empty model
	 */
	public AnnotatingTableRowSorter( JTable table , M model , SortRunner sortRunner )
	{
		super( table , sortRunner );
		setModel( model );
	}
	
	/**
	 * Sets the object responsible for converting values from the model to strings. If non-<code>null</code> this is used to convert any object values, that do
	 * not have a registered <code>Comparator</code>, to strings.
	 * 
	 * @param stringConverter
	 *            the object responsible for converting values from the model to strings
	 */
	public void setStringConverter( TableStringConverter stringConverter )
	{
		this.stringConverter = stringConverter;
	}
	
	/**
	 * Returns the object responsible for converting values from the model to strings.
	 * 
	 * @return object responsible for converting values to strings.
	 */
	public TableStringConverter getStringConverter( )
	{
		return stringConverter;
	}
	
	/**
	 * Returns the <code>Comparator</code> for the specified column. If a <code>Comparator</code> has not been specified using the <code>setComparator</code>
	 * method a <code>Comparator</code> will be returned based on the column class (<code>TableModel.getColumnClass</code>) of the specified column. If the
	 * column class is <code>String</code>, <code>Collator.getInstance</code> is returned. If the column class implements <code>Comparable</code> a private
	 * <code>Comparator</code> is returned that invokes the <code>compareTo</code> method. Otherwise <code>Collator.getInstance</code> is returned.
	 * 
	 * @throws IndexOutOfBoundsException
	 *             {@inheritDoc}
	 */
	public Comparator<?> getComparator( int column )
	{
		Comparator comparator = super.getComparator( column );
		if( comparator != null )
		{
			return comparator;
		}
		Class columnClass = getModel( ).getColumnClass( column );
		if( columnClass == String.class )
		{
			return Collator.getInstance( );
		}
		if( Comparable.class.isAssignableFrom( columnClass ) )
		{
			return COMPARABLE_COMPARATOR;
		}
		return Collator.getInstance( );
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @throws IndexOutOfBoundsException
	 *             {@inheritDoc}
	 */
	protected boolean useToString( int column )
	{
		Comparator comparator = super.getComparator( column );
		if( comparator != null )
		{
			return false;
		}
		Class columnClass = getModel( ).getColumnClass( column );
		if( columnClass == String.class )
		{
			return false;
		}
		if( Comparable.class.isAssignableFrom( columnClass ) )
		{
			return false;
		}
		return true;
	}
	
	@Override
	protected org.andork.swing.AnnotatingRowSorter.ModelWrapper<M, Integer> createModelWrapper( M model )
	{
		return new TableRowSorterModelWrapper( model );
	}
	
	/**
	 * Implementation of AnnotatingRowSorter.ModelWrapper that delegates to a TableModel.
	 */
	private class TableRowSorterModelWrapper<M extends TableModel> extends ModelWrapper<M, Integer>
	{
		M	tableModel;
		
		public TableRowSorterModelWrapper( M tableModel )
		{
			super( );
			this.tableModel = tableModel;
		}
		
		public M getModel( )
		{
			return tableModel;
		}
		
		public int getColumnCount( )
		{
			return ( tableModel == null ) ? 0 : tableModel.getColumnCount( );
		}
		
		public int getRowCount( )
		{
			return ( tableModel == null ) ? 0 : tableModel.getRowCount( );
		}
		
		public Object getValueAt( int row , int column )
		{
			return tableModel.getValueAt( row , column );
		}
		
		public String getStringValueAt( int row , int column )
		{
			TableStringConverter converter = getStringConverter( );
			if( converter != null )
			{
				// Use the converter
				String value = converter.toString(
						tableModel , row , column );
				if( value != null )
				{
					return value;
				}
				return "";
			}
			
			// No converter, use getValueAt followed by toString
			Object o = getValueAt( row , column );
			if( o == null )
			{
				return "";
			}
			String string = o.toString( );
			if( string == null )
			{
				return "";
			}
			return string;
		}
		
		public Integer getIdentifier( int index )
		{
			return index;
		}
	}
	
	public static abstract class AbstractTableModelCopier<M extends AbstractTableModel> extends ModelCopier<M>
	{
		protected static Class<?>[ ] getColumnClasses( TableModel model )
		{
			Class<?>[ ] result = new Class[ model.getColumnCount( ) ];
			for( int i = 0 ; i < model.getColumnCount( ) ; i++ )
			{
				result[ i ] = model.getColumnClass( i );
			}
			return result;
		}
		
		protected static Object[ ] getColumnIdentifiers( TableModel model )
		{
			Object[ ] result = new Object[ model.getColumnCount( ) ];
			for( int i = 0 ; i < model.getColumnCount( ) ; i++ )
			{
				result[ i ] = model.getColumnName( i );
			}
			return result;
		}
		
		@Override
		public void copyRow( M src , int row , M dest )
		{
			for( int column = 0 ; column < Math.min( src.getColumnCount( ) , dest.getColumnCount( ) ) ; column++ )
			{
				dest.setValueAt( src.getValueAt( row , column ) , row , column );
			}
		}
	}
	
	public static class DefaultTableModelCopier extends AbstractTableModelCopier<DefaultTableModel>
	{
		@Override
		public DefaultTableModel createEmptyCopy( DefaultTableModel model )
		{
			return new DefaultTableModelCopy( getColumnClasses( model ) , getColumnIdentifiers( model ) , model.getRowCount( ) );
		}
	}
	
	@SuppressWarnings( "serial" )
	private static class DefaultTableModelCopy extends DefaultTableModel
	{
		Class<?>[ ]	columnClasses;
		
		public DefaultTableModelCopy( Class<?>[ ] columnClasses , Object[ ] columnIdentifiers , int rowCount )
		{
			super( columnIdentifiers , rowCount );
			this.columnClasses = Arrays.copyOf( columnClasses , columnClasses.length );
		}
		
		public Class<?> getColumnClass( int columnIndex )
		{
			return columnClasses[ columnIndex ];
		}
	}
	
	private static class ComparableComparator implements Comparator
	{
		@SuppressWarnings( "unchecked" )
		public int compare( Object o1 , Object o2 )
		{
			return ( ( Comparable ) o1 ).compareTo( o2 );
		}
	}
}