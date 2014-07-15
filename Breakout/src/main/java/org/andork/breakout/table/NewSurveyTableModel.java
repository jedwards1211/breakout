package org.andork.breakout.table;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.andork.q.QLinkedHashMap;
import org.andork.q.QObject;
import org.andork.q.QSpec;
import org.andork.swing.table.AnnotatingTableRowSorter.AbstractTableModelCopier;
import org.andork.swing.table.FormatAndDisplayInfo;
import org.andork.swing.table.NiceTableModel;
import org.andork.util.DoubleFormat;
import org.andork.util.Format;
import org.andork.util.FormattedText;
import org.andork.util.IntegerFormat;
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
	
	public static enum CustomColumnType
	{
		STRING
		{
			Column<QObject<Row>> createColumn( String name )
			{
				return MapColumn.newInstance(
						QObjectColumn.newInstance( Row.spec , Row.customAttrs ) ,
						name ,
						String.class ,
						( ) -> QLinkedHashMap.newInstance( ) );
			}
			
			@Override
			public Class<?> valueClass( )
			{
				return String.class;
			}
		} ,
		INTEGER
		{
			Column<QObject<Row>> createColumn( String name )
			{
				return FormattedTextColumn.newInstance( MapColumn.newInstance(
						QObjectColumn.newInstance( Row.spec , Row.customAttrs ) ,
						name ,
						FormattedText.class ,
						( ) -> QLinkedHashMap.newInstance( ) ) ,
						Integer.class ,
						( ) -> new FormattedText( IntegerFormat.instance ) );
			}
			
			@Override
			public Class<?> valueClass( )
			{
				return Integer.class;
			}
		} ,
		REAL_NUMBER
		{
			Column<QObject<Row>> createColumn( String name )
			{
				return FormattedTextColumn.newInstance( MapColumn.newInstance(
						QObjectColumn.newInstance( Row.spec , Row.customAttrs ) ,
						name ,
						FormattedText.class ,
						( ) -> QLinkedHashMap.newInstance( ) ) ,
						Double.class ,
						( ) -> new FormattedText( DoubleFormat.instance ) );
			}
			
			@Override
			public Class<?> valueClass( )
			{
				return Double.class;
			}
		} ,
		DATE
		{
			Column<QObject<Row>> createColumn( String name )
			{
				return FormattedTextColumn.newInstance( MapColumn.newInstance(
						QObjectColumn.newInstance( Row.spec , Row.customAttrs ) ,
						name ,
						FormattedText.class ,
						( ) -> QLinkedHashMap.newInstance( ) ) , Date.class );
			}
			
			@Override
			public Class<?> valueClass( )
			{
				return Date.class;
			}
		};
		
		abstract Column<QObject<Row>> createColumn( String name );
		
		public abstract Class<?> valueClass( );
	}
	
	public final Column<QObject<Row>>		fromColumn;
	public final Column<QObject<Row>>		toColumn;
	public final Column<QObject<Row>>		shotMeasurementColumn;
	public final Column<QObject<Row>>		fromCrossSectionColumn;
	public final Column<QObject<Row>>		toCrossSectionColumn;
	
	private final Map<String, CustomColumn>	customColumns	= new LinkedHashMap<>( );
	
	public static class ColumnModel extends QSpec<ColumnModel>
	{
		public static final Attribute<Boolean>					fixed			= newAttribute( Boolean.class , "Fixed" );
		public static final Attribute<Boolean>					visible			= newAttribute( Boolean.class , "Show" );
		public static final Attribute<String>					name			= newAttribute( String.class , "Name" );
		public static final Attribute<CustomColumnType>			type			= newAttribute( CustomColumnType.class , "Type" );
		public static final Attribute<FormatAndDisplayInfo<?>>	defaultFormat	= newAttribute( FormatAndDisplayInfo.class , "Default Format" );
		
		public static final ColumnModel							instance		= new ColumnModel( );
		
		private ColumnModel( )
		{
			
		}
	}
	
	public static final class CustomColumn implements Column<QObject<Row>>
	{
		public final Column<QObject<Row>>	wrapped;
		public final CustomColumnType		type;
		Format<?>							defaultFormat;
		
		public CustomColumn( String name , CustomColumnType type , Format<?> defaultFormat )
		{
			super( );
			this.wrapped = type.createColumn( name );
			this.type = type;
			setDefaultFormat( defaultFormat );
		}
		
		private void setDefaultFormat( Format<?> defaultFormat )
		{
			this.defaultFormat = defaultFormat;
			if( wrapped instanceof FormattedTextColumn )
			{
				( ( FormattedTextColumn ) wrapped ).setFormattedTextSupplier( ( ) -> new FormattedText( defaultFormat ) );
			}
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
		setColumns( Arrays.asList(
				fromColumn = QObjectColumn.newInstance( Row.spec , Row.from ) ,
				
				toColumn = QObjectColumn.newInstance( Row.spec , Row.to ) ,
				
				shotMeasurementColumn = FormattedTextColumn.newInstance(
						QObjectColumn.newInstance( Row.spec , Row.shotMeasurement ) ,
						ShotMeasurement.class ,
						( ) -> new FormattedText( ) ).sortable( false ) ,
				
				fromCrossSectionColumn = FormattedTextColumn.newInstance(
						QObjectColumn.newInstance( Row.spec , Row.fromCrossSection ) ,
						CrossSection.class ,
						( ) -> new FormattedText( ) ).sortable( false ) ,
				
				toCrossSectionColumn = FormattedTextColumn.newInstance(
						QObjectColumn.newInstance( Row.spec , Row.toCrossSection ) ,
						CrossSection.class ,
						( ) -> new FormattedText( ) ).sortable( false )
				) );
		
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
	
	public List<QObject<ColumnModel>> getColumnModels( )
	{
		List<QObject<ColumnModel>> result = new ArrayList<>( );
		
		for( CustomColumn column : customColumns.values( ) )
		{
			QObject<ColumnModel> model = ColumnModel.instance.newObject( );
			model.set( ColumnModel.name , column.getColumnName( ) );
			model.set( ColumnModel.type , column.type );
			model.set( ColumnModel.defaultFormat , column.defaultFormat );
			result.add( model );
		}
		
		return result;
	}
	
	public void setColumnModels( Collection<? extends QObject<ColumnModel>> newCustomColumns )
	{
		List<Column<QObject<Row>>> newList = new ArrayList<>( getColumns( ) );
		Set<CustomColumn> toKeep = new HashSet<>( );
		for( QObject<ColumnModel> colModel : newCustomColumns )
		{
			String name = colModel.get( ColumnModel.name );
			CustomColumnType type = colModel.get( ColumnModel.type );
			FormatAndDisplayInfo<?> defaultFormat = colModel.get( ColumnModel.defaultFormat );
			
			CustomColumn existing = customColumns.get( name );
			if( existing != null && existing.type == type )
			{
				toKeep.add( existing );
			}
			else
			{
				newList.add( new CustomColumn( name , type , defaultFormat ) );
			}
		}
		
		for( CustomColumn column : customColumns.values( ) )
		{
			if( !toKeep.contains( column ) )
			{
				newList.remove( column );
			}
		}
		
		setColumns( newList );
		
		customColumns.clear( );
		for( Column<QObject<Row>> column : newList )
		{
			if( column instanceof CustomColumn )
			{
				customColumns.put( column.getColumnName( ) , ( CustomColumn ) column );
			}
		}
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
