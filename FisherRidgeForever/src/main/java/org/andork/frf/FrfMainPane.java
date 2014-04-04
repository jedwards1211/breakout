package org.andork.frf;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Image;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

@SuppressWarnings( "serial" )
public class FrfMainPane extends JPanel
{
	private ExecutorServiceBackgroundLoaded<MapsView>	mapsView;
	private BackgroundLoadedPane<MapsView>				mapsViewHolder;
	private ExecutorService								executor;
	
	public FrfMainPane( )
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
		
		Image splashImage = null;
		
		try
		{
			splashImage = ImageIO.read( getClass( ).getResource( "splash.jpg" ) );
		}
		catch( Exception ex )
		{
			ex.printStackTrace( );
		}
		
		final Image finalSplashImage = splashImage;
		
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
				return super.createLoadingContent( finalSplashImage , "Initializing 3D view..." , Color.WHITE );
			}
		};
		
		add( mapsViewHolder , BorderLayout.CENTER );
	}
}
