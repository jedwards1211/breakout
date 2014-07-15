package org.andork.breakout.table;

import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

public class NewSurveyTableMenuButton extends JButton
{
	NewSurveyTable	table;
	
	public NewSurveyTableMenuButton( NewSurveyTable table )
	{
		super( "V" );
		this.table = table;
		init( );
	}
	
	private void init( )
	{
		addActionListener( new ActionListener( )
		{
			@Override
			public void actionPerformed( ActionEvent e )
			{
				JPopupMenu menu = new JPopupMenu( );
				menu.add( new JMenuItem( new CustomizeNewSurveyTableAction( table ) ) );
				
				menu.show( NewSurveyTableMenuButton.this , 0 , getHeight( ) );
			}
		} );
	}
}
