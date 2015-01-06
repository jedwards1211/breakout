package org.andork.breakout.table;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
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
import org.andork.event.BasicPropertyChangeListener;
import org.andork.q2.QHashMap;
import org.andork.q2.QObject;
import org.andork.q2.QObjectPropertyBinder;
import org.andork.q2.QSpec;
import org.andork.q2.QSpec.Property;
import org.andork.swing.table.NiceTableModel.Column;
import org.andork.swing.table.QObjectList;

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
	private Binder<QObject<NewProjectModel>>							projectModelBinder;

	private QObjectPropertyBinder<Character>							decimalSeparatorBinder;
	private QObjectPropertyBinder<QObjectList<Shot>>					shotListBinder;
	private QObjectPropertyBinder<QObjectList<ShotText>>				shotTextListBinder;
	private QObjectPropertyBinder<QHashMap<Integer, ShotTableColumn>>	columnDefsBinder;

	private QObjectList<Shot>											shotList;
	private QObjectList<ShotText>										shotTextList;
	private QHashMap<Integer, ShotTableColumn>							columnDefs;

	private ShotListener												shotListener		= new ShotListener( );
	private ShotTextListener											shotTextListener	= new ShotTextListener( );
	private ColumnDefsListener											columnDefsListener	= new ColumnDefsListener( );

	private final List<Column<Integer>>									columns				= new ArrayList<>( );
	private final List<Column<Integer>>									unmodifiableColumns	= Collections
																								.unmodifiableList( columns );

	private IntFunction<String>											intFormatter		= Integer::toString;
	private DoubleFunction<String>										doubleFormatter		= Double::toString;
	private Function<Double[ ], String>									twoDoubleFormatter	= new DefaultTwoElemFormatter<>(
																								d -> doubleFormatter
																									.apply( d ) );

	public final UncheckedStringColumn									fromColumn;
	public final UncheckedStringColumn									toColumn;
	public final SubVectorColumn<ShotVector, Double>					distColumn;
	public final SubVectorColumn<ShotVector, Double[ ]>					azmFsBsColumn;
	public final SubVectorColumn<ShotVector, Double>					azmFsColumn;
	public final SubVectorColumn<ShotVector, Double>					azmBsColumn;
	public final SubVectorColumn<ShotVector, Double[ ]>					incFsBsColumn;
	public final SubVectorColumn<ShotVector, Double>					incFsColumn;
	public final SubVectorColumn<ShotVector, Double>					incBsColumn;

	private final Map<String, Column<Integer>>							builtInColumns;

	public ShotTableModelPresenter( Binder<QObject<NewProjectModel>> projectModelBinder )
	{
		fromColumn = new UncheckedStringColumn( Shot.from );
		toColumn = new UncheckedStringColumn( Shot.to );

		distColumn = new SubVectorColumn<>( Shot.vector , ShotText.dist ,
			v ->
			{
				if( v instanceof DistAzmIncShotVector )
				{
					return ( ( DistAzmIncShotVector ) v ).dist;
				}
				return null;
			} , d -> doubleFormatter.apply( d ) );
		azmFsBsColumn = new SubVectorColumn<>( Shot.vector , ShotText.azmFsBs ,
			v ->
			{
				if( v instanceof DistAzmIncShotVector )
				{
					DistAzmIncShotVector vector = ( DistAzmIncShotVector ) v;
					return new Double[ ] { vector.azmFs , vector.azmBs };
				}
				return null;
			} , d -> twoDoubleFormatter.apply( d ) );
		azmFsColumn = new SubVectorColumn<>( Shot.vector , ShotText.azmFs ,
			v ->
			{
				if( v instanceof DistAzmIncShotVector )
				{
					return ( ( DistAzmIncShotVector ) v ).azmFs;
				}
				return null;
			} , d -> doubleFormatter.apply( d ) );
		azmBsColumn = new SubVectorColumn<>( Shot.vector , ShotText.azmBs ,
			v ->
			{
				if( v instanceof DistAzmIncShotVector )
				{
					return ( ( DistAzmIncShotVector ) v ).azmBs;
				}
				return null;
			} , d -> doubleFormatter.apply( d ) );
		incFsBsColumn = new SubVectorColumn<>( Shot.vector , ShotText.incFsBs ,
			v ->
			{
				if( v instanceof DistAzmIncShotVector )
				{
					DistAzmIncShotVector vector = ( DistAzmIncShotVector ) v;
					return new Double[ ] { vector.incFs , vector.incBs };
				}
				return null;
			} , d -> twoDoubleFormatter.apply( d ) );
		incFsColumn = new SubVectorColumn<>( Shot.vector , ShotText.incFs ,
			v ->
			{
				if( v instanceof DistAzmIncShotVector )
				{
					return ( ( DistAzmIncShotVector ) v ).incFs;
				}
				return null;
			} , d -> doubleFormatter.apply( d ) );
		incBsColumn = new SubVectorColumn<>( Shot.vector , ShotText.incBs ,
			v ->
			{
				if( v instanceof DistAzmIncShotVector )
				{
					return ( ( DistAzmIncShotVector ) v ).incBs;
				}
				return null;
			} , d -> doubleFormatter.apply( d ) );

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

		this.projectModelBinder = projectModelBinder;

		decimalSeparatorBinder = new QObjectPropertyBinder<>( NewProjectModel.decimalSep );
		decimalSeparatorBinder.objLink.bind( projectModelBinder );
		shotListBinder = new QObjectPropertyBinder<>( NewProjectModel.shotList );
		shotListBinder.objLink.bind( projectModelBinder );
		shotTextListBinder = new QObjectPropertyBinder<>( NewProjectModel.shotTextList );
		shotTextListBinder.objLink.bind( projectModelBinder );
		columnDefsBinder = new QObjectPropertyBinder<>( NewProjectModel.shotCols );
		columnDefsBinder.objLink.bind( projectModelBinder );
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
		shotListBinder.addBinding( force -> setShotList( shotListBinder.get( ) ) );
		shotTextListBinder.addBinding( force -> setShotTextList( shotTextListBinder.get( ) ) );
		columnDefsBinder.addBinding( force -> setColumnDefs( columnDefsBinder.get( ) ) );

		decimalSeparatorBinder.update( true );
		shotListBinder.update( true );
		shotTextListBinder.update( true );
		columnDefsBinder.update( true );
	}

	public Function<Double[ ], String> getTwoDoubleFormatter( )
	{
		return twoDoubleFormatter;
	}

	public void setTwoDoubleFormatter( Function<Double[ ], String> twoDoubleFormatter )
	{
		this.twoDoubleFormatter = Objects.requireNonNull( twoDoubleFormatter );
	}

	private void setShotList( QObjectList<Shot> newList )
	{
		if( shotList != newList )
		{
			if( shotList != null )
			{
				shotList.removeListener( shotListener );
			}
			shotList = newList;
			if( newList != null )
			{
				newList.addListener( shotListener );
			}
			setColumns( createColumns( ) );
		}
	}

	private void setShotTextList( QObjectList<ShotText> newList )
	{
		if( shotTextList != newList )
		{
			if( shotTextList != null )
			{
				shotTextList.removeListener( shotTextListener );
			}
			shotTextList = newList;
			if( newList != null )
			{
				newList.addListener( shotTextListener );
			}
			setColumns( createColumns( ) );
		}
	}

	private void setColumnDefs( QHashMap<Integer, ShotTableColumn> columnDefs )
	{
		if( this.columnDefs != columnDefs )
		{
			if( this.columnDefs != null )
			{
				this.columnDefs.removePropertyChangeListener( columnDefsListener );
			}
			this.columnDefs = columnDefs;
			if( columnDefs != null )
			{
				columnDefs.addPropertyChangeListener( columnDefsListener );
			}
			setColumns( createColumns( ) );
		}
	}

	public List<Column<Integer>> getColumns( )
	{
		return unmodifiableColumns;
	}

	private void setColumns( List<Column<Integer>> newColumns )
	{
		columns.clear( );
		columns.addAll( newColumns );
		fireTableStructureChanged( );
	}

	@Override
	public int getRowCount( )
	{
		return shotList == null || shotTextList == null ? 0 : Math.min( shotList.size( ) , shotTextList.size( ) );
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

	private List<Column<Integer>> createColumns( )
	{
		if( shotList == null || shotTextList == null || columnDefs == null )
		{
			return Collections.emptyList( );
		}

		TreeMap<Integer, Column<Integer>> result = new TreeMap<>( );

		for( Map.Entry<Integer, ShotTableColumn> entry : columnDefs.entrySet( ) )
		{
			int index = entry.getKey( );
			ShotTableColumnType type = entry.getValue( ).type;

			Column<Integer> col = null;

			if( type == ShotTableColumnType.BUILTIN )
			{
				String name = entry.getValue( ).name;
				col = builtInColumns.get( name );
			}
			else
			{
				String name = ShotTableColumnNames.custom( index );
				Property<?> shotProperty = shotList.spec( ).propertyNamed( name );
				Property<?> shotTextProperty = shotTextList.spec( ).propertyNamed( name );

				if( shotProperty != null && shotTextProperty != null )
				{
					col = createDefaultColumn( type , shotProperty , shotTextProperty );
				}
			}

			if( col != null )
			{
				result.put( index , col );
			}
		}

		return new ArrayList<>( result.values( ) );
	}

	private Column<Integer> createDefaultColumn( ShotTableColumnType type , Property<?> shotProperty ,
		Property<?> shotTextProperty )
	{
		switch( type )
		{
		case STRING:
			return new UncheckedStringColumn( shotProperty.cast( String.class ) );
		case INTEGER:
			return new CheckedColumn<Integer>( shotProperty.cast( Integer.class ) ,
				shotTextProperty.cast( ParsedText.class ) ,
				i -> intFormatter.apply( i ) );
		case DOUBLE:
			return new CheckedColumn<Double>( shotProperty.cast( Double.class ) ,
				shotTextProperty.cast( ParsedText.class ) ,
				d -> doubleFormatter.apply( d ) );
		}

		return null;
	}

	private class ShotListener implements QObjectList.Listener<Shot>
	{
		@Override
		public void elementsInserted( QObjectList<? extends Shot> list , int fromIndex , int toIndex )
		{
			fireTableChanged( new TableModelEvent( ShotTableModelPresenter.this , fromIndex , toIndex ,
				TableModelEvent.ALL_COLUMNS , TableModelEvent.INSERT ) );
		}

		@Override
		public void elementsDeleted( QObjectList<? extends Shot> list , int fromIndex , int toIndex )
		{
			fireTableChanged( new TableModelEvent( ShotTableModelPresenter.this , fromIndex , toIndex ,
				TableModelEvent.ALL_COLUMNS , TableModelEvent.DELETE ) );
		}

		@Override
		public void elementsUpdated( QObjectList<? extends Shot> list , int fromIndex , int toIndex )
		{
			fireTableChanged( new TableModelEvent( ShotTableModelPresenter.this , fromIndex , toIndex ,
				TableModelEvent.ALL_COLUMNS , TableModelEvent.UPDATE ) );
		}

		@Override
		public void
			elementsUpdated( QObjectList<? extends Shot> list , int fromIndex , int toIndex , Property<?> property )
		{
			fireTableChanged( new TableModelEvent( ShotTableModelPresenter.this , fromIndex , toIndex ,
				TableModelEvent.ALL_COLUMNS , TableModelEvent.UPDATE ) );
		}
	}

	private class ShotTextListener implements QObjectList.Listener<ShotText>
	{
		@Override
		public void elementsInserted( QObjectList<? extends ShotText> list , int fromIndex , int toIndex )
		{
		}

		@Override
		public void elementsDeleted( QObjectList<? extends ShotText> list , int fromIndex , int toIndex )
		{
		}

		@Override
		public void elementsUpdated( QObjectList<? extends ShotText> list , int fromIndex , int toIndex )
		{
			fireTableChanged( new TableModelEvent( ShotTableModelPresenter.this , fromIndex , toIndex ,
				TableModelEvent.ALL_COLUMNS , TableModelEvent.UPDATE ) );
		}

		@Override
		public void
			elementsUpdated( QObjectList<? extends ShotText> list , int fromIndex , int toIndex , Property<?> property )
		{
			fireTableChanged( new TableModelEvent( ShotTableModelPresenter.this , fromIndex , toIndex ,
				TableModelEvent.ALL_COLUMNS , TableModelEvent.UPDATE ) );
		}
	}

	private class ColumnDefsListener implements BasicPropertyChangeListener
	{
		@Override
		public void propertyChange( Object source , Object property , Object oldValue , Object newValue , int index )
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

	public class UncheckedStringColumn implements Column<Integer>
	{
		public UncheckedStringColumn( Property<? extends String> valueProperty )
		{
			super( );
			this.valueProperty = valueProperty;
		}

		Property<? extends String>	valueProperty;

		@Override
		public String getColumnName( )
		{
			return valueProperty.name( );
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
			return shotList.get( row ).get( valueProperty );
		}

		@Override
		public boolean setValueAt( Object aValue , Integer row )
		{
			return false;
		}
	}

	public class CheckedStringColumn implements Column<Integer>
	{
		public CheckedStringColumn( Property<? extends String> valueProperty ,
			Property<? extends ParsedText> textProperty )
		{
			super( );
			this.valueProperty = valueProperty;
			this.textProperty = textProperty;
		}

		Property<? extends String>		valueProperty;
		Property<? extends ParsedText>	textProperty;

		@Override
		public String getColumnName( )
		{
			return valueProperty.name( );
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
			String value = shotList.get( row ).get( valueProperty );
			ParsedText text = shotTextList.get( row ).get( textProperty );

			return value == null ? text : new ParsedText( value , text == null ? null : text.note );
		}

		@Override
		public boolean setValueAt( Object aValue , Integer row )
		{
			return false;
		}
	}

	public class CheckedColumn<V> implements Column<Integer>
	{
		public CheckedColumn( Property<? extends V> valueProperty , Property<? extends ParsedText> textProperty ,
			Function<V, String> valueFormatter )
		{
			super( );
			this.valueProperty = valueProperty;
			this.textProperty = textProperty;
			this.valueFormatter = valueFormatter;
		}

		Property<? extends V>			valueProperty;
		Property<? extends ParsedText>	textProperty;

		Function<V, String>				valueFormatter;

		@Override
		public String getColumnName( )
		{
			return textProperty.name( );
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
			V value = shotList.get( row ).get( valueProperty );
			ParsedText text = shotTextList.get( row ).get( textProperty );

			return value == null ? text : new ParsedText( valueFormatter.apply( value ) , text == null ? null
				: text.note );
		}

		@Override
		public boolean setValueAt( Object aValue , Integer row )
		{
			return false;
		}
	}

	public class SubVectorColumn<V, E> implements Column<Integer>
	{
		public SubVectorColumn( Property<? extends V> valueProperty , Property<? extends ParsedText> textProperty ,
			Function<V, E> elemFn ,
			Function<E, String> elemFormatter )
		{
			super( );
			this.valueProperty = valueProperty;
			this.textProperty = textProperty;
			this.elemFn = elemFn;
			this.elemFormatter = elemFormatter;
		}

		Property<? extends V>			valueProperty;
		Property<? extends ParsedText>	textProperty;

		Function<V, E>					elemFn;
		Function<E, String>				elemFormatter;

		@Override
		public String getColumnName( )
		{
			return textProperty.name( );
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
			E elem = elemFn.apply( shotList.get( row ).get( valueProperty ) );
			ParsedText text = shotTextList.get( row ).get( textProperty );

			return elem == null ? text : new ParsedText( elemFormatter.apply( elem ) , text == null ? null
				: text.note );
		}

		@Override
		public boolean setValueAt( Object aValue , Integer row )
		{
			return false;
		}
	}
}
