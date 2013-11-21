package org.andork.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Window;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.border.EmptyBorder;

import org.andork.ui.GridBagWizard.DefaultAutoInsets;

@SuppressWarnings( "serial" )
public class GenericProgressDialog extends JDialog
{
	JLabel			statusLabel;
	JProgressBar	progressBar;
	
	public GenericProgressDialog( Window owner )
	{
		super( owner );
		init( );
	}
	
	public JLabel getStatusLabel( )
	{
		return statusLabel;
	}
	
	public void setStatusLabel( JLabel statusLabel )
	{
		this.statusLabel = statusLabel;
	}
	
	public JProgressBar getProgressBar( )
	{
		return progressBar;
	}
	
	public void setProgressBar( JProgressBar progressBar )
	{
		this.progressBar = progressBar;
	}
	
	private void init( )
	{
		setResizable( false );

		JPanel content = new JPanel( );
		content.setBorder( new EmptyBorder( 15 , 15 , 15 , 15 ) );
		
		statusLabel = new JLabel( );
		progressBar = new JProgressBar( );
		progressBar.setPreferredSize( new Dimension( 500 , 30 ) );
		
		GridBagWizard g = GridBagWizard.create( content );
		g.defaults( ).autoinsets( new DefaultAutoInsets( 10 , 10 ) );
		g.put( statusLabel , progressBar ).fillx( 1.0 ).intoColumn( );
		
		getContentPane( ).add( content , BorderLayout.CENTER );
		
		pack( );
		setLocationRelativeTo( getOwner( ) );
	}
}
