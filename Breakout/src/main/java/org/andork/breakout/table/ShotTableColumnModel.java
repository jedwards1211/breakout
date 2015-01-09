package org.andork.breakout.table;

import java.awt.Color;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import org.andork.bind2.Binder;
import org.andork.bind2.Binding;
import org.andork.breakout.table.ShotVector.Dai.c;
import org.andork.breakout.table.ShotVector.Dai.u;
import org.andork.breakout.table.ShotVector.Nev.d;
import org.andork.breakout.table.ShotVector.Nev.el;
import org.andork.i18n.I18n;
import org.andork.i18n.I18n.Localizer;
import org.andork.swing.list.FunctionListCellRenderer;

@SuppressWarnings( "serial" )
public class ShotTableColumnModel extends DefaultTableColumnModel
{
	public final TableColumn								fromColumn;
	public final TableColumn								toColumn;
	public final TableColumn								vectorColumn;
	public final TableColumn								distColumn;
	public final TableColumn								azmFsBsColumn;
	public final TableColumn								azmFsColumn;
	public final TableColumn								azmBsColumn;
	public final TableColumn								incFsBsColumn;
	public final TableColumn								incFsColumn;
	public final TableColumn								incBsColumn;
	public final TableColumn								offsNColumn;
	public final TableColumn								offsEColumn;
	public final TableColumn								offsDColumn;

	private final Map<ShotColumnDef, TableColumn>			builtInColumns	= new HashMap<>( );

	private ShotTableFormats								formats;

	private IntFunction<String>								intFormatter	= Integer::toString;

	private final Map<ParseStatus, Color>					noteColors;

	private final Localizer									localizer;

	private TableCellRenderer								intRenderer;

	private Function<String, ParsedTextWithValue>			doubleParser;
	private TableCellRenderer								doubleRenderer;
	private TableCellEditor									doubleEditor;

	private TableCellRenderer								twoDoubleRenderer;
	private TableCellRenderer								lengthRenderer;
	private TableCellEditor									lengthEditor;
	private TableCellRenderer								angleRenderer;
	private TableCellEditor									angleEditor;
	private TableCellRenderer								azmPairRenderer;
	private TableCellRenderer								incPairRenderer;

	private Function<Object, Object>						vectorTypeGetter;
	private BiFunction<Object, String, ParsedTextWithValue>	vectorParser;
	private TableCellRenderer								vectorValueRender;
	private TypeTableCellRenderer							vectorRenderer;
	private TypedParsedTextWithValueCellEditor				vectorEditor;

