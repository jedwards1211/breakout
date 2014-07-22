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
