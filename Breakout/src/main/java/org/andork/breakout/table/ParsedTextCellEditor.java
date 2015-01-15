package org.andork.breakout.table;

import java.awt.Component;
import java.util.function.Function;

import javax.swing.DefaultCellEditor;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTree;

import org.andork.util.StringUtils;

/**
 * Editor for {@link ParsedText} cells.
 * 
 * @author James
 *
 * @param <V>
 *            the value parameter for {@link ParsedText}.
 */
@SuppressWarnings( "serial" )
public class ParsedTextCellEditor<V> extends DefaultCellEditor
{
	Function<String, ParsedText<? extends V>>	parser;
	Function<? super V, String>					valueFormatter;

	/**
	 * @param valueFormatter
	 *            takes the {@link ParsedText#getValue() value} of a cell's {@link ParsedText} and formats it into a
	 *            string. This is used when the {@link ParsedText} has a {@link ParsedText#getValue() value} but no
	 *            {@link ParsedText#getText() text}.<br>
	 *            {@code parser.apply(valueFormatter.apply(value))} must return a {@link ParsedText} with a
	 *            {@link ParsedText#getValue() value} {@link Object#equals(Object) equal} to {@code value}.
	 * @param parser
	 *            takes text the user entered and returns a {@link ParsedText} representing the results of the parse.
	 *            This is used by {@link #getCellEditorValue()}.
	 */
	public ParsedTextCellEditor( Function<? super V, String> valueFormatter  ,
		Function<String, ParsedText<? extends V>> parser  )
	{
		super( new JTextField( ) );
		this.parser = parser;
		this.valueFormatter = valueFormatter;
	}

	private String textOf( Object value )
	{
		if( value instanceof ParsedText )
		{
			ParsedText<? extends V> pt = ( ParsedText<? extends V> ) value;
			return pt.getText( ) != null ? pt.getText( ) : pt.getValue( ) != null ? valueFormatter
				.apply( pt.getValue( ) ) : null;
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
