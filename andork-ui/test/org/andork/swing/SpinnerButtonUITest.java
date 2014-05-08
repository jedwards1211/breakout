package org.andork.swing;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;

public class SpinnerButtonUITest
{
	public static void main( String[ ] args )
	{
		final JButton button = new JButton( );
		button.setUI( new SpinnerButtonUI( ) );
		button.addActionListener( new ActionListener( )
		{
			@Override
			public void actionPerformed( ActionEvent e )
			{
				SpinnerButtonUI ui = ( SpinnerButtonUI ) button.getUI( );
				ui.setSpinning( !ui.isSpinning( ) );
			}
		} );
		button.setBackground( Color.BLACK );
		JFrame frame = QuickTestFrame.frame( button );
		frame.setVisible( true );
	}
}
