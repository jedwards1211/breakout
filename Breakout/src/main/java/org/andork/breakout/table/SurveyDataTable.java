package org.andork.breakout.table;

import javax.swing.table.TableModel;

import org.andork.swing.table.AnnotatingJTable;
import org.andork.swing.table.MouseInputTableCellRenderer;

/**
 * The view for a {@link SurveyDataTableModel}.
 * 
 * @author James
 */
@SuppressWarnings( "serial" )
public class SurveyDataTable extends AnnotatingJTable
{

	public SurveyDataTable( )
	{
		super( );
		addMouseListener( new MouseInputTableCellRenderer.Controller( ) );
	}

	public SurveyDataTable( TableModel dm )
	{
		super( dm );
		addMouseListener( new MouseInputTableCellRenderer.Controller( ) );
	}
}
