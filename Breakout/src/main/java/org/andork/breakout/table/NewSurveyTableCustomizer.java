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
package org.andork.breakout.table;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import org.andork.awt.GridBagWizard;
import org.andork.awt.GridBagWizard.DefaultAutoInsets;
import org.andork.breakout.table.NewSurveyTableModel.SurveyColumnType;
import org.andork.q.QObject;
import org.andork.swing.selector.DefaultSelector;
import org.andork.swing.selector.FormatAndDisplayInfoListCellRenderer;
import org.andork.swing.table.FormatAndDisplayInfo;
import org.andork.swing.table.NiceTableModel;
import org.andork.util.StringUtils;
import org.andork.util.Java7.Objects;

public class NewSurveyTableCustomizer extends JPanel
{
	private JScrollPane				tableScrollPane;
	private JTable					table;
	private CustomizationTableModel	tableModel;
	
	private JButton					addButton;
	private JButton					removeButton;
	private JButton					applyButton;
	private JButton					okButton;
	
	private boolean					ignoreTableChanges;
	
	private Controller				controller;
	
	public NewSurveyTableCustomizer( )
	{
		initComponents( );
		initLayout( );
		initListeners( );
		
		updateRemoveButtonEnabled( );
	}
	
	public Controller getController( )
	{
		return controller;
	}
	
	public void setController( Controller controller )
	{
		this.controller = controller;
	}
	
	private DefaultSelector<FormatAndDisplayInfo<?>> createDefaultFormatSelector( )
	{
		DefaultSelector<FormatAndDisplayInfo<?>> result = new DefaultSelector<>( );
		FormatAndDisplayInfoListCellRenderer.setUpComboBox( result.getComboBox( ) );
		return result;
	}
	
	private void initComponents( )
	{
		addButton = new JButton( "Add Column" );
		removeButton = new JButton( "Remove Columns" );
		removeButton.setEnabled( false );
		applyButton = new JButton( "Apply" );
		applyButton.setEnabled( false );
		okButton = new JButton( "OK" );
		
		tableModel = new CustomizationTableModel( );
		table = new JTable( tableModel );
		table.setRowHeight( 25 );
		TableColumnModel columnModel = table.getColumnModel( );
		
		TableColumn visColumn = columnModel.getColumn( CustomizationTableModel.VISIBLE_INDEX );
		visColumn.setPreferredWidth( 40 );
		visColumn.setMaxWidth( 40 );
		
		UniqueTableCellEditor nameEditor = new UniqueTableCellEditor( new JTextField( ) );
		nameEditor.setDuplicateHandler( obj ->
				JOptionPane.showMessageDialog( SwingUtilities.getWindowAncestor( table ) ,
						"There is already another column named \"" + obj + "\".  Please enter a unique name." ,
						"Duplicate Name" ,
						JOptionPane.WARNING_MESSAGE ) );
		table.setDefaultEditor( String.class , nameEditor );
		
		table.setDefaultRenderer( Boolean.class , new JCheckBoxBooleanCellRenderer( ) );
		table.setDefaultRenderer( SurveyColumnType.class , new JComboBoxTableCellRenderer( ) );
		table.setDefaultRenderer( FormatAndDisplayInfo.class ,
				new DefaultSelectorTableCellRenderer( createDefaultFormatSelector( ) )
				{
					@Override
					public Component getTableCellRendererComponent( JTable table , Object value , boolean isSelected , boolean hasFocus , int row , int column )
					{
						SurveyColumnType type = ( SurveyColumnType ) tableModel.getValueAt( row , CustomizationTableModel.TYPE_INDEX );
						selector.setAvailableValues( NewSurveyTable.getAvailableFormats( type == null ? null : type.valueClass ) );
						return super.getTableCellRendererComponent( table , value , isSelected , hasFocus , row , column );
					}
				} );
		
		DefaultSelector<SurveyColumnType> typeSelector = new DefaultSelector<>( );
		typeSelector.setAvailableValues( SurveyColumnType.values( ) );
		
		JCheckBox editorCheckBox = new JCheckBox( );
		editorCheckBox.setHorizontalAlignment( JCheckBox.CENTER );
		table.setDefaultEditor( Boolean.class , new DefaultCellEditor( editorCheckBox ) );
		table.setDefaultEditor( SurveyColumnType.class , new DefaultCellEditor( typeSelector.getComboBox( ) ) );
		
		final DefaultSelector<FormatAndDisplayInfo<?>> formatSelector = createDefaultFormatSelector( );
		
		table.setDefaultEditor( FormatAndDisplayInfo.class , new DefaultCellEditor( formatSelector.getComboBox( ) )
		{
			
			@Override
			public Component getTableCellEditorComponent( JTable table , Object value , boolean isSelected , int row , int column )
			{
				SurveyColumnType type = ( SurveyColumnType ) tableModel.getValueAt( row , CustomizationTableModel.TYPE_INDEX );
				formatSelector.setAvailableValues( NewSurveyTable.getAvailableFormats( type == null ? null : type.valueClass ) );
				return super.getTableCellEditorComponent( table , value , isSelected , row , column );
			}
		} );
		
		tableScrollPane = new JScrollPane( table );
		tableScrollPane.setPreferredSize( new Dimension( 300 , 200 ) );
	}
	
