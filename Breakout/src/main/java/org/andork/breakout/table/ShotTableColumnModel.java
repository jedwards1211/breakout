package org.andork.breakout.table;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

import javax.swing.DefaultCellEditor;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumn;

import org.andork.awt.AWTUtil;
import org.andork.bind2.Binder;
import org.andork.bind2.Binding;
import org.andork.i18n.I18n;
import org.andork.i18n.I18n.Localizer;
import org.andork.q2.QObject;
import org.andork.swing.list.FunctionListCellRenderer;
import org.andork.swing.table.DefaultSelectorCellEditor;
import org.andork.swing.table.DefaultSelectorTableCellRenderer;
import org.andork.unit.Angle;
import org.andork.unit.Length;
import org.andork.unit.Unit;
import org.andork.unit.UnitNameType;
import org.andork.unit.UnitNames;

@SuppressWarnings( "serial" )
public class ShotTableColumnModel extends DefaultTableColumnModel
{
	private final BiConsumer<JTable, Component>		monospaceModifier;

	public final TableColumn						fromColumn;
	public final TableColumn						toColumn;
	public final TableColumn						vectorColumn;
	public final TableColumn						xSectionAtFromColumn;
	public final TableColumn						xSectionAtToColumn;
	public final TableColumn						lengthUnitColumn;
	public final TableColumn						angleUnitColumn;

	private final Map<ShotColumnDef, TableColumn>	builtInColumns		= new HashMap<>( );

	private QObject<DataDefaults>					dataDefaults;

	private ShotDataFormatter						formats;

	private final Map<ParseStatus, Color>			noteColors;

	private final I18n								i18n;
	private final Localizer							localizer;

	private Predicate<Object>						parseErrorTest;
	private Function<Object, Color>					noteColorGetter;
	private Function<Object, String>				noteMessageGetter;

	private final Object							DEFAULT_ITEM		= new Object( );

	private final Function<Object, Object>			nullToDefaultItem	= o -> o == null ? DEFAULT_ITEM : o;
	private final Function<Object, Object>			defaultItemToNull	= o -> o == DEFAULT_ITEM ? null : o;

	public ShotTableColumnModel( I18n i18n , ShotDataFormatter formats )
	{
		this.i18n = i18n;
		localizer = i18n.forClass( ShotTableColumnModel.class );

		this.formats = formats;

		monospaceModifier = ( table , renderer ) ->
			renderer.setFont( new Font( "Monospaced" , Font.PLAIN , table.getFont( ).getSize( ) ) );

		noteColors = new HashMap<>( );
		noteColors.put( ParseStatus.WARNING , Color.YELLOW );
		noteColors.put( ParseStatus.ERROR , Color.RED );

		parseErrorTest =
			n -> n instanceof ParseNote && ( ( ParseNote ) n ).getStatus( ) == ParseStatus.ERROR;
		noteColorGetter =
			n -> n instanceof ParseNote ? noteColors.get( ( ( ParseNote ) n ).getStatus( ) ) : null;
		noteMessageGetter =
			n -> n instanceof ParseNote ? ( ( ParseNote ) n ).apply( i18n ) : null;

		fromColumn = createFromColumn( );
		toColumn = createToColumn( );
		vectorColumn = createVectorColumn( );
		xSectionAtFromColumn = createXSectionColumn( ShotColumnDef.xSectionAtFrom );
		xSectionAtToColumn = createXSectionColumn( ShotColumnDef.xSectionAtTo );
		lengthUnitColumn = createLengthUnitColumn( );
		angleUnitColumn = createAngleUnitColumn( );

		for( TableColumn column : Arrays.asList(
			fromColumn ,
			toColumn ,
			vectorColumn ,
			xSectionAtFromColumn ,
			xSectionAtToColumn ,
			lengthUnitColumn ,
			angleUnitColumn ) )
		{
			ShotColumnDef def = ( ShotColumnDef ) column.getIdentifier( );
			Binder<String> b = localizer.stringBinder( def.name );
			Binding nameBinding = f -> column.setHeaderValue( b.get( ) );
			b.addBinding( nameBinding );
			nameBinding.update( true );
			builtInColumns.put( def , column );
		}
	}

