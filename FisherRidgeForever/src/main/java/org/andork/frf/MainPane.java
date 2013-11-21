package org.andork.frf;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.andork.frf.update.UpdateProperties;
import org.andork.frf.update.UpdateStatus;
import org.andork.frf.update.UpdateStatusPanel;
import org.andork.frf.update.UpdateStatusPanelController;
import org.andork.ui.GridBagWizard;
import org.andork.ui.I18n;

@SuppressWarnings( "serial" )
public class MainPane extends JPanel
{
	private SurveyTable									surveyTable;
	private JScrollPane									surveyTableScrollPane;
	private ExecutorServiceBackgroundLoaded<MapsView>	mapsView;
	private BackgroundLoadedPane<MapsView>				mapsViewHolder;
	private ExecutorService								executor;
	
	private JTabbedPane									tabbedPane;
	
	private JPanel										statusBar;
	private UpdateStatusPanel							updateStatusPanel;
	private UpdateStatusPanelController					updateStatusPanelController;
	
	public MainPane( )
	{
		init( );
	}
	
	private void init( )
	{
		setLayout( new BorderLayout( ) );
		
		surveyTable = new SurveyTable( );
		surveyTableScrollPane = new JScrollPane( surveyTable );
		
		executor = Executors.newSingleThreadExecutor( );
		
		mapsView = new ExecutorServiceBackgroundLoaded<MapsView>( executor )
		{
			@Override
			protected MapsView load( ) throws Exception
			{
				return new MapsView( );
			}
		};
		
		mapsView.loadInBackgroundIfNecessary( );
		
		mapsViewHolder = new BackgroundLoadedPane<MapsView>( mapsView )
		{
			@Override
			protected Component getContentComponent( MapsView content )
			{
				return content.getMainPanel( );
			}
			
			@Override
			protected Component createLoadingContent( )
			{
				return super.createLoadingContent( "Initializing 3D view..." );
			}
		};
		
		tabbedPane = new JTabbedPane( );
		tabbedPane.addTab( "Data" , surveyTableScrollPane );
		tabbedPane.addTab( "Maps" , mapsViewHolder );
		
		add( tabbedPane , BorderLayout.CENTER );
		
		tabbedPane.addChangeListener( new ChangeListener( )
		{
			@Override
			public void stateChanged( ChangeEvent e )
			{
				if( tabbedPane.getSelectedIndex( ) == 1 )
				{
					mapsView.get( ).updateModel( surveyTable.createShots( ) );
				}
			}
		} );
		
		statusBar = new JPanel( );
		
		URL updateUrl = null;
		File updateDir = null;
		File lastUpdateFile = null;
		try
		{
			Properties props = UpdateProperties.getUpdateProperties( );
			updateUrl = new URL( props.getProperty( UpdateProperties.SOURCE ) );
			updateDir = new File( props.getProperty( UpdateProperties.UPDATE_DIR ) );
			lastUpdateFile = new File( props.getProperty( UpdateProperties.LAST_UPDATE_FILE ) );
		}
		catch( MalformedURLException e1 )
		{
			e1.printStackTrace( );
		}
		
		updateStatusPanel = new UpdateStatusPanel( new I18n( ) );
		updateStatusPanel.setBorder( new EmptyBorder( 3 , 3 , 3 , 3 ) );
		updateStatusPanel.setStatus( UpdateStatus.UNCHECKED );
		updateStatusPanelController = new UpdateStatusPanelController( updateStatusPanel ,
				lastUpdateFile , updateUrl , new File( updateDir , "update.zip" ) );
		updateStatusPanelController.downloadUpdateIfAvailable( );
		
		GridBagWizard g = GridBagWizard.create( statusBar );
		g.put( updateStatusPanel ).xy( 0 , 0 ).weightx( 1.0 ).east( );
		statusBar.setPreferredSize( new Dimension( 100 , 22 ) );
		add( statusBar , BorderLayout.SOUTH );
	}
}
