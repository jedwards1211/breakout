package org.andork.swing.table;

import java.awt.Color;
import java.util.Collections;
import java.util.Map;

import javax.swing.RowFilter;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.TableModel;
import javax.swing.text.JTextComponent;

import org.andork.awt.AWTUtil;
import org.andork.swing.RowAnnotator;
import org.andork.swing.event.EasyDocumentListener;
import org.andork.swing.jump.JScrollAndJumpPane;

public class AnnotatingJTables
{
	public static <M extends TableModel> DocumentListener createHighlightFieldListener(
			final AnnotatingJTable<M, ? super RowFilter<M, Integer>> table ,
			final JTextComponent highlightField , final RowFilterFactory<String, M, Integer> filterFactory , final Color highlightColor )
	{
		return new EasyDocumentListener( )
		{
			@Override
			public void documentChanged( DocumentEvent e )
			{
				if( table.getAnnotatingRowSorter( ) == null )
				{
					{
						return;
					}
				}
				
				RowFilter<M, Integer> filter = null;
				
				if( highlightField.getText( ) != null && highlightField.getText( ).length( ) > 0 )
				{
					try
					{
						filter = filterFactory.createFilter( highlightField.getText( ) );
						highlightField.setForeground( Color.BLACK );
					}
					catch( Exception ex )
					{
						highlightField.setForeground( Color.RED );
					}
				}
				if( filter != null )
				{
					table.getAnnotatingRowSorter( ).setRowAnnotator( RowAnnotator.filterAnnotator( filter ) );
					setAnnotationColors( table , Collections.singletonMap( filter , highlightColor ) );
				}
				else
				{
					highlightField.setForeground( Color.BLACK );
					setAnnotationColors( table , Collections.<RowFilter<M, Integer>,Color>emptyMap( ) );
				}
				
			}
		};
	}
	
	private static <M extends TableModel, A> void setAnnotationColors( AnnotatingJTable<M, A> table , Map<? extends A, Color> colors )
	{
		table.setAnnotationColors( colors );
		JScrollAndJumpPane scrollPane = AWTUtil.getAncestorOfClass( JScrollAndJumpPane.class , table );
		if( scrollPane != null )
		{
			scrollPane.getJumpBar( ).setColorMap( colors );
		}
	}
	
	public static <M extends TableModel> DocumentListener createFilterFieldListener(
			final AnnotatingJTable<M, ? super RowFilter<M, Integer>> table , final JTextComponent filterField ,
			final RowFilterFactory<String, M, Integer> filterFactory )
	{
		return new EasyDocumentListener( )
		{
			@Override
			public void documentChanged( DocumentEvent e )
			{
				if( table.getAnnotatingRowSorter( ) == null )
				{
					return;
				}
				if( filterField.getText( ) != null && filterField.getText( ).length( ) > 0 )
				{
					RowFilter<M, Integer> filter = null;
					try
					{
						filter = filterFactory.createFilter( filterField.getText( ) );
						filterField.setForeground( Color.BLACK );
					}
					catch( Exception ex )
					{
						filterField.setForeground( Color.RED );
					}
					table.getAnnotatingRowSorter( ).setRowFilter( filter );
				}
				else
				{
					filterField.setForeground( Color.BLACK );
					
					table.getAnnotatingRowSorter( ).setRowFilter( null );
				}
			}
		};
	}
	
}