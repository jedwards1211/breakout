/*******************************************************************************
 * Breakout Cave Survey Visualizer
 *
 * Copyright (C) 2014 James Edwards
 *
 * jedwards8 at fastmail dot fm
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *******************************************************************************/
package org.andork.swing.table;

import java.awt.Color;
import java.util.Collections;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.RowSorterEvent;
import javax.swing.event.RowSorterListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

import org.andork.awt.GridBagWizard;
import org.andork.awt.GridBagWizard.DefaultAutoInsets;
import org.andork.swing.AnnotatingRowSorterCursorController;
import org.andork.swing.DoSwing;
import org.andork.swing.QuickTestFrame;
import org.andork.swing.RowAnnotator;
import org.andork.swing.TextComponentWithHintAndClear;
import org.andork.swing.event.EasyDocumentListener;

public class JTableSortTest
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
		
		final JTable table = new JTable( model );
		
		table.getSelectionModel( ).addListSelectionListener( new ListSelectionListener( )
		{
			@Override
			public void valueChanged( ListSelectionEvent e )
			{
				System.out.println( e );
			}
		} );
		
		final TableRowSorter<DefaultTableModel> rowSorter = new TableRowSorter<DefaultTableModel>( model );
		rowSorter.setSortsOnUpdates( true );
		
		table.setRowSorter( rowSorter );
		// rowSorter.sortLater( );
		
		rowSorter.addRowSorterListener( new RowSorterListener( )
		{
			@Override
			public void sorterChanged( RowSorterEvent e )
			{
				System.out.println( e + " " + e.getType( ) );
			}
		} );
		
		final JTextField filterField = new JTextField( );
		final JTextField highlightField = new JTextField( );
		
		final JScrollPane tableScrollPane = new JScrollPane( table );
		
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
					if( field == filterField )
					{
						rowSorter.setRowFilter( filter );
					}
				}
				else
				{
					field.setForeground( Color.BLACK );
					
					if( field == filterField )
					{
						rowSorter.setRowFilter( null );
					}
				}
				
			}
		};
		
		highlightField.getDocument( ).addDocumentListener( docListener );
		filterField.getDocument( ).addDocumentListener( docListener );
		
		// TextComponentWithHintAndClear highlightFieldWrapper = new TextComponentWithHintAndClear( highlightField , "Enter Regular Expression" );
		TextComponentWithHintAndClear filterFieldWrapper = new TextComponentWithHintAndClear( filterField , "Enter Regular Expression" );
		
		JPanel panel = new JPanel( );
		panel.setBorder( new EmptyBorder( 2 , 2 , 2 , 2 ) );
		GridBagWizard gbw = GridBagWizard.create( panel );
		
		gbw.defaults( ).autoinsets( new DefaultAutoInsets( 2 , 2 ) );
		// JLabel highlightLabel = new JLabel( "Highlight: " );
		// gbw.put( highlightLabel ).xy( 0 , 0 ).west( );
		// gbw.put( highlightFieldWrapper ).rightOf( highlightLabel ).fillx( 1.0 );
		JLabel filterLabel = new JLabel( "Filter: " );
		gbw.put( filterLabel ).xy( 0 , 0 ).west( );
		gbw.put( filterFieldWrapper ).rightOf( filterLabel ).fillx( 1.0 );
		gbw.put( tableScrollPane ).below( filterLabel , filterFieldWrapper ).fillboth( 1.0 , 1.0 );
		
		QuickTestFrame.frame( panel ).setVisible( true );
	}
}
