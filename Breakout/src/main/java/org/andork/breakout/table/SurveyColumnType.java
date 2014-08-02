package org.andork.breakout.table;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.andork.breakout.table.NewSurveyTableModel.Row;
import org.andork.collect.CollectionUtils;
import org.andork.format.CollectionFormat;
import org.andork.format.DateFormatWrapper;
import org.andork.format.FormattedText;
import org.andork.format.StringFormat;
import org.andork.q.QArrayList;
import org.andork.q.QLinkedHashMap;
import org.andork.q.QLinkedHashSet;
import org.andork.q.QObject;
import org.andork.swing.FormatAndDisplayInfo;
import org.andork.swing.table.NiceTableModel.Column;
import org.andork.swing.table.NiceTableModel.FormattedTextColumn;
import org.andork.swing.table.NiceTableModel.MapColumn;
import org.andork.swing.table.NiceTableModel.QObjectColumn;
import org.andork.util.StringUtils;

public enum SurveyColumnType
{
	STRING( "Text" , String.class , StringUtils.multiply( "m" , 20 ) , Collections.emptyList( ) )
	{
		public Column<QObject<Row>> createCustomColumn( String name )
		{
			return MapColumn.newInstance(
					QObjectColumn.newInstance( Row.spec , Row.customAttrs ) ,
					name ,
					String.class ,
					( ) -> QLinkedHashMap.newInstance( ) );
		}
		
	} ,
	INTEGER( "Integer" , Integer.class , Integer.MAX_VALUE , Collections.emptyList( ) ) ,
	REAL_NUMBER( "Real Number" , Double.class , Double.MAX_VALUE , Collections.emptyList( ) ) ,
	DATE( "Date" ,
			Date.class ,
			Collections.unmodifiableList( Arrays.asList(
					new FormatAndDisplayInfo<>(
							new DateFormatWrapper( "yyyy-MM-dd" ) ,
							"yyyy-MM-dd" ,
							"yyyy-MM-dd" ,
							null ) ,
					new FormatAndDisplayInfo<>(
							new DateFormatWrapper( "MM-dd-yyyy" ) ,
							"MM-dd-yyyy" ,
							"MM-dd-yyyy" ,
							null ) ,
					new FormatAndDisplayInfo<>(
							new DateFormatWrapper( "yyyy/MM/dd" ) ,
							"yyyy/MM/dd" ,
							"yyyy/MM/dd" ,
							null ) ,
					new FormatAndDisplayInfo<>(
							new DateFormatWrapper( "MM/dd/yyyy" ) ,
							"MM/dd/yyyy" ,
							"MM/dd/yyyy" ,
							null )
					) ) ,
			"2011-11-30" ,
			0 ) ,
	SHOT_MEASUREMENT(
			"Shot Measurement" ,
			ShotMeasurement.class ,
			Collections.unmodifiableList( Arrays.asList(
					new FormatAndDisplayInfo<>( new DistAzmIncMeasurementFormat( true ) ,
							"DAIu" ,
							"<html><b>Distance/Azimuth/Inclination (Uncorrected)</b><br>Examples:<i><br>5 183 -9<br>6.0 183/4.5 -9/10<br>15 -90</i></html>"
							, null ) ,
					new FormatAndDisplayInfo<>( new DistAzmIncMeasurementFormat( false ) ,
							"DAIc" ,
							"<html><b>Distance/Azimuth/Inclination (Corrected)</b><br>Examples:<i><br>15 183 -9<br>6.0 183/184.5 -9/-10<br>15 -90</i></html>" ,
							null ) ,
					new FormatAndDisplayInfo<>( new NorthEastElevVectorMeasurementFormat( ) ,
							"NEV" ,
							"<html><b>North/East/Elevation Offset</b><br><i>Positive elevation is up.</i><br>Example: <i>150.0 -23.5 -5.0</i></html>" ,
							null ) ,
					new FormatAndDisplayInfo<>( new NorthEastDepthVectorMeasurementFormat( ) ,
							"NED" ,
							"<html><b>North/East/Depth Offset</b><br><i>Positive depth is down.</i><br>Example: <i>150.0 -23.5 5.0</i></html>" ,
							null ) ,
					new FormatAndDisplayInfo<>( new NorthEastElevFixedToStationShotMeasurementFormat( ) ,
							"ToNEV" ,
							"<html><b>North/East/Elevation of To Station</b><br><i>Positive elevation is up.</i><br>Example: <i>150.0 -23.5 -5.0</i></html>" ,
							null ) ,
					new FormatAndDisplayInfo<>( new NorthEastDepthFixedToStationShotMeasurementFormat( ) ,
							"ToNED" ,
							"<html><b>North/East/Depth of To Station</b><br><i>Positive depth is down.</i><br>Example: <i>150.0 -23.5 5.0</i></html>" ,
							null )
					) ) ,
			"100.25 359.25/359.25 -89.25/-89.25" ,
			0 ) ,
	CROSS_SECTION(
			"Cross Section" ,
			CrossSection.class ,
			Collections.unmodifiableList( Arrays.asList(
					new FormatAndDisplayInfo<>( new BisectorLrudCrossSectionFormat( ) ,
							"bLRUD" ,
							"<html><b>Bisector Left/Right/Up/Down</b><br>Example: <i>2 3 0 4</i></html>" ,
							null ) ,
					new FormatAndDisplayInfo<>( new PerpLrudCrossSectionFormat( ) ,
							"pLRUD" ,
							"<html><b>Perpendicular Left/Right/Up/Down</b><br>Example: <i>2 3 0 4</i></html>" ,
							null ) ,
					new FormatAndDisplayInfo<>( new NsewCrossSectionFormat( ) ,
							"NSEW" ,
							"<html><b>North/South/East/West</b><br>Example: <i>2 3 0 4</i></html>" ,
							null ) ,
					new FormatAndDisplayInfo<>( new LlrrudCrossSectionFormat( ) ,
							"LLRRUD" ,
							"<html><b>Left North/Left East/Right North/Right East/Up/Down</b><br>Example: <i>1.8 -2.9 -3.6 4.2 0 4</i></html>" ,
							null )
					) ) ,
			"100.25 100.25 100.25 100.25 100.25 100.25" ,
			2 ) ,
	TAGS(
			"Tags" ,
			QLinkedHashSet.class ,
			Collections.unmodifiableList( Arrays.asList(
					new FormatAndDisplayInfo<>( new CollectionFormat<String, QLinkedHashSet<String>>( ',' , StringFormat.instance ,
							( ) -> QLinkedHashSet.<String>newInstance( ) ) ,
							"a,b,..." ,
							"<html><b>Comma-Separated List</b><br>Example: <i>Has Water, \"\"\"Awesome\"\" Alley\", Narrow Canyon</i></html>" ,
							null ) ,
					new FormatAndDisplayInfo<>( new CollectionFormat<String, QLinkedHashSet<String>>( ' ' , StringFormat.instance ,
							( ) -> QLinkedHashSet.<String>newInstance( ) ) ,
							"a b ..." ,
							"<html><b>Space-Separated List</b><br>Example: <i>\"Has Water\" \"\"\"Awesome\"\" Alley\" \"Narrow Canyon\"</i></html>" ,
							null )
					) ) ,
			"this, is, a, set, of, tags" ,
			0 ) ,
	PATH(
			"Path" ,
			QLinkedHashSet.class ,
			Collections.unmodifiableList( Arrays.asList(
					new FormatAndDisplayInfo<>( new CollectionFormat<String, QArrayList<String>>( '/' , " / " , StringFormat.instance ,
							( ) -> QArrayList.<String>newInstance( ) ) ,
							"a/b/..." ,
							"<html><b>Slash-Delimited Path</b><br>Example: <i>Northtown/Eveready Canyon Area/WB$</i></html>" ,
							null )
					) ) ,
			"this/is/a/path/with/lots/of/elements" ,
			0 );
	
