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

import java.awt.Color;
import java.awt.Component;
import java.net.URL;
import java.util.function.Function;
import java.util.function.Predicate;

import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * Renders {@link ParsedText} cell values.
 * 
 * @author James
 *
 * @param <V>
 *            the value parameter for the {@link ParsedText} cell contents.
 */
@SuppressWarnings( "serial" )
public class ParsedTextTableCellRenderer<V> extends DefaultTableCellRenderer
{
	private Function<? super V, ?>					valueFormatter;
	private Predicate<? super ParsedText<?>>		forceShowText;
	private Function<? super ParsedText<?>, Color>	backgroundColorFn;
	private Function<? super ParsedText<?>, String>	messageFn;

	/**
	 * @param valueFormatter
	 *            a function that takes a {@link URL} and returns an {@link Object} (typically a {@link String}
	 *            representing the {@code URL} for {@link DefaultTableCellRenderer} code to render
	 * @param forceShowText
	 *            a function that takes a cell value and returns {@code true} if and only if there was an error
	 *            parsing the last text typed into that cell (if so the original text will be displayed, instead of the
	 *            reformatted value)
	 * @param backgroundColorFn
	 *            a function that takes a cell value and returns the background color to use for the cell (this is
	 *            provided so that parse errors and warnings can be highlighted red and yellow)
	 * @param tooltipFn
	 *            a function that takes a cell value and returns the tooltip to display for the cell (this is provided
	 *            so that parse error and warning messages can be displayed as tooltips)
	 */
	public ParsedTextTableCellRenderer(
		Function<? super V, ?> valueFormatter ,
		Predicate<? super ParsedText<?>> forceShowText ,
		Function<? super ParsedText<?>, Color> backgroundColorFn ,
		Function<? super ParsedText<?>, String> messageFn )
	{
		super( );
		this.valueFormatter = valueFormatter;
		this.forceShowText = forceShowText;
		this.backgroundColorFn = backgroundColorFn;
		this.messageFn = messageFn;
	}

	@SuppressWarnings( "unchecked" )
	@Override
	public Component getTableCellRendererComponent( JTable table , Object value , boolean isSelected ,
		boolean hasFocus , int row , int column )
	{
		Object superValue = value;

		if( value instanceof ParsedText )
		{
			ParsedText<V> p = ( ParsedText<V> ) value;
			if( p.getValue( ) != null && !forceShowText.test( p ) )
			{
				superValue = valueFormatter.apply( p.getValue( ) );
			}
			else
			{
				superValue = p.getText( );
			}
		}

		JComponent renderer = ( JComponent ) super.getTableCellRendererComponent( table , superValue , isSelected ,
			hasFocus , row , column );

		if( !isSelected )
		{
			renderer.setBackground( null );
		}

		if( value instanceof ParsedText )
		{
			ParsedText<V> p = ( ParsedText<V> ) value;
			Color color = backgroundColorFn.apply( p );
			if( color != null )
			{
				renderer.setBackground( color );
			}
			renderer.setToolTipText( messageFn.apply( p ) );
		}

		return renderer;
	}
}
