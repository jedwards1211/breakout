package org.andork.survey;

import java.awt.BorderLayout;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class SurveyEditorPane extends JPanel
{
	private SurveyTable			surveyTable;
	private JScrollPane			surveyTableScrollPane;
	private Survey3DView		survey3dView;
	
	private PlanSurveyPlotPane	planSurveyPlotPane;
	
	private JTabbedPane			tabbedPane;
	
	public static void main( String[ ] args )
	{
		SurveyEditorPane editorPane = new SurveyEditorPane( );
		
		JFrame frame = new JFrame( );
		frame.getContentPane( ).add( editorPane , BorderLayout.CENTER );
		frame.setSize( 640 , 480 );
		frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		frame.setLocationRelativeTo( null );
		frame.setVisible( true );
	}
	
	public SurveyEditorPane( )
	{
		init( );
	}
	
	private void init( )
	{
		setLayout( new BorderLayout( ) );
		
		surveyTable = new SurveyTable( );
		surveyTableScrollPane = new JScrollPane( surveyTable );
		
		planSurveyPlotPane = new PlanSurveyPlotPane( );
		
		survey3dView = new Survey3DView( );
		
		tabbedPane = new JTabbedPane( );
		tabbedPane.addTab( "Survey" , surveyTableScrollPane );
		tabbedPane.addTab( "Plan" , planSurveyPlotPane );
		tabbedPane.addTab( "3D View" , survey3dView.getMainPanel( ) );
		
		add( tabbedPane , BorderLayout.CENTER );
		
		tabbedPane.addChangeListener( new ChangeListener( )
		{
			@Override
			public void stateChanged( ChangeEvent e )
			{
				if( tabbedPane.getSelectedComponent( ) == planSurveyPlotPane )
				{
					planSurveyPlotPane.planSurveyLayer.shots = surveyTable.createShots( );
				}
				else if( tabbedPane.getSelectedComponent( ) == survey3dView.getMainPanel( ) )
				{
					survey3dView.updateModel( surveyTable.createShots( ) );
				}
			}
		} );
	}
}
