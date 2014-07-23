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

public class DistParamShadelet extends Shadelet
{
	public DistParamShadelet( )
	{
		setProperty( "v" , "v" );
		setProperty( "m" , "m" );
		setProperty( "pos" , "a_pos" );
		setProperty( "convPos" , "vec4($pos, 1.0)" );
		setProperty( "out" , "v_dist" );
		setProperty( "vDeclaration" , "/* vertex */ uniform mat4 $v;" );
		setProperty( "mDeclaration" , "/* vertex */ uniform mat4 $m;" );
		setProperty( "posDeclaration" , "attribute vec3 $pos;" );
		setProperty( "outDeclaration" , "varying float $out;" );
	}
	
	public DistParamShadelet v( Object v )
	{
		setProperty( "v" , v );
		return this;
	}
	
	public DistParamShadelet m( Object m )
	{
		setProperty( "m" , m );
		return this;
	}
	
	public DistParamShadelet pos( Object pos )
	{
		setProperty( "pos" , pos );
		return this;
	}
	
	public DistParamShadelet convPos( Object convPos )
	{
		setProperty( "convPos" , convPos );
		return this;
	}
	
	public DistParamShadelet out( Object out )
	{
		setProperty( "out" , out );
		return this;
	}
	
	public String out( )
	{
		return replaceProperties( "$out" );
	}
	
	public DistParamShadelet vDeclaration( Object vDeclaration )
	{
		setProperty( "vDeclaration" , vDeclaration );
		return this;
	}
	
	public DistParamShadelet mDeclaration( Object mDeclaration )
	{
		setProperty( "mDeclaration" , mDeclaration );
		return this;
	}
	
	public DistParamShadelet outDeclaration( Object outDeclaration )
	{
		setProperty( "outDeclaration" , outDeclaration );
		return this;
	}
	
	public DistParamShadelet posDeclaration( Object posDeclaration )
	{
		setProperty( "posDeclaration" , posDeclaration );
		return this;
	}
	
	@Override
	public String getVertexShaderMainCode( )
	{
		return "  $out = -($v * $m * $convPos).z;";
	}
}
