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
	Function<Object, String>				valueFormatter;

	public ParsedTextWithValueCellEditor( Function<String, ParsedTextWithValue> parser ,
		Function<Object, String> valueFormatter )
	{
		super( new JTextField( ) );
		this.parser = parser;
		this.valueFormatter = valueFormatter;
	}

	private String textOf( Object value )
	{
		if( value instanceof ParsedTextWithValue )
		{
			ParsedTextWithValue pt = ( ParsedTextWithValue ) value;
			return pt.text != null ? pt.text : pt.value != null ? valueFormatter.apply( pt.value ) : null;
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
