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
package org.andork.swing.table;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.Popup;
import javax.swing.PopupFactory;
import javax.swing.SwingUtilities;
import javax.swing.border.LineBorder;
import javax.swing.table.TableCellRenderer;

/**
 * A popup that appears when the user moves the mouse over a table cell whose contents are truncated. The popup uses the {@link TableCellRenderer} to display
 * the full contents of the cell.
 * 
 * @author james.a.edwards
 */
@SuppressWarnings( "serial" )
public class TruncatedCellPopup extends JPanel
{
	private TableMouseHandler	mouseHandler		= new TableMouseHandler( );
	
	private Popup				popup;
	private JTable				currentTable;
	private int					currentRow;
	private int					currentColumn;
	
	private boolean				verticalExpand		= false;
	private boolean				horizontalExpand	= true;
	
	public boolean isVerticalExpand( )
	{
		return verticalExpand;
	}
	
	public void setVerticalExpand( boolean verticalExpand )
	{
		this.verticalExpand = verticalExpand;
	}
	
	public boolean isHorizontalExpand( )
	{
		return horizontalExpand;
	}
	
	public void setHorizontalExpand( boolean horizontalExpand )
	{
		this.horizontalExpand = horizontalExpand;
	}
	
	public void installOn( JTable table )
	{
		table.addMouseMotionListener( mouseHandler );
	}
	
	public void uninstallFrom( JTable table )
	{
		table.removeMouseMotionListener( mouseHandler );
	}
	
	private void showPopupIfNecessary( MouseEvent e )
	{
		JTable table = ( JTable ) e.getComponent( );
		int row = table.rowAtPoint( e.getPoint( ) );
		int column = table.columnAtPoint( e.getPoint( ) );
		
		if( table == currentTable && row == currentRow && column == currentColumn )
		{
			return;
		}
		
		hidePopup( );
		
		if( row < 0 || column < 0 )
		{
			return;
		}
		
		Rectangle cellRect = table.getCellRect( row , column , false );
		
		if( !cellRect.contains( e.getPoint( ) ) )
		{
			return;
		}
		
		currentTable = table;
		currentRow = row;
		currentColumn = column;
		
		TableCellRenderer cellRenderer = table.getCellRenderer( row , column );
		Component rendComp = table.prepareRenderer( cellRenderer , row , column );
		Dimension prefSize = rendComp.getPreferredSize( );
		
		Rectangle wholeRect = new Rectangle( cellRect );
		Rectangle visibleCellRect = cellRect.intersection( table.getVisibleRect( ) );
		if( horizontalExpand )
		{
			wholeRect.width = Math.max( cellRect.width , prefSize.width );
		}
		if( verticalExpand )
		{
			wholeRect.height = Math.max( cellRect.height , prefSize.height );
		}
		if( !wholeRect.equals( visibleCellRect ) )
		{
			showPopup( table , row , column , cellRect , wholeRect );
		}
	}
	
	private void hidePopup( )
	{
		if( popup != null )
		{
			popup.hide( );
			popup = null;
		}
		currentTable = null;
		currentRow = -1;
		currentColumn = -1;
	}
	
