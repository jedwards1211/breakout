package org.andork.breakout.table;

import java.awt.Component;
import java.util.Collections;

import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import org.andork.swing.selector.DefaultSelector;

public class DefaultSelectorTableCellRenderer implements TableCellRenderer
{
	protected final DefaultSelector	selector;
	
	public DefaultSelectorTableCellRenderer( )
	{
		this( new DefaultSelector<>( ) );
	}
	
	public DefaultSelectorTableCellRenderer( DefaultSelector selector )
	{
		super( );
		this.selector = selector;
	}
	
	@Override
	public Component getTableCellRendererComponent( JTable table , Object value , boolean isSelected , boolean hasFocus , int row , int column )
	{
		selector.setAvailableValues( value == null ? Collections.emptyList( ) : Collections.singletonList( value ) );
		selector.setSelection( value );
		return selector.getComboBox( );
	}
}
