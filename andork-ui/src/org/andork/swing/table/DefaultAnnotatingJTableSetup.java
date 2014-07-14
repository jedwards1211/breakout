package org.andork.swing.table;

import java.awt.Color;
import java.util.Map;
import java.util.function.Function;

import javax.swing.table.TableModel;

import org.andork.swing.AnnotatingRowSorter.SortRunner;
import org.andork.swing.AnnotatingRowSorterCursorController;
import org.andork.swing.jump.JScrollAndJumpPane;
import org.andork.swing.jump.JTableJumpSupport;

public class DefaultAnnotatingJTableSetup
{
	public final JScrollAndJumpPane						scrollPane;
	public final AnnotatingJTable						table;
	public final AnnotatingRowSorterCursorController	cursorController;
	
	public DefaultAnnotatingJTableSetup( AnnotatingJTable table , SortRunner sortRunner )
	{
		this.table = table;
		AnnotatingTableRowSorter sorter;
		if( table.getRowSorter( ) instanceof AnnotatingTableRowSorter )
		{
			sorter = ( AnnotatingTableRowSorter ) table.getRowSorter( );
		}
		else
		{
			sorter = new AnnotatingTableRowSorter( table , sortRunner );
			table.setRowSorter( sorter );
		}
		
		scrollPane = new JScrollAndJumpPane( table );
		scrollPane.getJumpBar( ).setModel( new AnnotatingJTableJumpBarModel( table ) );
		scrollPane.getJumpBar( ).setJumpSupport( new JTableJumpSupport( table ) );
		
		cursorController = new AnnotatingRowSorterCursorController( scrollPane );
		sorter.addRowSorterListener( cursorController );
	}
	
	protected AnnotatingJTable createTable( TableModel model )
	{
		return new AnnotatingJTable( model );
	}
	
	public void setAnnotationColors( Map<?, Color> colors )
	{
		table.setAnnotationColors( colors );
		scrollPane.getJumpBar( ).setColorMap( colors );
	}
	
	public void setColorer( Function<Object, Color> colorer )
	{
		table.setColorer( colorer );
		scrollPane.getJumpBar( ).setColorer( colorer );
	}
}
