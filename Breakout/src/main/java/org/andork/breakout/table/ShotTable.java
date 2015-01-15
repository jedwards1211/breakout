package org.andork.breakout.table;

import javax.swing.table.TableModel;

import org.andork.swing.table.AnnotatingJTable;
import org.andork.swing.table.MouseInputTableCellRenderer;

/**
 * The view for a {@link ShotTableModel}.
 * 
 * @author James
 */
@SuppressWarnings( "serial" )
public class ShotTable extends AnnotatingJTable
{

	public ShotTable( )
	{
		super( );
		addMouseListener( new MouseInputTableCellRenderer.Controller( ) );
	}

	public ShotTable( TableModel dm )
	{
		super( dm );
		addMouseListener( new MouseInputTableCellRenderer.Controller( ) );
	}
}
