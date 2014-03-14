/*
 * %W% %E%
 *
 * Copyright (c) 2006, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package org.andork.swing;

import static org.andork.swing.DoSwing.doSwing;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ExecutorService;

import javax.swing.DefaultRowSorter;
import javax.swing.JTable;
import javax.swing.RowFilter;
import javax.swing.RowSorter;
import javax.swing.SortOrder;

import org.andork.awt.CheckEDT;
import org.andork.swing.table.AnnotatingTableRowSorter;

/**
 * A {@link RowSorter} adapted from {@link DefaultRowSorter} with the following features added:
 * <ul>
 * <li>Sorting is done on a background thread; as such it hangs the UI much less than {@link DefaultRowSorter} on large tables
 * <li>If any changes occur while sorting, the background sort will restart efficiently
 * <li>Rows can be annotated by a {@link RowAnnotator} (this can be used, for example, to highlight rows matching a filter)
 * </ul>
 * 
 * @param <M>
 *            the type of the model
 * @param <I>
 *            the type of the identifier passed to the <code>RowFilter</code>
 * @param <A>
 *            the row annotation type
 * @version %I% %G%
 * @see #getAnnotation(int)
 * @see #sortLater()
 * @see #sortAndWait()
 * @see #isSortingInBackground()
 * @see AnnotatingTableRowSorter
 * @see javax.swing.table.DefaultTableModel
 * @see java.text.Collator
 * @since 1.6
 */
public abstract class AnnotatingRowSorter<M, I, A> extends RowSorter<M>
{
	/**
	 * Whether or not we resort on TableModelEvent.UPDATEs.
	 */
	private boolean											sortsOnUpdates;
	
	private M												model;
	
	/**
	 * View (JTable) -> model.
	 */
	private Row<A>[ ]										viewToModel;
	
	/**
	 * model -> view (JTable)
	 */
	private int[ ]											modelToView;
	
	/**
	 * Comparators specified by column.
	 */
	private Comparator[ ]									comparators;
	
	/**
	 * Whether or not the specified column is sortable, by column.
	 */
	private boolean[ ]										isSortable;
	
	/**
	 * Cached SortKeys for the current sort.
	 */
	private SortKey[ ]										cachedSortKeys;
	
	/**
	 * Cached comparators for the current sort
	 */
	private Comparator[ ]									sortComparators;
	
	/**
	 * Developer supplied Annotator.
	 */
	private RowAnnotator<? super M, ? super I, ? extends A>	annotator;
	
	/**
	 * Developer supplied Filter.
	 */
	private RowFilter<? super M, ? super I>					filter;
	
	/**
	 * Value passed to the filter. The same instance is passed to the filter for different rows.
	 */
	private FilterEntry<M, I>								filterEntry;
	
	/**
	 * The sort keys.
	 */
	private List<SortKey>									sortKeys;
	
	/**
	 * Whether or not to use getStringValueAt. This is indexed by column.
	 */
	private boolean[ ]										useToString;
	
	/**
	 * Indicates the contents are sorted. This is used if getSortsOnUpdates is false and an update event is received.
	 */
	private boolean											sorted;
	
	/**
	 * Maximum number of sort keys.
	 */
	private int												maxSortKeys;
	
	/**
	 * Provides access to the data we're sorting/filtering.
	 */
	private ModelWrapper<M, I>								modelWrapper;
	
	/**
	 * Copies the model for the background sorter.
	 */
	private ModelCopier<M>									modelCopier;
	
	/**
	 * Size of the model. This is used to enforce error checking within the table changed notification methods (such as rowsInserted).
	 */
	private int												modelRowCount;
	
	private boolean											isSorting;
	
	private boolean											sortRequested;
	
	private boolean											sortExistingDataRequested;
	
	private SortRunner										sortRunner;
	
	private BackgroundSortTask<M, I, A>						sortTask;
	
	private final Queue<Runnable>							invokeWhenDoneSortingQueue	= new LinkedList<Runnable>( );
	
	/**
	 * Creates an empty <code>DefaultRowSorter</code>.
	 */
	public AnnotatingRowSorter( JTable table , SortRunner sortRunner )
	{
		sortKeys = Collections.emptyList( );
		maxSortKeys = 3;
		this.sortRunner = sortRunner;
		
		// selectionMaintainer.setTable( table );
	}
	
	/**
	 * Sets the model wrapper providing the data that is being sorted and filtered.
	 * 
	 * @param modelWrapper
	 *            the model wrapper responsible for providing the data that gets sorted and filtered
	 * @throws IllegalArgumentException
	 *             if {@code modelWrapper} is {@code null}
	 */
	protected final void setModelWrapper( ModelWrapper<M, I> modelWrapper )
	{
		if( modelWrapper == null )
		{
			throw new IllegalArgumentException(
					"modelWrapper most be non-null" );
		}
		ModelWrapper<M, I> last = this.modelWrapper;
		this.modelWrapper = modelWrapper;
		if( filterEntry != null )
		{
			filterEntry.modelWrapper = modelWrapper;
		}
		if( last != null )
		{
			modelStructureChanged( );
		}
		else
		{
			// If last is null, we're in the constructor. If we're in
			// the constructor we don't want to call to overridable methods.
			modelRowCount = getModelWrapper( ).getRowCount( );
		}
	}
	
	/**
	 * Returns the model wrapper providing the data that is being sorted and filtered.
	 * 
	 * @return the model wrapper responsible for providing the data that gets sorted and filtered
	 */
	protected final ModelWrapper<M, I> getModelWrapper( )
	{
		return modelWrapper;
	}
	
	public final void setModelCopier( ModelCopier<M> modelCopier )
	{
		if( modelCopier == null )
		{
			throw new IllegalArgumentException( "modelCopier must be non-null" );
		}
		this.modelCopier = modelCopier;
	}
	
	public final ModelCopier<M> getModelCopier( )
	{
		return this.modelCopier;
	}
	
	protected abstract ModelWrapper<M, I> createModelWrapper( M model );
	
	/**
	 * Returns the underlying model.
	 * 
	 * @return the underlying model
	 */
	public final M getModel( )
	{
		return getModelWrapper( ).getModel( );
	}
	
	/**
	 * Sets the <code>TableModel</code> to use as the underlying model for this <code>AnnotatingTableRowSorter</code>. A value of <code>null</code> can be used
	 * to set an empty model.
	 * 
	 * @param model
	 *            the underlying model to use, or <code>null</code>
	 */
	public void setModel( M model )
	{
		this.model = model;
		setModelWrapper( createModelWrapper( model ) );
	}
	
	/**
	 * Sets whether or not the specified column is sortable. The specified value is only checked when <code>toggleSortOrder</code> is invoked. It is still
	 * possible to sort on a column that has been marked as unsortable by directly setting the sort keys. The default is true.
	 * 
	 * @param column
	 *            the column to enable or disable sorting on, in terms of the underlying model
	 * @param sortable
	 *            whether or not the specified column is sortable
	 * @throws IndexOutOfBoundsException
	 *             if <code>column</code> is outside the range of the model
	 * @see #toggleSortOrder
	 * @see #setSortKeys
	 */
	public void setSortable( int column , boolean sortable )
	{
		checkColumn( column );
		if( isSortable == null )
		{
			isSortable = new boolean[ getModelWrapper( ).getColumnCount( ) ];
			for( int i = isSortable.length - 1 ; i >= 0 ; i-- )
			{
				isSortable[ i ] = true;
			}
		}
		isSortable[ column ] = sortable;
	}
	
	/**
	 * Returns true if the specified column is sortable; otherwise, false.
	 * 
	 * @param column
	 *            the column to check sorting for, in terms of the underlying model
	 * @return true if the column is sortable
	 * @throws IndexOutOfBoundsException
	 *             if column is outside the range of the underlying model
	 */
	public boolean isSortable( int column )
	{
		checkColumn( column );
		return ( isSortable == null ) ? true : isSortable[ column ];
	}
	
