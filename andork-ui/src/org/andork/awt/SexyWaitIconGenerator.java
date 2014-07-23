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
package org.andork.awt;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import org.andork.util.Reparam;

public class SexyWaitIconGenerator
{
	public static void main( String[ ] args ) throws Exception
	{
		int totalFrames = 30;
		int scale = 1;
		for( int frame = 0 ; frame < totalFrames ; frame++ )
		{
			ImageIcon icon = generateIcon2( 28 * scale , 8 , 5f * scale , 10f * scale , 2f * scale , Color.RED , frame , totalFrames );
			
			BufferedImage image = ( BufferedImage ) icon.getImage( );
			
			ImageIO.write( image , "png" , new File( "spinner-" + frame + ".png" ) );
		}
		ImageIcon icon = generateIcon2Neutral( 28 * scale , 8 , 5f * scale , 10f * scale , 2f * scale , Color.RED , 0 , totalFrames );
		ImageIO.write( ( BufferedImage ) icon.getImage( ) , "png" , new File( "spinner-off.png" ) );
	}
	
	public static ImageIcon generateIcon( int size , int lobeCount , float innerRadius , float outerRadius , float blobRadius ,
			Color color , int frame , int totalFrames )
	{
		float blobRadius2 = blobRadius * blobRadius;
		
		float lobeRadius = ( outerRadius - innerRadius ) / 2f;
		float lobeRadius2 = lobeRadius * lobeRadius;
		float centerRadius = ( innerRadius + outerRadius ) / 2f;
		
		float blobAngle = ( float ) Math.PI * 2f * frame / totalFrames;
		float blobx = size / 2f + centerRadius * ( float ) Math.sin( blobAngle );
		float bloby = size / 2f + centerRadius * ( float ) Math.cos( blobAngle );
		
		BufferedImage image = new BufferedImage( size , size , BufferedImage.TYPE_INT_ARGB );
		
		float[ ] lobex = new float[ lobeCount ];
		float[ ] lobey = new float[ lobeCount ];
		
		for( int lobe = 0 ; lobe < lobeCount ; lobe++ )
		{
			float lobeAngle = ( float ) Math.PI * 2f * lobe / lobeCount;
			
			lobex[ lobe ] = size / 2f + centerRadius * ( float ) Math.sin( lobeAngle );
			lobey[ lobe ] = size / 2f + centerRadius * ( float ) Math.cos( lobeAngle );
		}
		
		for( int x = 0 ; x < size ; x++ )
		{
			for( int y = 0 ; y < size ; y++ )
			{
				float value = 0f;
				
				float dx = x - blobx;
				float dy = y - bloby;
				float dxy2 = dx * dx + dy * dy;
				// if( dxy2 < blobRadius2 )
				// {
				value += blobRadius2 / dxy2;
				// }
				
				for( int lobe = 0 ; lobe < lobeCount ; lobe++ )
				{
					dx = x - lobex[ lobe ];
					dy = y - lobey[ lobe ];
					
					dxy2 = dx * dx + dy * dy;
					// if( dxy2 < lobeRadius2 )
					// {
					value += lobeRadius2 / dxy2;
					// }
				}
				
				value = Math.max( 0f , Math.min( 1f , Reparam.linear( value , 4f , 5f , 0f , 1f ) ) );
				// value = Math.max( 0f , Math.min( 1f , value ) );
				
				int rgb = ( ( int ) ( 255 * value ) ) << 24 | ( color.getRGB( ) & 0xffffff );
				
				image.setRGB( x , y , rgb );
			}
		}
		
		return new ImageIcon( image );
	}
	
