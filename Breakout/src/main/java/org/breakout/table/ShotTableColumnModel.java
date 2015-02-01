package org.breakout.table;

import java.util.Arrays;
import java.util.function.BiFunction;
import java.util.function.Function;

import javax.swing.DefaultCellEditor;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;

import org.andork.bind2.Binder;
import org.andork.bind2.Binding;
import org.andork.i18n.I18n;
import org.andork.swing.list.FunctionListCellRenderer;

@SuppressWarnings( "serial" )
public class ShotTableColumnModel extends SurveyDataTableColumnModel<Shot>
{
	public final TableColumn	fromColumn;
	public final TableColumn	toColumn;
	public final TableColumn	vectorColumn;
	public final TableColumn	xSectionAtFromColumn;
	public final TableColumn	xSectionAtToColumn;
	public final TableColumn	lengthUnitColumn;
	public final TableColumn	angleUnitColumn;
	public ShotTableColumnModel( I18n i18n , SurveyDataFormatter formats )
	{
		super( i18n , formats );

		fromColumn = createFromColumn( );
		toColumn = createToColumn( );
		vectorColumn = createVectorColumn( );
		xSectionAtFromColumn = createXSectionColumn( ShotColumnDefs.xSectionAtFrom );
		xSectionAtToColumn = createXSectionColumn( ShotColumnDefs.xSectionAtTo );
		lengthUnitColumn = createLengthUnitColumn( );
		angleUnitColumn = createAngleUnitColumn( ShotColumnDefs.angleUnit );

		for( TableColumn column : Arrays.asList(
			fromColumn ,
			toColumn ,
			vectorColumn ,
			xSectionAtFromColumn ,
			xSectionAtToColumn ,
			lengthUnitColumn ,
			angleUnitColumn ) )
		{
			SurveyDataColumnDef def = ( SurveyDataColumnDef ) column.getIdentifier( );
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
		result.setIdentifier( ShotColumnDefs.fromStationName );
		result
			.setCellRenderer( new MonospaceFontRenderer( new DefaultTableCellRenderer( ) ) );
		result.setCellEditor( new MonospaceFontEditor( new DefaultCellEditor( new JTextField( ) ) ) );
		return result;
	}

	private TableColumn createToColumn( )
	{
		TableColumn result = new TableColumn( );
		result.setIdentifier( ShotColumnDefs.toStationName );
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
		vectorColumn.setIdentifier( ShotColumnDefs.vector );
		vectorColumn.setCellRenderer( vectorRenderer );
		vectorColumn.setCellEditor( new MonospaceFontEditor( vectorEditor ) );

		return vectorColumn;
	}

	private TableColumn createXSectionColumn( SurveyDataColumnDef def )
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
}
