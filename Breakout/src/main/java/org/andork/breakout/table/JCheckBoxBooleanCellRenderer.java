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
		checkBox.setSelected( Boolean.TRUE.equals( value ) );
		checkBox.setBackground( hintComp.getBackground( ) );
		checkBox.setBorder( ( ( JComponent ) hintComp ).getBorder( ) );
		checkBox.setFont( hintComp.getFont( ) );
		checkBox.setHorizontalAlignment( JCheckBox.CENTER );
		return checkBox;
	}
	
}
