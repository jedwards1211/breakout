package org.andork.swing.table;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.DefaultCellEditor;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.JTextField;

import org.andork.util.Format;
import org.andork.util.FormattedText;
import org.andork.util.StringUtils;

public class MultiFormattedTextTableCellEditor extends DefaultCellEditor
{
	final List<Format>			availableFormats	= new ArrayList<Format>( );
	final Map<Format, Icon>		icons				= new HashMap<>( );
	final Map<Format, String>	abbrevs				= new HashMap<>( );
	final Map<Format, String>	descriptions		= new HashMap<>( );
	
	JPanel						rootPanel;
	JButton						formatButton		= new JButton( );
	
	Format						currentFormat;
	
	private static final Icon	dropdownIcon		= new ImageIcon( MultiFormattedTextTableCellEditor.class.getResource( "dropdown.png" ) );
	
	public MultiFormattedTextTableCellEditor( JTextField textField )
	{
		super( textField );
		textField.setBorder( null );
		textField.setRequestFocusEnabled( true );
		rootPanel = new JPanel( );
		rootPanel.setLayout( new BorderLayout( 0 , 0 ) );
		rootPanel.setFocusable( false );
		formatButton.setMargin( new Insets( 2 , 2 , 2 , 2 ) );
		formatButton.setFocusable( false );
		formatButton.addActionListener( new ActionListener( )
		{
			@Override
			public void actionPerformed( ActionEvent e )
			{
				JPopupMenu popup = new JPopupMenu( );
				for( Format format : availableFormats )
				{
					JMenuItem item = new JMenuItem( descriptions.get( format ) , icons.get( format ) );
					item.setFont( item.getFont( ).deriveFont( Font.PLAIN ) );
					popup.add( item );
					
					item.addActionListener( new ActionListener( )
					{
						@Override
						public void actionPerformed( ActionEvent e )
						{
							setCurrentFormat( format );
						}
					} );
				}
				
				popup.show( formatButton , 0 , formatButton.getHeight( ) );
			}
		} );
		rootPanel.add( textField , BorderLayout.CENTER );
		rootPanel.add( formatButton , BorderLayout.EAST );
	}
	
	public void requestTextFieldFocus( )
	{
		getComponent( ).requestFocus( );
	}
	
	public void addFormat( Format format , String description , String abbrev , Icon icon )
	{
		availableFormats.add( format );
		icons.put( format , icon );
		abbrevs.put( format , abbrev );
		descriptions.put( format , description );
	}
	
	private void setCurrentFormat( Format format )
	{
		currentFormat = format;
		Icon icon = currentFormat == null ? null : icons.get( currentFormat );
		if( icon == null )
		{
			icon = dropdownIcon;
		}
		formatButton.setIcon( icon );
		formatButton.setText( currentFormat == null ? null : abbrevs.get( currentFormat ) );
	}
	
	@Override
	public Object getCellEditorValue( )
	{
		Object value = super.getCellEditorValue( );
		if( StringUtils.isNullOrEmpty( value ) )
		{
			return null;
		}
		FormattedText result = new FormattedText( currentFormat );
		result.setText( value.toString( ) );
		return result;
	}
	
	@Override
	public Component getTableCellEditorComponent( JTable table , Object value , boolean isSelected , int row , int column )
	{
		Format format = null;
		if( value instanceof FormattedText )
		{
			FormattedText currentFormattedText = ( FormattedText ) value;
			value = currentFormattedText.getText( );
			format = currentFormattedText.getFormat( );
		}
		if( format == null )
		{
			format = availableFormats.get( 0 );
		}
		setCurrentFormat( format );
		super.getTableCellEditorComponent( table , value , isSelected , row , column );
		return rootPanel;
	}
}
