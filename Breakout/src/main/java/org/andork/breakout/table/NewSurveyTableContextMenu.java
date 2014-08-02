package org.andork.breakout.table;

import java.awt.Font;
import java.awt.Point;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

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
			if( type != null )
			{
				JMenu formatMenu = new JMenu( "Apply Format" );
				for( FormatAndDisplayInfo<?> format : type.availableFormats )
				{
					JMenuItem item = new JMenuItem( new NewSurveyTableBatchSetFormatAction( table , column , format ) );
					item.setFont( item.getFont( ).deriveFont( Font.PLAIN ) );
					formatMenu.add( item );
				}
				add( formatMenu );
			}
		}
	}
}
