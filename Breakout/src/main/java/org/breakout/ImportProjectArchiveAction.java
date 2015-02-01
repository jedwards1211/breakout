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
package org.breakout;

import java.awt.event.ActionEvent;
import java.io.File;
import java.nio.file.Path;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.andork.awt.I18n.Localizer;
import org.andork.swing.OnEDT;
import org.breakout.model.RootModel;

@SuppressWarnings( "serial" )
public class ImportProjectArchiveAction extends AbstractAction
{
	BreakoutMainView	mainView;

	JFileChooser		fileChooser;

	public ImportProjectArchiveAction( final BreakoutMainView mainView )
	{
		super( );
		this.mainView = mainView;

		new OnEDT( )
		{
			@Override
			public void run( ) throws Throwable
			{
				Localizer localizer = mainView.getI18n( ).forClass( ImportProjectArchiveAction.this.getClass( ) );
				localizer.setName( ImportProjectArchiveAction.this , "name" );

				fileChooser = new JFileChooser( );
				fileChooser.setAcceptAllFileFilterUsed( false );
				fileChooser.addChoosableFileFilter( new FileNameExtensionFilter( "Breakout Project Archive (*.boa)" ,
					"boa" ) );
			}
		};
	}

	@Override
	public void actionPerformed( ActionEvent e )
	{
		File directory = mainView.getRootModel( ).get( RootModel.currentArchiveFileChooserDirectory );
		if( directory == null )
		{
			Path currentProjectFile = mainView.getRootModel( ).get( RootModel.currentProjectFile );
			if( currentProjectFile != null )
			{
				directory = currentProjectFile.getParent( ).toFile( );
			}
		}
		if( directory != null )
		{
			fileChooser.setCurrentDirectory( directory );
		}

		int choice = fileChooser.showOpenDialog( mainView.getMainPanel( ) );

		if( choice != JFileChooser.APPROVE_OPTION )
		{
			return;
		}

		mainView.getRootModel( )
			.set( RootModel.currentArchiveFileChooserDirectory , fileChooser.getCurrentDirectory( ) );

		File file = fileChooser.getSelectedFile( );
		mainView.importProjectArchive( file );
	}
}
