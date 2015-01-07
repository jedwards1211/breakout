package org.andork.breakout.table;

import java.awt.Component;
import java.util.function.Function;

import javax.swing.DefaultCellEditor;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTree;

import org.andork.util.StringUtils;

@SuppressWarnings( "serial" )
public class ParsedTextWithValueCellEditor extends DefaultCellEditor
{
	Function<String, ParsedTextWithValue>	parser;

	public ParsedTextWithValueCellEditor( Function<String, ParsedTextWithValue> parser )
	{
		super( new JTextField( ) );
		this.parser = parser;
	}

	private static String textOf( Object value )
	{
		if( value instanceof ParsedText )
		{
			return ( ( ParsedText ) value ).text;
		}
		return null;
	}

	@Override
	public Component getTreeCellEditorComponent( JTree tree , Object value , boolean isSelected , boolean expanded ,
		boolean leaf , int row )
	{
		return super.getTreeCellEditorComponent( tree , textOf( value ) , isSelected , expanded , leaf , row );
	}

	@Override
	public Component getTableCellEditorComponent( JTable table , Object value , boolean isSelected , int row ,
		int column )
	{
		return super.getTableCellEditorComponent( table , textOf( value ) , isSelected , row , column );
	}

	@Override
	public Object getCellEditorValue( )
	{
		Object o = super.getCellEditorValue( );
		return StringUtils.isNullOrEmpty( o ) ? null : parser.apply( o.toString( ) );
	}
}
