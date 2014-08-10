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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.IntUnaryOperator;

import org.andork.format.FormattedText;
import org.andork.q.QLinkedHashMap;
import org.andork.q.QObject;
import org.andork.q.QSpec;
import org.andork.swing.table.AnnotatingTableRowSorter.AbstractTableModelCopier;
import org.andork.swing.table.NiceTableModel;
import org.andork.util.StringUtils;

@SuppressWarnings( "serial" )
public class NewSurveyTableModel extends NiceTableModel<QObject<NewSurveyTableModel.Row>>
{
	public static class Row extends QSpec<Row>
	{
		public static final Attribute<Integer>							id					= newAttribute( Integer.class , "id" );
		public static final Attribute<String>							from				= newAttribute( String.class , "From" );
		public static final Attribute<String>							to					= newAttribute( String.class , "To" );
		public static final Attribute<FormattedText>					shotMeasurement		= newAttribute( FormattedText.class , "Shot Measurement" );
		public static final Attribute<FormattedText>					fromCrossSection	= newAttribute( FormattedText.class , "Cross Section at From" );
		public static final Attribute<FormattedText>					toCrossSection		= newAttribute( FormattedText.class , "Cross Section at To" );
		public static final Attribute<QLinkedHashMap<Integer, Object>>	customAttrs			= newAttribute( QLinkedHashMap.class , "Custom" );
		
		public static final Row											spec				= new Row( );
		
		private Row( )
		{
		}
	}
	
	private final SurveyColumn								fromColumn;
	private final SurveyColumn								toColumn;
	private final SurveyColumn								shotMeasurementColumn;
	private final SurveyColumn								fromCrossSectionColumn;
	private final SurveyColumn								toCrossSectionColumn;
	
	private boolean											autoResize		= true;
	
	private final Map<Integer, SurveyColumn>				fixedColumnMap	= new LinkedHashMap<>( );
	private final Map<Integer, SurveyColumn>				columnMap		= new LinkedHashMap<>( );
	private final List<QObject<SurveyColumnModel>>			columnModelList	= new ArrayList<>( );
	private final Map<Integer, QObject<SurveyColumnModel>>	columnModelMap	= new LinkedHashMap<>( );
	
	private IdManager										rowIdManager	= new IdManager( );
	private IdManager										columnIdManager	= new IdManager( );
	
	public static final class SurveyColumn implements Column<QObject<Row>>
	{
		public final Column<QObject<Row>>	wrapped;
		public final SurveyColumnType		type;
		
		private SurveyColumn( SurveyColumnType type , Column<QObject<Row>> wrapped )
		{
			super( );
			this.wrapped = type.createColumn( wrapped );
			this.type = type;
		}
		
		public SurveyColumn( int id , String name , SurveyColumnType type )
		{
			super( );
			this.wrapped = type.createCustomColumn( id );
			this.type = type;
		}
		
		@Override
		public String getColumnName( )
		{
			return wrapped.getColumnName( );
		}
		
		@Override
		public Class<?> getColumnClass( )
		{
			return wrapped.getColumnClass( );
		}
		
		@Override
		public boolean isCellEditable( QObject<Row> row )
		{
			return wrapped.isCellEditable( row );
		}
		
		@Override
		public Object getValueAt( QObject<Row> row )
		{
			return wrapped.getValueAt( row );
		}
		
		@Override
		public void setValueAt( Object aValue , QObject<Row> row )
		{
			wrapped.setValueAt( aValue , row );
		}
		
		@Override
		public boolean isSortable( )
		{
			return wrapped.isSortable( );
		}
	}
	
	public NewSurveyTableModel( )
	{
		fromColumn = new SurveyColumn( SurveyColumnType.STRING , QObjectColumn.newInstance( Row.spec , Row.from ) );
		toColumn = new SurveyColumn( SurveyColumnType.STRING , QObjectColumn.newInstance( Row.spec , Row.to ) );
		shotMeasurementColumn = new SurveyColumn( SurveyColumnType.SHOT_MEASUREMENT , QObjectColumn.newInstance( Row.spec , Row.shotMeasurement ) );
		fromCrossSectionColumn = new SurveyColumn( SurveyColumnType.CROSS_SECTION , QObjectColumn.newInstance( Row.spec , Row.fromCrossSection ) );
		toCrossSectionColumn = new SurveyColumn( SurveyColumnType.CROSS_SECTION , QObjectColumn.newInstance( Row.spec , Row.toCrossSection ) );
		
		setColumns( Arrays.asList(
				fromColumn ,
				toColumn ,
				shotMeasurementColumn ,
				fromCrossSectionColumn ,
				toCrossSectionColumn
				) );
		
		for( Column<QObject<Row>> column : getColumns( ) )
		{
			SurveyColumn col = ( SurveyColumn ) column;
			int id = columnIdManager.nextId( );
			fixedColumnMap.put( id , col );
			QObject<SurveyColumnModel> colModel = SurveyColumnModel.instance.newObject( );
			colModel.set( SurveyColumnModel.id , id );
			colModel.set( SurveyColumnModel.name , col.getColumnName( ) );
			colModel.set( SurveyColumnModel.visible , true );
			colModel.set( SurveyColumnModel.fixed , true );
			colModel.set( SurveyColumnModel.type , col.type );
			colModel.set( SurveyColumnModel.defaultFormat , col.type.defaultFormat );
			
			columnModelList.add( colModel );
			columnModelMap.put( colModel.get( SurveyColumnModel.id ) , colModel );
		}
		
		addRow( Row.spec.newObject( ) );
	}
	