	public Column<QObject<Row>> createCustomColumn( String id )
	{
		return createColumn( MapColumn.newInstance(
				QObjectColumn.newInstance( Row.spec , Row.customAttrs ) ,
				id ,
				FormattedText.class ,
				( ) -> QLinkedHashMap.newInstance( ) ) );
	}
	
	public Column<QObject<Row>> createColumn( Column<QObject<Row>> wrapped )
	{
		return FormattedTextColumn.newInstance(
				wrapped ,
				valueClass ,
				( ) -> new FormattedText( defaultFormat ) );
	}
	
	public final String										displayName;
	public final Class<?>									valueClass;
	public final Object										prototypeValue;
	public final List<FormatAndDisplayInfo<?>>				availableFormats;
	public final FormatAndDisplayInfo<?>					defaultFormat;
	
	private static final Map<Class<?>, SurveyColumnType>	valueClassMap;
	
	static
	{
		valueClassMap = Collections.unmodifiableMap( CollectionUtils.keyify( Arrays.asList( values( ) ).stream( ) ,
				type -> type.valueClass ) );
	}
	
	public static SurveyColumnType fromValueClass( Class<?> valueClass )
	{
		return valueClassMap.get( valueClass );
	}
	
	private SurveyColumnType( String displayName , Class<?> valueClass , Object prototypeValue , List<FormatAndDisplayInfo<?>> availableFormats )
	{
		this.displayName = displayName;
		this.valueClass = valueClass;
		this.prototypeValue = prototypeValue;
		this.availableFormats = availableFormats;
		this.defaultFormat = availableFormats.isEmpty( ) ? null : availableFormats.get( 0 );
	}
	
	private SurveyColumnType( String displayName , Class<?> valueClass , List<FormatAndDisplayInfo<?>> availableFormats , String prototypeValueText , int prototypeFormatIndex )
	{
		this.displayName = displayName;
		this.valueClass = valueClass;
		this.availableFormats = availableFormats;
		this.defaultFormat = availableFormats.isEmpty( ) ? null : availableFormats.get( 0 );
		this.prototypeValue = new FormattedText( prototypeValueText , availableFormats.get( prototypeFormatIndex ) );
	}
	
	public String toString( )
	{
		return displayName;
	}
}