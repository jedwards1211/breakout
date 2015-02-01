package org.breakout.table;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JTable;
import javax.swing.RowFilter.Entry;

import org.andork.bind2.DefaultBinder;
import org.andork.i18n.I18n;
import org.andork.q2.QArrayObject;
import org.andork.q2.QObject;
import org.andork.q2.QObjectBinder;
import org.andork.swing.OnEDT;
import org.andork.swing.QuickTestFrame;
import org.andork.swing.RowAnnotator;
import org.andork.swing.jump.JScrollAndJumpPane;
import org.andork.swing.jump.JTableJumpSupport;
import org.andork.swing.table.AnnotatingJTableJumpBarModel;
import org.andork.swing.table.AnnotatingTableRowSorter;
import org.breakout.table.DataDefaults;
import org.breakout.table.ParseNote;
import org.breakout.table.ParseStatus;
import org.breakout.table.Station;
import org.breakout.table.StationColumnDefs;
import org.breakout.table.StationTableColumnModel;
import org.breakout.table.StationTableModel;
import org.breakout.table.StationTableModelCopier;
import org.breakout.table.SurveyDataColumnDef;
import org.breakout.table.SurveyDataColumnType;
import org.breakout.table.SurveyDataFormatter;
import org.breakout.table.SurveyDataList;
import org.breakout.table.SurveyDataTable;
import org.breakout.table.SurveyModel;

public class StationTableTest
{
	public static void main( String[ ] args )
	{
		OnEDT.onEDT( ( ) ->
		{
			I18n i18n = new I18n( );

			SurveyDataList<Station> stationList = new SurveyDataList<>( new Station( ) );

			stationList.setCustomColumnDefs( Arrays.asList(
				new SurveyDataColumnDef( "Water Level" , SurveyDataColumnType.DOUBLE )
				) );

			stationList.setCustomColumnDefs( Arrays.asList(
				new SurveyDataColumnDef( "Section" , SurveyDataColumnType.SECTION ) ,
				new SurveyDataColumnDef( "Test" , SurveyDataColumnType.INTEGER ) ,
				new SurveyDataColumnDef( "Water Level" , SurveyDataColumnType.DOUBLE ) ,
				new SurveyDataColumnDef( "Surveyors" , SurveyDataColumnType.TAGS ) ,
				new SurveyDataColumnDef( "Link" , SurveyDataColumnType.LINK )
				) );

			QObject<SurveyModel> projModel = QArrayObject.create( SurveyModel.spec );
			QObjectBinder<SurveyModel> projModelBinder = new QObjectBinder<SurveyModel>( SurveyModel.spec );
			projModelBinder.objLink.bind( new DefaultBinder<>( projModel ) );

			StationTableModel tableModel = new StationTableModel( );
			tableModel.setSurveyDataList( stationList );

			projModel.get( SurveyModel.defaults ).set( DataDefaults.decimalSeparator , ',' );

			ExecutorService executor = Executors.newSingleThreadExecutor( );

			SurveyDataTable table = new SurveyDataTable( tableModel );
			AnnotatingTableRowSorter<StationTableModel> rowSorter = new AnnotatingTableRowSorter<>( table ,
				r -> executor.submit( r ) );
			rowSorter.setModelCopier( new StationTableModelCopier( ) );
			rowSorter.setRowAnnotator( new RowAnnotator<StationTableModel, Integer>( ) {
				@Override
				public Object annotate( Entry<? extends StationTableModel, ? extends Integer> entry )
				{
					StationTableModel model = entry.getModel( );
					return model.getNotesInRow( entry.getIdentifier( ) );
				}
			} );
			rowSorter.setSortsOnUpdates( true );
			table.setRowSorter( rowSorter );
			table.setAutoResizeMode( JTable.AUTO_RESIZE_OFF );

			SurveyDataFormatter formats = new SurveyDataFormatter( i18n );
			formats.setDecimalSeparator( ',' );

			StationTableColumnModel columnModel = new StationTableColumnModel( i18n , formats );
			columnModel.update( tableModel , Arrays.asList(
				StationColumnDefs.name ,
				StationColumnDefs.north ,
				StationColumnDefs.east ,
				StationColumnDefs.up ,
				StationColumnDefs.lengthUnit ,
				new SurveyDataColumnDef( "Test" , SurveyDataColumnType.INTEGER ) ,
				new SurveyDataColumnDef( "Water Level" , SurveyDataColumnType.DOUBLE ) ,
				new SurveyDataColumnDef( "Section" , SurveyDataColumnType.SECTION ) ,
				new SurveyDataColumnDef( "Surveyors" , SurveyDataColumnType.TAGS ) ,
				new SurveyDataColumnDef( "Link" , SurveyDataColumnType.LINK )
				) );


			projModelBinder.property( SurveyModel.defaults ).addBinding( force -> columnModel.setDataDefaults(
				projModelBinder.property( SurveyModel.defaults ).get( ) ) );

			table.setColumnModel( columnModel );
			table.setRowHeight( 21 );

			JScrollAndJumpPane scrollPane = new JScrollAndJumpPane( table );
			scrollPane.getJumpBar( ).setModel( new AnnotatingJTableJumpBarModel( table ) );
			scrollPane.getJumpBar( ).setJumpSupport( new JTableJumpSupport( table ) );
			scrollPane.getJumpBar( ).setColorer( o ->
			{
				if( o instanceof Color )
				{
					return ( Color ) o;
				}
				if( o instanceof List )
				{
					List<?> list = ( List<?> ) o;
					Color color = null;

					for( Object elem : list )
					{
						if( elem instanceof ParseNote )
						{
							ParseStatus status = ( ( ParseNote ) elem ).getStatus( );
							switch( status )
							{
							case ERROR:
								return Color.RED;
							case WARNING:
								color = Color.YELLOW;
								break;
							}
						}
					}

					return color;
				}
				return null;
			} );

			Dimension prefSize = table.getPreferredSize( );
			prefSize.height = table.getPreferredScrollableViewportSize( ).height;
			table.setPreferredScrollableViewportSize( prefSize );

			QuickTestFrame.frame( scrollPane ).setVisible( true );
		} );
	}
}
