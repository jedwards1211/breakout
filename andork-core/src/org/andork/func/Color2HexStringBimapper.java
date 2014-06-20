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