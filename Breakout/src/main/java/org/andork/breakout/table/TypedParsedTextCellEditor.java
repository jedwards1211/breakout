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

@SuppressWarnings( "serial" )
public class TypedParsedTextCellEditor<V> extends DefaultCellEditor
{
	final DefaultSelector<Object>						typeSelector;

	BiFunction<String, Object, ParsedTextWithType<V>>	parser;
	Function<V, String>									valueFormatter;
	Function<Object, ?>									typeGetter;

	Border												compoundBorder;
	Border												outerBorder;

	public TypedParsedTextCellEditor(
		Function<V, String> valueFormatter ,
		Function<Object, ?> typeGetter ,
		BiFunction<String, Object, ParsedTextWithType<V>> parser )
	{
		this( new JTextField( ) , new DefaultSelector<>( ) , valueFormatter , typeGetter , parser );
	}

	public TypedParsedTextCellEditor(
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
		textField.add( typeSelector.getComboBox( ) , BorderLayout.EAST );
	}

	@Override
	public boolean isCellEditable( EventObject anEvent )
	{
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
					if( deepest == typeSelector.getComboBox( ) )
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
		textField.setFont( table.getFont( ) );
		textField.setBorder( compoundBorder );
		textField.setLayout( new Layout( ) );
		textField.add( typeSelector.getComboBox( ) , BorderLayout.EAST );
		return textField;
	}

	private class Layout extends BorderLayout
	{
		@Override
		public void layoutContainer( Container target )
		{
			Dimension targetSize = target.getSize( );
			Insets insets = outerBorder.getBorderInsets( target );
			Rectangle bounds = new Rectangle( typeSelector.getComboBox( ).getPreferredSize( ) );
			bounds.width = Math.min( bounds.width , targetSize.width - insets.left - insets.right );
			bounds.height = Math.min( bounds.height , targetSize.height - insets.top - insets.bottom );
			bounds.y = insets.top;
			bounds.x = targetSize.width - insets.right - bounds.width;

			typeSelector.getComboBox( ).setBounds( bounds );
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
			return new Insets( 0 , 0 , 0 , typeSelector.getComboBox( ).getPreferredSize( ).width );
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
