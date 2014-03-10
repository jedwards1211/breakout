package org.andork.frf;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.html.HTMLEditorKit;

import org.andork.awt.GridBagWizard;
import org.andork.frf.BackgroundLoaded.State;
import org.andork.util.StringUtils;

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
		
		this.backgroundLoaded = backgroundLoaded;
		backgroundLoaded.addChangeListener( new ChangeHandler( ) );
		
		update( );
	}
	
	protected Component createLoadingContent( )
	{
		return createLoadingContent( "Loading..." );
	}
	
	protected Component createLoadingContent( String message )
	{
		JPanel loadingPane = new JPanel( );
		JLabel loadingLabel = new JLabel( message );
		JProgressBar loadingBar = new JProgressBar( );
		loadingBar.setIndeterminate( true );
		
		GridBagWizard g = GridBagWizard.create( loadingPane );
		g.put( loadingLabel , loadingBar ).intoColumn( ).insets( 10 , 10 , 10 , 10 );
		
		return loadingPane;
	}
	
	protected abstract Component getContentComponent( C content );
	
	protected Component createLoadFailedContent( Throwable t )
	{
		JPanel errorPane = new JPanel( );
		JLabel iconLabel = new JLabel( UIManager.getIcon( "OptionPane.errorIcon" ) );
		JLabel headerLabel = new JLabel( "<html><h1>Failed to load</h1><html>" );
		JEditorPane errorEditor = new JEditorPane( );
		errorEditor.setBackground( null );
		errorEditor.setEditable( false );
		errorEditor.setEditorKit( new HTMLEditorKit( ) );
		errorEditor.setText( "<html>" + StringUtils.formatThrowableForHTML( t ) + "</html>" );
		
		GridBagWizard g = GridBagWizard.create( errorPane );
		g.put( iconLabel ).xy( 0 , 0 ).east( ).insets( 5 , 5 , 5 , 5 );
		g.put( headerLabel ).rightOf( iconLabel ).west( );
		g.put( errorEditor ).below( headerLabel ).northwest( );
		
		return errorPane;
	}
	
	private class ChangeHandler implements ChangeListener
	{
		@Override
		public void stateChanged( ChangeEvent e )
		{
			SwingUtilities.invokeLater( new Runnable( )
			{
				@Override
				public void run( )
				{
					update( );
				}
			} );
		}
	}
	
	private void update( )
	{
		BackgroundLoaded.State state = backgroundLoaded.getState( );
		
		removeAll( );
		if( state == State.LOAD_FAILED )
		{
			if( loadFailedContent == null )
			{
				loadFailedContent = createLoadFailedContent( backgroundLoaded.getLoadingError( ) );
			}
			add( loadFailedContent , BorderLayout.CENTER );
		}
		else if( state == State.LOADED )
		{
			if( loadedContent == null )
			{
				loadedContent = getContentComponent( backgroundLoaded.get( ) );
			}
			add( loadedContent , BorderLayout.CENTER );
		}
		else
		{
			if( loadingContent == null )
			{
				loadingContent = createLoadingContent( );
			}
			add( loadingContent , BorderLayout.CENTER );
		}
		revalidate( );
		repaint( );
	}
}
