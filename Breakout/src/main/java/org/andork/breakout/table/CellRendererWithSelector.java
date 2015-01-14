package org.andork.breakout.table;

import java.awt.BorderLayout;
import java.awt.Component;
import java.util.function.Function;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import org.andork.swing.selector.DefaultSelector;

@SuppressWarnings( "serial" )
public class CellRendererWithSelector extends JPanel implements TableCellRenderer
{
	private TableCellRenderer		wrapped;
	private Function<Object, ?>		selectionGetter;
	private DefaultSelector<Object>	selector;

	public CellRendererWithSelector( TableCellRenderer wrapped , Function<Object, ?> typeGetter )
	{
		this( wrapped , typeGetter , createDefaultSelector( ) );
	}

	public CellRendererWithSelector( TableCellRenderer wrapped , Function<Object, ?> typeGetter ,
		DefaultSelector<Object> typeSelector )
	{
		super( );
		this.wrapped = wrapped;
		this.selector = typeSelector;
		this.selectionGetter = typeGetter;
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

		Object type = selectionGetter.apply( value );
		selector.setSelection( type );
		add( wrappedComp , BorderLayout.CENTER );
		add( selector.comboBox( ) , BorderLayout.EAST );
		if( wrappedComp instanceof JComponent )
		{
			setToolTipText( ( ( JComponent ) wrappedComp ).getToolTipText( ) );
		}
		return this;
	}

	public DefaultSelector<Object> selector( )
	{
		return selector;
	}
}
