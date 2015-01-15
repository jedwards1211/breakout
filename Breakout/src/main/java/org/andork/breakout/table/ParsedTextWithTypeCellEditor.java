/*******************************************************************************
 * Breakout Cave Survey Visualizer
 *
 * Copyright (C) 2014 James Edwards
 *
 * jedwards8 at fastmail dot fm
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *******************************************************************************/
package org.andork.breakout.table;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.util.Collection;
import java.util.EventObject;
import java.util.function.BiFunction;
import java.util.function.Function;

import javax.swing.DefaultCellEditor;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableCellEditor;

import org.andork.swing.selector.DefaultSelector;

/**
 * Editor for {@link ParsedTextWithType} cells. It contains a {@link JTextField} and a {@link DefaultSelector} dropdown
 * for selecting the desired type. The text and selected type are parsed by a lambda into a {@link ParsedTextWithType}.
 * 
 * @author James
 *
 * @param <V>
 *            the value parameter for the {@link ParsedTextWithType} cell values.
 */
@SuppressWarnings( "serial" )
public class ParsedTextWithTypeCellEditor<V> extends DefaultCellEditor
{
	final DefaultSelector<Object>						typeSelector;

	BiFunction<String, Object, ParsedTextWithType<V>>	parser;
	Function<V, String>									valueFormatter;
	Function<Object, ?>									typeGetter;

	Border												compoundBorder;
	Border												outerBorder;

	/**
	 * @param valueFormatter
	 *            takes the {@link ParsedText#getValue() value} of a cell's {@link ParsedText} and formats it into a
	 *            string. This is used when the {@link ParsedText} has a {@link ParsedText#getValue() value} but no
	 *            {@link ParsedText#getText() text}.<br>
	 *            {@code parser.apply(valueFormatter.apply(value))} must return a {@link ParsedText} with a
	 *            {@link ParsedText#getValue() value} {@link Object#equals(Object) equal} to {@code value}.
	 * @param typeGetter
	 *            takes the cell value and returns the type that should be selected in the dropdown.
	 * @param parser
	 *            takes text the user entered and returns a {@link ParsedText} representing the results of the parse.
	 *            This is used by {@link #getCellEditorValue()}.
	 */
	public ParsedTextWithTypeCellEditor(
		Function<V, String> valueFormatter ,
		Function<Object, ?> typeGetter ,
		BiFunction<String, Object, ParsedTextWithType<V>> parser )
	{
		this( new JTextField( ) , new DefaultSelector<>( ) , valueFormatter , typeGetter , parser );
	}

	public ParsedTextWithTypeCellEditor(
		JTextField textField ,
		DefaultSelector<Object> typeSelector ,
		Function<V, String> valueFormatter ,
		Function<Object, ?> typeGetter ,
		BiFunction<String, Object, ParsedTextWithType<V>> parser )
	{
		super( textField );
		this.typeSelector = typeSelector;
		this.valueFormatter = valueFormatter;
		this.typeGetter = typeGetter;
		this.parser = parser;
		outerBorder = new EmptyBorder( 0 , 0 , 0 , 0 );
		textField.setBorder( compoundBorder = new CompoundBorder( outerBorder , new InnerBorder( ) ) );
		textField.setLayout( new Layout( ) );
		textField.add( typeSelector.comboBox( ) , BorderLayout.EAST );
	}

	/**
	 * This method is overridden to return {@code true} the first time the user clicks on the {@link #typeSelector()}.
	 * When the user clicks anywhere else the super behavior will be used (usually two clicks).<br>
	 * <br>
	 * {@inheritDoc}
	 */
	@Override
	public boolean isCellEditable( EventObject anEvent )
	{
		// this is a lot of code, but it's actually just determining if the user clicked on the type selector.
		if( anEvent instanceof MouseEvent )
		{
			MouseEvent me = ( MouseEvent ) anEvent;
			JTable table = ( JTable ) me.getComponent( );
			int row = table.rowAtPoint( me.getPoint( ) );
			int column = table.columnAtPoint( me.getPoint( ) );
			if( row >= 0 && column >= 0 )
			{
				TableCellEditor editor = table.getCellEditor( table.rowAtPoint( me.getPoint( ) ) ,
					table.columnAtPoint( me.getPoint( ) ) );
				if( editor == this )
				{
					Rectangle cellRect = table.getCellRect( row , column , true );
					Component editorComp = table.prepareEditor( editor , row , column );
					editorComp.setBounds( cellRect );
					editorComp.doLayout( );
					Component deepest = SwingUtilities.getDeepestComponentAt( editorComp , me.getX( ) - cellRect.x ,
						me.getY( ) - cellRect.y );
					if( deepest == typeSelector.comboBox( ) )
					{
						return true;
					}
				}
			}
		}
		return super.isCellEditable( anEvent );
	}

	@Override
	public Object getCellEditorValue( )
	{
		Object o = super.getCellEditorValue( );
		return parser.apply( o.toString( ) , typeSelector.getSelection( ) );
	}

	@SuppressWarnings( "rawtypes" )
	@Override
	public Component getTableCellEditorComponent( JTable table , Object value , boolean isSelected , int row ,
		int column )
	{
		ParsedText<? extends V> pt = ( ParsedText<? extends V> ) value;
		String text = pt == null ? null : pt.getText( );
		V val = pt == null ? null : pt.getValue( );
		Object superValue = text != null ? text :
			val != null ? valueFormatter.apply( val ) : null;
		typeSelector.setSelection( typeGetter.apply( pt ) );
		JTextField textField = ( JTextField ) super.getTableCellEditorComponent( table , superValue , isSelected , row ,
			column );
		textField.setBorder( compoundBorder );
		textField.setLayout( new Layout( ) );
		textField.add( typeSelector.comboBox( ) , BorderLayout.EAST );
		return textField;
	}

	private class Layout extends BorderLayout
	{
		@Override
		public void layoutContainer( Container target )
		{
			Dimension targetSize = target.getSize( );
			Insets insets = outerBorder.getBorderInsets( target );
			Rectangle bounds = new Rectangle( typeSelector.comboBox( ).getPreferredSize( ) );
			bounds.width = Math.min( bounds.width , targetSize.width - insets.left - insets.right );
			bounds.height = Math.min( bounds.height , targetSize.height - insets.top - insets.bottom );
			bounds.y = insets.top;
			bounds.x = targetSize.width - insets.right - bounds.width;

			typeSelector.comboBox( ).setBounds( bounds );
		}
	}

	private class InnerBorder implements Border
	{
		@Override
		public void paintBorder( Component c , Graphics g , int x , int y , int width , int height )
		{
		}

		@Override
		public Insets getBorderInsets( Component c )
		{
			return new Insets( 0 , 0 , 0 , typeSelector.comboBox( ).getPreferredSize( ).width );
		}

		@Override
		public boolean isBorderOpaque( )
		{
			return false;
		}
	}

	public DefaultSelector<Object> typeSelector( )
	{
		return typeSelector;
	}

	public void setAvailableTypes( Collection<?> types )
	{
		typeSelector.setAvailableValues( types );
	}
}