	public ShotTableColumnModel( I18n i18n , ShotTableFormats formats )
	{
		localizer = i18n.forClass( ShotTableColumnModel.class );

		this.formats = formats;

		noteColors = new HashMap<>( );
		noteColors.put( ParseStatus.WARNING , Color.YELLOW );
		noteColors.put( ParseStatus.ERROR , Color.RED );

		Predicate<Object> isError =
			n -> n instanceof ParseNote && ( ( ParseNote ) n ).status == ParseStatus.ERROR;
		Function<Object, Color> noteColor =
			n -> n instanceof ParseNote ? noteColors.get( ( ( ParseNote ) n ).status ) : null;
		Function<Object, String> noteMessage =
			n -> n instanceof ParseNote ? ( ( ParseNote ) n ).apply( i18n ) : null;

		fromColumn = new TableColumn( );
		fromColumn.setIdentifier( ShotColumnDef.from );

		toColumn = new TableColumn( );
		toColumn.setIdentifier( ShotColumnDef.to );

		intRenderer =
			new ParsedTextWithValueTableCellRenderer(
				v -> v instanceof Integer ? intFormatter.apply( ( Integer ) v ) : null ,
				isError , noteColor , noteMessage );
		( ( JLabel ) intRenderer ).setHorizontalAlignment( JLabel.RIGHT );

		doubleRenderer =
			new ParsedTextWithValueTableCellRenderer(
				v -> v instanceof Double ? formats.formatDouble( ( Double ) v ) : null ,
				isError , noteColor , noteMessage );
		( ( JLabel ) doubleRenderer ).setHorizontalAlignment( JLabel.RIGHT );
		doubleParser = new DefaultParser( s -> formats.parseDouble( s ) );
		doubleEditor = new ParsedTextWithValueCellEditor( doubleParser , d -> formats.formatDouble( ( Double ) d ) );

		lengthRenderer =
			new ParsedTextWithValueTableCellRenderer(
				v -> v instanceof Double ? formats.formatLength( ( Double ) v ) : null ,
				isError , noteColor , noteMessage );
		( ( JLabel ) lengthRenderer ).setHorizontalAlignment( JLabel.RIGHT );

		lengthEditor = new ParsedTextWithValueCellEditor(
			new DefaultParser( s -> formats.parseDouble( s ) ) ,
			l -> formats.formatLength( ( Double ) l ) );

		angleRenderer =
			new ParsedTextWithValueTableCellRenderer(
				v -> v instanceof Double ? formats.formatAngle( ( Double ) v ) : null ,
				isError , noteColor , noteMessage );
		( ( JLabel ) angleRenderer ).setHorizontalAlignment( JLabel.RIGHT );
		angleEditor = new ParsedTextWithValueCellEditor(
			new DefaultParser( s -> formats.parseDouble( s ) ) ,
			l -> formats.formatLength( ( Double ) l ) );

		azmPairRenderer =
			new ParsedTextWithValueTableCellRenderer(
				v -> v instanceof Double[ ] ? formats.formatAzmPair( ( Double[ ] ) v ) : null ,
				isError , noteColor , noteMessage );
		( ( JLabel ) azmPairRenderer ).setHorizontalAlignment( JLabel.RIGHT );

		incPairRenderer =
			new ParsedTextWithValueTableCellRenderer(
				v -> v instanceof Double[ ] ? formats.formatIncPair( ( Double[ ] ) v ) : null ,
				isError , noteColor , noteMessage );
		( ( JLabel ) incPairRenderer ).setHorizontalAlignment( JLabel.RIGHT );

		Function<Object, String> vectorValueFormatter =
			v -> v instanceof ShotVector ? formats.format( ( ShotVector ) v ) : null;
		Function<Object, String> vectorValueRawFormatter =
			v -> v instanceof ShotVector ? formats.formatRaw( ( ShotVector ) v ) : null;

		vectorValueRender =
			new ParsedTextWithValueTableCellRenderer( vectorValueFormatter , isError , noteColor , noteMessage );
		vectorTypeGetter = p ->
		{
			if( p instanceof ParsedTextWithValue )
			{
				Object value = ( ( ParsedTextWithValue ) p ).value;
				return value != null ? value.getClass( ) : null;
			}
			return null;
		};

		vectorParser = ( type , text ) ->
		{
			ShotVector vector;
			try
			{
				vector = ( ( Class<? extends ShotVector> ) type ).newInstance( );
			}
			catch( Exception e )
			{
				e.printStackTrace( );
				return new ParsedTextWithValue( text , ParseNote.forMessageKey( ParseStatus.ERROR , "unexpected" ) ,
					null );
			}
			try
			{
				formats.parseVector( text , vector );
				return new ParsedTextWithValue( text , null , vector );
			}
			catch( ParseNote note )
			{
				return new ParsedTextWithValue( text , note , vector );
			}
		};

		vectorRenderer =
			new TypeTableCellRenderer( vectorValueRender , vectorTypeGetter );
		vectorRenderer.setAvailableTypes( Arrays.asList(
			c.class , u.class , d.class , el.class ) );
		vectorRenderer.typeSelector( ).getComboBox( ).setRenderer(
			new FunctionListCellRenderer(
				c -> c == null ? null : localizer.getString( ( ( Class<?> ) c ).getName( ) + ".abbrev" ) ,
				new DefaultListCellRenderer( ) ) );
		vectorEditor = new TypedParsedTextWithValueCellEditor(
			vectorValueRawFormatter ,
			vectorTypeGetter ,
			vectorParser );
		vectorEditor.setAvailableTypes( Arrays.asList(
			c.class , u.class , d.class , el.class ) );
		vectorEditor.typeSelector( ).getComboBox( ).setRenderer(
			new FunctionListCellRenderer(
				c -> c == null ? null : localizer.getString( ( ( Class<?> ) c ).getName( ) + ".abbrev" ) ,
				new DefaultListCellRenderer( ) ) );

		vectorColumn = new TableColumn( );
		vectorColumn.setIdentifier( ShotColumnDef.vector );
		vectorColumn.setCellRenderer( vectorRenderer );
		vectorColumn.setCellEditor( vectorEditor );

		distColumn = new TableColumn( );
		distColumn.setIdentifier( ShotColumnDef.dist );
		distColumn.setCellRenderer( lengthRenderer );
		distColumn.setCellEditor( lengthEditor );

		azmFsBsColumn = new TableColumn( );
		azmFsBsColumn.setIdentifier( ShotColumnDef.azmFsBs );
		azmFsBsColumn.setCellRenderer( azmPairRenderer );

		azmFsColumn = new TableColumn( );
		azmFsColumn.setIdentifier( ShotColumnDef.azmFs );
		azmFsColumn.setCellRenderer( angleRenderer );
		azmFsColumn.setCellEditor( angleEditor );

		azmBsColumn = new TableColumn( );
		azmBsColumn.setIdentifier( ShotColumnDef.azmBs );
		azmBsColumn.setCellRenderer( angleRenderer );
		azmBsColumn.setCellEditor( angleEditor );

		incFsBsColumn = new TableColumn( );
		incFsBsColumn.setIdentifier( ShotColumnDef.incFsBs );
		incFsBsColumn.setCellRenderer( incPairRenderer );

		incFsColumn = new TableColumn( );
		incFsColumn.setIdentifier( ShotColumnDef.incFs );
		incFsColumn.setCellRenderer( angleRenderer );
		incFsColumn.setCellEditor( angleEditor );

		incBsColumn = new TableColumn( );
		incBsColumn.setIdentifier( ShotColumnDef.incBs );
		incBsColumn.setCellRenderer( angleRenderer );
		incBsColumn.setCellEditor( angleEditor );

		offsNColumn = new TableColumn( );
		offsNColumn.setIdentifier( ShotColumnDef.offsN );
		offsNColumn.setCellRenderer( lengthRenderer );
		offsNColumn.setCellEditor( lengthEditor );

		offsEColumn = new TableColumn( );
		offsEColumn.setIdentifier( ShotColumnDef.offsE );
		offsEColumn.setCellRenderer( lengthRenderer );
		offsEColumn.setCellEditor( lengthEditor );

		offsDColumn = new TableColumn( );
		offsDColumn.setIdentifier( ShotColumnDef.offsV );
		offsDColumn.setCellRenderer( lengthRenderer );
		offsDColumn.setCellEditor( lengthEditor );

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
