package org.andork.breakout.table;

import java.awt.Point;
import java.util.List;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import org.andork.breakout.table.NewSurveyTableModel.SurveyColumnType;
import org.andork.q.QObject;
import org.andork.swing.FormatAndDisplayInfo;

@SuppressWarnings( "serial" )
public class NewSurveyTableContextMenu extends JPopupMenu
{
	public NewSurveyTableContextMenu( NewSurveyTable table , Point p )
	{
		this( table , table.rowAtPoint( p ) , table.columnAtPoint( p ) );
	}
	
	public NewSurveyTableContextMenu( NewSurveyTable table , int row , int column )
	{
		add( new JMenuItem( new NewSurveyTableBatchEditAction( table , row , column ) ) );
		
		if( column >= 0 )
		{
			int modelColumn = table.convertColumnIndexToModel( column );
			QObject<SurveyColumnModel> columnModel = table.getModel( ).getColumnModel( modelColumn );
			SurveyColumnType type = columnModel.get( SurveyColumnModel.type );
			List<FormatAndDisplayInfo<?>> formats = null;
			if( type != null )
			{
				formats = NewSurveyTable.formatMap.get( type.valueClass );
			}
			if( formats != null )
			{
				JMenu formatMenu = new JMenu( "Apply Format" );
				for( FormatAndDisplayInfo<?> format : formats )
				{
					formatMenu.add( new JMenuItem( new NewSurveyTableBatchSetFormatAction( table , column , format ) ) );
				}
				add( formatMenu );
			}
		}
	}
}
