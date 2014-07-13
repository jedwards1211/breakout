package org.andork.swing.table;

import java.awt.Color;
import java.awt.Component;
import java.util.Map;
import java.util.function.Function;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

@SuppressWarnings( "serial" )
public class ColoringAnnotatingTableCellRenderer extends DefaultTableCellRenderer implements AnnotatingTableCellRenderer
{
	Function<Object, Color>	colorer;
	
	public ColoringAnnotatingTableCellRenderer( )
	{
		
	}
	
	public ColoringAnnotatingTableCellRenderer( Map<?, Color> annotationColors )
	{
		this( );
		setAnnotationColors( annotationColors );
	}
	
	public void setAnnotationColors( Map<?, Color> annotationColors )
	{
		colorer = annotationColors == null ? null : o -> annotationColors.get( o );
	}
	
	public void setColorer( Function<Object, Color> colorer )
	{
		this.colorer = colorer;
	}
	
	@Override
	public Component getTableCellRendererComponent( JTable table , Object value , Object annotation , boolean isSelected , boolean hasFocus , int row , int column )
	{
		Component renderer = getTableCellRendererComponent( table , value , isSelected , hasFocus , row , column );
		if( !isSelected )
		{
			renderer.setBackground( table.getBackground( ) );
			if( annotation != null && colorer != null )
			{
				Color bg = colorer.apply( annotation );
				if( bg != null )
				{
					renderer.setBackground( bg );
				}
			}
		}
		return renderer;
	}
	
}
