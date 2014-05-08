package org.andork.breakout;

import java.awt.Insets;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.basic.BasicButtonUI;

import org.andork.awt.GridBagWizard;

@SuppressWarnings( "serial" )
public class ViewButtonsPanel extends JPanel
{
	JToggleButton		planButton;
	JToggleButton		perspectiveButton;
	JToggleButton		northButton;
	JToggleButton		southButton;
	JToggleButton		eastButton;
	JToggleButton		westButton;
	
	JToggleButton[ ]	buttons;
	
	JLabel				hintLabel;
	
	public ViewButtonsPanel( )
	{
		setOpaque( false );
		
		planButton = new JToggleButton( );
		planButton.setIcon( new ImageIcon( getClass( ).getResource( "plan-normal.png" ) ) );
		planButton.setRolloverIcon( new ImageIcon( getClass( ).getResource( "plan-rollover.png" ) ) );
		planButton.setSelectedIcon( new ImageIcon( getClass( ).getResource( "plan-selected.png" ) ) );
		planButton.setName( "Plan" );
		
		perspectiveButton = new JToggleButton( );
		perspectiveButton.setIcon( new ImageIcon( getClass( ).getResource( "perspective-normal.png" ) ) );
		perspectiveButton.setRolloverIcon( new ImageIcon( getClass( ).getResource( "perspective-rollover.png" ) ) );
		perspectiveButton.setSelectedIcon( new ImageIcon( getClass( ).getResource( "perspective-selected.png" ) ) );
		perspectiveButton.setName( "Perspective" );
		
		northButton = new JToggleButton( );
		northButton.setIcon( new ImageIcon( getClass( ).getResource( "north-facing-profile-normal.png" ) ) );
		northButton.setRolloverIcon( new ImageIcon( getClass( ).getResource( "north-facing-profile-rollover.png" ) ) );
		northButton.setSelectedIcon( new ImageIcon( getClass( ).getResource( "north-facing-profile-selected.png" ) ) );
		northButton.setName( "North-Facing Profile" );
		
		southButton = new JToggleButton( );
		southButton.setIcon( new ImageIcon( getClass( ).getResource( "south-facing-profile-normal.png" ) ) );
		southButton.setRolloverIcon( new ImageIcon( getClass( ).getResource( "south-facing-profile-rollover.png" ) ) );
		southButton.setSelectedIcon( new ImageIcon( getClass( ).getResource( "south-facing-profile-selected.png" ) ) );
		southButton.setName( "South-Facing Profile" );
		
		eastButton = new JToggleButton( );
		eastButton.setIcon( new ImageIcon( getClass( ).getResource( "east-facing-profile-normal.png" ) ) );
		eastButton.setRolloverIcon( new ImageIcon( getClass( ).getResource( "east-facing-profile-rollover.png" ) ) );
		eastButton.setSelectedIcon( new ImageIcon( getClass( ).getResource( "east-facing-profile-selected.png" ) ) );
		eastButton.setName( "East-Facing Profile" );
		
		westButton = new JToggleButton( );
		westButton.setIcon( new ImageIcon( getClass( ).getResource( "west-facing-profile-normal.png" ) ) );
		westButton.setRolloverIcon( new ImageIcon( getClass( ).getResource( "west-facing-profile-rollover.png" ) ) );
		westButton.setSelectedIcon( new ImageIcon( getClass( ).getResource( "west-facing-profile-selected.png" ) ) );
		westButton.setName( "West-Facing Profile" );
		
		buttons = new JToggleButton[ ] { planButton , perspectiveButton , northButton , southButton , eastButton , westButton };
		
		ButtonGroup group = new ButtonGroup( );
		for( JToggleButton button : buttons )
		{
			button.setUI( new BasicButtonUI( ) );
			button.setBorderPainted( false );
			button.setContentAreaFilled( false );
			button.setOpaque( false );
			button.setMargin( new Insets( 0 , 0 , 0 , 0 ) );
			group.add( button );
		}
		
		hintLabel = new JLabel( );
		hintLabel.setHorizontalAlignment( JLabel.CENTER );
		
		GridBagWizard w = GridBagWizard.create( this );
		
		w.put( planButton ).xy( 1 , 1 );
		w.put( northButton ).above( planButton );
		w.put( southButton ).below( planButton );
		w.put( eastButton ).rightOf( planButton );
		w.put( westButton ).leftOf( planButton );
		w.put( perspectiveButton ).rightOf( northButton );
		w.put( hintLabel ).below( westButton , southButton , eastButton ).fillx( 1.0 );
		
		ChangeListener changeHandler = new ChangeListener( )
		{
			@Override
			public void stateChanged( ChangeEvent e )
			{
				updateHintLabel( );
			}
		};
		
		for( JToggleButton button : buttons )
		{
			button.getModel( ).addChangeListener( changeHandler );
		}
	}
	
	private void updateHintLabel( )
	{
		for( JToggleButton button : buttons )
		{
			if( button.getModel( ).isRollover( ) )
			{
				hintLabel.setText( button.getName( ) );
				return;
			}
		}
		
		for( JToggleButton button : buttons )
		{
			if( button.isSelected( ) )
			{
				hintLabel.setText( button.getName( ) );
				return;
			}
		}
		
		hintLabel.setText( null );
	}
	
	public JToggleButton getPlanButton( )
	{
		return planButton;
	}
	
	public JToggleButton getPerspectiveButton( )
	{
		return perspectiveButton;
	}
	
	public JToggleButton getNorthButton( )
	{
		return northButton;
	}
	
	public JToggleButton getSouthButton( )
	{
		return southButton;
	}
	
	public JToggleButton getEastButton( )
	{
		return eastButton;
	}
	
	public JToggleButton getWestButton( )
	{
		return westButton;
	}
}
