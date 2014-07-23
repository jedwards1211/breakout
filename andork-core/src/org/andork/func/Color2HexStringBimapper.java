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
package org.andork.func;

import java.awt.Color;

public class Color2HexStringBimapper implements Bimapper<Color, Object>
{
	public static final Color2HexStringBimapper	instance	= new Color2HexStringBimapper();

	private Color2HexStringBimapper()
	{

	}

	@Override
	public Object map(Color in)
	{
		return in == null ? null : String.format("#%06x", in.getRGB() & 0xffffff);
	}

	@Override
	public Color unmap(Object out)
	{
		return out == null ? null : new Color(Integer.parseInt(out.toString().substring(1), 16));
	}
}