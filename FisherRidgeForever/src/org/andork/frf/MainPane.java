package org.andork.frf;

import java.awt.BorderLayout;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class MainPane extends JPanel
{
	private SurveyTable			surveyTable;
	private JScrollPane			surveyTableScrollPane;
	private MapsView		survey3dView;
	
	private JTabbedPane			tabbedPane;
	
	public MainPane( )
	{
		init( );
	}
	
	private void init( )
	{
		setLayout( new BorderLayout( ) );
		
		surveyTable = new SurveyTable( );
		surveyTableScrollPane = new JScrollPane( surveyTable );
		
		survey3dView = new MapsView( );
		
		tabbedPane = new JTabbedPane( );
		tabbedPane.addTab( "Data" , surveyTableScrollPane );
		tabbedPane.addTab( "Maps" , survey3dView.getMainPanel( ) );
		
		add( tabbedPane , BorderLayout.CENTER );
		
		tabbedPane.addChangeListener( new ChangeListener( )
		{
			@Override
			public void stateChanged( ChangeEvent e )
			{
				if( tabbedPane.getSelectedComponent( ) == survey3dView.getMainPanel( ) )
				{
					survey3dView.updateModel( surveyTable.createShots( ) );
				}
			}
		} );
	}
}