	private void showPopup( final JTable table , int row , int column , Rectangle cellRect , Rectangle wholeRect )
	{
		final RendererPanel rendererPanel = new RendererPanel( table , row , column );
		rendererPanel.setBorder( new LineBorder( Color.GRAY ) );
		
		wholeRect = pad( convertToScreen( wholeRect , table ) , rendererPanel.getInsets( ) );
		rendererPanel.setPreferredSize( wholeRect.getSize( ) );
		
		final Rectangle keepShowingRect = new Rectangle( cellRect );
		keepShowingRect.x = rendererPanel.getInsets( ).left;
		keepShowingRect.y = rendererPanel.getInsets( ).top;
		
		popup = PopupFactory.getSharedInstance( ).getPopup( table , rendererPanel , wholeRect.x , wholeRect.y );
		
		class PopupMouseHandler extends MouseAdapter implements MouseMotionListener , MouseWheelListener
		{
			@Override
			public void mousePressed( MouseEvent e )
			{
				table.dispatchEvent( retargetEvent( e , table ) );
				rendererPanel.repaint( );
			}
			
			@Override
			public void mouseReleased( MouseEvent e )
			{
				table.dispatchEvent( retargetEvent( e , table ) );
				rendererPanel.repaint( );
			}
			
			@Override
			public void mouseExited( MouseEvent e )
			{
				hidePopup( );
			}
			
			public void mouseDragged( MouseEvent e )
			{
				mouseMoved( e );
				rendererPanel.repaint( );
			}
			
			public void mouseMoved( MouseEvent e )
			{
				if( !keepShowingRect.contains( e.getPoint( ) ) )
				{
					hidePopup( );
				}
			}
			
			public void mouseWheelMoved( MouseWheelEvent e )
			{
				hidePopup( );
				table.dispatchEvent( retargetEvent( e , table ) );
				// without this a blank spot where the popup used to be might
				// remain
				table.repaint( );
			}
		};
		
		PopupMouseHandler popupMouseHandler = new PopupMouseHandler( );
		rendererPanel.addMouseListener( popupMouseHandler );
		rendererPanel.addMouseMotionListener( popupMouseHandler );
		rendererPanel.addMouseWheelListener( popupMouseHandler );
		popup.show( );
		// without this the popup might be blank
		rendererPanel.repaint( );
	}
	
	private Rectangle pad( Rectangle r , Insets i )
	{
		return new Rectangle( r.x - i.left , r.y - i.top , r.width + i.left + i.right , r.height + i.top + i.bottom );
	}
	
	private Rectangle convertToScreen( Rectangle r , Component c )
	{
		Point p1 = new Point( r.x , r.y );
		SwingUtilities.convertPointToScreen( p1 , c );
		return new Rectangle( p1.x , p1.y , r.width , r.height );
	}
	
	private MouseEvent retargetEvent( MouseEvent e , Component target )
	{
		Point newPoint = SwingUtilities.convertPoint( e.getComponent( ) , e.getPoint( ) , target );
		return new MouseEvent( target , e.getID( ) , e.getWhen( ) , e.getModifiers( ) , newPoint.x , newPoint.y , e.getClickCount( ) , e.isPopupTrigger( ) , e.getButton( ) );
	}
	
	private MouseEvent retargetEvent( MouseWheelEvent e , Component target )
	{
		Point newPoint = SwingUtilities.convertPoint( e.getComponent( ) , e.getPoint( ) , target );
		return new MouseWheelEvent( target , e.getID( ) , e.getWhen( ) , e.getModifiers( ) , newPoint.x , newPoint.y , e.getClickCount( ) , e.isPopupTrigger( ) , e.getScrollType( ) , e.getScrollAmount( ) , e.getWheelRotation( ) );
	}
	
	private class RendererPanel extends JPanel
	{
		private JTable	table;
		private int		row;
		private int		column;
		
		RendererPanel( JTable table , int row , int column )
		{
			super( );
			this.table = table;
			this.row = row;
			this.column = column;
		}
		
		@Override
		protected void paintComponent( Graphics g )
		{
			super.paintComponent( g );
			
			Graphics2D g2 = ( Graphics2D ) g;
			
			Insets insets = getInsets( );
			g2.translate( insets.left , insets.top );
			TableCellRenderer renderer = table.getCellRenderer( row , column );
			Component rendComp = table.prepareRenderer( renderer , row , column );
			Color bg = null;
			if( rendComp.isOpaque( ) )
			{
				bg = rendComp.getBackground( );
			}
			else
			{
				bg = table.getBackground( );
			}
			if( bg != null )
			{
				g2.setBackground( bg );
				g2.clearRect( 0 , 0 , getWidth( ) , getHeight( ) );
			}
			rendComp.setSize( getWidth( ) - insets.left - insets.right , getHeight( ) - insets.top - insets.bottom );
			rendComp.paint( g );
			g2.translate( -insets.left , -insets.top );
		}
	}
	
	private class TableMouseHandler extends MouseAdapter implements MouseMotionListener
	{
		public void mouseMoved( MouseEvent e )
		{
			showPopupIfNecessary( e );
		}
		
		public void mouseDragged( MouseEvent e )
		{
			showPopupIfNecessary( e );
		}
	}
}