	/**
	 * Sets the sort keys. This creates a copy of the supplied {@code List}; subsequent changes to the supplied {@code List} do not effect this
	 * {@code DefaultRowSorter}. If the sort keys have changed this triggers a sort.
	 * 
	 * @param sortKeys
	 *            the new <code>SortKeys</code>; <code>null</code> is a shorthand for specifying an empty list, indicating that the view should be unsorted
	 * @throws IllegalArgumentException
	 *             if any of the values in <code>sortKeys</code> are null or have a column index outside the range of the model
	 */
	public void setSortKeys( List<? extends SortKey> sortKeys )
	{
		List<SortKey> old = this.sortKeys;
		if( sortKeys != null && sortKeys.size( ) > 0 )
		{
			int max = getModelWrapper( ).getColumnCount( );
			for( SortKey key : sortKeys )
			{
				if( key == null || key.getColumn( ) < 0 ||
						key.getColumn( ) >= max )
				{
					throw new IllegalArgumentException( "Invalid SortKey" );
				}
			}
			this.sortKeys = Collections.unmodifiableList(
					new ArrayList<SortKey>( sortKeys ) );
		}
		else
		{
			this.sortKeys = Collections.emptyList( );
		}
		if( !this.sortKeys.equals( old ) )
		{
			fireSortOrderChanged( );
			if( viewToModel == null )
			{
				// Currently unsorted, use sort so that internal fields
				// are correctly set.
				sortLater( );
			}
			else
			{
				sortExistingDataLater( );
			}
		}
	}
	
	/**
	 * Returns the current sort keys. This returns an unmodifiable {@code non-null List}. If you need to change the sort keys, make a copy of the returned
	 * {@code List}, mutate the copy and invoke {@code setSortKeys} with the new list.
	 * 
	 * @return the current sort order
	 */
	public List<? extends SortKey> getSortKeys( )
	{
		return sortKeys;
	}
	
	/**
	 * Sets the maximum number of sort keys. The number of sort keys determines how equal values are resolved when sorting. For example, assume a table row
	 * sorter is created and <code>setMaxSortKeys(2)</code> is invoked on it. The user clicks the header for column 1, causing the table rows to be sorted based
	 * on the items in column 1. Next, the user clicks the header for column 2, causing the table to be sorted based on the items in column 2; if any items in
	 * column 2 are equal, then those particular rows are ordered based on the items in column 1. In this case, we say that the rows are primarily sorted on
	 * column 2, and secondarily on column 1. If the user then clicks the header for column 3, then the items are primarily sorted on column 3 and secondarily
	 * sorted on column 2. Because the maximum number of sort keys has been set to 2 with <code>setMaxSortKeys</code>, column 1 no longer has an effect on the
	 * order.
	 * <p>
	 * The maximum number of sort keys is enforced by <code>toggleSortOrder</code>. You can specify more sort keys by invoking <code>setSortKeys</code> directly
	 * and they will all be honored. However if <code>toggleSortOrder</code> is subsequently invoked the maximum number of sort keys will be enforced. The
	 * default value is 3.
	 * 
	 * @param max
	 *            the maximum number of sort keys
	 * @throws IllegalArgumentException
	 *             if <code>max</code> &lt; 1
	 */
	public void setMaxSortKeys( int max )
	{
		if( max < 1 )
		{
			throw new IllegalArgumentException( "Invalid max" );
		}
		maxSortKeys = max;
	}
	
	/**
	 * Returns the maximum number of sort keys.
	 * 
	 * @return the maximum number of sort keys
	 */
	public int getMaxSortKeys( )
	{
		return maxSortKeys;
	}
	
	/**
	 * If true, specifies that a sort should happen when the underlying model is updated (<code>rowsUpdated</code> is invoked). For example, if this is true and
	 * the user edits an entry the location of that item in the view may change. The default is false.
	 * 
	 * @param sortsOnUpdates
	 *            whether or not to sort on update events
	 */
	public void setSortsOnUpdates( boolean sortsOnUpdates )
	{
		this.sortsOnUpdates = sortsOnUpdates;
	}
	
	/**
	 * Returns true if a sort should happen when the underlying model is updated; otherwise, returns false.
	 * 
	 * @return whether or not to sort when the model is updated
	 */
	public boolean getSortsOnUpdates( )
	{
		return sortsOnUpdates;
	}
	
	/**
	 * Sets the annotator that annotates rows in the view. The annotator is applied before sorting. A value of <code>null</code> indicates that no rows should
	 * be annotated.
	 * <p>
	 * <code>RowAnnotator</code>'s <code>annotate</code> method is passed an <code>Entry</code> that wraps the underlying model. The number of columns in the
	 * <code>Entry</code> corresponds to the number of columns in the <code>ModelWrapper</code>. The identifier comes from the <code>ModelWrapper</code> as
	 * well.
	 * <p>
	 * This method triggers a sort.
	 * 
	 * @param annotator
	 *            the annotator used to annotate rows
	 */
	public void setRowAnnotator( RowAnnotator<? super M, ? super I, ? extends A> annotator )
	{
		this.annotator = annotator;
		sortExistingDataLater( );
	}
	
	/**
	 * Returns the annotator that annotates rows in the view.
	 * 
	 * @return the annotator
	 */
	public RowAnnotator<? super M, ? super I, ? extends A> getRowAnnotator( )
	{
		return annotator;
	}
	
	/**
	 * Sets the filter that determines which rows, if any, should be hidden from the view. The filter is applied before sorting. A value of <code>null</code>
	 * indicates all values from the model should be included.
	 * <p>
	 * <code>RowFilter</code>'s <code>include</code> method is passed an <code>Entry</code> that wraps the underlying model. The number of columns in the
	 * <code>Entry</code> corresponds to the number of columns in the <code>ModelWrapper</code>. The identifier comes from the <code>ModelWrapper</code> as
	 * well.
	 * <p>
	 * This method triggers a sort.
	 * 
	 * @param filter
	 *            the filter used to determine what entries should be included
	 */
	public void setRowFilter( RowFilter<? super M, ? super I> filter )
	{
		this.filter = filter;
		sortLater( );
	}
	
	/**
	 * Returns the filter that determines which rows, if any, should be hidden from view.
	 * 
	 * @return the filter
	 */
	public RowFilter<? super M, ? super I> getRowFilter( )
	{
		return filter;
	}
	
	/**
	 * If not currently sorting, runs {@code r}. Otherwise, schedules {@code r} to be run next time sorting finishes.
	 */
	public void invokeWhenDoneSorting( Runnable r )
	{
		CheckEDT.checkEDT( );
		
		if( isSortingInBackground( ) )
		{
			invokeWhenDoneSortingQueue.add( r );
		}
		else
		{
			r.run( );
		}
	}
	
	/**
	 * Reverses the sort order from ascending to descending (or descending to ascending) if the specified column is already the primary sorted column;
	 * otherwise, makes the specified column the primary sorted column, with an ascending sort order. If the specified column is not sortable, this method has
	 * no effect.
	 * 
	 * @param column
	 *            index of the column to make the primary sorted column, in terms of the underlying model
	 * @throws IndexOutOfBoundsException
	 *             {@inheritDoc}
	 * @see #setSortable(int,boolean)
	 * @see #setMaxSortKeys(int)
	 */
	public void toggleSortOrder( int column )
	{
		checkColumn( column );
		if( isSortable( column ) )
		{
			List<SortKey> keys = new ArrayList<SortKey>( getSortKeys( ) );
			SortKey sortKey;
			int sortIndex;
			for( sortIndex = keys.size( ) - 1 ; sortIndex >= 0 ; sortIndex-- )
			{
				if( keys.get( sortIndex ).getColumn( ) == column )
				{
					break;
				}
			}
			if( sortIndex == -1 )
			{
				// Key doesn't exist
				sortKey = new SortKey( column , SortOrder.ASCENDING );
				keys.add( 0 , sortKey );
			}
			else if( sortIndex == 0 )
			{
				SortOrder newOrder = toggle( keys.get( 0 ).getSortOrder( ) );
				if( newOrder == null )
				{
					keys.clear( );
				}
				else
				{
					keys.set( 0 , new SortKey( column , newOrder ) );
				}
			}
			else
			{
				// It's not the first, but was sorted on, remove old
				// entry, insert as first with ascending.
				keys.remove( sortIndex );
				keys.add( 0 , new SortKey( column , SortOrder.ASCENDING ) );
			}
			if( keys.size( ) > getMaxSortKeys( ) )
			{
				keys = keys.subList( 0 , getMaxSortKeys( ) );
			}
			setSortKeys( keys );
		}
	}
	
