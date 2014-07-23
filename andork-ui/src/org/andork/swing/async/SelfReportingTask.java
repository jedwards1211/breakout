/*******************************************************************************
 * Breakout Cave Survey Visualizer
 *
 * Copyright (C) 2014 James Edwards
 *
 * jedwards8 at fastmail dot fm
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *******************************************************************************/
package org.andork.swing.async;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog.ModalityType;
import java.awt.Window;

import javax.swing.JDialog;
import javax.swing.SwingUtilities;

public abstract class SelfReportingTask extends Task
{
	protected Component	dialogParent;
	protected JDialog	dialog;
	
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
		dialog.setDefaultCloseOperation( JDialog.DO_NOTHING_ON_CLOSE );
		return dialog;
	}
	
	public void showDialogLater() {
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
				if( dialog == null )
				{
					dialog = createDialog( owner );
				}
				if( !dialog.isVisible( ) )
				{
					dialog.setLocationRelativeTo( owner );
					dialog.setVisible( true );
				}
			}
		} );
	}
	
	@Override
	protected void execute( ) throws Exception
	{
		showDialogLater( );
		
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
