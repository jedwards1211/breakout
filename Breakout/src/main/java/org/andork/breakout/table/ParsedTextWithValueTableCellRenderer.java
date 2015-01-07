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

import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

@SuppressWarnings( "serial" )
public class ParsedTextWithValueTableCellRenderer extends DefaultTableCellRenderer
{
	private Function<Object, Object>	valueRenderer;
	private Function<Object, Color>		noteColor;
	private Function<Object, String>	noteMessage;

	public ParsedTextWithValueTableCellRenderer(
		Function<Object, Object> valueRenderer ,
		Function<Object, Color> noteColor ,
		Function<Object, String> noteMessage )
	{
		super( );
		this.valueRenderer = valueRenderer;
		this.noteColor = noteColor;
		this.noteMessage = noteMessage;
	}

	@Override
	public Component getTableCellRendererComponent( JTable table , Object value , boolean isSelected ,
		boolean hasFocus , int row , int column )
	{
		Object superValue = value;

		if( value instanceof ParsedTextWithValue )
		{
			ParsedTextWithValue p = ( ParsedTextWithValue ) value;
			if( p.value != null )
			{
				superValue = valueRenderer.apply( p.value );
			}
			else
			{
				superValue = p.text;
			}
		}

		JComponent renderer = ( JComponent ) super.getTableCellRendererComponent( table , superValue , isSelected ,
			hasFocus , row , column );

		if( !isSelected )
		{
			renderer.setBackground( null );
		}

		if( value instanceof ParsedTextWithValue )
		{
			ParsedTextWithValue p = ( ParsedTextWithValue ) value;
			Color color = p.note == null ? null : noteColor.apply( p.note );
			if( color != null )
			{
				renderer.setBackground( color );
			}
			renderer.setToolTipText( p.note == null ? null : noteMessage.apply( p.note ) );
		}

		return renderer;
	}
}
