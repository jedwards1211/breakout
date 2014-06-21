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
public class NewProjectAction extends AbstractAction
{
	BreakoutMainView	mainView;
	
	JFileChooser		projectFileChooser;
	
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
				
				projectFileChooser = new JFileChooser( );
				projectFileChooser.setAcceptAllFileFilterUsed( false );
				projectFileChooser.addChoosableFileFilter( new FileNameExtensionFilter( "Breakout Project File (*.bop)" , "bop" ) );
			}
		};
	}
	
	@Override
	public void actionPerformed( ActionEvent e )
	{
		I18n i18n = mainView.getI18n( );
		Localizer localizer = i18n.forClass( getClass( ) );
		
		File currentProjectFile = mainView.getRootModel( ).get( RootModel.currentProjectFile );
		if( currentProjectFile != null )
		{
			projectFileChooser.setCurrentDirectory( currentProjectFile.getParentFile( ) );
		}
		
		projectFileChooser.setDialogTitle( "Save New Project As" );
		
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
				File surveyFile = pickDefaultSurveyFile( projectFile );
				
				if( surveyFile.exists( ) )
				{
					choice = JOptionPane.showConfirmDialog( mainView.getMainPanel( ) ,
							new MultilineLabelHolder( localizer.getFormattedString( "surveyFileAlreadyExistsDialog.message" ,
									surveyFile.getName( ) ) ).setWidth( 600 ) ,
							localizer.getString( "surveyFileAlreadyExistsDialog.title" ) ,
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
				
				break;
			}
		} while( true );
		
		try
		{
			projectFile.createNewFile( );
		}
		catch( Exception ex )
		{
			ex.printStackTrace( );
			JOptionPane.showMessageDialog( mainView.getMainPanel( ) ,
					new MultilineLabelHolder( localizer.getFormattedString( "failedToCreateProjectFileDialog.message" ,
							projectFile.toString( ) + "<br><b>" + ex.getClass( ).getName( ) + "</b>: " + ex.getLocalizedMessage( ) ) )
							.setWidth( 600 ) ,
					localizer.getString( "failedToCreateProjectFileDialog.title" ) ,
					JOptionPane.ERROR_MESSAGE );
			return;
		}
		
		mainView.openProject( projectFile );
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
