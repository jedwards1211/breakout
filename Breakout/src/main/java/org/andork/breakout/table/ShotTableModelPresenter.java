package org.andork.breakout.table;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.function.DoubleFunction;
import java.util.function.Function;
import java.util.function.IntFunction;

import javax.swing.event.TableModelEvent;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

import org.andork.bind2.Binder;
import org.andork.breakout.model.NewProjectModel;
import org.andork.q2.QObject;
import org.andork.q2.QObjectBinder;
import org.andork.q2.QSpec;
import org.andork.q2.QSpec.Property;
import org.andork.swing.table.NiceTableModel.Column;
import org.andork.swing.table.QObjectList;
import org.andork.swing.table.TableModelList;

/**
 * A {@link TableModel} backed by a {@link QObjectList} (every row is a {@link QObject}). You specify the columns which
 * determine how the properties of the {@link QObject}s get mapped to table cells.
 * 
 * @author James
 *
 * @param <S>
 *            the {@link QSpec} of the {@link QObject}s.
 */
@SuppressWarnings( "serial" )
public class ShotTableModelPresenter extends AbstractTableModel
{
	private QObjectBinder<NewProjectModel>		projectModelBinder;

	private Binder<Character>					decimalSeparatorBinder;
	private ShotList							shotList;

	private ShotListListener					shotListListener	= new ShotListListener( );

	private final List<Column<Integer>>			columns				= new ArrayList<>( );

	private IntFunction<String>					intFormatter		= Integer::toString;
	private DoubleFunction<String>				doubleFormatter		= Double::toString;
	private Function<Double[ ], String>			twoDoubleFormatter	= new DefaultTwoElemFormatter<>(
																		d -> doubleFormatter
																			.apply( d ) );

	public final StringColumn					fromColumn;
	public final StringColumn					toColumn;
	public final ParsedTextColumn				distColumn;
	public final ParsedTextColumn				azmFsBsColumn;
	public final ParsedTextColumn				azmFsColumn;
	public final ParsedTextColumn				azmBsColumn;
	public final ParsedTextColumn				incFsBsColumn;
	public final ParsedTextColumn				incFsColumn;
	public final ParsedTextColumn				incBsColumn;

	private final Map<String, Column<Integer>>	builtInColumns;

	private class DaiShotVectorProperty<V> implements Function<Shot, V>
	{
		Function<DaiShotVector, V>	wrapped;

		public DaiShotVectorProperty( Function<DaiShotVector, V> wrapped )
		{
			super( );
			this.wrapped = wrapped;
		}

		@Override
		public V apply( Shot t )
		{
			if( t.vector instanceof DaiShotVector )
			{
				return wrapped.apply( ( DaiShotVector ) t.vector );
			}
			return null;
		}
	}

	public ShotTableModelPresenter( Binder<QObject<NewProjectModel>> projectModelBinder )
	{
		fromColumn = new StringColumn( ShotTableColumnNames.from , s -> s.from );
		toColumn = new StringColumn( ShotTableColumnNames.to , s -> s.to );

		distColumn = new ParsedTextColumn( ShotTableColumnNames.dist ,
			new DaiShotVectorProperty<>( v -> v.dist == null ? null : doubleFormatter.apply( v.dist ) ) ,
			new DaiShotVectorProperty<>( v -> v.distText ) );
		azmFsBsColumn = new ParsedTextColumn( ShotTableColumnNames.azmFsBs ,
			new DaiShotVectorProperty<>( v -> v.azmFs == null && v.azmBs == null ? null :
				twoDoubleFormatter.apply( new Double[ ] { v.azmFs , v.azmBs } ) ) ,
			new DaiShotVectorProperty<>( v -> v.azmFsBsText ) );
		azmFsColumn = new ParsedTextColumn( ShotTableColumnNames.azmFs ,
			new DaiShotVectorProperty<>( v -> v.azmFs == null ? null : doubleFormatter.apply( v.azmFs ) ) ,
			new DaiShotVectorProperty<>( v -> v.azmFsText ) );
		azmBsColumn = new ParsedTextColumn( ShotTableColumnNames.azmBs ,
			new DaiShotVectorProperty<>( v -> v.azmBs == null ? null : doubleFormatter.apply( v.azmBs ) ) ,
			new DaiShotVectorProperty<>( v -> v.azmBsText ) );
		incFsBsColumn = new ParsedTextColumn( ShotTableColumnNames.incFsBs ,
			new DaiShotVectorProperty<>( v -> v.incFs == null && v.incBs == null ? null :
				twoDoubleFormatter.apply( new Double[ ] { v.incFs , v.incBs } ) ) ,
			new DaiShotVectorProperty<>( v -> v.incFsBsText ) );
		incFsColumn = new ParsedTextColumn( ShotTableColumnNames.incFs ,
			new DaiShotVectorProperty<>( v -> v.incFs == null ? null : doubleFormatter.apply( v.incFs ) ) ,
			new DaiShotVectorProperty<>( v -> v.incFsText ) );
		incBsColumn = new ParsedTextColumn( ShotTableColumnNames.incBs ,
			new DaiShotVectorProperty<>( v -> v.incBs == null ? null : doubleFormatter.apply( v.incBs ) ) ,
			new DaiShotVectorProperty<>( v -> v.incBsText ) );

		builtInColumns = new HashMap<>( );
		builtInColumns.put( ShotTableColumnNames.from , fromColumn );
		builtInColumns.put( ShotTableColumnNames.to , toColumn );
		builtInColumns.put( ShotTableColumnNames.dist , distColumn );
		builtInColumns.put( ShotTableColumnNames.azmFsBs , azmFsBsColumn );
		builtInColumns.put( ShotTableColumnNames.azmFs , azmFsColumn );
		builtInColumns.put( ShotTableColumnNames.azmBs , azmBsColumn );
		builtInColumns.put( ShotTableColumnNames.incFsBs , incFsBsColumn );
		builtInColumns.put( ShotTableColumnNames.incFs , incFsColumn );
		builtInColumns.put( ShotTableColumnNames.incBs , incBsColumn );

		this.projectModelBinder = QObjectBinder.create( NewProjectModel.spec );
		this.projectModelBinder.objLink.bind( projectModelBinder );
		decimalSeparatorBinder = this.projectModelBinder.property( NewProjectModel.decimalSep );

		decimalSeparatorBinder.addBinding( force ->
		{
			DecimalFormat format = ( DecimalFormat ) DecimalFormat.getInstance( );
			DecimalFormatSymbols symbols = format.getDecimalFormatSymbols( );
			symbols.setDecimalSeparator( decimalSeparatorBinder.get( ) );
			format.setDecimalFormatSymbols( symbols );
			format.setMinimumFractionDigits( 2 );
			format.setMaximumFractionDigits( 2 );

			doubleFormatter = format::format;
		} );

		this.projectModelBinder.update( true );
	}

