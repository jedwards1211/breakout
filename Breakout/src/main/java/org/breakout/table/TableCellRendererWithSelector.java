package org.breakout.table;

import java.awt.BorderLayout;
import java.awt.Component;
import java.util.function.Function;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableCellRenderer;

import org.andork.swing.selector.DefaultSelector;

/**
 * Wraps another {@link TableCellRenderer} and combines it with a {@link DefaultSelector} dropdown at the right. The
 * dropdown can be used to display the {@link ParsedTextWithType#getType() type} of {@link ParsedTextWithType} cell
 * values.
 */
@SuppressWarnings( "serial" )
public class TableCellRendererWithSelector extends JPanel implements TableCellRenderer
{
	private TableCellRenderer		wrapped;
	private Function<Object, ?>		selectionGetter;
	private DefaultSelector<Object>	selector;

	/**
	 * @param wrapped
	 *            the {@link TableCellRenderer} to wrap. The component it returns from
	 *            {@link #getTableCellRendererComponent(JTable, Object, boolean, boolean, int, int)
	 *            getTableCellRendererComponent(...)} will be added as a child to this
	 *            {@link TableCellRendererWithSelector}.
	 * @param selectionGetter
	 *            takes the cell value and returns the value to select in the dropdown.
	 */
	public TableCellRendererWithSelector( TableCellRenderer wrapped , Function<Object, ?> selectionGetter )
	{
		this( wrapped , selectionGetter , createDefaultSelector( ) );
	}

	/**
	 * @param wrapped
	 *            the {@link TableCellRenderer} to wrap. The component it returns from
	 *            {@link #getTableCellRendererComponent(JTable, Object, boolean, boolean, int, int)
	 *            getTableCellRendererComponent(...)} will be added as a child to this
	 *            {@link TableCellRendererWithSelector}.
	 * @param selectionGetter
	 *            takes the cell value and returns the value to select in the dropdown.
	 * @param selector
	 *            the {@link DefaultSelector} to use for the dropdown.
	 */
	public TableCellRendererWithSelector( TableCellRenderer wrapped , Function<Object, ?> selectionGetter ,
		DefaultSelector<Object> selector )
	{
		super( );
		this.wrapped = wrapped;
		this.selector = selector;
		this.selectionGetter = selectionGetter;
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
