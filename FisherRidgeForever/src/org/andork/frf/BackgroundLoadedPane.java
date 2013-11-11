package org.andork.frf;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.andork.frf.BackgroundLoaded.State;
import org.andork.ui.GridBagWizard;

@SuppressWarnings( "serial" )
public abstract class BackgroundLoadedPane<C> extends JPanel
{
	ExecutorServiceBackgroundLoaded<C>	backgroundLoaded;
	
	Component							loadingContent;
	Component							loadFailedContent;
	Component							loadedContent;
	
	public BackgroundLoadedPane( ExecutorServiceBackgroundLoaded<C> backgroundLoaded )
	{
		super( );
		setLayout( new BorderLayout( ) );
		
		loadingContent = createLoadingContent( );
		loadFailedContent = createLoadFailedContent( );
		
		this.backgroundLoaded = backgroundLoaded;
		backgroundLoaded.addChangeListener( new ChangeHandler( ) );
		
		update( );
	}
	
	protected Component createLoadingContent( )
	{
		JPanel loadingPane = new JPanel( );
		JLabel loadingLabel = new JLabel( "Loading..." );
		JProgressBar loadingBar = new JProgressBar( );
		loadingBar.setIndeterminate( true );
		
		GridBagWizard g = GridBagWizard.create( loadingPane );
		g.put( loadingLabel , loadingBar ).intoColumn( );
		
		return loadingPane;
	}
	
	protected abstract Component getContentComponent( C content );
	
	protected Component createLoadFailedContent( )
	{
		JPanel errorPane = new JPanel( );
		JLabel errorLabel = new JLabel( "Failed to load." );
		errorLabel.setIcon( UIManager.getIcon( "OptionPane.errorIcon" ) );
		
		GridBagWizard g = GridBagWizard.create( errorPane );
		g.put( errorLabel ).xy( 0 , 0 );
		
		return errorPane;
	}
	
	private class ChangeHandler implements ChangeListener
	{
		@Override
		public void stateChanged( ChangeEvent e )
		{
			update( );
		}
	}
	
	private void update( )
	{
		BackgroundLoaded.State state = backgroundLoaded.getState( );
		
		removeAll( );
		if( state == State.LOAD_FAILED )
		{
			add( loadFailedContent , BorderLayout.CENTER );
		}
		else if( state == State.LOADED )
		{
			loadedContent = getContentComponent( backgroundLoaded.get( ) );
			add( loadedContent , BorderLayout.CENTER );
		}
		else
		{
			add( loadingContent , BorderLayout.CENTER );
		}
		revalidate( );
		repaint( );
	}
}
