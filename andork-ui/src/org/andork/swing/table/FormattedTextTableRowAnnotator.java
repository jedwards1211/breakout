/*******************************************************************************
 * Breakout Cave Survey Visualizer
 *
 * Copyright (C) 2014 James Edwards
 *
 * jedwards8 at fastmail dot fm
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *******************************************************************************/
package org.andork.swing.table;

import java.util.function.BinaryOperator;

import javax.swing.RowFilter.Entry;
import javax.swing.table.TableModel;

import org.andork.swing.RowAnnotator;
import org.andork.util.FormattedText;

public class FormattedTextTableRowAnnotator extends RowAnnotator<TableModel, Integer>
{
	RowAnnotator<TableModel, Integer>	wrapped;
	
	private BinaryOperator<Exception>	combiner					= ( e1 , e2 ) -> e1;
	
	public static final Object			FORMAT_EXCEPTION_ANNOTATION	= new Object( );
	
	public FormattedTextTableRowAnnotator( )
	{
		this( null );
	}
	
	public FormattedTextTableRowAnnotator( RowAnnotator<TableModel, Integer> wrapped )
	{
		super( );
		this.wrapped = wrapped;
	}
	
	public void setWrapped( RowAnnotator<TableModel, Integer> wrapped )
	{
		this.wrapped = wrapped;
	}
	
	public BinaryOperator<Exception> getCombiner( )
	{
		return combiner;
	}
	
	public void setCombiner( BinaryOperator<Exception> combiner )
	{
		this.combiner = combiner;
	}
	
	/**
	 * @return if the wrapped annotator is not null and returns an annotation, returns that annotation. Otherwise, returns {@link #FORMAT_EXCEPTION_ANNOTATION}
	 *         if any {@link FormattedText}s in the row have format exceptions,
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
		
		Exception result = null;
		
		for( int column = 0 ; column < model.getColumnCount( ) ; column++ )
		{
			Object value = model.getValueAt( entry.getIdentifier( ) , column );
			if( value instanceof FormattedText )
			{
				Exception ex = ( ( FormattedText ) value ).getFormatException( );
				if( ex != null )
				{
					if( result == null )
					{
						result = ex;
					}
					else
					{
						result = combiner.apply( result , ex );
					}
				}
			}
		}
		
		return result;
	}
}
