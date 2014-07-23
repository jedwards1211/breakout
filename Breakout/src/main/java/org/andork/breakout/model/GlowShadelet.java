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
package org.andork.breakout.model;

import org.andork.jogl.shadelet.Shadelet;

public class GlowShadelet extends Shadelet
{
	public GlowShadelet( )
	{
		setProperty( "vertParam" , "a_glow" );
		setProperty( "fragParam" , "v_glow" );
		setProperty( "color" , "color" );
		setProperty( "out" , "gl_FragColor" );
		setProperty( "vertParamDeclaration" , "attribute vec2 $vertParam;" );
		setProperty( "fragParamDeclaration" , "varying vec2 $fragParam;" );
		setProperty( "colorDeclaration" , "/* fragment */ uniform vec4 $color;" );
	}
	
	public String vertParam( )
	{
		return replaceProperties( "$vertParam" );
	}
	
	public GlowShadelet vertParam( Object vertParam )
	{
		setProperty( "vertParam" , vertParam );
		return this;
	}
	
	public GlowShadelet fragParam( Object fragParam )
	{
		setProperty( "fragParam" , fragParam );
		return this;
	}
	
	public GlowShadelet color( Object color )
	{
		setProperty( "color" , color );
		return this;
	}
	
	public GlowShadelet out( Object out )
	{
		setProperty( "out" , out );
		return this;
	}
	
	public GlowShadelet vertParamDeclaration( Object vertParamDeclaration )
	{
		setProperty( "vertParamDeclaration" , vertParamDeclaration );
		return this;
	}
	
	public GlowShadelet fragParamDeclaration( Object fragParamDeclaration )
	{
		setProperty( "fragParamDeclaration" , fragParamDeclaration );
		return this;
	}
	
	public GlowShadelet colorDeclaration( Object colorDeclaration )
	{
		setProperty( "colorDeclaration" , colorDeclaration );
		return this;
	}
	
	public GlowShadelet outDeclaration( Object outDeclaration )
	{
		setProperty( "outDeclaration" , outDeclaration );
		return this;
	}
	
	public String getVertexShaderMainCode( )
	{
		return "$fragParam = $vertParam;";
	}
	
	public String getFragmentShaderMainCode( )
	{
		return "  $out = mix($out, $color, clamp(min($fragParam.x, $fragParam.y), 0.0, 1.0));";
	}
}