	private SortKey toggle( SortKey key )
	{
		if( key.getSortOrder( ) == SortOrder.ASCENDING )
		{
			return new SortKey( key.getColumn( ) , SortOrder.DESCENDING );
		}
		return new SortKey( key.getColumn( ) , SortOrder.ASCENDING );
	}
	
	private SortOrder toggle( SortOrder order )
	{
		if( order == null )
		{
			return SortOrder.ASCENDING;
		}
		if( order == SortOrder.ASCENDING )
		{
			return SortOrder.DESCENDING;
		}
		return null;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @throws IndexOutOfBoundsException
	 *             {@inheritDoc}
	 */
	public int convertRowIndexToView( int index )
	{
		if( modelToView == null )
		{
			if( index < 0 || index >= getModelWrapper( ).getRowCount( ) )
			{
				throw new IndexOutOfBoundsException( "Invalid index" );
			}
			return index;
		}
		return modelToView[ index ];
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @throws IndexOutOfBoundsException
	 *             {@inheritDoc}
	 */
	public int convertRowIndexToModel( int index )
	{
		if( viewToModel == null )
		{
			if( index < 0 || index >= getModelWrapper( ).getRowCount( ) )
			{
				throw new IndexOutOfBoundsException( "Invalid index" );
			}
			return index;
		}
		return viewToModel[ index ].modelIndex;
	}
	
	/**
	 * Gets the annotation of the row at the given index in the view.
	 * 
	 * @param viewIndex
	 *            the view index of a row.
	 * @return the row's annotation, or {@code null} if it has none
	 */
	public A getAnnotation( int viewIndex )
	{
		if( viewToModel == null )
		{
			return null;
		}
		return viewToModel[ viewIndex ].annotation;
	}
	
	private boolean isUnsorted( )
	{
		// This is changed to always be false so that annotations will be stored even when the rows are unsorted
		return false;
		
		// List<? extends SortKey> keys = getSortKeys( );
		// int keySize = keys.size( );
		// return( keySize == 0 || keys.get( 0 ).getSortOrder( ) == SortOrder.UNSORTED );
	}
	
	private void sortExistingDataLater( )
	{
		CheckEDT.checkEDT( );
		
		sortExistingDataRequested = true;
		if( sortTask == null )
		{
			sortTask = new BackgroundSortTask<M, I, A>( this );
			sortRunner.submit( sortTask );
		}
		// else
		// {
		// sortTask.viewToModel = viewToModel;
		// sortTask.modelToView = modelToView;
		// }
		
		int[ ] lastRowIndexToModel = getViewToModelAsInts( viewToModel );
		
		sorted = false;
		viewToModel = null;
		modelToView = null;
		fireRowSorterChanged( lastRowIndexToModel );
	}
	
	private void sortExistingDataAndWait( )
	{
		CheckEDT.checkEDT( );
		
		sortExistingDataRequested = true;
		new BackgroundSortTask<M, I, A>( this ).run( );
		
		// Careful! If the model changes and this method is run
		// while a background sort is running, it could cause the
		// background sort to install an obsolete ordering when it
		// finishes! So just force it to start over so it will install
		// the most up-to-date ordering.
		if( isSortingInBackground( ) )
		{
			sortRequested = true;
		}
	}
	
	public void sortLater( )
	{
		CheckEDT.checkEDT( );
		
		sortRequested = true;
		if( sortTask == null )
		{
			sortTask = new BackgroundSortTask<M, I, A>( this );
			sortRunner.submit( sortTask );
		}
		// else
		// {
		// sortTask.viewToModel = viewToModel;
		// sortTask.modelToView = modelToView;
		// }
		
		int[ ] lastRowIndexToModel = getViewToModelAsInts( viewToModel );
		
		sorted = false;
		viewToModel = null;
		modelToView = null;
		fireRowSorterChanged( lastRowIndexToModel );
	}
	
	public void sortAndWait( )
	{
		CheckEDT.checkEDT( );
		
		sortRequested = true;
		new BackgroundSortTask<M, I, A>( this ).run( );
		
		// Careful! If the model changes and this method is run
		// while a background sort is running, it could cause the
		// background sort to install an obsolete ordering when it
		// finishes! So just force it to start over so it will install
		// the most up-to-date ordering.
		if( isSortingInBackground( ) )
		{
			sortRequested = true;
		}
	}
	
	public boolean isSortingInBackground( )
	{
		CheckEDT.checkEDT( );
		
		return sortTask != null;
	}
	
	/**
	 * Updates the useToString mapping before a sort.
	 */
	private void updateUseToString( )
	{
		int i = getModelWrapper( ).getColumnCount( );
		if( useToString == null || useToString.length != i )
		{
			useToString = new boolean[ i ];
		}
		for( --i ; i >= 0 ; i-- )
		{
			useToString[ i ] = useToString( i );
		}
	}
	
	/**
	 * Resets the viewToModel and modelToView mappings based on the current Filter.
	 */
	private void initializeFilteredMapping( )
	{
		int rowCount = getModelWrapper( ).getRowCount( );
		int i, j;
		int excludedCount = 0;
		
		// Update model -> view
		createModelToView( rowCount );
		for( i = 0 ; i < rowCount ; i++ )
		{
			if( include( i ) )
			{
				modelToView[ i ] = i - excludedCount;
			}
			else
			{
				modelToView[ i ] = -1;
				excludedCount++ ;
			}
		}
		
		// Update view -> model
		createViewToModel( rowCount - excludedCount );
		for( i = 0 , j = 0 ; i < rowCount ; i++ )
		{
			if( modelToView[ i ] != -1 )
			{
				viewToModel[ j ].modelIndex = i;
				viewToModel[ j++ ].annotation = annotate( i );
			}
		}
	}
	
	/**
	 * Makes sure the modelToView array is of size rowCount.
	 */
	private void createModelToView( int rowCount )
	{
		if( modelToView == null || modelToView.length != rowCount )
		{
			modelToView = new int[ rowCount ];
		}
	}
	
	/**
	 * Resets the viewToModel array to be of size rowCount.
	 */
	private void createViewToModel( int rowCount )
	{
		int recreateFrom = 0;
		if( viewToModel != null )
		{
			recreateFrom = Math.min( rowCount , viewToModel.length );
			if( viewToModel.length != rowCount )
			{
				Row[ ] oldViewToModel = viewToModel;
				viewToModel = new Row[ rowCount ];
				System.arraycopy( oldViewToModel , 0 , viewToModel ,
						0 , recreateFrom );
			}
		}
		else
		{
			viewToModel = new Row[ rowCount ];
		}
		int i;
		for( i = 0 ; i < recreateFrom ; i++ )
		{
			viewToModel[ i ].modelIndex = i;
			viewToModel[ i ].annotation = annotate( i );
		}
		for( i = recreateFrom ; i < rowCount ; i++ )
		{
			viewToModel[ i ] = new Row( this , i , annotate( i ) );
		}
	}
	
	/**
	 * Caches the sort keys before a sort.
	 */
	private void cacheSortKeys( List<? extends SortKey> keys )
	{
		int keySize = keys.size( );
		sortComparators = new Comparator[ keySize ];
		for( int i = 0 ; i < keySize ; i++ )
		{
			sortComparators[ i ] = getComparator0( keys.get( i ).getColumn( ) );
		}
		cachedSortKeys = keys.toArray( new SortKey[ keySize ] );
	}
	
	/**
	 * Returns whether or not to convert the value to a string before doing comparisons when sorting. If true <code>ModelWrapper.getStringValueAt</code> will be
	 * used, otherwise <code>ModelWrapper.getValueAt</code> will be used. It is up to subclasses, such as <code>AnnotatingTableRowSorter</code>, to honor this
	 * value in their <code>ModelWrapper</code> implementation.
	 * 
	 * @param column
	 *            the index of the column to test, in terms of the underlying model
	 * @throws IndexOutOfBoundsException
	 *             if <code>column</code> is not valid
	 */
	protected boolean useToString( int column )
	{
		return( getComparator( column ) == null );
	}
	
	/**
	 * Refreshes the modelToView mapping from that of viewToModel. If <code>unsetFirst</code> is true, all indices in modelToView are first set to -1.
	 */
	private void setModelToViewFromViewToModel( boolean unsetFirst )
	{
		int i;
		if( unsetFirst )
		{
			for( i = modelToView.length - 1 ; i >= 0 ; i-- )
			{
				modelToView[ i ] = -1;
			}
		}
		for( i = viewToModel.length - 1 ; i >= 0 ; i-- )
		{
			modelToView[ viewToModel[ i ].modelIndex ] = i;
		}
	}
	
	private int[ ] getViewToModelAsInts( Row[ ] viewToModel )
	{
		if( viewToModel != null )
		{
			int[ ] viewToModelI = new int[ viewToModel.length ];
			for( int i = viewToModel.length - 1 ; i >= 0 ; i-- )
			{
				viewToModelI[ i ] = viewToModel[ i ].modelIndex;
			}
			return viewToModelI;
		}
		return new int[ 0 ];
	}
	
	/**
	 * Sets the <code>Comparator</code> to use when sorting the specified column. This does not trigger a sort. If you want to sort after setting the comparator
	 * you need to explicitly invoke <code>sort</code>.
	 * 
	 * @param column
	 *            the index of the column the <code>Comparator</code> is to be used for, in terms of the underlying model
	 * @param comparator
	 *            the <code>Comparator</code> to use
	 * @throws IndexOutOfBoundsException
	 *             if <code>column</code> is outside the range of the underlying model
	 */
	public void setComparator( int column , Comparator<?> comparator )
	{
		checkColumn( column );
		if( comparators == null )
		{
			comparators = new Comparator[ getModelWrapper( ).getColumnCount( ) ];
		}
		comparators[ column ] = comparator;
	}
	
	/**
	 * Returns the <code>Comparator</code> for the specified column. This will return <code>null</code> if a <code>Comparator</code> has not been specified for
	 * the column.
	 * 
	 * @param column
	 *            the column to fetch the <code>Comparator</code> for, in terms of the underlying model
	 * @return the <code>Comparator</code> for the specified column
	 * @throws IndexOutOfBoundsException
	 *             if column is outside the range of the underlying model
	 */
	public Comparator<?> getComparator( int column )
	{
		checkColumn( column );
		if( comparators != null )
		{
			return comparators[ column ];
		}
		return null;
	}
	
	// Returns the Comparator to use during sorting. Where as
	// getComparator() may return null, this will never return null.
	private Comparator getComparator0( int column )
	{
		Comparator comparator = getComparator( column );
		if( comparator != null )
		{
			return comparator;
		}
		// This should be ok as useToString(column) should have returned
		// true in this case.
		return Collator.getInstance( );
	}
	
	private RowFilter.Entry<M, I> getFilterEntry( int modelIndex )
	{
		if( filterEntry == null )
		{
			filterEntry = new FilterEntry<M, I>( );
			filterEntry.modelWrapper = modelWrapper;
		}
		filterEntry.modelIndex = modelIndex;
		return filterEntry;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public int getViewRowCount( )
	{
		if( viewToModel != null )
		{
			// When filtering this may differ from getModelWrapper().getRowCount()
			return viewToModel.length;
		}
		return getModelWrapper( ).getRowCount( );
	}
	
	/**
	 * {@inheritDoc}
	 */
	public int getModelRowCount( )
	{
		return getModelWrapper( ).getRowCount( );
	}
	
	private void allChanged( )
	{
		modelToView = null;
		viewToModel = null;
		comparators = null;
		isSortable = null;
		if( isUnsorted( ) )
		{
			// Keys are already empty, to force a resort we have to
			// call sort
			sortLater( );
		}
		else
		{
			setSortKeys( null );
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void modelStructureChanged( )
	{
		allChanged( );
		modelRowCount = getModelWrapper( ).getRowCount( );
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void allRowsChanged( )
	{
		modelRowCount = getModelWrapper( ).getRowCount( );
		sortLater( );
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @throws IndexOutOfBoundsException
	 *             {@inheritDoc}
	 */
	public void rowsInserted( int firstRow , int endRow )
	{
		checkAgainstModel( firstRow , endRow );
		int newModelRowCount = getModelWrapper( ).getRowCount( );
		if( endRow >= newModelRowCount )
		{
			throw new IndexOutOfBoundsException( "Invalid range" );
		}
		modelRowCount = newModelRowCount;
		if( shouldOptimizeChange( firstRow , endRow ) )
		{
			rowsInserted0( firstRow , endRow );
		}
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @throws IndexOutOfBoundsException
	 *             {@inheritDoc}
	 */
	public void rowsDeleted( int firstRow , int endRow )
	{
		checkAgainstModel( firstRow , endRow );
		if( firstRow >= modelRowCount || endRow >= modelRowCount )
		{
			throw new IndexOutOfBoundsException( "Invalid range" );
		}
		modelRowCount = getModelWrapper( ).getRowCount( );
		if( shouldOptimizeChange( firstRow , endRow ) )
		{
			rowsDeleted0( firstRow , endRow );
		}
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @throws IndexOutOfBoundsException
	 *             {@inheritDoc}
	 */
	public void rowsUpdated( int firstRow , int endRow )
	{
		checkAgainstModel( firstRow , endRow );
		if( firstRow >= modelRowCount || endRow >= modelRowCount )
		{
			throw new IndexOutOfBoundsException( "Invalid range" );
		}
		if( getSortsOnUpdates( ) )
		{
			if( shouldOptimizeChange( firstRow , endRow ) )
			{
				rowsUpdated0( firstRow , endRow );
			}
		}
		else
		{
			sorted = false;
		}
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @throws IndexOutOfBoundsException
	 *             {@inheritDoc}
	 */
	public void rowsUpdated( int firstRow , int endRow , int column )
	{
		checkColumn( column );
		rowsUpdated( firstRow , endRow );
	}
	
	private void checkAgainstModel( int firstRow , int endRow )
	{
		if( firstRow > endRow || firstRow < 0 || endRow < 0 ||
				firstRow > modelRowCount )
		{
			throw new IndexOutOfBoundsException( "Invalid range" );
		}
	}
	
	/**
	 * Returns true if the specified row should be included.
	 */
	private boolean include( int row )
	{
		RowFilter<? super M, ? super I> filter = getRowFilter( );
		if( filter != null )
		{
			return filter.include( getFilterEntry( row ) );
		}
		// null filter, always include the row.
		return true;
	}
	
	/**
	 * Returns true if the specified row should be included.
	 */
	private A annotate( int row )
	{
		RowAnnotator<? super M, ? super I, ? extends A> annotator = getRowAnnotator( );
		if( annotator != null )
		{
			return annotator.annotate( getFilterEntry( row ) );
		}
		return null;
	}
	
	@SuppressWarnings( "unchecked" )
	private int compare( int model1 , int model2 )
	{
		int column;
		SortOrder sortOrder;
		Object v1, v2;
		int result;
		
		for( int counter = 0 ; counter < cachedSortKeys.length ; counter++ )
		{
			column = cachedSortKeys[ counter ].getColumn( );
			sortOrder = cachedSortKeys[ counter ].getSortOrder( );
			if( sortOrder == SortOrder.UNSORTED )
			{
				result = model1 - model2;
			}
			else
			{
				// v1 != null && v2 != null
				if( useToString[ column ] )
				{
					v1 = getModelWrapper( ).getStringValueAt( model1 , column );
					v2 = getModelWrapper( ).getStringValueAt( model2 , column );
				}
				else
				{
					v1 = getModelWrapper( ).getValueAt( model1 , column );
					v2 = getModelWrapper( ).getValueAt( model2 , column );
				}
				// Treat nulls as < then non-null
				if( v1 == null )
				{
					if( v2 == null )
					{
						result = 0;
					}
					else
					{
						result = -1;
					}
				}
				else if( v2 == null )
				{
					result = 1;
				}
				else
				{
					result = sortComparators[ counter ].compare( v1 , v2 );
				}
				if( sortOrder == SortOrder.DESCENDING )
				{
					result *= -1;
				}
			}
			if( result != 0 )
			{
				return result;
			}
		}
		// If we get here, they're equal. Fallback to model order.
		return model1 - model2;
	}
	
	/**
	 * Whether not we are filtering/sorting.
	 */
	private boolean isTransformed( )
	{
		return( viewToModel != null );
	}
	
	/**
	 * Insets new set of entries.
	 * 
	 * @param toAdd
	 *            the Rows to add, sorted
	 * @param current
	 *            the array to insert the items into
	 */
	private void insertInOrder( List<Row<A>> toAdd , Row<A>[ ] current )
	{
		int last = 0;
		int index;
		int max = toAdd.size( );
		for( int i = 0 ; i < max ; i++ )
		{
			index = Arrays.binarySearch( current , toAdd.get( i ) );
			if( index < 0 )
			{
				index = -1 - index;
			}
			System.arraycopy( current , last ,
					viewToModel , last + i , index - last );
			viewToModel[ index + i ] = toAdd.get( i );
			last = index;
		}
		System.arraycopy( current , last , viewToModel , last + max ,
				current.length - last );
	}
	
	/**
	 * Returns true if we should try and optimize the processing of the <code>TableModelEvent</code>. If this returns false, assume the event was dealt with and
	 * no further processing needs to happen.
	 */
	private boolean shouldOptimizeChange( int firstRow , int lastRow )
	{
		if( !isTransformed( ) )
		{
			// Not transformed, nothing to do.
			return false;
		}
		if( !sorted || ( lastRow - firstRow ) > viewToModel.length / 10 )
		{
			// We either weren't sorted, or to much changed, sort it all
			sortLater( );
			return false;
		}
		return true;
	}
	
	private void rowsInserted0( int firstRow , int lastRow )
	{
		int[ ] oldViewToModel = getViewToModelAsInts( viewToModel );
		int i;
		int delta = ( lastRow - firstRow ) + 1;
		List<Row<A>> added = new ArrayList<Row<A>>( delta );
		
		// Build the list of Rows to add into added
		for( i = firstRow ; i <= lastRow ; i++ )
		{
			if( include( i ) )
			{
				added.add( new Row<A>( this , i , annotate( i ) ) );
			}
		}
		
		// Adjust the model index of rows after the effected region
		int viewIndex;
		for( i = modelToView.length - 1 ; i >= firstRow ; i-- )
		{
			viewIndex = modelToView[ i ];
			if( viewIndex != -1 )
			{
				viewToModel[ viewIndex ].modelIndex += delta;
				viewToModel[ viewIndex ].annotation = annotate( viewToModel[ viewIndex ].modelIndex );
			}
		}
		
		// Insert newly added rows into viewToModel
		if( added.size( ) > 0 )
		{
			Collections.sort( added );
			Row<A>[ ] lastViewToModel = viewToModel;
			viewToModel = new Row[ viewToModel.length + added.size( ) ];
			insertInOrder( added , lastViewToModel );
		}
		
		// Update modelToView
		createModelToView( getModelWrapper( ).getRowCount( ) );
		setModelToViewFromViewToModel( true );
		
		// Notify of change
		fireRowSorterChanged( oldViewToModel );
	}
	
	private void rowsDeleted0( int firstRow , int lastRow )
	{
		int[ ] oldViewToModel = getViewToModelAsInts( viewToModel );
		int removedFromView = 0;
		int i;
		int viewIndex;
		
		// Figure out how many visible rows are going to be effected.
		for( i = firstRow ; i <= lastRow ; i++ )
		{
			viewIndex = modelToView[ i ];
			if( viewIndex != -1 )
			{
				removedFromView++ ;
				viewToModel[ viewIndex ] = null;
			}
		}
		
		// Update the model index of rows after the effected region
		int delta = lastRow - firstRow + 1;
		for( i = modelToView.length - 1 ; i > lastRow ; i-- )
		{
			viewIndex = modelToView[ i ];
			if( viewIndex != -1 )
			{
				viewToModel[ viewIndex ].modelIndex -= delta;
				viewToModel[ viewIndex ].annotation = annotate( viewToModel[ viewIndex ].modelIndex );
			}
		}
		
		// Then patch up the viewToModel array
		if( removedFromView > 0 )
		{
			Row[ ] newViewToModel = new Row[ viewToModel.length -
					removedFromView ];
			int newIndex = 0;
			int last = 0;
			for( i = 0 ; i < viewToModel.length ; i++ )
			{
				if( viewToModel[ i ] == null )
				{
					System.arraycopy( viewToModel , last ,
							newViewToModel , newIndex , i - last );
					newIndex += ( i - last );
					last = i + 1;
				}
			}
			System.arraycopy( viewToModel , last ,
					newViewToModel , newIndex , viewToModel.length - last );
			viewToModel = newViewToModel;
		}
		
		// Update the modelToView mapping
		createModelToView( getModelWrapper( ).getRowCount( ) );
		setModelToViewFromViewToModel( true );
		
		// And notify of change
		fireRowSorterChanged( oldViewToModel );
	}
	
	private void rowsUpdated0( int firstRow , int lastRow )
	{
		int[ ] oldViewToModel = getViewToModelAsInts( viewToModel );
		int i, j;
		int delta = lastRow - firstRow + 1;
		int modelIndex;
		int last;
		int index;
		
		if( getRowFilter( ) == null )
		{
			// Sorting only:
			
			// Remove the effected rows
			Row<A>[ ] updated = new Row[ delta ];
			for( j = 0 , i = firstRow ; i <= lastRow ; i++ , j++ )
			{
				updated[ j ] = viewToModel[ modelToView[ i ] ];
				updated[ j ].annotation = annotate( i );
			}
			
			// Sort the update rows
			Arrays.sort( updated );
			
			// Build the intermediary array: the array of
			// viewToModel without the effected rows.
			Row<A>[ ] intermediary = new Row[ viewToModel.length - delta ];
			for( i = 0 , j = 0 ; i < viewToModel.length ; i++ )
			{
				modelIndex = viewToModel[ i ].modelIndex;
				if( modelIndex < firstRow || modelIndex > lastRow )
				{
					intermediary[ j++ ] = viewToModel[ i ];
				}
			}
			
			// Build the new viewToModel
			insertInOrder( Arrays.asList( updated ) , intermediary );
			
			// Update modelToView
			setModelToViewFromViewToModel( false );
		}
		else
		{
			// Sorting & filtering.
			
			// Remove the effected rows, adding them to updated and setting
			// modelToView to -2 for any rows that were not filtered out
			List<Row<A>> updated = new ArrayList<Row<A>>( delta );
			int newlyVisible = 0;
			int newlyHidden = 0;
			int effected = 0;
			for( i = firstRow ; i <= lastRow ; i++ )
			{
				if( modelToView[ i ] == -1 )
				{
					// This row was filtered out
					if( include( i ) )
					{
						// No longer filtered
						updated.add( new Row<A>( this , i , annotate( i ) ) );
						newlyVisible++ ;
					}
				}
				else
				{
					// This row was visible, make sure it should still be
					// visible.
					if( !include( i ) )
					{
						newlyHidden++ ;
					}
					else
					{
						updated.add( viewToModel[ modelToView[ i ] ] );
					}
					modelToView[ i ] = -2;
					effected++ ;
				}
			}
			
			// Sort the updated rows
			Collections.sort( updated );
			
			// Build the intermediary array: the array of
			// viewToModel without the updated rows.
			Row<A>[ ] intermediary = new Row[ viewToModel.length - effected ];
			for( i = 0 , j = 0 ; i < viewToModel.length ; i++ )
			{
				modelIndex = viewToModel[ i ].modelIndex;
				if( modelToView[ modelIndex ] != -2 )
				{
					intermediary[ j++ ] = viewToModel[ i ];
				}
			}
			
			// Recreate viewToModel, if necessary
			if( newlyVisible != newlyHidden )
			{
				viewToModel = new Row[ viewToModel.length + newlyVisible -
						newlyHidden ];
			}
			
			// Rebuild the new viewToModel array
			insertInOrder( updated , intermediary );
			
			// Update modelToView
			setModelToViewFromViewToModel( true );
		}
		// And finally fire a sort event.
		fireRowSorterChanged( oldViewToModel );
	}
	
	private void checkColumn( int column )
	{
		if( column < 0 || column >= getModelWrapper( ).getColumnCount( ) )
		{
			throw new IndexOutOfBoundsException(
					"column beyond range of TableModel" );
		}
	}
	
	/**
	 * <code>DefaultRowSorter.ModelWrapper</code> is responsible for providing the data that gets sorted by <code>DefaultRowSorter</code>. You normally do not
	 * interact directly with <code>ModelWrapper</code>. Subclasses of <code>DefaultRowSorter</code> provide an implementation of <code>ModelWrapper</code>
	 * wrapping another model. For example, <code>AnnotatingTableRowSorter</code> provides a <code>ModelWrapper</code> that wraps a <code>TableModel</code>.
	 * <p>
	 * <code>ModelWrapper</code> makes a distinction between values as <code>Object</code>s and <code>String</code>s. This allows implementations to provide a
	 * custom string converter to be used instead of invoking <code>toString</code> on the object.
	 * 
	 * @param <M>
	 *            the type of the underlying model
	 * @param <I>
	 *            the identifier supplied to the filter
	 * @since 1.6
	 * @see RowFilter
	 * @see RowFilter.Entry
	 */
	protected abstract static class ModelWrapper<M, I>
	{
		/**
		 * Creates a new <code>ModelWrapper</code>.
		 */
		protected ModelWrapper( )
		{
		}
		
		/**
		 * Returns the underlying model that this <code>Model</code> is wrapping.
		 * 
		 * @return the underlying model
		 */
		public abstract M getModel( );
		
		/**
		 * Returns the number of columns in the model.
		 * 
		 * @return the number of columns in the model
		 */
		public abstract int getColumnCount( );
		
		/**
		 * Returns the number of rows in the model.
		 * 
		 * @return the number of rows in the model
		 */
		public abstract int getRowCount( );
		
		/**
		 * Returns the value at the specified index.
		 * 
		 * @param row
		 *            the row index
		 * @param column
		 *            the column index
		 * @return the value at the specified index
		 * @throws IndexOutOfBoundsException
		 *             if the indices are outside the range of the model
		 */
		public abstract Object getValueAt( int row , int column );
		
		/**
		 * Returns the value as a <code>String</code> at the specified index. This implementation uses <code>toString</code> on the result from
		 * <code>getValueAt</code> (making sure to return an empty string for null values). Subclasses that override this method should never return null.
		 * 
		 * @param row
		 *            the row index
		 * @param column
		 *            the column index
		 * @return the value at the specified index as a <code>String</code>
		 * @throws IndexOutOfBoundsException
		 *             if the indices are outside the range of the model
		 */
		public String getStringValueAt( int row , int column )
		{
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
		
		/**
		 * Returns the identifier for the specified row. The return value of this is used as the identifier for the <code>RowFilter.Entry</code> that is passed
		 * to the <code>RowFilter</code>.
		 * 
		 * @param row
		 *            the row to return the identifier for, in terms of the underlying model
		 * @return the identifier
		 * @see RowFilter.Entry#getIdentifier
		 */
		public abstract I getIdentifier( int row );
	}
	
	public static abstract class ModelCopier<M>
	{
		protected ModelCopier( )
		{
			
		}
		
		public abstract M createEmptyCopy( M model );
		
		public abstract void copyRow( M src , int row , M dest );
	}
	
	/**
	 * RowFilter.Entry implementation that delegates to the ModelWrapper. getFilterEntry(int) creates the single instance of this that is passed to the Filter.
	 * Only call getFilterEntry(int) to get the instance.
	 */
	private static class FilterEntry<M, I> extends RowFilter.Entry<M, I>
	{
		ModelWrapper<M, I>	modelWrapper;
		
		/**
		 * The index into the model, set in getFilterEntry
		 */
		int					modelIndex;
		
		public M getModel( )
		{
			return modelWrapper.getModel( );
		}
		
		public int getValueCount( )
		{
			return modelWrapper.getColumnCount( );
		}
		
		public Object getValue( int index )
		{
			return modelWrapper.getValueAt( modelIndex , index );
		}
		
		public String getStringValue( int index )
		{
			return modelWrapper.getStringValueAt( modelIndex , index );
		}
		
		public I getIdentifier( )
		{
			return modelWrapper.getIdentifier( modelIndex );
		}
	}
	
	/**
	 * Row is used to handle the actual sorting by way of Comparable. It will use the sortKeys to do the actual comparison.
	 */
	// NOTE: this class is static so that it can be placed in an array
	private static class Row<A> implements Comparable<Row<A>>
	{
		private AnnotatingRowSorter<?, ?, A>	sorter;
		int										modelIndex;
		A										annotation;
		
		public Row( AnnotatingRowSorter<?, ?, A> sorter , int index , A annotation )
		{
			this.sorter = sorter;
			modelIndex = index;
			this.annotation = annotation;
		}
		
		public int compareTo( Row<A> o )
		{
			return sorter.compare( modelIndex , o.modelIndex );
		}
	}
	
	public static interface SortRunner
	{
		public void submit( Runnable r );
	}
	
	public static class ExecutorServiceSortRunner implements SortRunner
	{
		ExecutorService	executorService;
		
		public ExecutorServiceSortRunner( ExecutorService executorService )
		{
			super( );
			this.executorService = executorService;
		}
		
		@Override
		public void submit( Runnable r )
		{
			executorService.submit( r );
		}
	}
	
	private static class BackgroundSortTask<M, I, A> implements Runnable
	{
		private final AnnotatingRowSorter<M, I, A>				sorter;
		
		private ModelCopier<M>									modelCopier;
		
		private M												modelCopy;
		
		/**
		 * View (JTable) -> model.
		 */
		private Row<A>[ ]										viewToModel;
		
		/**
		 * model -> view (JTable)
		 */
		private int[ ]											modelToView;
		
		/**
		 * Comparators specified by column.
		 */
		private Comparator[ ]									comparators;
		
		/**
		 * Cached SortKeys for the current sort.
		 */
		private SortKey[ ]										cachedSortKeys;
		
		/**
		 * Cached comparators for the current sort
		 */
		private Comparator[ ]									sortComparators;
		
		/**
		 * Developer supplied Annotator.
		 */
		private RowAnnotator<? super M, ? super I, ? extends A>	annotator;
		
		/**
		 * Developer supplied Filter.
		 */
		private RowFilter<? super M, ? super I>					filter;
		
		/**
		 * Value passed to the filter. The same instance is passed to the filter for different rows.
		 */
		private FilterEntry<M, I>								filterEntry;
		
		/**
		 * The sort keys.
		 */
		private List<SortKey>									sortKeys;
		
		/**
		 * Whether or not to use getStringValueAt. This is indexed by column.
		 */
		private boolean[ ]										useToString;
		
		/**
		 * Indicates the contents are sorted. This is used if getSortsOnUpdates is false and an update event is received.
		 */
		private boolean											sorted;
		
		/**
		 * Provides access to the data we're sorting/filtering.
		 */
		private ModelWrapper<M, I>								modelWrapper;
		
		private Comparator<Row<A>>								rowComparator	= new Comparator<AnnotatingRowSorter.Row<A>>( )
																				{
																					@Override
																					public int compare( Row<A> o1 , Row<A> o2 )
																					{
																						return BackgroundSortTask.this.compare( o1.modelIndex , o2.modelIndex );
																					}
																				};
		
		public BackgroundSortTask( AnnotatingRowSorter<M, I, A> sorter )
		{
			this.sorter = sorter;
			this.viewToModel = sorter.viewToModel;
			this.modelToView = sorter.modelToView;
		}
		
		private ModelWrapper<M, I> getModelWrapper( )
		{
			return modelWrapper;
		}
		
		/**
		 * Sorts the existing filtered data. This should only be used if the filter hasn't changed.
		 */
		private void sortExistingData( )
		{
			updateUseToString( );
			cacheSortKeys( getSortKeys( ) );
			
			if( isUnsorted( ) )
			{
				if( getRowFilter( ) == null )
				{
					viewToModel = null;
					modelToView = null;
				}
				else
				{
					int included = 0;
					for( int i = 0 ; i < modelToView.length ; i++ )
					{
						if( modelToView[ i ] != -1 )
						{
							viewToModel[ included ].modelIndex = i;
							viewToModel[ included ].annotation = annotate( i );
							modelToView[ i ] = included++ ;
						}
					}
				}
			}
			else
			{
				for( int i = 0 ; i < viewToModel.length ; i++ )
				{
					viewToModel[ i ].annotation = annotate( viewToModel[ i ].modelIndex );
				}
				
				// sort the data
				Arrays.sort( viewToModel , rowComparator );
				
				// Update the modelToView array
				setModelToViewFromViewToModel( false );
			}
		}
		
		/**
		 * Sorts and filters the rows in the view based on the sort keys of the columns currently being sorted and the filter, if any, associated with this
		 * sorter. An empty <code>sortKeys</code> list indicates that the view should unsorted, the same as the model.
		 * 
		 * @see #setRowFilter
		 * @see #setSortKeys
		 */
		public void sort( )
		{
			sorted = true;
			updateUseToString( );
			if( isUnsorted( ) )
			{
				// Unsorted
				cachedSortKeys = new SortKey[ 0 ];
				if( getRowFilter( ) == null )
				{
					// No filter & unsorted
					if( viewToModel != null )
					{
						// sorted -> unsorted
						viewToModel = null;
						modelToView = null;
					}
					else
					{
						// unsorted -> unsorted
						// No need to do anything.
						return;
					}
				}
				else
				{
					// There is filter, reset mappings
					initializeFilteredMapping( );
				}
			}
			else
			{
				cacheSortKeys( getSortKeys( ) );
				
				if( getRowFilter( ) != null )
				{
					initializeFilteredMapping( );
				}
				else
				{
					createModelToView( getModelWrapper( ).getRowCount( ) );
					createViewToModel( getModelWrapper( ).getRowCount( ) );
				}
				
				// sort them
				Arrays.sort( viewToModel , rowComparator );
				
				// Update the modelToView array
				setModelToViewFromViewToModel( false );
			}
		}
		
		/**
		 * Updates the useToString mapping before a sort.
		 */
		private void updateUseToString( )
		{
			int i = getModelWrapper( ).getColumnCount( );
			if( useToString == null || useToString.length != i )
			{
				useToString = new boolean[ i ];
			}
			for( --i ; i >= 0 ; i-- )
			{
				useToString[ i ] = useToString( i );
			}
		}
		
		private boolean isUnsorted( )
		{
			// This is changed to always be false so that annotations will be stored even when the rows are unsorted
			return false;
			
			// List<? extends SortKey> keys = getSortKeys( );
			// int keySize = keys.size( );
			// return( keySize == 0 || keys.get( 0 ).getSortOrder( ) == SortOrder.UNSORTED );
		}
		
		/**
		 * Returns the filter that determines which rows, if any, should be hidden from view.
		 * 
		 * @return the filter
		 */
		public RowFilter<? super M, ? super I> getRowFilter( )
		{
			return filter;
		}
		
		/**
		 * Returns true if the specified row should be included.
		 */
		private boolean include( int row )
		{
			RowFilter<? super M, ? super I> filter = getRowFilter( );
			if( filter != null )
			{
				return filter.include( getFilterEntry( row ) );
			}
			// null filter, always include the row.
			return true;
		}
		
		/**
		 * Resets the viewToModel and modelToView mappings based on the current Filter.
		 */
		private void initializeFilteredMapping( )
		{
			int rowCount = getModelWrapper( ).getRowCount( );
			int i, j;
			int excludedCount = 0;
			
			// Update model -> view
			createModelToView( rowCount );
			for( i = 0 ; i < rowCount ; i++ )
			{
				if( include( i ) )
				{
					modelToView[ i ] = i - excludedCount;
				}
				else
				{
					modelToView[ i ] = -1;
					excludedCount++ ;
				}
			}
			
			// Update view -> model
			createViewToModel( rowCount - excludedCount );
			for( i = 0 , j = 0 ; i < rowCount ; i++ )
			{
				if( modelToView[ i ] != -1 )
				{
					viewToModel[ j ].modelIndex = i;
					viewToModel[ j++ ].annotation = annotate( i );
				}
			}
		}
		
		/**
		 * Makes sure the modelToView array is of size rowCount.
		 */
		private void createModelToView( int rowCount )
		{
			if( modelToView == null || modelToView.length != rowCount )
			{
				modelToView = new int[ rowCount ];
			}
		}
		
		/**
		 * Resets the viewToModel array to be of size rowCount.
		 */
		private void createViewToModel( int rowCount )
		{
			int recreateFrom = 0;
			if( viewToModel != null )
			{
				recreateFrom = Math.min( rowCount , viewToModel.length );
				if( viewToModel.length != rowCount )
				{
					Row[ ] oldViewToModel = viewToModel;
					viewToModel = new Row[ rowCount ];
					System.arraycopy( oldViewToModel , 0 , viewToModel ,
							0 , recreateFrom );
				}
			}
			else
			{
				viewToModel = new Row[ rowCount ];
			}
			int i;
			for( i = 0 ; i < recreateFrom ; i++ )
			{
				viewToModel[ i ].modelIndex = i;
				viewToModel[ i ].annotation = annotate( i );
			}
			for( i = recreateFrom ; i < rowCount ; i++ )
			{
				viewToModel[ i ] = new Row( sorter , i , annotate( i ) );
			}
		}
		
		private List<SortKey> getSortKeys( )
		{
			return sortKeys;
		}
		
		/**
		 * Caches the sort keys before a sort.
		 */
		private void cacheSortKeys( List<? extends SortKey> keys )
		{
			int keySize = keys.size( );
			sortComparators = new Comparator[ keySize ];
			for( int i = 0 ; i < keySize ; i++ )
			{
				sortComparators[ i ] = getComparator0( keys.get( i ).getColumn( ) );
			}
			cachedSortKeys = keys.toArray( new SortKey[ keySize ] );
		}
		
		/**
		 * Returns whether or not to convert the value to a string before doing comparisons when sorting. If true <code>ModelWrapper.getStringValueAt</code>
		 * will be used, otherwise <code>ModelWrapper.getValueAt</code> will be used. It is up to subclasses, such as <code>AnnotatingTableRowSorter</code>, to
		 * honor this value in their <code>ModelWrapper</code> implementation.
		 * 
		 * @param column
		 *            the index of the column to test, in terms of the underlying model
		 * @throws IndexOutOfBoundsException
		 *             if <code>column</code> is not valid
		 */
		protected boolean useToString( int column )
		{
			return( getComparator( column ) == null );
		}
		
		/**
		 * Refreshes the modelToView mapping from that of viewToModel. If <code>unsetFirst</code> is true, all indices in modelToView are first set to -1.
		 */
		private void setModelToViewFromViewToModel( boolean unsetFirst )
		{
			int i;
			if( unsetFirst )
			{
				for( i = modelToView.length - 1 ; i >= 0 ; i-- )
				{
					modelToView[ i ] = -1;
				}
			}
			for( i = viewToModel.length - 1 ; i >= 0 ; i-- )
			{
				modelToView[ viewToModel[ i ].modelIndex ] = i;
			}
		}
		
		private void checkColumn( int column )
		{
			if( column < 0 || column >= getModelWrapper( ).getColumnCount( ) )
			{
				throw new IndexOutOfBoundsException(
						"column beyond range of TableModel" );
			}
		}
		
		/**
		 * Returns the <code>Comparator</code> for the specified column. This will return <code>null</code> if a <code>Comparator</code> has not been specified
		 * for the column.
		 * 
		 * @param column
		 *            the column to fetch the <code>Comparator</code> for, in terms of the underlying model
		 * @return the <code>Comparator</code> for the specified column
		 * @throws IndexOutOfBoundsException
		 *             if column is outside the range of the underlying model
		 */
		public Comparator<?> getComparator( int column )
		{
			checkColumn( column );
			if( comparators != null )
			{
				return comparators[ column ];
			}
			return null;
		}
		
		// Returns the Comparator to use during sorting. Where as
		// getComparator() may return null, this will never return null.
		private Comparator getComparator0( int column )
		{
			Comparator comparator = getComparator( column );
			if( comparator != null )
			{
				return comparator;
			}
			// This should be ok as useToString(column) should have returned
			// true in this case.
			return Collator.getInstance( );
		}
		
		@SuppressWarnings( "unchecked" )
		private int compare( int model1 , int model2 )
		{
			int column;
			SortOrder sortOrder;
			Object v1, v2;
			int result;
			
			for( int counter = 0 ; counter < cachedSortKeys.length ; counter++ )
			{
				column = cachedSortKeys[ counter ].getColumn( );
				sortOrder = cachedSortKeys[ counter ].getSortOrder( );
				if( sortOrder == SortOrder.UNSORTED )
				{
					result = model1 - model2;
				}
				else
				{
					// v1 != null && v2 != null
					if( useToString[ column ] )
					{
						v1 = getModelWrapper( ).getStringValueAt( model1 , column );
						v2 = getModelWrapper( ).getStringValueAt( model2 , column );
					}
					else
					{
						v1 = getModelWrapper( ).getValueAt( model1 , column );
						v2 = getModelWrapper( ).getValueAt( model2 , column );
					}
					// Treat nulls as < then non-null
					if( v1 == null )
					{
						if( v2 == null )
						{
							result = 0;
						}
						else
						{
							result = -1;
						}
					}
					else if( v2 == null )
					{
						result = 1;
					}
					else
					{
						result = sortComparators[ counter ].compare( v1 , v2 );
					}
					if( sortOrder == SortOrder.DESCENDING )
					{
						result *= -1;
					}
				}
				if( result != 0 )
				{
					return result;
				}
			}
			// If we get here, they're equal. Fallback to model order.
			return model1 - model2;
		}
		
		private RowFilter.Entry<M, I> getFilterEntry( int modelIndex )
		{
			if( filterEntry == null )
			{
				filterEntry = new FilterEntry<M, I>( );
				filterEntry.modelWrapper = modelWrapper;
			}
			filterEntry.modelIndex = modelIndex;
			return filterEntry;
		}
		
		/**
		 * Returns true if the specified row should be included.
		 */
		private A annotate( int row )
		{
			if( annotator != null )
			{
				return annotator.annotate( getFilterEntry( row ) );
			}
			return null;
		}
		
		private int[ ] getViewToModelAsInts( Row[ ] viewToModel )
		{
			if( viewToModel != null )
			{
				int[ ] viewToModelI = new int[ viewToModel.length ];
				for( int i = viewToModel.length - 1 ; i >= 0 ; i-- )
				{
					viewToModelI[ i ] = viewToModel[ i ].modelIndex;
				}
				return viewToModelI;
			}
			return new int[ 0 ];
		}
		
		@Override
		public void run( )
		{
			try
			{
				// NOTE: To guarantee thread safety, all FilteringTableModel
				// instance variables should only be accessed on the Swing
				// thread!
				
				boolean sortRequested;
				boolean sortExistingDataRequested;
				
				do
				{
					// copy the filter, column names and classes and clear the
					// backingModelChanged flag (the instance variable, not the
					// local variable) on the EDT.
					
					PreSort preSort = new PreSort( );
					doSwing( preSort );
					
					sortRequested = preSort.sortRequested;
					sortExistingDataRequested = preSort.sortExistingDataRequested;
					
					// Copy the rows from backingModel in chunks on the EDT, so we
					// don't tie it up. If the backing model is changed during this
					// process, start over again.
					
					RowCopier rowCopier = new RowCopier( );
					while( !rowCopier.complete && !rowCopier.sortRequested && !rowCopier.sortExistingDataRequested )
					{
						doSwing( rowCopier );
					}
					if( rowCopier.sortRequested || rowCopier.sortExistingDataRequested )
					{
						continue;
					}
					
					// Now we have a coherent copy of the backing model, and we can
					// sort it.
					
					int[ ] lastRowIndexToModel = getViewToModelAsInts( viewToModel );
					
					if( sortRequested || viewToModel == null )
					{
						sort( );
					}
					else if( sortExistingDataRequested )
					{
						sortExistingData( );
					}
					
					// Install the filtered data on the EDT, and check if the
					// backing
					// model has been changed again.
					
					PostSort postSort = new PostSort( lastRowIndexToModel );
					doSwing( postSort );
					
					sortRequested = postSort.sortRequested;
					sortExistingDataRequested = postSort.sortExistingDataRequested;
					
					// if the backing model changed after all the data was copied,
					// start over again.
					
				} while( sortRequested || sortExistingDataRequested );
			}
			catch( Exception ex )
			{
				ex.printStackTrace( );
			}
			finally
			{
				new DoSwing( )
				{
					@Override
					public void run( )
					{
						sorter.sortTask = null;
					}
				};
			}
		}
		
		private class PreSort implements Runnable
		{
			boolean	sortRequested;
			boolean	sortExistingDataRequested;
			
			@Override
			public void run( )
			{
				modelCopier = sorter.modelCopier;
				
				sortRequested = sorter.sortRequested;
				sortExistingDataRequested = sorter.sortExistingDataRequested;
				
				modelCopy = modelCopier.createEmptyCopy( sorter.getModel( ) );
				comparators = sorter.comparators == null ? null : Arrays.copyOf( sorter.comparators , sorter.comparators.length );
				cachedSortKeys = sorter.cachedSortKeys == null ? null : Arrays.copyOf( sorter.cachedSortKeys , sorter.cachedSortKeys.length );
				annotator = sorter.annotator;
				filter = sorter.filter;
				sortKeys = sorter.sortKeys;
				sorted = sorter.sorted;
				useToString = sorter.useToString == null ? null : Arrays.copyOf( sorter.useToString , sorter.useToString.length );
				
				modelWrapper = sorter.createModelWrapper( modelCopy );
				
				sorter.sortRequested = false;
				sorter.sortExistingDataRequested = false;
			}
		}
		
		private class RowCopier implements Runnable
		{
			int		nextRow;
			int		stepSize	= 100;
			boolean	sortRequested;
			boolean	sortExistingDataRequested;
			boolean	complete;
			
			@Override
			public void run( )
			{
				sortRequested = sorter.sortRequested;
				sortExistingDataRequested = sorter.sortExistingDataRequested;
				
				if( sortRequested || sortExistingDataRequested )
				{
					return;
				}
				
				for( int i = 0 ; i < stepSize && nextRow < sorter.getModelRowCount( ) ; i++ , nextRow++ )
				{
					modelCopier.copyRow( sorter.getModel( ) , nextRow , modelCopy );
				}
				
				complete = nextRow == sorter.getModelRowCount( );
			}
		}
		
		private class PostSort implements Runnable
		{
			boolean	sortRequested;
			boolean	sortExistingDataRequested;
			
			int[ ]	lastRowIndexToModel;
			
			public PostSort( int[ ] lastRowIndexToModel )
			{
				super( );
				this.lastRowIndexToModel = lastRowIndexToModel;
			}
			
			@Override
			public void run( )
			{
				sortRequested = sorter.sortRequested;
				sortExistingDataRequested = sorter.sortExistingDataRequested;
				
				if( sortRequested || sortExistingDataRequested )
				{
					return;
				}
				
				sorter.viewToModel = viewToModel;
				sorter.modelToView = modelToView;
				sorter.cachedSortKeys = cachedSortKeys;
				sorter.useToString = useToString;
				sorter.sorted = sorted;
				
				sorter.sortTask = null;
				
				sorter.fireRowSorterChanged( lastRowIndexToModel );
				
				while( !sorter.invokeWhenDoneSortingQueue.isEmpty( ) )
				{
					try
					{
						sorter.invokeWhenDoneSortingQueue.poll( ).run( );
					}
					catch( Exception ex )
					{
						ex.printStackTrace( );
					}
				}
			}
		}
	}
}
