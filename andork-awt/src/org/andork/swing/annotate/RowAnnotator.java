/*
 * %W% %E%
 *
 * Copyright (c) 2006, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package org.andork.swing.annotate;

import java.util.Arrays;

import javax.swing.RowFilter;
import javax.swing.RowFilter.Entry;

/**
 * <code>RowAnnotator</code> is used to annotate entries from the model with some object. The view may customize the rendering of an entry based upon its
 * annotation. For example, a <code>RowAnnotator</code> associated with a <code>JTable</code> might annotate rows with invalid data entries with an error
 * messages. The meaning of <em>entry</em> depends on the component type. For example, when a filter is associated with a <code>JTable</code>, an entry
 * corresponds to a row; when associated with a <code>JTree</code>, an entry corresponds to a node.
 * <p>
 * Subclasses must override the <code>annotate</code> method to annotate a given entry. The <code>Entry</code> argument can be used to obtain the values in each
 * of the columns in that entry. The following example shows an <code>annotate</code> method that annotates entries with a count of how many values start with
 * the string "a":
 * 
 * <pre>
 * RowAnnotator&lt;Object, Object, Integer&gt;	startsWithAAnnotator	= new RowAnnotator&lt;Object, Object, Integer&gt;( )
 * 																{
 * 																	public Integer annotate( Entry&lt;? extends Object, ? extends Object&gt; entry )
 * 																	{
 * 																		int count = 0;
 * 																		
 * 																		for( int i = entry.getValueCount( ) - 1 ; i &gt;= 0 ; i-- )
 * 																		{
 * 																			if( entry.getStringValue( i ).startsWith( &quot;a&quot; ) )
 * 																			{
 * 																				count++ ;
 * 																			}
 * 																		}
 * 																		return count;
 * 																	}
 * 																};
 * </pre>
 * 
 * @param <M>
 *            the type of the model; for example <code>PersonModel</code>
 * @param <I>
 *            the type of the identifier; when using <code>AnnotatingTableRowSorter</code> this will be <code>Integer</code>
 * @param <A>
 *            the type of the annotation
 * @see AnnotatingRowSorter
 */
public abstract class RowAnnotator<M, I, A>
{
	/**
	 * Returns a non-null {@code A} if the specified entry should be annotated; returns {@code null} if the entry should not be annotated.
	 * <p>
	 * The <code>entry</code> argument is valid only for the duration of the invocation. Using <code>entry</code> after the call returns results in undefined
	 * behavior.
	 * 
	 * @param entry
	 *            a non-<code>null</code> object that wraps the underlying object from the model
	 * @return a non-null {@code A} if the entry should be annotated
	 */
	public abstract A annotate( Entry<? extends M, ? extends I> entry );
	
	public static <M, I> RowAnnotator<M, I, RowFilter<M, I>> filterAnnotator( RowFilter<M, I> ... filters )
	{
		return new FilterAnnotator<M, I>( filters );
	}
	
	private static class FilterAnnotator<M, I> extends RowAnnotator<M, I, RowFilter<M, I>>
	{
		private final RowFilter<M, I>[ ]	filters;
		
		@SuppressWarnings( "unchecked" )
		public FilterAnnotator( RowFilter<M, I> ... filters )
		{
			super( );
			this.filters = filters;
		}
		
		@Override
		public RowFilter<M, I>  annotate( Entry<? extends M, ? extends I> entry )
		{
			int resultCount = 0;
			
			for( RowFilter<M, I> filter : filters )
			{
				if( filter.include( entry ) )
				{
					return filter;
				}
			}
			
			return null;
		}
	}
}
