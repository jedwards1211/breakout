package org.andork.frf;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

@SuppressWarnings( "serial" )
public class MainPane extends JPanel
{
	private SurveyTable	surveyTable;
	private JScrollPane	surveyTableScrollPane;
	private MapsView	mapsView;
	
	private JTabbedPane	tabbedPane;
	
	public MainPane( )
	{
		init( );
	}
	
	private void init( )
	{
		setLayout( new BorderLayout( ) );
		
		surveyTable = new SurveyTable( );
		surveyTableScrollPane = new JScrollPane( surveyTable );
		
		mapsView = new MapsView( );
		
		tabbedPane = new JTabbedPane( );
		tabbedPane.addTab( "Data" , surveyTableScrollPane );
		tabbedPane.addTab( "Maps" , mapsView.getMainPanel( ) );
		
		add( tabbedPane , BorderLayout.CENTER );
		
		tabbedPane.addChangeListener( new ChangeListener( )
		{
			@Override
			public void stateChanged( ChangeEvent e )
			{
				if( tabbedPane.getSelectedIndex( ) == 1 )
				{
					mapsView.updateModel( surveyTable.createShots( ) );
				}
			}
		} );
	}
}
