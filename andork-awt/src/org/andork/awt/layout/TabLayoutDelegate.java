package org.andork.awt.layout;

import java.awt.Component;
import java.awt.Container;
import java.awt.Insets;
import java.awt.Rectangle;

import org.andork.awt.layout.DelegatingLayoutManager.LayoutDelegate;

public class TabLayoutDelegate implements LayoutDelegate
{
	public TabLayoutDelegate( Component content , Corner corner , Side side )
	{
		super( );
		this.content = content;
		this.corner = corner;
		this.side = side;
	}
	
	Component	content;
	Corner		corner;
	Side		side;
	Insets		insets;
	
	public void setInsets( Insets insets )
	{
		this.insets = insets;
	}
	
	@Override
	public Rectangle desiredBounds( Container parent , Component target , LayoutSize layoutSize )
	{
		Rectangle bounds = new Rectangle( layoutSize.get( target ) );
		
		if( corner != null )
		{
			if( side != null )
			{
				side.opposite( ).setLocation( bounds , side.location( content ) );
				Side otherSide = side == corner.xSide( ) ? corner.ySide( ) : corner.xSide( );
				otherSide.setLocation( bounds , otherSide.location( content ) );
			}
			else
			{
				corner.xSide( ).opposite( ).setLocation( bounds , corner.xSide( ).location( content ) );
				corner.ySide( ).opposite( ).setLocation( bounds , corner.ySide( ).location( content ) );
			}
		}
		else
		{
			side.opposite( ).setLocation( bounds , side.location( content ) );
			Axis invAxis = side.axis( ).opposite( );
			invAxis.setLower( bounds , invAxis.center( content ) - invAxis.size( target ) / 2 );
		}
		
		if( insets != null )
		{
			bounds.x += insets.left;
			bounds.width = bounds.width - insets.left - insets.right;
			bounds.y += insets.top;
			bounds.height = bounds.height - insets.top - insets.bottom;
		}
		
		return bounds;
	}
	
	@Override
	public void layoutComponent( Container parent , Component target )
	{
		target.setBounds( desiredBounds( parent , target , LayoutSize.PREFERRED ) );
	}
}