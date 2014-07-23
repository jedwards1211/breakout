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

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

public class JCheckBoxBooleanCellRenderer implements TableCellRenderer
{
	JCheckBox					checkBox;
	
	DefaultTableCellRenderer	hintRenderer	= new DefaultTableCellRenderer( );
	
	public JCheckBoxBooleanCellRenderer( )
	{
		this( new JCheckBox( ) );
	}
	
	public JCheckBoxBooleanCellRenderer( JCheckBox checkBox )
	{
		super( );
		this.checkBox = checkBox;
	}
	
	@Override
	public Component getTableCellRendererComponent( JTable table , Object value , boolean isSelected , boolean hasFocus , int row , int column )
	{
		Component hintComp = hintRenderer.getTableCellRendererComponent( table , value , isSelected , hasFocus , row , column );
		checkBox.setEnabled( table.isCellEditable( row , column ) );
		checkBox.setSelected( Boolean.TRUE.equals( value ) );
		checkBox.setBackground( hintComp.getBackground( ) );
		checkBox.setBorder( ( ( JComponent ) hintComp ).getBorder( ) );
		checkBox.setFont( hintComp.getFont( ) );
		checkBox.setHorizontalAlignment( JCheckBox.CENTER );
		return checkBox;
	}
	
}