	private TableColumn createFromColumn( )
	{
		TableColumn result = new TableColumn( );
		result.setIdentifier( ShotColumnDef.fromStationName );
		result.setCellRenderer( new PostmodTableCellRenderer( new DefaultTableCellRenderer( ) , monospaceModifier ) );
		return result;
	}

	private TableColumn createToColumn( )
	{
		TableColumn result = new TableColumn( );
		result.setIdentifier( ShotColumnDef.toStationName );
		result.setCellRenderer( new PostmodTableCellRenderer( new DefaultTableCellRenderer( ) , monospaceModifier ) );
		result.setCellEditor( new PostmodCellEditor( new DefaultCellEditor( new JTextField( ) ) , monospaceModifier ) );
		return result;
	}

	private TableColumn createVectorColumn( )
	{
		Function<ShotVector, String> vectorValueFormatter =
			v -> v != null ? formats.format( v ) : null;
		Function<ShotVector, String> vectorValueRawFormatter =
			v -> v != null ? formats.formatRaw( v ) : null;

		ParsedTextTableCellRenderer<ShotVector> vectorValueRender =
			new ParsedTextTableCellRenderer<>( vectorValueFormatter , parseErrorTest , noteColorGetter ,
				noteMessageGetter );

		Function<Object, Object> vectorTypeGetter = p ->
		{
			if( p instanceof ParsedTextWithType )
			{
				return ( ShotVectorType ) ( ( ParsedTextWithType<?> ) p ).getType( );
			}
			if( dataDefaults != null )
			{
				return dataDefaults.get( DataDefaults.shotVectorType );
			}
			return null;
		};

		BiFunction<String, Object, ParsedTextWithType<ShotVector>> vectorParser = ( text , type ) ->
		{
			return formats.parseShotVector( text , ( ShotVectorType ) type );
		};

		CellRendererWithSelector vectorRenderer = new CellRendererWithSelector(
			new PostmodTableCellRenderer( vectorValueRender , monospaceModifier ) , vectorTypeGetter );
		vectorRenderer.selector( ).setAvailableValues( Arrays.asList( ShotVectorType.values( ) ) );

		FunctionListCellRenderer vectorTypeRenderer = new FunctionListCellRenderer(
			c -> c == null ? null : localizer.getString( c.toString( ) ) ,
			new DefaultListCellRenderer( ) );

		vectorRenderer.selector( ).comboBox( ).setRenderer( vectorTypeRenderer );

		ParsedTextWithTypeCellEditor<ShotVector> vectorEditor = new ParsedTextWithTypeCellEditor<>(
			vectorValueRawFormatter , vectorTypeGetter , vectorParser );
		vectorEditor.setAvailableTypes( Arrays.asList( ShotVectorType.values( ) ) );
		vectorEditor.typeSelector( ).comboBox( ).setRenderer( vectorTypeRenderer );

		TableColumn vectorColumn = new TableColumn( );
		vectorColumn.setIdentifier( ShotColumnDef.vector );
		vectorColumn.setCellRenderer( vectorRenderer );
		vectorColumn.setCellEditor( new PostmodCellEditor( vectorEditor , monospaceModifier ) );

		return vectorColumn;
	}

