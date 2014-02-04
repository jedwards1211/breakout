package org.andork.awt;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

public class SortingTableTest
{
	public static void main( String[ ] args )
	{
		TableModel model = new DefaultTableModel( 5 , 5 );
		JTable table = new JTable( model );
		table.setRowSorter( new TableRowSorter<TableModel>( ) );
		
		QuickTestFrame.frame( new JScrollPane( table ) ).setVisible( true );
	}
}
