package org.andork.breakout.table;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.RowSorter;
import javax.swing.table.TableModel;

import org.andork.breakout.model.SurveyTableModel;
import org.andork.breakout.table.NewSurveyTableModel.Row;
import org.andork.q.QObject;
import org.andork.swing.table.AnnotatingJTable;
import org.andork.swing.table.AnnotatingTableRowSorter;
import org.andork.swing.table.FormatAndDisplayInfo;
import org.andork.swing.table.FormattedTextTableCellEditor;
import org.andork.swing.table.FormattedTextTableCellRenderer;
import org.andork.swing.table.NiceTableModel.Column;
import org.andork.util.DateFormatWrapper;
import org.andork.util.FormatWarning;
import org.andork.util.FormattedText;

@SuppressWarnings( "serial" )
public class NewSurveyTable extends AnnotatingJTable
{
	public static final Map<Class<?>, List<FormatAndDisplayInfo<?>>>	formatMap;
	
	public static List<FormatAndDisplayInfo<?>> getAvailableFormats( Class<?> type )
	{
		return type != null && formatMap.containsKey( type ) ? formatMap.get( type ) : Collections.emptyList( );
	}
	
	public static FormatAndDisplayInfo<?> getDefaultFormat( Class<?> type )
	{
		return type != null && formatMap.containsKey( type ) ? formatMap.get( type ).get( 0 ) : null;
	}
	
	static
	{
		Map<Class<?>, List<FormatAndDisplayInfo<?>>> m = new HashMap<>( );
		
		List<FormatAndDisplayInfo<?>> l = new ArrayList<>( );
		l.add( new FormatAndDisplayInfo<>( new DistAzmIncMeasurementFormat( true ) ,
				"DAIu" ,
				"<html><b>Distance/Azimuth/Inclination (Uncorrected)</b><br>Examples:<br>\"15 183 -9\"<br>\"6.0 183/4.5 -9/10\"<br>\"15 -90\"</html>"
				, null ) );
		l.add( new FormatAndDisplayInfo<>( new DistAzmIncMeasurementFormat( false ) ,
				"DAIc" ,
				"<html><b>Distance/Azimuth/Inclination (Corrected)</b><br>Examples:<br>\"15 183 -9\"<br>\"6.0 183/184.5 -9/-10\"<br>\"15 -90\"</html>" ,
				null ) );
		l.add( new FormatAndDisplayInfo<>( new NorthEastElevVectorMeasurementFormat( ) ,
				"NEV" ,
				"<html><b>North/East/Elevation Offset</b><br><i>Positive elevation is up.</i><br>Example: \"150.0 -23.5 -5.0\"</html>" ,
				null ) );
		l.add( new FormatAndDisplayInfo<>( new NorthEastDepthVectorMeasurementFormat( ) ,
				"NED" ,
				"<html><b>North/East/Depth Offset</b><br><i>Positive depth is down.</i><br>Example: \"150.0 -23.5 5.0\"</html>" ,
				null ) );
		l.add( new FormatAndDisplayInfo<>( new NorthEastElevFixedToStationShotMeasurementFormat( ) ,
				"ToNEV" ,
				"<html><b>North/East/Elevation of To Station</b><br><i>Positive elevation is up.</i><br>Example: \"150.0 -23.5 -5.0\"</html>" ,
				null ) );
		l.add( new FormatAndDisplayInfo<>( new NorthEastDepthFixedToStationShotMeasurementFormat( ) ,
				"ToNED" ,
				"<html><b>North/East/Depth of To Station</b><br><i>Positive depth is down.</i><br>Example: \"150.0 -23.5 5.0\"</html>" ,
				null ) );
		m.put( ShotMeasurement.class , Collections.unmodifiableList( l ) );
		
		l = new ArrayList<>( );
		l.add( new FormatAndDisplayInfo<>( new BisectorLrudCrossSectionFormat( ) ,
				"bLRUD" ,
				"<html><b>Bisector Left/Right/Up/Down</b><br>Example:<br>\"2 3 0 4\"</html>" ,
				null ) );
		l.add( new FormatAndDisplayInfo<>( new PerpLrudCrossSectionFormat( ) ,
				"pLRUD" ,
				"<html><b>Perpendicular Left/Right/Up/Down</b><br>Example:<br>\"2 3 0 4\"</html>" ,
				null ) );
		l.add( new FormatAndDisplayInfo<>( new NsewCrossSectionFormat( ) ,
				"NSEW" ,
				"<html><b>North/South/East/West</b><br>Example:<br>\"2 3 0 4\"</html>" ,
				null ) );
		l.add( new FormatAndDisplayInfo<>( new LlrrudCrossSectionFormat( ) ,
				"LLRRUD" ,
				"<html><b>Left North/Left East/Right North/Right East/Up/Down</b><br>Example:<br>\"1.8 -2.9 -3.6 4.2 0 4\"</html>" ,
				null ) );
		m.put( CrossSection.class , Collections.unmodifiableList( l ) );
		
		l = new ArrayList<>( );
		l.add( new FormatAndDisplayInfo<>(
				new DateFormatWrapper( "yyyy-MM-dd" ) ,
				"yyyy-MM-dd" ,
				"yyyy-MM-dd" ,
				null ) );
		l.add( new FormatAndDisplayInfo<>(
				new DateFormatWrapper( "MM-dd-yyyy" ) ,
				"MM-dd-yyyy" ,
				"MM-dd-yyyy" ,
				null ) );
		l.add( new FormatAndDisplayInfo<>(
				new DateFormatWrapper( "yyyy/MM/dd" ) ,
				"yyyy/MM/dd" ,
				"yyyy/MM/dd" ,
				null ) );
		l.add( new FormatAndDisplayInfo<>(
				new DateFormatWrapper( "MM/dd/yyyy" ) ,
				"MM/dd/yyyy" ,
				"MM/dd/yyyy" ,
				null ) );
		m.put( Date.class , Collections.unmodifiableList( l ) );
		
		formatMap = Collections.unmodifiableMap( m );
	}
	
