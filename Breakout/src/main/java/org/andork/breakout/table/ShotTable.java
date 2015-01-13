package org.andork.breakout.table;

import java.util.Vector;

import javax.swing.ListSelectionModel;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import org.andork.swing.table.AnnotatingJTable;

@SuppressWarnings( "serial" )
public class ShotTable extends AnnotatingJTable
{

	public ShotTable( )
	{
		super( );
		// TODO Auto-generated constructor stub
	}

	public ShotTable( int numRows , int numColumns )
	{
		super( numRows , numColumns );
		// TODO Auto-generated constructor stub
	}

	public ShotTable( Object[ ][ ] rowData , Object[ ] columnNames )
	{
		super( rowData , columnNames );
		// TODO Auto-generated constructor stub
	}

	public ShotTable( TableModel dm , TableColumnModel cm , ListSelectionModel sm )
	{
		super( dm , cm , sm );
		// TODO Auto-generated constructor stub
	}

	public ShotTable( TableModel dm , TableColumnModel cm )
	{
		super( dm , cm );
		// TODO Auto-generated constructor stub
	}

	public ShotTable( TableModel dm )
	{
		super( dm );
		// TODO Auto-generated constructor stub
	}

	public ShotTable( Vector rowData , Vector columnNames )
	{
		super( rowData , columnNames );
		// TODO Auto-generated constructor stub
	}

}
