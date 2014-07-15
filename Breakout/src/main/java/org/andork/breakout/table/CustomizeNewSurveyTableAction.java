package org.andork.breakout.table;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

@SuppressWarnings( "serial" )
public class CustomizeNewSurveyTableAction extends AbstractAction
{
	NewSurveyTable	table;
	
	public CustomizeNewSurveyTableAction( NewSurveyTable table )
	{
		super( );
		putValue( NAME , "Customize..." );
		this.table = table;
	}
	
	@Override
	public void actionPerformed( ActionEvent e )
	{
		NewSurveyTableCustomizer customizer = new NewSurveyTableCustomizer( );
		customizer.setColumnModels( table.getModel( ).getColumnModels( ) );
		int option = JOptionPane.showConfirmDialog( null , customizer , "Customize Table" , JOptionPane.OK_CANCEL_OPTION );
		if( option == JOptionPane.OK_OPTION )
		{
			table.getModel( ).setColumnModels( customizer.getColumnModels( ) );
		}
	}
}
