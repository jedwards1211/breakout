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

public class PositionVertexShadelet extends Shadelet
{
	public PositionVertexShadelet( )
	{
		setProperty( "p" , "p" );
		setProperty( "v" , "v" );
		setProperty( "m" , "m" );
		setProperty( "pos" , "a_pos" );
		setProperty( "out" , "gl_Position" );
		setProperty( "pDeclaration" , "/* vertex */ uniform mat4 $p;" );
		setProperty( "vDeclaration" , "/* vertex */ uniform mat4 $v;" );
		setProperty( "mDeclaration" , "/* vertex */ uniform mat4 $m;" );
		setProperty( "posDeclaration" , "attribute vec3 $pos;" );
	}
	
	public String pos( )
	{
		return replaceProperties( "$pos" );
	}
	
	public PositionVertexShadelet p( String p )
	{
		setProperty( "p" , p );
		return this;
	}
	
	public PositionVertexShadelet v( String v )
	{
		setProperty( "v" , v );
		return this;
	}
	
	public PositionVertexShadelet m( String m )
	{
		setProperty( "m" , m );
		return this;
	}
	
	public PositionVertexShadelet pos( Object pos )
	{
		setProperty( "pos" , pos );
		return this;
	}
	
	public PositionVertexShadelet out( Object out )
	{
		setProperty( "out" , out );
		return this;
	}
	
	public PositionVertexShadelet pDeclaration( String pDeclaration )
	{
		setProperty( "pDeclaration" , pDeclaration );
		return this;
	}
	
	public PositionVertexShadelet vDeclaration( String vDeclaration )
	{
		setProperty( "vDeclaration" , vDeclaration );
		return this;
	}
	
	public PositionVertexShadelet mDeclaration( String mDeclaration )
	{
		setProperty( "mDeclaration" , mDeclaration );
		return this;
	}
	
	public PositionVertexShadelet posDeclaration( String posDeclaration )
	{
		setProperty( "posDeclaration" , posDeclaration );
		return this;
	}
	
	public PositionVertexShadelet outDeclaration( String outDeclaration )
	{
		setProperty( "outDeclaration" , outDeclaration );
		return this;
	}
	
	public String getVertexShaderMainCode( )
	{
		return "  $out = $p * $v * $m * vec4($pos, 1.0);";
	}
}
