package org.andork.swing;

import java.awt.Component;
import java.util.function.Function;

import javax.swing.JList;
import javax.swing.ListCellRenderer;

public class CellRenderers
{
	public static <E> ListCellRenderer<E> map( Function<? super E, ? extends E> valueFn , ListCellRenderer<? super E> renderer )
	{
		return new ListCellRenderer<E>( )
		{
			@Override
			public Component getListCellRendererComponent( JList<? extends E> list , E value , int index , boolean isSelected , boolean cellHasFocus )
			{
				return renderer.getListCellRendererComponent( list , valueFn.apply( value ) , index , isSelected , cellHasFocus );
			}
		};
	}
}
