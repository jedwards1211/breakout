package org.andork.swing.table;

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

import org.andork.awt.GridBagWizard;
import org.andork.awt.GridBagWizard.DefaultAutoInsets;
import org.andork.swing.AnnotatingRowSorter.ExecutorServiceSortRunner;
import org.andork.swing.AnnotatingRowSorter.SortRunner;
import org.andork.swing.AnnotatingRowSorterCursorController;
import org.andork.swing.DoSwing;
import org.andork.swing.QuickTestFrame;
import org.andork.swing.RowAnnotator;
import org.andork.swing.TextComponentWithHintAndClear;
import org.andork.swing.event.EasyDocumentListener;
import org.andork.swing.jump.JScrollAndJumpPane;
import org.andork.swing.jump.JTableJumpSupport;
import org.andork.swing.table.AnnotatingTableRowSorter.DefaultTableModelCopier;

public class AnnotatingJTableTest
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
		
		ExecutorService sortExecutor = Executors.newSingleThreadExecutor( );
		SortRunner sortRunner = new ExecutorServiceSortRunner( sortExecutor );
		
		final DefaultAnnotatingJTableSetup<DefaultTableModel, Object> setup =
				new DefaultAnnotatingJTableSetup<DefaultTableModel, Object>( new AnnotatingJTable( model ) , sortRunner );
		
		setup.table.getAnnotatingRowSorter( ).setModelCopier( new DefaultTableModelCopier( ) );
		setup.table.getAnnotatingRowSorter( ).setSortsOnUpdates( true );
		
		final JTextField filterField = new JTextField( );
		final JTextField highlightField = new JTextField( );
		
		highlightField.getDocument( ).addDocumentListener(
				DefaultAnnotatingJTableSetup.createHighlightFieldListener( setup , highlightField , Color.YELLOW ) );
		filterField.getDocument( ).addDocumentListener(
				DefaultAnnotatingJTableSetup.createFilterFieldListener( setup , filterField ) );
		
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
		gbw.put( setup.scrollPane ).below( filterLabel , filterFieldWrapper ).fillboth( 1.0 , 1.0 );
		
		QuickTestFrame.frame( panel ).setVisible( true );
	}
}
