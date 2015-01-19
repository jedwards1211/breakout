package org.andork.breakout.wallsimport;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.andork.unit.Angle;
import org.andork.unit.Length;
import org.andork.unit.UnitizedDouble;
import org.andork.unit.Unit;

/**
 * The state of everything that can be controlled by a #Units directive in Walls .srv files.
 * Entire groups of state can be pushed and popped by #Units Save and #Units Restore.<br>
 * <br>
 * I have chosen to name most of the fields in this class according to the parameter names in
 * Walls .srv files, rather than more human-readable names. See the
 * <a href="http://www.texasspeleologicalsurvey.org/software/walls/Walls_manual.pdf">Walls manual</a> for more
 * information.
 * 
 * @author James
 */
public class WallsUnits
{
	/**
	 * The order of measurements in data rows, represented as a sequence of characters. For compass and tape
	 * measurements, some sequence of:
	 * <ul>
	 * <li>D = distance</li>
	 * <li>A = azimuth</li>
	 * <li>V = inclination (optional)</li>
	 * </ul>
	 * For RECT vectors, some combination of:
	 * <ul>
	 * <li>E = east</li>
	 * <li>N = north</li>
	 * <li>U = up (optional)</li>
	 * </ul>
	 */
	public String					order				= "dav";
	/**
	 * Unit for primary distance measurements (distance between stations, components of RECT vectors, FIX positions)
	 */
	public Unit<Length>				d_unit				= Length.meters;
	/**
	 * Unit for secondary distance measurements (distance between the instrument and station, LRUDs)
	 */
	public Unit<Length>				s_unit				= Length.meters;
	/**
	 * Frontsight azimuth unit.
	 */
	public Unit<Angle>				a_unit				= Angle.degrees;
	/**
	 * Backsight azimuth unit.
	 */
	public Unit<Angle>				ab_unit				= Angle.degrees;
	/**
	 * Frontsight inclination unit.
	 */
	public Unit<Angle>				v_unit				= Angle.degrees;
	/**
	 * Backsight inclination unit.
	 */
	public Unit<Angle>				vb_unit				= Angle.degrees;

	/**
	 * Declination (true north minus magnetic north) that would need to be added to the azimuth measurements of
	 * compass and tape data lines to obtain a vector's true north direction.
	 */
	public UnitizedDouble<Angle>	decl				= new UnitizedDouble<>( 0.0 , null );

	/**
	 * UTM grid convergence angle, #fix fectors would need to be rotated by this amount to obtain true north relative
	 * vectors.
	 */
	public UnitizedDouble<Angle>	grid				= new UnitizedDouble<>( 0.0 , null );

	/**
	 * The displacement vectors corresponding to RECT data lines would need to be rotated clockwise by this amount to
	 * obtain true north relative vectors.
	 */
	public UnitizedDouble<Angle>	rect				= new UnitizedDouble<>( 0.0 , null );

	/**
	 * Distance instrument correction.
	 */
	public UnitizedDouble<Length>	incd				= new UnitizedDouble<>( 0.0 , null );
	/**
	 * Frontsight azimuth instrument correction.
	 */
	public UnitizedDouble<Angle>	inca				= new UnitizedDouble<>( 0.0 , null );
	/**
	 * Backsight azimuth instrument correction.
	 */
	public UnitizedDouble<Angle>	incab				= new UnitizedDouble<>( 0.0 , null );
	/**
	 * Frontsight inclination instrument correction.
	 */
	public UnitizedDouble<Angle>	incv				= new UnitizedDouble<>( 0.0 , null );
	/**
	 * Backsight inclination instrument correction.
	 */
	public UnitizedDouble<Angle>	incvb				= new UnitizedDouble<>( 0.0 , null );
	/**
	 * Secondary distance measurement correction (distance btw. instruments and stations or LRUDs)
	 */
	public UnitizedDouble<Length>	incs				= new UnitizedDouble<>( 0.0 , null );
	/**
	 * Height adjustment, amount added to the elevation of the To station relative to the From station.
	 */
	public UnitizedDouble<Length>	inch				= new UnitizedDouble<>( 0.0 , null );
	/**
	 * Whether the azimuth backsight is corrected
	 */
	public boolean					typeab_corrected	= false;
	/**
	 * Whether the inclination backsight is corrected
	 */
	public boolean					typevb_corrected	= false;
	/**
	 * Whether to change or preserve the case of station names.
	 */
	public Case						case_				= Case.Mixed;
	/**
	 * Where LRUDs are positioned and oriented
	 */
	public Lrud						lrud				= Lrud.From;
	/**
	 * The order LRUDs are given in
	 */
	public String					lrud_order			= "LRUD";
	/**
	 * Taping method
	 */
	public Tape						tape				= Tape.IT;
	/**
	 * Flag (arbitrary text)
	 */
	public String					flag				= null;
	/**
	 * Macro names and replacement values
	 */
	public Map<String, String>		macros				= new HashMap<String, String>( );
	/**
	 * Prefixes for station names (the first is applied rightmost)
	 */
	public List<String>				prefix				= new ArrayList<String>( );
	/**
	 * Survey date
	 */
	public Date						date;
	/**
	 * Survey segment (absolute or relative path separated by /)
	 */
	public String					segment				= null;

	public static enum Case
	{
		/**
		 * Convert station names to uppercase.
		 */
		Upper,
		/**
		 * Convert station names to lowercase.
		 */
		Lower,
		/**
		 * Preserve station name case.
		 */
		Mixed;
	}

	public static enum Lrud
	{
		/**
		 * Perpendicular to shot at from station
		 */
		From,
		/**
		 * Perpendicular to shot at to station
		 */
		To,
		/**
		 * Bisecting shots at from station
		 */
		FB,
		/**
		 * Bisecting shots at to station
		 */
		TB;
	}

	public static enum Tape
	{
		/**
		 * Instrument-to-target
		 */
		IT,
		/**
		 * Station-to-station
		 */
		SS,
		/**
		 * Instrument-to-station
		 */
		IS,
		/**
		 * Station-to-target
		 */
		ST;
	}
}
