package org.andork.breakout.table;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.util.Date;

import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.RowSorter;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import org.andork.breakout.model.SurveyTableModel;
import org.andork.breakout.table.NewSurveyTableModel.CustomColumn;
import org.andork.breakout.table.NewSurveyTableModel.Row;
import org.andork.q.QObject;
import org.andork.swing.table.AnnotatingJTable;
import org.andork.swing.table.AnnotatingTableRowSorter;
import org.andork.swing.table.FormattedTextTableCellEditor;
import org.andork.swing.table.FormattedTextTableCellRenderer;
import org.andork.swing.table.MultiFormattedTextTableCellEditor;
import org.andork.swing.table.NiceTableModel.Column;
import org.andork.swing.table.NiceTableModel.FormattedTextColumn;
import org.andork.util.DateFormatWrapper;
import org.andork.util.FormatWarning;
import org.andork.util.FormattedText;

@SuppressWarnings( "serial" )
public class NewSurveyTable extends AnnotatingJTable
{
	private MultiFormattedTextTableCellEditor	shotEditor;
	private MultiFormattedTextTableCellEditor	xSectEditor;
	private MultiFormattedTextTableCellEditor	dateEditor;
	
	public NewSurveyTable( )
	{
		this( new NewSurveyTableModel( ) );
	}
	
	public NewSurveyTable( NewSurveyTableModel model )
	{
		super( model );
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
	}
	
	@Override
	public void setCellEditor( TableCellEditor anEditor )
	{
		super.setCellEditor( anEditor );
		if( anEditor instanceof MultiFormattedTextTableCellEditor )
		{
			( ( MultiFormattedTextTableCellEditor ) anEditor ).requestTextFieldFocus( );
		}
	}
	
	@Override
	public void createDefaultColumnsFromModel( )
	{
		super.createDefaultColumnsFromModel( );
		
		if( shotEditor == null )
		{
			shotEditor = new MultiFormattedTextTableCellEditor( new JTextField( ) );
			shotEditor.addFormat( new DistAzmIncMeasurementFormat( true ) ,
					"<html><b>Distance/Azimuth/Inclination (Uncorrected)</b><br>Examples:<br>\"15 183 -9\"<br>\"6.0 183/4.5 -9/10\"<br>\"15 -90\"</html>" , "DAIu" , null );
			shotEditor.addFormat( new DistAzmIncMeasurementFormat( false ) ,
					"<html><b>Distance/Azimuth/Inclination (Corrected)</b><br>Examples:<br>\"15 183 -9\"<br>\"6.0 183/184.5 -9/-10\"<br>\"15 -90\"</html>" , "DAIc" , null );
			shotEditor.addFormat( new NorthEastElevVectorMeasurementFormat( ) ,
					"<html><b>North/East/Elevation Offset</b><br><i>Positive elevation is up.</i><br>Example: \"150.0 -23.5 -5.0\"</html>" , "NEEv" , null );
			shotEditor.addFormat( new NorthEastDepthVectorMeasurementFormat( ) ,
					"<html><b>North/East/Depth Offset</b><br><i>Positive depth is down.</i><br>Example: \"150.0 -23.5 5.0\"</html>" , "NEDp" , null );
		}
		
		if( xSectEditor == null )
		{
			xSectEditor = new MultiFormattedTextTableCellEditor( new JTextField( ) );
			xSectEditor.addFormat( new BisectorLrudCrossSectionFormat( ) ,
					"<html><b>Bisector Left/Right/Up/Down</b><br>Example:<br>\"2 3 0 4\"</html>" , "bLRUD" , null );
			xSectEditor.addFormat( new PerpLrudCrossSectionFormat( ) ,
					"<html><b>Perpendicular Left/Right/Up/Down</b><br>Example:<br>\"2 3 0 4\"</html>" , "pLRUD" , null );
			xSectEditor.addFormat( new NsewCrossSectionFormat( ) ,
					"<html><b>North/South/East/West</b><br>Example:<br>\"2 3 0 4\"</html>" , "NSEW" , null );
			xSectEditor.addFormat( new LlrrudCrossSectionFormat( ) ,
					"<html><b>Left North/Left East/Right North/Right East/Up/Down</b><br>Example:<br>\"1.8 -2.9 -3.6 4.2 0 4\"</html>" , "LLRRUD" , null );
		}
		
		if( dateEditor == null )
		{
			dateEditor = new MultiFormattedTextTableCellEditor( new JTextField( ) );
			dateEditor.addFormat( new DateFormatWrapper( "yyyy-MM-dd" ) , "yyyy-mm-dd" , "" , null );
			dateEditor.addFormat( new DateFormatWrapper( "MM-dd-yyyy" ) , "mm-dd-yyyy" , "" , null );
			dateEditor.addFormat( new DateFormatWrapper( "yyyy/MM/dd" ) , "yyyy/mm/dd" , "" , null );
			dateEditor.addFormat( new DateFormatWrapper( "MM/dd/yyyy" ) , "mm/dd/yyyy" , "" , null );
		}
		
		NewSurveyTableModel model = getModel( );
		
		int modelIndex = 0;
		for( Column<QObject<Row>> column : model.getColumns( ) )
		{
			int viewIndex = convertColumnIndexToView( modelIndex );
			
			if( viewIndex >= 0 )
			{
				TableColumn tc = getColumnModel( ).getColumn( viewIndex );
				
				if( column instanceof CustomColumn )
				{
					column = ( ( CustomColumn ) column ).wrapped;
				}
				
				if( column instanceof FormattedTextColumn )
				{
					Class<?> valueClass = ( ( FormattedTextColumn ) column ).valueClass;
					
					if( valueClass == ShotMeasurement.class )
					{
						tc.setCellEditor( shotEditor );
					}
					else if( valueClass == CrossSection.class )
					{
						tc.setCellEditor( xSectEditor );
					}
					else if( valueClass == Date.class )
					{
						tc.setCellEditor( dateEditor );
					}
				}
			}
			modelIndex++ ;
		}
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
