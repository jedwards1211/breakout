package org.andork.breakout.table;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

@SuppressWarnings( "serial" )
public class CustomizeNewSurveyTableAction extends AbstractAction
{
	NewSurveyTable								table;
	NewSurveyTableCustomizer					customizer	= new NewSurveyTableCustomizer( );
	NewSurveyTableCustomizer.DefaultController	controller;
	
	public CustomizeNewSurveyTableAction( NewSurveyTable table )
	{
		super( );
		putValue( NAME , "Customize..." );
		this.table = table;
		controller = new NewSurveyTableCustomizer.DefaultController( table , customizer );
		customizer.setController( controller );
	}
	
	@Override
	public void actionPerformed( ActionEvent e )
	{
		controller.showCustomizer( );
	}
}
