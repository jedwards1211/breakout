package org.andork.awt;

import java.awt.Color;

public class ColorUtils
{
	public static Color alphaColor( Color c , int alpha )
	{
		return new Color( c.getRed( ) , c.getGreen( ) , c.getBlue( ) , alpha );
	}
	
	public static Color lighterColor( Color c , double amount )
	{
		return darkerColor( c , -amount );
	}
	
	public static Color darkerColor( Color c , double amount )
	{
		float[ ] hsbvals = new float[ 3 ];
		Color.RGBtoHSB( c.getRed( ) , c.getGreen( ) , c.getBlue( ) , hsbvals );
		hsbvals[ 2 ] = Math.min( 1f , Math.max( 0f , ( float ) ( hsbvals[ 2 ] - amount ) ) );
		int rgb = Color.HSBtoRGB( hsbvals[ 0 ] , hsbvals[ 1 ] , hsbvals[ 2 ] );
		rgb |= c.getAlpha( ) << 24;
		return new Color( rgb , true );
	}
}
