package org.andork.breakout.table;

import java.awt.BorderLayout;
import java.awt.Component;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import org.andork.swing.selector.DefaultSelector;

@SuppressWarnings( "serial" )
public class ParsedTextWithTypeTableCellRenderer extends JPanel implements TableCellRenderer
{
	TableCellRenderer		wrapped;
	DefaultSelector<Object>	typeSelector;

	public ParsedTextWithTypeTableCellRenderer( TableCellRenderer wrapped )
	{
		this( wrapped , createDefaultSelector( ) );
	}

	public ParsedTextWithTypeTableCellRenderer( TableCellRenderer wrapped , DefaultSelector<Object> typeSelector )
	{
		super( );
		this.wrapped = wrapped;
		this.typeSelector = typeSelector;
		setLayout( new BorderLayout( ) );
	}

	private static DefaultSelector<Object> createDefaultSelector( )
	{
		DefaultSelector<Object> result = new DefaultSelector<>( );
		result.setAllowSelectionNotAvailable( true );
		return result;
	}

	@Override
	public Component getTableCellRendererComponent( JTable table , Object value , boolean isSelected ,
		boolean hasFocus , int row , int column )
	{
		Component wrappedComp = wrapped.getTableCellRendererComponent( table , value , isSelected , hasFocus , row ,
			column );
		removeAll( );
		if( value == null || value instanceof ParsedTextWithType )
		{
			ParsedTextWithType pt = ( ParsedTextWithType ) value;
			typeSelector.setSelection( pt == null ? null : pt.type );
			add( wrappedComp , BorderLayout.CENTER );
			add( typeSelector.getComboBox( ) , BorderLayout.EAST );
			return this;
		}
		return wrappedComp;
	}

	public void setAvailableTypes( List<Object> types )
	{
		typeSelector.setAvailableValues( types );
	}
}
