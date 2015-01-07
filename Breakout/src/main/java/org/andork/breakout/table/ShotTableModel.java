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
import org.andork.i18n.I18n;
import org.andork.i18n.I18n.Localizer;
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
public class ShotTableModel extends AbstractTableModel
{
	private QObjectBinder<NewProjectModel>		projectModelBinder;

	private ShotList							shotList;

	private ShotListListener					shotListListener	= new ShotListListener( );

	private final List<ShotModelColumn>			columns				= new ArrayList<>( );
	private final Map<ShotColumnDef, Integer>	defIndices			= new HashMap<>( );

	public final ShotModelColumn				fromColumn;
	public final ShotModelColumn				toColumn;
	public final ShotModelColumn				vectorColumn;
	public final ShotModelColumn				distColumn;
	public final ShotModelColumn				azmFsBsColumn;
	public final ShotModelColumn				azmFsColumn;
	public final ShotModelColumn				azmBsColumn;
	public final ShotModelColumn				incFsBsColumn;
	public final ShotModelColumn				incFsColumn;
	public final ShotModelColumn				incBsColumn;
	public final ShotModelColumn				offsNColumn;
	public final ShotModelColumn				offsEColumn;
	public final ShotModelColumn				offsDColumn;

	public ShotTableModel( I18n i18n , Binder<QObject<NewProjectModel>> projectModelBinder )
	{
		fromColumn = new FunctionColumn( ShotColumnDef.from , s -> s.from );
		toColumn = new FunctionColumn( ShotColumnDef.to , s -> s.to );

		vectorColumn = new SynthParsedTextWithValueColumn( ShotColumnDef.vector ,
			s -> s.vector ,
			s -> s.vector == null ? null : s.vector.combinedText );

		distColumn = new SynthParsedTextWithValueColumn( ShotColumnDef.dist ,
			new DaiShotVectorProperty<>( v -> v.dist ) ,
			new DaiShotVectorProperty<>( v -> v.distText ) );

		azmFsBsColumn = new SynthParsedTextWithValueColumn( ShotColumnDef.azmFsBs ,
			new DaiShotVectorProperty<>( v -> v.azmFs == null && v.azmBs == null ? null :
				new Double[ ] { v.azmFs , v.azmBs } ) ,
			new DaiShotVectorProperty<>( v -> v.azmFsBsText ) );

		azmFsColumn = new SynthParsedTextWithValueColumn( ShotColumnDef.azmFs ,
			new DaiShotVectorProperty<>( v -> v.azmFs ) ,
			new DaiShotVectorProperty<>( v -> v.azmFsText ) );

		azmBsColumn = new SynthParsedTextWithValueColumn( ShotColumnDef.azmBs ,
			new DaiShotVectorProperty<>( v -> v.azmBs ) ,
			new DaiShotVectorProperty<>( v -> v.azmBsText ) );

		incFsBsColumn = new SynthParsedTextWithValueColumn( ShotColumnDef.incFsBs ,
			new DaiShotVectorProperty<>( v -> v.incFs == null && v.incBs == null ? null :
				new Double[ ] { v.incFs , v.incBs } ) ,
			new DaiShotVectorProperty<>( v -> v.incFsBsText ) );

		incFsColumn = new SynthParsedTextWithValueColumn( ShotColumnDef.incFs ,
			new DaiShotVectorProperty<>( v -> v.incFs ) ,
			new DaiShotVectorProperty<>( v -> v.incFsText ) );

		incBsColumn = new SynthParsedTextWithValueColumn( ShotColumnDef.incBs ,
			new DaiShotVectorProperty<>( v -> v.incBs ) ,
			new DaiShotVectorProperty<>( v -> v.incBsText ) );

		offsNColumn = new SynthParsedTextWithValueColumn( ShotColumnDef.offsN ,
			new NevShotVectorProperty<>( v -> v.n ) ,
			new NevShotVectorProperty<>( v -> v.nText ) );

		offsEColumn = new SynthParsedTextWithValueColumn( ShotColumnDef.offsE ,
			new NevShotVectorProperty<>( v -> v.e ) ,
			new NevShotVectorProperty<>( v -> v.eText ) );

		offsDColumn = new SynthParsedTextWithValueColumn( ShotColumnDef.offsD ,
			new NevShotVectorProperty<>( v -> v.v ) ,
			new NevShotVectorProperty<>( v -> v.vText ) );

		this.projectModelBinder = QObjectBinder.create( NewProjectModel.spec );
		this.projectModelBinder.objLink.bind( projectModelBinder );

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
			return new FunctionColumn( def , s -> ( String ) s.custom[ index ] );
		case INTEGER:
		case DOUBLE:
			return new FunctionColumn( def , s -> ( ParsedTextWithValue ) s.custom[ index ] );
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
			fireTableChanged( new TableModelEvent( ShotTableModel.this , fromIndex , toIndex ,
				TableModelEvent.ALL_COLUMNS , TableModelEvent.INSERT ) );
		}

		@Override
		public void elementsDeleted( TableModelList<Shot> list , int fromIndex , int toIndex )
		{
			fireTableChanged( new TableModelEvent( ShotTableModel.this , fromIndex , toIndex ,
				TableModelEvent.ALL_COLUMNS , TableModelEvent.DELETE ) );
		}

		@Override
		public void elementsUpdated( TableModelList<Shot> list , int fromIndex , int toIndex )
		{
			fireTableChanged( new TableModelEvent( ShotTableModel.this , fromIndex , toIndex ,
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

	private class NevShotVectorProperty<V> implements Function<Shot, V>
	{
		Function<NevShotVector, V>	wrapped;

		public NevShotVectorProperty( Function<NevShotVector, V> wrapped )
		{
			super( );
			this.wrapped = wrapped;
		}

		@Override
		public V apply( Shot t )
		{
			if( t.vector instanceof NevShotVector )
			{
				return wrapped.apply( ( NevShotVector ) t.vector );
			}
			return null;
		}
	}

	private class FunctionColumn extends ShotModelColumn
	{
		public FunctionColumn( ShotColumnDef def , Function<Shot, ?> valueProperty )
		{
			super( def );
			this.valueProperty = valueProperty;
		}

		Function<Shot, ?>	valueProperty;

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

	private class SynthParsedTextWithValueColumn extends ShotModelColumn
	{
		Function<Shot, Object>		valueProperty;
		Function<Shot, ParsedText>	textProperty;

		public SynthParsedTextWithValueColumn( ShotColumnDef def , Function<Shot, Object> valueProperty ,
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
			Object value = valueProperty.apply( shot );
			ParsedText text = textProperty.apply( shot );

			return new ParsedTextWithValue(
				text == null ? null : text.text ,
				text == null ? null : text.note ,
				value );
		}

		@Override
		public boolean setValueAt( Object aValue , Integer row )
		{
			return false;
		}
	}
}
