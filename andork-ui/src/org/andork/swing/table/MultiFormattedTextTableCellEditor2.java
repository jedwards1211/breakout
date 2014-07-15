package org.andork.swing.table;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.util.Collection;

import javax.swing.DefaultCellEditor;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;

import org.andork.swing.selector.DefaultSelector;
import org.andork.swing.selector.FormatAndDisplayInfoListCellRenderer;
import org.andork.util.FormattedText;
import org.andork.util.StringUtils;

public class MultiFormattedTextTableCellEditor2 extends DefaultCellEditor
{
	final DefaultSelector<FormatAndDisplayInfo<?>>	formatSelector;
	
	Border											compoundBorder;
	Border											outerBorder;
	
	public MultiFormattedTextTableCellEditor2( Collection<? extends FormatAndDisplayInfo<?>> formats )
	{
		this( new JTextField( ) , new DefaultSelector<>( ) );
		FormatAndDisplayInfoListCellRenderer.setUpComboBox( formatSelector.getComboBox( ) );
		formatSelector.setAvailableValues( formats );
	}
	
	public MultiFormattedTextTableCellEditor2( JTextField textField , DefaultSelector<FormatAndDisplayInfo<?>> formatSelector )
	{
		super( textField );
		this.formatSelector = formatSelector;
		outerBorder = textField.getBorder( );
		textField.setBorder( compoundBorder = new CompoundBorder( outerBorder , new InnerBorder( ) ) );
		textField.setLayout( new Layout( ) );
		textField.add( formatSelector.getComboBox( ) , BorderLayout.EAST );
	}
	
	@Override
	public Object getCellEditorValue( )
	{
		Object value = super.getCellEditorValue( );
		if( StringUtils.isNullOrEmpty( value ) )
		{
			return null;
		}
		FormattedText result = new FormattedText( formatSelector.getSelection( ) );
		result.setText( value.toString( ) );
		return result;
	}
	
	@Override
	public Component getTableCellEditorComponent( JTable table , Object value , boolean isSelected , int row , int column )
	{
		FormatAndDisplayInfo<?> format = null;
		if( value instanceof FormattedText )
		{
			FormattedText currentFormattedText = ( FormattedText ) value;
			value = currentFormattedText.getText( );
			if( currentFormattedText.getFormat( ) instanceof FormatAndDisplayInfo )
			{
				format = ( FormatAndDisplayInfo<?> ) currentFormattedText.getFormat( );
			}
		}
		if( format == null )
		{
			format = formatSelector.getAvailableValues( ).get( 0 );
		}
		formatSelector.setSelection( format );
		JTextField textField = ( JTextField ) super.getTableCellEditorComponent( table , value , isSelected , row , column );
		textField.setBorder( compoundBorder );
		textField.setLayout( new Layout( ) );
		textField.add( formatSelector.getComboBox( ) , BorderLayout.EAST );
		return textField;
	}
	
	private class Layout extends BorderLayout
	{
		@Override
		public void layoutContainer( Container target )
		{
			Dimension targetSize = target.getSize( );
			Insets insets = outerBorder.getBorderInsets( target );
			Rectangle bounds = new Rectangle( formatSelector.getComboBox( ).getPreferredSize( ) );
			bounds.width = Math.min( bounds.width , targetSize.width - insets.left - insets.right );
			bounds.height = Math.min( bounds.height , targetSize.height - insets.top - insets.bottom );
			bounds.y = insets.top;
			bounds.x = targetSize.width - insets.right - bounds.width;
			
			formatSelector.getComboBox( ).setBounds( bounds );
		}
	}
	
	private class InnerBorder implements Border
	{
		@Override
		public void paintBorder( Component c , Graphics g , int x , int y , int width , int height )
		{
		}
		
		@Override
		public Insets getBorderInsets( Component c )
		{
			return new Insets( 0 , 0 , 0 , formatSelector.getComboBox( ).getPreferredSize( ).width );
		}
		
		@Override
		public boolean isBorderOpaque( )
		{
			return false;
		}
	}
}
