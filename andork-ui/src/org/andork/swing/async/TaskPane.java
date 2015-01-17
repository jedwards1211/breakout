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

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.border.EmptyBorder;

import org.andork.awt.GridBagWizard;
import org.andork.awt.GridBagWizard.DefaultAutoInsets;
import org.andork.event.BasicPropertyChangeListener;
import org.andork.swing.async.Task.State;

@SuppressWarnings( "serial" )
public class TaskPane extends JPanel
{
	Task								task;

	JLabel								statusLabel;
	JProgressBar						progressBar;
	JButton								cancelButton;

	private final ModelChangeHandler	modelChangeHandler	= new ModelChangeHandler( );

	public TaskPane( )
	{
		init( );
	}

	public TaskPane( Task child )
	{
		this( );
		setTask( child );
	}

	public void setTask( Task task )
	{
		if( this.task != task )
		{
			if( this.task != null )
			{
				this.task.changeSupport( ).removePropertyChangeListener( modelChangeHandler );
			}
			this.task = task;
			if( task != null )
			{
				task.changeSupport( ).addPropertyChangeListener( modelChangeHandler );
			}

			modelToView( );
		}
	}

	public Task getTask( )
	{
		return task;
	}

	protected void init( )
	{
		statusLabel = new JLabel( );
		progressBar = new JProgressBar( );
		cancelButton = new JButton( "Cancel" );

		progressBar.setPreferredSize( new Dimension( 400 , cancelButton.getPreferredSize( ).height ) );

		cancelButton.addActionListener( new ActionListener( )
		{
			@Override
			public void actionPerformed( ActionEvent e )
			{
				if( task != null )
				{
					task.cancel( );
				}
			}
		} );

		setBorder( new EmptyBorder( 10 , 10 , 10 , 10 ) );

		GridBagWizard gbw = GridBagWizard.create( this );

		gbw.defaults( ).autoinsets( new DefaultAutoInsets( 5 , 5 ) );
		gbw.put( progressBar ).xy( 0 , 1 ).north( ).fillboth( 1.0 , 0.0 );
		gbw.put( cancelButton ).rightOf( progressBar ).northwest( ).filly( 0.0 );
		gbw.put( statusLabel ).above( progressBar , cancelButton ).southwest( );

		modelToView( );
	}

	protected void modelToView( )
	{
		statusLabel.setText( task == null ? null : task.getStatus( ) );
		progressBar.setIndeterminate( task == null ? true : task.isIndeterminate( ) );
		progressBar.setMaximum( task == null ? 0 : task.getTotal( ) );
		progressBar.setValue( task == null ? 0 : task.getCompleted( ) );
		cancelButton.setEnabled( task == null ? false : task.isCancelable( ) && task.getState( ) != State.CANCELING
			&& task.getState( ) != State.CANCELED );
		cancelButton.setText( task != null && task.getState( ) == State.CANCELING ? "Canceling..." : "Cancel" );
	}

	private class ModelChangeHandler implements BasicPropertyChangeListener
	{
		private long	lastUpdate;

		@Override
		public void propertyChange( Object source , Object property , Object oldValue , Object newValue , int index )
		{
			long time = System.currentTimeMillis( );
			if( time - lastUpdate > 10 )
			{
				modelToView( );
			}
			lastUpdate = time;
		}
	}
}
