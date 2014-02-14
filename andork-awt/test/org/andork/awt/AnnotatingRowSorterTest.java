package org.andork.awt;

import java.awt.Color;
import java.util.Collections;
import java.util.regex.Pattern;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.event.DocumentEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import org.andork.awt.GridBagWizard.DefaultAutoInsets;

public class AnnotatingRowSorterTest
{
	public static void main( String[ ] args )
	{
		DefaultTableModel model = new DefaultTableModel( 10 , 10 );
		
		final ColorMapAnnotatingTableCellRenderer renderer = new ColorMapAnnotatingTableCellRenderer( );
		
		AnnotatingJTable table = new AnnotatingJTable( model )
		{
			@Override
			public TableCellRenderer getCellRenderer( int row , int column )
			{
				return renderer;
			}
		};
		
		final AnnotatingTableRowSorter<DefaultTableModel, RowFilter<DefaultTableModel, Integer>> rowSorter =
				new AnnotatingTableRowSorter<DefaultTableModel, RowFilter<DefaultTableModel, Integer>>( model );
		
		rowSorter.setSortsOnUpdates( true );
		
		table.setRowSorter( rowSorter );
		rowSorter.sort( );
		
		final JTextField regexField = new JTextField( );
		
		final JScrollAndJumpPane tableScrollPane = new JScrollAndJumpPane( table );
		tableScrollPane.setBorder( null );
		
		final JumpBarModelFromAnnotatingJTable jumpBarModel = new JumpBarModelFromAnnotatingJTable( table );
		
		tableScrollPane.getJumpBar( ).setModel( jumpBarModel );
		
		regexField.getDocument( ).addDocumentListener( new EasyDocumentListener( )
		{
			@Override
			public void documentChanged( DocumentEvent e )
			{
				try
				{
					if( regexField.getText( ) != null && regexField.getText( ).length( ) > 0 )
					{
						RowFilter<DefaultTableModel, Integer> filter = RowFilter.regexFilter( regexField.getText( ) , 0 );
						rowSorter.setRowAnnotator( RowAnnotator.filterAnnotator( filter ) );
						renderer.setAnnotationColors( Collections.singletonMap( filter , Color.YELLOW ) );
						tableScrollPane.getJumpBar( ).setColorMap( Collections.singletonMap( filter , Color.YELLOW ) );
					}
					else
					{
						rowSorter.setRowAnnotator( null );
						renderer.setAnnotationColors( Collections.<Object,Color>emptyMap( ) );
						tableScrollPane.getJumpBar( ).setColorMap( null );
					}
					regexField.setForeground( Color.BLACK );
					
				}
				catch( Exception ex )
				{
					regexField.setForeground( Color.RED );
				}
			}
		} );
		
		JPanel panel = new JPanel( );
		GridBagWizard gbw = GridBagWizard.create( panel );
		
		gbw.defaults( ).autoinsets( new DefaultAutoInsets( 2 , 2 ) );
		gbw.put( regexField ).xy( 0 , 0 ).fillx( 1.0 );
		gbw.put( tableScrollPane ).below( regexField ).fillboth( 1.0 , 1.0 );
		
		QuickTestFrame.frame( panel ).setVisible( true );
	}
}
