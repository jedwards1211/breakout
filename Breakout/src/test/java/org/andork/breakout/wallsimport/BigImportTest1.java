package org.andork.breakout.wallsimport;

import java.awt.Color;
import java.awt.Dimension;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JTable;
import javax.swing.RowFilter.Entry;

import org.andork.bind2.DefaultBinder;
import org.andork.breakout.table.Shot;
import org.andork.breakout.table.DataDefaults;
import org.andork.breakout.table.ParseNote;
import org.andork.breakout.table.ParseStatus;
import org.andork.breakout.table.ShotColumnDefs;
import org.andork.breakout.table.ShotTableColumnModel;
import org.andork.breakout.table.ShotTableModel;
import org.andork.breakout.table.ShotTableModelCopier;
import org.andork.breakout.table.SurveyDataFormatter;
import org.andork.breakout.table.SurveyDataList;
import org.andork.breakout.table.SurveyDataTable;
import org.andork.breakout.table.SurveyModel;
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

public class BigImportTest1
{

	public static void main( String[ ] args )
	{
		OnEDT.onEDT( ( ) ->
		{
			I18n i18n = new I18n( );

			WallsImporter importer = new WallsImporter( i18n );

			importer.importSrvFile( Paths.get( "src/test/java/org/andork/breakout/wallsimport/vectorTest1.srv" ) , null );
			SurveyDataList<Shot> shotList = importer.getOutputModel( ).get( SurveyModel.shotList );

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
				ShotColumnDefs.angleUnit
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
