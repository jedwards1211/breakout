package org.andork.swing.table;

import java.awt.Component;

import javax.swing.DefaultCellEditor;
import javax.swing.JTable;
import javax.swing.JTextField;

import org.andork.util.FormattedText;

public class FormattedTextTableCellEditor extends DefaultCellEditor
{
	public FormattedTextTableCellEditor( JTextField textField )
	{
		super( textField );
	}
	
	@Override
	public Component getTableCellEditorComponent( JTable table , Object value , boolean isSelected , int row , int column )
	{
		if( value instanceof FormattedText )
		{
			value = ( ( FormattedText ) value ).getText( );
		}
		return super.getTableCellEditorComponent( table , value , isSelected , row , column );
	}
}
