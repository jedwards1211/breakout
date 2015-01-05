package org.andork.breakout.table;

import org.andork.swing.table.QObjectList;
import org.andork.swing.table.QObjectListTableModel;

@SuppressWarnings( "serial" )
public class ShotTableModel extends QObjectListTableModel<Shot>
{
	public ShotTableModel( )
	{
	}

	public ShotTableModel( QObjectList<Shot> model )
	{
		super( model );
	}
}
