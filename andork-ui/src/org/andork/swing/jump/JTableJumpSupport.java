package org.andork.swing.jump;

import java.awt.Rectangle;

import javax.swing.JTable;

import org.andork.swing.jump.JumpBar.JumpSupport;

public class JTableJumpSupport implements JumpSupport
{
	JTable	table;
	
	public JTableJumpSupport( JTable table )
	{
		super( );
		this.table = table;
	}

	@Override
	public void scrollElementToVisible( int index )
	{
		Rectangle visibleRect = table.getVisibleRect( );
		Rectangle cellRect = table.getCellRect( index , 0 , true );
		
		visibleRect.y = cellRect.y + cellRect.height / 2 - visibleRect.height / 2;
		
		table.scrollRectToVisible( visibleRect );
	}
}
