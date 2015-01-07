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
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.util.Collection;
import java.util.EventObject;
import java.util.List;

import javax.swing.DefaultCellEditor;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableCellEditor;

import org.andork.format.FormattedText;
import org.andork.swing.FormatAndDisplayInfo;
import org.andork.swing.selector.DefaultSelector;
import org.andork.swing.selector.FormatAndDisplayInfoListCellRenderer;
import org.andork.util.StringUtils;

@SuppressWarnings( "serial" )
public class FormattedTextWithSelectorTableCellEditor extends DefaultCellEditor
{
	final DefaultSelector<FormatAndDisplayInfo<?>>	formatSelector;
	FormatAndDisplayInfo<?>							defaultFormat;
	
	Border											compoundBorder;
	Border											outerBorder;
	
	public FormattedTextWithSelectorTableCellEditor( )
	{
		this( new JTextField( ) , new DefaultSelector<>( ) );
		FormatAndDisplayInfoListCellRenderer.setUpComboBox( formatSelector.getComboBox( ) );
	}
	
	public FormattedTextWithSelectorTableCellEditor( Collection<? extends FormatAndDisplayInfo<?>> formats )
	{
		this( );
		formatSelector.setAvailableValues( formats );
		defaultFormat = formats.isEmpty( ) ? null : formats.iterator( ).next( );
	}
	
	public FormattedTextWithSelectorTableCellEditor( JTextField textField , DefaultSelector<FormatAndDisplayInfo<?>> formatSelector )
	{
		super( textField );
		this.formatSelector = formatSelector;
		outerBorder = new EmptyBorder( 0 , 0 , 0 , 0 );
		textField.setBorder( compoundBorder = new CompoundBorder( outerBorder , new InnerBorder( ) ) );
		textField.setLayout( new Layout( ) );
		textField.add( formatSelector.getComboBox( ) , BorderLayout.EAST );
	}
	
	@Override
	public boolean isCellEditable( EventObject anEvent )
	{
		if( anEvent instanceof MouseEvent )
		{
			MouseEvent me = ( MouseEvent ) anEvent;
			JTable table = ( JTable ) me.getComponent( );
			int row = table.rowAtPoint( me.getPoint( ) );
			int column = table.columnAtPoint( me.getPoint( ) );
			if( row >= 0 && column >= 0 )
			{
				TableCellEditor editor = table.getCellEditor( table.rowAtPoint( me.getPoint( ) ) , table.columnAtPoint( me.getPoint( ) ) );
				if( editor == this )
				{
					Rectangle cellRect = table.getCellRect( row , column , true );
					Component editorComp = table.prepareEditor( editor , row , column );
					editorComp.setBounds( cellRect );
					editorComp.doLayout( );
					Component deepest = SwingUtilities.getDeepestComponentAt( editorComp , me.getX( ) - cellRect.x , me.getY( ) - cellRect.y );
					if( deepest == formatSelector.getComboBox( ) )
					{
						return true;
					}
				}
			}
		}
		return super.isCellEditable( anEvent );
	}
	
	public FormatAndDisplayInfo<?> getDefaultFormat( )
	{
		return defaultFormat;
	}
	
	public void setDefaultFormat( FormatAndDisplayInfo<?> defaultFormat )
	{
		this.defaultFormat = defaultFormat;
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
				format = ( org.andork.swing.FormatAndDisplayInfo<?> ) currentFormattedText.getFormat( );
			}
		}
		if( format == null )
		{
			format = defaultFormat;
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
	
	public void setAvailableFormats( Collection<? extends FormatAndDisplayInfo<?>> formats )
	{
		formatSelector.setAvailableValues( formats );
	}
}
