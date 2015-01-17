package org.andork.breakout.wallsimport;

import org.andork.unit.Angle;
import org.andork.unit.Length;
import org.andork.unit.Unit;

public class WallsUnits
{
	public String		Order				= "DAV";
	public Unit<Length>	D_Unit				= Length.meters;
	public Unit<Length>	S_Unit				= Length.meters;
	public Unit<Angle>	A_Unit				= Angle.degrees;
	public Unit<Angle>	AB_Unit				= Angle.degrees;
	public Unit<Angle>	V_Unit				= Angle.degrees;
	public Unit<Angle>	VB_Unit				= Angle.degrees;

	public double		DECL				= 0.0;
	public double		GRID				= 0.0;
	public double		RECT				= 0.0;

	public double		INCD				= 0.0;
	public double		INCA				= 0.0;
	public double		INCAB				= 0.0;
	public double		INCV				= 0.0;
	public double		INCVB				= 0.0;
	public double		INCS				= 0.0;
	public double		INCH				= 0.0;

	public boolean		TYPEAB_corrected	= false;
	public double		TYPEAB_tolerance	= 5.0;
	public boolean		TYPEAB_no_averaging	= false;

	public static enum Case
	{
		Upper, Lower, Mixed;
	}
}
