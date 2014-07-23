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
import java.util.function.Consumer;

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.table.TableModel;

public class UniqueTableCellEditor extends DefaultCellEditor
{
	int					modelColumn;
	int					modelRow;
	TableModel			tableModel;
	JTable				table;
	
	Consumer<Object>	duplicateHandler	= obj ->
													JOptionPane.showMessageDialog( SwingUtilities.getWindowAncestor( table ) ,
															"Another row already has the value " + obj );
	
	public UniqueTableCellEditor( JComboBox comboBox )
	{
		super( comboBox );
	}
	
	public UniqueTableCellEditor( JTextField textField )
	{
		super( textField );
	}
	
	public Consumer<Object> getDuplicateHandler( )
	{
		return duplicateHandler;
	}
	
	public void setDuplicateHandler( Consumer<Object> duplicateHandler )
	{
		this.duplicateHandler = duplicateHandler;
	}
	
	@Override
	public boolean stopCellEditing( )
	{
		Object value = getCellEditorValue( );
		
		for( int row = 0 ; row < tableModel.getRowCount( ) ; row++ )
		{
			if( row == modelRow )
			{
				continue;
			}
			if( value != null && value.equals( tableModel.getValueAt( row , modelColumn ) ) )
			{
				duplicateHandler.accept( value );
				return false;
			}
		}
		
		return super.stopCellEditing( );
	}
	
	@Override
	public Component getTreeCellEditorComponent( JTree tree , Object value , boolean isSelected , boolean expanded , boolean leaf , int row )
	{
		throw new UnsupportedOperationException( );
	}
	
	@Override
	public Component getTableCellEditorComponent( JTable table , Object value , boolean isSelected , int row , int column )
	{
		this.table = table;
		this.tableModel = table.getModel( );
		this.modelRow = table.convertRowIndexToModel( row );
		this.modelColumn = table.convertColumnIndexToModel( column );
		return super.getTableCellEditorComponent( table , value , isSelected , row , column );
	}
}