	public NewSurveyTable( )
	{
		this( new NewSurveyTableModel( ) );
	}
	
	public NewSurveyTable( NewSurveyTableModel model )
	{
		super( model );
		setRowHeight( 25 );
		setDefaultEditor( FormattedText.class , new FormattedTextTableCellEditor( new JTextField( ) ) );
		setDefaultRenderer( FormattedText.class , new FormattedTextTableCellRenderer(
				getDefaultRenderer( Object.class ) , f -> null , e -> {
					if( e instanceof FormatWarning )
					{
						return Color.YELLOW;
					}
					return Color.RED;
				} ) );
		setAutoCreateColumnsFromModel( true );
		setAutoResizeMode( JTable.AUTO_RESIZE_OFF );
	}
	
	@Override
	public void createDefaultColumnsFromModel( )
	{
		SurveyTableColumnModel columnModel;
		if( getColumnModel( ) instanceof SurveyTableColumnModel )
		{
			columnModel = ( SurveyTableColumnModel ) getColumnModel( );
		}
		else
		{
			columnModel = new SurveyTableColumnModel( );
		}
		
		if( getModel( ) instanceof NewSurveyTableModel )
		{
			columnModel.setColumnModels( ( ( NewSurveyTableModel ) getModel( ) ).getColumnModels( ) );
		}
		
		setColumnModel( columnModel );
	}
	
	public void setRowSorter( RowSorter<? extends TableModel> sorter )
	{
		super.setRowSorter( sorter );
		
		if( sorter instanceof AnnotatingTableRowSorter )
		{
			AnnotatingTableRowSorter<SurveyTableModel> aSorter = ( AnnotatingTableRowSorter<SurveyTableModel> ) sorter;
			
			NewSurveyTableModel model = getModel( );
			
			int modelIndex = 0;
			for( Column<QObject<Row>> column : model.getColumns( ) )
			{
				aSorter.setSortable( modelIndex , column.isSortable( ) );
				modelIndex++ ;
			}
		}
	}
	
	public NewSurveyTableModel getModel( )
	{
		return ( NewSurveyTableModel ) super.getModel( );
	}
	
	@Override
	protected boolean processKeyBinding( KeyStroke ks , KeyEvent e , int condition , boolean pressed )
	{
		if( e.getKeyCode( ) == KeyEvent.VK_DELETE )
		{
			getModel( ).removeRows( getSelectedRows( getModelSelectionModel( ) ) );
			
			return true;
		}
		return super.processKeyBinding( ks , e , condition , pressed );
	}
	
	public static int[ ] getSelectedRows( ListSelectionModel selectionModel )
	{
		int iMin = selectionModel.getMinSelectionIndex( );
		int iMax = selectionModel.getMaxSelectionIndex( );
		
		if( ( iMin == -1 ) || ( iMax == -1 ) )
		{
			return new int[ 0 ];
		}
		
		int[ ] rvTmp = new int[ 1 + ( iMax - iMin ) ];
		int n = 0;
		for( int i = iMin ; i <= iMax ; i++ )
		{
			if( selectionModel.isSelectedIndex( i ) )
			{
				rvTmp[ n++ ] = i;
			}
		}
		int[ ] rv = new int[ n ];
		System.arraycopy( rvTmp , 0 , rv , 0 , n );
		return rv;
	}
}