	public List<Column<QObject<Row>>> getColumns( )
	{
		return super.getColumns( );
	}
	
	@Override
	public void setValueAt( Object aValue , int rowIndex , int columnIndex )
	{
		super.setValueAt( aValue , rowIndex , columnIndex );
		
		if( ( StringUtils.isNullOrEmpty( aValue ) && rowIndex == getRowCount( ) - 2 ) || ( !StringUtils.isNullOrEmpty( aValue ) && rowIndex == getRowCount( ) - 1 ) )
		{
			updateEndRows( );
		}
	}
	
	public void blockSetValues( List<Object[ ]> srcRows , IntUnaryOperator convertRowToDest , IntUnaryOperator convertColumnToDest )
	{
		if( srcRows.isEmpty( ) )
		{
			return;
		}
		int minDestRowIndex = Integer.MAX_VALUE;
		int maxDestRowIndex = Integer.MIN_VALUE;
		
		for( int srcRowIndex = 0 ; srcRowIndex < srcRows.size( ) ; srcRowIndex++ )
		{
			int destRowIndex = convertRowToDest.applyAsInt( srcRowIndex );
			if( destRowIndex >= 0 )
			{
				minDestRowIndex = Math.min( minDestRowIndex , destRowIndex );
				maxDestRowIndex = Math.max( maxDestRowIndex , destRowIndex );
			}
		}
		
		List<QObject<Row>> rowsToAdd = new LinkedList<>( );
		for( int i = getRowCount( ) ; i <= maxDestRowIndex ; i++ )
		{
			rowsToAdd.add( Row.spec.newObject( ) );
		}
		addRows( rowsToAdd );
		
		int srcRowIndex = 0;
		
		for( Object[ ] srcRow : srcRows )
		{
			int destRowIndex = convertRowToDest.applyAsInt( srcRowIndex++ );
			
			if( destRowIndex < 0 )
			{
				continue;
			}
			
			QObject<Row> destRow = getRow( destRowIndex );
			
			for( int srcColumnIndex = 0 ; srcColumnIndex < srcRow.length ; srcColumnIndex++ )
			{
				int destColumnIndex = convertColumnToDest.applyAsInt( srcColumnIndex );
				
				if( destColumnIndex < 0 || destColumnIndex >= getColumnCount( ) )
				{
					continue;
				}
				
				Column<QObject<Row>> destColumn = getColumn( destColumnIndex );
				destColumn.setValueAt( srcRow[ srcColumnIndex ] , destRow );
			}
		}
		fireTableRowsUpdated( minDestRowIndex , maxDestRowIndex );
		
		updateEndRows( );
	}
	
	protected void updateEndRows( )
	{
		if( !autoResize )
		{
			return;
		}
		
		int lastRowWithData;
		rowLoop: for( lastRowWithData = getRowCount( ) - 1 ; lastRowWithData >= 0 ; lastRowWithData-- )
		{
			for( int column = 0 ; column < getColumnCount( ) ; column++ )
			{
				Object value = getValueAt( lastRowWithData , column );
				if( value != null && !"".equals( value ) )
				{
					break rowLoop;
				}
			}
		}
		
		if( lastRowWithData == getRowCount( ) - 1 )
		{
			addRow( Row.spec.newObject( ) );
		}
		else if( lastRowWithData + 2 < getRowCount( ) )
		{
			removeRows( lastRowWithData + 2 , getRowCount( ) - 1 );
		}
	}
	
	public void removeRows( int ... rows )
	{
		int[ ] removedRowIds = new int[ rows.length ];
		for( int i = 0 ; i < rows.length ; i++ )
		{
			removedRowIds[ i ] = getRow( rows[ i ] ).get( Row.id );
		}
		if( rows.length <= 10 )
		{
			for( int i = rows.length - 1 ; i >= 0 ; i-- )
			{
				removeRow( rows[ i ] );
			}
		}
		else
		{
			super.removeRows( rows );
		}
		
		updateEndRows( );
	}
	
	@Override
	protected void addRow( QObject<Row> row )
	{
		addRow( getRowCount( ) , row );
	}
	
	@Override
	protected void addRow( int index , QObject<Row> row )
	{
		row.set( Row.id , rowIdManager.nextId( ) );
		super.addRow( index , row );
	}
	
