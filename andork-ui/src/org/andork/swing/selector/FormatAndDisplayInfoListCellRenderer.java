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
package org.andork.swing.selector;

import java.awt.Component;
import java.awt.Font;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;

import org.andork.swing.FormatAndDisplayInfo;

@SuppressWarnings( "serial" )
public class FormatAndDisplayInfoListCellRenderer extends DefaultListCellRenderer
{
	public FormatAndDisplayInfoListCellRenderer( )
	{
	}
	
	@Override
	public Component getListCellRendererComponent( JList<?> list , Object value , int index , boolean isSelected , boolean cellHasFocus )
	{
		if( value instanceof FormatAndDisplayInfo )
		{
			FormatAndDisplayInfo<?> info = (org.andork.swing.FormatAndDisplayInfo<?> ) value;
			value = index < 0 ? info.name( ) : info.description( );
			Component comp = super.getListCellRendererComponent( list , value , index , isSelected , cellHasFocus );
			( ( JLabel ) comp ).setIcon( info.icon( ) );
			comp.setFont( comp.getFont( ).deriveFont( Font.PLAIN ) );
			return comp;
		}
		return super.getListCellRendererComponent( list , value , index , isSelected , cellHasFocus );
	}

	public static void setUpComboBox( JComboBox<?> comboBox )
	{
		comboBox.setRenderer( new FormatAndDisplayInfoListCellRenderer( ) );
		comboBox.setUI( new BiggerPopupComboBoxUI( ) );
	}
}
