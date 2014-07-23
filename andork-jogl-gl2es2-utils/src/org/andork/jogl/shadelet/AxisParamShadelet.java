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
package org.andork.jogl.shadelet;

public class AxisParamShadelet extends Shadelet
{
	public AxisParamShadelet( )
	{
		setProperty( "pos" , "a_pos" );
		setProperty( "origin" , "u_origin" );
		setProperty( "axis" , "u_axis" );
		setProperty( "out" , "v_axisParam" );
		setProperty( "posDeclaration" , "attribute vec3 $pos;" );
		setProperty( "originDeclaration" , "/* vertex */ uniform vec3 u_origin;" );
		setProperty( "axisDeclaration" , "/* vertex */ uniform vec3 u_axis;" );
		setProperty( "outDeclaration" , "varying float $out;" );
	}
	
	public String out( )
	{
		return replaceProperties( "$out" );
	}
	
	public String origin( )
	{
		return replaceProperties( "$origin" );
	}
	
	public String axis( )
	{
		return replaceProperties( "$axis" );
	}
	
	public AxisParamShadelet pos( Object pos )
	{
		setProperty( "pos" , pos );
		return this;
	}
	
	public AxisParamShadelet origin( Object origin )
	{
		setProperty( "origin" , origin );
		return this;
	}
	
	public AxisParamShadelet axis( Object axis )
	{
		setProperty( "axis" , axis );
		return this;
	}
	
	public AxisParamShadelet convPos( Object convPos )
	{
		setProperty( "convPos" , convPos );
		return this;
	}
	
	public AxisParamShadelet out( Object out )
	{
		setProperty( "out" , out );
		return this;
	}
	
	public AxisParamShadelet outDeclaration( Object outDeclaration )
	{
		setProperty( "outDeclaration" , outDeclaration );
		return this;
	}
	
	public AxisParamShadelet posDeclaration( Object posDeclaration )
	{
		setProperty( "posDeclaration" , posDeclaration );
		return this;
	}
	
	public AxisParamShadelet originDeclaration( Object originDeclaration )
	{
		setProperty( "originDeclaration" , originDeclaration );
		return this;
	}
	
	public AxisParamShadelet axisDeclaration( Object axisDeclaration )
	{
		setProperty( "axisDeclaration" , axisDeclaration );
		return this;
	}
	
	@Override
	public String getVertexShaderMainCode( )
	{
		return "  $out = dot($pos - $origin, $axis);";
	}
}