	@Override
	protected void addRows( Collection<? extends QObject<Row>> rows )
	{
		addRows( getRowCount( ) , rows );
	}
	
	@Override
	protected void addRows( int index , Collection<? extends QObject<Row>> rows )
	{
		for( QObject<Row> row : rows )
		{
			row.set( Row.id , rowIdManager.nextId( ) );
		}
		super.addRows( index , rows );
	}
	
	@Override
	protected void removeRow( int index )
	{
		rowIdManager.release( getRow( index ).get( Row.id ) );
		super.removeRow( index );
	}
	
	@Override
	protected void removeRows( int firstIndex , int lastIndex )
	{
		for( int index = firstIndex ; index <= lastIndex ; index++ )
		{
			rowIdManager.release( getRow( index ).get( Row.id ) );
		}
		super.removeRows( firstIndex , lastIndex );
	}
	
	@Override
	protected void clearRows( )
	{
		rowIdManager.reset( Collections.emptySet( ) );
		super.clearRows( );
	}
	
	@Override
	protected void setRows( Collection<? extends QObject<Row>> rows )
	{
		rowIdManager.reset( rows.stream( ).map( r -> r.get( Row.id ) ) );
		for( QObject<Row> row : rows )
		{
			if( row.get( Row.id ) == null )
			{
				row.set( Row.id , rowIdManager.nextId( ) );
			}
		}
		super.setRows( rows );
	}
	
	@Override
	protected void setRow( int index , QObject<Row> row )
	{
		row.set( Row.id , getRow( index ).get( Row.id ) );
		super.setRow( index , row );
	}
	
	@Override
	protected void setRows( int index , Collection<? extends QObject<Row>> rows )
	{
		int k = 0;
		for( QObject<Row> row : rows )
		{
			row.set( Row.id , getRow( index + k ).get( Row.id ) );
			k++ ;
		}
		super.setRows( index , rows );
	}
	
	/**
	 * Returns the internal row object at the given index. If you modify it no events will be fired!
	 */
	public QObject<Row> getRow( int rowIndex )
	{
		return super.getRow( rowIndex );
	}
	
	public SurveyColumn getColumnWithId( Integer id )
	{
		return columnMap.get( id );
	}
	
	public QObject<SurveyColumnModel> getColumnModelWithId( Integer id )
	{
		return columnModelMap.get( id );
	}
	
	public List<QObject<SurveyColumnModel>> getColumnModels( )
	{
		return new ArrayList<>( columnModelList );
	}
	
	public void setColumnModels( Collection<? extends QObject<SurveyColumnModel>> newColumnModels )
	{
		List<SurveyColumn> newColumns = new ArrayList<>( );
		
		columnMap.clear( );
		columnModelList.clear( );
		columnModelMap.clear( );
		
		columnIdManager.reset( newColumnModels.stream( ).map( c -> c.get( SurveyColumnModel.id ) ) );
		
		for( QObject<SurveyColumnModel> colModel : newColumnModels )
		{
			if( colModel.get( SurveyColumnModel.id ) == null )
			{
				colModel.set( SurveyColumnModel.id , columnIdManager.nextId( ) );
			}
			
			columnModelList.add( colModel );
			columnModelMap.put( colModel.get( SurveyColumnModel.id ) , colModel );
			SurveyColumn column = fixedColumnMap.get( colModel.get( SurveyColumnModel.id ) );
			if( column == null )
			{
				column = new SurveyColumn(
						colModel.get( SurveyColumnModel.id ) ,
						colModel.get( SurveyColumnModel.name ) ,
						colModel.get( SurveyColumnModel.type ) );
			}
			newColumns.add( column );
			columnMap.put( colModel.get( SurveyColumnModel.id ) , column );
		}
		
		setColumns( newColumns );
	}
	
	public static class NewSurveyTableModelCopier extends AbstractTableModelCopier<NewSurveyTableModel>
	{
		@Override
		public NewSurveyTableModel createEmptyCopy( NewSurveyTableModel model )
		{
			NewSurveyTableModel copy = new NewSurveyTableModel( );
			for( int i = 1 ; i < model.getRowCount( ) ; i++ )
			{
				copy.addRow( Row.spec.newObject( ) );
			}
			copy.setColumnModels( model.getColumnModels( ) );
			return copy;
		}
		
		public NewSurveyTableModel copy( NewSurveyTableModel src )
		{
			NewSurveyTableModel dest = createEmptyCopy( src );
			for( int row = 0 ; row < src.getRowCount( ) - 1 ; row++ )
			{
				copyRow( src , row , dest );
			}
			return dest;
		}
		
		@Override
		public void copyRow( NewSurveyTableModel src , int row , NewSurveyTableModel dest )
		{
			if( row == src.getRowCount( ) - 1 )
			{
				return;
			}
			super.copyRow( src , row , dest );
		}
	}
	
	public QObject<SurveyColumnModel> getColumnModel( int columnIndex )
	{
		return columnModelList.get( columnIndex );
	}
}
