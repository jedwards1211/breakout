package org.andork.breakout.table;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import javax.swing.event.TableModelEvent;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

import org.andork.bind2.BinderHolder;
import org.andork.bind2.Link;
import org.andork.breakout.table.ShotVector.Nev;
import org.andork.i18n.I18n;
import org.andork.q2.QObject;
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
	private BinderHolder<QObject<DataDefaults>>	dataDefaultsHolder	= new BinderHolder<>( );
	private BinderHolder<ShotList>				shotListHolder		= new BinderHolder<>( );

	private ShotTableLogic						logic;

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

	public ShotTableModel( I18n i18n , ShotTableLogic logic )
	{
		this.logic = logic;

		fromColumn = new FunctionColumn( ShotColumnDef.from , s -> s.from );
		toColumn = new FunctionColumn( ShotColumnDef.to , s -> s.to );

		vectorColumn = new SynthParsedTextWithValueColumn( ShotColumnDef.vector ,
			s -> s.vector ,
			castField( s -> s.vectorText , ShotVectorText.Joint.class , v -> v.text ) );

		distColumn = new SynthParsedTextWithValueColumn( ShotColumnDef.dist ,
			castField( s -> s.vector , ShotVector.Dai.class , v -> v.dist ) ,
			castField( s -> s.vectorText , ShotVectorText.Dai.class , v -> v.distText ) );

		azmFsBsColumn = new SynthParsedTextWithValueColumn( ShotColumnDef.azmFsBs ,
			castField( s -> s.vector , ShotVector.Dai.class ,
				v -> v.azmFs == null && v.azmBs == null ? null : new Double[ ] { v.azmFs , v.azmBs } ) ,
			castField( s -> s.vectorText , ShotVectorText.Dai.PairedAngles.class , v -> v.azmFsBsText ) );

		azmFsColumn = new SynthParsedTextWithValueColumn( ShotColumnDef.azmFs ,
			castField( s -> s.vector , ShotVector.Dai.class , v -> v.azmFs ) ,
			castField( s -> s.vectorText , ShotVectorText.Dai.SplitAngles.class , v -> v.azmFsText ) );

		azmBsColumn = new SynthParsedTextWithValueColumn( ShotColumnDef.azmBs ,
			castField( s -> s.vector , ShotVector.Dai.class , v -> v.azmBs ) ,
			castField( s -> s.vectorText , ShotVectorText.Dai.SplitAngles.class , v -> v.azmBsText ) );

		incFsBsColumn = new SynthParsedTextWithValueColumn( ShotColumnDef.incFsBs ,
			castField( s -> s.vector , ShotVector.Dai.class ,
				v -> v.incFs == null && v.incBs == null ? null : new Double[ ] { v.incFs , v.incBs } ) ,
			castField( s -> s.vectorText , ShotVectorText.Dai.PairedAngles.class , v -> v.incFsBsText ) );

		incFsColumn = new SynthParsedTextWithValueColumn( ShotColumnDef.incFs ,
			castField( s -> s.vector , ShotVector.Dai.class , v -> v.incFs ) ,
			castField( s -> s.vectorText , ShotVectorText.Dai.SplitAngles.class , v -> v.incFsText ) );

		incBsColumn = new SynthParsedTextWithValueColumn( ShotColumnDef.incBs ,
			castField( s -> s.vector , ShotVector.Dai.class , v -> v.incBs ) ,
			castField( s -> s.vectorText , ShotVectorText.Dai.SplitAngles.class , v -> v.incBsText ) );

		offsNColumn = new SynthParsedTextWithValueColumn( ShotColumnDef.offsN ,
			castField( s -> s.vector , ShotVector.Nev.class , v -> v.n ) ,
			castField( s -> s.vectorText , ShotVectorText.Nev.class , v -> v.nText ) );

		offsEColumn = new SynthParsedTextWithValueColumn( ShotColumnDef.offsE ,
			castField( s -> s.vector , ShotVector.Nev.class , v -> v.e ) ,
			castField( s -> s.vectorText , ShotVectorText.Nev.class , v -> v.eText ) );

		offsDColumn = new SynthParsedTextWithValueColumn( ShotColumnDef.offsV ,
			castField( s -> s.vector , ShotVector.Nev.class , v -> v.v ) ,
			castField( s -> s.vectorText , ShotVectorText.Nev.class , v -> v.vText ) );

		shotListHolder.addBinding( force ->
		{
			setShotList( shotListHolder.get( ) );
		} );
	}

	public Link<QObject<DataDefaults>> dataDefaultsLink( )
	{
		return dataDefaultsHolder.binderLink;
	}

	public Link<ShotList> shotListLink( )
	{
		return shotListHolder.binderLink;
	}

	private void setShotList( ShotList newList )
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
		if( columns.get( columnIndex ).setValueAt( aValue , rowIndex ) )
		{
			fireTableRowsUpdated( rowIndex , rowIndex );
		}
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

	private static <F, T extends F, V> CastFieldProperty<F, T, V> castField( Function<Shot, F> fieldGetter ,
		Class<T> requiredType ,
		Function<T, V> valueGetter )
	{
		return new CastFieldProperty<>( fieldGetter , requiredType , valueGetter );
	}

	private static class CastFieldProperty<F, T extends F, V> implements Function<Shot, V>
	{
		Function<Shot, F>	fieldGetter;
		Class<T>			requiredType;
		Function<T, V>		valueGetter;

		public CastFieldProperty( Function<Shot, F> fieldGetter , Class<T> requiredType , Function<T, V> valueGetter )
		{
			super( );
			this.fieldGetter = fieldGetter;
			this.requiredType = requiredType;
			this.valueGetter = valueGetter;
		}

		@Override
		public V apply( Shot shot )
		{
			F field = fieldGetter.apply( shot );
			if( field != null && requiredType.isAssignableFrom( field.getClass( ) ) )
			{
				return valueGetter.apply( requiredType.cast( field ) );
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
		Function<Shot, ?>			valueGetter;
		Function<Shot, ParsedText>	textGetter;

		public SynthParsedTextWithValueColumn( ShotColumnDef def , Function<Shot, ?> valueProperty ,
			Function<Shot, ParsedText> textGetter )
		{
			super( def );
			this.valueGetter = valueProperty;
			this.textGetter = textGetter;
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
			Shot shot = shotList.get( row );
			Object value = valueGetter.apply( shot );
			ParsedText text = textGetter.apply( shot );

			return new ParsedTextWithValue(
				text == null ? null : text.text ,
				text == null ? null : text.note ,
				value );
		}

		@Override
		public boolean setValueAt( Object aValue , Integer row )
		{
			Shot shot = shotList.get( row );
			return logic.set( shot , def , ( ParsedTextWithValue ) aValue );
		}
	}
}
