package org.andork.breakout.table;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.net.URL;
import java.util.Arrays;
import java.util.EventObject;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

import javax.swing.CellEditor;
import javax.swing.DefaultCellEditor;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.SwingConstants;
import javax.swing.event.CellEditorListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import org.andork.bind2.Binder;
import org.andork.bind2.Binding;
import org.andork.i18n.I18n;
import org.andork.i18n.I18n.Localizer;
import org.andork.io.CSV;
import org.andork.q2.QObject;
import org.andork.swing.list.FunctionListCellRenderer;
import org.andork.swing.table.DefaultSelectorCellEditor;
import org.andork.swing.table.DefaultSelectorTableCellRenderer;
import org.andork.swing.table.FunctionCellEditor;
import org.andork.swing.table.FunctionTableCellRenderer;
import org.andork.unit.Angle;
import org.andork.unit.Length;
import org.andork.unit.Unit;
import org.andork.unit.UnitNameType;
import org.andork.unit.UnitNames;
import org.andork.util.StringUtils;

/**
 * The {@link TableColumnModel} for a {@link ShotTable}. {@link ShotTableColumnModel} and {@link ShotTableModel} form
 * the
 * presenter layer of an MVP pattern, where {@link ShotList} is the model and {@link ShotTable} is the view.
 * 
 * @author James
 */
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

	private Predicate<? super ParsedText<?>>		forceShowText;
	private Function<? super ParsedText<?>, Color>	backgroundColorFn;
	private Function<? super ParsedText<?>, String>	messageFn;

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

		forceShowText =
			p -> p.getNote( ) instanceof ParseNote && ( ( ParseNote ) p.getNote( ) ).getStatus( ) == ParseStatus.ERROR;
		backgroundColorFn =
			p -> p.getNote( ) instanceof ParseNote ? noteColors.get( ( ( ParseNote ) p.getNote( ) ).getStatus( ) )
				: null;
		messageFn =
			p -> p.getNote( ) instanceof ParseNote ? ( ( ParseNote ) p.getNote( ) ).apply( i18n ) : null;

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
		result
			.setCellRenderer( new MonospaceFontRenderer( new DefaultTableCellRenderer( ) ) );
		return result;
	}

	private TableColumn createToColumn( )
	{
		TableColumn result = new TableColumn( );
		result.setIdentifier( ShotColumnDef.toStationName );
		result
			.setCellRenderer( new MonospaceFontRenderer( new DefaultTableCellRenderer( ) ) );
		result.setCellEditor( new MonospaceFontEditor( new DefaultCellEditor( new JTextField( ) ) ) );
		return result;
	}

	private TableColumn createVectorColumn( )
	{
		Function<ShotVector, String> vectorValueFormatter =
			v -> v != null ? formats.format( v ) : null;
		Function<ShotVector, String> vectorValueRawFormatter =
			v -> v != null ? formats.formatRaw( v ) : null;

		ParsedTextTableCellRenderer<ShotVector> vectorValueRender =
			new ParsedTextTableCellRenderer<ShotVector>( vectorValueFormatter , forceShowText , backgroundColorFn ,
				messageFn );

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

		TableCellRendererWithSelector vectorRenderer = new TableCellRendererWithSelector(
			new MonospaceFontRenderer( vectorValueRender ) , vectorTypeGetter );
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
		vectorColumn.setCellEditor( new MonospaceFontEditor( vectorEditor ) );

		return vectorColumn;
	}

	private TableColumn createXSectionColumn( ShotColumnDef def )
	{
		Function<XSection, String> xSectionValueFormatter =
			v -> v != null ? formats.format( v ) : null;
		Function<XSection, String> xSectionValueRawFormatter =
			v -> v != null ? formats.formatRaw( v ) : null;

		ParsedTextTableCellRenderer<XSection> xSectionValueRender =
			new ParsedTextTableCellRenderer<>( xSectionValueFormatter , forceShowText , backgroundColorFn ,
				messageFn );

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

		TableCellRendererWithSelector xSectionRenderer = new TableCellRendererWithSelector(
			new MonospaceFontRenderer( xSectionValueRender ) , xSectionTypeGetter );
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
		xSectionColumn.setCellEditor( new MonospaceFontEditor( xSectionEditor ) );

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
		case TAGS:
			return createCustomTagsColumn( def );
		case SECTION:
			return createCustomSectionColumn( def );
		case LINK:
			return createCustomLinkColumn( def );
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
			valueFormatter , forceShowText , backgroundColorFn , messageFn );
		renderer.setHorizontalAlignment( SwingConstants.RIGHT );
		result.setCellRenderer( new MonospaceFontRenderer( renderer ) );

		ParsedTextCellEditor<Integer> editor = new ParsedTextCellEditor<>( valueFormatter ,
			formats::parseCustomInteger );
		result.setCellEditor( new MonospaceFontEditor( editor ) );

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
			valueFormatter , forceShowText , backgroundColorFn , messageFn );
		renderer.setHorizontalAlignment( SwingConstants.RIGHT );
		result.setCellRenderer( new MonospaceFontRenderer( renderer ) );

		ParsedTextCellEditor<Double> editor = new ParsedTextCellEditor<>( valueFormatter ,
			formats::parseCustomDouble );
		result.setCellEditor( new MonospaceFontEditor( editor ) );

		return result;
	}

	private TableColumn createCustomTagsColumn( ShotColumnDef def )
	{
		TableColumn result = new TableColumn( );
		result.setIdentifier( def );
		result.setHeaderValue( def.name );

		CSV csv = new CSV( );
		csv.trimWhitespace( true );

		Function<LinkedHashSet<String>, String> valueFormatter =
			v -> v != null ? csv.formatLine( v ) : null;
		Function<String, LinkedHashSet<String>> valueParser =
			v ->
			{
				if( StringUtils.isNullOrEmpty( v ) )
				{
					return null;
				}
				LinkedHashSet<String> set = new LinkedHashSet<>( );
				csv.parseLine( v , set );
				return set;
			};

		CollectionTableCellRenderer<String> renderer = new CollectionTableCellRenderer<String>(
			StringUtils::toStringOrNull );
		result.setCellRenderer( renderer );

		FunctionCellEditor editor = new FunctionCellEditor( new DefaultCellEditor( new JTextField( ) ) ,
			valueFormatter , valueParser );
		result.setCellEditor( editor );

		return result;
	}

	private TableColumn createCustomSectionColumn( ShotColumnDef def )
	{
		TableColumn result = new TableColumn( );
		result.setIdentifier( def );
		result.setHeaderValue( def.name );

		CSV csv = new CSV( );
		csv.trimWhitespace( true );
		csv.separator( '/' );

		Function<List<String>, String> valueFormatter =
			v -> v != null ? csv.formatLine( v ) : null;
		Function<String, List<String>> valueParser =
			v ->
			{
				if( StringUtils.isNullOrEmpty( v ) )
				{
					return null;
				}
				return csv.parseLine( v );
			};

		FunctionTableCellRenderer renderer = new FunctionTableCellRenderer( valueFormatter ,
			new DefaultTableCellRenderer( ) );
		result.setCellRenderer( renderer );

		FunctionCellEditor editor = new FunctionCellEditor( new DefaultCellEditor( new JTextField( ) ) ,
			valueFormatter , valueParser );
		result.setCellEditor( editor );

		return result;
	}

	private TableColumn createCustomLinkColumn( ShotColumnDef def )
	{
		TableColumn result = new TableColumn( );
		result.setIdentifier( def );
		result.setHeaderValue( def.name );

		HyperlinkTableCellRenderer renderer = new HyperlinkTableCellRenderer(
			i18n , StringUtils::toStringOrNull , forceShowText , backgroundColorFn , messageFn );
		result.setCellRenderer( renderer );

		ParsedTextCellEditor<URL> editor = new ParsedTextCellEditor<>( StringUtils::toStringOrNull ,
			formats::parseUrl );
		result.setCellEditor( editor );

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

	/**
	 * Wraps a {@link TableCellRenderer} and converts its font to Monospaced. If the table's font is resized, this
	 * renderer will pick up the new size but still use a Monospaced font.
	 * 
	 * @author James
	 */
	private static class MonospaceFontRenderer implements TableCellRenderer
	{
		TableCellRenderer	wrapped;

		public MonospaceFontRenderer( TableCellRenderer wrapped )
		{
			super( );
			this.wrapped = wrapped;
		}

		@Override
		public Component getTableCellRendererComponent( JTable table , Object value , boolean isSelected ,
			boolean hasFocus , int row , int column )
		{
			Component renderer = wrapped.getTableCellRendererComponent( table , value , isSelected , hasFocus , row ,
				column );
			renderer.setFont( new Font( "Monospaced" , Font.PLAIN , table.getFont( ).getSize( ) ) );
			return renderer;
		}
	}

	/**
	 * Wraps a {@link CellEditor} and converts its font to Monospaced. If the table's font is resized, this
	 * renderer will pick up the new size but still use a Monospaced font.
	 * 
	 * @author James
	 */
	private static class MonospaceFontEditor implements CellEditor , TableCellEditor
	{
		CellEditor	wrapped;

		public MonospaceFontEditor( CellEditor wrapped )
		{
			super( );
			this.wrapped = wrapped;
		}

		@Override
		public Component getTableCellEditorComponent( JTable table , Object value , boolean isSelected , int row ,
			int column )
		{
			Component editor = ( ( TableCellEditor ) wrapped ).getTableCellEditorComponent( table , value , isSelected ,
				row , column );
			editor.setFont( new Font( "Monospaced" , Font.PLAIN , table.getFont( ).getSize( ) ) );
			return editor;
		}

		@Override
		public Object getCellEditorValue( )
		{
			return wrapped.getCellEditorValue( );
		}

		@Override
		public boolean isCellEditable( EventObject anEvent )
		{
			return wrapped.isCellEditable( anEvent );
		}

		@Override
		public boolean shouldSelectCell( EventObject anEvent )
		{
			return wrapped.shouldSelectCell( anEvent );
		}

		@Override
		public boolean stopCellEditing( )
		{
			return wrapped.stopCellEditing( );
		}

		@Override
		public void cancelCellEditing( )
		{
			wrapped.cancelCellEditing( );
		}

		@Override
		public void addCellEditorListener( CellEditorListener l )
		{
			wrapped.addCellEditorListener( l );
		}

		@Override
		public void removeCellEditorListener( CellEditorListener l )
		{
			wrapped.removeCellEditorListener( l );
		}
	}
}
