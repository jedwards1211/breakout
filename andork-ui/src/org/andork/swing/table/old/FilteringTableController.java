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