	private void initLayout( )
	{
		setLayout( new BorderLayout( 5 , 5 ) );
		
		JPanel buttonPanel = new JPanel( );
		GridBagWizard w = GridBagWizard.create( buttonPanel );
		w.defaults( ).autoinsets( new DefaultAutoInsets( 3 , 3 ) );
		w.put( addButton , removeButton , applyButton , okButton ).intoRow( );
		w.put( addButton , removeButton ).west( );
		w.put( applyButton , okButton ).east( );
		w.put( applyButton ).weightx( 1.0 );
		
		add( buttonPanel , BorderLayout.NORTH );
		add( tableScrollPane , BorderLayout.CENTER );
	}
	
	private void initListeners( )
	{
		addButton.addActionListener( new ActionListener( )
		{
			@Override
			public void actionPerformed( ActionEvent e )
			{
				QObject<SurveyColumnModel> newColumn = SurveyColumnModel.instance.newObject( );
				newColumn.set( SurveyColumnModel.type , SurveyColumnType.STRING );
				newColumn.set( SurveyColumnModel.visible , true );
				tableModel.addRow( newColumn );
			}
		} );
		
		removeButton.addActionListener( new ActionListener( )
		{
			@Override
			public void actionPerformed( ActionEvent e )
			{
				int[ ] selRows = table.getSelectedRows( );
				
				for( int i = selRows.length - 1 ; i >= 0 ; i-- )
				{
					int modelIndex = table.convertRowIndexToModel( selRows[ i ] );
					QObject<SurveyColumnModel> colModel = tableModel.getRow( modelIndex );
					if( !Boolean.TRUE.equals( colModel.get( SurveyColumnModel.fixed ) ) )
					{
						tableModel.removeRow( modelIndex );
					}
				}
			}
		} );
		
		applyButton.addActionListener( new ActionListener( )
		{
			@Override
			public void actionPerformed( ActionEvent e )
			{
				if( controller != null )
				{
					controller.applyButtonPressed( );
				}
			}
		} );
		
		okButton.addActionListener( new ActionListener( )
		{
			@Override
			public void actionPerformed( ActionEvent e )
			{
				if( controller != null )
				{
					controller.okButtonPressed( );
				}
			}
		} );
		
		tableModel.addTableModelListener( new TableModelListener( )
		{
			@Override
			public void tableChanged( TableModelEvent e )
			{
				if( !ignoreTableChanges )
				{
					applyButton.setEnabled( true );
				}
			}
		} );
		
		table.getSelectionModel( ).addListSelectionListener( new ListSelectionListener( )
		{
			@Override
			public void valueChanged( ListSelectionEvent e )
			{
				updateRemoveButtonEnabled( );
			}
		} );
	}
	
	private void updateRemoveButtonEnabled( )
	{
		removeButton.setEnabled( table.getSelectedRowCount( ) > 0 );
	}
	
	public void setColumnModels( Collection<? extends QObject<SurveyColumnModel>> columns )
	{
		ignoreTableChanges = true;
		try
		{
			tableModel.setRows( columns );
		}
		finally
		{
			ignoreTableChanges = false;
		}
	}
	
	public JTable getTable( )
	{
		return table;
	}
	
	private class CustomizationTableModel extends NiceTableModel<QObject<SurveyColumnModel>>
	{
		static final int	VISIBLE_INDEX			= 0;
		static final int	NAME_INDEX				= 1;
		static final int	TYPE_INDEX				= 2;
		static final int	DEFAULT_FORMAT_INDEX	= 3;
		
		public CustomizationTableModel( )
		{
			addColumn( QObjectColumn.newInstance( SurveyColumnModel.instance , SurveyColumnModel.visible ) );
			addColumn( QObjectColumn.newInstance( SurveyColumnModel.instance , SurveyColumnModel.name ) );
			addColumn( QObjectColumn.newInstance( SurveyColumnModel.instance , SurveyColumnModel.type ) );
			addColumn( QObjectColumn.newInstance( SurveyColumnModel.instance , SurveyColumnModel.defaultFormat ) );
		}
		
