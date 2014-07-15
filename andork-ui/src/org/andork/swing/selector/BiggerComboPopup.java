package org.andork.swing.selector;

import java.awt.Component;
import java.awt.Dimension;

import javax.swing.JComboBox;
import javax.swing.plaf.basic.BasicComboPopup;

public class BiggerComboPopup extends BasicComboPopup
{
	
	public BiggerComboPopup( JComboBox combo )
	{
		super( combo );
	}
	
	@Override
	public void show( Component invoker , int x , int y )
	{
		Dimension size = list.getPreferredSize( );
		size.width = Math.max( size.width , comboBox.getWidth( ) );
		scroller.setPreferredSize( size );
		scroller.setMaximumSize( size );
		pack( );
		super.show( invoker , x , y );
	}
}