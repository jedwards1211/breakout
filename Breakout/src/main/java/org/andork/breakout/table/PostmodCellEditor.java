package org.andork.breakout.table;

import java.awt.Component;
import java.util.EventObject;
import java.util.function.BiConsumer;

import javax.swing.CellEditor;
import javax.swing.JTable;
import javax.swing.event.CellEditorListener;
import javax.swing.table.TableCellEditor;

public class PostmodCellEditor implements CellEditor , TableCellEditor
{
	CellEditor						wrapped;
	BiConsumer<JTable, Component>	postModifier;

	public PostmodCellEditor( CellEditor wrapped , BiConsumer<JTable, Component> postModifier )
	{
		super( );
		this.wrapped = wrapped;
		this.postModifier = postModifier;
	}

	@Override
	public Component getTableCellEditorComponent( JTable table , Object value , boolean isSelected , int row ,
		int column )
	{
		Component editor = ( ( TableCellEditor ) wrapped ).getTableCellEditorComponent( table , value , isSelected ,
			row , column );
		postModifier.accept( table , editor );
		return editor;
	}

	@Override
	public Object getCellEditorValue( )
	{
		return wrapped.getCellEditorValue( );
	}

	@Override
	public boolean isCellEditable( EventObject anEvent )
	{
		return wrapped.isCellEditable( anEvent );
	}

	@Override
	public boolean shouldSelectCell( EventObject anEvent )
	{
		return wrapped.shouldSelectCell( anEvent );
	}

	@Override
	public boolean stopCellEditing( )
	{
		return wrapped.stopCellEditing( );
	}

	@Override
	public void cancelCellEditing( )
	{
		wrapped.cancelCellEditing( );
	}

	@Override
	public void addCellEditorListener( CellEditorListener l )
	{
		wrapped.addCellEditorListener( l );
	}

	@Override
	public void removeCellEditorListener( CellEditorListener l )
	{
		wrapped.removeCellEditorListener( l );
	}
}
