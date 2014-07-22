package org.andork.breakout.table;

import java.awt.Color;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JButton;

import org.andork.breakout.model.SurveyTableModel;
import org.andork.breakout.table.NewSurveyTableModel.NewSurveyTableModelCopier;
import org.andork.swing.OnEDT;
import org.andork.swing.QuickTestFrame;
import org.andork.swing.table.AnnotatingTableRowSorter;
import org.andork.swing.table.DefaultAnnotatingJTableSetup;
import org.andork.swing.table.FormattedTextTableRowAnnotator;
import org.andork.util.FormatWarning;

public class NewSurveyTableTest
{
	public static void main( String[ ] args )
	{
		OnEDT.onEDT( ( ) -> {
			NewSurveyTableModel model = new NewSurveyTableModel( );
			
			NewSurveyTable table = new NewSurveyTable( model );
			ExecutorService executor = Executors.newSingleThreadExecutor( );
			DefaultAnnotatingJTableSetup setup = new DefaultAnnotatingJTableSetup( table , r -> executor.submit( r ) );
			FormattedTextTableRowAnnotator annotator = new FormattedTextTableRowAnnotator( );
			annotator.setCombiner( ( e1 , e2 ) -> e1 instanceof FormatWarning ? e2 : e1 );
			AnnotatingTableRowSorter<NewSurveyTableModel> sorter = ( AnnotatingTableRowSorter<NewSurveyTableModel> ) table.getRowSorter( );
			sorter.setModelCopier( new NewSurveyTableModelCopier( ) );
			sorter.setSortsOnUpdates( true );
			( ( AnnotatingTableRowSorter<SurveyTableModel> ) table.getRowSorter( ) ).setRowAnnotator( annotator );
			setup.scrollPane.getJumpBar( ).setColorer( a -> {
				if( a instanceof FormatWarning )
				{
					return Color.YELLOW;
				}
					else if( a instanceof Exception )
					{
						return Color.RED;
					}
					return null;
				} );
			
			setup.scrollPane.setTopRightCornerComp( new JButton( new CustomizeNewSurveyTableAction( table ) ) );
			
			QuickTestFrame.frame( setup.scrollPane ).setVisible( true );
		} );
	}
}