	public static ImageIcon generateIcon2( int size , int lobeCount , float innerRadius , float outerRadius , float expansion ,
			Color color , int frame , int totalFrames )
	{
		float lobeRadius = ( outerRadius - innerRadius ) / 2f;
		float lobeRadius2 = lobeRadius * lobeRadius;
		float centerRadius = ( innerRadius + outerRadius ) / 2f;
		
		float spinAngle = ( float ) Math.PI * 2f * frame / totalFrames;
		
		BufferedImage image = new BufferedImage( size , size , BufferedImage.TYPE_INT_ARGB );
		
		float[ ] lobex = new float[ lobeCount ];
		float[ ] lobey = new float[ lobeCount ];
		
		for( int lobe = 0 ; lobe < lobeCount ; lobe++ )
		{
			float lobeAngle = ( float ) Math.PI * 2f * lobe / lobeCount;
			
			lobex[ lobe ] = size / 2f + centerRadius * ( float ) Math.sin( lobeAngle );
			lobey[ lobe ] = size / 2f + centerRadius * ( float ) Math.cos( lobeAngle );
		}
		
		for( int x = 0 ; x < size ; x++ )
		{
			for( int y = 0 ; y < size ; y++ )
			{
				float value = 0f;
				
				for( int lobe = 0 ; lobe < lobeCount ; lobe++ )
				{
					float lobeAngle = ( float ) Math.PI * 2f * lobe / lobeCount;
					
					float dangle = ( float ) Math.min( Math.abs( spinAngle - lobeAngle ) ,
							Math.PI * 2f - Math.abs( spinAngle - lobeAngle ) );
					
					float fexp = expansion * dangle / ( float ) Math.PI;
					
					float rad = lobeRadius + fexp;
					float rad2 = rad * rad;
					
					float nlobex = lobex[ lobe ] + fexp * ( float ) Math.sin( lobeAngle );
					float nlobey = lobey[ lobe ] + fexp * ( float ) Math.cos( lobeAngle );
					
					float dx = x - nlobex;
					float dy = y - nlobey;
					
					float dxy2 = dx * dx + dy * dy;
					if( dxy2 < rad2 )
					{
						value += 1 - dxy2 / rad2;
					}
				}
				
				float angle = ( float ) Math.atan2( x - size / 2 , y - size / 2 );
				if( angle < 0 )
				{
					angle += Math.PI * 2;
				}
				float dangle = ( float ) Math.min( Math.abs( angle - spinAngle ) ,
						Math.PI * 2f - Math.abs( angle - spinAngle ) );
				
				int colorScale = ( int ) Reparam.linear( dangle , 0 , ( float ) Math.PI , 127 , 255 );
				
				int rgb = ( ( int ) ( 255 * value ) ) << 24 |
						( ( color.getRed( ) * colorScale / 255 ) << 16 ) |
						( ( color.getGreen( ) * colorScale / 255 ) << 8 ) |
						( ( color.getBlue( ) * colorScale / 255 ) );
				
				image.setRGB( x , y , rgb );
			}
		}
		
		return new ImageIcon( image );
	}
	
	public static ImageIcon generateIcon2Neutral( int size , int lobeCount , float innerRadius , float outerRadius , float expansion ,
			Color color , int frame , int totalFrames )
	{
		float lobeRadius = ( outerRadius - innerRadius ) / 2f;
		float lobeRadius2 = lobeRadius * lobeRadius;
		float centerRadius = ( innerRadius + outerRadius ) / 2f;
		
		BufferedImage image = new BufferedImage( size , size , BufferedImage.TYPE_INT_ARGB );
		
		float[ ] lobex = new float[ lobeCount ];
		float[ ] lobey = new float[ lobeCount ];
		
		for( int lobe = 0 ; lobe < lobeCount ; lobe++ )
		{
			float lobeAngle = ( float ) Math.PI * 2f * lobe / lobeCount;
			
			lobex[ lobe ] = size / 2f + centerRadius * ( float ) Math.sin( lobeAngle );
			lobey[ lobe ] = size / 2f + centerRadius * ( float ) Math.cos( lobeAngle );
		}
		
		for( int x = 0 ; x < size ; x++ )
		{
			for( int y = 0 ; y < size ; y++ )
			{
				float value = 0f;
				
				for( int lobe = 0 ; lobe < lobeCount ; lobe++ )
				{
					float dx = x - lobex[ lobe ];
					float dy = y - lobey[ lobe ];
					
					float dxy2 = dx * dx + dy * dy;
					if( dxy2 < lobeRadius2 )
					{
						value += 1 - dxy2 / lobeRadius2;
					}
				}
				
				int colorScale = 127;
				
				int rgb = ( ( int ) ( 255 * value ) ) << 24 |
						( ( color.getRed( ) * colorScale / 255 ) << 16 ) |
						( ( color.getGreen( ) * colorScale / 255 ) << 8 ) |
						( ( color.getBlue( ) * colorScale / 255 ) );
				
				image.setRGB( x , y , rgb );
			}
		}
		
		return new ImageIcon( image );
	}
}
