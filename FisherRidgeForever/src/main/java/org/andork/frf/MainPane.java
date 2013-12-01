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

import org.andork.awt.GridBagWizard;
import org.andork.awt.I18n;
import org.andork.frf.update.UpdateProperties;
import org.andork.frf.update.UpdateStatus;
import org.andork.frf.update.UpdateStatusPanel;
import org.andork.frf.update.UpdateStatusPanelController;

@SuppressWarnings( "serial" )
public class MainPane extends JPanel
{
	private ExecutorServiceBackgroundLoaded<MapsView>	mapsView;
	private BackgroundLoadedPane<MapsView>				mapsViewHolder;
	private ExecutorService								executor;
	
	public MainPane( )
	{
		init( );
	}
	
	private void init( )
	{
		setLayout( new BorderLayout( ) );
		
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
		
		add( mapsViewHolder , BorderLayout.CENTER );
	}
}
