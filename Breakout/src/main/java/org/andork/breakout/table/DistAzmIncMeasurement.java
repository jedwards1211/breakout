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
package org.andork.breakout.table;

public class DistAzmIncMeasurement implements ShotMeasurement
{
	public final double	distance;
	public final Double	frontsightAzimuth;
	public final Double	backsightAzimuth;
	public final Double	frontsightInclination;
	public final Double	backsightInclination;
	
	public DistAzmIncMeasurement( double distance , Double frontsightAzimuth , Double backsightAzimuth , Double frontsightInclination , Double backsightInclination )
	{
		super( );
		this.distance = distance;
		this.frontsightAzimuth = frontsightAzimuth;
		this.backsightAzimuth = backsightAzimuth;
		this.frontsightInclination = frontsightInclination;
		this.backsightInclination = backsightInclination;
	}
	
	public DistAzmIncMeasurement( double distance , Double[ ] azimuth , Double[ ] inclination )
	{
		this.distance = distance;
		frontsightAzimuth = azimuth == null ? null : azimuth[ 0 ];
		backsightAzimuth = azimuth == null || azimuth.length < 2 ? null : azimuth[ 1 ];
		frontsightInclination = inclination[ 0 ];
		backsightInclination = inclination.length > 1 ? inclination[ 1 ] : null;
	}
}
