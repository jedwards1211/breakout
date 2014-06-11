package org.andork.breakout;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.andork.awt.I18n.Localizer;
import org.andork.breakout.model.RootModel;
import org.andork.swing.OnEDT;

@SuppressWarnings( "serial" )
public class NewProjectAction extends AbstractAction
{
	BreakoutMainView	mainView;
	
	JFileChooser		fileChooser;
	
	public NewProjectAction( final BreakoutMainView mainView )
	{
		super( );
		this.mainView = mainView;
		
		new OnEDT( )
		{
			@Override
			public void run( ) throws Throwable
			{
				Localizer localizer = mainView.getI18n( ).forClass( NewProjectAction.this.getClass( ) );
				localizer.setName( NewProjectAction.this , "name" );
				
				fileChooser = new JFileChooser( );
				fileChooser.setAcceptAllFileFilterUsed( false );
				fileChooser.addChoosableFileFilter( new FileNameExtensionFilter( "Breakout YAML Project File (*.yaml)" , "yaml" ) );
			}
		};
	}
	
	@Override
	public void actionPerformed( ActionEvent e )
	{
		File currentProjectFile = mainView.getRootModel( ).get( RootModel.currentProjectFile );
		if( currentProjectFile != null )
		{
			fileChooser.setCurrentDirectory( currentProjectFile.getParentFile( ) );
		}
		
		int choice = fileChooser.showSaveDialog( mainView.getMainPanel( ) );
		
		if( choice != JFileChooser.APPROVE_OPTION )
		{
			return;
		}
		
		File file = fileChooser.getSelectedFile( );
		if( file.exists( ) )
		{
			choice = JOptionPane.showConfirmDialog( mainView.getMainPanel( ) , file + " already exists.  Would you like to open it?" , "New Project" , JOptionPane.OK_CANCEL_OPTION );
			if( choice != JOptionPane.OK_OPTION )
			{
				return;
			}
		}
		
		mainView.openProject( file );
	}
}
