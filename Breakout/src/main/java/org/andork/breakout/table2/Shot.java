package org.andork.breakout.table2;

import org.andork.unit.Angle;
import org.andork.unit.Length;
import org.andork.unit.Unit;

public class Shot
{
	public String		from;
	public String		to;
	public ShotVector	vector;
	public XSect		fromXsect;
	public XSect		toXsect;
	public Unit<Length>	distUnit;
	public Unit<Angle>	angleUnit;
	public Object		note;
	public Object[ ]	custom;
}
