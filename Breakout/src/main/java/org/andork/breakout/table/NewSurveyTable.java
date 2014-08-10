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

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.RowSorter;
import javax.swing.table.TableModel;

import org.andork.breakout.model.SurveyTableModel;
import org.andork.breakout.table.NewSurveyTableModel.Row;
import org.andork.format.FormatWarning;
import org.andork.format.FormattedText;
import org.andork.q.QObject;
import org.andork.swing.table.AnnotatingJTable;
import org.andork.swing.table.AnnotatingTableRowSorter;
import org.andork.swing.table.FormattedTextTableCellEditor;
import org.andork.swing.table.FormattedTextTableCellRenderer;
import org.andork.swing.table.NiceTableModel.Column;

@SuppressWarnings( "serial" )
public class NewSurveyTable extends AnnotatingJTable
{
	public NewSurveyTable( )
	{
		this( new NewSurveyTableModel( ) );
	}
	
	public NewSurveyTable( NewSurveyTableModel model )
	{
		super( model , new SurveyTableColumnModel( ) );
		getColumnModel( ).setColumnModels( this , model.getColumnModels( ) );
		setRowHeight( 25 );
		setDefaultEditor( FormattedText.class , new FormattedTextTableCellEditor( new JTextField( ) ) );
		setDefaultRenderer( FormattedText.class , new FormattedTextTableCellRenderer(
				getDefaultRenderer( Object.class ) , f -> null , e -> {
					if( e instanceof FormatWarning )
					{
						return Color.YELLOW;
					}
					return Color.RED;
				} ) );
		setAutoCreateColumnsFromModel( true );
		setAutoResizeMode( JTable.AUTO_RESIZE_OFF );
		
		addMouseListener( new MouseAdapter( )
		{
			public void mouseClicked( MouseEvent e )
			{
				if( e.getButton( ) == MouseEvent.BUTTON3 )
				{
					new NewSurveyTableContextMenu( NewSurveyTable.this , e.getPoint( ) )
							.show( NewSurveyTable.this , e.getX( ) , e.getY( ) );
				}
			}
		} );
	}
	
	@Override
	public void createDefaultColumnsFromModel( )
	{
		SurveyTableColumnModel columnModel;
		if( getColumnModel( ) instanceof SurveyTableColumnModel )
		{
			columnModel = ( SurveyTableColumnModel ) getColumnModel( );
		}
		else
		{
			columnModel = new SurveyTableColumnModel( );
		}
		
		if( getModel( ) instanceof NewSurveyTableModel )
		{
			columnModel.setColumnModels( this , ( ( NewSurveyTableModel ) getModel( ) ).getColumnModels( ) );
		}
		
		setColumnModel( columnModel );
	}
	
	public void setRowSorter( RowSorter<? extends TableModel> sorter )
	{
		super.setRowSorter( sorter );
		
		if( sorter instanceof AnnotatingTableRowSorter )
		{
			AnnotatingTableRowSorter<SurveyTableModel> aSorter = ( AnnotatingTableRowSorter<SurveyTableModel> ) sorter;
			
			NewSurveyTableModel model = getModel( );
			
			int modelIndex = 0;
			for( Column<QObject<Row>> column : model.getColumns( ) )
			{
				aSorter.setSortable( modelIndex , column.isSortable( ) );
				modelIndex++ ;
			}
		}
	}
	
	public SurveyTableColumnModel getColumnModel( )
	{
		return ( SurveyTableColumnModel ) super.getColumnModel( );
	}
	
	public NewSurveyTableModel getModel( )
	{
		return ( NewSurveyTableModel ) super.getModel( );
	}
	
	@Override
	protected boolean processKeyBinding( KeyStroke ks , KeyEvent e , int condition , boolean pressed )
	{
		if( e.getKeyCode( ) == KeyEvent.VK_DELETE && e.getID( ) == KeyEvent.KEY_RELEASED )
		{
			getModel( ).removeRows( getSelectedRows( getModelSelectionModel( ) ) );
			
			return true;
		}
		return super.processKeyBinding( ks , e , condition , pressed );
	}
	
	public static int[ ] getSelectedRows( ListSelectionModel selectionModel )
	{
		int iMin = selectionModel.getMinSelectionIndex( );
		int iMax = selectionModel.getMaxSelectionIndex( );
		
		if( ( iMin == -1 ) || ( iMax == -1 ) )
		{
			return new int[ 0 ];
		}
		
		int[ ] rvTmp = new int[ 1 + ( iMax - iMin ) ];
		int n = 0;
		for( int i = iMin ; i <= iMax ; i++ )
		{
			if( selectionModel.isSelectedIndex( i ) )
			{
				rvTmp[ n++ ] = i;
			}
		}
		int[ ] rv = new int[ n ];
		System.arraycopy( rvTmp , 0 , rv , 0 , n );
		return rv;
	}
}