	private TableColumn createXSectionColumn( ShotColumnDef def )
	{
		Function<XSection, String> xSectionValueFormatter =
			v -> v != null ? formats.format( v ) : null;
		Function<XSection, String> xSectionValueRawFormatter =
			v -> v != null ? formats.formatRaw( v ) : null;

		ParsedTextTableCellRenderer<XSection> xSectionValueRender =
			new ParsedTextTableCellRenderer<>( xSectionValueFormatter , parseErrorTest , noteColorGetter ,
				noteMessageGetter );

		Function<Object, Object> xSectionTypeGetter = p ->
		{
			if( p instanceof ParsedTextWithType )
			{
				return ( XSectionType ) ( ( ParsedTextWithType<?> ) p ).getType( );
			}
			if( dataDefaults != null )
			{
				return dataDefaults.get( DataDefaults.xSectionType );
			}
			return null;
		};

		BiFunction<String, Object, ParsedTextWithType<XSection>> xSectionParser = ( text , type ) ->
		{
			return formats.parseXSection( text , ( XSectionType ) type );
		};

		CellRendererWithSelector xSectionRenderer = new CellRendererWithSelector(
			new PostmodTableCellRenderer( xSectionValueRender , monospaceModifier ) , xSectionTypeGetter );
		xSectionRenderer.selector( ).setAvailableValues( Arrays.asList( XSectionType.values( ) ) );

		FunctionListCellRenderer xSectionTypeRenderer = new FunctionListCellRenderer(
			c -> c == null ? null : localizer.getString( c.toString( ) ) ,
			new DefaultListCellRenderer( ) );

		xSectionRenderer.selector( ).comboBox( ).setRenderer( xSectionTypeRenderer );

		ParsedTextWithTypeCellEditor<XSection> xSectionEditor = new ParsedTextWithTypeCellEditor<>(
			xSectionValueRawFormatter , xSectionTypeGetter , xSectionParser );
		xSectionEditor.setAvailableTypes( Arrays.asList( XSectionType.values( ) ) );
		xSectionEditor.typeSelector( ).comboBox( ).setRenderer( xSectionTypeRenderer );

		TableColumn xSectionColumn = new TableColumn( );
		xSectionColumn.setIdentifier( def );
		xSectionColumn.setCellRenderer( xSectionRenderer );
		xSectionColumn.setCellEditor( new PostmodCellEditor( xSectionEditor , monospaceModifier ) );

		return xSectionColumn;
	}

	private TableColumn createLengthUnitColumn( )
	{
		TableColumn result = new TableColumn( );
		result.setIdentifier( ShotColumnDef.lengthUnit );

		ListCellRenderer unitRenderer = new FunctionListCellRenderer( o ->
		{
			if( o == DEFAULT_ITEM )
			{
				return localizer.getString( "defaultItem" );
			}
			if( o instanceof Unit )
			{
				Unit<Length> unit = ( Unit<Length> ) o;
				return UnitNames.getName( i18n.getLocale( ) , unit , 2 , UnitNameType.FULL );
			}
			return null;
		} , new DefaultListCellRenderer( ) );

		DefaultSelectorTableCellRenderer renderer = new DefaultSelectorTableCellRenderer( nullToDefaultItem );
		renderer.selector( ).setAvailableValues( Length.type.units( ) );
		renderer.selector( ).addAvailableValue( 0 , DEFAULT_ITEM );
		renderer.selector( ).comboBox( ).setRenderer( unitRenderer );
		result.setCellRenderer( renderer );

		DefaultSelectorCellEditor editor = new DefaultSelectorCellEditor( nullToDefaultItem , defaultItemToNull );
		editor.selector( ).setAvailableValues( Length.type.units( ) );
		editor.selector( ).addAvailableValue( 0 , DEFAULT_ITEM );
		editor.selector( ).comboBox( ).setRenderer( unitRenderer );
		result.setCellEditor( editor );

		return result;
	}

