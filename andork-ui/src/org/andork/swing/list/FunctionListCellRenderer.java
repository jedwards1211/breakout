package org.andork.swing.list;

import java.awt.Component;
import java.util.function.Function;

import javax.swing.JList;
import javax.swing.ListCellRenderer;

@SuppressWarnings( "rawtypes" )
public class FunctionListCellRenderer implements ListCellRenderer
{
	Function			valueFn;
	ListCellRenderer	wrapped;

	public FunctionListCellRenderer( Function valueFn , ListCellRenderer wrapped )
	{
		super( );
		this.valueFn = valueFn;
		this.wrapped = wrapped;
	}

	@SuppressWarnings( "unchecked" )
	@Override
	public Component getListCellRendererComponent( JList list , Object value , int index ,
		boolean isSelected , boolean cellHasFocus )
	{
		return wrapped.getListCellRendererComponent( list , valueFn.apply( value ) , index , isSelected , cellHasFocus );
	}
}
