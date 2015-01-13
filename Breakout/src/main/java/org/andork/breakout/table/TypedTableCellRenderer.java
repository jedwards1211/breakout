package org.andork.breakout.table;

import java.awt.BorderLayout;
import java.awt.Component;
import java.util.List;
import java.util.function.Function;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import org.andork.swing.selector.DefaultSelector;

@SuppressWarnings( "serial" )
public class TypedTableCellRenderer extends JPanel implements TableCellRenderer
{
	TableCellRenderer		wrapped;
	Function<Object, ?>		typeGetter;
	DefaultSelector<Object>	typeSelector;

	public TypedTableCellRenderer( TableCellRenderer wrapped , Function<Object, ?> typeGetter )
	{
		this( wrapped , typeGetter , createDefaultSelector( ) );
	}

	public TypedTableCellRenderer( TableCellRenderer wrapped , Function<Object, ?> typeGetter ,
		DefaultSelector<Object> typeSelector )
	{
		super( );
		this.wrapped = wrapped;
		this.typeSelector = typeSelector;
		this.typeGetter = typeGetter;
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

		Object type = typeGetter.apply( value );
		typeSelector.setSelection( type );
		add( wrappedComp , BorderLayout.CENTER );
		add( typeSelector.comboBox( ) , BorderLayout.EAST );
		if( wrappedComp instanceof JComponent )
		{
			setToolTipText( ( ( JComponent ) wrappedComp ).getToolTipText( ) );
		}
		return this;
	}

	public DefaultSelector<Object> typeSelector( )
	{
		return typeSelector;
	}

	public void setAvailableTypes( List<?> types )
	{
		typeSelector.setAvailableValues( types );
	}
}
