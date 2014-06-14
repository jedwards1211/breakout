package org.andork.awt.layout;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager;
import java.awt.Rectangle;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.plaf.basic.BasicHTML;
import javax.swing.text.View;

@SuppressWarnings( "serial" )
public class MultilineLabelHolder extends JPanel
{
	JLabel	label;
	int		width;
	
	public MultilineLabelHolder( JLabel label )
	{
		super( );
		this.label = label;
		add( label );
		setLayout( new Layout( ) );
	}
	
	public MultilineLabelHolder( String text )
	{
		this( new JLabel( wrapText( text ) ) );
	}
	
	private static String wrapText( String text )
	{
		if( !text.trim( ).toLowerCase( ).startsWith( "<html>" ) )
		{
			text = "<html>" + text + "</html>";
		}
		return text;
	}
	
	public int getWidth( )
	{
		return width;
	}
	
	public MultilineLabelHolder setWidth( int width )
	{
		this.width = width;
		return this;
	}
	
	private class Layout implements LayoutManager
	{
		
		@Override
		public void addLayoutComponent( String name , Component comp )
		{
		}
		
		@Override
		public void removeLayoutComponent( Component comp )
		{
		}
		
		private Dimension layoutSize( Container parent , LayoutSize sizeType )
		{
			Dimension size = sizeType.get( label );
			View view = ( View ) label.getClientProperty( BasicHTML.propertyKey );
			if( view != null )
			{
				view.setSize( width , 0 );
				size.height = ( int ) Math.ceil( view.getPreferredSpan( View.Y_AXIS ) );
			}
			return size;
		}
		
		@Override
		public Dimension preferredLayoutSize( Container parent )
		{
			return layoutSize( parent , LayoutSize.PREFERRED );
		}
		
		@Override
		public Dimension minimumLayoutSize( Container parent )
		{
			return layoutSize( parent , LayoutSize.MINIMUM );
		}
		
		@Override
		public void layoutContainer( Container parent )
		{
			label.setBounds( RectangleUtils.insetCopy( new Rectangle( parent.getSize( ) ) , parent.getInsets( ) ) );
		}
	}
}
