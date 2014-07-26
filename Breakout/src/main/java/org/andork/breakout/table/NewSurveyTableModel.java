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
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.IntUnaryOperator;

import org.andork.collect.CollectionUtils;
import org.andork.format.FormattedText;
import org.andork.q.QLinkedHashMap;
import org.andork.q.QObject;
import org.andork.q.QSpec;
import org.andork.swing.FormatAndDisplayInfo;
import org.andork.swing.table.AnnotatingTableRowSorter.AbstractTableModelCopier;
import org.andork.swing.table.NiceTableModel;
import org.andork.util.StringUtils;

@SuppressWarnings( "serial" )
public class NewSurveyTableModel extends NiceTableModel<QObject<NewSurveyTableModel.Row>>
{
	public static class Row extends QSpec<Row>
	{
		public static final Attribute<String>							from				= newAttribute( String.class , "From" );
		public static final Attribute<String>							to					= newAttribute( String.class , "To" );
		public static final Attribute<FormattedText>					shotMeasurement		= newAttribute( FormattedText.class , "Shot" );
		public static final Attribute<FormattedText>					fromCrossSection	= newAttribute( FormattedText.class , "Cross Section at From" );
		public static final Attribute<FormattedText>					toCrossSection		= newAttribute( FormattedText.class , "Cross Section at To" );
		public static final Attribute<QLinkedHashMap<String, Object>>	customAttrs			= newAttribute( QLinkedHashMap.class , "custom" );
		
		public static final Row											spec				= new Row( );
		
		private Row( )
		{
		}
	}
	
	public static enum SurveyColumnType
	{
		STRING( "Text" , String.class , StringUtils.multiply( "m" , 20 ) )
		{
			Column<QObject<Row>> createColumn( String name )
			{
				return MapColumn.newInstance(
						QObjectColumn.newInstance( Row.spec , Row.customAttrs ) ,
						name ,
						String.class ,
						( ) -> QLinkedHashMap.newInstance( ) );
			}
		} ,
		INTEGER( "Integer" , Integer.class , Integer.MAX_VALUE ) ,
		REAL_NUMBER( "Real Number" , Double.class , Double.MAX_VALUE ) ,
		DATE( "Date" , Date.class , new FormattedText( "2011/11/30" , NewSurveyTable.getAvailableFormats( Date.class )
				.stream( ).filter( f -> f.name( ).equals( "yyyy/MM/dd" ) ).findAny( ).get( ) ) ) ,
		SHOT_MEASUREMENT( "Shot Measurement" , ShotMeasurement.class ,
				new FormattedText( "100.25 359.25/359.25 -89.25/-89.25" ,
						NewSurveyTable.getAvailableFormats( ShotMeasurement.class ).stream( )
								.filter( f -> f.format( ) instanceof DistAzmIncMeasurementFormat ).findAny( ).get( ) ) ) ,
		CROSS_SECTION( "Cross Section" , CrossSection.class ,
				new FormattedText( "100.25 100.25 100.25 100.25 100.25 100.25" , NewSurveyTable.formatMap.get( CrossSection.class )
						.stream( ).filter( f -> f.format( ) instanceof LlrrudCrossSectionFormat ).findAny( ).get( ) ) );
		
		Column<QObject<Row>> createColumn( String name )
		{
			return createColumn( MapColumn.newInstance(
					QObjectColumn.newInstance( Row.spec , Row.customAttrs ) ,
					name ,
					FormattedText.class ,
					( ) -> QLinkedHashMap.newInstance( ) ) );
		}
		
		Column<QObject<Row>> createColumn( Column<QObject<Row>> wrapped )
		{
			return FormattedTextColumn.newInstance(
					wrapped ,
					valueClass ,
					( ) -> new FormattedText( NewSurveyTable.getDefaultFormat( valueClass ) ) );
		}
		
		public final String										displayName;
		public final Class<?>									valueClass;
		public final Object										prototypeValue;
		
		private static final Map<Class<?>, SurveyColumnType>	valueClassMap;
		
		static
		{
			valueClassMap = Collections.unmodifiableMap( CollectionUtils.keyify( Arrays.asList( values( ) ).stream( ) ,
					type -> type.valueClass ) );
		}
		
		public static SurveyColumnType fromValueClass( Class<?> valueClass )
		{
			return valueClassMap.get( valueClass );
		}
		
		private SurveyColumnType( String displayName , Class<?> valueClass , Object prototypeValue )
		{
			this.displayName = displayName;
			this.valueClass = valueClass;
			this.prototypeValue = prototypeValue;
		}
		
		public String toString( )
		{
			return displayName;
		}
	}
	
