package org.andork.breakout.table;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import org.andork.awt.GridBagWizard;
import org.andork.awt.GridBagWizard.DefaultAutoInsets;
import org.andork.breakout.table.NewSurveyTableModel.ColumnModel;
import org.andork.breakout.table.NewSurveyTableModel.CustomColumnType;
import org.andork.q.QObject;
import org.andork.swing.selector.DefaultSelector;
import org.andork.swing.selector.FormatAndDisplayInfoListCellRenderer;
import org.andork.swing.table.FormatAndDisplayInfo;
import org.andork.swing.table.NiceTableModel;
import org.andork.swing.table.NiceTableModel.Column;
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
	
	public NewSurveyTableCustomizer( )
	{
		initComponents( );
		initLayout( );
		initListeners( );
		
		updateRemoveButtonEnabled( );
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
		
		table.setDefaultRenderer( Boolean.class , new JCheckBoxBooleanCellRenderer( ) );
		table.setDefaultRenderer( CustomColumnType.class , new JComboBoxTableCellRenderer( ) );
		table.setDefaultRenderer( FormatAndDisplayInfo.class ,
				new DefaultSelectorTableCellRenderer( createDefaultFormatSelector( ) )
				{
					@Override
					public Component getTableCellRendererComponent( JTable table , Object value , boolean isSelected , boolean hasFocus , int row , int column )
					{
						CustomColumnType type = ( CustomColumnType ) tableModel.getValueAt( row , CustomizationTableModel.TYPE_INDEX );
						selector.setAvailableValues( NewSurveyTable.getAvailableFormats( type == null ? null : type.valueClass ) );
						return super.getTableCellRendererComponent( table , value , isSelected , hasFocus , row , column );
					}
				} );
		
		DefaultSelector<CustomColumnType> typeSelector = new DefaultSelector<>( );
		typeSelector.setAvailableValues( CustomColumnType.values( ) );
		
		JCheckBox editorCheckBox = new JCheckBox( );
		editorCheckBox.setHorizontalAlignment( JCheckBox.CENTER );
		table.setDefaultEditor( Boolean.class , new DefaultCellEditor( editorCheckBox ) );
		table.setDefaultEditor( CustomColumnType.class , new DefaultCellEditor( typeSelector.getComboBox( ) ) );
		
		final DefaultSelector<FormatAndDisplayInfo<?>> formatSelector = createDefaultFormatSelector( );
		
		table.setDefaultEditor( FormatAndDisplayInfo.class , new DefaultCellEditor( formatSelector.getComboBox( ) )
		{
			
			@Override
			public Component getTableCellEditorComponent( JTable table , Object value , boolean isSelected , int row , int column )
			{
				CustomColumnType type = ( CustomColumnType ) tableModel.getValueAt( row , CustomizationTableModel.TYPE_INDEX );
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
				QObject<ColumnModel> newColumn = ColumnModel.instance.newObject( );
				newColumn.set( ColumnModel.visible , true );
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
					QObject<ColumnModel> colModel = tableModel.getRow( modelIndex );
					if( !Boolean.TRUE.equals( colModel.get( ColumnModel.fixed ) ) )
					{
						tableModel.removeRow( modelIndex );
					}
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
	
	public void setColumnModels( Collection<? extends QObject<ColumnModel>> columns )
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
	
	private class CustomizationTableModel extends NiceTableModel<QObject<ColumnModel>>
	{
		static final int	VISIBLE_INDEX			= 0;
		static final int	NAME_INDEX				= 1;
		static final int	TYPE_INDEX				= 2;
		static final int	DEFAULT_FORMAT_INDEX	= 3;
		
		public CustomizationTableModel( )
		{
			addColumn( QObjectColumn.newInstance( ColumnModel.instance , ColumnModel.visible ) );
			addColumn( QObjectColumn.newInstance( ColumnModel.instance , ColumnModel.name ) );
			addColumn( QObjectColumn.newInstance( ColumnModel.instance , ColumnModel.type ) );
			addColumn( QObjectColumn.newInstance( ColumnModel.instance , ColumnModel.defaultFormat ) );
		}
		
		@Override
		public void addRow( QObject<ColumnModel> row )
		{
			super.addRow( row );
		}
		
		@Override
		public void removeRow( int index )
		{
			super.removeRow( index );
		}
		
		@Override
		public void setRows( Collection<? extends QObject<ColumnModel>> rows )
		{
			super.setRows( rows );
		}
		
		@Override
		public boolean isCellEditable( int rowIndex , int columnIndex )
		{
			QObject<ColumnModel> colModel = getRow( rowIndex );
			if( Boolean.TRUE.equals( colModel.get( ColumnModel.fixed ) ) )
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
					if( aValue instanceof CustomColumnType )
					{
						CustomColumnType type = ( CustomColumnType ) aValue;
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
		protected QObject<ColumnModel> getRow( int rowIndex )
		{
			// TODO Auto-generated method stub
			return super.getRow( rowIndex );
		}
		
		@Override
		protected List<QObject<ColumnModel>> getRows( )
		{
			return super.getRows( );
		}
	}
	
	public List<QObject<ColumnModel>> getColumnModels( )
	{
		return new ArrayList<>( tableModel.getRows( ) );
	}
}
