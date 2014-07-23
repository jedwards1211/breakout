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
package org.andork.breakout.awt;

import static org.andork.math3d.Vecmath.length;
import static org.andork.math3d.Vecmath.normalize2;
import static org.andork.math3d.Vecmath.setf;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.ColorModel;

import org.andork.breakout.awt.MultipleGradientPaint.ColorSpaceType;
import org.andork.breakout.awt.MultipleGradientPaint.CycleMethod;

public class ParamGradientMapPaintContext extends MultipleGradientPaintContext
{
	private final float[ ]	origin		= new float[ 2 ];
	private float			startParam , endParam;
	private final float[ ]	majorAxis	= new float[ 2 ];
	private final float[ ]	minorAxis	= new float[ 2 ];
	
	/**
	 * Constructor for ParamGradientMapPaintContext.
	 * 
	 * @param paint
	 *            the {@code ParamGradientMapPaint} from which this context is created
	 * @param cm
	 *            {@code ColorModel} that receives the <code>Paint</code> data. This is used only as a hint.
	 * @param deviceBounds
	 *            the device space bounding box of the graphics primitive being rendered
	 * @param userBounds
	 *            the user space bounding box of the graphics primitive being rendered
	 * @param t
	 *            the {@code AffineTransform} from user space into device space (gradientTransform should be concatenated with this)
	 * @param hints
	 *            the hints that the context object uses to choose between rendering alternatives
	 * @param dStart
	 *            gradient start point, in user space
	 * @param dEnd
	 *            gradient end point, in user space
	 * @param fractions
	 *            the fractions specifying the gradient distribution
	 * @param colors
	 *            the gradient colors
	 * @param cycleMethod
	 *            either NO_CYCLE, REFLECT, or REPEAT
	 * @param colorSpace
	 *            which colorspace to use for interpolation, either SRGB or LINEAR_RGB
	 */
	ParamGradientMapPaintContext( ParamGradientMapPaint paint ,
			ColorModel cm ,
			Rectangle deviceBounds ,
			Rectangle2D userBounds ,
			AffineTransform t ,
			RenderingHints hints ,
			float[ ] origin ,
			float[ ] majorAxis ,
			float[ ] minorAxis ,
			float startParam ,
			float endParam ,
			float[ ] fractions ,
			Color[ ] colors ,
			CycleMethod cycleMethod ,
			ColorSpaceType colorSpace )
	{
		super( paint , cm , deviceBounds , userBounds , t , hints , fractions ,
				colors , cycleMethod , colorSpace );
		float majorLength = length( majorAxis , 0 , 2 );
		float minorLength = length( minorAxis , 0 , 2 );
		
		setf( this.origin , origin );
		normalize2( majorAxis , this.majorAxis );
		normalize2( minorAxis , this.minorAxis );
		this.majorAxis[ 0 ] /= majorLength;
		this.majorAxis[ 1 ] /= majorLength;
		this.minorAxis[ 0 ] /= minorLength * majorLength;
		this.minorAxis[ 1 ] /= minorLength * majorLength;
		this.startParam = startParam;
		this.endParam = endParam;
	}
	
	/**
	 * Return a Raster containing the colors generated for the graphics operation. This is where the area is filled with colors distributed linearly.
	 * 
	 * @param x
	 *            ,y,w,h the area in device space for which colors are generated.
	 */
	protected void fillRaster( int[ ] pixels , int off , int adjust ,
			int x , int y , int w , int h )
	{
		// TODO incorporate translation
		
		for( int py = y ; py < y + h ; py++ )
		{
			for( int px = x ; px < x + w ; px++ )
			{
				float dx = px - origin[ 0 ];
				float dy = py - origin[ 1 ];
				
				float major = dx * majorAxis[ 0 ] + dy * majorAxis[ 1 ];
				float minor = dx * minorAxis[ 0 ] + dy * minorAxis[ 1 ];
				
				float param = startParam + ( endParam - startParam ) * ( major + minor );
				
				pixels[ off++ ] = indexIntoGradientsArrays( param );
			}
			
			off += adjust;
		}
	}
}
