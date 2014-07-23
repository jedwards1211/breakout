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
package org.andork.swing.table.old;

import java.awt.Color;
import java.util.regex.Pattern;

import javax.swing.JTable;
import javax.swing.event.DocumentEvent;
import javax.swing.text.JTextComponent;

import org.andork.swing.event.EasyDocumentListener;
import org.andork.swing.table.old.FilteringTableModel.Filter;
import org.andork.swing.table.old.FilteringTableModel.PatternFilter;

public class FilteringTableController
{
	JTextComponent	regexField;
	JTable			table;
	
	public FilteringTableController( JTextComponent regexField , JTable table )
	{
		super( );
		this.regexField = regexField;
		this.table = table;
		
		regexField.getDocument( ).addDocumentListener( new EasyDocumentListener( )
		{
			@Override
			public void documentChanged( DocumentEvent e )
			{
				onRegexChanged( );
			}
		} );
	}
	
	protected void onRegexChanged( )
	{
		Pattern p = null;
		try
		{
			if( regexField.getText( ) != null && !"".equals( regexField.getText( ) ) )
			{
				p = Pattern.compile( regexField.getText( ) );
			}
			regexField.setForeground( Color.BLACK );
		}
		catch( Exception ex )
		{
			regexField.setForeground( Color.RED );
			return;
		}
		
		if( table.getModel( ) instanceof FilteringTableModel )
		{
			FilteringTableModel model = ( FilteringTableModel ) table.getModel( );
			model.setFilter( p == null ? null : createFilter( p ) );
		}
	}
	
	protected Filter createFilter( Pattern p )
	{
		return new PatternFilter( p );
	}
}