	public Function<Double[ ], String> getTwoDoubleFormatter( )
	{
		return twoDoubleFormatter;
	}

	public void setTwoDoubleFormatter( Function<Double[ ], String> twoDoubleFormatter )
	{
		this.twoDoubleFormatter = Objects.requireNonNull( twoDoubleFormatter );
	}

	public void setShotList( ShotList newList )
	{
		if( shotList != newList )
		{
			if( shotList != null )
			{
				shotList.removeListener( shotListListener );
			}
			shotList = newList;
			if( newList != null )
			{
				newList.addListener( shotListListener );
			}
			setColumns( createColumns( ) );
		}
	}

	private void setColumns( List<Column<Integer>> newColumns )
	{
		columns.clear( );
		columns.addAll( newColumns );
		fireTableStructureChanged( );
	}

	private List<Column<Integer>> createColumns( )
	{
		if( shotList == null )
		{
			return Collections.emptyList( );
		}

		List<Column<Integer>> result = new ArrayList<>( );

		for( ShotColumnDef def : shotList.getBuiltinColumnDefs( ) )
		{
			Column<Integer> col = builtInColumns.get( def.name );
			if( col != null )
			{
				result.add( col );
			}
		}

		int custCols = 0;
		for( ShotColumnDef def : shotList.getCustomColumnDefs( ) )
		{
			Column<Integer> col = createCustomColumn( custCols++ , def );
			if( col != null )
			{
				result.add( col );
			}
		}

		return result;
	}

	private class CustomProperty<V> implements Function<Shot, V>
	{
		int									index;
		Function<ParsedTextWithValue, V>	wrapped;

		public CustomProperty( int index , Function<ParsedTextWithValue, V> wrapped )
		{
			super( );
			this.index = index;
			this.wrapped = wrapped;
		}

		@Override
		public V apply( Shot t )
		{
			return t.custom[ index ] == null ? null : wrapped.apply( ( ParsedTextWithValue ) t.custom[ index ] );
		}
	}

	private Column<Integer> createCustomColumn( int index , ShotColumnDef def )
	{
		switch( def.type )
		{
		case STRING:
			return new StringColumn( def.name , s -> ( String ) s.custom[ index ] );
		case INTEGER:
			return new ParsedTextColumn( def.name ,
				new CustomProperty<>( index , t -> intFormatter.apply( ( Integer ) t.value ) ) ,
				new CustomProperty<>( index , t -> t ) );
		case DOUBLE:
			return new ParsedTextColumn( def.name ,
				new CustomProperty<>( index , t -> doubleFormatter.apply( ( Double ) t.value ) ) ,
				new CustomProperty<>( index , t -> t ) );
		}

		return null;
	}

	@Override
	public int getRowCount( )
	{
		return shotList != null ? shotList.size( ) : 0;
	}

	@Override
	public int getColumnCount( )
	{
		return columns.size( );
	}

