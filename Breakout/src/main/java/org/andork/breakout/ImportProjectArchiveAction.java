package org.andork.breakout;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.andork.awt.I18n.Localizer;
import org.andork.breakout.model.RootModel;
import org.andork.swing.OnEDT;

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
				fileChooser.addChoosableFileFilter( new FileNameExtensionFilter( "Breakout Project Archive (*.boa)" , "boa" ) );
			}
		};
	}
	
	@Override
	public void actionPerformed( ActionEvent e )
	{
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
			fileChooser.setCurrentDirectory( directory );
		}
		
		int choice = fileChooser.showOpenDialog( mainView.getMainPanel( ) );
		
		if( choice != JFileChooser.APPROVE_OPTION )
		{
			return;
		}
		
		mainView.getRootModel( ).set( RootModel.currentArchiveFileChooserDirectory , fileChooser.getCurrentDirectory( ) );
		
		File file = fileChooser.getSelectedFile( );
		mainView.importProjectArchive( file );
	}
}