	public final SurveyColumn						fromColumn;
	public final SurveyColumn						toColumn;
	public final SurveyColumn						shotMeasurementColumn;
	public final SurveyColumn						fromCrossSectionColumn;
	public final SurveyColumn						toCrossSectionColumn;
	
	private boolean									autoResize			= true;
	
	private final Map<String, SurveyColumn>			fixedColumns		= new LinkedHashMap<>( );
	private final List<QObject<SurveyColumnModel>>	surveyColumnModels	= new ArrayList<>( );
	
	public static final class SurveyColumn implements Column<QObject<Row>>
	{
		public final Column<QObject<Row>>	wrapped;
		public final SurveyColumnType		type;
		FormatAndDisplayInfo<?>				defaultFormat;
		
		private SurveyColumn( SurveyColumnType type , Column<QObject<Row>> wrapped )
		{
			super( );
			this.wrapped = type.createColumn( wrapped );
			this.type = type;
			setDefaultFormat( NewSurveyTable.getDefaultFormat( type.valueClass ) );
		}
		
		public SurveyColumn( String name , SurveyColumnType type , FormatAndDisplayInfo<?> defaultFormat )
		{
			super( );
			this.wrapped = type.createColumn( name );
			this.type = type;
			setDefaultFormat( defaultFormat );
		}
		
		private void setDefaultFormat( FormatAndDisplayInfo<?> defaultFormat )
		{
			this.defaultFormat = defaultFormat;
			if( wrapped instanceof FormattedTextColumn )
			{
				( ( FormattedTextColumn ) wrapped ).setFormattedTextSupplier( ( ) -> new FormattedText( defaultFormat ) );
			}
		}
		
		public FormatAndDisplayInfo<?> getDefaultFormat( )
		{
			return defaultFormat;
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
			fixedColumns.put( col.getColumnName( ) , col );
			QObject<SurveyColumnModel> colModel = SurveyColumnModel.instance.newObject( );
			colModel.set( SurveyColumnModel.name , col.getColumnName( ) );
			colModel.set( SurveyColumnModel.visible , true );
			colModel.set( SurveyColumnModel.fixed , true );
			colModel.set( SurveyColumnModel.type , col.type );
			colModel.set( SurveyColumnModel.defaultFormat , col.defaultFormat );
			
			surveyColumnModels.add( colModel );
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
			int destRowIndex = convertRowToDest.applyAsInt( srcRowIndex );
			
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
			
			srcRowIndex++ ;
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
	
	public void removeRows( int[ ] rows )
	{
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
	
	public List<QObject<SurveyColumnModel>> getColumnModels( )
	{
		return new ArrayList<>( surveyColumnModels );
	}
	
	public void setColumnModels( Collection<? extends QObject<SurveyColumnModel>> newColumnModels )
	{
		List<SurveyColumn> newColumns = new ArrayList<>( );
		
		surveyColumnModels.clear( );
		
		for( QObject<SurveyColumnModel> colModel : newColumnModels )
		{
			surveyColumnModels.add( colModel );
			//			if( !Boolean.TRUE.equals( colModel.get( SurveyColumnModel.visible ) ) )
			//			{
			//				continue;
			//			}
			SurveyColumn column = fixedColumns.get( colModel.get( SurveyColumnModel.name ) );
			if( column == null )
			{
				column = new SurveyColumn(
						colModel.get( SurveyColumnModel.name ) ,
						colModel.get( SurveyColumnModel.type ) ,
						colModel.get( SurveyColumnModel.defaultFormat ) );
			}
			else
			{
				column.setDefaultFormat( colModel.get( SurveyColumnModel.defaultFormat ) );
			}
			newColumns.add( column );
		}
		
		setColumns( newColumns );
	}
	
	public static class NewSurveyTableModelCopier extends AbstractTableModelCopier<NewSurveyTableModel>
	{
		@Override
		public NewSurveyTableModel createEmptyCopy( NewSurveyTableModel model )
		{
			NewSurveyTableModel copy = new NewSurveyTableModel( );
			for( int i = 0 ; i < model.getRowCount( ) ; i++ )
			{
				copy.addRow( Row.spec.newObject( ) );
			}
			copy.setColumnModels( model.getColumnModels( ) );
			return copy;
		}
		
		public NewSurveyTableModel copy( NewSurveyTableModel src )
		{
			NewSurveyTableModel dest = createEmptyCopy( src );
			for( int row = 0 ; row < src.getRowCount( ) ; row++ )
			{
				copyRow( src , row , dest );
			}
			return dest;
		}
	}
	
	public QObject<SurveyColumnModel> getColumnModel( int columnIndex )
	{
		return surveyColumnModels.get( columnIndex );
	}
}
