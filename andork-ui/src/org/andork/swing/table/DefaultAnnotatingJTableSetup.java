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
	public final AnnotatingJTable<M, A>					table;
	public final AnnotatingRowSorterCursorController	cursorController;
	
	public DefaultAnnotatingJTableSetup( AnnotatingJTable<M, A> table , SortRunner sortRunner )
	{
		this.table = table;
		AnnotatingTableRowSorter<M, A> sorter = new AnnotatingTableRowSorter<M, A>( table , sortRunner );
		table.setRowSorter( sorter );
		
		scrollPane = new JScrollAndJumpPane( table );
		scrollPane.getJumpBar( ).setModel( new AnnotatingJTableJumpBarModel( table ) );
		scrollPane.getJumpBar( ).setJumpSupport( new JTableJumpSupport( table ) );
		
		cursorController = new AnnotatingRowSorterCursorController( scrollPane );
		sorter.addRowSorterListener( cursorController );
	}
	
	protected AnnotatingJTable<M, A> createTable( M model )
	{
		return new AnnotatingJTable<M, A>( model );
	}
	
	public void setAnnotationColors( Map<? extends A, Color> colors )
	{
		table.setAnnotationColors( colors );
		scrollPane.getJumpBar( ).setColorMap( colors );
	}
}
