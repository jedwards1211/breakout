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
import org.breakout.table.Shot;
import org.breakout.table.ShotColumnDefs;
import org.breakout.table.ShotTableColumnModel;
import org.breakout.table.ShotTableModel;
import org.breakout.table.ShotTableModelCopier;
import org.breakout.table.SurveyDataColumnDef;
import org.breakout.table.SurveyDataColumnType;
import org.breakout.table.SurveyDataFormatter;
import org.breakout.table.SurveyDataList;
import org.breakout.table.SurveyDataTable;
import org.breakout.table.SurveyModel;

public class ShotTableTest
{
	public static void main( String[ ] args )
	{
		OnEDT.onEDT( ( ) ->
		{
			I18n i18n = new I18n( );

			SurveyDataList<Shot> shotList = new SurveyDataList<>( new Shot( ) );

			shotList.setCustomColumnDefs( Arrays.asList(
				new SurveyDataColumnDef( "Water Level" , SurveyDataColumnType.DOUBLE )
				) );

			shotList.setCustomColumnDefs( Arrays.asList(
				new SurveyDataColumnDef( "Section" , SurveyDataColumnType.SECTION ) ,
				new SurveyDataColumnDef( "Test" , SurveyDataColumnType.INTEGER ) ,
				new SurveyDataColumnDef( "Water Level" , SurveyDataColumnType.DOUBLE ) ,
				new SurveyDataColumnDef( "Surveyors" , SurveyDataColumnType.TAGS ) ,
				new SurveyDataColumnDef( "Link" , SurveyDataColumnType.LINK )
				) );

			QObject<SurveyModel> projModel = QArrayObject.create( SurveyModel.spec );
			QObjectBinder<SurveyModel> projModelBinder = new QObjectBinder<SurveyModel>( SurveyModel.spec );
			projModelBinder.objLink.bind( new DefaultBinder<>( projModel ) );

			ShotTableModel tableModel = new ShotTableModel( );
			tableModel.setSurveyDataList( shotList );

			projModel.get( SurveyModel.defaults ).set( DataDefaults.decimalSeparator , ',' );
			projModel.set( SurveyModel.shotList , shotList );

			ExecutorService executor = Executors.newSingleThreadExecutor( );

			SurveyDataTable table = new SurveyDataTable( tableModel );
			AnnotatingTableRowSorter<ShotTableModel> rowSorter = new AnnotatingTableRowSorter<>( table ,
				r -> executor.submit( r ) );
			rowSorter.setModelCopier( new ShotTableModelCopier( ) );
			rowSorter.setRowAnnotator( new RowAnnotator<ShotTableModel, Integer>( ) {
				@Override
				public Object annotate( Entry<? extends ShotTableModel, ? extends Integer> entry )
				{
					ShotTableModel model = entry.getModel( );
					return model.getNotesInRow( entry.getIdentifier( ) );
				}
			} );
			rowSorter.setSortsOnUpdates( true );
			table.setRowSorter( rowSorter );
			table.setAutoResizeMode( JTable.AUTO_RESIZE_OFF );

			SurveyDataFormatter formats = new SurveyDataFormatter( i18n );
			formats.setDecimalSeparator( ',' );

			ShotTableColumnModel columnModel = new ShotTableColumnModel( i18n , formats );
			columnModel.update( tableModel , Arrays.asList(
				ShotColumnDefs.fromStationName ,
				ShotColumnDefs.toStationName ,
				ShotColumnDefs.vector ,
				ShotColumnDefs.xSectionAtFrom ,
				ShotColumnDefs.xSectionAtTo ,
				ShotColumnDefs.lengthUnit ,
				ShotColumnDefs.angleUnit ,
				new SurveyDataColumnDef( "Test" , SurveyDataColumnType.INTEGER ) ,
				new SurveyDataColumnDef( "Water Level" , SurveyDataColumnType.DOUBLE ) ,
				new SurveyDataColumnDef( "Section" , SurveyDataColumnType.SECTION ) ,
				new SurveyDataColumnDef( "Surveyors" , SurveyDataColumnType.TAGS ) ,
				new SurveyDataColumnDef( "Link" , SurveyDataColumnType.LINK )
				) );

			columnModel.vectorColumn.setPreferredWidth( 300 );
			columnModel.xSectionAtFromColumn.setPreferredWidth( 300 );
			columnModel.xSectionAtToColumn.setPreferredWidth( 300 );

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
