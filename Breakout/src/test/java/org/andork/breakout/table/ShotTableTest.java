package org.andork.breakout.table;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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

public class ShotTableTest
{
	public static void main( String[ ] args )
	{
		OnEDT.onEDT( ( ) ->
		{
			I18n i18n = new I18n( );

			ShotList shotList = new ShotList( );

			shotList.setCustomColumnDefs( Arrays.asList(
				new ShotColumnDef( "Water Level" , ShotColumnType.DOUBLE )
				) );

			shotList.setCustomColumnDefs( Arrays.asList(
				new ShotColumnDef( "Test" , ShotColumnType.INTEGER ) ,
				new ShotColumnDef( "Water Level" , ShotColumnType.DOUBLE ) ,
				new ShotColumnDef( "Cave" , ShotColumnType.STRING )
				) );

			QObject<ProjectModel> projModel = QArrayObject.create( ProjectModel.spec );
			QObjectBinder<ProjectModel> projModelBinder = new QObjectBinder<ProjectModel>( ProjectModel.spec );
			projModelBinder.objLink.bind( new DefaultBinder<>( projModel ) );

			ShotTableModel tableModel = new ShotTableModel( );
			tableModel.setShotList( shotList );

			projModel.get( ProjectModel.defaults ).set( DataDefaults.decimalSeparator , ',' );
			projModel.set( ProjectModel.shotList , shotList );

			ExecutorService executor = Executors.newSingleThreadExecutor( );

			ShotTable table = new ShotTable( tableModel );
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
//			table.setFont( new Font( "Monospaced" , Font.PLAIN , 11 ) );

			ShotDataFormatter formats = new ShotDataFormatter( i18n );
			formats.setDecimalSeparator( ',' );

			ShotTableColumnModel columnModel = new ShotTableColumnModel( i18n , formats );
			columnModel.update( tableModel , Arrays.asList(
				ShotColumnDef.fromStationName ,
				ShotColumnDef.toStationName ,
				ShotColumnDef.vector ,
				ShotColumnDef.xSectionAtFrom ,
				ShotColumnDef.xSectionAtTo ,
				ShotColumnDef.lengthUnit ,
				ShotColumnDef.angleUnit ,
				new ShotColumnDef( "Test" , ShotColumnType.INTEGER ) ,
				new ShotColumnDef( "Water Level" , ShotColumnType.DOUBLE ) ,
				new ShotColumnDef( "Cave" , ShotColumnType.STRING )
				) );

			columnModel.vectorColumn.setPreferredWidth( 300 );
			columnModel.xSectionAtFromColumn.setPreferredWidth( 300 );
			columnModel.xSectionAtToColumn.setPreferredWidth( 300 );

			projModelBinder.property( ProjectModel.defaults ).addBinding( force -> columnModel.setDataDefaults(
				projModelBinder.property( ProjectModel.defaults ).get( ) ) );

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
