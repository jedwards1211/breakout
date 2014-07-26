package org.andork.swing.table;

import java.awt.BorderLayout;
import java.awt.Component;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import org.andork.format.FormattedText;
import org.andork.swing.FormatAndDisplayInfo;
import org.andork.swing.selector.DefaultSelector;
import org.andork.swing.selector.FormatAndDisplayInfoListCellRenderer;

@SuppressWarnings( "serial" )
public class FormattedTextWithSelectorTableCellRenderer extends JPanel implements TableCellRenderer
{
	TableCellRenderer							wrapped;
	DefaultSelector<FormatAndDisplayInfo<?>>	formatSelector;
	
	FormatAndDisplayInfo<?>						defaultFormat;
	
	public FormattedTextWithSelectorTableCellRenderer( TableCellRenderer wrapped )
	{
		this( wrapped , createDefaultSelector( ) );
	}
	
	public FormattedTextWithSelectorTableCellRenderer( TableCellRenderer wrapped , DefaultSelector<FormatAndDisplayInfo<?>> formatSelector )
	{
		super( );
		this.wrapped = wrapped;
		this.formatSelector = formatSelector;
		setLayout( new BorderLayout( ) );
	}
	
	private static DefaultSelector<FormatAndDisplayInfo<?>> createDefaultSelector( )
	{
		DefaultSelector<FormatAndDisplayInfo<?>> result = new DefaultSelector<>( );
		FormatAndDisplayInfoListCellRenderer.setUpComboBox( result.getComboBox( ) );
		result.setAllowSelectionNotAvailable( true );
		return result;
	}
	
	@Override
	public Component getTableCellRendererComponent( JTable table , Object value , boolean isSelected , boolean hasFocus , int row , int column )
	{
		Component wrappedComp = wrapped.getTableCellRendererComponent( table , value , isSelected , hasFocus , row , column );
		removeAll( );
		if( value == null || value instanceof FormattedText )
		{
			FormattedText ft = ( FormattedText ) value;
			if( ft != null && ft.getFormat( ) instanceof FormatAndDisplayInfo )
			{
				formatSelector.setSelection( ( org.andork.swing.FormatAndDisplayInfo<?> ) ft.getFormat( ) );
			}
			else if( ft == null || ft.getFormat( ) == null )
			{
				formatSelector.setSelection( defaultFormat );
			}
			add( wrappedComp , BorderLayout.CENTER );
			add( formatSelector.getComboBox( ) , BorderLayout.EAST );
			return this;
		}
		return wrappedComp;
	}
	
	public void setAvailableFormats( List<FormatAndDisplayInfo<?>> formats )
	{
		formatSelector.setAvailableValues( formats );
	}
	
	public void setDefaultFormat( FormatAndDisplayInfo<?> defaultFormat )
	{
		this.defaultFormat = defaultFormat;
	}
}
