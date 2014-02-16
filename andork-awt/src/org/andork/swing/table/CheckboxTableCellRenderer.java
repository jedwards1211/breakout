package org.andork.swing.table;

import java.awt.Component;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

@SuppressWarnings( "serial" )
public class CheckboxTableCellRenderer extends DefaultTableCellRenderer implements TableCellRenderer
{
	JCheckBox	checkBox;
	
	public CheckboxTableCellRenderer( JCheckBox checkBox )
	{
		super( );
		this.checkBox = checkBox;
	}
	
	@Override
	public Component getTableCellRendererComponent( JTable table , Object value , boolean isSelected , boolean hasFocus , int row , int column )
	{
		JComponent superRenderer = ( JComponent ) super.getTableCellRendererComponent( table , value , isSelected , hasFocus , row , column );
		checkBox.setSelected( value == Boolean.TRUE );
		checkBox.setEnabled( table.isCellEditable( row , column ) );
		checkBox.setBackground( superRenderer.getBackground( ) );
		checkBox.setForeground( superRenderer.getForeground( ) );
		checkBox.setBorder( superRenderer.getBorder( ) );
		return checkBox;
	}
	
}
