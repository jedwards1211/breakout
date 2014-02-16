package org.andork.swing.annotate;

import java.awt.Color;
import java.util.Collections;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import org.andork.awt.GridBagWizard;
import org.andork.awt.GridBagWizard.DefaultAutoInsets;
import org.andork.swing.AnnotatingRowSorterCursorController;
import org.andork.swing.DoSwing;
import org.andork.swing.QuickTestFrame;
import org.andork.swing.RowAnnotator;
import org.andork.swing.TextComponentWithHintAndClear;
import org.andork.swing.event.EasyDocumentListener;
import org.andork.swing.jump.JScrollAndJumpPane;
import org.andork.swing.jump.JTableJumpSupport;
import org.andork.swing.table.AnnotatingJTable;
import org.andork.swing.table.AnnotatingJTableJumpBarModel;
import org.andork.swing.table.AnnotatingTableRowSorter;
import org.andork.swing.table.ColorMapAnnotatingTableCellRenderer;
import org.andork.swing.table.AnnotatingTableRowSorter.DefaultTableModelCopier;

public class AnnotatingRowSorterTest
{
	public static void main( String[ ] args )
	{
		new DoSwing( )
		{
			@Override
			public void run( )
			{
				try
				{
					UIManager.setLookAndFeel( UIManager.getSystemLookAndFeelClassName( ) );
				}
				catch( Exception ex )
				{
					ex.printStackTrace( );
				}
			}
		};
		
		DefaultTableModel model = new DefaultTableModel( 100000 , 10 );
		
		for( int row = 0 ; row < model.getRowCount( ) ; row++ )
		{
			for( int column = 0 ; column < model.getColumnCount( ) ; column++ )
			{
				model.setValueAt( String.valueOf( row + ", " + column ) , row , column );
			}
		}
		
		final ColorMapAnnotatingTableCellRenderer renderer = new ColorMapAnnotatingTableCellRenderer( );
		
		AnnotatingJTable table = new AnnotatingJTable( model )
		{
			@Override
			public TableCellRenderer getCellRenderer( int row , int column )
			{
				return renderer;
			}
		};
		
		ExecutorService sortExecutor = Executors.newSingleThreadExecutor( );
		
		final AnnotatingTableRowSorter<DefaultTableModel, RowFilter<DefaultTableModel, Integer>> rowSorter =
				new AnnotatingTableRowSorter<DefaultTableModel, RowFilter<DefaultTableModel, Integer>>( model , sortExecutor );
		rowSorter.setModelCopier( new DefaultTableModelCopier( ) );
		rowSorter.setSortsOnUpdates( true );
		
		table.setRowSorter( rowSorter );
		// rowSorter.sortLater( );
		
		final JTextField filterField = new JTextField( );
		final JTextField highlightField = new JTextField( );
		
		final JScrollAndJumpPane tableScrollPane = new JScrollAndJumpPane( table );
		tableScrollPane.setBorder( null );
		
		final AnnotatingJTableJumpBarModel jumpBarModel = new AnnotatingJTableJumpBarModel( table );
		
		tableScrollPane.getJumpBar( ).setModel( jumpBarModel );
		tableScrollPane.getJumpBar( ).setJumpSupport( new JTableJumpSupport( table ) );
		
		AnnotatingRowSorterCursorController cursorController = new AnnotatingRowSorterCursorController( tableScrollPane );
		rowSorter.addRowSorterListener( cursorController );
		
		DocumentListener docListener = new EasyDocumentListener( )
		{
			@Override
			public void documentChanged( DocumentEvent e )
			{
				JTextField field = e.getDocument( ) == highlightField.getDocument( ) ? highlightField : filterField;
				
				if( field.getText( ) != null && field.getText( ).length( ) > 0 )
				{
					RowFilter<DefaultTableModel, Integer> filter = null;
					try
					{
						filter = RowFilter.regexFilter( field.getText( ) , 0 );
						field.setForeground( Color.BLACK );
					}
					catch( Exception ex )
					{
						field.setForeground( Color.RED );
					}
					if( field == highlightField )
					{
						rowSorter.setRowAnnotator( RowAnnotator.filterAnnotator( filter ) );
						renderer.setAnnotationColors( Collections.singletonMap( filter , Color.YELLOW ) );
						tableScrollPane.getJumpBar( ).setColorMap( Collections.singletonMap( filter , Color.YELLOW ) );
					}
					else if( field == filterField )
					{
						rowSorter.setRowFilter( filter );
					}
				}
				else
				{
					field.setForeground( Color.BLACK );
					
					if( field == highlightField )
					{
						rowSorter.setRowAnnotator( null );
						renderer.setAnnotationColors( Collections.<Object,Color>emptyMap( ) );
						tableScrollPane.getJumpBar( ).setColorMap( null );
					}
					else if( field == filterField )
					{
						rowSorter.setRowFilter( null );
					}
				}
				
			}
		};
		
		highlightField.getDocument( ).addDocumentListener( docListener );
		filterField.getDocument( ).addDocumentListener( docListener );
		
		TextComponentWithHintAndClear highlightFieldWrapper = new TextComponentWithHintAndClear( highlightField , "Enter Regular Expression" );
		TextComponentWithHintAndClear filterFieldWrapper = new TextComponentWithHintAndClear( filterField , "Enter Regular Expression" );
		
		JPanel panel = new JPanel( );
		panel.setBorder( new EmptyBorder( 2 , 2 , 2 , 2 ) );
		GridBagWizard gbw = GridBagWizard.create( panel );
		
		gbw.defaults( ).autoinsets( new DefaultAutoInsets( 2 , 2 ) );
		JLabel highlightLabel = new JLabel( "Highlight: " );
		gbw.put( highlightLabel ).xy( 0 , 0 ).west( );
		gbw.put( highlightFieldWrapper ).rightOf( highlightLabel ).fillx( 1.0 );
		JLabel filterLabel = new JLabel( "Filter: " );
		gbw.put( filterLabel ).below( highlightLabel ).west( );
		gbw.put( filterFieldWrapper ).rightOf( filterLabel ).fillx( 1.0 );
		gbw.put( tableScrollPane ).below( filterLabel , filterFieldWrapper ).fillboth( 1.0 , 1.0 );
		
		QuickTestFrame.frame( panel ).setVisible( true );
	}
}
