package org.andork.breakout.table;

import java.awt.Color;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.DoubleFunction;
import java.util.function.Function;
import java.util.function.IntFunction;

import javax.swing.DefaultListCellRenderer;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import org.andork.bind2.Binder;
import org.andork.bind2.Binding;
import org.andork.i18n.I18n;
import org.andork.i18n.I18n.Localizer;
import org.andork.swing.list.FunctionListCellRenderer;

@SuppressWarnings( "serial" )
public class ShotTableColumnModel extends DefaultTableColumnModel
{
	public final TableColumn						fromColumn;
	public final TableColumn						toColumn;
	public final TableColumn						vectorColumn;
	public final TableColumn						distColumn;
	public final TableColumn						azmFsBsColumn;
	public final TableColumn						azmFsColumn;
	public final TableColumn						azmBsColumn;
	public final TableColumn						incFsBsColumn;
	public final TableColumn						incFsColumn;
	public final TableColumn						incBsColumn;
	public final TableColumn						offsNColumn;
	public final TableColumn						offsEColumn;
	public final TableColumn						offsDColumn;

	private final Map<ShotColumnDef, TableColumn>	builtInColumns		= new HashMap<>( );

	private IntFunction<String>						intFormatter		= Integer::toString;
	private DoubleFunction<String>					doubleFormatter		= Double::toString;
	private Function<Double[ ], String>				twoDoubleFormatter	= new TwoElemFormatter<>(
																			d -> doubleFormatter
																				.apply( d ) );

	private Function<String, ParsedTextWithValue>	doubleParser;

	private Function<DaiShotVector, String>			daiShotVectorFormatter;
	private Function<NevShotVector, String>			nedShotVectorFormatter;
	private Function<NevShotVector, String>			neelShotVectorFormatter;
	private Function<ShotVector, String>			shotVectorFormatter;

	private final Map<ParseStatus, Color>			noteColors;

	private final Localizer							localizer;

	private TableCellRenderer						intRenderer;
	private TableCellRenderer						doubleRenderer;
	private TableCellEditor							doubleEditor;
	private TableCellRenderer						twoDoubleRenderer;
	private TableCellRenderer						vectorValueRender;
	private TypeTableCellRenderer					vectorRenderer;