	private TableColumn createAngleUnitColumn( )
	{
		TableColumn result = new TableColumn( );
		result.setIdentifier( ShotColumnDef.angleUnit );

		ListCellRenderer unitRenderer = new FunctionListCellRenderer( o ->
		{
			if( o == DEFAULT_ITEM )
			{
				return localizer.getString( "defaultItem" );
			}
			if( o instanceof Unit )
			{
				Unit<Angle> unit = ( Unit<Angle> ) o;
				return UnitNames.getName( i18n.getLocale( ) , unit , 2 , UnitNameType.FULL );
			}
			return null;
		} , new DefaultListCellRenderer( ) );

		DefaultSelectorTableCellRenderer renderer = new DefaultSelectorTableCellRenderer( nullToDefaultItem );
		renderer.selector( ).setAvailableValues( Angle.type.units( ) );
		renderer.selector( ).addAvailableValue( 0 , DEFAULT_ITEM );
		renderer.selector( ).comboBox( ).setRenderer( unitRenderer );
		result.setCellRenderer( renderer );

		DefaultSelectorCellEditor editor = new DefaultSelectorCellEditor( nullToDefaultItem , defaultItemToNull );
		editor.selector( ).setAvailableValues( Angle.type.units( ) );
		editor.selector( ).addAvailableValue( 0 , DEFAULT_ITEM );
		editor.selector( ).comboBox( ).setRenderer( unitRenderer );
		result.setCellEditor( editor );

		return result;
	}

	private TableColumn createCustomColumn( ShotColumnDef def )
	{
		switch( def.type )
		{
		case BUILTIN:
			throw new IllegalArgumentException( "def must have a custom column type" );
		case STRING:
			return createCustomStringColumn( def );
		case INTEGER:
			return createCustomIntegerColumn( def );
		case DOUBLE:
			return createCustomDoubleColumn( def );
		default:
			return null;
		}
	}

	private TableColumn createCustomStringColumn( ShotColumnDef def )
	{
		TableColumn result = new TableColumn( );
		result.setIdentifier( def );
		result.setHeaderValue( def.name );
		result.setCellEditor( new DefaultCellEditor( new JTextField( ) ) );
		return result;
	}

	private TableColumn createCustomIntegerColumn( ShotColumnDef def )
	{
		TableColumn result = new TableColumn( );
		result.setIdentifier( def );
		result.setHeaderValue( def.name );

		Function<Integer, String> valueFormatter =
			v -> v != null ? formats.formatInteger( v ) : null;

		ParsedTextTableCellRenderer<Integer> renderer = new ParsedTextTableCellRenderer<>(
			valueFormatter , parseErrorTest , noteColorGetter , noteMessageGetter );
		renderer.setHorizontalAlignment( SwingConstants.RIGHT );
		result.setCellRenderer( new PostmodTableCellRenderer( renderer , monospaceModifier ) );

		ParsedTextCellEditor<Integer> editor = new ParsedTextCellEditor<>( formats::parseCustomInteger ,
			valueFormatter );
		result.setCellEditor( new PostmodCellEditor( editor , monospaceModifier ) );

		return result;
	}

	private TableColumn createCustomDoubleColumn( ShotColumnDef def )
	{
		TableColumn result = new TableColumn( );
		result.setIdentifier( def );
		result.setHeaderValue( def.name );

		Function<Double, String> valueFormatter =
			v -> v != null ? formats.formatDouble( v ) : null;

		ParsedTextTableCellRenderer<Double> renderer = new ParsedTextTableCellRenderer<>(
			valueFormatter , parseErrorTest , noteColorGetter , noteMessageGetter );
		renderer.setHorizontalAlignment( SwingConstants.RIGHT );
		result.setCellRenderer( new PostmodTableCellRenderer( renderer , monospaceModifier ) );

		ParsedTextCellEditor<Double> editor = new ParsedTextCellEditor<>( formats::parseCustomDouble ,
			valueFormatter );
		result.setCellEditor( new PostmodCellEditor( editor , monospaceModifier ) );

		return result;
	}

	public QObject<DataDefaults> getDataDefaults( )
	{
		return dataDefaults;
	}

	public void setDataDefaults( QObject<DataDefaults> dataDefaults )
	{
		this.dataDefaults = dataDefaults;
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
				column = createCustomColumn( def );
			}

			if( column != null )
			{
				column.setModelIndex( index );
				addColumn( column );
			}
		}
	}
}