		@Override
		public void addRow( QObject<SurveyColumnModel> row )
		{
			super.addRow( row );
		}
		
		@Override
		public void removeRow( int index )
		{
			super.removeRow( index );
		}
		
		@Override
		public void setRows( Collection<? extends QObject<SurveyColumnModel>> rows )
		{
			super.setRows( rows );
		}
		
		@Override
		public boolean isCellEditable( int rowIndex , int columnIndex )
		{
			QObject<SurveyColumnModel> colModel = getRow( rowIndex );
			if( Boolean.TRUE.equals( colModel.get( SurveyColumnModel.fixed ) ) )
			{
				return columnIndex == VISIBLE_INDEX || columnIndex == DEFAULT_FORMAT_INDEX;
			}
			return super.isCellEditable( rowIndex , columnIndex );
		}
		
		@Override
		public void setValueAt( Object aValue , int rowIndex , int columnIndex )
		{
			if( columnIndex == TYPE_INDEX )
			{
				if( !Objects.equals( aValue , getValueAt( rowIndex , columnIndex ) ) )
				{
					super.setValueAt( aValue , rowIndex , columnIndex );
					FormatAndDisplayInfo<?> defaultFormat = null;
					if( aValue instanceof SurveyColumnType )
					{
						SurveyColumnType type = ( SurveyColumnType ) aValue;
						defaultFormat = NewSurveyTable.getDefaultFormat( type.valueClass );
					}
					
					setValueAt( defaultFormat , rowIndex , DEFAULT_FORMAT_INDEX );
				}
			}
			else
			{
				super.setValueAt( aValue , rowIndex , columnIndex );
			}
		}
		
		@Override
		protected QObject<SurveyColumnModel> getRow( int rowIndex )
		{
			return super.getRow( rowIndex );
		}
		
		@Override
		protected List<QObject<SurveyColumnModel>> getRows( )
		{
			return super.getRows( );
		}
	}
	
	public List<QObject<SurveyColumnModel>> getColumnModels( )
	{
		return new ArrayList<>( tableModel.getRows( ) );
	}
	
	public static interface Controller
	{
		public void applyButtonPressed( );
		
		public void okButtonPressed( );
	}
	
	public static class DefaultController implements Controller
	{
		JDialog						dialog;
		NewSurveyTableCustomizer	customizer;
		NewSurveyTable				table;
		
		private static final String	EMPTY_NAME	= "empty name";
		
		public DefaultController( NewSurveyTable table , NewSurveyTableCustomizer customizer )
		{
			super( );
			this.table = table;
			this.customizer = customizer;
		}
		
		public void showCustomizer( )
		{
			customizer.setColumnModels( table.getModel( ).getColumnModels( ) );
			
			Window owner = SwingUtilities.getWindowAncestor( table );
			if( dialog == null || dialog.getOwner( ) != owner )
			{
				dialog = new JDialog( owner );
				dialog.setTitle( "Customize table" );
				dialog.getContentPane( ).setLayout( new BorderLayout( ) );
				dialog.getContentPane( ).add( customizer , BorderLayout.CENTER );
			}
			Rectangle tableBounds = table.getBounds( );
			Point loc = tableBounds.getLocation( );
			SwingUtilities.convertPointToScreen( loc , table );
			tableBounds.setLocation( loc );
			
			dialog.pack( );
			
			Rectangle bounds = new Rectangle( dialog.getSize( ) );
			bounds.x = tableBounds.x + tableBounds.width - bounds.width;
			bounds.y = tableBounds.y;
			
			dialog.setBounds( bounds );
			dialog.setVisible( true );
		}
		
		@Override
		public void applyButtonPressed( )
		{
			try
			{
				doApply( );
			}
			catch( RuntimeException ex )
			{
			}
		}
		
		private void doApply( )
		{
			JTable customizeTable = customizer.getTable( );
			if( customizeTable.isEditing( ) )
			{
				customizeTable.getCellEditor( ).stopCellEditing( );
			}
			List<QObject<SurveyColumnModel>> columnModels = customizer.getColumnModels( );
			for( QObject<SurveyColumnModel> model : columnModels )
			{
				if( StringUtils.isNullOrEmpty( model.get( SurveyColumnModel.name ) ) )
				{
					JOptionPane.showMessageDialog(
							dialog ,
							"Column names may not be blank." ,
							"Unable to Apply" ,
							JOptionPane.WARNING_MESSAGE );
					
					throw new RuntimeException( EMPTY_NAME );
				}
			}
			table.getModel( ).setColumnModels( columnModels );
		}
		
		@Override
		public void okButtonPressed( )
		{
			try
			{
				doApply( );
			}
			catch( RuntimeException ex )
			{
				return;
			}
			dialog.dispose( );
		}
	}
}
