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

import java.awt.Component;
import java.util.Collections;

import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import org.andork.swing.selector.DefaultSelector;

public class DefaultSelectorTableCellRenderer implements TableCellRenderer
{
	protected final DefaultSelector	selector;
	
	public DefaultSelectorTableCellRenderer( )
	{
		this( new DefaultSelector<>( ) );
	}
	
	public DefaultSelectorTableCellRenderer( DefaultSelector selector )
	{
		super( );
		this.selector = selector;
	}
	
	@Override
	public Component getTableCellRendererComponent( JTable table , Object value , boolean isSelected , boolean hasFocus , int row , int column )
	{
		selector.setEnabled( table.isCellEditable( row , column ) );
		selector.setAvailableValues( value == null ? Collections.emptyList( ) : Collections.singletonList( value ) );
		selector.setSelection( value );
		return selector.getComboBox( );
	}
}
