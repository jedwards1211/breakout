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

import java.awt.Component;

import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

public class TableCellRendererFromEditor implements TableCellRenderer
{
	@Override
	public Component getTableCellRendererComponent( JTable table , Object value , boolean isSelected , boolean hasFocus , int row , int column )
	{
		TableCellEditor editor = table.getCellEditor( row , column );
		Component rendComp = table.getDefaultRenderer( String.class ).getTableCellRendererComponent(
				table , value , isSelected , hasFocus , row , column );
		if( editor != null )
		{
			Component editorComp = editor.getTableCellEditorComponent( table , value , isSelected , row , column );
			editorComp.setBackground( rendComp.getBackground( ) );
			editorComp.setForeground( rendComp.getForeground( ) );
			
			if( editorComp instanceof JComponent && rendComp instanceof JComponent )
			{
				( ( JComponent ) editorComp ).setBorder( ( ( JComponent ) rendComp ).getBorder( ) );
			}
			
			return editorComp;
		}
		return rendComp;
	}
}
