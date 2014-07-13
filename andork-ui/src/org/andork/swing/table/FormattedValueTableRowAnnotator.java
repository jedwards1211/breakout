package org.andork.swing.table;

import javax.swing.RowFilter.Entry;
import javax.swing.table.TableModel;

import org.andork.swing.RowAnnotator;
import org.andork.util.FormattedValue;

public class FormattedValueTableRowAnnotator extends RowAnnotator<TableModel, Integer>
{
	RowAnnotator<TableModel, Integer>	wrapped;
	
	public static final Object	FORMAT_EXCEPTION_ANNOTATION	= new Object( );
	
	public FormattedValueTableRowAnnotator( )
	{
		this( null );
	}
	
	public FormattedValueTableRowAnnotator( RowAnnotator<TableModel, Integer> wrapped )
	{
		super( );
		this.wrapped = wrapped;
	}
	
	public void setWrapped( RowAnnotator<TableModel, Integer> wrapped )
	{
		this.wrapped = wrapped;
	}
	
	/**
	 * @return if the wrapped annotator is not null and returns an annotation, returns that annotation. Otherwise, returns {@link #FORMAT_EXCEPTION_ANNOTATION}
	 *         if any {@link FormattedValue}s in the row have format exceptions,
	 *         or null if none do.
	 */
	@Override
	public Object annotate( Entry<? extends TableModel, ? extends Integer> entry )
	{
		Object wrappedAnnot = wrapped == null ? null : wrapped.annotate( entry );
		if( wrappedAnnot != null )
		{
			return wrappedAnnot;
		}
		
		TableModel model = entry.getModel( );
		
		for( int column = 0 ; column < model.getColumnCount( ) ; column++ )
		{
			Object value = model.getValueAt( entry.getIdentifier( ) , column );
			if( value instanceof FormattedValue )
			{
				Exception ex = ( ( FormattedValue ) value ).getFormatException( );
				if( ex != null )
				{
					return FORMAT_EXCEPTION_ANNOTATION;
				}
			}
		}
		
		return null;
	}
}
