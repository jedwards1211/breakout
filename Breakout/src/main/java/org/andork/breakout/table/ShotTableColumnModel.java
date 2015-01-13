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
import javax.swing.ListCellRenderer;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import org.andork.bind2.Binder;
import org.andork.bind2.Binding;
import org.andork.i18n.I18n;
import org.andork.i18n.I18n.Localizer;
import org.andork.q2.QObject;
import org.andork.swing.list.FunctionListCellRenderer;

@SuppressWarnings( "serial" )
public class ShotTableColumnModel extends DefaultTableColumnModel
{
	public final TableColumn											fromColumn;
	public final TableColumn											toColumn;
	public final TableColumn											vectorColumn;
	private final Map<ShotColumnDef, TableColumn>						builtInColumns	= new HashMap<>( );

	private QObject<DataDefaults>										dataDefaults;

	private ShotDataFormatter											formats;

	private IntFunction<String>											intFormatter	= Integer::toString;

	private final Map<ParseStatus, Color>								noteColors;

	private final Localizer												localizer;

	private TableCellRenderer											intRenderer;

	private Function<Object, ShotVectorType>							vectorTypeGetter;
	private ListCellRenderer<Object>									vectorTypeRenderer;
	private BiFunction<String, Object, ParsedTextWithType<ShotVector>>	vectorParser;
	private TableCellRenderer											vectorValueRender;
	private TypedTableCellRenderer										vectorRenderer;
	private TypedParsedTextCellEditor<ShotVector>						vectorEditor;

	private Function<Object, XSectionType>								xSectionTypeGetter;
	private ListCellRenderer<Object>									xSectionTypeRenderer;
	private BiFunction<String, Object, ParsedTextWithType<XSection>>	xSectionParser;
	private TableCellRenderer											xSectionValueRender;
	private TypedTableCellRenderer										xSectionRenderer;
	private TypedParsedTextCellEditor<XSection>							xSectionEditor;

