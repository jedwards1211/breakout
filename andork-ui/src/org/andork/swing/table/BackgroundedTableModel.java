package org.andork.swing.table;

import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.SwingUtilities;
import javax.swing.event.EventListenerList;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import org.andork.awt.CheckEDT;
import org.andork.swing.OnEDT;

public final class BackgroundedTableModel implements TableModel
{
	private BackgroundRunner			backgroundRunner;
	private final EventListenerList		listenerList			= new EventListenerList( );
	private final FgModelChangeHandler	fgModelChangeHandler	= new FgModelChangeHandler( );
	
	private final AtomicBoolean			runningInBackground		= new AtomicBoolean( false );
	
	TableModel							fgModel;
	TableModel							bgModel;
	
	@Override
	public int getRowCount( )
	{
		return fgModel.getRowCount( );
	}
	
	@Override
	public int getColumnCount( )
	{
		return fgModel.getColumnCount( );
	}
	
	@Override
	public String getColumnName( int columnIndex )
	{
		return fgModel.getColumnName( columnIndex );
	}
	
	@Override
	public Class<?> getColumnClass( int columnIndex )
	{
		return fgModel.getColumnClass( columnIndex );
	}
	
	@Override
	public boolean isCellEditable( int rowIndex , int columnIndex )
	{
		return fgModel.isCellEditable( rowIndex , columnIndex );
	}
	
	@Override
	public Object getValueAt( int rowIndex , int columnIndex )
	{
		return fgModel.getValueAt( rowIndex , columnIndex );
	}
	
	@Override
	public void setValueAt( Object aValue , int rowIndex , int columnIndex )
	{
		try
		{
			fgModelChangeHandler.allowChange = true;
			fgModel.setValueAt( aValue , rowIndex , columnIndex );
		}
		finally
		{
			fgModelChangeHandler.allowChange = false;
		}
		
		backgroundRunner.run( new BackgroundSingleSetter( aValue , rowIndex , columnIndex ) );
	}
	
	public void modifyInForeground( final Modifier modifier )
	{
		CheckEDT.checkEDT( );
		try
		{
			fgModelChangeHandler.allowChange = true;
			modifier.modify( fgModel );
		}
		finally
		{
			fgModelChangeHandler.allowChange = false;
		}
		
		backgroundRunner.run( new SafeBackgroundRunnable( )
		{
			@Override
			protected void execute( )
			{
				modifier.modify( bgModel );
			}
		} );
	}
	
	public void modifyInBackground( final Modifier modifier )
	{
		class Handler extends SafeBackgroundRunnable implements TableModelListener
		{
			List<TableModelEvent>	events	= new LinkedList<TableModelEvent>( );
			
			@Override
			public void tableChanged( TableModelEvent e )
			{
				events.add( e );
			}
			
			@Override
			protected void execute( )
			{
				try
				{
					bgModel.addTableModelListener( this );
					modifier.modify( bgModel );
				}
				finally
				{
					bgModel.removeTableModelListener( this );
				}
				
				swapModels( );
			}
			
		}
		backgroundRunner.run( new Handler( ) );
	}
	
	@Override
	public void addTableModelListener( TableModelListener l )
	{
		listenerList.add( TableModelListener.class , l );
	}
	
	@Override
	public void removeTableModelListener( TableModelListener l )
	{
		listenerList.remove( TableModelListener.class , l );
	}
	
	/**
	 * Forwards the given notification event to all <code>TableModelListeners</code> that registered themselves as listeners for this table model.
	 * 
	 * @param e
	 *            the event to be forwarded
	 * 
	 * @see #addTableModelListener
	 * @see TableModelEvent
	 * @see EventListenerList
	 */
	private void fireTableChanged( TableModelEvent e )
	{
		// Guaranteed to return a non-null array
		Object[ ] listeners = listenerList.getListenerList( );
		// Process the listeners last to first, notifying
		// those that are interested in this event
		for( int i = listeners.length - 2 ; i >= 0 ; i -= 2 )
		{
			if( listeners[ i ] == TableModelListener.class )
			{
				( ( TableModelListener ) listeners[ i + 1 ] ).tableChanged( e );
			}
		}
	}
	
	private void swapModels( )
	{
		new OnEDT( )
		{
			@Override
			public void run( ) throws Throwable
			{
				fgModel.removeTableModelListener( fgModelChangeHandler );
				TableModel swap = fgModel;
				fgModel = bgModel;
				bgModel = swap;
				fgModel.addTableModelListener( fgModelChangeHandler );
			}
		};
	}
	
	private class FgModelChangeHandler implements TableModelListener
	{
		boolean	allowChange;
		
		@Override
		public void tableChanged( TableModelEvent e )
		{
			if( e.getSource( ) == fgModel )
			{
				if( !allowChange )
				{
					throw new IllegalStateException( "changes to foreground model must be made through BackgroundedTableModel!" );
				}
				fireTableChanged( new TableModelEvent( BackgroundedTableModel.this ,
						e.getFirstRow( ) , e.getLastRow( ) , e.getColumn( ) , e.getType( ) ) );
			}
		}
	}
	
	private class BackgroundSingleSetter implements Runnable
	{
		private Object	aValue;
		private int		rowIndex;
		private int		columnIndex;
		
		private BackgroundSingleSetter( Object aValue , int rowIndex , int columnIndex )
		{
			super( );
			this.aValue = aValue;
			this.rowIndex = rowIndex;
			this.columnIndex = columnIndex;
		}
		
		@Override
		public void run( )
		{
			CheckEDT.checkNotEDT( );
			bgModel.setValueAt( aValue , rowIndex , columnIndex );
		}
	}
	
	private class BackgroundMultiSetter implements Runnable , TableModelListener
	{
		Object[ ][ ]	values;
		int[ ]			rowIndices;
		int[ ]			columnIndices;
		
		@Override
		public void run( )
		{
			for( int i = 0 ; i < values.length ; i++ )
			{
				Object[ ] valuesRow = values[ i ];
				for( int j = 0 ; j < valuesRow.length ; j++ )
				{
					bgModel.setValueAt( valuesRow[ j ] , rowIndices[ i ] , columnIndices[ j ] );
				}
			}
		}
		
		@Override
		public void tableChanged( TableModelEvent e )
		{
			
		}
	}
	
	private abstract class SafeBackgroundRunnable implements Runnable
	{
		@Override
		public final void run( )
		{
			if( !runningInBackground.compareAndSet( false , true ) )
			{
				throw new IllegalStateException( "Please only run background updates on a single thread" );
			}
			
			try
			{
				execute( );
			}
			finally
			{
				if( !runningInBackground.compareAndSet( true , false ) )
				{
					throw new IllegalStateException( "Please only run backround updates on a single thread" );
				}
			}
		}
		
		protected abstract void execute( );
	}
	
	public static interface BackgroundRunner
	{
		public void run( Runnable r );
	}
	
	public static interface Modifier
	{
		public void modify( TableModel model );
	}
	
	public static class SetValueAt implements Modifier
	{
		private final Object	aValue;
		private final int		rowIndex;
		private final int		columnIndex;
		
		private SetValueAt( Object aValue , int rowIndex , int columnIndex )
		{
			super( );
			this.aValue = aValue;
			this.rowIndex = rowIndex;
			this.columnIndex = columnIndex;
		}
		
		@Override
		public void modify( TableModel model )
		{
			model.setValueAt( aValue , rowIndex , columnIndex );
		}
	}
}
