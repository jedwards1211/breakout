package org.breakout.table;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.net.URL;
import java.util.EventObject;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
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

import org.andork.i18n.I18n;
import org.andork.i18n.I18n.Localizer;
import org.andork.io.CSVFormat;
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
import org.andork.unit.UnitizedDouble;
import org.andork.util.StringUtils;

/**
 * The {@link TableColumnModel} for a {@link SurveyDataTable}. {@link SurveyDataTableColumnModel} and
 * {@link SurveyDataTableModel} form
 * the
 * presenter layer of an MVP pattern, where {@link SurveyDataList} is the model and {@link SurveyDataTable} is the view.
 * 
 * @author James
 */
@SuppressWarnings( "serial" )
public class SurveyDataTableColumnModel<R extends SurveyDataRow> extends DefaultTableColumnModel
{
	protected final Map<SurveyDataColumnDef, TableColumn>	builtInColumns		= new HashMap<>( );

	protected QObject<DataDefaults>							dataDefaults;

	protected SurveyDataFormatter							formats;

	protected final Map<ParseStatus, Color>					noteColors;

	protected final I18n									i18n;
	protected final Localizer								localizer;

	protected Predicate<? super ParsedText<?>>				forceShowText;
	protected Function<? super ParsedText<?>, Color>		backgroundColorFn;
	protected Function<? super ParsedText<?>, String>		messageFn;

	protected final Object									DEFAULT_ITEM		= new Object( );

	protected final Function<Object, Object>				nullToDefaultItem	= o -> o == null ? DEFAULT_ITEM : o;
	protected final Function<Object, Object>				defaultItemToNull	= o -> o == DEFAULT_ITEM ? null : o;

	public SurveyDataTableColumnModel( I18n i18n , SurveyDataFormatter formats )
	{
		this.i18n = i18n;
		localizer = i18n.forClass( SurveyDataTableColumnModel.class );

		this.formats = formats;

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
	}

	@SuppressWarnings( { "rawtypes" , "unchecked" } )
	protected TableColumn createLengthUnitColumn( )
	{
		TableColumn result = new TableColumn( );
		result.setIdentifier( ShotColumnDefs.lengthUnit );

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

	@SuppressWarnings( { "rawtypes" , "unchecked" } )
	protected TableColumn createAngleUnitColumn( SurveyDataColumnDef def )
	{
		TableColumn result = new TableColumn( );
		result.setIdentifier( def );

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

	protected TableColumn createCustomColumn( SurveyDataColumnDef def )
	{
		switch( def.type )
		{
		case BUILTIN:
			throw new IllegalArgumentException( "def must have a custom column type" );
		case STRING:
			return createStringColumn( def );
		case INTEGER:
			return createIntegerColumn( def );
		case DOUBLE:
			return createDoubleColumn( def );
		case TAGS:
			return createTagsColumn( def );
		case SECTION:
			return createSectionColumn( def );
		case LINK:
			return createLinkColumn( def );
		default:
			return null;
		}
	}

	protected TableColumn createStringColumn( SurveyDataColumnDef def )
	{
		TableColumn result = new TableColumn( );
		result.setIdentifier( def );
		result.setHeaderValue( def.name );
		result.setCellEditor( new DefaultCellEditor( new JTextField( ) ) );
		return result;
	}

	protected TableColumn createIntegerColumn( SurveyDataColumnDef def )
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

	protected TableColumn createDoubleColumn( SurveyDataColumnDef def )
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

	protected TableColumn createLengthColumn( SurveyDataColumnDef def )
	{
		TableColumn result = new TableColumn( );
		result.setIdentifier( def );
		result.setHeaderValue( def.name );

		Function<UnitizedDouble<Length>, String> valueFormatter =
			v -> v != null ? formats.formatLength( v ) : null;

		ParsedTextTableCellRenderer<UnitizedDouble<Length>> renderer = new ParsedTextTableCellRenderer<>(
			valueFormatter , forceShowText , backgroundColorFn , messageFn );
		renderer.setHorizontalAlignment( SwingConstants.RIGHT );
		result.setCellRenderer( new MonospaceFontRenderer( renderer ) );

		ParsedTextCellEditor<UnitizedDouble<Length>> editor = new ParsedTextCellEditor<>( valueFormatter ,
			formats::parseLength );
		result.setCellEditor( new MonospaceFontEditor( editor ) );

		return result;
	}

	protected TableColumn createTagsColumn( SurveyDataColumnDef def )
	{
		TableColumn result = new TableColumn( );
		result.setIdentifier( def );
		result.setHeaderValue( def.name );

		CSVFormat csv = new CSVFormat( );
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

	protected TableColumn createSectionColumn( SurveyDataColumnDef def )
	{
		TableColumn result = new TableColumn( );
		result.setIdentifier( def );
		result.setHeaderValue( def.name );

		CSVFormat csv = new CSVFormat( );
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

	protected TableColumn createLinkColumn( SurveyDataColumnDef def )
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

	public void update( SurveyDataTableModel<R> model , List<SurveyDataColumnDef> columnDefs )
	{
		while( getColumnCount( ) > 0 )
		{
			removeColumn( getColumn( 0 ) );
		}

		for( SurveyDataColumnDef def : columnDefs )
		{
			TableColumn column = null;
			int index = model.indexOfColumn( def );

			if( index < 0 )
			{
				continue;
			}

			if( def.type == SurveyDataColumnType.BUILTIN )
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
	protected static class MonospaceFontRenderer implements TableCellRenderer
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
	protected static class MonospaceFontEditor implements CellEditor , TableCellEditor
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