	public ShotTableColumnModel( I18n i18n , ShotDataFormatter formats )
	{
		localizer = i18n.forClass( ShotTableColumnModel.class );

		this.formats = formats;

		noteColors = new HashMap<>( );
		noteColors.put( ParseStatus.WARNING , Color.YELLOW );
		noteColors.put( ParseStatus.ERROR , Color.RED );

		Predicate<Object> parseErrorTest =
			n -> n instanceof ParseNote && ( ( ParseNote ) n ).getStatus( ) == ParseStatus.ERROR;
		Function<Object, Color> noteColorGetter =
			n -> n instanceof ParseNote ? noteColors.get( ( ( ParseNote ) n ).getStatus( ) ) : null;
		Function<Object, String> noteMessageGetter =
			n -> n instanceof ParseNote ? ( ( ParseNote ) n ).apply( i18n ) : null;

		fromColumn = new TableColumn( );
		fromColumn.setIdentifier( ShotColumnDef.fromStationName );

		toColumn = new TableColumn( );
		toColumn.setIdentifier( ShotColumnDef.toStationName );

		intRenderer =
			new ParsedTextTableCellRenderer(
				v -> v instanceof Integer ? intFormatter.apply( ( Integer ) v ) : null ,
				parseErrorTest , noteColorGetter , noteMessageGetter );
		( ( JLabel ) intRenderer ).setHorizontalAlignment( JLabel.RIGHT );

		Function<ShotVector, String> vectorValueFormatter =
			v -> v != null ? formats.format( v ) : null;
		Function<ShotVector, String> vectorValueRawFormatter =
			v -> v != null ? formats.formatRaw( v ) : null;

		vectorValueRender =
			new ParsedTextTableCellRenderer<ShotVector>( vectorValueFormatter , parseErrorTest , noteColorGetter ,
				noteMessageGetter );

		vectorTypeGetter = p ->
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

		vectorParser = ( text , type ) ->
		{
			return formats.parseShotVector( text , ( ShotVectorType ) type );
		};

		vectorRenderer = new TypedTableCellRenderer( vectorValueRender , vectorTypeGetter );
		vectorRenderer.setAvailableTypes( Arrays.asList( ShotVectorType.values( ) ) );

		vectorTypeRenderer = new FunctionListCellRenderer(
			c -> c == null ? null : localizer.getString( c.toString( ) ) ,
			new DefaultListCellRenderer( ) );

		vectorRenderer.typeSelector( ).getComboBox( ).setRenderer( vectorTypeRenderer );

		vectorEditor = new TypedParsedTextCellEditor<ShotVector>(
			vectorValueRawFormatter , vectorTypeGetter , vectorParser );
		vectorEditor.setAvailableTypes( Arrays.asList( ShotVectorType.values( ) ) );
		vectorEditor.typeSelector( ).getComboBox( ).setRenderer( vectorTypeRenderer );

		vectorColumn = new TableColumn( );
		vectorColumn.setIdentifier( ShotColumnDef.vector );
		vectorColumn.setCellRenderer( vectorRenderer );
		vectorColumn.setCellEditor( vectorEditor );

//		Function<XSection, String> xSectionValueFormatter =
//			v -> v != null ? formats.format( v ) : null;
//		Function<XSection, String> xSectionValueRawFormatter =
//			v -> v != null ? formats.formatRaw( v ) : null;
//
//		xSectionValueRender =
//			new ParsedTextTableCellRenderer<XSection>( xSectionValueFormatter , parseErrorTest , noteColorGetter ,
//				noteMessageGetter );
//
//		xSectionTypeGetter = p ->
//		{
//			if( p instanceof ParsedTextWithType )
//			{
//				return ( XSectionType ) ( ( ParsedTextWithType<?> ) p ).getType( );
//			}
//			if( dataDefaults != null )
//			{
//				return dataDefaults.get( DataDefaults.shotVectorType );
//			}
//			return null;
//		};
//
//		xSectionParser = ( text , type ) ->
//		{
//			return formats.parseXSection( text , ( XSectionType ) type );
//		};
//
//		xSectionRenderer = new TypedTableCellRenderer( xSectionValueRender , xSectionTypeGetter );
//		xSectionRenderer.setAvailableTypes( Arrays.asList( XSectionType.values( ) ) );
//
//		xSectionTypeRenderer = new FunctionListCellRenderer(
//			c -> c == null ? null : localizer.getString( c.toString( ) ) ,
//			new DefaultListCellRenderer( ) );
//
//		xSectionRenderer.typeSelector( ).getComboBox( ).setRenderer( xSectionTypeRenderer );
//
//		xSectionEditor = new TypedParsedTextCellEditor<XSection>(
//			xSectionValueRawFormatter , xSectionTypeGetter , xSectionParser );
//		xSectionEditor.setAvailableTypes( Arrays.asList( XSectionType.values( ) ) );
//		xSectionEditor.typeSelector( ).getComboBox( ).setRenderer( xSectionTypeRenderer );
//
//		xSectionColumn = new TableColumn( );
//		xSectionColumn.setIdentifier( ShotColumnDef.xSection );
//		xSectionColumn.setCellRenderer( xSectionRenderer );
//		xSectionColumn.setCellEditor( xSectionEditor );

		for( TableColumn column : Arrays.asList(
			fromColumn ,
			toColumn ,
			vectorColumn ) )
		{
			ShotColumnDef def = ( ShotColumnDef ) column.getIdentifier( );
			Binder<String> b = localizer.stringBinder( def.name );
			Binding nameBinding = f -> column.setHeaderValue( b.get( ) );
			b.addBinding( nameBinding );
			nameBinding.update( true );
			builtInColumns.put( def , column );
		}
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
				column = new TableColumn( );
				column.setHeaderValue( def.name );
				switch( def.type )
				{
				case INTEGER:
					column.setCellRenderer( intRenderer );
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