	@Override
	public String getColumnName( int column )
	{
		return columns.get( column ).getColumnName( );
	}

	@Override
	public Class<?> getColumnClass( int columnIndex )
	{
		return columns.get( columnIndex ).getColumnClass( );
	}

	@Override
	public boolean isCellEditable( int rowIndex , int columnIndex )
	{
		return columns.get( columnIndex ).isCellEditable( rowIndex );
	}

	@Override
	public void setValueAt( Object aValue , int rowIndex , int columnIndex )
	{
		columns.get( columnIndex ).setValueAt( aValue , rowIndex );
	}

	@Override
	public Object getValueAt( int rowIndex , int columnIndex )
	{
		return columns.get( columnIndex ).getValueAt( rowIndex );
	}

	private class ShotListListener implements TableModelList.Listener<Shot>
	{
		@Override
		public void elementsInserted( TableModelList<Shot> list , int fromIndex , int toIndex )
		{
			fireTableChanged( new TableModelEvent( ShotTableModelPresenter.this , fromIndex , toIndex ,
				TableModelEvent.ALL_COLUMNS , TableModelEvent.INSERT ) );
		}

		@Override
		public void elementsDeleted( TableModelList<Shot> list , int fromIndex , int toIndex )
		{
			fireTableChanged( new TableModelEvent( ShotTableModelPresenter.this , fromIndex , toIndex ,
				TableModelEvent.ALL_COLUMNS , TableModelEvent.DELETE ) );
		}

		@Override
		public void elementsUpdated( TableModelList<Shot> list , int fromIndex , int toIndex )
		{
			fireTableChanged( new TableModelEvent( ShotTableModelPresenter.this , fromIndex , toIndex ,
				TableModelEvent.ALL_COLUMNS , TableModelEvent.UPDATE ) );
		}

		@Override
		public void dataChanged( TableModelList<Shot> list )
		{
			fireTableDataChanged( );
		}

		@Override
		public void structureChanged( TableModelList<Shot> list )
		{
			setColumns( createColumns( ) );
		}
	}

	@SuppressWarnings( "rawtypes" )
	public static class PropertyColumn<S extends QSpec> implements Column<QObject<? extends S>>
	{
		protected final Property	property;
		protected boolean			editable;

		public PropertyColumn( Property property , boolean editable )
		{
			super( );
			this.property = property;
			this.editable = editable;
		}

		@Override
		public String getColumnName( )
		{
			return property.name( );
		}

		@Override
		public Class<?> getColumnClass( )
		{
			return property.type( );
		}

		@Override
		public boolean isCellEditable( QObject<? extends S> row )
		{
			return editable;
		}

		@SuppressWarnings( "unchecked" )
		@Override
		public Object getValueAt( QObject<? extends S> row )
		{
			return row.get( property );
		}

		@SuppressWarnings( "unchecked" )
		@Override
		public boolean setValueAt( Object aValue , QObject<? extends S> row )
		{
			Object oldValue = row.set( property , aValue );
			return !property.equals( oldValue , aValue );
		}
	}

	public class StringColumn implements Column<Integer>
	{
		public StringColumn( String name , Function<Shot, String> valueProperty )
		{
			super( );
			this.name = name;
			this.valueProperty = valueProperty;
		}

		String					name;

		Function<Shot, String>	valueProperty;

		@Override
		public String getColumnName( )
		{
			return name;
		}

		@Override
		public Class<?> getColumnClass( )
		{
			return ParsedText.class;
		}

		@Override
		public boolean isCellEditable( Integer row )
		{
			return true;
		}

		@Override
		public Object getValueAt( Integer row )
		{
			return valueProperty.apply( shotList.get( row ) );
		}

		@Override
		public boolean setValueAt( Object aValue , Integer row )
		{
			return false;
		}
	}

	public class ParsedTextColumn implements Column<Integer>
	{
		String						name;

		Function<Shot, String>		valueProperty;
		Function<Shot, ParsedText>	textProperty;

		public ParsedTextColumn( String name , Function<Shot, String> valueProperty ,
			Function<Shot, ParsedText> textProperty )
		{
			super( );
			this.name = name;
			this.valueProperty = valueProperty;
			this.textProperty = textProperty;
		}

		@Override
		public String getColumnName( )
		{
			return name;
		}

		@Override
		public Class<?> getColumnClass( )
		{
			return ParsedText.class;
		}

		@Override
		public boolean isCellEditable( Integer row )
		{
			return false;
		}

		@Override
		public Object getValueAt( Integer row )
		{
			Shot shot = shotList.get( row );
			String value = valueProperty.apply( shot );
			ParsedText text = textProperty.apply( shot );

			return value == null ? text : new ParsedText( value , text == null ? null : text.note );
		}

		@Override
		public boolean setValueAt( Object aValue , Integer row )
		{
			return false;
		}
	}
}
