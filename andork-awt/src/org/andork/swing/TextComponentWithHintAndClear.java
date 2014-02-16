package org.andork.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.text.JTextComponent;

import org.andork.swing.event.EasyDocumentListener;


@SuppressWarnings( "serial" )
public class TextComponentWithHintAndClear extends JPanel
{
	public final JTextComponent	textComponent;
	private JButton				clearButton;
	private JLabel				hintLabel;
	
	public TextComponentWithHintAndClear( String hint )
	{
		this( new JTextField( ) , hint );
	}
	
	public TextComponentWithHintAndClear( JTextComponent textComponent , String hint )
	{
		this.textComponent = textComponent;
		
		clearButton = new JButton( );
		ModernStyleClearButton.createClearButton( clearButton );
		
		hintLabel = new JLabel( hint );
		hintLabel.setForeground( Color.LIGHT_GRAY );
		hintLabel.setFont( hintLabel.getFont( ).deriveFont( Font.ITALIC ) );
		hintLabel.setOpaque( false );
		
		textComponent.setLayout( new BorderLayout( ) );
		textComponent.add( hintLabel , BorderLayout.WEST );
		
		setLayout( new BorderLayout( ) );
		add( textComponent , BorderLayout.CENTER );
		add( clearButton , BorderLayout.EAST );
		
		setBorder( textComponent.getBorder( ) );
		setBackground( textComponent.getBackground( ) );
		textComponent.setBorder( null );
		
		clearButton.addActionListener( new ActionListener( )
		{
			@Override
			public void actionPerformed( ActionEvent e )
			{
				TextComponentWithHintAndClear.this.textComponent.setText( "" );
			}
		} );
		
		textComponent.getDocument( ).addDocumentListener( new EasyDocumentListener( )
		{
			@Override
			public void documentChanged( DocumentEvent e )
			{
				updateHintLabelVisible( );
			}
		} );
		
		updateHintLabelVisible( );
	}
	
	private void updateHintLabelVisible( )
	{
		String text = textComponent.getText( );
		hintLabel.setVisible( text == null || "".equals( text ) );
	}
}