	public ShotTableColumnModel( I18n i18n )
	{
		localizer = i18n.forClass( ShotTableColumnModel.class );

		daiShotVectorFormatter = new DaiShotVectorFormatter( d -> doubleFormatter.apply( d ) ,
			localizer.stringBinder( DaiShotVector.class.getName( ) + ".format" ) );
		nedShotVectorFormatter = new NevShotVectorFormatter( d -> doubleFormatter.apply( d ) ,
			localizer.stringBinder( NedShotVector.class.getName( ) + ".format" ) );
		neelShotVectorFormatter = new NevShotVectorFormatter( d -> doubleFormatter.apply( d ) ,
			localizer.stringBinder( NeelShotVector.class.getName( ) + ".format" ) );

		shotVectorFormatter = v ->
		{
			if( v instanceof DaiShotVector )
			{
				return daiShotVectorFormatter.apply( ( DaiShotVector ) v );
			}
			else if( v instanceof NedShotVector )
			{
				return nedShotVectorFormatter.apply( ( NedShotVector ) v );
			}
			else if( v instanceof NeelShotVector )
			{
				return neelShotVectorFormatter.apply( ( NeelShotVector ) v );
			}
			return v.toString( );
		};

		noteColors = new HashMap<>( );
		noteColors.put( ParseStatus.WARNING , Color.YELLOW );
		noteColors.put( ParseStatus.ERROR , Color.RED );

		Function<Object, Color> noteColor =
			n -> n instanceof ParseNote ? noteColors.get( ( ( ParseNote ) n ).status ) : null;
		Function<Object, String> noteMessage = n ->
		{
			if( n instanceof ParseNote )
			{
				ParseNote note = ( ParseNote ) n;
				if( note.messageKey != null )
				{
					String message = localizer.getString( note.messageKey );
					if( note.status == ParseStatus.WARNING || note.status == ParseStatus.ERROR )
					{
						message = localizer.getFormattedString( note.status.toString( ) , message );
					}
					return message;
				}
			}
			return null;
		};

		fromColumn = new TableColumn( );
		fromColumn.setIdentifier( ShotColumnDef.from );

		toColumn = new TableColumn( );
		toColumn.setIdentifier( ShotColumnDef.to );

		intRenderer =
			new ParsedTextWithValueTableCellRenderer(
				v -> v instanceof Integer ? intFormatter.apply( ( Integer ) v ) : null ,
				noteColor , noteMessage );

		doubleRenderer =
			new ParsedTextWithValueTableCellRenderer(
				v -> v instanceof Double ? doubleFormatter.apply( ( Double ) v ) : null ,
				noteColor , noteMessage );
		doubleParser = new DefaultParser( Double::parseDouble );
		doubleEditor = new ParsedTextWithValueCellEditor( doubleParser );

		twoDoubleRenderer =
			new ParsedTextWithValueTableCellRenderer(
				v -> v instanceof Double[ ] ? twoDoubleFormatter.apply( ( Double[ ] ) v ) : null ,
				noteColor , noteMessage );

		vectorValueRender =
			new ParsedTextWithValueTableCellRenderer(
				v -> v instanceof ShotVector ? shotVectorFormatter.apply( ( ShotVector ) v ) : null ,
				noteColor , noteMessage );
		vectorRenderer =
			new TypeTableCellRenderer( vectorValueRender , p -> ( ( ParsedTextWithValue ) p ).value.getClass( ) );
		vectorRenderer.setAvailableTypes( Arrays.asList(
			DaicShotVector.class , DaiuShotVector.class , NedShotVector.class , NeelShotVector.class ) );
		vectorRenderer.typeSelector( ).getComboBox( ).setRenderer(
			new FunctionListCellRenderer(
				c -> c == null ? null : localizer.getString( ( ( Class<?> ) c ).getName( ) + ".abbrev" ) ,
				new DefaultListCellRenderer( ) ) );

		vectorColumn = new TableColumn( );
		vectorColumn.setIdentifier( ShotColumnDef.vector );
		vectorColumn.setCellRenderer( vectorRenderer );

		distColumn = new TableColumn( );
		distColumn.setIdentifier( ShotColumnDef.dist );
		distColumn.setCellRenderer( doubleRenderer );
		distColumn.setCellEditor( doubleEditor );

		azmFsBsColumn = new TableColumn( );
		azmFsBsColumn.setIdentifier( ShotColumnDef.azmFsBs );
		azmFsBsColumn.setCellRenderer( twoDoubleRenderer );

		azmFsColumn = new TableColumn( );
		azmFsColumn.setIdentifier( ShotColumnDef.azmFs );
		azmFsColumn.setCellRenderer( doubleRenderer );
		azmFsColumn.setCellEditor( doubleEditor );

		azmBsColumn = new TableColumn( );
		azmBsColumn.setIdentifier( ShotColumnDef.azmBs );
		azmBsColumn.setCellRenderer( doubleRenderer );
		azmBsColumn.setCellEditor( doubleEditor );

		incFsBsColumn = new TableColumn( );
		incFsBsColumn.setIdentifier( ShotColumnDef.incFsBs );
		incFsBsColumn.setCellRenderer( twoDoubleRenderer );

		incFsColumn = new TableColumn( );
		incFsColumn.setIdentifier( ShotColumnDef.incFs );
		incFsColumn.setCellRenderer( doubleRenderer );
		incFsColumn.setCellEditor( doubleEditor );

		incBsColumn = new TableColumn( );
		incBsColumn.setIdentifier( ShotColumnDef.incBs );
		incBsColumn.setCellRenderer( doubleRenderer );
		incBsColumn.setCellEditor( doubleEditor );

		offsNColumn = new TableColumn( );
		offsNColumn.setIdentifier( ShotColumnDef.offsN );
		offsNColumn.setCellRenderer( doubleRenderer );
		offsNColumn.setCellEditor( doubleEditor );

		offsEColumn = new TableColumn( );
		offsEColumn.setIdentifier( ShotColumnDef.offsE );
		offsEColumn.setCellRenderer( doubleRenderer );
		offsEColumn.setCellEditor( doubleEditor );

		offsDColumn = new TableColumn( );
		offsDColumn.setIdentifier( ShotColumnDef.offsV );
		offsDColumn.setCellRenderer( doubleRenderer );
		offsDColumn.setCellEditor( doubleEditor );

		for( TableColumn column : Arrays.asList(
			fromColumn ,
			toColumn ,
			vectorColumn ,
			distColumn ,
			azmFsBsColumn ,
			azmFsColumn ,
			azmBsColumn ,
			incFsBsColumn ,
			incFsColumn ,
			incBsColumn ,
			offsNColumn ,
			offsEColumn ,
			offsDColumn ) )
		{
			ShotColumnDef def = ( ShotColumnDef ) column.getIdentifier( );
			Binder<String> b = localizer.stringBinder( def.name );
			Binding nameBinding = f -> column.setHeaderValue( b.get( ) );
			b.addBinding( nameBinding );
			nameBinding.update( true );
			builtInColumns.put( def , column );
		}
	}

	public void update( ShotTableModel model , List<ShotColumnDef> columnDefs )
	{
		while( getColumnCount( ) > 0 )
		{
			removeColumn( getColumn( 0 ) );
		}

		for( ShotColumnDef def : columnDefs )
		{
			TableColumn column = null;
			int index = model.indexOfColumn( def );

			if( index < 0 )
			{
				continue;
			}

			if( def.type == ShotColumnType.BUILTIN )
			{
				column = builtInColumns.get( def );
			}
			else
			{
				column = new TableColumn( );
				column.setHeaderValue( def.name );
				switch( def.type )
				{
				case INTEGER:
					column.setCellRenderer( intRenderer );
					break;
				case DOUBLE:
					column.setCellRenderer( doubleRenderer );
					break;
				}
			}

			if( column != null )
			{
				column.setModelIndex( index );
				addColumn( column );
			}
		}
	}
}
