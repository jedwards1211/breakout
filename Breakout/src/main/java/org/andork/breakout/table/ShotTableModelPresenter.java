package org.andork.breakout.table;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
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

	private ShotListListener					shotListListener			= new ShotListListener( );

	private final List<ShotModelColumn>			columns						= new ArrayList<>( );
	private final Map<ShotColumnDef, Integer>	defIndices					= new HashMap<>( );

	private IntFunction<String>					intFormatter				= Integer::toString;
	private DoubleFunction<String>				doubleFormatter				= Double::toString;
	private BiFunction<Double, Double, String>	twoDoubleFormatter			= new TwoElemFormatter<>(
																				d -> doubleFormatter
																					.apply( d ) );
	private Function<DaiShotVector, String>		daiShotVectorFormatter		= new DaiShotVectorFormatter(
																				d -> doubleFormatter.apply( d ) );
	private Function<OffsetShotVector, String>	offsetShotVectorFormatter	= new OffsetShotVectorFormatter(
																				d -> doubleFormatter.apply( d ) );
	private Function<ShotVector, String>		shotVectorFormatter;

	public final StringColumn					fromColumn;
	public final StringColumn					toColumn;
	public final ParsedTextWithTypeColumn		vectorColumn;
	public final ParsedTextColumn				distColumn;
	public final ParsedTextColumn				azmFsBsColumn;
	public final ParsedTextColumn				azmFsColumn;
	public final ParsedTextColumn				azmBsColumn;
	public final ParsedTextColumn				incFsBsColumn;
	public final ParsedTextColumn				incFsColumn;
	public final ParsedTextColumn				incBsColumn;
	public final ParsedTextColumn				offsNColumn;
	public final ParsedTextColumn				offsEColumn;
	public final ParsedTextColumn				offsDColumn;

	private final Map<ShotColumnDef, Integer>	columnIndices				= new HashMap<>( );

	public ShotTableModelPresenter( Binder<QObject<NewProjectModel>> projectModelBinder )
	{
		fromColumn = new StringColumn( ShotColumnDef.from , s -> s.from );
		toColumn = new StringColumn( ShotColumnDef.to , s -> s.to );

		shotVectorFormatter = v ->
		{
			if( v instanceof DaiShotVector )
			{
				return daiShotVectorFormatter.apply( ( DaiShotVector ) v );
			}
			else if( v instanceof OffsetShotVector )
			{
				return offsetShotVectorFormatter.apply( ( OffsetShotVector ) v );
			}
			return v.toString( );
		};

		vectorColumn = new ParsedTextWithTypeColumn( ShotColumnDef.vector ,
			s -> s.vector == null ? null : shotVectorFormatter.apply( s.vector ) ,
			s -> s.vector == null ? null : s.vector.combinedText ,
			s -> s.vector == null ? null : s.vector.getClass( ) );
		distColumn = new ParsedTextColumn( ShotColumnDef.dist ,
			new DaiShotVectorProperty<>( v -> v.dist == null ? null : doubleFormatter.apply( v.dist ) ) ,
			new DaiShotVectorProperty<>( v -> v.distText ) );
		azmFsBsColumn = new ParsedTextColumn( ShotColumnDef.azmFsBs ,
			new DaiShotVectorProperty<>( v -> v.azmFs == null && v.azmBs == null ? null :
				twoDoubleFormatter.apply( v.azmFs , v.azmBs ) ) ,
			new DaiShotVectorProperty<>( v -> v.azmFsBsText ) );
		azmFsColumn = new ParsedTextColumn( ShotColumnDef.azmFs ,
			new DaiShotVectorProperty<>( v -> v.azmFs == null ? null : doubleFormatter.apply( v.azmFs ) ) ,
			new DaiShotVectorProperty<>( v -> v.azmFsText ) );
		azmBsColumn = new ParsedTextColumn( ShotColumnDef.azmBs ,
			new DaiShotVectorProperty<>( v -> v.azmBs == null ? null : doubleFormatter.apply( v.azmBs ) ) ,
			new DaiShotVectorProperty<>( v -> v.azmBsText ) );
		incFsBsColumn = new ParsedTextColumn( ShotColumnDef.incFsBs ,
			new DaiShotVectorProperty<>( v -> v.incFs == null && v.incBs == null ? null :
				twoDoubleFormatter.apply( v.incFs , v.incBs ) ) ,
			new DaiShotVectorProperty<>( v -> v.incFsBsText ) );
		incFsColumn = new ParsedTextColumn( ShotColumnDef.incFs ,
			new DaiShotVectorProperty<>( v -> v.incFs == null ? null : doubleFormatter.apply( v.incFs ) ) ,
			new DaiShotVectorProperty<>( v -> v.incFsText ) );
		incBsColumn = new ParsedTextColumn( ShotColumnDef.incBs ,
			new DaiShotVectorProperty<>( v -> v.incBs == null ? null : doubleFormatter.apply( v.incBs ) ) ,
			new DaiShotVectorProperty<>( v -> v.incBsText ) );
		offsNColumn = new ParsedTextColumn( ShotColumnDef.offsN ,
			new OffsetShotVectorProperty<>( v -> v.n == null ? null : doubleFormatter.apply( v.n ) ) ,
			new OffsetShotVectorProperty<>( v -> v.nText ) );
		offsEColumn = new ParsedTextColumn( ShotColumnDef.offsE ,
			new OffsetShotVectorProperty<>( v -> v.e == null ? null : doubleFormatter.apply( v.e ) ) ,
			new OffsetShotVectorProperty<>( v -> v.eText ) );
		offsDColumn = new ParsedTextColumn( ShotColumnDef.offsD ,
			new OffsetShotVectorProperty<>( v -> v.d == null ? null : doubleFormatter.apply( v.d ) ) ,
			new OffsetShotVectorProperty<>( v -> v.dText ) );

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

	public int indexOfColumn( ShotColumnDef def )
	{
		Integer result = defIndices.get( def );
		return result == null ? -1 : result;
	}

	private void setColumns( List<ShotModelColumn> newColumns )
	{
		columns.clear( );
		columns.addAll( newColumns );
		defIndices.clear( );
		int i = 0;
		for( ShotModelColumn column : newColumns )
		{
			defIndices.put( column.def , i++ );
		}
		fireTableStructureChanged( );
	}

	private List<ShotModelColumn> createColumns( )
	{
		if( shotList == null )
		{
			return Collections.emptyList( );
		}

		List<ShotModelColumn> result = new ArrayList<>( );

		result.add( fromColumn );
		result.add( toColumn );
		result.add( vectorColumn );
		result.add( distColumn );
		result.add( azmFsBsColumn );
		result.add( azmFsColumn );
		result.add( azmBsColumn );
		result.add( incFsBsColumn );
		result.add( incFsColumn );
		result.add( incBsColumn );
		result.add( offsNColumn );
		result.add( offsEColumn );
		result.add( offsDColumn );

		int custCols = 0;
		for( ShotColumnDef def : shotList.getCustomColumnDefs( ) )
		{
			ShotModelColumn col = createCustomColumn( custCols++ , def );
			if( col != null )
			{
				result.add( col );
			}
		}

		return result;
	}

	private ShotModelColumn createCustomColumn( int index , ShotColumnDef def )
	{
		switch( def.type )
		{
		case STRING:
			return new StringColumn( def , s -> ( String ) s.custom[ index ] );
		case INTEGER:
			return new ParsedTextColumn( def ,
				new CustomProperty<>( index , t -> intFormatter.apply( ( Integer ) t.value ) ) ,
				new CustomProperty<>( index , t -> t ) );
		case DOUBLE:
			return new ParsedTextColumn( def ,
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

	public abstract class ShotModelColumn implements Column<Integer>
	{
		public final ShotColumnDef	def;

		public ShotModelColumn( ShotColumnDef def )
		{
			super( );
			this.def = def;
		}

		@Override
		public String getColumnName( )
		{
			return def.name;
		}
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

	private class OffsetShotVectorProperty<V> implements Function<Shot, V>
	{
		Function<OffsetShotVector, V>	wrapped;

		public OffsetShotVectorProperty( Function<OffsetShotVector, V> wrapped )
		{
			super( );
			this.wrapped = wrapped;
		}

		@Override
		public V apply( Shot t )
		{
			if( t.vector instanceof OffsetShotVector )
			{
				return wrapped.apply( ( OffsetShotVector ) t.vector );
			}
			return null;
		}
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

	private class StringColumn extends ShotModelColumn
	{
		public StringColumn( ShotColumnDef def , Function<Shot, String> valueProperty )
		{
			super( def );
			this.valueProperty = valueProperty;
		}

		Function<Shot, String>	valueProperty;

		@Override
		public Class<?> getColumnClass( )
		{
			return String.class;
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

	private class ParsedTextColumn extends ShotModelColumn
	{
		Function<Shot, String>		valueProperty;
		Function<Shot, ParsedText>	textProperty;

		public ParsedTextColumn( ShotColumnDef def , Function<Shot, String> valueProperty ,
			Function<Shot, ParsedText> textProperty )
		{
			super( def );
			this.valueProperty = valueProperty;
			this.textProperty = textProperty;
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

	private class ParsedTextWithTypeColumn extends ShotModelColumn
	{
		Function<Shot, String>		valueProperty;
		Function<Shot, ParsedText>	textProperty;
		Function<Shot, Object>		typeProperty;

		public ParsedTextWithTypeColumn( ShotColumnDef def , Function<Shot, String> valueProperty ,
			Function<Shot, ParsedText> textProperty , Function<Shot, Object> typeProperty )
		{
			super( def );
			this.valueProperty = valueProperty;
			this.textProperty = textProperty;
			this.typeProperty = typeProperty;
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
			Object type = typeProperty.apply( shot );

			return value == null ?
				new ParsedTextWithType( text.text , text.note , type ) :
				new ParsedTextWithType( value , text == null ? null : text.note , type );
		}

		@Override
		public boolean setValueAt( Object aValue , Integer row )
		{
			return false;
		}
	}
}
