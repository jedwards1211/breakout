package org.andork.breakout.table;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.DefaultCellEditor;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumn;

import org.andork.collect.CollectionUtils;
import org.andork.q.QObject;
import org.andork.swing.table.FormatAndDisplayInfo;
import org.andork.swing.table.MultiFormattedTextTableCellEditor;

@SuppressWarnings( "serial" )
public class SurveyTableColumnModel extends DefaultTableColumnModel
{
	private final Map<String, TableColumn>	columnMap	= new HashMap<>( );
	
	public void setColumnModels( Collection<QObject<SurveyColumnModel>> columnModels )
	{
		List<TableColumn> newColumns = new ArrayList<>( );
		columnMap.keySet( ).retainAll( CollectionUtils.toHashSet( columnModels.stream( ) ) );
		
		int index = 0;
		for( QObject<SurveyColumnModel> columnModel : columnModels )
		{
			String name = columnModel.get( SurveyColumnModel.name );
			TableColumn column = columnMap.get( name );
			if( column == null )
			{
				column = new TableColumn( index );
				column.setHeaderValue( name );
				columnMap.put( name , column );
			}
			
			TableCellEditor editor = column.getCellEditor( );
			
			Class<?> valueClass = columnModel.get( SurveyColumnModel.type ).valueClass;
			
			List<FormatAndDisplayInfo<?>> formats = valueClass == null ? null : NewSurveyTable.formatMap.get( valueClass );
			
			if( formats != null )
			{
				if( !( editor instanceof MultiFormattedTextTableCellEditor ) )
				{
					editor = new MultiFormattedTextTableCellEditor( );
					column.setCellEditor( editor );
				}
				MultiFormattedTextTableCellEditor multiEditor = ( MultiFormattedTextTableCellEditor ) editor;
				multiEditor.setAvailableFormats( formats );
				multiEditor.setDefaultFormat( columnModel.get( SurveyColumnModel.defaultFormat ) );
			}
			else
			{
				if( editor instanceof MultiFormattedTextTableCellEditor )
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
		
		for( TableColumn newColumn : newColumns )
		{
			addColumn( newColumn );
		}
	}
}
