package org.andork.breakout;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.crypto.dsig.CanonicalizationMethod;

import org.andork.awt.I18n;
import org.andork.awt.I18n.Localizer;
import org.andork.awt.layout.MultilineLabelHolder;
import org.andork.breakout.model.ProjectModel;
import org.andork.breakout.model.RootModel;
import org.andork.io.FileUtils;
import org.andork.snakeyaml.YamlObject;
import org.andork.snakeyaml.YamlObjectStringBimapper;
import org.andork.swing.OnEDT;

@SuppressWarnings( "serial" )
public class NewProjectAction extends AbstractAction
{
	BreakoutMainView	mainView;
	
	JFileChooser		projectFileChooser;
	JFileChooser		surveyFileChooser;
	
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
				projectFileChooser.addChoosableFileFilter( new FileNameExtensionFilter( "Breakout YAML Project File (*.yaml)" , "yaml" ) );
				
				surveyFileChooser = new JFileChooser( );
				surveyFileChooser.setAcceptAllFileFilterUsed( true );
				surveyFileChooser.addChoosableFileFilter( new FileNameExtensionFilter( "Tab-Delimited Survey Data (*.txt)" , "txt" ) );
			}
		};
	}
	
	@Override
	public void actionPerformed( ActionEvent e )
	{
		I18n i18n = mainView.getI18n( );
		Localizer localizer = i18n.forClass( getClass( ) );
		
		JLabel messageLabel = new JLabel( "<html>" + localizer.getString( "newProjectInfoDialog.message" ) + "</html>" );
		messageLabel.setMaximumSize( new Dimension( 600 , Integer.MAX_VALUE ) );
		String title = localizer.getString( "newProjectInfoDialog.title" );
		
		MultilineLabelHolder messageLabelHolder = new MultilineLabelHolder( messageLabel );
		messageLabelHolder.setWidth( 600 );
		
		DoNotShowAgainDialogs.showMessageDialog( mainView.getMainPanel( ) ,
				messageLabelHolder , title , JOptionPane.INFORMATION_MESSAGE ,
				i18n , mainView.getRootModelBinder( ) , RootModel.doNotShowNewProjectInfoDialog );
		
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
			projectFile = projectFileChooser.getSelectedFile( );
			
			if( choice != JFileChooser.APPROVE_OPTION || projectFile == null )
			{
				return;
			}
			
			if( projectFile.exists( ) )
			{
				choice = JOptionPane.showConfirmDialog( mainView.getMainPanel( ) ,
						new MultilineLabelHolder( localizer.getFormattedString( "projectFileAlreadyExistsDialog.message" , projectFile.toString( ) ) ).setWidth( 600 ) ,
						localizer.getString( "projectFileAlreadyExistsDialog.title" ) , JOptionPane.YES_NO_CANCEL_OPTION );
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
		
		if( !projectFile.exists( ) )
		{
			surveyFileChooser.setDialogTitle( "Save New Survey As" );
			surveyFileChooser.setCurrentDirectory( projectFileChooser.getCurrentDirectory( ) );
			
			File surveyFile;
			
			do
			{
				
				int choice = surveyFileChooser.showSaveDialog( mainView.getMainPanel( ) );
				
				if( choice != JFileChooser.APPROVE_OPTION )
				{
					return;
				}
				
				surveyFile = surveyFileChooser.getSelectedFile( );
				
				if( surveyFile.exists( ) )
				{
					choice = JOptionPane.showConfirmDialog( mainView.getMainPanel( ) ,
							surveyFile + " already exists.  Would you like to use its survey data in the new project?" ,
							"New Project" , JOptionPane.YES_NO_CANCEL_OPTION );
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
			
			YamlObject<ProjectModel> newProjectModel = ProjectModel.instance.newObject( );
			newProjectModel.set( ProjectModel.surveyFile , FileUtils.canonicalize( projectFile.getParentFile( ) , surveyFile ) );
			
			YamlObjectStringBimapper<ProjectModel> bimapper = YamlObjectStringBimapper.newInstance( ProjectModel.instance );
			String yaml = bimapper.map( newProjectModel );
			
			FileOutputStream fileOut = null;
			BufferedWriter writer = null;
			
			try
			{
				fileOut = new FileOutputStream( projectFile );
				writer = new BufferedWriter( new OutputStreamWriter( fileOut ) );
				writer.write( yaml );
			}
			catch( Exception ex )
			{
				ex.printStackTrace( );
				JOptionPane.showMessageDialog( mainView.getMainPanel( ) ,
						localizer.getFormattedString( "failedToSaveNewProjectFileDialog.message" , projectFile ) +
								ex.getClass( ).getSimpleName( ) + ": " + ex.getLocalizedMessage( ) ,
						localizer.getString( "failedToSaveNewProjectFileDialog.title" ) ,
						JOptionPane.ERROR_MESSAGE );
				return;
			}
			finally
			{
				if( writer != null )
				{
					try
					{
						writer.close( );
					}
					catch( Exception ex )
					{
						ex.printStackTrace( );
					}
				}
			}
		}
		
		mainView.openProject( projectFile );
	}
}
