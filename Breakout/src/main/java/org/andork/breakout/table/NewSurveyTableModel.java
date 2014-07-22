package org.andork.breakout.table;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.andork.collect.CollectionUtils;
import org.andork.q.QLinkedHashMap;
import org.andork.q.QObject;
import org.andork.q.QSpec;
import org.andork.swing.table.AnnotatingTableRowSorter.AbstractTableModelCopier;
import org.andork.swing.table.FormatAndDisplayInfo;
import org.andork.swing.table.NiceTableModel;
import org.andork.util.Format;
import org.andork.util.FormattedText;
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
		STRING( "Text" , String.class )
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
		INTEGER( "Integer" , Integer.class ) ,
		REAL_NUMBER( "Real Number" , Double.class ) ,
		DATE( "Date" , Date.class ) ,
		SHOT_MEASUREMENT( "Shot Measurement" , ShotMeasurement.class ) ,
		CROSS_SECTION( "Cross Section" , CrossSection.class );
		
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
		
		private SurveyColumnType( String displayName , Class<?> valueClass )
		{
			this.displayName = displayName;
			this.valueClass = valueClass;
		}
		
		public String toString( )
		{
			return displayName;
		}
	}
	
	public final SurveyColumn								fromColumn;
	public final SurveyColumn								toColumn;
	public final SurveyColumn								shotMeasurementColumn;
	public final SurveyColumn								fromCrossSectionColumn;
	public final SurveyColumn								toCrossSectionColumn;
	
	private final Map<String, SurveyColumn>					fixedColumns		= new LinkedHashMap<>( );
	private final Map<String, QObject<SurveyColumnModel>>	surveyColumnModels	= new LinkedHashMap<>( );
	
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
			
			surveyColumnModels.put( column.getColumnName( ) , colModel );
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
	
	protected void blockSetValues( QObject<Row> srcRow , int rowIndex , int firstColumn , int lastColumn , boolean fireUpdate )
	{
		QObject<Row> row = getRow( rowIndex );
		
		for( int columnIndex = firstColumn ; columnIndex <= lastColumn ; columnIndex++ )
		{
			Column<QObject<Row>> column = getColumn( columnIndex );
			column.setValueAt( column.getValueAt( srcRow ) , row );
		}
		if( fireUpdate )
		{
			fireTableRowsUpdated( rowIndex , rowIndex );
		}
	}
	
	public void blockSetValues( List<QObject<Row>> srcRows , int rowIndex , int firstColumn , int lastColumn )
	{
		if( srcRows.isEmpty( ) || firstColumn > lastColumn )
		{
			return;
		}
		
		List<QObject<Row>> rowsToAdd = new ArrayList<QObject<Row>>( );
		for( int k = getRowCount( ) ; k < rowIndex + srcRows.size( ) ; k++ )
		{
			rowsToAdd.add( Row.spec.newObject( ) );
		}
		addRows( rowsToAdd );
		
		for( QObject<Row> srcRow : srcRows )
		{
			blockSetValues( srcRow , rowIndex++ , firstColumn , lastColumn , false );
		}
		fireTableRowsUpdated( rowIndex - srcRows.size( ) , rowIndex - 1 );
		updateEndRows( );
	}
	
	protected void updateEndRows( )
	{
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
		for( int i = rows.length - 1 ; i >= 0 ; i-- )
		{
			removeRow( rows[ i ] );
		}
		updateEndRows( );
	}
	
	public List<QObject<SurveyColumnModel>> getColumnModels( )
	{
		return new ArrayList<>( surveyColumnModels.values( ) );
	}
	
	public void setColumnModels( Collection<? extends QObject<SurveyColumnModel>> newColumnModels )
	{
		List<SurveyColumn> newColumns = new ArrayList<>( );
		
		surveyColumnModels.clear( );
		
		for( QObject<SurveyColumnModel> colModel : newColumnModels )
		{
			surveyColumnModels.put( colModel.get( SurveyColumnModel.name ) , colModel );
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
}
