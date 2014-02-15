package org.andork.swing.annotate;

import java.awt.Component;
import java.awt.Cursor;
import java.util.HashSet;
import java.util.Set;

import javax.swing.event.RowSorterEvent;
import javax.swing.event.RowSorterListener;

public class AnnotatingRowSorterCursorController implements RowSorterListener
{
	Component							target;
	
	Set<AnnotatingRowSorter<?, ?, ?>>	busySorters	= new HashSet<AnnotatingRowSorter<?, ?, ?>>( );
	
	public AnnotatingRowSorterCursorController( Component target )
	{
		super( );
		this.target = target;
	}
	
	@Override
	public void sorterChanged( RowSorterEvent e )
	{
		if( e.getSource( ) instanceof AnnotatingRowSorter )
		{
			AnnotatingRowSorter<?, ?, ?> sorter = (org.andork.swing.annotate.AnnotatingRowSorter<?, ?, ?> ) e.getSource( );
			if( sorter.isSortingInBackground( ) )
			{
				busySorters.add( sorter );
			}
			else
			{
				busySorters.remove( sorter );
			}
			
			target.setCursor( busySorters.isEmpty( ) ? null : Cursor.getPredefinedCursor( Cursor.WAIT_CURSOR ) );
		}
	}
}
