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
package org.andork.swing.table;

import java.awt.Color;
import java.awt.Component;
import java.util.function.Function;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import org.andork.util.Format;
import org.andork.util.FormattedText;

public class FormattedTextTableCellRenderer implements TableCellRenderer
{
	private TableCellRenderer					defaultRenderer;
	private Function<Format, TableCellRenderer>	formatRendererFunction;
	private Function<Exception, Color>			exceptionColorFunction;
	
	public FormattedTextTableCellRenderer( TableCellRenderer defaultRenderer , Function<Format, TableCellRenderer> formatRendererFunction , Function<Exception, Color> exceptionColorFunction )
	{
		super( );
		this.defaultRenderer = defaultRenderer;
		this.formatRendererFunction = formatRendererFunction;
		this.exceptionColorFunction = exceptionColorFunction;
	}
	
	@Override
	public Component getTableCellRendererComponent( JTable table , Object value , boolean isSelected , boolean hasFocus , int row , int column )
	{
		if( value instanceof FormattedText )
		{
			FormattedText fv = ( FormattedText ) value;
			
			Component comp;
			
			if( fv.getValue( ) != null )
			{
				TableCellRenderer renderer = formatRendererFunction.apply( fv.getFormat( ) );
				if( renderer != null )
				{
					comp = formatRendererFunction.apply( fv.getFormat( ) ).getTableCellRendererComponent(
							table , fv.getValue( ) , isSelected , hasFocus , row , column );
				}
				else
				{
					comp = defaultRenderer.getTableCellRendererComponent(
							table , fv , isSelected , hasFocus , row , column );
				}
			}
			else
			{
				comp = defaultRenderer.getTableCellRendererComponent( table , fv.getText( ) , isSelected , hasFocus , row , column );
			}
			if( fv.getFormatException( ) != null )
			{
				Color color = exceptionColorFunction.apply( fv.getFormatException( ) );
				if( color != null )
				{
					comp.setBackground( color );
				}
				( ( JComponent ) comp ).setToolTipText( fv.getFormatException( ).getLocalizedMessage( ) );
			}
			else
			{
				( ( JComponent ) comp ).setToolTipText( null );
			}
			return comp;
		}
		else
		{
			return defaultRenderer.getTableCellRendererComponent( table , value , isSelected , hasFocus , row , column );
		}
	}
}
