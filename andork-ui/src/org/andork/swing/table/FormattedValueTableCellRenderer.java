package org.andork.swing.table;

import java.awt.Color;
import java.awt.Component;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

import org.andork.util.Format;
import org.andork.util.FormattedValue;

public class FormattedValueTableCellRenderer implements TableCellRenderer
{
	private TableCellRenderer								defaultCellRenderer;
	private final Map<Format, TableCellRenderer>			formatRenderers	= new HashMap<>( );
	private Color											defaultExceptionColor;
	private final Map<Class<? extends Exception>, Color>	exceptionColors	= new HashMap<>( );
	
	public FormattedValueTableCellRenderer( )
	{
		this( new DefaultTableCellRenderer( ) , Color.RED );
	}
	
	public FormattedValueTableCellRenderer( TableCellRenderer defaultCellRenderer , Color defaultExceptionColor )
	{
		if( defaultCellRenderer == null )
		{
			throw new IllegalArgumentException( "defaultCellRenderer must be non-null" );
		}
		this.defaultCellRenderer = defaultCellRenderer;
		this.defaultExceptionColor = defaultExceptionColor;
	}
	
	public void setFormatRenderer( Format format , TableCellRenderer renderer )
	{
		if( renderer == null )
		{
			formatRenderers.remove( format );
		}
		else
		{
			formatRenderers.put( format , renderer );
		}
	}
	
	public void setExceptionColor( Class<? extends Exception> exceptionClass , Color color )
	{
		if( color == null )
		{
			exceptionColors.remove( color );
		}
		else
		{
			exceptionColors.put( exceptionClass , color );
		}
	}
	
	@Override
	public Component getTableCellRendererComponent( JTable table , Object value , boolean isSelected , boolean hasFocus , int row , int column )
	{
		if( value instanceof FormattedValue )
		{
			FormattedValue fv = ( FormattedValue ) value;
			Format format = fv.getFormat( );
			TableCellRenderer formatRenderer;
			
			Component comp;
			
			if( fv.getValue( ) != null && format != null && ( formatRenderer = formatRenderers.get( format ) ) != null )
			{
				comp = formatRenderer.getTableCellRendererComponent( table , fv.getValue( ) , isSelected , hasFocus , row , column );
			}
			else
			{
				comp = defaultCellRenderer.getTableCellRendererComponent( table , value , isSelected , hasFocus , row , column );
			}
			if( fv.getFormatException( ) != null )
			{
				Color color = exceptionColors.get( fv.getFormatException( ).getClass( ) );
				if( color == null )
				{
					color = defaultExceptionColor;
				}
				if( color != null )
				{
					comp.setBackground( color );
				}
			}
			return comp;
		}
		else
		{
			return defaultCellRenderer.getTableCellRendererComponent( table , value , isSelected , hasFocus , row , column );
		}
	}
	
}
