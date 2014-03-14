package org.andork.swing.table;

import java.awt.Color;
import java.util.Collections;
import java.util.Map;

import javax.swing.RowFilter;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.TableModel;
import javax.swing.text.JTextComponent;

import org.andork.swing.AnnotatingRowSorter.SortRunner;
import org.andork.swing.AnnotatingRowSorterCursorController;
import org.andork.swing.RowAnnotator;
import org.andork.swing.event.EasyDocumentListener;
import org.andork.swing.jump.JScrollAndJumpPane;
import org.andork.swing.jump.JTableJumpSupport;

public class DefaultAnnotatingJTableSetup<M extends TableModel, A>
{
	public final JScrollAndJumpPane						scrollPane;
	public final AnnotatingJTable						table;
	public final AnnotatingTableRowSorter<M, A>			sorter;
	public final AnnotatingRowSorterCursorController	cursorController;
	
	public DefaultAnnotatingJTableSetup( AnnotatingJTable table , SortRunner sortRunner )
	{
		this.table = table;
		sorter = new AnnotatingTableRowSorter<M, A>( table , sortRunner );
		table.setRowSorter( sorter );
		
		scrollPane = new JScrollAndJumpPane( table );
		scrollPane.getJumpBar( ).setModel( new AnnotatingJTableJumpBarModel( table ) );
		scrollPane.getJumpBar( ).setJumpSupport( new JTableJumpSupport( table ) );
		
		cursorController = new AnnotatingRowSorterCursorController( scrollPane );
		sorter.addRowSorterListener( cursorController );
	}
	
	protected AnnotatingJTable createTable( M model )
	{
		return new AnnotatingJTable( model );
	}
	
	protected RowFilter<M, Integer> createFilter( final JTextComponent textComp )
	{
		RowFilter<M, Integer> filter;
		filter = RowFilter.regexFilter( textComp.getText( ) , 0 );
		return filter;
	}
	
	public void setAnnotationColors( Map<?, Color> colors )
	{
		table.setAnnotationColors( colors );
		scrollPane.getJumpBar( ).setColorMap( colors );
	}
	
	public static <M extends TableModel> DocumentListener createHighlightFieldListener(
			final DefaultAnnotatingJTableSetup<M, ? super RowFilter<M, Integer>> setup ,
			final JTextComponent highlightField , final Color highlightColor )
	{
		return new EasyDocumentListener( )
		{
			@Override
			public void documentChanged( DocumentEvent e )
			{
				if( highlightField.getText( ) != null && highlightField.getText( ).length( ) > 0 )
				{
					RowFilter<M, Integer> filter = null;
					try
					{
						filter = setup.createFilter( highlightField );
						highlightField.setForeground( Color.BLACK );
					}
					catch( Exception ex )
					{
						highlightField.setForeground( Color.RED );
					}
					setup.sorter.setRowAnnotator( RowAnnotator.filterAnnotator( filter ) );
					setup.setAnnotationColors( Collections.singletonMap( filter , highlightColor ) );
				}
				else
				{
					highlightField.setForeground( Color.BLACK );
					
					setup.sorter.setRowAnnotator( null );
					setup.setAnnotationColors( Collections.<Object,Color>emptyMap( ) );
				}
				
			}
		};
	}
	
	public static <M extends TableModel> DocumentListener createFilterFieldListener(
			final DefaultAnnotatingJTableSetup<M, ? super RowFilter<M, Integer>> setup , final JTextComponent filterField )
	{
		return new EasyDocumentListener( )
		{
			@Override
			public void documentChanged( DocumentEvent e )
			{
				if( filterField.getText( ) != null && filterField.getText( ).length( ) > 0 )
				{
					RowFilter<M, Integer> filter = null;
					try
					{
						filter = setup.createFilter( filterField );
						filterField.setForeground( Color.BLACK );
					}
					catch( Exception ex )
					{
						filterField.setForeground( Color.RED );
					}
					setup.sorter.setRowFilter( filter );
				}
				else
				{
					filterField.setForeground( Color.BLACK );
					
					setup.sorter.setRowFilter( null );
				}
			}
		};
	}
}
