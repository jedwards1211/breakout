package org.andork.swing.async;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog.ModalityType;
import java.awt.Window;

import javax.swing.JDialog;
import javax.swing.SwingUtilities;

public abstract class SelfReportingTask extends Task
{
	private Component	dialogParent;
	private JDialog		dialog;
	
	public SelfReportingTask( Component dialogParent )
	{
		super( );
		this.dialogParent = dialogParent;
	}
	
	public SelfReportingTask( String status , Component dialogParent )
	{
		super( status );
		this.dialogParent = dialogParent;
	}
	
	protected JDialog createDialog( final Window owner )
	{
		TaskPane taskPane = new TaskPane( SelfReportingTask.this );
		JDialog dialog = new JDialog( owner );
		dialog.setModalityType( ModalityType.DOCUMENT_MODAL );
		dialog.getContentPane( ).add( taskPane , BorderLayout.CENTER );
		dialog.pack( );
		return dialog;
	}
	
	@Override
	protected void execute( ) throws Exception
	{
		SwingUtilities.invokeLater( new Runnable( )
		{
			@Override
			public void run( )
			{
				Window owner = null;
				if( dialogParent instanceof Window )
				{
					owner = ( Window ) dialogParent;
				}
				else if( dialogParent != null )
				{
					owner = SwingUtilities.getWindowAncestor( dialogParent );
				}
				dialog = createDialog( owner );
				dialog.setLocationRelativeTo( owner );
				dialog.setVisible( true );
			}
		} );
		
		try
		{
			duringDialog( );
		}
		finally
		{
			SwingUtilities.invokeLater( new Runnable( )
			{
				@Override
				public void run( )
				{
					dialog.dispose( );
				}
			} );
		}
	}
	
	protected abstract void duringDialog( ) throws Exception;
}
