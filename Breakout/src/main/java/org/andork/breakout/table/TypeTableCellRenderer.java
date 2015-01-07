package org.andork.breakout.table;

import java.awt.BorderLayout;
import java.awt.Component;
import java.util.List;
import java.util.function.Function;

import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import org.andork.swing.selector.DefaultSelector;

@SuppressWarnings( "serial" )
public class TypeTableCellRenderer extends JPanel implements TableCellRenderer
{
	TableCellRenderer			wrapped;
	Function<Object, Object>	typeFunction;
	DefaultSelector<Object>		typeSelector;

	public TypeTableCellRenderer( TableCellRenderer wrapped , Function<Object, Object> typeFunction )
	{
		this( wrapped , typeFunction , createDefaultSelector( ) );
	}

	public TypeTableCellRenderer( TableCellRenderer wrapped , Function<Object, Object> typeFunction ,
		DefaultSelector<Object> typeSelector )
	{
		super( );
		this.wrapped = wrapped;
		this.typeSelector = typeSelector;
		this.typeFunction = typeFunction;
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

		Object type = value == null ? null : typeFunction.apply( value );
		typeSelector.setSelection( type );
		add( wrappedComp , BorderLayout.CENTER );
		add( typeSelector.getComboBox( ) , BorderLayout.EAST );
		return this;
	}

	public DefaultSelector<Object> typeSelector( )
	{
		return typeSelector;
	}

	public void setAvailableTypes( List<Object> types )
	{
		typeSelector.setAvailableValues( types );
	}
}
