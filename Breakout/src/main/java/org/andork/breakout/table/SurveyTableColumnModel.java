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
import java.awt.Component;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.DefaultCellEditor;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import org.andork.collect.CollectionUtils;
import org.andork.format.FormatWarning;
import org.andork.q.QObject;
import org.andork.swing.FormatAndDisplayInfo;
import org.andork.swing.table.FormattedTextTableCellRenderer;
import org.andork.swing.table.FormattedTextWithSelectorTableCellEditor;
import org.andork.swing.table.FormattedTextWithSelectorTableCellRenderer;

@SuppressWarnings( "serial" )
public class SurveyTableColumnModel extends DefaultTableColumnModel
{
	private final Map<Integer, SurveyTableColumn>	columnMap		= new HashMap<>( );
	
	private final List<QObject<SurveyColumnModel>>	columnModels	= new ArrayList<>( );
	
	private final TableCellRenderer					formattedTextCellRenderer;
	
	public SurveyTableColumnModel( )
	{
		formattedTextCellRenderer = new FormattedTextTableCellRenderer(
				new DefaultTableCellRenderer( ) , f -> null , e -> {
					if( e instanceof FormatWarning )
					{
						return Color.YELLOW;
					}
					return Color.RED;
				} );
	}
	
	public void setColumnModels( JTable parentTable , Collection<QObject<SurveyColumnModel>> columnModels )
	{
		this.columnModels.clear( );
		this.columnModels.addAll( columnModels );
		
		List<SurveyTableColumn> newColumns = new ArrayList<>( );
		columnMap.keySet( ).retainAll( CollectionUtils.toHashSet( columnModels.stream( ).map( m -> m.get( SurveyColumnModel.id ) ) ) );
		
		int index = 0;
		for( QObject<SurveyColumnModel> columnModel : columnModels )
		{
			Integer id = columnModel.get( SurveyColumnModel.id );
			SurveyTableColumn column = columnMap.get( id );
			boolean updatePreferredWidth = false;
			if( column == null )
			{
				column = new SurveyTableColumn( index , columnModel );
				columnMap.put( id , column );
				updatePreferredWidth = true;
			}
			column.setHeaderValue( columnModel.get( SurveyColumnModel.name ) );
			
			TableCellEditor editor = column.getCellEditor( );
			TableCellRenderer renderer = column.getCellRenderer( );
			
			SurveyColumnType type = columnModel.get( SurveyColumnModel.type );
			
			List<FormatAndDisplayInfo<?>> formats = type.availableFormats;
			
			if( formats != null && !formats.isEmpty( ) )
			{
				if( !( editor instanceof FormattedTextWithSelectorTableCellEditor ) )
				{
					editor = new FormattedTextWithSelectorTableCellEditor( );
					column.setCellEditor( editor );
				}
				FormattedTextWithSelectorTableCellEditor multiEditor = ( FormattedTextWithSelectorTableCellEditor ) editor;
				multiEditor.setAvailableFormats( formats );
				multiEditor.setDefaultFormat( columnModel.get( SurveyColumnModel.defaultFormat ) );
				
				if( !( renderer instanceof FormattedTextWithSelectorTableCellRenderer ) )
				{
					renderer = new FormattedTextWithSelectorTableCellRenderer( formattedTextCellRenderer );
					column.setCellRenderer( renderer );
				}
				FormattedTextWithSelectorTableCellRenderer multiRenderer = ( FormattedTextWithSelectorTableCellRenderer ) renderer;
				multiRenderer.setAvailableFormats( formats );
				multiRenderer.setDefaultFormat( columnModel.get( SurveyColumnModel.defaultFormat ) );
				
				if( updatePreferredWidth )
				{
					Component rendComp = multiRenderer.getTableCellRendererComponent( parentTable , columnModel.get( SurveyColumnModel.type )
							.prototypeValue , false , false , 0 , 0 );
					column.setPreferredWidth( rendComp.getPreferredSize( ).width );
				}
			}
			else
			{
				if( editor instanceof FormattedTextWithSelectorTableCellEditor )
				{
					editor = new DefaultCellEditor( new JTextField( ) );
					column.setCellEditor( editor );
				}
			}
			
			Boolean visible = columnModel.get( SurveyColumnModel.visible );
			
			if( Boolean.TRUE.equals( visible ) )
			{
				newColumns.add( column );
			}
			index++ ;
		}
		
		while( getColumnCount( ) > 0 )
		{
			removeColumn( getColumn( 0 ) );
		}
		
		for( SurveyTableColumn newColumn : newColumns )
		{
			addColumn( newColumn );
		}
	}
	
	public List<QObject<SurveyColumnModel>> getColumnModels( )
	{
		return Collections.unmodifiableList( columnModels );
	}
	
	@Override
	public void moveColumn( int columnIndex , int newIndex )
	{
		if( columnIndex != newIndex )
		{
			SurveyTableColumn fromColumn = ( SurveyTableColumn ) getColumn( columnIndex );
			SurveyTableColumn toColumn = ( SurveyTableColumn ) getColumn( newIndex );
			int fromIndex = columnModels.indexOf( fromColumn.model );
			int toIndex = columnModels.indexOf( toColumn.model );
			if( fromIndex >= 0 && toIndex >= 0 )
			{
				columnModels.add( toIndex , columnModels.remove( fromIndex ) );
			}
		}
		super.moveColumn( columnIndex , newIndex );
	}
	
	private static class SurveyTableColumn extends TableColumn
	{
		private final QObject<SurveyColumnModel>	model;
		
		public SurveyTableColumn( int modelIndex , QObject<SurveyColumnModel> model )
		{
			super( modelIndex );
			this.model = model;
		}
	}
}
