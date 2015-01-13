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
import java.util.function.Function;
import java.util.function.Predicate;

import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

@SuppressWarnings( "serial" )
public class ParsedTextTableCellRenderer<V> extends DefaultTableCellRenderer
{
	private Function<? super V, ?>		valueFormatter;
	private Predicate<Object>			parseErrorTest;
	private Function<Object, Color>		noteColorGetter;
	private Function<Object, String>	noteMessageGetter;

	public ParsedTextTableCellRenderer(
		Function<? super V, ?> valueFormatter ,
		Predicate<Object> parseErrorTest ,
		Function<Object, Color> noteColorGetter ,
		Function<Object, String> noteMessageGetter )
	{
		super( );
		this.valueFormatter = valueFormatter;
		this.parseErrorTest = parseErrorTest;
		this.noteColorGetter = noteColorGetter;
		this.noteMessageGetter = noteMessageGetter;
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
			if( p.getValue( ) != null && ( p.getNote( ) == null || !parseErrorTest.test( p.getNote( ) ) ) )
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
			Color color = p.getNote( ) == null ? null : noteColorGetter.apply( p.getNote( ) );
			if( color != null )
			{
				renderer.setBackground( color );
			}
			renderer.setToolTipText( p.getNote( ) == null ? null : noteMessageGetter.apply( p.getNote( ) ) );
		}

		return renderer;
	}
}
