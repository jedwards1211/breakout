package org.andork.breakout.table;

import java.awt.Component;
import java.util.function.BiConsumer;

import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

public class PostmodTableCellRenderer implements TableCellRenderer
{
	TableCellRenderer				wrapped;
	BiConsumer<JTable, Component>	postModifier;

	public PostmodTableCellRenderer( TableCellRenderer wrapped , BiConsumer<JTable, Component> postModifier )
	{
		super( );
		this.wrapped = wrapped;
		this.postModifier = postModifier;
	}

	@Override
	public Component getTableCellRendererComponent( JTable table , Object value , boolean isSelected ,
		boolean hasFocus , int row , int column )
	{
		Component renderer = wrapped.getTableCellRendererComponent( table , value , isSelected , hasFocus , row ,
			column );
		postModifier.accept( table , renderer );
		return renderer;
	}
}
