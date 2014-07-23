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
package org.andork.swing.jump;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager;
import java.awt.Rectangle;

import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.border.MatteBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * A {@link JScrollPane} with a built-in {@link JumpBar} parallel to the
 * vertical scroll bar.
 * 
 * @author andy.edwards
 */
@SuppressWarnings( "serial" )
public class JScrollAndJumpPane extends JPanel implements LayoutManager
{
	JScrollPane	scrollPane;
	JumpBar		jumpBar;
	Component	topRightCornerComp;
	
	public JScrollAndJumpPane( )
	{
		this( null );
	}
	
	public JScrollAndJumpPane( Component view )
	{
		if( view != null )
		{
			scrollPane = new JScrollPane( view );
		}
		else
		{
			scrollPane = new JScrollPane( );
		}
		scrollPane.setVerticalScrollBarPolicy( JScrollPane.VERTICAL_SCROLLBAR_ALWAYS );
		jumpBar = createJumpBar( scrollPane.getVerticalScrollBar( ) );
		scrollPane.getViewport( ).addChangeListener( new ChangeListener( )
		{
			@Override
			public void stateChanged( ChangeEvent e )
			{
				revalidate( );
			}
		} );
		add( scrollPane );
		add( jumpBar );
		setBorder( scrollPane.getBorder( ) );
		scrollPane.setBorder( new MatteBorder( 0 , 0 , 0 , 1 , new Color( 224 , 224 , 224 ) ) );
		setLayout( this );
	}
	
	public JumpBar getJumpBar( )
	{
		return jumpBar;
	}
	
	public JScrollPane getScrollPane( )
	{
		return scrollPane;
	}
	
	public Component getTopRightCornerComp( )
	{
		return topRightCornerComp;
	}
	
	public void setTopRightCornerComp( Component topRightCornerComp )
	{
		if( this.topRightCornerComp != topRightCornerComp )
		{
			if( this.topRightCornerComp != null )
			{
				remove( this.topRightCornerComp );
			}
			this.topRightCornerComp = topRightCornerComp;
			if( this.topRightCornerComp != null )
			{
				add( this.topRightCornerComp );
			}
		}
	}
	
	protected JumpBar createJumpBar( JScrollBar scrollBar )
	{
		return new JumpBar( scrollBar );
	}
	
	@Override
	public void addLayoutComponent( String name , Component comp )
	{
		
	}
	
	@Override
	public void removeLayoutComponent( Component comp )
	{
		
	}
	
	@Override
	public Dimension preferredLayoutSize( Container parent )
	{
		Dimension size = scrollPane.getPreferredSize( );
		size.width += jumpBar.getPreferredSize( ).width;
		return size;
	}
	
	@Override
	public Dimension minimumLayoutSize( Container parent )
	{
		Dimension size = scrollPane.getMinimumSize( );
		size.width += jumpBar.getMinimumSize( ).width;
		return size;
	}
	
	@Override
	public void layoutContainer( Container parent )
	{
		Rectangle b = SwingUtilities.calculateInnerArea( this , null );
		scrollPane.setBounds( b.x , b.y , b.width , b.height );
		boolean scroll = scrollPane.getVerticalScrollBar( ).isVisible( );
		int highlightBarWidth = scroll ? jumpBar.getPreferredSize( ).width : 0;
		scrollPane.setBounds( b.x , b.y , b.width - highlightBarWidth - 1 , b.height );
		if( scroll )
		{
			jumpBar.setBounds( b.width - highlightBarWidth , b.y + scrollPane.getVerticalScrollBar( ).getY( ) ,
					highlightBarWidth , scrollPane.getVerticalScrollBar( ).getHeight( ) );
			if( topRightCornerComp != null )
			{
				topRightCornerComp.setBounds( jumpBar.getX( ) , b.y , jumpBar.getWidth( ) , jumpBar.getY( ) - b.y );
			}
		}
		jumpBar.setVisible( scroll );
		if( topRightCornerComp != null )
		{
			topRightCornerComp.setVisible( scroll );
		}
	}
}
