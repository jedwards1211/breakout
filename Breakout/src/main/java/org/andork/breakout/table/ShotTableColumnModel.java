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
	private Function<DaiShotVector, String>			daiShotVectorFormatter;
	private Function<NevShotVector, String>			nedShotVectorFormatter;
	private Function<NevShotVector, String>			neelShotVectorFormatter;
	private Function<ShotVector, String>			shotVectorFormatter;

	private final Map<ParseStatus, Color>			noteColors;

	private final Localizer							localizer;

	private TableCellRenderer						intRenderer;
	private TableCellRenderer						doubleRenderer;
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
		Function<Object, String> noteMessage =
			n -> n instanceof ParseNote ? ( ( ParseNote ) n ).messageKey : null;

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

		azmFsBsColumn = new TableColumn( );
		azmFsBsColumn.setIdentifier( ShotColumnDef.azmFsBs );
		azmFsBsColumn.setCellRenderer( twoDoubleRenderer );

		azmFsColumn = new TableColumn( );
		azmFsColumn.setIdentifier( ShotColumnDef.azmFs );
		azmFsColumn.setCellRenderer( doubleRenderer );

		azmBsColumn = new TableColumn( );
		azmBsColumn.setIdentifier( ShotColumnDef.azmBs );
		azmBsColumn.setCellRenderer( doubleRenderer );

		incFsBsColumn = new TableColumn( );
		incFsBsColumn.setIdentifier( ShotColumnDef.incFsBs );
		incFsBsColumn.setCellRenderer( twoDoubleRenderer );

		incFsColumn = new TableColumn( );
		incFsColumn.setIdentifier( ShotColumnDef.incFs );
		incFsColumn.setCellRenderer( doubleRenderer );

		incBsColumn = new TableColumn( );
		incBsColumn.setIdentifier( ShotColumnDef.incBs );
		incBsColumn.setCellRenderer( doubleRenderer );

		offsNColumn = new TableColumn( );
		offsNColumn.setIdentifier( ShotColumnDef.offsN );
		offsNColumn.setCellRenderer( doubleRenderer );

		offsEColumn = new TableColumn( );
		offsEColumn.setIdentifier( ShotColumnDef.offsE );
		offsEColumn.setCellRenderer( doubleRenderer );

		offsDColumn = new TableColumn( );
		offsDColumn.setIdentifier( ShotColumnDef.offsD );
		offsDColumn.setCellRenderer( doubleRenderer );

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