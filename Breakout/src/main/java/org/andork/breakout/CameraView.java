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
package org.andork.breakout;

public enum CameraView
{
	PERSPECTIVE( "Perspective" ) ,
	PLAN( "Plan" ) ,
	NORTH_FACING_PROFILE( "North-Facing Profile" ) ,
	SOUTH_FACING_PROFILE( "South-Facing Profile" ) ,
	EAST_FACING_PROFILE( "East-Facing Profile" ) ,
	WEST_FACING_PROFILE( "West-Facing Profile" ) ,
	AUTO_PROFILE( "Auto Profile" );
	
	private String	displayText;
	
	CameraView( String displayText )
	{
		this.displayText = displayText;
	}
	
	public String toString( )
	{
		return displayText;
	}
}