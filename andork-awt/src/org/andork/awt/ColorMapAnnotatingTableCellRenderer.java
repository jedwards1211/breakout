package org.andork.awt;

import java.awt.Color;
import java.awt.Component;
import java.awt.image.renderable.RenderableImage;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

@SuppressWarnings( "serial" )
public class ColorMapAnnotatingTableCellRenderer extends DefaultTableCellRenderer implements AnnotatingTableCellRenderer
{
	final Map<Object, Color>	annotationColors	= new HashMap<Object, Color>( );
	
	public ColorMapAnnotatingTableCellRenderer( )
	{
		
	}
	
	public ColorMapAnnotatingTableCellRenderer( Map<?, Color> annotationColors )
	{
		this( );
		setAnnotationColors( annotationColors );
	}
	
	public void setAnnotationColors( Map<?, Color> annotationColors )
	{
		this.annotationColors.clear( );
		this.annotationColors.putAll( annotationColors );
	}
	
	@Override
	public Component getTableCellRendererComponent( JTable table , Object value , Object annotation , boolean isSelected , boolean hasFocus , int row , int column )
	{
		Component renderer = getTableCellRendererComponent( table , value , isSelected , hasFocus , row , column );
		if( !isSelected )
		{
			renderer.setBackground( table.getBackground( ) );
			if( annotation != null )
			{
				Color bg = annotationColors.get( annotation );
				if( bg != null )
				{
					renderer.setBackground( bg );
				}
			}
		}
		return renderer;
	}
	
}
