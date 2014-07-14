package org.andork.breakout.table;

import javax.swing.Icon;

import org.andork.util.Format;

public interface SelfDescribingFormat<T> extends Format<T>
{
	public String getDescription( );
	
	public String getAbbreviation( );
	
	public Icon getIcon( );
}
