/*******************************************************************************
 * Breakout Cave Survey Visualizer
 *
 * Copyright (C) 2014 James Edwards
 *
 * jedwards8 at fastmail dot fm
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *******************************************************************************/
package org.andork.breakout;

import java.awt.Insets;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.plaf.basic.BasicOptionPaneUI.ButtonActionListener;

import org.andork.awt.GridBagWizard;

@SuppressWarnings( "serial" )
public class ViewButtonsPanel extends JPanel
{
	JToggleButton					planButton;
	JToggleButton					perspectiveButton;
	JToggleButton					northButton;
	JToggleButton					southButton;
	JToggleButton					eastButton;
	JToggleButton					westButton;
	JToggleButton					autoProfileButton;
	
	Map<CameraView, JToggleButton>	buttonMap;
	
	JLabel							hintLabel;
	
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
		
		autoProfileButton = new JToggleButton( );
		autoProfileButton.setIcon( new ImageIcon( getClass( ).getResource( "wand-normal.png" ) ) );
		autoProfileButton.setRolloverIcon( new ImageIcon( getClass( ).getResource( "wand-rollover.png" ) ) );
		autoProfileButton.setSelectedIcon( new ImageIcon( getClass( ).getResource( "wand-selected.png" ) ) );
		autoProfileButton.setName( "Auto Profile" );
		
		buttonMap = new HashMap<>( );
		buttonMap.put( CameraView.PERSPECTIVE , perspectiveButton );
		buttonMap.put( CameraView.PLAN , planButton );
		buttonMap.put( CameraView.NORTH_FACING_PROFILE , northButton );
		buttonMap.put( CameraView.SOUTH_FACING_PROFILE , southButton );
		buttonMap.put( CameraView.EAST_FACING_PROFILE , eastButton );
		buttonMap.put( CameraView.WEST_FACING_PROFILE , westButton );
		buttonMap.put( CameraView.AUTO_PROFILE , autoProfileButton );
		
		ButtonGroup group = new ButtonGroup( );
		for( JToggleButton button : buttonMap.values( ) )
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
		w.put( autoProfileButton ).rightOf( southButton );
		w.put( hintLabel ).below( westButton , southButton , eastButton ).fillx( 1.0 );
		
		ChangeListener changeHandler = new ChangeListener( )
		{
			@Override
			public void stateChanged( ChangeEvent e )
			{
				updateHintLabel( );
			}
		};
		
		for( JToggleButton button : buttonMap.values( ) )
		{
			button.getModel( ).addChangeListener( changeHandler );
		}
	}
	
	private void updateHintLabel( )
	{
		for( JToggleButton button : buttonMap.values( ) )
		{
			if( button.getModel( ).isRollover( ) )
			{
				hintLabel.setText( button.getName( ) );
				return;
			}
		}
		
		for( JToggleButton button : buttonMap.values( ) )
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
	
	public JToggleButton getAutoProfileButton( )
	{
		return autoProfileButton;
	}
	
	public JToggleButton getButton( CameraView view )
	{
		return buttonMap.get( view );
	}
}
