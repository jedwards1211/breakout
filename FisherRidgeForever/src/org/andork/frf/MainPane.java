package org.andork.frf;

import java.awt.BorderLayout;
import java.awt.Component;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

@SuppressWarnings( "serial" )
public class MainPane extends JPanel
{
	private SurveyTable									surveyTable;
	private JScrollPane									surveyTableScrollPane;
	private ExecutorServiceBackgroundLoaded<MapsView>	mapsView;
	private BackgroundLoadedPane<MapsView>				mapsViewHolder;
	private ExecutorService								executor;
	
	private JTabbedPane									tabbedPane;
	
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
	}
}
