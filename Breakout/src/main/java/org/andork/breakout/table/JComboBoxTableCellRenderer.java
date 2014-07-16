package org.andork.breakout.table;

import java.awt.Component;

import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

public class JComboBoxTableCellRenderer implements TableCellRenderer
{
	JComboBox	comboBox;
	
	public JComboBoxTableCellRenderer( )
	{
		this( new JComboBox<>( ) );
	}
	
	public JComboBoxTableCellRenderer( JComboBox comboBox )
	{
		super( );
		this.comboBox = comboBox;
	}
	
	@Override
	public Component getTableCellRendererComponent( JTable table , Object value , boolean isSelected , boolean hasFocus , int row , int column )
	{
		comboBox.removeAllItems( );
		if( value != null )
		{
			comboBox.addItem( value );
		}
		comboBox.setEnabled( table.isCellEditable( row , column ) );
		return comboBox;
	}
}
