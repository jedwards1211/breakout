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
package org.andork.breakout;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.andork.awt.I18n;
import org.andork.awt.I18n.Localizer;
import org.andork.awt.layout.MultilineLabelHolder;
import org.andork.breakout.model.RootModel;
import org.andork.swing.JFileChooserUtils;
import org.andork.swing.OnEDT;

@SuppressWarnings( "serial" )
public class ExportProjectArchiveAction extends AbstractAction
{
	BreakoutMainView	mainView;
	
	JFileChooser		projectFileChooser;
	
	public ExportProjectArchiveAction( final BreakoutMainView mainView )
	{
		super( );
		this.mainView = mainView;
		
		new OnEDT( )
		{
			@Override
			public void run( ) throws Throwable
			{
				Localizer localizer = mainView.getI18n( ).forClass( ExportProjectArchiveAction.this.getClass( ) );
				localizer.setName( ExportProjectArchiveAction.this , "name" );
				
				projectFileChooser = new JFileChooser( );
				projectFileChooser.setAcceptAllFileFilterUsed( true );
				projectFileChooser.addChoosableFileFilter( new FileNameExtensionFilter( "Breakout Project Archive File (*.boa)" , "boa" ) );
			}
		};
	}
	
	@Override
	public void actionPerformed( ActionEvent e )
	{
		I18n i18n = mainView.getI18n( );
		Localizer localizer = i18n.forClass( getClass( ) );
		
		File directory = mainView.getRootModel( ).get( RootModel.currentArchiveFileChooserDirectory );
		if( directory == null )
		{
			File currentProjectFile = mainView.getRootModel( ).get( RootModel.currentProjectFile );
			if( currentProjectFile != null )
			{
				directory = currentProjectFile.getParentFile( );
			}
		}
		if( directory != null )
		{
			projectFileChooser.setCurrentDirectory( directory );
		}
		
		projectFileChooser.setDialogTitle( "Save Project Archive As" );
		
		File projectFile;
		
		do
		{
			int choice = projectFileChooser.showSaveDialog( mainView.getMainPanel( ) );
			projectFile = JFileChooserUtils.correctSelectedFileExtension( projectFileChooser );
			
			if( choice != JFileChooser.APPROVE_OPTION || projectFile == null )
			{
				return;
			}
			
			if( projectFile.exists( ) )
			{
				choice = JOptionPane.showConfirmDialog( mainView.getMainPanel( ) ,
						new MultilineLabelHolder( localizer.getFormattedString( "projectFileAlreadyExistsDialog.message" ,
								projectFile.getName( ) ) ).setWidth( 600 ) ,
						localizer.getString( "projectFileAlreadyExistsDialog.title" ) ,
						JOptionPane.YES_NO_CANCEL_OPTION );
				if( choice == JOptionPane.YES_OPTION )
				{
					break;
				}
				else if( choice != JOptionPane.NO_OPTION )
				{
					return;
				}
			}
			else
			{
				break;
			}
		} while( true );
		
		mainView.getRootModel( ).set( RootModel.currentArchiveFileChooserDirectory , projectFileChooser.getCurrentDirectory( ) );
		
		mainView.exportProjectArchive( projectFile );
	}
	
	public static final File pickDefaultSurveyFile( File projectFile )
	{
		String surveyFileName = projectFile.getName( );
		int extIndex = surveyFileName.lastIndexOf( '.' );
		if( extIndex > 0 )
		{
			surveyFileName = surveyFileName.substring( 0 , extIndex );
		}
		surveyFileName += "-survey.txt";
		return new File( projectFile.getParentFile( ) , surveyFileName );
	}
}
